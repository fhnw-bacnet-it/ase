package ch.fhnw.bacnetit.stack.encoding;

import java.net.InetSocketAddress;

public class _Address extends ITBBase {
    public static final int LOCAL_NETWORK = 0;

    public static final _Address GLOBAL = new _Address(
            new UnsignedInteger16(0xFFFF), null);

    private static final long serialVersionUID = -3376358193474831753L;

    private final UnsignedInteger16 networkNumber;

    private final _OctetString macAddress;

    public _Address(final int networkNumber, final byte[] macAddress) {
        this(new UnsignedInteger16(networkNumber),
                new _OctetString(macAddress));
    }

    public _Address(final int networkNumber, final String dottedString) {
        this(new UnsignedInteger16(networkNumber),
                new _OctetString(dottedString));
    }

    public _Address(final _OctetString macAddress) {
        this(LOCAL_NETWORK, macAddress);
    }

    public _Address(final int networkNumber, final _OctetString macAddress) {
        this(new UnsignedInteger16(networkNumber), macAddress);
    }

    public _Address(final UnsignedInteger16 networkNumber,
            final _OctetString macAddress) {
        this.networkNumber = networkNumber;
        this.macAddress = macAddress;
    }

    /**
     * Convenience constructor for MS/TP addresses local to this network.
     *
     * @param station
     *            the station id
     */
    public _Address(final byte station) {
        this(LOCAL_NETWORK, station);
    }

    /**
     * Convenience constructor for MS/TP addresses remote to this network.
     *
     * @param network
     * @param station
     */
    public _Address(final int networkNumber, final byte station) {
        this.networkNumber = new UnsignedInteger16(networkNumber);
        macAddress = new _OctetString(new byte[] { station });
    }

    /**
     * Convenience constructor for IP addresses local to this network.
     *
     * @param ipAddress
     * @param port
     */
    public _Address(final byte[] ipAddress, final int port) {
        this(LOCAL_NETWORK, ipAddress, port);
    }

    /**
     * Convenience constructor for IP addresses remote to this network.
     *
     * @param network
     * @param ipAddress
     * @param port
     */
    public _Address(final int networkNumber, final byte[] ipAddress,
            final int port) {
        this.networkNumber = new UnsignedInteger16(networkNumber);

        final byte[] ipMacAddress = new byte[ipAddress.length + 2];
        System.arraycopy(ipAddress, 0, ipMacAddress, 0, ipAddress.length);
        ipMacAddress[ipAddress.length] = (byte) (port >> 8);
        ipMacAddress[ipAddress.length + 1] = (byte) port;
        macAddress = new _OctetString(ipMacAddress);
    }

    public _Address(final String host, final int port) {
        this(LOCAL_NETWORK, host, port);
    }

    public _Address(final int networkNumber, final String host,
            final int port) {
        // this(networkNumber, InetAddrCache.get(host, port));
        this(networkNumber, new InetSocketAddress(host, port));
    }

    public _Address(final InetSocketAddress addr) {
        this(LOCAL_NETWORK, addr.getAddress().getAddress(), addr.getPort());
    }

    public _Address(final int networkNumber, final InetSocketAddress addr) {
        this(networkNumber, addr.getAddress().getAddress(), addr.getPort());
    }

    @Override
    public void write(final _ByteQueue queue) {
        write(queue, networkNumber);
        write(queue, macAddress);
    }

    public _Address(final _ByteQueue queue) throws Exception {
        networkNumber = read(queue, UnsignedInteger16.class);
        macAddress = read(queue, _OctetString.class);
    }

    public _OctetString getMacAddress() {
        return macAddress;
    }

    public UnsignedInteger31 getNetworkNumber() {
        return networkNumber;
    }

    public boolean isGlobal() {
        return networkNumber.intValue() == 0xFFFF;
    }

    //
    //
    // General convenience
    //
    public String getDescription() {
        final StringBuilder sb = new StringBuilder();
        sb.append(macAddress.getDescription());
        if (networkNumber.intValue() != 0) {
            sb.append('(').append(networkNumber).append(')');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Address [networkNumber=" + networkNumber + ", macAddress="
                + macAddress + "]";
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = PRIME * result
                + ((networkNumber == null) ? 0 : networkNumber.hashCode());
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
        final _Address other = (_Address) obj;
        if (macAddress == null) {
            if (other.macAddress != null) {
                return false;
            }
        } else if (!macAddress.equals(other.macAddress)) {
            return false;
        }
        if (networkNumber == null) {
            if (other.networkNumber != null) {
                return false;
            }
        } else if (!networkNumber.equals(other.networkNumber)) {
            return false;
        }
        return true;
    }
}
