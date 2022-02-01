/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;


import org.adamalang.common.*;
import org.adamalang.common.metrics.*;
import org.adamalang.connection.*;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRouter {
  public final ConnectionNexus nexus;
  public final RootHandler handler;
  public final HashMap<Long, AttachmentUploadHandler> inflightAttachmentUpload;
  public final HashMap<Long, WaitingForEmailHandler> inflightWaitingForEmail;
  public final HashMap<Long, DocumentStreamHandler> inflightDocumentStream;

  public ConnectionRouter(ConnectionNexus nexus, RootHandler handler) {
    this.nexus = nexus;
    this.handler = handler;
    this.inflightAttachmentUpload = new HashMap<>();
    this.inflightWaitingForEmail = new HashMap<>();
    this.inflightDocumentStream = new HashMap<>();
  }

  public void disconnect() {
    for (Map.Entry<Long, AttachmentUploadHandler> entry : inflightAttachmentUpload.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightAttachmentUpload.clear();
    for (Map.Entry<Long, WaitingForEmailHandler> entry : inflightWaitingForEmail.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightWaitingForEmail.clear();
    for (Map.Entry<Long, DocumentStreamHandler> entry : inflightDocumentStream.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightDocumentStream.clear();
  }

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      long requestId = request.id();
      String method = request.method();
      nexus.executor.execute(new NamedRunnable("handle", method) {
        @Override
        public void execute() throws Exception {
          nexus.session.activity();
          switch (method) {
            case "init/start": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_InitStart.start();
              InitStartRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(InitStartRequest resolved) {
                  WaitingForEmailHandler handlerMade = handler.handle(nexus.session, resolved, new SimpleResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightWaitingForEmail, requestId, responder)));
                  inflightWaitingForEmail.put(requestId, handlerMade);
                  handlerMade.bind();
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "init/revoke-all": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_InitRevokeAll.start();
              InitRevokeAllRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(InitRevokeAllRequest resolved) {
                  WaitingForEmailHandler handlerToUse = inflightWaitingForEmail.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(441361);
                    responder.error(new ErrorCodeException(441361));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "init/generate-identity": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_InitGenerateIdentity.start();
              InitGenerateIdentityRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(InitGenerateIdentityRequest resolved) {
                  WaitingForEmailHandler handlerToUse = inflightWaitingForEmail.remove(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(454673);
                    responder.error(new ErrorCodeException(454673));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "probe": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_Probe.start();
              ProbeRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(ProbeRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityCreate.start();
              AuthorityCreateRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityCreateRequest resolved) {
                  handler.handle(nexus.session, resolved, new ClaimResultResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/set": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthoritySet.start();
              AuthoritySetRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AuthoritySetRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/get": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityGet.start();
              AuthorityGetRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityGetRequest resolved) {
                  handler.handle(nexus.session, resolved, new KeystoreResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityList.start();
              AuthorityListRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityListRequest resolved) {
                  handler.handle(nexus.session, resolved, new AuthorityListingResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/destroy": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityDestroy.start();
              AuthorityDestroyRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityDestroyRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceCreate.start();
              SpaceCreateRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceCreateRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/usage": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceUsage.start();
              SpaceUsageRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceUsageRequest resolved) {
                  handler.handle(nexus.session, resolved, new BillingUsageResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/get": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceGet.start();
              SpaceGetRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceGetRequest resolved) {
                  handler.handle(nexus.session, resolved, new PlanResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSet.start();
              SpaceSetRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/delete": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceDelete.start();
              SpaceDeleteRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceDeleteRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set-role": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSetRole.start();
              SpaceSetRoleRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetRoleRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/reflect": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceReflect.start();
              SpaceReflectRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceReflectRequest resolved) {
                  handler.handle(nexus.session, resolved, new ReflectionResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "space/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceList.start();
              SpaceListRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(SpaceListRequest resolved) {
                  handler.handle(nexus.session, resolved, new SpaceListingResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "document/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentCreate.start();
              DocumentCreateRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(DocumentCreateRequest resolved) {
                  handler.handle(nexus.session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "document/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentList.start();
              DocumentListRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(DocumentListRequest resolved) {
                  handler.handle(nexus.session, resolved, new KeyListingResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/create": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_ConnectionCreate.start();
              ConnectionCreateRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionCreateRequest resolved) {
                  DocumentStreamHandler handlerMade = handler.handle(nexus.session, resolved, new DataResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightDocumentStream, requestId, responder)));
                  inflightDocumentStream.put(requestId, handlerMade);
                  handlerMade.bind();
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/send": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionSend.start();
              ConnectionSendRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionSendRequest resolved) {
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(457745);
                    responder.error(new ErrorCodeException(457745));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/update": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionUpdate.start();
              ConnectionUpdateRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionUpdateRequest resolved) {
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(438302);
                    responder.error(new ErrorCodeException(438302));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/end": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionEnd.start();
              ConnectionEndRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionEndRequest resolved) {
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.remove(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(474128);
                    responder.error(new ErrorCodeException(474128));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/start": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_AttachmentStart.start();
              AttachmentStartRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentStartRequest resolved) {
                  AttachmentUploadHandler handlerMade = handler.handle(nexus.session, resolved, new ProgressResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightAttachmentUpload, requestId, responder)));
                  inflightAttachmentUpload.put(requestId, handlerMade);
                  handlerMade.bind();
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/append": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AttachmentAppend.start();
              AttachmentAppendRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentAppendRequest resolved) {
                  AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.get(resolved.upload);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(477201);
                    responder.error(new ErrorCodeException(477201));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/finish": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AttachmentFinish.start();
              AttachmentFinishRequest.resolve(nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentFinishRequest resolved) {
                  AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.remove(resolved.upload);
                  if (handlerToUse != null) {
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder)));
                  } else {
                    mInstance.failure(478227);
                    responder.error(new ErrorCodeException(478227));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  responder.error(ex);
                }
              });
            } return;
          }
          responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
        }
      });
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
