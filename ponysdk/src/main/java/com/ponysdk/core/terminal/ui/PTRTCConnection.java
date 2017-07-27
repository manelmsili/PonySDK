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

package com.ponysdk.core.terminal.ui;

import com.ponysdk.core.model.ServerToClientModel;
//import com.ponysdk.core.terminal.SignalingWebSocket;
import com.ponysdk.core.terminal.UIBuilder;
import com.ponysdk.core.terminal.model.BinaryModel;
import com.ponysdk.core.terminal.model.ReaderBuffer;

import elemental.client.Browser;
//import elemental.dom.MediaStreamList;
//import elemental.html.SessionDescription;
import elemental.html.WebSocket;
//import elemental.js.html.JsPeerConnectionBis;

/**
 * @author mmsili
 *
 */
public class PTRTCConnection extends AbstractPTObject {

	// public JsPeerConnectionBis rtcConnection;

	public WebSocket newWebSocket;

	public PTRTCConnection() {
		// this.pc = new JsPeerConnectionBis();
	}

	@Override
	public void create(final ReaderBuffer buffer, final int id, final UIBuilder uiBuilder) {
		super.create(buffer, id, uiBuilder);
		final BinaryModel urlModel = buffer.readBinaryModel();
		final String url = urlModel.getStringValue();
		Browser.getWindow().getConsole().log(urlModel.getStringValue());
		this.newWebSocket = Browser.getWindow().newWebSocket(url);
		newWebSocket.setBinaryType("banarydata");
		this.initJs(newWebSocket);
	}

	@Override
	public boolean update(final ReaderBuffer buffer, final BinaryModel binaryModel) {
		final int modelOrdinal = binaryModel.getModel().ordinal();
		if (ServerToClientModel.WEBRTC_START.ordinal() == modelOrdinal) {
			startJs(newWebSocket);
			return true;
		} else {
			return super.update(buffer, binaryModel);
		}
	}

	private native void initJs(WebSocket newWebSocket) /*-{
														if ($wnd.initJs && typeof $wnd.initJs == 'function') $wnd.initJs(newWebSocket);
														}-*/;

	private native void startJs(WebSocket newWebSocket) /*-{
														if ($wnd.startRTC && typeof $wnd.startRTC == 'function') $wnd.startRTC(newWebSocket);
														}-*/;

	public native void start(WebSocket newWebSocket)/*-{
																		var configuration = {
													iceServers: [{ "url": "stun:stun.l.google.com:19302"},
													{ url: "turn:numb.viagenie.ca",credential: "manel",username: "1234"}]}
													var pc;
													var otherStream;
													var localStream;

													var el = $wnd.document.getElementsByClassName("pLabel")[0];
													el.addEventListener("click", createOffer, false);

													// crée une offre
													function createOffer() {
													configurateParams();

													newWebSocket.onmessage = function(evt) {
													var message = JSON.parse(evt.data);
													console.log("******* message de type ", message.type);

													if (message.type == "offer") {
													console.log("----------- Offre ------------ ");

													pc.setRemoteDescription(new RTCSessionDescription(message.payload)).then(function () {
													console.log("setRemoteDescription");
													return pc.createAnswer();
													}, function(reason){
													console.error("catch error : " + reason);
													})
													.then(function (answer) {
													console.log("sendAnswer");
													return pc.setLocalDescription(answer);
													})
													.then(function () {
													var data = {
													type: desc,
													payload: pc.localDescription
													};
													sendToServer(data);
													})
													.then(function error(e){
													console.log("error");
													});

													console.log("apres");

													}else if (message.type=="answer"){
													console.log("Je viens de recevoir une réponse");
													pc.setRemoteDescription(desc);
													}
													else if(message.type=="candidate"){
													console.log("---icecandidate recu---");
													pc.addIceCandidate(message.candidate);
													console.log("fin ice candidate");
													}
													else console.log("???????");
													}
													}


													function configurateParams() {

													pc = new RTCPeerConnection(configuration);
													navigator.getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);
													if (navigator.mediaDevices.getUserMedia) {
													navigator.getUserMedia({ audio: true },function(stream) {
													localStream = stream;
													console.log("adding localStream to pc");
													pc.addStream(localStream);
													},
													function(e) {
													alert('Error capturing audio.');
													});
													} else
													alert('getUserMedia not supported in this browser.');

													pc.ontrack = function(e) {
													console.log("je viens de recevoir un stream de l'autre bout");
													otherStream = e.stream;
													};

													pc.onicecandidate = function(evt) {
													if (!evt || !evt.candidate) return;

													console.log("je viens de capter iceCandidate, que je vais envoyer ...");
													var data = {
													type: 'candidate',
													payload: evt.candidate
													};
													sendToServer(data);
													};

													pc.onnegotiationneeded = function () {
													console.log("creating an offer" , pc);
													pc.createOffer().then(function (offer) {
													return pc.setLocalDescription(offer);
													})
													.then(function () {
													console.log("desc sent to server" , pc);
													var data = {
													type: 'offer',
													payload: pc.localDescription
													};
													sendToServer(data);
													});
													};
													}

													// Envoyer au SignalingWebSocket les données reçus
													function sendToServer(data) {
													try {
													newWebSocket.send(JSON.stringify(data));
													return true;
													} catch (e) {
													console.log('There is no connection to the websocket server');
													return false;
													}
													}

													function createRTCIceCandidate(candidate) {
													var ice;
													if (typeof(webkitRTCIceCandidate) === 'function') {
													ice = new webkitRTCIceCandidate(candidate);
													} else if (typeof(RTCIceCandidate) === 'function') {
													ice = new RTCIceCandidate(candidate);
													}
													return ice;
													}

													function setIceCandidates(iceCandidate) {
													pc.addIceCandidate(createRTCIceCandidate(iceCandidate.candidate));
													}
													
													}-*/;
}
