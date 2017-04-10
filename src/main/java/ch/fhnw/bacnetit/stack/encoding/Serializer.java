
package ch.fhnw.bacnetit.stack.encoding;

import java.io.Serializable;

abstract public class Serializer implements Serializable {
    private static final long serialVersionUID = -4378016931626697698L;

    abstract public void write(_ByteQueue queue);

    abstract public void write(_ByteQueue queue, int contextId);

    @Override
    public String toString() {
        return "Encodable(" + getClass().getName() + ")";
    }

    protected static void popTagData(final _ByteQueue queue,
            final _TagData tagData) {
        peekTagData(queue, tagData);
        queue.pop(tagData.tagLength);
    }

    protected static void peekTagData(final _ByteQueue queue,
            final _TagData tagData) {
        int peekIndex = 0;
        final byte b = queue.peek(peekIndex++);
        tagData.tagNumber = (b & 0xff) >> 4;
        tagData.contextSpecific = (b & 8) != 0;
        tagData.length = (b & 7);

        if (tagData.tagNumber == 0xf) {
            // Extended tag.
            tagData.tagNumber = _BACnetUtils.toInt(queue.peek(peekIndex++));
        }

        if (tagData.length == 5) {
            tagData.length = _BACnetUtils.toInt(queue.peek(peekIndex++));
            if (tagData.length == 254) {
                tagData.length = (_BACnetUtils
                        .toInt(queue.peek(peekIndex++)) << 8)
                        | _BACnetUtils.toInt(queue.peek(peekIndex++));
            } else if (tagData.length == 255) {
                tagData.length = (_BACnetUtils
                        .toLong(queue.peek(peekIndex++)) << 24)
                        | (_BACnetUtils.toLong(queue.peek(peekIndex++)) << 16)
                        | (_BACnetUtils.toLong(queue.peek(peekIndex++)) << 8)
                        | _BACnetUtils.toLong(queue.peek(peekIndex++));
            }
        }

        tagData.tagLength = peekIndex;
    }

    protected static int peekTagNumber(final _ByteQueue queue) {
        if (queue.size() == 0) {
            return -1;
        }

        // Take a peek at the tag number.
        int tagNumber = (queue.peek(0) & 0xff) >> 4;
        if (tagNumber == 15) {
            tagNumber = queue.peek(1) & 0xff;
        }
        return tagNumber;
    }

    //
    // Write context tags for base types.
    public void writeContextTag(final _ByteQueue queue, final int contextId,
            final boolean start) {
        if (contextId <= 14) {
            queue.push((contextId << 4) | (start ? 0xe : 0xf));
        } else {
            queue.push(start ? 0xfe : 0xff);
            queue.push(contextId);
        }
    }

    //
    // Read start tags.
    protected static int readStart(final _ByteQueue queue) {
        if (queue.size() == 0) {
            return -1;
        }

        final int b = queue.peek(0) & 0xff;
        if ((b & 0xf) != 0xe) {
            return -1;
        }
        if ((b & 0xf0) == 0xf0) {
            return queue.peek(1);
        }
        return b >> 4;
    }

    protected static int popStart(final _ByteQueue queue) {
        final int contextId = readStart(queue);
        if (contextId != -1) {
            queue.pop();
            if (contextId > 14) {
                queue.pop();
            }
        }
        return contextId;
    }

    protected static void popStart(final _ByteQueue queue,
            final int contextId) throws Exception {
        if (popStart(queue) != contextId) {
            throw new Exception("ErrorClass.property,ErrorCode.missingRequiredParameter");
            // throw new BACnetErrorException(ErrorClass.property,
            // ErrorCode.missingRequiredParameter);
        }
    }

    //
    // Read end tags.
    protected static int readEnd(final _ByteQueue queue) {
        if (queue.size() == 0) {
            return -1;
        }
        final int b = queue.peek(0) & 0xff;
        if ((b & 0xf) != 0xf) {
            return -1;
        }
        if ((b & 0xf0) == 0xf0) {
            return queue.peek(1);
        }
        return b >> 4;
    }

    protected static void popEnd(final _ByteQueue queue, final int contextId) {
        if (readEnd(queue) != contextId) {
            /*
             * throw new BACnetErrorException(ErrorClass.property,
             * ErrorCode.missingRequiredParameter);
             */
        }
        queue.pop();
        if (contextId > 14) {
            queue.pop();
        }
    }

    private static boolean matchContextId(final _ByteQueue queue,
            final int contextId) {
        return peekTagNumber(queue) == contextId;
    }

    protected static boolean matchStartTag(final _ByteQueue queue,
            final int contextId) {
        return matchContextId(queue, contextId) && (queue.peek(0) & 0xf) == 0xe;
    }

    protected static boolean matchEndTag(final _ByteQueue queue,
            final int contextId) {
        return matchContextId(queue, contextId) && (queue.peek(0) & 0xf) == 0xf;
    }

    protected static boolean matchNonEndTag(final _ByteQueue queue,
            final int contextId) {
        return matchContextId(queue, contextId) && (queue.peek(0) & 0xf) != 0xf;
    }

    //
    // Basic read and write. Pretty trivial.
    protected static void write(final _ByteQueue queue, final Serializer type) {
        type.write(queue);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Serializer> T read(final _ByteQueue queue,
            final Class<T> clazz) throws Exception {
        if (clazz == PrimitiveSerializer.class) {
            return (T) PrimitiveSerializer.createPrimitive(queue);
        }

        try {
            return clazz.getConstructor(new Class[] { _ByteQueue.class })
                    .newInstance(new Object[] { queue });
        } catch (final Exception e) {
            throw new Exception(e);
        }
       
    }

    //
    // Read and write with context id.
    protected static <T extends Serializer> T read(final _ByteQueue queue,
            final Class<T> clazz, final int contextId) throws Exception {
        if (!matchNonEndTag(queue, contextId)) {
            // throw new BACnetErrorException(ErrorClass.property,
            // ErrorCode.missingRequiredParameter);
        }

        if (PrimitiveSerializer.class.isAssignableFrom(clazz)) {
            return read(queue, clazz);
        }
        return readWrapped(queue, clazz, contextId);
    }

    protected static void write(final _ByteQueue queue, final Serializer type,
            final int contextId) {
        type.write(queue, contextId);
    }

    //
    // Optional read and write.
    protected static void writeOptional(final _ByteQueue queue,
            final Serializer type) {
        if (type == null) {
            return;
        }
        write(queue, type);
    }

    protected static void writeOptional(final _ByteQueue queue,
            final Serializer type, final int contextId) {
        if (type == null) {
            return;
        }
        write(queue, type, contextId);
    }

    protected static <T extends Serializer> T readOptional(
            final _ByteQueue queue, final Class<T> clazz, final int contextId)
            throws Exception {
        if (!matchNonEndTag(queue, contextId)) {
            return null;
        }
        return read(queue, clazz, contextId);
    }

   

    // Read and write encodable
    protected static void writeEncodable(final _ByteQueue queue,
            final Serializer type, final int contextId) {
        if (PrimitiveSerializer.class.isAssignableFrom(type.getClass())) {
            ((PrimitiveSerializer) type).writeEncodable(queue, contextId);
        } else {
            type.write(queue, contextId);
        }
    }
    

    private static <T extends Serializer> T readWrapped(final _ByteQueue queue,
            final Class<T> clazz, final int contextId) throws Exception {
        popStart(queue, contextId);
        final T result = read(queue, clazz);
        popEnd(queue, contextId);
        return result;
    }
}
