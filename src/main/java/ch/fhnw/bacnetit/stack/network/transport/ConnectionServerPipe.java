package ch.fhnw.bacnetit.stack.network.transport;

public interface ConnectionServerPipe {
    public ConnectionServer createConnectionServer();

    public int getServerPort();
}
