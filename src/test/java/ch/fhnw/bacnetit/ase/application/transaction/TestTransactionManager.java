/**
 *
 */
package ch.fhnw.bacnetit.ase.application.transaction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger31;
import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger8;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;

/**
 * @author IMVS, FHNW
 *
 */
public class TestTransactionManager {

    /****************************************
     * HELPER METHODS
     ****************************************/

    private TransactionKey getRandomTransactionKey() {

        final TransactionKey tk = new TransactionKey(
                new BACnetEID((int) (Math.random() * 1000)),
                new BACnetEID((int) (Math.random() * 1000)),
                new UnsignedInteger8((int) (Math.random() * 10)), 0);
        return tk;
    }

    private T_UnitDataRequest getRandomTUnitDataRequest(
            final boolean withInvokeId, final boolean dataExpectingReply) {
        final byte[] body = (dataExpectingReply) ? new byte[] { 0x0e, 2, 3 }
                : new byte[] { 1, 2, 3 };

        try {
            final T_UnitDataRequest tudr = new T_UnitDataRequest(
                    new URI("http://google.ch"),
                    new TPDU(new BACnetEID(1234), new BACnetEID(5678), body), 1,
                    new Object());
            if (withInvokeId) {
                tudr.getData().setInvokeId(new UnsignedInteger8(22));
            }
            return tudr;
        } catch (final URISyntaxException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    /****************************************
     * TESTS
     ****************************************/
    @Test
    public void createOutboundTransactionWithInvokeIdThatWasNeverSeenBefore() {
        final TransactionManager tm = new TransactionManager();
        // Create a outbound transaction with a TUnitDataRequest containing a
        // invoke id.
        // The tm has this Source-Dest-Invoke ID never seen before
        final T_UnitDataRequest request = getRandomTUnitDataRequest(true, true);
        final UnsignedInteger8 invokeId = tm.createOutboundTransaction(request);

        // Test invoke id didn't get overwritten
        Assert.assertEquals(request.getData().getInvokeId(), invokeId);

        final Transaction t = tm.findTransaction(
                new TransactionKey(request.getData().getSourceEID(),
                        request.getData().getDestinationEID(),
                        request.getData().getInvokeId(), 1));

        Assert.assertEquals(1, tm.getTransactionsSortedByTime().size());
        Assert.assertEquals(TransactionState.REQUESTED_WAITING, t.getState());

    }

    @Test
    public void createOutboundTransactionWithInvokeIdThatWasNeverSeenBefore2() {
        final TransactionManager tm = new TransactionManager();
        // Create a outbound transaction with a TUnitDataRequest containing a
        // invoke id.
        // The tm has this Source-Dest-Invoke ID never seen before
        final T_UnitDataRequest request = getRandomTUnitDataRequest(true,
                false);
        final UnsignedInteger8 invokeId = tm.createOutboundTransaction(request);

        // Test invoke id didn't get overwritten
        Assert.assertEquals(request.getData().getInvokeId(), invokeId);

        final Transaction t = tm.findTransaction(
                new TransactionKey(request.getData().getSourceEID(),
                        request.getData().getDestinationEID(),
                        request.getData().getInvokeId(), 1));

        Assert.assertEquals(1, tm.getTransactionsSortedByTime().size());
        Assert.assertEquals(TransactionState.REQUESTED_DONE, t.getState());
    }

    @Test
    public void createOutboundTransactionWithoutInvokeIdAndGetAnAnswer()
            throws URISyntaxException {

        // TM1 sends something that expects a reply
        final TransactionManager tm1 = new TransactionManager();
        final T_UnitDataRequest request = getRandomTUnitDataRequest(false,
                true);

        final UnsignedInteger8 invokeId = tm1
                .createOutboundTransaction(request);
        request.getData().setInvokeId(invokeId);
        final Transaction t = tm1.findTransaction(
                new TransactionKey(request.getData().getSourceEID(),
                        request.getData().getDestinationEID(), invokeId,
                        TransactionKey.DIRECTION_OUT));
        Assert.assertNotNull(t);
        Assert.assertEquals(TransactionState.REQUESTED_WAITING, t.getState());

        // TM2 receives the thing from TM1
        final TransactionManager tm2 = new TransactionManager();
        final T_UnitDataIndication indication = new T_UnitDataIndication(
                new URI("http://localhost"), request.getData(),
                new UnsignedInteger31(request.getNetworkPriority()));

        tm2.createInboundTransaction(indication);
        final Transaction tInWrong = tm2.findTransaction(
                new TransactionKey(request.getData().getSourceEID(),
                        request.getData().getDestinationEID(), invokeId,
                        TransactionKey.DIRECTION_OUT));
        Assert.assertNull(tInWrong);
        final Transaction tIn = tm2.findTransaction(
                new TransactionKey(request.getData().getSourceEID(),
                        request.getData().getDestinationEID(), invokeId,
                        TransactionKey.DIRECTION_IN));
        Assert.assertNotNull(tIn);
        Assert.assertEquals(TransactionState.INDICATED_WAITING, tIn.getState());

        final BACnetEID _src = request.getData().getSourceEID();
        final BACnetEID _dest = request.getData().getDestinationEID();
        request.getData().setDestinationEID(_src);
        request.getData().setSourceEID(_dest);

        Assert.assertEquals(invokeId, tm2.createOutboundTransaction(request));

        final Transaction tOut = tm2.findTransaction(
                new TransactionKey(request.getData().getDestinationEID(),
                        request.getData().getSourceEID(), invokeId,
                        TransactionKey.DIRECTION_IN));
        Assert.assertNotNull(tOut);
        Assert.assertEquals(TransactionState.INDICATED_DONE, tOut.getState());

        final Transaction tOutWrong = tm2.findTransaction(
                new TransactionKey(request.getData().getDestinationEID(),
                        request.getData().getSourceEID(), invokeId,
                        TransactionKey.DIRECTION_OUT));
        Assert.assertNull(tOutWrong);

        final T_UnitDataIndication indicationBack = new T_UnitDataIndication(
                new URI("http://google.ch"), request.getData(),
                new UnsignedInteger31(request.getNetworkPriority()));
        tm1.createInboundTransaction(indicationBack);
        final Transaction tBack = tm1.findTransaction(
                new TransactionKey(request.getData().getDestinationEID(),
                        request.getData().getSourceEID(), invokeId,
                        TransactionKey.DIRECTION_OUT));
        Assert.assertNotNull(tBack);

        Assert.assertEquals(TransactionState.REQUESTED_DONE, tBack.getState());

        final Transaction tBackWrong = tm1.findTransaction(
                new TransactionKey(request.getData().getDestinationEID(),
                        request.getData().getSourceEID(), invokeId,
                        TransactionKey.DIRECTION_IN));
        Assert.assertNull(tBackWrong);

    }

    @Test
    public void findNotPresentTransaction() {
        final TransactionManager tm = new TransactionManager();
        final TransactionKey tk = getRandomTransactionKey();
        Assert.assertNull(tm.findTransaction(tk));
    }

    @Test
    public void amountUniqueInvokeId_wrong() {
        final TransactionManager tm = new TransactionManager();
        final Set<UnsignedInteger8> receivedUniqueInvokeIds = new HashSet<UnsignedInteger8>();

        final BACnetEID source = new BACnetEID(1000);
        final BACnetEID destination = new BACnetEID(2000);

        for (int i = 0; i < 300; i++) {
            receivedUniqueInvokeIds
                    .add(tm.getUniqueInvokeId(source, destination));
        }
        Assert.assertNotEquals(300, receivedUniqueInvokeIds.size());

    }

    @Test
    public void amountUniqueInvokeId() {
        final TransactionManager tm = new TransactionManager();
        final Set<UnsignedInteger8> receivedUniqueInvokeIds = new HashSet<UnsignedInteger8>();

        final BACnetEID source = new BACnetEID(1000);
        final BACnetEID destination = new BACnetEID(2000);

        for (int i = 0; i < 255; i++) {
            receivedUniqueInvokeIds
                    .add(tm.getUniqueInvokeId(source, destination));
        }
        Assert.assertEquals(255, receivedUniqueInvokeIds.size());

    }

    @Test
    public void amountUniqueInvokeId_inRange0To255() {
        final TransactionManager tm = new TransactionManager();
        final Set<UnsignedInteger8> receivedUniqueInvokeIds = new HashSet<UnsignedInteger8>();

        final BACnetEID source = new BACnetEID(1000);
        final BACnetEID destination = new BACnetEID(2000);

        for (int i = 0; i < 255; i++) {
            receivedUniqueInvokeIds
                    .add(tm.getUniqueInvokeId(source, destination));
        }

        final BACnetEID source2 = new BACnetEID(1001);
        final BACnetEID destination2 = new BACnetEID(2001);

        for (int i = 0; i < 255; i++) {
            receivedUniqueInvokeIds
                    .add(tm.getUniqueInvokeId(source2, destination2));
        }
        Assert.assertEquals(255, receivedUniqueInvokeIds.size());

    }

    @Test
    public void amountUniqueInvokeId_inRange0To255combined() {
        final TransactionManager tm = new TransactionManager();
        final Set<UnsignedInteger8> receivedUniqueInvokeIds = new HashSet<UnsignedInteger8>();

        final BACnetEID source = new BACnetEID(1000);
        final BACnetEID destination = new BACnetEID(2000);

        for (int i = 0; i < 255; i++) {
            receivedUniqueInvokeIds
                    .add(tm.getUniqueInvokeId(source, destination));
        }

        final Set<UnsignedInteger8> receivedUniqueInvokeIds2 = new HashSet<UnsignedInteger8>();
        final BACnetEID source2 = new BACnetEID(1001);
        final BACnetEID destination2 = new BACnetEID(2001);

        for (int i = 0; i < 255; i++) {
            receivedUniqueInvokeIds2
                    .add(tm.getUniqueInvokeId(source2, destination2));
        }
        Assert.assertEquals(510, receivedUniqueInvokeIds.size()
                + receivedUniqueInvokeIds2.size());

    }
}
