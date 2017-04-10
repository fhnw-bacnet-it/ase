package ch.fhnw.bacnetit.stack.network.directory;

import java.net.URI;
import java.util.List;

import ch.fhnw.bacnetit.stack.encoding.BACnetEID;

public interface DirectoryBinding {
    public List<BACnetEID> findBDS();

    public URI resolve(BACnetEID eid);

    public void delete(BACnetEID eid);

    public void register(BACnetEID eid, URI url, boolean isBDS);

    public void registerObject(String instance, boolean isInstanceObjectName,
            String txtvers, BACnetEID bacnetEid, String oid_oname, int ttl,
            int quality);
}
