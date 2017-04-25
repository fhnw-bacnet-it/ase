package ch.fhnw.bacnetit.ase.network.transport;

import io.netty.channel.ChannelHandler;

public interface ConnectionServer {
    ChannelHandler[] getChannelHandlers();
}
