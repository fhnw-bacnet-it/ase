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

/**
 * Represents a BACnetEID BACnetEID options are "Not Assigned", "Device
 * Identifier", "Device Group Identifier"
 *
 * @author IMVS, FHNW
 *
 */
public class BACnetEID extends Serializer implements Comparable<BACnetEID> {

    private static final long serialVersionUID = 3323595176060683977L;
    private final UnsignedInteger31 identifier;
    private final BACnetEIDOption choice;

    /**
     * @param queue
     *            ByteQueue to initialize a BACnetEID instance
     * @param contextId
     * @throws Exception
     * @throws BACnetException
     */
    public BACnetEID(final _ByteQueue queue, final int contextId,
            final BACnetEIDOption option) throws Exception {
        queue.pop();
        identifier = read(queue, UnsignedInteger31.class, contextId);
        choice = option;
    }

    public BACnetEID(final _ByteQueue queue) throws Exception {
        int c = queue.peek(0);
        c = c >> 4;
        this.choice = BACnetEIDOption.get((byte) c);
        identifier = read(queue, UnsignedInteger31.class);

    }

    /**
     * Initializes a BACnetEID instance with option Device Identifier
     *
     * @param eid
     *            EID as integer
     */
    public BACnetEID(final int eid) {
        this(new UnsignedInteger31(eid), BACnetEIDOption.DEVICE);
    }

    public BACnetEID(final int id, final BACnetEIDOption c) {
        this(new UnsignedInteger31(id), c);
    }

    /**
     * Initializes a BACnetEID instance with option Device Identifier
     *
     * @param eid
     *            EID as UnsignedInteger instance
     */
    public BACnetEID(final UnsignedInteger31 eid) {
        this(eid, BACnetEIDOption.DEVICE);
    }

    public BACnetEID(final UnsignedInteger31 id, final BACnetEIDOption choice) {
        this.identifier = id;
        this.choice = choice;
    }

    public int getIdentifier() {
        return this.identifier.intValue();
    }

    public String getIdentifierAsString() {
        return this.identifier.toString();
    }

    public BACnetEIDOption getChoice() {
        return this.choice;
    }

    public byte[] bytes() {
        final _ByteQueue queue = new _ByteQueue();
        final UnsignedInteger31 ui = this.identifier;
        ui.write(queue, this.choice.getId());
        return queue.peekAll();
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
        final BACnetEID other = (BACnetEID) obj;
        if (choice != other.choice) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((choice == null) ? 0 : choice.hashCode());
        result = prime * result
                + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }

    @Override
    public String toString() {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        byte[] bytes = bytes();
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars).trim();
    }

    @Override
    public void write(final _ByteQueue queue) {
        queue.push(bytes());

    }

    @Override
    public void write(final _ByteQueue queue, final int contextId) {
        queue.push(bytes());

    }

    @Override
    public int compareTo(final BACnetEID o) {
        if (this.getIdentifier() > o.getIdentifier()) {
            return 1;
        } else if (this.getIdentifier() < o.getIdentifier()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * The ENUM represents the 3 BACnetEID Options
     *
     * @author IMVS, FHNW
     *
     */
    public static enum BACnetEIDOption {
        NOTASSIGNED((byte) 0), DEVICE((byte) 1), GROUP((byte) 2);

        private byte id;

        private BACnetEIDOption(final byte id) {
            this.id = id;
        }

        public byte getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }

        public static BACnetEIDOption get(final byte b) {
            for (final BACnetEIDOption choice : values()) {
                if (choice.getId() == b) {
                    return choice;
                }
            }
            return null;
        }
    }

}
