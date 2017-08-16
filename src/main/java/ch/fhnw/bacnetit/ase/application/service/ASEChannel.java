/**
 *
 */
package ch.fhnw.bacnetit.ase.application.service;

import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ch.fhnw.bacnetit.ase.application.service.api.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.application.service.api.ChannelFactory;
import ch.fhnw.bacnetit.ase.application.transaction.TransactionManager;
import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger31;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;
import ch.fhnw.bacnetit.ase.transportbinding.service.api.ASEService;

/**
 * @author IMVS, FHNW
 *
 */
public class ASEChannel
        implements ch.fhnw.bacnetit.ase.application.service.api.ASEServices {

    // Interface to transport binding
    private final List<ASEService> aseServices;
    private final TransactionManager transactionManager;
    private final List<ChannelListener> channelListeners;
    private BACnetEntityListener entityListener = null;

    public ASEChannel(Object o) throws Exception {
        if (!(o instanceof ChannelFactory)){
            throw new Exception("Use ChannelFactory to get an instance of ASEChannel");
        }
        channelListeners = new ArrayList<ChannelListener>();
        aseServices = new LinkedList<ASEService>();
        transactionManager = new TransactionManager();
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /***********************************************************************
     * Implementation interface TransportBindingService
     ***********************************************************************/

    @Override
    public void onIndication(final TPDU msg,
            final SocketAddress remoteSocketAddress) {
        T_UnitDataIndication indicationUnit = null;
        indicationUnit = new T_UnitDataIndication(null, msg, msg.getPriority());

        transactionManager.createInboundTransaction(indicationUnit);
        
        if(this.channelListeners.size() == 0){
            System.err.println("No channel listener is registered");
            return;
        }

        for (final ChannelListener l : this.channelListeners) {
            if (l.getEID().equals(msg.getDestinationEID())) {
                l.onIndication(indicationUnit, remoteSocketAddress);
            }
        }

    }
    
    @Override
    public List<UnsignedInteger31> getChannelListeners(){
        final List<UnsignedInteger31> bacneteids = new LinkedList<UnsignedInteger31>();
        
        for (final ChannelListener cl : this.channelListeners) {
            bacneteids.add(new UnsignedInteger31(cl.getEID().getIdentifier()));
        }
        return bacneteids;
        
        
    }
    
    @Override
    public void onRemoteAdded(BACnetEID eid, URI uri) {
        this.entityListener.onRemoteAdded(eid, uri);
        
    }

    /***********************************************************************
     * Implementation interface ChannelConfiguration
     ***********************************************************************/

    @Override
    public void setASEService(final ASEService aseService) {
        // Are several aseService instances possible?
        this.aseServices.add(aseService);
        aseService.setTransportBindingService(this);

    }
    
    // Register a device which use this instance as messaging
    @Override
    public void registerChannelListener(final ChannelListener msgListener) {
        if (msgListener != null) {
            this.channelListeners.add(msgListener);
        }
    }

    @Override
    public void setEntityListener(final BACnetEntityListener _entityListener) {
        if (this.entityListener != null) {
            // LOG.error("EntityListener is already set");
            return;
        }
        this.entityListener = _entityListener;
    }

    /***********************************************************************
     * Implementation interface ApplicationService
     ***********************************************************************/
    @Override
    public void doRequest(final T_UnitDataRequest t_unitDataRequest) {
        // Pass outgoing request to the Transaction Manager, receive an invokeId
        // from transaction manager
        t_unitDataRequest.getData().setInvokeId(transactionManager
                .createOutboundTransaction(t_unitDataRequest));
        aseServices.get(0).doRequest(t_unitDataRequest);

    }

    @Override
    public void doCancel(final BACnetEID destination, final BACnetEID source) {
        aseServices.get(0).doCancel(destination, source);
    }

}
