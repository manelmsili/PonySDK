/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.core.terminal.request;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONValue;
import com.ponysdk.core.terminal.PonySDK;
import com.ponysdk.core.terminal.model.ReaderBuffer;

import elemental.client.Browser;
import elemental.events.MessageEvent;
import elemental.html.Uint8Array;

public class ParentFrameRequest implements RequestBuilder {

    private static final Logger log = Logger.getLogger(ParentFrameRequest.class.getName());

    private final RequestCallback callback;

    public ParentFrameRequest(final String frameID, final RequestCallback callback) {
        this.callback = callback;

        PonySDK.get().setContextId(getContextIDFromParent());

        Browser.getWindow().setOnmessage(event -> {
            log.severe("ParentFrameRequest : " + event);
            final Uint8Array buffer = (Uint8Array) ((MessageEvent) event).getData();
            final ReaderBuffer readerBuffer = new ReaderBuffer();
            readerBuffer.init(buffer);
            onDataReceived(readerBuffer);
        });

        setReadyFrame(frameID);
    }

    /**
     * To Main terminal
     */
    public static native void setReadyFrame(final String frameID) /*-{
                                                                  $wnd.parent.pony.setReadyFrame(frameID);
                                                                  }-*/;

    public static native Integer getContextIDFromParent() /*-{
                                                          return $wnd.parent.pony.contextId;
                                                          }-*/;

    /**
     * To Main terminal
     */
    @Override
    public void send(final JSONValue value) {
        sendToParent(value.toString());
    }

    /**
     * To Main terminal
     */
    public static native void sendToParent(final String data) /*-{
                                                              $wnd.parent.pony.sendDataToServerFromWindow(data);
                                                              }-*/;

    /**
     * From Main terminal to the matching window terminal
     */
    public void onDataReceived(final ReaderBuffer buffer) {
        if (log.isLoggable(Level.FINE)) log.fine("Data received from main terminal " + buffer.toString());
        callback.onDataReceived(buffer);
    }

}
