package org.adamalang.canary.local;

import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LocalAgent implements Streamback {
  private final CoreService service;
  private final LocalCanaryConfig config;
  private final NtClient who;
  private final Key key;
  private final ScheduledExecutorService executor;
  private CoreStream stream;
  private final Random rng;
  private AtomicBoolean dedupe;

  public LocalAgent(CoreService service, LocalCanaryConfig config, int agentId, ScheduledExecutorService executor) {
    this.service = service;
    this.config = config;
    this.who = new NtClient("agent" + agentId, "canary");
    int keyId = (int) (config.keyIdMin + (config.keyIdMax - config.keyIdMin + 1) * Math.random());
    this.key = new Key(config.space, config.keyPrefix + keyId);
    this.executor = executor;
    this.rng = new Random();
    this.dedupe = new AtomicBoolean(true);
  }

  public void kickOff() {
    service.connect(who, key, "{}", this);
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    this.stream = stream;
    AtomicInteger messages = new AtomicInteger(config.messagesPerAgent);
    AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();
    future.set(executor.scheduleAtFixedRate(() -> {
      if (messages.getAndDecrement() < 0) {
        future.get().cancel(false);
        if (dedupe.compareAndSet(true, false)) {
          config.quitter.countDown();
        }
        return;
      }
      SimpleCanaryConfig.Message msg = config.messages[rng.nextInt(config.messages.length)];
      config.metrics.messages_sent.incrementAndGet();
      stream.send(msg.channel, null, msg.message.toString(), new Callback<Integer>() {
        @Override
        public void success(Integer value) {
          config.metrics.messages_failed.incrementAndGet();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          config.metrics.report_failure(ex.code);
          config.metrics.messages_failed.incrementAndGet();
        }
      });
    }, (int) (config.messageDelayMs * Math.random()),  config.messageDelayMs, TimeUnit.MILLISECONDS));
  }

  @Override
  public void status(StreamStatus status) {
  }

  @Override
  public void next(String data) {
    config.metrics.deltas.incrementAndGet();
  }

  @Override
  public void failure(ErrorCodeException exception) {
    config.metrics.stream_failed.incrementAndGet();
    config.metrics.report_failure(exception.code);
    if (dedupe.compareAndSet(true, false)) {
      config.quitter.countDown();
    }
  }
}