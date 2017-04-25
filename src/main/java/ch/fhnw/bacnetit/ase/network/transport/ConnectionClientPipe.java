package ch.fhnw.bacnetit.ase.network.transport;

import java.net.InetSocketAddress;

public interface ConnectionClientPipe {
    public ConnectionClient provideConnectionClient(
            InetSocketAddress remoteAddress);
}
