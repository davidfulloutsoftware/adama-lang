/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.finder;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.mocks.SimpleFinderCallback;
import org.adamalang.mysql.mocks.SimpleMockCallback;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.junit.Test;

public class FinderTests {
  private final Key KEY1 = new Key("space-1", "key-1");
  private final Key KEY2 = new Key("space-2", "key-2");

  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory(), "noop"))) {
      FinderInstaller installer = new FinderInstaller(dataBase);
      try {
        installer.install();
        Finder machine = new Finder(dataBase);
        /*
        Finder machineA = new Finder(dataBase, "region", "machineA:124");
        Finder machineB = new Finder(dataBase, "region", "machineB:523");
        */

        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.update(KEY1, 1, 2, callback);
          callback.assertFailure(707651);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY2, cb);
          cb.assertFailure(625676);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.set(KEY1, "region", "machineA:124", callback);
          callback.assertFailure(786509);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.create(KEY1, callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Fresh, "");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Fresh, "");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.set(KEY1, "region", "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.set(KEY1, "region", "machineA:124", callback);
          callback.assertFailure(786509);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.set(KEY1, "region", "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.archive(KEY1, "new-achive-key", "machineA:124", callback);
          callback.assertFailure(735308);
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Machine, "machineB:523");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.archive(KEY1, "new-achive-key", "machineB:523", callback);
          callback.assertSuccess();
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Archive, "new-achive-key");
        }
        {
          SimpleFinderCallback cb = new SimpleFinderCallback();
          machine.find(KEY1, cb);
          cb.assertSuccess(FinderService.Location.Archive, "new-achive-key");
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.update(KEY1, 1, 2, callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.set(KEY1, "region", "targetNew", callback);
          callback.assertSuccess();
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.delete(KEY1, "nop", callback);
          callback.assertFailure(773247);
        }
        {
          SimpleMockCallback callback = new SimpleMockCallback();
          machine.delete(KEY1, "targetNew", callback);
          callback.assertSuccess();
        }
      } finally {
        installer.uninstall();
      }
    }
  }
}
