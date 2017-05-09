package ch.fhnw.bacnetit.ase.network.transport.api;

import ch.fhnw.bacnetit.ase.network.transport.ConnectionServer;

public interface ConnectionServerPipe {
    public ConnectionServer createConnectionServer();

    public int getServerPort();
}
