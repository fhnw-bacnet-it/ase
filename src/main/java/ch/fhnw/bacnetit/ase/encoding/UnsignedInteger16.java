package ch.fhnw.bacnetit.ase.encoding;

public class UnsignedInteger16 extends UnsignedInteger31 {
    private static final long serialVersionUID = -4615555609013008931L;

    private static final int MAX = 0xffff;

    public UnsignedInteger16(final int value) {
        super(value);
        if (value > MAX) {
            throw new IllegalArgumentException(
                    "Value cannot be greater than " + MAX);
        }
    }

    public UnsignedInteger16(final _ByteQueue queue) {
        super(queue);
    }
}
