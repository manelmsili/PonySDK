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
import java.util.HashMap;
import java.util.Map;

import com.ponysdk.core.ui.WebSocketServerChat;

/**
 * @author mmsili
 *
 */
public class ManagerOfPWebSocket {

    private static final ManagerOfPWebSocket INSTANCE = new ManagerOfPWebSocket();

    public static Map<String, PWebSocket> urlPWSMap;

    static {
        urlPWSMap = new HashMap<>();
    }

    private ManagerOfPWebSocket() {
    }

    public static ManagerOfPWebSocket get() {
        return INSTANCE;
    }

    public Map<String, PWebSocket> getUrlPWSMap() {
        return urlPWSMap;
    }

    public void setUrlPWSMap(final PWebSocket pwebSocket) {
        urlPWSMap.put(pwebSocket.getURL(), pwebSocket);
    }

    // Lors d'une connexion d'un PWS, on le récupère via son URL et on lui associe un WebServerChat
    public void onWebSocketConnectionSuccessfull(final WebSocketServerChat webSocketServerChat) {
        final PWebSocket pWebSocket = urlPWSMap.get(webSocketServerChat.getUrlAdress());
        if (pWebSocket != null) {
            pWebSocket.setWebSocketServerChat(webSocketServerChat);
        } else System.err.println("tu ne devrais pas etre là !");
    }

    public void onWebSocketCloseSuccessfull(final WebSocketServerChat webSocketServerChat) {
        final PWebSocket pWebSocket = urlPWSMap.get(webSocketServerChat.getUrlAdress());
        if (pWebSocket != null) {
            urlPWSMap.remove(webSocketServerChat.getUrlAdress());
            pWebSocket.setWebSocketServerChat(null);
        }
    }

    public void onWebSocketMessageReceived(final String message) {
        System.err.println("Manager : message reçu " + message);
    }

    public void onWebSocketBinaryReceived(final byte[] payload, final int i, final int length,
                                          final WebSocketServerChat webSocketServerChat)
            throws IOException {

        final PWebSocket pWebSocket = urlPWSMap.get(webSocketServerChat.getUrlAdress());

        if (pWebSocket.getURL().equals("wss://mmsili.smart-trade.net:8082/sample/chat/1"))
            urlPWSMap.get("wss://mmsili.smart-trade.net:8082/sample/chat/2").sendByteArray(payload);
        else urlPWSMap.get("wss://mmsili.smart-trade.net:8082/sample/chat/1").sendByteArray(payload);

        //        final PWebSocket pWebSocket = urlPWSMap.get(webSocketServerChat.getUrlAdress());
        //        final Iterator it = urlPWSMap.entrySet().iterator();
        //        while (it.hasNext()) {
        //            final Map.Entry pair = (Map.Entry) it.next();
        //            if (webSocketServerChat.getUrlAdress() != null) {
        //                if (!webSocketServerChat.getUrlAdress().equals(pair.getKey())) {
        //                    ((PWebSocket) pair.getValue()).sendByteArray(payload);
        //                } else;
        //            }
        //        }
    }

}
