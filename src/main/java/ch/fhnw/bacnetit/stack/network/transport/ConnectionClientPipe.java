package ch.fhnw.bacnetit.stack.network.transport;

import java.net.InetSocketAddress;

public interface ConnectionClientPipe {
    public ConnectionClient provideConnectionClient(
            InetSocketAddress remoteAddress);
}
