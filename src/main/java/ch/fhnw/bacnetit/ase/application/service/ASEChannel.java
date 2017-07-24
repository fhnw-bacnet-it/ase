/**
 * 
 */
package ch.fhnw.bacnetit.ase.application.service;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ch.fhnw.bacnetit.ase.application.api.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.application.transaction.TransactionManager;
import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.application.transaction.api.TransportBindingService;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;

/**
 * @author IMVS, FHNW
 *
 */
public class ASEChannel 
implements 
ch.fhnw.bacnetit.ase.application.transaction.api.ASEServices,
ch.fhnw.bacnetit.ase.application.service.ASEService
{
    
    private final List<ASEService> aseServices;
    
    private final TransactionManager transactionManager ;
    private final List<ChannelListener> channelListeners = new ArrayList<ChannelListener>();
    private BACnetEntityListener entityListener = null;
    
    public ASEChannel(){
        aseServices = new LinkedList<ASEService>();
        transactionManager = new TransactionManager();
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

    @Override
    public void onIndication(final TPDU msg, final SocketAddress remoteSocketAddress) {
        T_UnitDataIndication indicationUnit = null;
        indicationUnit = new T_UnitDataIndication(null, msg, msg.getPriority());

        transactionManager.createInboundTransaction(indicationUnit);

        for (final ChannelListener l : this.channelListeners) {
            if (l.getEID().equals(msg.getDestinationEID())) {
                l.onIndication(indicationUnit, remoteSocketAddress);
            }
        }
        
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /* (non-Javadoc)
     * @see ch.fhnw.bacnetit.ase.application.transaction.api.ApplicationService#doRequest(ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest)
     */
    @Override
    public void doRequest(T_UnitDataRequest t_unitDataRequest) {
        

        // Pass outgoing request to the Transaction Manager, receive an invokeId
        // from transaction manager
        t_unitDataRequest.getData().setInvokeId(transactionManager
                .createOutboundTransaction(t_unitDataRequest));
        
        aseServices.get(0).doRequest(t_unitDataRequest);
        
    }

    /* (non-Javadoc)
     * @see ch.fhnw.bacnetit.ase.application.transaction.api.ApplicationService#doCancel(ch.fhnw.bacnetit.ase.encoding.api.BACnetEID, ch.fhnw.bacnetit.ase.encoding.api.BACnetEID)
     */
    @Override
    public void doCancel(BACnetEID destination, BACnetEID source) {
        aseServices.get(0).doCancel(destination, source);
        
    }




    /* (non-Javadoc)
     * @see ch.fhnw.bacnetit.ase.application.service.ASEService#setTransportBindingService(ch.fhnw.bacnetit.ase.application.transaction.api.TransportBindingService)
     */
    @Override
    public void setTransportBindingService(
            TransportBindingService transportBindingService) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see ch.fhnw.bacnetit.ase.application.transaction.api.ChannelConfiguration#addBinding(ch.fhnw.bacnetit.ase.application.service.ASEService)
     */
    @Override
    public void addBinding(ASEService aseService) {
        this.aseServices.add(aseService);
        aseService.setTransportBindingService(this);
        
    }



}
