
package ch.fhnw.bacnetit.stack.encoding;

abstract public class PrimitiveSerializer extends Serializer {
    private static final long serialVersionUID = 611651273642455709L;

    public static PrimitiveSerializer createPrimitive(final _ByteQueue queue)
            throws Exception {
        // Get the first byte. The 4 high-order bits will tell us what the data
        // type is.
        byte type = queue.peek(0);
        type = (byte) ((type & 0xff) >> 4);
        return createPrimitive(type, queue);
    }

    public static PrimitiveSerializer createPrimitive(final _ByteQueue queue,
            final int contextId, final int typeId) throws Exception {
        final int tagNumber = peekTagNumber(queue);

        // Check if the tag number matches the context id. If they match, then
        // create the context-specific parameter,
        // otherwise return null.
        if (tagNumber != contextId) {
            return null;
        }

        return createPrimitive(typeId, queue);
    }

    private static PrimitiveSerializer createPrimitive(final int typeId,
            final _ByteQueue queue) throws Exception {

        /*
         * if (typeId == Null.TYPE_ID) { return new Null(queue); } if (typeId ==
         * Boolean.TYPE_ID) { return new Boolean(queue); }
         */

        if (typeId == UnsignedInteger31.TYPE_ID) {
            return new UnsignedInteger31(queue);
        }
        /*
         * if (typeId == SignedInteger.TYPE_ID) { return new
         * SignedInteger(queue); } if (typeId == Real.TYPE_ID) { return new
         * Real(queue); } if (typeId == Double.TYPE_ID) { return new
         * Double(queue); } if (typeId == OctetString.TYPE_ID) { return new
         * OctetString(queue); } if (typeId == CharacterString.TYPE_ID) { return
         * new CharacterString(queue); } if (typeId == BitString.TYPE_ID) {
         * return new BitString(queue); } if (typeId == Enumerated.TYPE_ID) {
         * return new Enumerated(queue); } if (typeId == Date.TYPE_ID) { return
         * new Date(queue); } if (typeId == Time.TYPE_ID) { return new
         * Time(queue); } if (typeId == BACnetObjectIdentifier.TYPE_ID) { return
         * new BACnetObjectIdentifier(queue); }
         */
        throw new Exception("ErrorCode.invalidParameterDataType");

        // throw new BACnetErrorException(ErrorClass.property,
        // ErrorCode.invalidParameterDataType);
    }

    /**
     * This field is maintained specifically for boolean types, since their
     * encoding differs depending on whether the type is context specific or
     * not.
     */
    protected boolean contextSpecific;

    @Override
    final public void write(final _ByteQueue queue) {
        writeTag(queue, getTypeId(), false, getLength());
        writeImpl(queue);
    }

    @Override
    final public void write(final _ByteQueue queue, final int contextId) {
        contextSpecific = true;
        writeTag(queue, contextId, true, getLength());
        writeImpl(queue);
    }

    final public void writeEncodable(final _ByteQueue queue,
            final int contextId) {
        writeContextTag(queue, contextId, true);
        write(queue);
        writeContextTag(queue, contextId, false);
    }

    abstract protected void writeImpl(_ByteQueue queue);

    abstract protected long getLength();

    abstract protected byte getTypeId();

    private void writeTag(final _ByteQueue queue, final int tagNumber,
            final boolean classTag, final long length) {
        final int classValue = classTag ? 8 : 0;

        if (length < 0 || length > 0x100000000l) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        final boolean extendedTag = tagNumber > 14;

        if (length < 5) {
            if (extendedTag) {
                queue.push(0xf0 | classValue | length);
                queue.push(tagNumber);
            } else {
                queue.push((tagNumber << 4) | classValue | length);
            }
        } else {
            if (extendedTag) {
                queue.push(0xf5 | classValue);
                queue.push(tagNumber);
            } else {
                queue.push((tagNumber << 4) | classValue | 0x5);
            }

            if (length < 254) {
                queue.push(length);
            } else if (length < 65536) {
                queue.push(254);
                _BACnetUtils.pushShort(queue, length);
            } else {
                queue.push(255);
                _BACnetUtils.pushInt(queue, length);
            }
        }
    }

    protected long readTag(final _ByteQueue queue) {
        final byte b = queue.pop();
        int tagNumber = (b & 0xff) >> 4;
        contextSpecific = (b & 8) != 0;
        long length = (b & 7);

        if (tagNumber == 0xf) {
            // Extended tag.
            tagNumber = queue.popU1B();
        }

        if (length == 5) {
            length = queue.popU1B();
            if (length == 254) {
                length = queue.popU2B();
            } else if (length == 255) {
                length = queue.popU4B();
            }
        }

        return length;
    }
}
