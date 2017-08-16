/**
 *
 */
package ch.fhnw.bacnetit.ase.application.service.api;

import ch.fhnw.bacnetit.ase.application.transaction.api.ChannelListener;
import ch.fhnw.bacnetit.ase.transportbinding.service.api.ASEService;

/**
 * @author IMVS, FHNW
 *
 */
public interface ChannelConfiguration {
    public void setASEService(ASEService aseService);

    public void registerChannelListener(ChannelListener msgListener);

    public void setEntityListener(BACnetEntityListener entityListener);

}
