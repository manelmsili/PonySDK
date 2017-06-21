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
        System.err.println("url : " + url);
        final WebSocket newWebSocket = Browser.getWindow().newWebSocket(url);
        newWebSocket.setBinaryType("blob");
        this.sendAudio(newWebSocket);

    }

    public native void sendMessage(WebSocket newWebSocket) /*-{
                                                                 //        var el = $wnd.document.getElementsByClassName("pLabel2")[0];
                                                                 //                                                                  el.addEventListener("click",sendMsg, false);
                                                                 //                                                                  function sendMsg(){
                                                               //  newWebSocket.send("bonjour");
                                                                 //                                                                  }
                                                                 }-*/;

    public native void sendAudio(WebSocket newWebSocket)/*-{
                                                        var AudioContext = window.AudioContext || window.webkitAudioContext;
                                                            myAudioContextSent = new AudioContext();
                                                            myAudioContextReceive = new AudioContext();
                                                            BUFF_SIZE = 4096;
                                                            MyMediaStreamSource = null,
                                                            myGainNode = null,
                                                            myScriptProcessor = null,
                                                            nextStartTime = 0;
                                                            sourceNode = null;
                                                        
                                                        var el = $wnd.document.getElementsByClassName("pLabel")[0];
                                                        el.addEventListener("click",sendSound, false);
                                                        
                                                        var el1 = $wnd.document.getElementsByClassName("pLabelstop")[0];
                                                        el1.addEventListener("click",stopSound, false)
                                                        
                                                         function stopSound(){
                                                            myAudioContextReceive.close().then(function() {
                                                            console.log("close");
                                                            });
                                                        }
                                                        function addToQueue(){
                                                            if(!nextStartTime){
                                                            nextStartTime= myAudioContextReceive.currentTime++;
                                                            }
                                                         };
                                                        
                                                        newWebSocket.onmessage= function(message){
                                                        console.log("onMessage");
                                                            var arrayBuffer;
                                                            var reader = new FileReader();
                                                            reader.onload = function(e) {
                                                                arrayBuffer = this.result;
                                                                addToQueue();
                                                                init(arrayBuffer);
                                                                 var frameCount = myAudioContextReceive.sampleRate * 2.0;
                                                                 myArrayBuffer  = myAudioContextReceive.createBuffer(2,frameCount , myAudioContextReceive.sampleRate);
                                                                 for (var canal = 0; canal < 2; canal++) {
                                                                      var dataArray = myArrayBuffer .getChannelData(canal);
                                                                      for (var i = 0; i < arrayBuffer.length; i++){
                                                                        dataArray[i] = arrayBuffer[i];
                                                                      }
                                                                }
                                                                var sourceNode = myAudioContextReceive.createBufferSource();
                                                                sourceNode.buffer = myArrayBuffer;
                                                                sourceNode.connect(myAudioContextReceive.destination);
                                                                sourceNode.start(nextStartTime);
                                                                nextStartTime+= myArrayBuffer.duration;

                                                            };
                                                        
                                                                reader.readAsArrayBuffer(message.data);
                                                                function init(arrayBufferr){
                                                                     var frameCount = myAudioContextReceive.sampleRate * 2.0;
                                                                     myArrayBuffer  = myAudioContextReceive.createBuffer(2,frameCount , myAudioContextReceive.sampleRate);
                                                                     for (var canal = 0; canal < 2; canal++) {
                                                                          var dataArray = myArrayBuffer .getChannelData(canal);
                                                                          for (var i = 0; i < arrayBuffer.length; i++){
                                                                            dataArray[i] = arrayBuffer[i];
                                                                          }
                                                                    }
                                                                }
                                                        };

                                                         function sendSound(){
                                                         console.log("sendSound");
                                                            configureNavigator();
                                                            function start_microphone(stream){
                                                                console.log("start stream");
                                                                configureNodes(stream);
                                                                myScriptProcessor.onaudioprocess = function(audioProcessingEvent) {
                                                                    var inputBuffer = audioProcessingEvent.inputBuffer;
                                                                    var outputBuffer = audioProcessingEvent.outputBuffer;
                                                                    var blob = null;

                                                                    for (var channel = 0; channel < outputBuffer.numberOfChannels; channel++) {
                                                                        var inputData = inputBuffer.getChannelData(channel);
                                                                        var outputData = outputBuffer.getChannelData(channel);
                                                                        for (var sample = 0; sample < inputBuffer.length; sample++) {
                                                                            outputData[sample] = inputData[sample];
                                                                        }
                                                                     }
                                                                     console.log("case 1");
                                                                     blob = new Blob([outputBuffer], {type:'audio/wav'});
                                                                     newWebSocket.send(blob);
                                                                };
                                                            }



                                                         function configureNodes(stream){
                                                           myGainNode = myAudioContextSent.createGain();
                                                                myGainNode.connect(myAudioContextSent.destination);
                                                                //myGainNode.gain.value = 0;
                                                                myMediaStreamSource = myAudioContextSent.createMediaStreamSource(stream);
                                                                myMediaStreamSource.connect(myGainNode);
                                                                myScriptProcessor = myAudioContextSent.createScriptProcessor(BUFF_SIZE, 1, 1);
                                                                myScriptProcessor.connect(myGainNode);
                                                                myMediaStreamSource.connect(myScriptProcessor);
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

}
