package ch.fhnw.bacnetit.stack.encoding;

import java.util.Arrays;

public class TPDU extends ITBBase {
    private final int REVISION = 2;

    private static final long serialVersionUID = -4011173466131041450L;

    // Revision 0
    private UnsignedInteger16 version = new UnsignedInteger16(REVISION);

    // Priority 1
    private UnsignedInteger31 priority;

    // Source BACnetEID 2
    private BACnetEID sourceEID;

    // Destination BACnetEID 3
    private BACnetEID destinationEID;

    // TODO Original Destination EID

    // Invoke ID 4
    private UnsignedInteger8 invokeId;

    // Forwards 5
    private UnsignedInteger8 forwards;

    // BACnet Security Parameters 6
    private SecurityParameters security;

    private byte[] body;

    // private Service bacnetService = null;

    // Slim constructors
    public TPDU(final BACnetEID sourceEID, final BACnetEID destinationEID,
            final byte[] b) {
        this(new UnsignedInteger31(NetworkPriority.NORMAL), sourceEID,
                destinationEID, null, null, null, b);
    }

    /*
     * // Slim constructors public TPDU(final BACnetEID sourceEID, final
     * BACnetEID destinationEID, final Service b) { this(new
     * UnsignedInteger(NetworkPriority.NORMAL), sourceEID, destinationEID, null,
     * null, null, b); }
     */

    // Main Constructor
    public TPDU(final UnsignedInteger31 priority, final BACnetEID sourceEID,
            final BACnetEID destinationEID, final UnsignedInteger8 invokeId,
            final UnsignedInteger8 forwards, final SecurityParameters sp,
            final byte[] body) {
        // this.bacnetTransportBindingAPDUType = typ;
        this.priority = priority;
        this.sourceEID = sourceEID;
        this.destinationEID = destinationEID;
        this.invokeId = invokeId;
        this.forwards = forwards;
        this.security = sp;
        this.body = body;
    }

    /*
     * // Main Constructor public TPDU(final UnsignedInteger priority, final
     * BACnetEID sourceEID, final BACnetEID destinationEID, final Unsigned8
     * invokeId, final Unsigned8 forwards, final SecurityParameters sp, final
     * Service body) { // this.bacnetTransportBindingAPDUType = typ;
     * this.priority = priority; this.sourceEID = sourceEID; this.destinationEID
     * = destinationEID; this.invokeId = invokeId; this.forwards = forwards;
     * this.security = sp; this.bacnetService = body; final ByteQueue queue =
     * new ByteQueue(); body.write(queue); this.body = queue.popAll(); }
     */

    public TPDU(final byte[] queue) throws Exception {
        this(new _ByteQueue(queue));
    }

    public TPDU(final _ByteQueue queue) throws Exception {
        // Popping two websocket bytes occurs further down in the stack
        // websocket-revision + websocket-version
        queue.pop();
        version = read(queue, UnsignedInteger16.class, 0);
        priority = readOptional(queue, UnsignedInteger31.class, 1);
        sourceEID = read(queue, BACnetEID.class, 2);
        destinationEID = read(queue, BACnetEID.class, 3);
        invokeId = readOptional(queue, UnsignedInteger8.class, 4);
        forwards = readOptional(queue, UnsignedInteger8.class, 5);
        security = readOptional(queue, SecurityParameters.class, 6);
        queue.pop();
        queue.pop();
        final byte[] tmp = queue.popAll();
        body = Arrays.copyOfRange(tmp, 0, tmp.length - 1);

    }

    @Override
    public void write(final _ByteQueue queue) {
        writeContextTag(queue, 0, true);
        write(queue, version, 0);
        writeOptional(queue, priority, 1);
        writeContextTag(queue, 2, true);
        write(queue, sourceEID, 1);
        writeContextTag(queue, 2, false);
        writeContextTag(queue, 3, true);
        write(queue, destinationEID, 1);
        writeContextTag(queue, 3, false);
        writeOptional(queue, invokeId, 4);
        writeOptional(queue, forwards, 5);
        writeOptional(queue, security, 6);
        writeContextTag(queue, 0, false);
        writeContextTag(queue, 1, true);
        queue.push(body);
        writeContextTag(queue, 1, false);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TPDU other = (TPDU) obj;

        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }
        if (sourceEID == null) {
            if (other.sourceEID != null) {
                return false;
            }
        } else if (!sourceEID.equals(other.sourceEID)) {
            return false;
        }
        if (destinationEID == null) {
            if (other.destinationEID != null) {
                return false;
            }
        } else if (!destinationEID.equals(other.destinationEID)) {
            return false;
        }
        if (invokeId == null) {
            if (other.invokeId != null) {
                return false;
            }
        } else if (!invokeId.equals(other.invokeId)) {
            return false;
        }
        if (forwards == null) {
            if (other.forwards != null) {
                return false;
            }
        } else if (!forwards.equals(other.forwards)) {
            return false;
        }
        if (security == null) {
            if (other.security != null) {
                return false;
            }
        } else if (!security.equals(other.security)) {
            return false;
        }

        return true;

    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((version == null) ? 0 : version.hashCode());
        result = PRIME * result
                + ((priority == null) ? 0 : priority.hashCode());
        result = PRIME * result
                + ((sourceEID == null) ? 0 : sourceEID.hashCode());
        result = PRIME * result
                + ((destinationEID == null) ? 0 : destinationEID.hashCode());
        result = PRIME * result
                + ((invokeId == null) ? 0 : invokeId.hashCode());
        result = PRIME * result
                + ((forwards == null) ? 0 : forwards.hashCode());
        result = PRIME * result
                + ((security == null) ? 0 : security.hashCode());
        return result;
    }

    /*
     * Getter and Setter
     */

    public UnsignedInteger31 getPriority() {
        return this.priority;
    }

    public BACnetEID getSourceEID() {
        return this.sourceEID;
    }

    public BACnetEID getDestinationEID() {
        return destinationEID;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setForwards(final UnsignedInteger8 f) {
        this.forwards = f;
    }

    /*
     * public void setBody(final Service s) { final ByteQueue serviceData = new
     * ByteQueue(); s.write(serviceData); this.body = serviceData.peekAll(); }
     */

    public UnsignedInteger31 getRevision() {
        return this.version;
    }

    public void setInvokeId(final UnsignedInteger8 tid) {
        this.invokeId = tid;
    }

    public UnsignedInteger8 getInvokeId() {
        return this.invokeId;
    }

    public UnsignedInteger31 getForwards() {
        return this.forwards;
    }

    /*
     * public Service getService() { return this.bacnetService; }
     */

    /*
     * public TPDUType getType() { return this.bacnetTransportBindingAPDUType; }
     *
     * public void setType(TPDUType type) { this.bacnetTransportBindingAPDUType
     * = type; }
     */

    public void setPriority(final UnsignedInteger31 prio) {
        this.priority = prio;
    }

    public void setSourceEID(final BACnetEID uid) {
        this.sourceEID = uid;
    }

    public void setDestinationEID(final BACnetEID uid) {
        this.destinationEID = uid;
    }

    public void setBACnetSecurityParameter(final SecurityParameters security) {
        this.security = security;
    }

    public void setBody(final byte[] b) {
        this.body = b;
    }

    // If the body contains a CONFIRMEDREQUEST the method returns true otherwise
    // false
    public boolean isConfirmedRequest() {
        final byte type = this.body[0];
        return ((byte) (type & 0x0f)) == ((byte) 0xe)
                && ((byte) ((type & 0xff) >> 4)) == (byte) 0;

    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + " = ";
        s += "Revision " + this.version + "; ";
        s += "Priority " + this.priority + "; ";
        s += "SourceEID " + this.sourceEID.getIdentifierAsString() + "; ";
        s += "DestinationEID " + this.destinationEID.getIdentifierAsString()
                + "; ";
        s += "InvokeID " + this.invokeId + "; ";
        s += "Forwards " + this.forwards;
        return s;
    }
}
