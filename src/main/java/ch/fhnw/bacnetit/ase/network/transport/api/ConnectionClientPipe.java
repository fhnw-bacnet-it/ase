package ch.fhnw.bacnetit.ase.network.transport.api;

import java.net.InetSocketAddress;

import ch.fhnw.bacnetit.ase.network.transport.ConnectionClient;

public interface ConnectionClientPipe {
    public ConnectionClient provideConnectionClient(
            InetSocketAddress remoteAddress);
}
