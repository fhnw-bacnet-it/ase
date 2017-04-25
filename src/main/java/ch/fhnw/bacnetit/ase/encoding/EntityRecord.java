package ch.fhnw.bacnetit.ase.encoding;

public class EntityRecord extends ITBBase {

    /**
     *
     */
    private static final long serialVersionUID = -4832973466131041450L;

    private final BACnetEID identifier;
    private final NetworkAddress networkaddress;
    private final UnsignedInteger16 priority;
    private final UnsignedInteger31 timeToLive;
    private final DirectoryRecordQuality quality;

    // Constructor
    public EntityRecord(final BACnetEID identifier,
            final NetworkAddress networkaddress,
            final UnsignedInteger16 priority,
            final UnsignedInteger31 timeToLive,
            final DirectoryRecordQuality quality) {
        this.identifier = identifier;
        this.networkaddress = networkaddress;
        this.priority = priority;
        this.timeToLive = timeToLive;
        this.quality = quality;
    }

    @Override
    public void write(final _ByteQueue queue) {
        writeContextTag(queue, 0, true);
        write(queue, identifier, 0);
        writeContextTag(queue, 0, false);
        write(queue, networkaddress, 1);
        write(queue, priority, 2);
        write(queue, timeToLive, 3);
        write(queue, quality, 4);

    }

    // public static final byte TYPE_ID = 2;
    public EntityRecord(final _ByteQueue queue) throws Exception {
        identifier = read(queue, BACnetEID.class, 0);
        networkaddress = read(queue, NetworkAddress.class, 1);
        priority = read(queue, UnsignedInteger16.class, 2);
        timeToLive = read(queue, UnsignedInteger31.class, 3);
        quality = read(queue, DirectoryRecordQuality.class, 4);
    }

    @Override
    public String toString() {
        String s = "DeviceRecord\n";
        s += "-------------\n";
        s += "-------------\n";
        s += "identifier: " + this.identifier.getIdentifierAsString() + "\n";
        s += "networkaddress: " + this.networkaddress + "\n";
        s += "-------------\n";
        s += "priority: " + this.priority + "\n";
        s += "timeToLive: " + this.timeToLive + "\n";
        s += "quality: " + this.quality + "\n";
        s += "-------------\n";
        s += "-------------\n";

        return s;

    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((identifier == null) ? 0 : identifier.hashCode());
        result = PRIME * result
                + ((networkaddress == null) ? 0 : networkaddress.hashCode());
        result = PRIME * result
                + ((priority == null) ? 0 : priority.hashCode());
        result = PRIME * result
                + ((timeToLive == null) ? 0 : timeToLive.hashCode());
        result = PRIME * result + ((quality == null) ? 0 : quality.hashCode());
        return result;
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
        final EntityRecord other = (EntityRecord) obj;

        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }

        if (networkaddress == null) {
            if (other.networkaddress != null) {
                return false;
            }
        } else if (!networkaddress.equals(other.networkaddress)) {
            return false;
        }

        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }

        if (timeToLive == null) {
            if (other.timeToLive != null) {
                return false;
            }
        } else if (!timeToLive.equals(other.timeToLive)) {
            return false;
        }

        if (quality == null) {
            if (other.quality != null) {
                return false;
            }
        } else if (!quality.equals(other.quality)) {
            return false;
        }

        return true;

    }

}
