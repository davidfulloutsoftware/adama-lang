/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.connection.Session;

public interface DocumentStreamHandler {
  public void bind();

  public void handle(ConnectionSendRequest request, SeqResponder responder);

  public void handle(ConnectionSendOnceRequest request, SeqResponder responder);

  public void handle(ConnectionCanAttachRequest request, YesResponder responder);

  public void handle(ConnectionAttachRequest request, SeqResponder responder);

  public void handle(ConnectionUpdateRequest request, SimpleResponder responder);

  public void handle(ConnectionEndRequest request, SimpleResponder responder);

  public void logInto(ObjectNode node);

  public void disconnect(long id);

}
