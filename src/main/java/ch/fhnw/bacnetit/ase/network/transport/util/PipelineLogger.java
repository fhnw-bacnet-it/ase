package ch.fhnw.bacnetit.ase.network.transport.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PipelineLogger extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx,
            final Object msg) {

        final StringBuilder output = new StringBuilder();
        output.append("//////////////////////////////");
        output.append(System.getProperty("line.separator"));
        output.append("THE PIPELINE");
        output.append(System.getProperty("line.separator"));
        output.append("For Channel with HashCode");
        output.append(":");
        output.append(ctx.channel().hashCode());
        output.append(System.getProperty("line.separator"));
        ctx.pipeline().forEach(h -> {
            output.append(h.getKey());
            output.append(System.getProperty("line.separator"));
        });
        output.append("//////////////////////////////");
        // System.out.println(output.toString());
        

        final ByteBuf in = (ByteBuf) msg;
        ctx.fireChannelRead(in.retain());

    }

}
