/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.properties;

public enum CanAssignResult {
  No, //
  YesWithIngestionCodeGen, //
  YesWithMakeThenSetter, //
  YesWithNativeOp, //
  YesWithSetter, //
  YesWithTransformSetter, //
  YesWithTransformThenMakeSetter
}
