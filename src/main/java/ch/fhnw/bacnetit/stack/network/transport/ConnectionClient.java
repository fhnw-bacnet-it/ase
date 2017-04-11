package ch.fhnw.bacnetit.stack.network.transport;

import java.net.SocketAddress;
import java.net.URI;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public interface ConnectionClient {
    void close();

    SocketAddress getAddress();

    ChannelHandler[] getChannelHandlers();

    void initialize();

    Channel getChannel();

    // String getBACnetEID(); // TODO check if still valid, connection not bound
    // to ONE EID!

    URI getURI();

    void setChannel(Channel channel);
}
