package ch.fhnw.bacnetit.stack.application.transaction;

import ch.fhnw.bacnetit.stack.network.transport.TransportProtocolType;

public class ProtocolConfig {
    public final TransportProtocolType protocol;

    public final int serverPort;

    public ProtocolConfig(final String protocolName, final int serverPort) {
        this.protocol = TransportProtocolType.fromString(protocolName);
        this.serverPort = serverPort;
    }
}
