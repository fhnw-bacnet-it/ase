package ch.fhnw.bacnetit.ase.application.transaction;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.fhnw.bacnetit.ase.application.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.application.ExceptionManager;
import ch.fhnw.bacnetit.ase.application.service.EndPointHandler;
import ch.fhnw.bacnetit.ase.application.service.IncomingConnectionHandler;
import ch.fhnw.bacnetit.ase.application.service.OutgoingConnectionHandler;
import ch.fhnw.bacnetit.ase.encoding.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.ControlMessage;
import ch.fhnw.bacnetit.ase.encoding.ControlMessageInitEvent;
import ch.fhnw.bacnetit.ase.encoding.ControlMessageReceivedEvent;
import ch.fhnw.bacnetit.ase.encoding.TPDU;
import ch.fhnw.bacnetit.ase.encoding.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.T_UnitDataRequest;
import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger31;
import ch.fhnw.bacnetit.ase.network.transport.ConnectionClient;
import ch.fhnw.bacnetit.ase.network.transport.ConnectionFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

@Sharable
public class Channel extends ChannelDuplexHandler
        implements EndPointHandler, ApplicationService, ChannelConfiguration {
    private static final InternalLogger LOG = InternalLoggerFactory
            .getInstance(Channel.class);
    private OutgoingConnectionHandler outgoingConnectionHandler;
    private IncomingConnectionHandler incomingConnectionHandler;
    private final TransactionManager transactionManager = new TransactionManager();
    private final List<ChannelListener> channelListeners = new ArrayList<ChannelListener>();
    private BACnetEntityListener entityListener = null;
    private boolean isInitialized = false;

    @Override
    public void setEntityListener(final BACnetEntityListener _entityListener) {
        if (this.entityListener != null) {
            LOG.error("EntityListener is already set");
            return;
        }
        this.entityListener = _entityListener;

    }

    private List<ChannelListener> getChannelListeners() {
        return this.channelListeners;
    }

    // Register a device which use this instance as messaging
    @Override
    public void registerChannelListener(final ChannelListener msgListener) {
        if (msgListener != null) {
            this.channelListeners.add(msgListener);
            LOG.debug("Channel listener " + msgListener.getEID().getIdentifier()
                    + " added");
        }
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx,
            final Object evt) {
        try {
            // Event ControlMessageReceivedEvent: Do register the received (by
            // ControlMessage)
            // BacnetEids
            LOG.debug("Host received " + evt);
            if (evt instanceof ControlMessageReceivedEvent) {
                LOG.debug("Got a ControlMessageReceivedEvent");
                for (final UnsignedInteger31 ui : ((ControlMessageReceivedEvent) evt)
                        .getControlMessage().getBacnetEidList()) {
                    // in case localhost is written as
                    // "localhost/127.0.0.1:8080"
                    final String[] uri = ctx.channel().remoteAddress()
                            .toString().split("/");

                    // control message protocol is always websocket
                    final URI remoteUri = new URI("ws://" + uri[uri.length]);
                    if (this.entityListener == null) {
                        LOG.error("The BACnetEntityListener isn't set");
                        return;
                    }
                    this.entityListener.onRemoteAdded(new BACnetEID(ui),
                            remoteUri);
                    LOG.debug(
                            "Pass received control message to BACnetEntityHandler");

                }
            }
            // Event ControlMessageInitEvent: Prepare and send a ControlMessage
            // containing the ChannelListeners
            else if (evt instanceof ControlMessageInitEvent) { // TODO check if
                                                               // control
                                                               // messages are
                                                               // allowed
                LOG.debug("Got a ControlMessageInitEvent");
                // Create ControlMessage

                final byte controlMessageType = ControlMessage.ADDREMOTE;
                final List<UnsignedInteger31> bacneteids = new LinkedList<UnsignedInteger31>();
                // Read out managed devices
                for (final ChannelListener cl : this.getChannelListeners()) {
                    bacneteids.add(
                            new UnsignedInteger31(cl.getEID().getIdentifier()));
                    LOG.debug(
                            "Got a ControlMessageInitEvent - Add to the sending list: "
                                    + cl.getEID().getIdentifierAsString());
                }
                // TODO Read out group controller ids
                final List<UnsignedInteger31> groupIds = null;
                final ControlMessage cm = new ControlMessage(controlMessageType,
                        bacneteids, groupIds);

                ctx.writeAndFlush(cm);
            } else if (evt instanceof WriteTimeoutException) {
                System.out.println("WriteTimeoutException");
            } else if (evt instanceof TPDU) {
                /*
                 * Handle the bacnet message.
                 *
                 * TPDU msg = (TPDU) evt; if (msg.getService() instanceof
                 * ConfirmedRequestService) {
                 * LOG.debug("trigger received for ConfirmedRequestService: " +
                 * msg.getService()); } else if (msg.getService() instanceof
                 * UnconfirmedRequestService) {
                 * LOG.debug("trigger received for UnconfirmedRequestService: "
                 * + msg.getService()); } else if (msg.getService() instanceof
                 * AcknowledgementService) {
                 * LOG.debug("trigger received for AcknowledgementService: " +
                 * msg.getService()); } else if (msg.getBody().length > 0) {
                 * LOG.debug("trigger received for Response"); }
                 */
            } else if (evt instanceof ConnectionClient) {
                /*
                 * Request received from the transport binding layer. Check if
                 * the connection must be cached, what is needed if a stateful
                 * protocol like websocket is in use. Update DirectoryService if
                 * necessary.
                 */
                final ConnectionClient connection = (ConnectionClient) evt;
                final InetSocketAddress remoteAddress = (InetSocketAddress) ctx
                        .channel().remoteAddress();
                LOG.debug("(evt instanceof ConnectionClient) ");
                LOG.debug(remoteAddress.toString());

                /*
                 * Create or update the connection which contains a channel to
                 * the initiating remote partner and which can be used in future
                 * as communication channel. We are acting as server!
                 */

                outgoingConnectionHandler.updateConnectionCache(remoteAddress,
                        connection);
            } else if (evt.equals(ChannelEvent.CLOSE_CHANNEL_EVENT)) {
                /*
                 * Close the channel which is not needed any longer.
                 */
                ctx.channel().close();
                LOG.debug("trigger CLOSE_CHANNEL_EVENT received");
            } else if (evt.equals(
                    ChannelEvent.CLOSE_CHANNEL_EVENT_ONLY_ON_UNCONFIRMED_REQUEST)) {
                /*
                 * Close the channel but only if the service request was a
                 * unconfirmed request.
                 */
                final ChannelEvent event = (ChannelEvent) evt;
                if (event.getMsg() instanceof TPDU) {
                    final TPDU msg = (TPDU) event.getMsg();
                    // if (msg.getService() instanceof
                    // UnconfirmedRequestService) {
                    if (!msg.isConfirmedRequest()) {
                        ctx.channel().close();
                        LOG.debug(
                                "trigger CLOSE_CHANNEL_EVENT_ONLY_ON_UNCONFIRMED_REQUEST received");
                    }
                }
            } else if (evt.equals(ChannelEvent.REMOVE_CONNECTION_EVENT)) {
                LOG.debug("trigger REMOVE_CONNECTION_EVENT received");
                final ChannelEvent event = (ChannelEvent) evt;
                if (event.getMsg() instanceof InetSocketAddress) {
                    final InetSocketAddress address = (InetSocketAddress) event
                            .getMsg();
                    outgoingConnectionHandler.removeConnectionClient(address);
                }
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof TPDU) {
            System.out.println("Is a invoke id here no2:");
            System.out.println(((TPDU) msg).getInvokeId());
            final TPDU tpdu = (TPDU) msg;
            LOG.debug("Channel got a BACnetMessage");
            // TODO, next line is stop and fail
            LOG.debug("whole message: " + tpdu.toString());

            this.onIndication(tpdu, ctx);

            /*
             * Change for ExtractingService ASDU request =
             * getServiceFromBody(input.getBody());
             *
             * // Incoming: Confirmed Request or Unconfirmed Request if (request
             * instanceof ConfirmedRequest || request instanceof
             * UnconfirmedRequest) {
             * LOG.debug("Channel got a confirmed or unconfirmed request");
             * this.onIndication(input, request, ctx); } // Incoming: AckAPDU
             * else if (request instanceof AckASDU) {
             * LOG.debug("Channel got an AckASDU"); this.onIndication(input,
             * getServiceFromBody(input.getBody()), ctx); } // Incoming:
             * ComplexAPDU else if (request instanceof ComplexACK) {
             * LOG.debug("Channel got an ComplexACK"); this.onIndication(input,
             * getServiceFromBody(input.getBody()), ctx); } else {
             * LOG.debug("Unknown request received: " + request); throw new
             * TransactionManagerExcpetion("Unknown request"); }
             */

        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) {
        // handleException(cause, null, null);

        new ExceptionManager().manageException(cause, null, null, this);
    }

    @Override
    public synchronized void doRequest(
            final T_UnitDataRequest t_unitDataRequest) {

        // Pass Outgoing request to the Transaction Manager
        t_unitDataRequest.getData().setInvokeId(transactionManager
                .createOutboundTransaction(t_unitDataRequest));

        LOG.debug("T_UnitData destination: "
                + t_unitDataRequest.getDestinationAddress());
        outgoingConnectionHandler
                .connect(t_unitDataRequest.getDestinationAddress());
        outgoingConnectionHandler.writeAndFlush(t_unitDataRequest.getData());
    }

    @Override
    public void doCancel(final BACnetEID destination, final BACnetEID source) {
        // TODO Auto-generated method stub
    }

    private void onIndication(final TPDU msg, final ChannelHandlerContext ctx) {
        System.out.println("Is a invoke id here?");
        System.out.println(msg.getInvokeId());

        T_UnitDataIndication indicationUnit = null;
        indicationUnit = new T_UnitDataIndication(null, msg, msg.getPriority());

        transactionManager.createInboundTransaction(indicationUnit);

        for (final ChannelListener l : this.channelListeners) {
            if (l.getEID().equals(msg.getDestinationEID())) {
                System.out.println(
                        "do a onIndication at " + l.getEID().toString());
                l.onIndication(indicationUnit, ctx);
            }
        }
    }

    /*
     * private void handleError(ErrorClass errorClass, ErrorCode errorCode, TPDU
     * msg, ConfirmedRequest request, Unsigned8 invokeId, ChannelHandlerContext
     * ctx) throws Exception {
     *
     * BACnetEID dest = msg.getSourceEID(); BACnetEID src =
     * msg.getDestinationEID();
     *
     * byte serviceId = ((ConfirmedRequest)
     * request).getServiceRequest().getChoiceId(); BACnetErrorAck errorACK = new
     * BACnetErrorAck(errorClass, errorCode, serviceId); ByteQueue bqService =
     * new ByteQueue(); errorACK.write(bqService);
     *
     * TPDU tpdu = new TPDU(src, dest, bqService); URI destUri =
     * DirectoryService.getInstance().resolve(dest); T_UnitDataRequest
     * t_unitDataRequest = new T_UnitDataRequest(destUri, tpdu, 1, false, null);
     * this.doRequest(t_unitDataRequest); }
     */
    /*
     * private ASDU getServiceFromBody(byte[] body) { ByteQueue queue = new
     * ByteQueue(body); ServicesSupported servicesSupported = new
     * ServicesSupported(); servicesSupported.setAll(true);
     * IncomingRequestParser parser = new
     * IncomingRequestParser(servicesSupported, queue); ASDU request = null;
     *
     * try { request = parser.parse(); } catch (BACnetException e) {
     *
     * LOG.debug(e.toString()); throw new
     * TransactionManagerExcpetion(e.getMessage()); } return request; }
     */

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    // public boolean isInitialized() {
    // return isInitialized;
    // }

    @Override
    public void initializeAndStart(final ConnectionFactory connectionFactory) {
        // Check if at least one protocol is set

        // Use the same EventLoopGroup for all EventLoop
        final EventLoopGroup group = new NioEventLoopGroup();

        outgoingConnectionHandler = new OutgoingConnectionHandler(group,
                connectionFactory);
        outgoingConnectionHandler.setLogging(false); // TODO move this somewhere
                                                     // convenient
        outgoingConnectionHandler.initialize(this);

        incomingConnectionHandler = new IncomingConnectionHandler(group,
                connectionFactory);
        incomingConnectionHandler.setLogging(false); // TODO move this somewhere
                                                     // convenient
        incomingConnectionHandler.initialize(this);
        // incomingConnectionHandler.start();

        isInitialized = true;
    }

    @Override
    public IncomingConnectionHandler getServerChannel() {
        return this.incomingConnectionHandler;
    }

    public OutgoingConnectionHandler getClientChannel() {
        return this.outgoingConnectionHandler;
    }

    public void waitUntilClosed() {
        incomingConnectionHandler.waitUntilClosed();
    }

    @Override
    public void shutdown() {
        incomingConnectionHandler.shutdown();
        outgoingConnectionHandler.shutdown();
    }

}
