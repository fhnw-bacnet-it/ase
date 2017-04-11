package ch.fhnw.bacnetit.stack.network.transport;

public enum TransportProtocolType {
    WebSocket("WS"), WebSocketSecure("WSS");

    private String name;

    TransportProtocolType(final String name) {
        this.name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public static TransportProtocolType fromString(final String name) {
        final String protocol = name.toLowerCase();
        if (protocol != null) {
            for (final TransportProtocolType tpt : TransportProtocolType
                    .values()) {
                if (protocol.equalsIgnoreCase(tpt.name)) {
                    return tpt;
                }
            }
        }
        throw new IllegalArgumentException(
                "No TransportProtocolType with name " + name + " found");
    }
}
