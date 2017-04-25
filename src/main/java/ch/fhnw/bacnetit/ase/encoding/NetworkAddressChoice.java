package ch.fhnw.bacnetit.ase.encoding;

public enum NetworkAddressChoice {

    NULL((byte) 0), URL((byte) 1), BACNETADDRESS((byte) 2);

    private byte id;

    private NetworkAddressChoice(final byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    static NetworkAddressChoice getNetworkAddressChoice(final byte b) {

        switch (b) {
        case 0:
            return NetworkAddressChoice.NULL;
        case 1:
            return NetworkAddressChoice.URL;
        case 2:
            return NetworkAddressChoice.BACNETADDRESS;
        default:
            return null;

        }

    }
}
