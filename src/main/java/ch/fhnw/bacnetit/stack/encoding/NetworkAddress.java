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

public class NetworkAddress extends ITBBase {

    private _CharacterString url;
    private _Address bacnetaddress;
    private NetworkAddressChoice choice;

    private static final long serialVersionUID = -337635817234441753L;

    public NetworkAddress(final _CharacterString url,
            final NetworkAddressChoice c) {
        this.choice = c;
        this.url = url;
    }

    public NetworkAddress(final _Address bacnetaddress,
            final NetworkAddressChoice c) {
        this.choice = c;
        this.bacnetaddress = bacnetaddress;
    }

    public NetworkAddress(final _ByteQueue queue) throws Exception {
        if ((byte) 1 == (byte) (queue.peek(0) >> 4)) {
            url = read(queue, _CharacterString.class, 1);
            choice = NetworkAddressChoice.URL;
        } else if ((byte) 2 == (byte) (queue.peek(0) >> 4)) {
            bacnetaddress = read(queue, _Address.class, 2);
            choice = NetworkAddressChoice.BACNETADDRESS;

        }
    }

    public _CharacterString getUrl() {
        return this.url;
    }

    public NetworkAddressChoice getChoice() {
        return this.choice;
    }

    @Override
    public String toString() {
        String s = "NetworkAddres\n";
        s += "Choice: " + this.choice + "\n";
        s += "BacnetAddress: " + this.bacnetaddress + "\n";
        s += "URL: " + this.url;
        return s;
    }

    @Override
    public void write(final _ByteQueue queue) {
        queue.push(bytes());
    }

    public byte[] bytes() {
        final _ByteQueue queue = new _ByteQueue();

        final _CharacterString cs = this.url;
        cs.write(queue, this.choice.getId());

        return queue.peekAll();

    }

    public NetworkAddress(final byte[] stream) throws Exception {
        final _ByteQueue q = new _ByteQueue(stream);
        this.choice = NetworkAddressChoice.getNetworkAddressChoice(q.pop());
        this.url = new _CharacterString(q);

    }

}
