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

import com.ponysdk.core.terminal.UIBuilder;
import com.ponysdk.core.terminal.model.BinaryModel;
import com.ponysdk.core.terminal.model.ReaderBuffer;

import elemental.client.Browser;
import elemental.html.WebSocket;

/**
 * @author mmsili
 *
 */
public class PTWebSocket extends AbstractPTObject {

    public PTWebSocket() {

    }

    @Override
    public void create(final ReaderBuffer buffer, final int id, final UIBuilder uiBuilder) {

        super.create(buffer, id, uiBuilder);

        final BinaryModel urlModel = buffer.readBinaryModel();
        final String url = urlModel.getStringValue();
        Browser.getWindow().getConsole().log(urlModel.getStringValue());
        final WebSocket newWebSocket = Browser.getWindow().newWebSocket(url);
        sendVoice(newWebSocket);
    }

    public static native void sendMessage(WebSocket newWebSocket) /*-{
                                                                  var el = $wnd.document.getElementsByClassName("pLabel2")[0];
                                                                  el.addEventListener("click",sendMsg, false);
                                                                  function sendMsg(){
                                                                  newWebSocket.send("bonjour");
                                                                  }
                                                                  }-*/;

    public static native void sendVoice(WebSocket newWebSocket)/*-{
                                                               var el = $wnd.document.getElementsByClassName("pLabel2")[0];
                                                                  el.addEventListener("click",sendSound, false);
                                                               
                                                                function sendSound(){
                                                               var AudioContext = window.AudioContext || window.webkitAudioContext;
                                                               var myAudioContext = new AudioContext();
                                                               var BUFF_SIZE = 16384;
                                                               MyMediaStreamSource = null,
                                                               myGainNode = null,
                                                               myScriptProcessor = null,
                                                               myAnalyser = null;

                                                               if (navigator.mediaDevices.getUserMedia) {
                                                               navigator.getUserMedia({
                                                               audio: true
                                                               },
                                                               function(stream) {
                                                               start_microphone(stream);
                                                               },
                                                               function(e) {
                                                               alert('Error capturing audio.');
                                                               }
                                                               );
                                                               } else {
                                                               alert('getUserMedia not supported in this browser.');
                                                               }

                                                               function start_microphone(stream) {
                                                               var oscillator = myAudioContext.createOscillator();
                                                               myGainNode = myAudioContext.createGain();

                                                               oscillator.connect(myGainNode);

                                                               myGainNode.connect(myAudioContext.destination);

                                                               oscillator.channelInterpretation = 'discrete';

                                                               MyMediaStreamSource = myAudioContext.createMediaStreamSource(stream);
                                                               MyMediaStreamSource.connect(myGainNode);

                                                               myScriptProcessor = myAudioContext.createScriptProcessor(BUFF_SIZE, 1, 1);
                                                               myScriptProcessor.connect(myGainNode);

                                                               MyMediaStreamSource.connect(myScriptProcessor);

                                                               var myAnalyser = myAudioContext.createAnalyser();
                                                               myAnalyser.fftSize = 2048;
                                                               myAnalyser.smoothingTimeConstant = 0;

                                                               MyMediaStreamSource.connect(myAnalyser);

                                                               myScriptProcessor.onaudioprocess = function() {
                                                               var array = new Uint8Array(myAnalyser.frequencyBinCount);
                                                               myAnalyser.getByteFrequencyData(array);
                                                               };
                                                               }
                                                                }
                                                               
                                                                }-*/;

}
