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
