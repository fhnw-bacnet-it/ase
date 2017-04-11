package ch.fhnw.bacnetit.stack.encoding;

public class UnsignedInteger8 extends UnsignedInteger31 {
    private static final long serialVersionUID = 3671427317129449189L;

    private static final int MAX = 0xff - 1;

    public UnsignedInteger8(final int value) {
        super(value);
        if (value > MAX) {
            throw new IllegalArgumentException(
                    "Value cannot be greater than " + MAX);
        }
    }

    public UnsignedInteger8(final _ByteQueue queue) {
        super(queue);
    }
}
