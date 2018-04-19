import IOClient from 'socket.io-client';
import Globals from '../globals';
import { processSignInResult } from '../core/core-actions';

/*
Set up and close down the web socket for the landing page. This means core-actions can use emit()
*/

// Connects the web socket for *for this area of the system*
export const connectWebSocket = () => {
	Globals.ws = IOClient.connect(Globals.wsUrl, { reconnect: true });

	// Log we've connected. Triggered on every connection.
	Globals.ws.on('connect', function (socket) {
		console.log("Main Web Socket Connected!");
	});

	// Pass the web socket id to the Globals object for this user
	Globals.ws.on('init-user-session', function (id) {
		Globals.user.socketId = id;
	});

	// Display the sign-on success / failure message in core-actions
	Globals.ws.on('to-do-app-response', processSignInResult);

	return null;
}


/*
Tear down the web socket
*/
export const disconnectWebSocket = () => {
	console.log("Main Web Socket Disconnected");
	Globals.ws.disconnect();
	Globals.ws = null;
	return true;
}