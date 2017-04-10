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

public class _BACnetUtils {
    public static void pushShort(final _ByteQueue queue, final long value) {
        queue.push((byte) (0xff & (value >> 8)));
        queue.push((byte) (0xff & value));
    }

    public static void pushInt(final _ByteQueue queue, final long value) {
        queue.push((byte) (0xff & (value >> 24)));
        queue.push((byte) (0xff & (value >> 16)));
        queue.push((byte) (0xff & (value >> 8)));
        queue.push((byte) (0xff & value));
    }

    public static void pushLong(final _ByteQueue queue, final long value) {
        queue.push((byte) (0xff & (value >> 56)));
        queue.push((byte) (0xff & (value >> 48)));
        queue.push((byte) (0xff & (value >> 40)));
        queue.push((byte) (0xff & (value >> 32)));
        queue.push((byte) (0xff & (value >> 24)));
        queue.push((byte) (0xff & (value >> 16)));
        queue.push((byte) (0xff & (value >> 8)));
        queue.push((byte) (0xff & value));
    }

    public static int popShort(final _ByteQueue queue) {
        return (short) ((toInt(queue.pop()) << 8) | toInt(queue.pop()));
    }

    public static int popInt(final _ByteQueue queue) {
        return (toInt(queue.pop()) << 24) | (toInt(queue.pop()) << 16)
                | (toInt(queue.pop()) << 8) | toInt(queue.pop());
    }

    public static long popLong(final _ByteQueue queue) {
        return (toLong(queue.pop()) << 56) | (toLong(queue.pop()) << 48)
                | (toLong(queue.pop()) << 40) | (toLong(queue.pop()) << 32)
                | (toLong(queue.pop()) << 24) | (toLong(queue.pop()) << 16)
                | (toLong(queue.pop()) << 8) | toLong(queue.pop());
    }

    public static int toInt(final byte b) {
        return b & 0xff;
    }

    public static long toLong(final byte b) {
        return (b & 0xff);
    }

    public static byte[] convertToBytes(final boolean[] bdata) {
        final int byteCount = (bdata.length + 7) / 8;
        final byte[] data = new byte[byteCount];
        for (int i = 0; i < bdata.length; i++) {
            data[i / 8] |= (bdata[i] ? 1 : 0) << (7 - (i % 8));
        }
        return data;
    }

    public static boolean[] convertToBooleans(final byte[] data,
            final int length) {
        final boolean[] bdata = new boolean[length];
        for (int i = 0; i < bdata.length; i++) {
            bdata[i] = ((data[i / 8] >> (7 - (i % 8))) & 0x1) == 1;
        }
        return bdata;
    }

    public static byte[] dottedStringToBytes(final String s)
            throws NumberFormatException {
        final String[] parts = s.split("\\.");
        final byte[] b = new byte[parts.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(parts[i]);
        }
        return b;
    }

    public static String bytesToDottedString(final byte[] b) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(0xff & b[i]);
        }
        return sb.toString();
    }
}
