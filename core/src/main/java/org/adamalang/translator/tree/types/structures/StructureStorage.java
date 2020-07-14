/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.privacy.PrivatePolicy;
import org.adamalang.translator.tree.privacy.PublicPolicy;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveClient;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.reactive.TyReactiveInteger;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

public class StructureStorage extends DocumentPosition {
  public final boolean anonymous;
  public final TreeMap<String, BubbleDefinition> bubbles;
  public Token closeBraceToken;
  public final ArrayList<Consumer<Consumer<Token>>> emissions;
  public final TreeMap<String, FieldDefinition> fields;
  public final ArrayList<FieldDefinition> fieldsByOrder;
  public final HashSet<String> indexSet;
  public final ArrayList<IndexDefinition> indices;
  public final ArrayList<DefineMethod> methods;
  public final HashMap<String, TyNativeFunctional> methodTypes;
  public final Token openBraceToken;
  public final TreeMap<String, DefineCustomPolicy> policies;
  public final ArrayList<String> policiesForVisibility;
  public final StorageSpecialization specialization;
  public final ArrayList<Consumer<Environment>> typeCheckOrder;

  public StructureStorage(final StorageSpecialization specialization, final boolean anonymous, final Token openBraceToken) {
    this.specialization = specialization;
    this.anonymous = anonymous;
    this.openBraceToken = openBraceToken;
    closeBraceToken = null;
    typeCheckOrder = new ArrayList<>();
    fields = new TreeMap<>();
    bubbles = new TreeMap<>();
    methods = new ArrayList<>();
    fieldsByOrder = new ArrayList<>();
    policies = new TreeMap<>();
    policiesForVisibility = new ArrayList<>();
    emissions = new ArrayList<>();
    indices = new ArrayList<>();
    indexSet = new HashSet<>();
    methodTypes = new HashMap<>();
    ingest(openBraceToken);
  }

  public void add(final BubbleDefinition bd) {
    add(bd, typeCheckOrder);
  }

  public void add(final BubbleDefinition bd, final ArrayList<Consumer<Environment>> order) {
    emissions.add(emit -> bd.emit(emit));
    ingest(bd);
    order.add(env -> {
      bd.typing(env.watch(name -> {
        // TODO: This is a Hack at the moment...
        if (!env.document.functionTypes.containsKey(name)) {
          bd.variablesToWatch.add(name);
        }
      }));
    });
    if (has(bd.nameToken.text)) {
      order.add(env -> {
        env.document.createError(bd, String.format("Bubble '%s' was already defined", bd.nameToken.text), "StructureDefine");
      });
      return;
    }
    bubbles.put(bd.nameToken.text, bd);
  }

  public void add(final DefineMethod dm) {
    emissions.add(emit -> dm.emit(emit));
    add(dm, typeCheckOrder);
  }

  public void add(final DefineMethod dm, final ArrayList<Consumer<Environment>> order) {
    methods.add(dm);
    order.add(env -> {
      final var foi = dm.typing(env);
      var functional = methodTypes.get(dm.name);
      if (functional == null) {
        functional = new TyNativeFunctional(dm.name, new ArrayList<>(), FunctionStyleJava.ExpressionThenNameWithArgs);
        methodTypes.put(dm.name, functional);
      }
      functional.overloads.add(foi);
    });
  }

  /** add the given field to the record */
  public void add(final FieldDefinition fd) {
    this.add(fd, typeCheckOrder);
  }

  /** add the given field to the record such that type checking is done in the
   * given order */
  public void add(final FieldDefinition fd, final ArrayList<Consumer<Environment>> order) {
    emissions.add(emit -> fd.emit(emit));
    ingest(fd);
    if (has(fd.nameToken.text)) {
      order.add(env -> {
        env.document.createError(fd, String.format("Field '%s' was already defined", fd.nameToken.text), "StructureDefine");
      });
      return;
    }
    order.add(env -> {
      fd.typing(env.watch(name -> {
        // TODO: This is a Hack at the moment...
        if (!env.document.functionTypes.containsKey(name)) {
          fd.variablesToWatch.add(name);
        }
      }), this);
      env.define(fd.name, fd.type, false, fd);
    });
    fields.put(fd.name, fd);
    fieldsByOrder.add(fd);
  }

  public void add(final IndexDefinition indexDefn) {
    emissions.add(x -> indexDefn.emit(x));
    if (!indexSet.contains(indexDefn.nameToken.text)) {
      indices.add(indexDefn);
      indexSet.add(indexDefn.nameToken.text);
      typeCheckOrder.add(env -> {
        final var fd = fields.get(indexDefn.nameToken.text);
        if (fd == null) {
          env.document.createError(indexDefn, String.format("Index could not find field '%s'", indexDefn.nameToken.text), "StructureDefine");
        } else {
          final var canBeIndex = fd.type instanceof TyReactiveInteger || fd.type instanceof TyReactiveEnum || fd.type instanceof TyReactiveClient;
          if (!canBeIndex) {
            env.document.createError(indexDefn, String.format("Index for field '%s' is not possible due to type", indexDefn.nameToken.text, fd.type.getAdamaType()), "StructureDefine");
          }
        }
      });
    } else {
      typeCheckOrder.add(env -> {
        env.document.createError(indexDefn, String.format("Index was already defined: '%s'", indexDefn.nameToken.text), "StructureDefine");
      });
    }
  }

  /** add the policy to the record */
  public void addPolicy(final DefineCustomPolicy policy) {
    emissions.add(emit -> policy.emit(emit));
    if (policies.containsKey(policy.name.text)) {
      typeCheckOrder.add(env -> {
        env.document.createError(policy, String.format("Policy '%s' was already defined", policy.name.text), "RecordMethodDefine");
      });
      return;
    }
    policies.put(policy.name.text, policy);
    typeCheckOrder.add(env -> {
      policy.typeCheck(env);
    });
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(openBraceToken);
    for (final Consumer<Consumer<Token>> c : emissions) {
      c.accept(yielder);
    }
    yielder.accept(closeBraceToken);
  }

  public void end(final Token closeBraceToken) {
    this.closeBraceToken = closeBraceToken;
    ingest(closeBraceToken);
  }

  public void finalizeRecord() {
    if (!fields.containsKey("id")) {
      final var fakeId = FieldDefinition.invent(new TyReactiveInteger(null), "id");
      fields.put("id", fakeId);
      fieldsByOrder.add(fakeId);
    }
  }

  /** does this record contain this field */
  public boolean has(final String name) {
    return fields.containsKey(name) || bubbles.containsKey(name);
  }

  /** can anyone see the same result */
  public boolean hasViewerIndependentPrivacyPolicy(final Environment environment, final int depthLimit) {
    if (depthLimit <= 0) { return false; }
    if (bubbles.size() > 0 || policiesForVisibility.size() > 0) { return false; }
    // ^ (1) no bubbles, (2) no policies for visibility
    for (final FieldDefinition fd : fieldsByOrder) {
      // every field must be private or public
      final var policyCachable = fd.policy instanceof PrivatePolicy || fd.policy instanceof PublicPolicy || fd.policy == null;
      if (!policyCachable) { return false; }
      // the field...
      var resolvedType = environment.rules.Resolve(fd.type, true);
      // if maybe, list, lazy, then go extract inner type
      if (resolvedType instanceof DetailContainsAnEmbeddedType) {
        resolvedType = environment.rules.ExtractEmbeddedType(resolvedType, true);
      }
      // must be a structure
      if (resolvedType instanceof IsStructure) {
        // which is also open
        if (!((IsStructure) resolvedType).storage().hasViewerIndependentPrivacyPolicy(environment, depthLimit - 1)) { return false; }
      } else if (!(resolvedType instanceof IsReactiveValue || resolvedType instanceof IsNativeValue)) {
        // or it must a primal type
        return false;
      }
    }
    return true;
  }

  public void markPolicyForVisibility(final Token requireToken, final Token policyToCheckToken, final Token semicolon) {
    emissions.add(yielder -> {
      yielder.accept(requireToken);
      yielder.accept(policyToCheckToken);
      yielder.accept(semicolon);
    });
    final var policyToCheck = policyToCheckToken.text;
    final var dp = new DocumentPosition();
    policiesForVisibility.add(policyToCheck);
    typeCheckOrder.add(env -> {
      if (!policies.containsKey(policyToCheck)) {
        env.document.createError(dp, String.format("Policy '%s' was not found", policyToCheck), "CustomPolicy");
      }
    });
  }

  /** is the other record type the same as this type. Must be exact. */
  public boolean match(final StructureStorage other, final Environment environment) {
    if (specialization != other.specialization) { return false; }
    if (fields.size() != other.fields.size()) { return false; }
    final var thisIt = fields.values().iterator();
    final var thisOther = other.fields.values().iterator();
    while (thisIt.hasNext() && thisOther.hasNext()) {
      final var a = thisIt.next();
      final var b = thisOther.next();
      if (!a.equals(b)) { return false; }
    }
    return !thisIt.hasNext() && !thisOther.hasNext();
  }
}
