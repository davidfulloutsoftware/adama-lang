/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.CharsetUtil;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebClientBase {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(WebClientBase.class);
  private static final byte[] EMPTY_BODY = new byte[0];

  private final WebConfig config;
  private final EventLoopGroup group;

  public WebClientBase(WebConfig config) {
    group = new NioEventLoopGroup();
    this.config = config;
  }

  public void shutdown() {
    group.shutdownGracefully();
  }

  /** start of a new simpler execute http request */
  public void execute(SimpleHttpRequest request, SimpleHttpResponder responder) {
    URI uri = URI.create(request.uri);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    String path = uri.getRawPath();
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), host, port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new WriteTimeoutHandler(3));
        ch.pipeline().addLast(new ReadTimeoutHandler(3));
        ch.pipeline().addLast(
            new SimpleChannelInboundHandler<HttpObject>() {
              boolean first = false;

              @Override
              protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
                // Need to test this
                if (msg instanceof HttpResponse) {
                  HttpResponse response = (HttpResponse) msg;
                } else if (msg instanceof HttpContent) {
                  HttpContent content = (HttpContent) msg;
                }
              }

              @Override
              public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                // responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_EXECUTE_FAILED_EXCEPTION_CAUGHT, cause, EXLOGGER));
                ctx.close();
              }
            });
      }
    });

    b.connect(host, port).addListeners((ChannelFutureListener) future -> {
      if (future.isSuccess()) {
        // convert the method
        HttpMethod method = HttpMethod.valueOf(request.method.toUpperCase());
        // initialiize the headers
        HttpHeaders headers = new DefaultHttpHeaders(true);
        headers.set("Host", host);
        // get the body size
        long bodySize = request.body.size();
        if (method != HttpMethod.GET || bodySize > 0) {
          headers.set(HttpHeaderNames.CONTENT_LENGTH, bodySize);
        }
        // apply the headers
        for (Map.Entry<String, String> entry : request.headers.entrySet()) {
          headers.set(entry.getKey(), entry.getValue());
        }
        if (bodySize < 32 * 1024) {
          final ByteBuf content;
          if (bodySize == 0) {
            content = Unpooled.wrappedBuffer(EMPTY_BODY);
          } else {
            byte[] buffer = new byte[8196];
            content = Unpooled.buffer((int) bodySize);
            int left = (int) bodySize;
            while (left > 0) {
              int sz = request.body.read(buffer);
              content.writeBytes(buffer, 0, sz);
              left -= sz;
            }
          }
          future.channel().writeAndFlush(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path, content, headers, new DefaultHttpHeaders(true)));
        } else {
          future.channel().write(new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, path, headers));
          long left = bodySize;
          byte[] buffer = new byte[8196];
          while (left > 0) {
            int sz = request.body.read(buffer);
            final ByteBuf content;
            if (sz == buffer.length) {
              content = Unpooled.wrappedBuffer(buffer);
            } else {
              content = Unpooled.wrappedBuffer(Arrays.copyOfRange(buffer, 0, sz));
            }
            left -= sz;
            if (left == 0) {
              future.channel().write(new DefaultLastHttpContent(content));
            } else {
              future.channel().write(new DefaultHttpContent(content));
            }
          }
        }
      } else {
        // responder.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_EXECUTE_FAILED_CONNECT));
      }
    });
  }

  public void executePost(String url, HashMap<String, String> headersRaw, byte[] body, Callback<String> callback) {
    URI uri = URI.create(url);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    String path = uri.getRawPath();
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), host, port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(2424242));
        ch.pipeline().addLast(new WriteTimeoutHandler(3));
        ch.pipeline().addLast(new ReadTimeoutHandler(3));
        ch.pipeline().addLast(
            new SimpleChannelInboundHandler<FullHttpResponse>() {
              boolean first = false;
              @Override
              protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
                if (!first) {
                  first = true;
                  if (msg.status() == HttpResponseStatus.OK) {
                    callback.success(msg.content().toString(CharsetUtil.UTF_8));
                  } else {
                    callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_GET_FAILED_NOT_200, msg.content().toString(StandardCharsets.UTF_8)));
                  }
                } else {
                  System.err.println("ANOTHER MESSAGE?");
                }
                ctx.close();
              }

              @Override
              public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_GET_FAILED_EXECUTE, cause, EXLOGGER));
                ctx.close();
              }
            });
      }
    });

    b.connect(host, port).addListeners((ChannelFutureListener) future -> {
      if (future.isSuccess()) {
        System.err.println("SENDING");
        HttpHeaders headers = new DefaultHttpHeaders(true);
        for (Map.Entry<String, String> entry : headersRaw.entrySet()) {
          headers.set(entry.getKey(), entry.getValue());
        }
        headers.set("Host", host);
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path, Unpooled.wrappedBuffer(body), headers, new DefaultHttpHeaders(true));
        future.channel().writeAndFlush(request);
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_GET_FAILED_CONNECT));
      }
    });
  }

  public void executeGet(String url, HashMap<String, String> headersRaw, Callback<String> callback) {
    URI uri = URI.create(url);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    String path = uri.getRawPath();
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), host, port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(2424242));
        ch.pipeline().addLast(new WriteTimeoutHandler(3));
        ch.pipeline().addLast(new ReadTimeoutHandler(3));
        ch.pipeline().addLast(
          new SimpleChannelInboundHandler<FullHttpResponse>() {
            @Override
            protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
              if (msg.status() == HttpResponseStatus.OK) {
                callback.success(msg.content().toString(CharsetUtil.UTF_8));
              } else {
                callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_POST_FAILED_NOT_200, msg.content().toString(StandardCharsets.UTF_8)));
              }
              ctx.close();
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
              callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_POST_FAILED_EXECUTE, cause, EXLOGGER));
              ctx.close();
            }
          });
      }
    });

    b.connect(host, port).addListeners((ChannelFutureListener) future -> {
      if (future.isSuccess()) {
        HttpHeaders headers = new DefaultHttpHeaders(true);
        for (Map.Entry<String, String> entry : headersRaw.entrySet()) {
          headers.set(entry.getKey(), entry.getValue());
        }
        headers.set("Host", host);
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path, Unpooled.buffer(0), headers, new DefaultHttpHeaders(true));
        future.channel().writeAndFlush(request);
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_POST_FAILED_CONNECT));
      }
    });
  }

  public void open(String endpoint, WebLifecycle lifecycle) {
    URI uri = URI.create(endpoint);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("wss") || uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();

    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc()));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(2424242));
        ch.pipeline().addLast(new WriteTimeoutHandler(3));
        ch.pipeline().addLast(new ReadTimeoutHandler(3));
        ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
        ch.pipeline().addLast(new WebSocketClientProtocolHandler( //
            URI.create(endpoint), //
            WebSocketVersion.V13, //
            null, //
            true, //
            new DefaultHttpHeaders(), //
            50000));
        ch.pipeline().addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
          final ConcurrentHashMap<Integer, WebJsonStream> streams = new ConcurrentHashMap<>();
          WebClientConnection connection;
          private boolean closed = false;

          @Override
          protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) throws Exception {
            ObjectNode node = Json.parseJsonObject(frame.text());
            if (node.has("ping")) {
              int latency = node.get("latency").asInt();
              if (latency > 0) {
                lifecycle.ping(latency);
              }
              node.put("pong", true);
              ch.writeAndFlush(new TextWebSocketFrame(node.toString()));
              return;
            }

            if (node.has("status")) {
              if ("connected".equals(node.get("status").textValue())) {
                lifecycle.connected(connection);
              }
              return;
            }

            if (node.has("failure")) {
              int id = node.get("failure").asInt();
              int reason = node.get("reason").asInt();
              WebJsonStream streamback = streams.remove(id);
              if (streamback != null) {
                streamback.failure(reason);
              }
            } else if (node.has("deliver")) {
              int id = node.get("deliver").asInt();
              boolean done = node.get("done").asBoolean();
              WebJsonStream streamback = done ? streams.remove(id) : streams.get(id);
              if (streamback != null) {
                ObjectNode response = (ObjectNode) node.get("response");
                if (!response.isEmpty()) {
                  streamback.data(id, response);
                }
                if (done) {
                  streamback.complete();
                }
              }
            }
          }

          @Override
          public void channelActive(ChannelHandlerContext ctx) throws Exception {
            connection = new WebClientConnection(ctx, streams, () -> {
              end(ctx);
            });
          }

          @Override
          public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            end(ctx);
          }

          @Override
          public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            lifecycle.failure(cause);
            end(ctx);
          }

          private boolean end(ChannelHandlerContext ctx) {
            if (closed) {
              return false;
            }
            lifecycle.disconnected();
            closed = true;
            ctx.close();
            return true;
          }
        });
      }
    });
    b.connect(host, port).addListeners(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
          lifecycle.failure(new Exception("Failed to connect to " + host + ":" + port));
          lifecycle.disconnected();
        }
      }
    });
  }
}
