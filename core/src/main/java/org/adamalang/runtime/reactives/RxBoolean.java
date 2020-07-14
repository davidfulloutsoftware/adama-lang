/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive boolean */
public class RxBoolean extends RxBase implements Comparable<RxBoolean>, CanGetAndSet<Boolean> {
  private boolean backup;
  private boolean value;

  public RxBoolean(final RxParent owner, final boolean value) {
    super(owner);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(final String name, final ObjectNode delta) {
    if (__isDirty()) {
      delta.put(name, value);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  @Override
  public int compareTo(final RxBoolean other) {
    return Boolean.compare(value, other.value);
  }

  @Override
  public Boolean get() {
    return value;
  }

  @Override
  public void set(final Boolean value) {
    if (this.value != value) {
      this.value = value;
      __raiseDirty();
    }
  }
}
