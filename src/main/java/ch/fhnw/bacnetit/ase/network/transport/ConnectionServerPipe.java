package ch.fhnw.bacnetit.ase.network.transport;

public interface ConnectionServerPipe {
    public ConnectionServer createConnectionServer();

    public int getServerPort();
}
