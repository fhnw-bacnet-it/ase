package ch.fhnw.bacnetit.stack;
/*
 * package ch.fhnw.bacnet.ipb;
 *
 * import java.net.URI;
 *
 * import ch.fhnw.bacnet.ipb.application.BACnetEntityListener; import
 * ch.fhnw.bacnet.ipb.application.NetworkPortObj; import
 * ch.fhnw.bacnet.ipb.application.configuration.DiscoveryConfig; import
 * ch.fhnw.bacnet.ipb.application.configuration.KeystoreConfig; import
 * ch.fhnw.bacnet.ipb.application.configuration.TruststoreConfig; import
 * ch.fhnw.bacnet.ipb.application.deviceobjects.BACnetEID; import
 * ch.fhnw.bacnet.ipb.application.encoding.type.itb.primitive.
 * T_UnitDataIndication; import
 * ch.fhnw.bacnet.ipb.application.encoding.type.primitive.CharacterString;
 * import ch.fhnw.bacnet.ipb.application.encoding.type.primitive.Real; import
 * ch.fhnw.bacnet.ipb.application.encoding.util.ByteQueue; import
 * ch.fhnw.bacnet.ipb.application.transaction.Channel; import
 * ch.fhnw.bacnet.ipb.application.transaction.ChannelListener; import
 * ch.fhnw.bacnet.ipb.network.directory.DirectoryBindingType; import
 * ch.fhnw.bacnet.ipb.network.directory.DirectoryService; import
 * ch.fhnw.bacnet.ipb.network.transport.ConnectionFactory; import
 * ch.fhnw.bacnet.ipb.network.transport.ws.incoming.WSConnectionServerFactory;
 * import
 * ch.fhnw.bacnet.ipb.network.transport.ws.outgoing.WSConnectionClientFactory;
 * import io.netty.channel.ChannelHandlerContext;
 *
 * public class BasicBasicMain {
 *
 * private static String PW = "123456"; private static String KS_PATH =
 * System.getProperty("user.home") +
 * "/git/BACnet-IT/stores/siemens_agent01/keyStoreDev1.jks"; private static
 * String TS_PATH = System.getProperty("user.home") +
 * "/git/BACnet-IT/stores/siemens_agent01/trustStore.jks";
 *
 * public static void main(final String[] args) throws Exception {
 * DirectoryService.init(); DirectoryService.getInstance() .setDns(new
 * DiscoveryConfig(DirectoryBindingType.DNSSD.name(), "86.119.39.127",
 * "itb.bacnet.ch.", "bds._sub._bacnet._tcp.", "dev._sub._bacnet._tcp.",
 * "obj._sub._bacnet._tcp.", false));
 *
 * final KeystoreConfig keystoreConfig = new KeystoreConfig(KS_PATH, PW, "dev");
 * // final TruststoreConfig truststoreConfig = new // TruststoreConfig(TS_PATH,
 * // PW, "trust"); final NetworkPortObj npo = new NetworkPortObj("ws", 8080,
 * keystoreConfig);
 *
 * final Channel channel = new Channel();
 *
 * final ChannelListener channelListener = new ChannelListener( new
 * BACnetEID(2000)) {
 *
 * @Override public void onIndication( final T_UnitDataIndication
 * tUnitDataIndication, final ChannelHandlerContext ctx) { final Real real = new
 * Real( new ByteQueue(tUnitDataIndication.getData().getBody()));
 * System.out.println("Received value: " + real.floatValue()); }
 *
 * @Override public void onError(final String cause) { }
 *
 * @Override public CharacterString getURIfromNPO() { return npo.getUri(); } };
 * channel.registerChannelListener(channelListener);
 *
 * final BACnetEntityListener bacNetEntityListener = new BACnetEntityListener()
 * {
 *
 * @Override public void onRemoteRemove(final BACnetEID eid) { }
 *
 * @Override public void onRemoteAdded(final BACnetEID eid, final URI uri) {
 * DirectoryService.getInstance().register(eid, uri, false, true); }
 *
 * @Override public void onLocalRequested(final BACnetEID eid) { } };
 * channel.setEntityListener(bacNetEntityListener);
 *
 * final ConnectionFactory connectionFactory = new ConnectionFactory();
 * connectionFactory.addConnectionClient("ws", new WSConnectionClientFactory());
 * connectionFactory.addConnectionServer("ws", new
 * WSConnectionServerFactory(8080));
 * channel.initializeAndStart(connectionFactory); }
 *
 * }
 */
