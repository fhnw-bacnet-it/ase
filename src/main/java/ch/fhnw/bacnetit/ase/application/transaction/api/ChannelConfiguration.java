/**
 * 
 */
package ch.fhnw.bacnetit.ase.application.transaction.api;

import ch.fhnw.bacnetit.ase.application.api.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.application.service.ASEService;
import ch.fhnw.bacnetit.transportbinding.ws.ConnectionFactory;

/**
 * @author IMVS, FHNW
 *
 */
public interface ChannelConfiguration {
    public void addBinding(ASEService aseService);
    public void registerChannelListener(ChannelListener msgListener);
    public void setEntityListener(BACnetEntityListener entityListener);

}
