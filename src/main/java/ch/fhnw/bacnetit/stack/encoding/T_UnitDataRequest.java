package ch.fhnw.bacnetit.stack.encoding;

import java.net.URI;

public class T_UnitDataRequest {
    // The URI, providing the network level address of the destination BACnet
    // device or BACnet device group.
    private final URI destinationAddress;
    // The data unit to be transmitted. The data unit shall be an octet string
    // containing an encoded BACnet Transport PDU.
    private final TPDU data;
    // This parameter, of type BACnetNetworkPriority, specifies the network
    // priority for the data unit to be sent
    private final int networkPriority;
    // The data_expecting_reply parameter indicates whether (TRUE) or not
    // (FALSE) a reply data unit is expected for the data unit being
    // transferred.
    private final boolean dataExpectingReply;
    // TODO
    // The optional parameter 'context' contains context information required
    // for related T-REPORT.indication primitives to be related to application
    // TSMs.
    // The format and content of this parameter is a local matter of the
    // application layer.
    private final Object context;

    public T_UnitDataRequest(final URI _destinationAddress, final TPDU _data,
            final int _networkPriority, final boolean _dataExpectingReply,
            final Object _context) {
        this.destinationAddress = _destinationAddress;
        this.data = _data;
        this.networkPriority = _networkPriority;
        this.dataExpectingReply = _dataExpectingReply;
        this.context = _context;
    }

    public URI getDestinationAddress() {
        return this.destinationAddress;
    }

    public TPDU getData() {
        return this.data;
    }

    public int getNetworkPriority() {
        return this.networkPriority;
    }

    public boolean getDataExpectingReply() {
        return this.dataExpectingReply;
    }

    public Object getContext() {
        return this.context;
    }

}
