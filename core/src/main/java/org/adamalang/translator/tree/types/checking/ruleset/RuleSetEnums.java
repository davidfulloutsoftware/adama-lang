/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeEnum;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.traits.IsEnum;

public class RuleSetEnums {
  public static IsEnum FindEnumType(final Environment environment, final String name, final DocumentPosition position, final boolean silent) {
    final var type = environment.document.types.get(name);
    if (type != null) {
      if (type instanceof IsEnum) {
        return (IsEnum) type.makeCopyWithNewPosition(position);
      } else if (!silent) {
        environment.document.createError(position, String.format("Type incorrect: expecting '%s' to be an enumeration; instead, found a type of '%s'.", name, type.getAdamaType()), "TypeCheckReferences");
      }
    } else if (!silent) {
      environment.document.createError(position, String.format("Type not found: an enumeration named '%s' was not found.", name), "TypeCheckReferences");
    }
    return null;
  }

  static boolean IsEnum(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof IsEnum) { return true; }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativeEnum(null, Token.WRAP("enum<?>"), null, new EnumStorage("?"), null), tyTypeOriginal, silent);
    }
    return false;
  }
}
