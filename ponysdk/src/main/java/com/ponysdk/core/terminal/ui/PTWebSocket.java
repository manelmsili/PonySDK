/*
 * Copyright (c) 2017 PonySDK
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

package com.ponysdk.core.terminal.ui;

import com.ponysdk.core.terminal.UIBuilder;
import com.ponysdk.core.terminal.model.BinaryModel;
import com.ponysdk.core.terminal.model.ReaderBuffer;

import elemental.client.Browser;
import elemental.html.AudioContext;
import elemental.html.Blob;
import elemental.html.WebSocket;
import jsinterop.annotations.JsMethod;

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
        System.err.println("url : " + url);
        final WebSocket newWebSocket = Browser.getWindow().newWebSocket(url);
        newWebSocket.setBinaryType("blob");

        this.startAudio(newWebSocket);

    }

    public native void startAudio(WebSocket newWebSocket)/*-{
                                                         
                                                         var AudioContext = window.AudioContext || window.webkitAudioContext;
                                                            myAudioContextSent = new AudioContext();
                                                            myAudioContextReceive = new AudioContext();
                                                            BUFF_SIZE = 16384;
                                                            MyMediaStreamSource = null,
                                                            myGainNode = null,
                                                            myScriptProcessor = null,
                                                            sourceNode = null;
                                                         
                                                         var el = $wnd.document.getElementsByClassName("pLabel")[0];
                                                         el.addEventListener("click",sendSound, false);
                                                         
                                                           function configureNodes(stream){
                                                           myGainNode = myAudioContextSent.createGain();
                                                                myGainNode.connect(myAudioContextSent.destination);
                                                                myGainNode.gain.value = 0; // ou 1??
                                                                myMediaStreamSource = myAudioContextSent.createMediaStreamSource(stream);
                                                                myMediaStreamSource.connect(myGainNode);
                                                                // we can use  the constructot whithout parameters
                                                                myScriptProcessor = myAudioContextSent.createScriptProcessor(BUFF_SIZE, 1, 1);
                                                                myScriptProcessor.connect(myGainNode);
                                                                myMediaStreamSource.connect(myScriptProcessor);
                                                         }
                                                         
                                                         
                                                         
                                                         newWebSocket.onmessage= onMessage(message,myAudioContextReceive);
                                                         
                                                         
                                                         function sendSound(){
                                                            configureNavigator();
                                                            function start_microphone(stream){
                                                                configureNodes(stream);
                                                                myScriptProcessor.onaudioprocess = function(audioProcessingEvent) {
                                                                    var inputBuffer = audioProcessingEvent.inputBuffer;
                                                                    var outputBuffer = audioProcessingEvent.outputBuffer;
                                                                    var blob = null;
                                                                    var inputData = inputBuffer.getChannelData(0);
                                                                    var outputData = outputBuffer.getChannelData(0);
                                                                    for (var sample = 0; sample < inputBuffer.length; sample++) {
                                                                        outputData[sample] = inputData[sample];
                                                                    }
                                                                   blob = new Blob([outputData]);
                                                                   newWebSocket.send(blob);
                                                              };
                                                            }
                                                         
                                                         
                                                         
                                                          function configureNavigator(){
                                                          navigator.getUserMedia = ( navigator.getUserMedia ||
                                                               navigator.webkitGetUserMedia ||
                                                               navigator.mozGetUserMedia ||
                                                               navigator.msGetUserMedia);
                                                            if (navigator.mediaDevices.getUserMedia) {
                                                            navigator.getUserMedia({
                                                            audio: true
                                                            },
                                                            function(stream) {
                                                            start_microphone(stream);
                                                            },
                                                            function(e) {
                                                                alert('Error capturing audio.');
                                                             });
                                                            } else
                                                                alert('getUserMedia not supported in this browser.');
                                                         };
                                                         };
                                                         }-*/;

    @JsMethod
    public native void onMessage(Blob message, AudioContext myAudioContextReceive)/*-{
                                                                                    var reader = new FileReader();
                                                                                    var offset=0;
                                                                                    receivedBuffer= null;
                                                                                    var arrayBuffer=null;
                                                                                    reader.onload = function(e) {
                                                                                        arrayBuffer = reader.result;
                                                                                  
                                                                                        addToQueue(myAudioContextReceive);
                                                                                        init();
                                                                                        var sourceNodeOnMsg = myAudioContextReceive.createBufferSource();
                                                                                        sourceNodeOnMsg.buffer =receivedBuffer;
                                                                                        sourceNodeOnMsg.connect(myAudioContextReceive.destination);
                                                                                        sourceNodeOnMsg.start(myAudioContextReceive.nextStartTime);
                                                                                        myAudioContextReceive.nextStartTime+=receivedBuffer.duration;
                                                                                        console.log("duration : "+receivedBuffer.duration);
                                                                                        console.log("nextStartTime : "+myAudioContextReceive.nextStartTime +" currentTime : "+ myAudioContextReceive.currentTime);
                                                                                    };
                                                                                  
                                                                                    reader.readAsArrayBuffer(message.data);
                                                                                  
                                                                                    function init(){
                                                                                        receivedBuffer = myAudioContextReceive.createBuffer(2, (message.data.size)/4, myAudioContextReceive.sampleRate);
                                                                                        var array32 = new Float32Array(arrayBuffer);
                                                                                        for (var canal = 0; canal < 2; canal++) {
                                                                                            var dataArray = receivedBuffer.getChannelData(canal);
                                                                                            for (var i = 0; i < array32.length; i++){
                                                                                                dataArray[i] = array32[i];
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    }-*/;

    public native void addToQueue(AudioContext myAudioContextReceive)/*-{
                                                                       function addToQueue(){
                                                                           if(!myAudioContextReceive.nextStartTime){
                                                                           myAudioContextReceive.nextStartTime= myAudioContextReceive.currentTime++;
                                                                           }
                                                                        };
                                                                     }-*/;
}
