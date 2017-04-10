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
package ch.fhnw.bacnetit.stack.network.transport;

public enum TransportProtocolType {
    WebSocket("WS"), WebSocketSecure("WSS");

    private String name;

    TransportProtocolType(final String name) {
        this.name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public static TransportProtocolType fromString(final String name) {
        final String protocol = name.toLowerCase();
        if (protocol != null) {
            for (final TransportProtocolType tpt : TransportProtocolType
                    .values()) {
                if (protocol.equalsIgnoreCase(tpt.name)) {
                    return tpt;
                }
            }
        }
        throw new IllegalArgumentException(
                "No TransportProtocolType with name " + name + " found");
    }
}
