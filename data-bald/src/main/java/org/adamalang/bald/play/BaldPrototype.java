package org.adamalang.bald.play;

import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Index;
import org.adamalang.bald.organization.Region;

import java.util.ArrayList;

public class BaldPrototype {

  public Heap heap;
  public Index index;

  public BaldPrototype(Heap heap, Index index) {
    this.heap = heap;
    this.index = index;
  }

  public int append(long id, byte[] memory) {
    Region region = heap.ask(memory.length);
    int sz = index.append(id, region);
    // write APPEND:id,memory.length,region.start,bytes to WAL
    // ID:8,memory.length:4,START:8,region
    // write to disk
    return sz;
  }

  public void trim(long id, int writes) {
    ArrayList<Region> regionsToTrim = index.trim(id, writes);
    // write TRIMs to log
    for (Region region : regionsToTrim) {
      heap.free(region);
    }
  }
}