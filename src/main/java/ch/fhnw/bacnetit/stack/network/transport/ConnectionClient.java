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

import java.net.SocketAddress;
import java.net.URI;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public interface ConnectionClient {
    void close();

    SocketAddress getAddress();

    ChannelHandler[] getChannelHandlers();

    void initialize();

    Channel getChannel();

    // String getBACnetEID(); // TODO check if still valid, connection not bound
    // to ONE EID!

    URI getURI();

    void setChannel(Channel channel);
}
