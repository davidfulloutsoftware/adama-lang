/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** this bridges messages to and from JSON. This has the role of bridging types within the living document into JSON and back. */
public abstract class MessageBridge<Ty extends NtMessageBase> implements Bridge<Ty> {
  @Override
  public void appendTo(final Ty value, final ArrayNode array) {
    array.add(value.convertToObjectNode());
  }

  public abstract Ty convert(ObjectNode node);

  public Ty[] convertArrayMessage(final ObjectNode objectNode) {
    final var arrayNode = (ArrayNode) objectNode.get("items");
    final var arr = makeArray(arrayNode.size());
    for (var k = 0; k < arr.length; k++) {
      arr[k] = fromJsonNode(arrayNode.get(k));
    }
    return arr;
  }

  @Override
  public Ty fromJsonNode(final JsonNode node) {
    ObjectNode tree;
    if (node instanceof ObjectNode) {
      tree = (ObjectNode) node;
    } else {
      tree = Utility.createObjectNode();
    }
    return convert(tree);
  }

  public Ty readFromMessageObject(final ObjectNode node, final String field) {
    return fromJsonNode(node.get(field));
  }

  @Override
  public JsonNode toPrivateJsonNode(final NtClient who, final Ty value) {
    return value.convertToObjectNode();
  }

  @Override
  public void writeTo(final String name, final Ty value, final ObjectNode node) {
    node.set(name, value.convertToObjectNode());
  }
}
