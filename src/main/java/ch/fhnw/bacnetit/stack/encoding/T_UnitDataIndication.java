package ch.fhnw.bacnetit.stack.encoding;

import java.net.URI;

public class T_UnitDataIndication {

    // The URI of the sending entity, if available and valid for the local
    // application layer
    private final URI sourceAddress;

    // The data unit received. The data unit shall be an octet string containing
    // an encoded BACnet Transport PDU.
    private final TPDU data;

    // This parameter, of type BACnetNetworkPriority, specifies the network
    // priority for the data unit received
    private final UnsignedInteger31 networkPriority;

    // This parameter indicates whether (TRUE) or not (FALSE) a reply data unit
    // is expected for the data unit received
    private final boolean dataExpectingReply;

    public T_UnitDataIndication(final URI _sourceAddress, final TPDU _data,
            final UnsignedInteger31 _networkPriority) {
        this.sourceAddress = _sourceAddress;
        this.data = _data;
        this.networkPriority = _networkPriority;
        this.dataExpectingReply = _data.isConfirmedRequest();
    }

    public URI getSourceAddress() {
        return this.sourceAddress;
    }

    public TPDU getData() {
        return this.data;
    }

    public UnsignedInteger31 getNetworkPriority() {
        return this.networkPriority;
    }

    public boolean getDataExpectingReply() {
        return this.dataExpectingReply;
    }

}
