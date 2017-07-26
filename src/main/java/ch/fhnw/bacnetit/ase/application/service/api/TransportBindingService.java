/**
 *
 */
package ch.fhnw.bacnetit.ase.application.service.api;

import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import ch.fhnw.bacnetit.ase.encoding.UnsignedInteger31;
import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;

/**
 * @author IMVS, FHNW
 *
 */
public interface TransportBindingService {
    public void onIndication(final TPDU msg,
            final SocketAddress remoteSocketAddress);
    
    public List<UnsignedInteger31> getChannelListeners();
    
    public void onRemoteAdded(BACnetEID eid, URI uri);
}
