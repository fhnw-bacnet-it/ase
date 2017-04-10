package ch.fhnw.bacnetit.stack.application;

import java.net.URI;

import ch.fhnw.bacnetit.stack.encoding.BACnetEID;

public interface BACnetEntityListener {

    public void onRemoteAdded(BACnetEID eid, URI uri);

    public void onRemoteRemove(BACnetEID eid);

    public void onLocalRequested(BACnetEID eid);
}
