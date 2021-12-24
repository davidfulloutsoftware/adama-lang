package org.adamalang.grpc.server;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import org.adamalang.grpc.common.ExceptionRunnable;
import org.adamalang.grpc.common.ExceptionSupplier;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.runtime.sys.CoreService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Server implements AutoCloseable {
    private final Supplier<io.grpc.Server> serverSupplier;
    private io.grpc.Server server;
    private final AtomicBoolean alive;

    public Server(MachineIdentity identity, CoreService service, int port) throws Exception{
        this.alive = new AtomicBoolean(false);
        this.server = null;
        this.serverSupplier = ExceptionSupplier.TO_RUNTIME(() ->
            NettyServerBuilder.forPort(port).addService(new Handler(service))
                    .sslContext(GrpcSslContexts //
                            .forServer(identity.getCert(), identity.getKey()) //
                            .trustManager(identity.getTrust()) //
                            .clientAuth(ClientAuth.REQUIRE) //
                            .build())
                    .build()
        );
    }

    /** Start serving requests. */
    public void start() throws IOException {
        if (alive.compareAndExchange(false, true) == false) {
            server = serverSupplier.get();
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
                Server.this.close();
            })));
        }
    }

    /** Finish serving request */
    @Override
    public void close() throws InterruptedException {
        if (alive.compareAndExchange(true, false) == true) {
            server.shutdownNow().awaitTermination(2, TimeUnit.SECONDS);
            server = null;
        }
    }
}
