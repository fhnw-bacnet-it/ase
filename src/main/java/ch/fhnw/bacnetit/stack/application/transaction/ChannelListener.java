package ch.fhnw.bacnetit.stack.application.transaction;

import java.net.URI;

import ch.fhnw.bacnetit.stack.encoding.BACnetEID;
import ch.fhnw.bacnetit.stack.encoding.T_UnitDataIndication;
import io.netty.channel.ChannelHandlerContext;

/**
 * The listener interface for receiving events emitted in the {@link Channel}.
 * This is the main interface for applications to receive bacnet messages from
 * the requesting bacnet host.
 *
 * @author juerg.luthiger@fhnw.ch
 *
 */
public abstract class ChannelListener {

    protected BACnetEID eid;

    // Register by MessagingService
    public ChannelListener(final BACnetEID listenerEid) {
        this.eid = listenerEid;
    }

    public BACnetEID getEID() {
        return this.eid;
    }

    // public abstract _CharacterString getURIfromNPO();
    public abstract URI getURIfromNPO();

    public abstract void onIndication(T_UnitDataIndication tUnitDataIndication,
            ChannelHandlerContext ctx);

    public abstract void onError(String cause);

    public boolean isSource(final BACnetEID source) {
        return source.equals(eid);
    }

}
