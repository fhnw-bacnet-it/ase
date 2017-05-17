
package ch.fhnw.bacnetit.ase.application.transaction.api;

import java.util.List;

import ch.fhnw.bacnetit.ase.application.api.BACnetEntityListener;
import ch.fhnw.bacnetit.ase.network.transport.api.ConnectionFactory;

public interface ChannelConfiguration {

    public void setEntityListener(BACnetEntityListener entityListener);

    public void registerChannelListener(ChannelListener msgListener);

    public void initializeAndStart(ConnectionFactory connectionFactory);

    public List<ChannelListener> getChannelListeners();

    public void shutdown();

}
