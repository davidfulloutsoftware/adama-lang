/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.structures;

import java.util.LinkedHashSet;
import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.privacy.Policy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;

/** the definition for a field */
public class FieldDefinition extends StructureComponent {
  public static FieldDefinition invent(final TyType type, final String name) {
    return new FieldDefinition(null, null, type, Token.WRAP(name), null, null, null, null);
  }

  public final Expression computeExpression;
  public final Expression defaultValueOverride;
  public final Token equalsToken;
  public final Token introToken;
  public final String name;
  public final Token nameToken;
  public final Policy policy;
  public final Token semicolonToken;
  public TyType type;
  public final LinkedHashSet<String> variablesToWatch;

  public FieldDefinition(final Policy policy, final Token introToken, final TyType type, final Token nameToken, final Token equalsToken, final Expression computeExpression, final Expression defaultValueOverride,
      final Token semicolonToken) {
    this.policy = policy;
    this.introToken = introToken;
    this.type = type;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.computeExpression = computeExpression;
    this.equalsToken = equalsToken;
    this.defaultValueOverride = defaultValueOverride;
    this.semicolonToken = semicolonToken;
    if (policy != null) {
      ingest(policy);
    }
    if (introToken != null) {
      ingest(introToken);
    }
    if (type != null) {
      ingest(type);
    }
    if (computeExpression != null) {
      ingest(computeExpression);
    }
    if (defaultValueOverride != null) {
      ingest(defaultValueOverride);
    }
    if (semicolonToken != null) {
      ingest(semicolonToken);
    }
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (policy != null) {
      policy.emit(yielder);
    }
    if (introToken != null) {
      yielder.accept(introToken);
    }
    if (type != null) {
      type.emit(yielder);
    }
    yielder.accept(nameToken);
    if (equalsToken != null) {
      yielder.accept(equalsToken);
      if (computeExpression != null) {
        computeExpression.emit(yielder);
      }
      if (defaultValueOverride != null) {
        defaultValueOverride.emit(yielder);
      }
    }
    yielder.accept(semicolonToken);
  }

  @Override
  public boolean equals(final Object otherRaw) {
    if (otherRaw instanceof FieldDefinition) {
      final var other = (FieldDefinition) otherRaw;
      if (name.equals(other.name)) { return type.getAdamaType().equals(other.type.getAdamaType()); }
    }
    return false;
  }

  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
    if (policy != null) {
      policy.typing(environment, owningStructureStorage);
    }
    if (type != null) {
      type = environment.rules.Resolve(type, false);
    }
    if (type == null && computeExpression != null) {
      type = computeExpression.typing(environment.scopeReactiveExpression().scopeWithComputeContext(ComputeContext.Computation), null /* no suggestion makes sense */);
      if (type != null) {
        type = new TyReactiveLazy(type).withPosition(type);
      }
    }
    if (type != null) {
      type.typing(environment);
      if (defaultValueOverride != null) {
        final var defType = defaultValueOverride.typing(environment, null /* no suggestion makes sense */);
        if (defType != null) {
          environment.rules.CanTypeAStoreTypeB(type, defType, StorageTweak.None, false);
        }
      }
    }
  }

  public void writePolicy(final StringBuilderWithTabs sb, final Environment environment) {
    if (policy != null) {
      policy.writePrivacyCheckAndExtractJava(sb, this, environment);
    }
  }
}
