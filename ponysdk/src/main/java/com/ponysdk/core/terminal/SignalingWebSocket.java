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

package com.ponysdk.core.terminal;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.ponysdk.core.ui.WebSocketServerChat;
import com.ponysdk.core.ui.basic.PRTCConnection;

/**
 * @author mmsili
 *
 */
@ServerEndpoint("/webrtc")
public class SignalingWebSocket {

	private static final SignalingWebSocket INSTANCE = new SignalingWebSocket();

	public static Map<String, PRTCConnection> urlRTCMap = new HashMap<>();

	private SignalingWebSocket() {
	}

	public static SignalingWebSocket get() {
		return INSTANCE;
	}

	public Map<String, PRTCConnection> urlRTCMap() {
		return urlRTCMap;
	}

	public void setUrlRTCMap(final PRTCConnection pRTCConnection) {
		urlRTCMap.put(pRTCConnection.getLocalURL(), pRTCConnection);
	}

	public void onWebSocketCloseSuccessfull(final WebSocketServerChat webSocketServerChat) {
		final PRTCConnection pRTCConnection = urlRTCMap.get(webSocketServerChat.getUrlAdress());
		if (pRTCConnection != null) {
			urlRTCMap.remove(webSocketServerChat.getUrlAdress());
			pRTCConnection.setWebSocketServerChat(null);
		}
	}

	public void onWebSocketConnectionSuccessfull(final WebSocketServerChat webSocketServerChat) {
		final PRTCConnection pRTCConnection = urlRTCMap.get(webSocketServerChat.getUrlAdress());
		if (pRTCConnection != null) {
			pRTCConnection.setWebSocketServerChat(webSocketServerChat);
		} else
			System.err.println("pRTCConnection = null");
	}

	@OnMessage
	public void onMessage(final String message, final WebSocketServerChat webSocketServerChat) throws IOException {
		final PRTCConnection pRTCConnection = urlRTCMap.get(webSocketServerChat.getUrlAdress());
		final JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		String type = "";
		if (jsonObject.containsKey("type") == true)
			type = jsonObject.getString("type");
		else
			;
		if (!message.equals(null)) {
			urlRTCMap.get(pRTCConnection.getRemoteURL()).sendString(message);
		} else
			System.out.println("message null : " + message);
	}

}
