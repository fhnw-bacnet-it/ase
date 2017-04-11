package ch.fhnw.bacnetit.stack.application.service;

import java.util.LinkedList;
import java.util.List;

import ch.fhnw.bacnetit.stack.network.transport.ConnectionFactory;
import ch.fhnw.bacnetit.stack.network.transport.ConnectionServer;
import ch.fhnw.bacnetit.stack.network.transport.ConnectionServerPipe;
import ch.fhnw.bacnetit.stack.network.transport.util.ByteBufLogger;
import ch.fhnw.bacnetit.stack.network.transport.util.MessageLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class IncomingConnectionHandler {
    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(IncomingConnectionHandler.class);
    private final EventLoopGroup serverBossGroup;
    private final EventLoopGroup serverWorkerGroup;
    // private ServerBootstrap serverBootstrap = new ServerBootstrap();
    protected ChannelFuture future;
    private boolean doesLogging = false;
    private final ConnectionFactory connectionFactory;

    public IncomingConnectionHandler(
            final ConnectionFactory connectionFactory) {
        this(new NioEventLoopGroup(), connectionFactory);
    }

    public IncomingConnectionHandler(final EventLoopGroup group,
            final ConnectionFactory connectionFactory) {
        this.serverBossGroup = new NioEventLoopGroup(1);
        this.serverWorkerGroup = group;
        // this.serverBootstrap = new ServerBootstrap();
        this.connectionFactory = connectionFactory;
    }

    public void setLogging(final boolean isActive) {
        doesLogging = isActive;
    }

    List<ServerBootstrap> serverBootstraps = new LinkedList<ServerBootstrap>();

    public void initialize(final EndPointHandler endPointHandler) {
        for (final ConnectionServerPipe connectionServerPipe : connectionFactory
                .getConnectionServers()) {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            final ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(final SocketChannel ch)
                        throws Exception {
                    final ChannelPipeline p = ch.pipeline();

                    // Add all handlers from the actual connection.
                    // The connection handlers are protocol specific!
                    final ConnectionServer server = connectionServerPipe
                            .createConnectionServer();
                    final ChannelHandler[] handlers = server
                            .getChannelHandlers();
                    if (doesLogging) {
                        p.addFirst(new ByteBufLogger());
                    }
                    for (final ChannelHandler handler : handlers) {
                        p.addLast(handler.getClass().getSimpleName(), handler);
                    }
                    if (doesLogging) {
                        p.addLast(new MessageLogger());
                    }

                    // application layer
                    p.addLast((ChannelHandler) endPointHandler);
                }
            };

            serverBootstrap.group(serverBossGroup, serverWorkerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(channelInitializer);

            serverBootstraps.add(serverBootstrap);

            try {
                future = serverBootstrap
                        .bind(connectionServerPipe.getServerPort()).sync();
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                System.err.println("can't bind to server port");
                e.printStackTrace();
            }
        }
    }

    public void waitUntilClosed() {
        try {
            future.channel().closeFuture().sync();
        } catch (final InterruptedException e) {
            shutdown();
        }
    }

    public void shutdown() {
        serverBossGroup.shutdownGracefully();
        serverWorkerGroup.shutdownGracefully();
    }

}
