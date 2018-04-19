import IOClient from 'socket.io-client';
import _ from 'lodash';
import { processWSResponse } from './to-do-actions';

import { cancelProfile, submitProfile } from './to-do-actions';
import { postPendingData } from '../core/core-logic';
import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

// Set up the web socket
export const connectWebSocket = (callback) => {
	Globals.ws = IOClient.connect(Globals.wsUrl, { reconnect: true });

	Globals.ws.on('connect', function (socket) {
		console.log("To Do Web Socket Connected!");

		// Not quite sure when this runs
		Globals.ws.on('init-user-session', function (id) {
			Globals.user.socketId = id;
			callback();
		});

		// This runs on response from web socket server
		Globals.ws.on('to-do-response', processWSResponse);
	});

	return null;
}

// Disconnect the web socket
export const disconnectWebSocket = () => {
	console.log("To Do Web Socket Disconnected");
	Globals.ws.disconnect();
	Globals.ws = null;

	return true;
}

// Cancel editing of a document
export const confirmCancel = (dispatch) => {
	dispatch(cancelProfile());
	return true;
}

// Validate Form before submitting
export const validateSubmit = (dispatch, state) => {
	let result = true;
	let result2 = true;
	let htmlContent = "";
	let htmlStart = "<div><ul>"
	let htmlEnd = "</ul></div>"
	let html = "";
	let entry;

	entry = tempData.toDo.activeEntry.data;

	//Validate Fields
	if (entry.taskName === "") {
		result = false;
		htmlContent += "<li>Please provide a Task Name</li>";
	}

	if (entry.description === "") {
		result = false;
		htmlContent += "<li>Please provide a Description</li>";
	}

	if (!entry.dueDate) {
		result = false;
		htmlContent += "<li>Please provide a Due Date</li>";
	}

	if (entry.priority === "") {
		result = false;
		htmlContent += "<li>Please provide a Priority</li>";
	}

	/*
	 *FINALISE
	 */
	let tmpDiv = document.getElementById('divMessages');

	if (result) {
		tmpDiv.innerHTML = "";
		dispatch(submitProfile(state));
	} else {
		html = htmlStart + htmlContent + htmlEnd;
		tmpDiv.innerHTML = html;
	}
}