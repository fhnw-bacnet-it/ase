/**
 * 
 */
package ch.fhnw.bacnetit.ase.application.transaction.api;

import java.net.SocketAddress;
import ch.fhnw.bacnetit.ase.encoding.api.TPDU;

/**
 * @author IMVS, FHNW
 *
 */
public interface TransportBindingService {
    public void onIndication(final TPDU msg, final SocketAddress remoteSocketAddress);
}
