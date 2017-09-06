/**
 *
 */
package ch.fhnw.bacnetit.ase.application.service;

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;

import ch.fhnw.bacnetit.ase.application.service.api.ASEServices;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelConfiguration;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelFactory;
import ch.fhnw.bacnetit.ase.application.service.api.TransportBindingService;
import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger8;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;
import ch.fhnw.bacnetit.ase.transportbinding.service.api.ASEService;

/**
 * @author IMVS, FHNW
 *
 */
public class TestASEChannel {

    @Test
    public void createInstanceWithoutFactory() {
        boolean worked;
        try {
            final ASEChannel aseChannel = new ASEChannel(this);
            worked = true;
        } catch (final Exception e) {
            System.err.println(e);
            worked = false;
        }
        Assert.assertEquals(false, worked);
    }

    @Test
    public void createInstanceWithoutFactory2() {
        boolean worked;
        try {
            final ASEChannel aseChannel = new ASEChannel(new Object());
            worked = true;
        } catch (final Exception e) {
            System.err.println(e);
            worked = false;
        }
        Assert.assertEquals(false, worked);
    }

    @Test
    public void createInstanceWithFactory() {
        boolean worked;
        try {
            final ASEServices aseServices = ChannelFactory.getInstance();
            worked = true;
        } catch (final Exception e) {
            System.err.println(e);
            worked = false;
        }
        Assert.assertEquals(true, worked);

    }

    // Testing ChannelConfiguration
    @Test
    public void ChannelConfiguration1() {
        final ChannelConfiguration cc = ChannelFactory.getInstance();
        Assert.assertEquals(0,
                ((TransportBindingService) cc).getChannelListeners().size());

        class CL extends ChannelListener {

            public CL(final BACnetEID listenerEid) {
                super(listenerEid);
                // TODO Auto-generated constructor stub
            }

            @Override
            public void onIndication(
                    final T_UnitDataIndication tUnitDataIndication,
                    final Object context) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(final String cause) {
                // TODO Auto-generated method stub

            }

        }
        cc.registerChannelListener(new CL(new BACnetEID(1000)));
        Assert.assertEquals(1,
                ((TransportBindingService) cc).getChannelListeners().size());
        cc.registerChannelListener(new CL(new BACnetEID(1001)));
        Assert.assertEquals(2,
                ((TransportBindingService) cc).getChannelListeners().size());

        Assert.assertEquals(((TransportBindingService) cc).getChannelListeners()
                .get(0).intValue(), 1000);
        Assert.assertEquals(((TransportBindingService) cc).getChannelListeners()
                .get(1).intValue(), 1001);

    }

    // Testing ApplicationService
    @Test
    public void testApplicationService() {
        final ASEServices ases = ChannelFactory.getInstance();

        // Mock ASEService (Not ASEService"s")
        final ASEService aseServiceMock = new ASEService() {

            @Override
            public void doCancel(final BACnetEID destination,
                    final BACnetEID source) {
                // TODO Auto-generated method stub

            }

            @Override
            public void doRequest(final T_UnitDataRequest t_unitDataRequest) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setTransportBindingService(
                    final TransportBindingService transportBindingService) {
                // TODO Auto-generated method stub

            }
        };
    }

    // Testing TransportBindingService
    @Test
    public void testOnIndication() {
        final ASEServices ases = ChannelFactory.getInstance();

        final TPDU testTPDU = new TPDU(new BACnetEID(1000), new BACnetEID(1010),
                new byte[] { 0x0e, 2, 3 });
        testTPDU.setInvokeId(new UnsignedInteger8(12));
        ((TransportBindingService) ases).onIndication(testTPDU,
                new InetSocketAddress("http://localhost", 80));
        // No channel listener registered at this point

        final T_UnitDataIndication received;
        // Dummy channel listener
        final ChannelListener cl = new ChannelListener(new BACnetEID(1010)) {

            @Override
            public void onIndication(
                    final T_UnitDataIndication tUnitDataIndication,
                    final Object context) {
                Assert.assertArrayEquals(new byte[] { 0x0e, 2, 3 },
                        tUnitDataIndication.getData().getBody());
                System.out.println(
                        this.getEID().toString() + " received indiciation");
            }

            @Override
            public void onError(final String cause) {
                // TODO Auto-generated method stub
            }
        };

        ((ChannelConfiguration) ases).registerChannelListener(cl);
        ((TransportBindingService) ases).onIndication(testTPDU,
                new InetSocketAddress("http://localhost", 80));

    }

}
