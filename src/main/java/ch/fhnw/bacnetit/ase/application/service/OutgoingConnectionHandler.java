package ch.fhnw.bacnetit.ase.application.service;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

import ch.fhnw.bacnetit.ase.encoding.TransportError;
import ch.fhnw.bacnetit.ase.encoding.exception.StackCommunicationException;
import ch.fhnw.bacnetit.ase.network.transport.ConnectionClient;
import ch.fhnw.bacnetit.ase.network.transport.api.ConnectionFactory;
import ch.fhnw.bacnetit.ase.network.transport.util.ByteBufLogger;
import ch.fhnw.bacnetit.ase.network.transport.util.MessageLogger;
import ch.fhnw.bacnetit.ase.network.transport.util.PipelineLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class OutgoingConnectionHandler {
    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(OutgoingConnectionHandler.class);

    private final EventLoopGroup clientGroup;

    private final Bootstrap bootstrap;

    protected Channel channel;

    private boolean doesLogging = false;

    private final ConnectionFactory connectionFactory;

    private final Map<InetSocketAddress, ConnectionClientContext> connectionCache = new ConcurrentHashMap<>();
    // TODO check search by host not port
    protected ConnectionClient client = null;

    public OutgoingConnectionHandler(
            final ConnectionFactory connectionFactory) {
        this(new NioEventLoopGroup(), connectionFactory);
    }

    public OutgoingConnectionHandler(final EventLoopGroup group,
            final ConnectionFactory connectionFactory) {
        this.clientGroup = group;
        this.bootstrap = new Bootstrap();
        this.connectionFactory = connectionFactory;
    }

    public void setLogging(final boolean isActive) {
        doesLogging = isActive;
    }

    public void initialize(final EndPointHandler endPointHandler) {
        bootstrap.group(clientGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(final SocketChannel ch)
                            throws Exception {
                        final ChannelPipeline p = ch.pipeline();

                        // Add all handlers from the actual connection.
                        // The connection handlers are protocol specific!
                        final ChannelHandler[] handlers = client
                                .getChannelHandlers();
                        if (doesLogging) {
                            p.addFirst(new ByteBufLogger());
                        }
                        p.addLast(new PipelineLogger()); // for debugging
                        for (final ChannelHandler handler : handlers) {
                            p.addLast(handler.getClass().getSimpleName(),
                                    handler);
                        }
                        if (doesLogging) {
                            p.addLast(new MessageLogger());
                        }

                        // Add handler for the application layer
                        p.addLast((ChannelHandler) endPointHandler);
                    }
                });
    }

    public void connect(final URI uri) throws StackCommunicationException {
        
        // Find open connections
        // Otherwise open a new connection
        client = findConnectionClient(uri);

        if (client == null) {
            throw new StackCommunicationException(
                    "No connection could be instantiated",
                    new TransportError(
                            TransportError.TransportErrorType.ConnectionError,
                            2));
        }

        if (client.getChannel() == null || !client.getChannel().isOpen()) {
            try {
                LOG.debug("Client " + client + " not connected. Connecting...");
                channel = bootstrap.connect(client.getAddress()).sync()
                        .channel();
                LOG.debug("Channel bootstrapped " + channel);
                LOG.debug(client.getClass().toString());
                client.setChannel(channel);
                LOG.debug("Channel set");
                LOG.debug("gone1");
                client.initialize();
                LOG.debug("gone2");
                LOG.debug("Connection initiazized");

            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        channel = client.getChannel();

        LOG.debug("connection made " + client);
    }

    public ChannelFuture writeAndFlush(final Object msg) {
        Preconditions.checkArgument(channel != null, "no channel is set");
        Preconditions.checkArgument(msg != null, "message is null");

        return channel.writeAndFlush(msg);
    }

    public void shutdown() {
        clientGroup.shutdownGracefully();
    }

    public void updateConnectionCache(final InetSocketAddress remoteAddress,
            final ConnectionClient client) {
        removeConnectionClient(remoteAddress);
        connectionCache.put(remoteAddress, new ConnectionClientContext(client));
        LOG.debug("Updated connection client for IP host to '" + remoteAddress
                + "'");
    }

    public void removeConnectionClient(final InetSocketAddress address) {
        final ConnectionClientContext context = connectionCache.remove(address);
        if (context != null) {
            context.client.close();
            LOG.debug("Connection " + client + " has been removed");
        }
    }

    public ConnectionClient findConnectionClient(final URI uri) {
        ConnectionClient connectionClient = null;
        try {
            final InetSocketAddress remoteAddress = new InetSocketAddress(
                    uri.getHost(), uri.getPort());
            // FIXME connection is never found with uri -> search with
            // remoteAddress
            // a connection will be found however it appears to be closed when
            // trying to bootstrap
            ConnectionClientContext context = connectionCache
                    .get(remoteAddress);
            if (context == null) {
                LOG.debug("No connection to " + remoteAddress
                        + " found, creating new one...");
                context = new ConnectionClientContext(
                        connectionFactory.createConnection(uri));

                connectionCache.put(remoteAddress, context);
                LOG.debug("Added ConnectionClientContext to connectionCache. "
                        + remoteAddress + "," + context.toString());
            } else {
                LOG.debug("Connection to " + uri + " found");
            }
            connectionClient = context.getConnectionClient();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return connectionClient;
    }

    /**
     * Wrapper for connection client that saves the timestamp when the
     * connection has been added
     *
     * @author artem
     *
     */
    static class ConnectionClientContext {
        /*
         * Add a timestamp to be able to clean the cache based on the creation
         * time.
         */
        private final long timestamp;

        /*
         * The connection client itself which should be cached for later use.
         */
        private final ConnectionClient client;

        public ConnectionClientContext(final ConnectionClient client) {
            this.timestamp = new Date().getTime();
            this.client = client;
        }

        public ConnectionClient getConnectionClient() {
            return client;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
