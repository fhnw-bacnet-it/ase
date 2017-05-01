
package ch.fhnw.bacnetit.ase.application.transaction;

import ch.fhnw.bacnetit.ase.application.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.network.transport.ConnectionFactory;

public interface ChannelConfiguration {

    public void setEntityListener(BACnetEntityListener entityListener);

    public void registerChannelListener(ChannelListener msgListener);

    public void initializeAndStart(ConnectionFactory connectionFactory);

    public void shutdown();

}
