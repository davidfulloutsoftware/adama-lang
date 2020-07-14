/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeFuture extends TyType implements DetailContainsAnEmbeddedType, DetailTypeHasMethods, AssignmentViaNative {
  public final Token futureToken;
  public final TyType resultType;
  public final TokenizedItem<TyType> tokenResultType;

  public TyNativeFuture(final Token futureToken, final TokenizedItem<TyType> tokenResultType) {
    this.futureToken = futureToken;
    resultType = tokenResultType.item;
    this.tokenResultType = tokenResultType;
    ingest(futureToken);
    ingest(resultType);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(futureToken);
    tokenResultType.emitBefore(yielder);
    tokenResultType.item.emit(yielder);
    tokenResultType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("future<%s>", resultType.getAdamaType());
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(resultType, false);
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("SimpleFuture<%s>", getEmbeddedType(environment).getJavaBoxType(environment));
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("await".equals(name)) {
      final var returnType = environment.rules.Resolve(resultType, false);
      return new TyNativeFunctional("await", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("await", returnType, new ArrayList<>(), false)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeFuture(futureToken, new TokenizedItem<>(resultType.makeCopyWithNewPosition(position))).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenResultType.item.typing(environment);
  }
}
