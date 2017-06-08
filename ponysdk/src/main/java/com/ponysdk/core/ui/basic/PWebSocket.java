/*
 * Copyright (c) 2017 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
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

package com.ponysdk.core.ui.basic;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.WriteCallback;

import com.ponysdk.core.model.ServerToClientModel;
import com.ponysdk.core.model.WidgetType;
import com.ponysdk.core.ui.WebSocketServerChat;
import com.ponysdk.core.writer.ModelWriter;

/**
 * @author mmsili
 *
 */
public class PWebSocket extends PObject {

    private WebSocketServerChat webSocketServerChat;

    public WebSocketServerChat getWebSocketServerChat() {
        return webSocketServerChat;
    }

    public void setWebSocketServerChat(final WebSocketServerChat webSocketServerChat) {
        this.webSocketServerChat = webSocketServerChat;
    }

    public ModelWriter writer;
    private final String url;

    public PWebSocket(final String url) {
        this.url = url;
        ManagerOfPWebSocket.get().setUrlPWSMap(this);
    }

    public String getURL() {
        return this.url;
    }

    public void sendMessage(final String message) throws IOException {
        webSocketServerChat.getSession().getRemote().sendString(message);
    }

    public void sendByteArray(final byte[] data) {
        //TODO: initialiser la variable callback
        final WriteCallback callback = null;
        final ByteBuffer data1 = ByteBuffer.wrap(data);
        webSocketServerChat.getSession().getRemote().sendBytes(data1, callback);
    }

    @Override
    protected void enrichOnInit(final ModelWriter writer) {
        super.enrichOnInit(writer);
        writer.write(ServerToClientModel.WEBSOCKET_CREATE, this.url);
    }

    @Override
    public boolean attach(final PWindow window) {
        final boolean result = super.attach(window);
        if (result) window.addDestroyListener(event -> onDestroy());
        return result;
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.WEB_SOCKET;
    }
}
