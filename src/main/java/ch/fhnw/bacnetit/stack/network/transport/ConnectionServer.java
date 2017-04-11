package ch.fhnw.bacnetit.stack.network.transport;

import io.netty.channel.ChannelHandler;

public interface ConnectionServer {
    ChannelHandler[] getChannelHandlers();
}
