/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class MachineStartResponder {
  public final JsonResponder responder;

  public MachineStartResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(String masterKey, String hostKey) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("masterKey", masterKey);
    _obj.put("hostKey", hostKey);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}