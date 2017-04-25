package ch.fhnw.bacnetit.ase.application;

import java.net.URI;

import ch.fhnw.bacnetit.ase.encoding.BACnetEID;

public interface BACnetEntityListener {

    public void onRemoteAdded(BACnetEID eid, URI uri);

    public void onRemoteRemove(BACnetEID eid);

    public void onLocalRequested(BACnetEID eid);
}
