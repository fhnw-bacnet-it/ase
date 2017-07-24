/**
 * 
 */
package ch.fhnw.bacnetit.ase.application.service;

import ch.fhnw.bacnetit.ase.application.transaction.api.TransportBindingService;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataRequest;

public interface ASEService {
    public void doCancel(BACnetEID destination, BACnetEID source);
    public void doRequest(T_UnitDataRequest t_unitDataRequest);
    public void setTransportBindingService(TransportBindingService transportBindingService);

}
