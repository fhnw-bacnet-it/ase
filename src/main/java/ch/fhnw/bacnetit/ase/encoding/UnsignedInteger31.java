package ch.fhnw.bacnetit.ase.encoding;

public class UnsignedInteger31 extends PrimitiveSerializer {
    private static final long serialVersionUID = -3350034351888356100L;

    public static final byte TYPE_ID = 2;

    private int value;

    public UnsignedInteger31(final int _value) {
        if (_value < 0) {
            throw new IllegalArgumentException(
                    "Value cannot be smaller than zero");
        }
        if (_value > 0x7FFFFFFF) { // max signed int: 2147483647
            throw new IllegalArgumentException(
                    "Value cannot be bigger than 0x7FFFFFFF");
        }
        value = _value;
    }

    public int intValue() {

        return value;

    }

    public long longValue() {

        return value;

    }

    //
    // Reading and writing
    //
    public UnsignedInteger31(final _ByteQueue queue) {
        int length = (int) readTag(queue);
        if (length < 4) {
            while (length > 0) {
                value |= (queue.pop() & 0xff) << (--length * 8);
            }
        }
        // else {
        // final byte[] bytes = new byte[length + 1];
        // queue.pop(bytes, 1, length);
        // bigValue = new BigInteger(bytes);
        // }
    }

    @Override
    public void writeImpl(final _ByteQueue queue) {
        int length = (int) getLength();

        while (length > 0) {
            queue.push(value >> (--length * 8));
        }

    }

    @Override
    public long getLength() {
        // if (bigValue == null) {
        int length;
        if (value < 0x100) {
            length = 1;
        } else if (value < 0x10000) {
            length = 2;
        } else if (value < 0x1000000) {
            length = 3;
        } else {
            length = 4;
        }

        return length;

    }

    @Override
    protected byte getTypeId() {
        return TYPE_ID;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;

        result = PRIME * result + value;
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
        final UnsignedInteger31 other = (UnsignedInteger31) obj;
        return other.intValue() == this.intValue();
    }

    @Override
    public String toString() {

        return Integer.toString(value);

    }
}
