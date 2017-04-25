package ch.fhnw.bacnetit.ase.network.transport;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ConnectionFactory {
    private final Map<String, ConnectionClientPipe> connectionClients = new HashMap<>();
    private final Map<String, ConnectionServerPipe> connectionServers = new HashMap<>();
    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(ConnectionFactory.class);

    public void addConnectionClient(final String protocol,
            final ConnectionClientPipe connectionClientPipe) {
        LOG.debug("Added client pipe for " + protocol);
        connectionClients.put(protocol, connectionClientPipe);
    }

    public void addConnectionServer(final String protocol,
            final ConnectionServerPipe connectionServerPipe) {
        LOG.debug("Added server pipe for " + protocol);
        connectionServers.put(protocol, connectionServerPipe);
    }

    public LinkedList<ConnectionServerPipe> getConnectionServers() {
        return new LinkedList<ConnectionServerPipe>(connectionServers.values());
    }

    public LinkedList<ConnectionClientPipe> getConnectionClients() {
        return new LinkedList<ConnectionClientPipe>(connectionClients.values());
    }

    public ConnectionClient createConnection(final URI remoteUri)
            throws Exception {
        LOG.debug("Creating connection to " + remoteUri + " with scheme "
                + remoteUri.getScheme());
        LOG.debug("Found client factory: "
                + connectionClients.get(remoteUri.getScheme()));
        return connectionClients.get(remoteUri.getScheme())
                .provideConnectionClient(new InetSocketAddress(
                        remoteUri.getHost(), remoteUri.getPort()));
    }
}
