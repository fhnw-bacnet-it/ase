/*******************************************************************************
 * Copyright (C) 2016 The Java BACnetITB Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.fhnw.bacnetit.stack.encoding;

import java.io.UnsupportedEncodingException;



public class _CharacterString extends PrimitiveSerializer {
    private static final long serialVersionUID = -3146333907363025078L;

    public static final byte TYPE_ID = 7;

    public interface Encodings {
        byte ANSI_X3_4 = 0;

        byte IBM_MS_DBCS = 1;

        byte JIS_C_6226 = 2;

        byte ISO_10646_UCS_4 = 3;

        byte ISO_10646_UCS_2 = 4;

        byte ISO_8859_1 = 5;
    }

    private final byte encoding;

    private final String value;

    public _CharacterString(final String value) {
        encoding = Encodings.ANSI_X3_4;
        this.value = value;
    }

    public _CharacterString(final byte encoding, final String value) throws Exception {
        try {
            validateEncoding();
        } catch(Exception e){
            throw new Exception(e);
        }
//        catch (final BACnetErrorException e) {
//            // This is an API constructor, so it doesn't need to throw checked
//            // exceptions. Convert to runtime.
//            throw new BACnetRuntimeException(e);
//        }
        this.encoding = encoding;
        this.value = value;
    }

    public byte getEncoding() {
        return encoding;
    }

    public String getValue() {
        return value;
    }

    //
    // Reading and writing
    //
    public _CharacterString(final _ByteQueue queue) throws Exception {
        final int length = (int) readTag(queue);

        encoding = queue.pop();
        validateEncoding();

        final byte[] bytes = new byte[length - 1];
        queue.pop(bytes);

        value = decode(encoding, bytes);
    }

    @Override
    public void writeImpl(final _ByteQueue queue) {
        queue.push(encoding);
        queue.push(encode(encoding, value));
    }

    @Override
    protected long getLength() {
        return encode(encoding, value).length + 1;
    }

    @Override
    protected byte getTypeId() {
        return TYPE_ID;
    }

    private static byte[] encode(final byte encoding, final String value) {
        try {
            switch (encoding) {
            case Encodings.ANSI_X3_4:
                return value.getBytes("UTF-8");
            case Encodings.ISO_10646_UCS_2:
                return value.getBytes("UTF-16");
            case Encodings.ISO_8859_1:
                return value.getBytes("ISO-8859-1");
            }
        } catch (final UnsupportedEncodingException e) {
            // Should never happen, so convert to a runtime exception.
            throw new RuntimeException(e);
        }
        return null;
    }

    private static String decode(final byte encoding, final byte[] bytes) {
        try {
            switch (encoding) {
            case Encodings.ANSI_X3_4:
                return new String(bytes, "UTF-8");
            case Encodings.ISO_10646_UCS_2:
                return new String(bytes, "UTF-16");
            case Encodings.ISO_8859_1:
                return new String(bytes, "ISO-8859-1");
            }
        } catch (final UnsupportedEncodingException e) {
            // Should never happen, so convert to a runtime exception.
            throw new RuntimeException(e);
        }
        return null;
    }

    private void validateEncoding() throws Exception {
        if (encoding != Encodings.ANSI_X3_4
                && encoding != Encodings.ISO_10646_UCS_2
                && encoding != Encodings.ISO_8859_1) {
            throw new Exception("ErrorClass.property ErrorCode.characterSetNotSupported Byte.toString(encoding)");
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((value == null) ? 0 : value.hashCode());
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
        final _CharacterString other = (_CharacterString) obj;
        if (encoding != other.encoding) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value;
    }
}
