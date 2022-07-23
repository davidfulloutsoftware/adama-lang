/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.data;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class DiskMetrics {
  public final Runnable flush;
  public final Inflight total_storage_allocated;
  public final Inflight free_storage_available;
  public final Inflight alarm_storage_over_80_percent;

  public DiskMetrics(MetricsFactory factory) {
    this.flush = factory.counter("disk_flush");
    this.total_storage_allocated = factory.inflight("disk_total_storage_allocated_mb");
    this.free_storage_available = factory.inflight("disk_free_storage_available_mb");
    this.alarm_storage_over_80_percent = factory.inflight("alarm_storage_over_80_percent");
  }
}