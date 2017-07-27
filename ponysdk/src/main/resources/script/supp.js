
var configuration = {
    iceServers: [{ "url": "stun:stun.l.google.com:19302"},
    { url: "turn:numb.viagenie.ca",credential: "manel",username: "1234"}]}
var pc;
var audio;

function initJs(newWebSocket) {

	audio = document.querySelector('audio');

    pc = new RTCPeerConnection(configuration);
    pc.onicecandidate = function(evt) {
        if (!evt || !evt.candidate) return;

       // console.log("je viens de capter iceCandidate, que je vais envoyer ...");
        var data = {
            type: 'candidate',
            payload: evt.candidate
        };
        sendToServer(newWebSocket, data);
    };
	function setIceCandidates(iceCandidate) {
	    pc.addIceCandidate(createRTCIceCandidate(iceCandidate));
	}

	 pc.onaddstream = function(e) {
        console.log("a stream received");

		if (window.URL) {
		    audio.src = window.URL.createObjectURL(e.stream);
		  } else {
		    audio.src = e.stream;
		  }

		audio.onloadedmetadata = function(e){
		    audio.play();
		}              
    };

    newWebSocket.onmessage = function(evt) {
        var message = JSON.parse(evt.data);

        if (message.type == "offer") {
        	console.log("an offer received");
			pc.setRemoteDescription(new RTCSessionDescription(message.payload))
			    .then(function () {
		    		//	console.log("setRemoteDescription");
		        	return pc.createAnswer();
	        	}, function error(reason){
	        		console.error("catch error : " + reason);
	        	})
	        	.then(function (answer) {
		       		// console.log("sendAnswer");
		            return pc.setLocalDescription(answer);
		        }, function error(reason){
	        		console.error("catch error : " + reason);
	        	})
		        .then(function () {
		            var data = {
	                    type: 'answer',
	                    payload: pc.localDescription
	                };
		            sendToServer(newWebSocket, data);
		        }, function error(reason){
	        		console.error("catch error : " + reason);
	        	});
        } else if (message.type=="answer"){
        	console.log("a answer received");

              pc.setRemoteDescription(message.payload);
        }
  		else if(message.type=="candidate"){
			console.log("a candidate received");
			setIceCandidates(message.payload);
         }
         else console.log(" unknowen received "+message.type);
    }
}

function startRTC(newWebSocket) {
	navigator.getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);
    if (navigator.mediaDevices.getUserMedia) {

        navigator.getUserMedia({ audio: true },function(stream) {
            console.log("adding localStream to pc");
            pc.addStream(stream);
        },function(e) {
            alert('Error capturing audio.');
        });
    } else
        alert('getUserMedia not supported in this browser.');

    pc.onnegotiationneeded = function () {
      console.log("Creating an offer" );
        pc.createOffer().then(function (offer) {
            return pc.setLocalDescription(offer);
        }).then(function () {
            var data = {
                type: 'offer',
                payload: pc.localDescription
            };
            sendToServer(newWebSocket, data);
        });
    };
}

// Envoyer au SignalingWebSocket les données reçus
function sendToServer(newWebSocket, data) {
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

if (typeof module !== 'undefined' && module.hasOwnProperty('exports')) module.exports.startRTC = startRTC;
else window['startRTC'] = startRTC;