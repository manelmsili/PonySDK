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

package com.ponysdk.core.ui;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.common.WebSocketSession;

import com.ponysdk.core.terminal.SignalingWebSocket;
import com.ponysdk.core.ui.basic.ManagerOfPWebSocket;
import com.ponysdk.core.ui.basic.PWebSocket;

/**
 * @author mmsili
 *
 */
public class WebSocketServerChat extends WebSocketAdapter {

	private String urlAdress;
	private PWebSocket pwebSocket;

	public String getUrlAdress() {
		return urlAdress;
	}

	public void setUrlAdress(final String urlAdress) {
		this.urlAdress = urlAdress;
	}

	public WebSocketServerChat() {
	}

	@Override
	public void onWebSocketConnect(final Session sess) {
		super.onWebSocketConnect(sess);

		this.urlAdress = ((WebSocketSession) sess).getRequestURI().toString();
		// ManagerOfPWebSocket.get().onWebSocketConnectionSuccessfull(this);
		SignalingWebSocket.get().onWebSocketConnectionSuccessfull(this);

		System.out.println("Socket Connected: " + sess);
		// session.getRemote().sendString()
	}

	@Override
	public void onWebSocketText(final String message) {
		super.onWebSocketText(message);
		// ManagerOfPWebSocket.get().onWebSocketMessageReceived(message);
		try {
			SignalingWebSocket.get().onMessage(message, this);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		// System.out.println("Received TEXT message: " + message);
	}

	@Override
	public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {
		super.onWebSocketBinary(payload, offset, len);
		try {
			ManagerOfPWebSocket.get().onWebSocketBinaryReceived(payload, 0, payload.length, this);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(final int statusCode, final String reason) {
		super.onWebSocketClose(statusCode, reason);
		ManagerOfPWebSocket.get().onWebSocketCloseSuccessfull(this);
		System.out.println("Socket Closed: [" + statusCode + "] " + reason);
	}

	@Override
	public void onWebSocketError(final Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}

	public PWebSocket getPwebSocket() {
		return pwebSocket;
	}

	public void setPwebSocket(final PWebSocket pwebSocket) {
		this.pwebSocket = pwebSocket;
	}

}
