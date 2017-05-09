package ch.fhnw.bacnetit.ase.application.auth.http;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import ch.fhnw.bacnetit.ase.application.configuration.api.HttpAuthConfig;
import ch.fhnw.bacnetit.ase.application.transaction.ChannelEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Configure the Basic Auth behaviour (Authentification over HTTP)
 *
 * @author IMVS, FHNW
 *
 */
public class HttpBasicAuthHandler extends SimpleChannelInboundHandler<Object> {
    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(SimpleChannelInboundHandler.class);
    // Mustn't be local to channel or handler instance
    // Each HTTP Request creates a new channel, thus the Set
    // httpBasicAuthSessions must be
    // global available at runtime for server part
    private static Set<String> httpBasicAuthSessions = new HashSet<String>();
    private final HttpAuthConfig httpAuthConfig;

    public static Set<String> getHttpBasicAuthSessions() {
        return httpBasicAuthSessions;
    }

    public HttpBasicAuthHandler(final HttpAuthConfig httpAuthConfig) {
        this.httpAuthConfig = httpAuthConfig;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx,
            final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, ((WebSocketFrame) msg).retain());
        }
    }

    /**
     * Compares incoming Basic Auth credentials with the configured list of
     * allowed credentials. Prepares the Http Reponse with a Cookie.
     *
     * @param ctx
     * @param req
     */
    private void checkBasicAuth(final ChannelHandlerContext ctx,
            final FullHttpRequest req) {
        final String emailandpw = req.headers().get("Authorization");

        final String[] parts = Pattern.compile(" ").split(emailandpw);
        if (parts.length != 2) {
            System.err.println("Wrong Basic Auth Syntax");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
                    HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION));
            return;
        }

        if (httpAuthConfig.httpAuthValidate.equals(emailandpw)) {
            System.err.println("Wrong Authorization credentials");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
                    HttpResponseStatus.FORBIDDEN));
            return;
        }

        // GET Authorization Request enabled
        final FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1,
                HttpResponseStatus.OK);

        // Generate UUIDs
        final String uuid = UUID.randomUUID().toString();

        // Save session
        httpBasicAuthSessions.add("SID=" + uuid);
        resp.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX
                .encode("SID", uuid + ";max-age=700;path=/"));
        resp.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET");
        resp.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,
                "OPTIONS");

        sendHttpResponse(ctx, req, resp);
        System.err.println("user is loggedin");

        return;
    }

    /**
     * Handles incoming HTTP requests
     *
     * @param ctx
     * @param httpreq
     * @throws BACnetException
     */
    private void handleHttpRequest(final ChannelHandlerContext ctx,
            final FullHttpRequest httpreq) {

        // Handle a bad request.
        if (!httpreq.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, httpreq,
                    new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Preflight
        if (httpreq.method() == HttpMethod.OPTIONS) {
            sendHttpResponse(ctx, httpreq, new DefaultFullHttpResponse(HTTP_1_1,
                    HttpResponseStatus.OK));
            System.err.println("sent preflight");
            return;
        }

        // Authorization
        if (httpAuthConfig.isEnabled()
                && httpreq.headers().get("Authorization") != null) {
            checkBasicAuth(ctx, httpreq);
            return;
        }

        // Upgrade Request
        if (httpreq.headers().contains("Upgrade")) {
            handleWebSocketFrame(ctx, httpreq.retain());
            return;
        }

        // Unknown Request
        sendHttpResponse(ctx, httpreq, new DefaultFullHttpResponse(HTTP_1_1,
                HttpResponseStatus.BAD_REQUEST));
    }

    private static void sendHttpResponse(final ChannelHandlerContext ctx,
            final FullHttpRequest req, final FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        final ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                CharsetUtil.UTF_8);
        res.content().writeBytes(buf);
        buf.release();
        HttpUtil.setContentLength(res, res.content().readableBytes());

        // Send the response and close the connection if necessary.
        final ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // Go further to WebSocketConnectionServerHandler
    private void handleWebSocketFrame(final ChannelHandlerContext ctx,
            final Object msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx)
            throws Exception {
        LOG.debug("UnregisterEvent reveived from "
                + ctx.channel().remoteAddress());
        final ChannelEvent event = ChannelEvent.REMOVE_CONNECTION_EVENT;
        event.setMsg(ctx.channel().remoteAddress());
        ctx.fireUserEventTriggered(event);
    }

}
