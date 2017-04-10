package ch.fhnw.bacnetit.stack.network.directory.bindings;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import ch.fhnw.bacnetit.stack.application.configuration.DiscoveryConfig;
import ch.fhnw.bacnetit.stack.encoding.BACnetEID;
import ch.fhnw.bacnetit.stack.network.directory.DirectoryBinding;

public class MDNS implements DirectoryBinding {

    public MDNS(final DiscoveryConfig config) throws UnknownHostException {

    }

    @Override
    public List<BACnetEID> findBDS() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI resolve(final BACnetEID eid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(final BACnetEID eid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void register(final BACnetEID eid, final URI url,
            final boolean isBDS) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerObject(final String instance,
            final boolean isInstanceObjectName, final String txtvers,
            final BACnetEID bacnetEid, final String oid_oname, final int ttl,
            final int quality) {
        // TODO Auto-generated method stub

    }

}
