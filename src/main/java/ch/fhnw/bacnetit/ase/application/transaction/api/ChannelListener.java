package ch.fhnw.bacnetit.ase.application.transaction.api;

import ch.fhnw.bacnetit.ase.encoding.api.BACnetEID;
import ch.fhnw.bacnetit.ase.encoding.api.T_UnitDataIndication;

public abstract class ChannelListener {

    private final BACnetEID eid;

    public ChannelListener(final BACnetEID listenerEid) {
        this.eid = listenerEid;
    }

    public BACnetEID getEID() {
        return this.eid;
    }

    public abstract void onIndication(T_UnitDataIndication tUnitDataIndication,
            Object context);

    public abstract void onError(String cause);

    public boolean isSource(final BACnetEID source) {
        return source.equals(eid);
    }

}
