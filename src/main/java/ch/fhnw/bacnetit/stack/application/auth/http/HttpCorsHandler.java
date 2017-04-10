package ch.fhnw.bacnetit.stack.application.auth.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;

/**
 * Configure cors behaviour based on the CorsConfig
 *
 * @author IMVS, FHNW
 *
 */
public class HttpCorsHandler extends CorsHandler {

    public HttpCorsHandler(final CorsConfig config) {
        super(config);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {
        // logger.error("Caught error in CorsHandler", cause);
        ctx.fireExceptionCaught(cause);
    }

}
