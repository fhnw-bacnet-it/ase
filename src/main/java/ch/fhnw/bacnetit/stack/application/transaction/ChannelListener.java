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
package ch.fhnw.bacnetit.stack.application.transaction;

import java.net.URI;

import ch.fhnw.bacnetit.stack.encoding.BACnetEID;
import ch.fhnw.bacnetit.stack.encoding.T_UnitDataIndication;
import io.netty.channel.ChannelHandlerContext;

/**
 * The listener interface for receiving events emitted in the {@link Channel}.
 * This is the main interface for applications to receive bacnet messages from
 * the requesting bacnet host.
 *
 * @author juerg.luthiger@fhnw.ch
 *
 */
public abstract class ChannelListener {

    protected BACnetEID eid;

    // Register by MessagingService
    public ChannelListener(final BACnetEID listenerEid) {
        this.eid = listenerEid;
    }

    public BACnetEID getEID() {
        return this.eid;
    }

    // public abstract _CharacterString getURIfromNPO();
    public abstract URI getURIfromNPO();

    public abstract void onIndication(T_UnitDataIndication tUnitDataIndication,
            ChannelHandlerContext ctx);

    public abstract void onError(String cause);

    public boolean isSource(final BACnetEID source) {
        return source.equals(eid);
    }

}
