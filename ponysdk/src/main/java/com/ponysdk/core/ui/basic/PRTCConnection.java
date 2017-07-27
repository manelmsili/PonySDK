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

import com.ponysdk.core.model.ServerToClientModel;
import com.ponysdk.core.model.WidgetType;
import com.ponysdk.core.terminal.SignalingWebSocket;
import com.ponysdk.core.ui.WebSocketServerChat;
import com.ponysdk.core.writer.ModelWriter;

/**
 * @author mmsili
 *
 */
public class PRTCConnection extends PObject {

	public String localUrl;
	public String remoteUrl;

	private WebSocketServerChat webSocketServerChat;

	public PRTCConnection(final String url) {
		this.localUrl = url;
		SignalingWebSocket.get().setUrlRTCMap(this);
	}

	public WebSocketServerChat getWebSocketServerChat() {
		return webSocketServerChat;
	}

	public void setWebSocketServerChat(final WebSocketServerChat webSocketServerChat) {
		this.webSocketServerChat = webSocketServerChat;
	}

	@Override
	protected void enrichOnInit(final ModelWriter writer) {
		super.enrichOnInit(writer);
		writer.write(ServerToClientModel.WEBSOCKET_CREATE, this.localUrl);
	}

	@Override
	public boolean attach(final PWindow window, final PFrame frame) {
		final boolean result = super.attach(window, frame);
		if (result)
			window.addDestroyListener(event -> onDestroy());
		return result;
	}

	@Override
	protected WidgetType getWidgetType() {
		return WidgetType.RTC_CONNECTION;
	}

	// faire une méthode pour on Offer ... qui appelle directement ces memes
	// méthodes dans PTRTC en mode native
	// public void onOffer()

	public void sendString(final String data) throws IOException {
		webSocketServerChat.getSession().getRemote().sendString(data);
	}

	public void start() {
		saveUpdate(writer -> writer.write(ServerToClientModel.WEBRTC_START, null));
	}

	public String getLocalURL() {
		return this.localUrl;
	}

	public String getRemoteURL() {
		return this.remoteUrl;
	}

	public void setRemoteURL(final String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

}
