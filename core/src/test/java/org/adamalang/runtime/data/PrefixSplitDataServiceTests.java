package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PrefixSplitDataServiceTests {

  @Test
  public void coverage() throws Exception {
    MockInstantDataService dataServiceA = new MockInstantDataService();
    MockInstantDataService dataServiceB = new MockInstantDataService();
    PrefixSplitDataService ds = new PrefixSplitDataService(dataServiceA, "x", dataServiceB);
    Key key = new Key("space", "key");
    Key key2 = new Key("space", "xyz");
    RemoteDocumentUpdate update =
        new RemoteDocumentUpdate(1, 1, NtClient.NO_ONE, "", "", "", false, 1, 123, UpdateType.AddUserData);
    CountDownLatch latch = new CountDownLatch(7);
    ds.get(
        key,
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {}

          @Override
          public void failure(ErrorCodeException ex) {
            latch.countDown();
          }
        });
    ds.initialize(
        key,
        update,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.initialize(
        key2,
        update,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.patch(
        key,
        new RemoteDocumentUpdate[] { update },
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.patch(
        key2,
        new RemoteDocumentUpdate[] { update },
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.compute(
        key,
        ComputeMethod.Rewind,
        1,
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.delete(
        key,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    CountDownLatch latchCompacted = new CountDownLatch(1);
    ds.compact(key, 100, new Callback<Integer>() {
      @Override
      public void success(Integer value) {
        latchCompacted.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(latchCompacted.await(1000, TimeUnit.MILLISECONDS));
  }
}