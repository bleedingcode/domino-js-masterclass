import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

import axios from 'axios';  // Promise based HTTP client for the browser and node.js
import {actions as toDoActions} from '../to-do/to-do-actions';
import {actions as toDoStoreActions} from '../to-do-store/to-do-store-actions';
import {toProperCase} from '../utilities';
import validate from 'validate.js';
import _ from 'lodash';

import {loadDataExtended as loadDataExtendedToDo} from '../to-do/to-do-actions';

// Pulling together all redux actions we're exposing. See landing-actions.js for a simpler example

export const actions = {
	SWITCH_MENU: 'SWITCH_MENU',
	SIGN_IN_USER: 'SIGN_IN_USER',
	SIGN_OUT_USER: 'SIGN_OUT_USER',
	OPEN_DRAWER: 'OPEN_DRAWER',
	TOGGLE_DRAWER: 'TOGGLEDRAWER',  
	INIT_PREVIOUS_APP: 'INIT_PREVIOUS_APP',
	NULL:'NULL'
}

/*
	Action definitions. Here we include any external code. core-app-reducer handles updating state
*/

// Actions for switching to a new menu option
export const switchMenu = (dataId) => {
	return dispatch => {
		switch (dataId) {
		case 'node-red'://Launch Node-RED and Continue - I think this is just in John's "starter" app
			dataId = null;
			window.open(Globals.nodeRedUrl);
		default:
			// dispatch to update ToDos and ToDoStores - hides "New" button until data is loaded from REST service
			dispatch({
				type: toDoActions.RESET_LOADING
			});
			dispatch({
				type: toDoStoreActions.RESET_LOADING
			});

			dispatch({
				type: actions.SWITCH_MENU,
				dataId
			})
		}
	}
}

export const openDrawer = () => {
	return {
		type: actions.OPEN_DRAWER
	}
}

export const toggleDrawer = () => {
	return {
		type: actions.TOGGLE_DRAWER
	}
}

export const initPreviousApp = () => {
	return dispatch => {
		dispatch({
			type: actions.INIT_PREVIOUS_APP
		})
	}
}

// Action for authenticating the current user, bound in home-anonymous-sign-in-container, called from authenticate() method in home-anonymous-sign-in
export const signInUser = (callback) => {
	return dispatch => {
		Globals.tempCallback = callback;	// tempThis.setState({ authenticating: false });
		let result = true;
		let htmlContent = '';
		let htmlStart = '<div><ul>'
		let htmlEnd = '</ul></div>'
		let html = '';
		let tmpDiv = document.getElementById('divMessages');
		let params = {};
		let username = tempData.signInForm.username;
		let password = tempData.signInForm.password;

		//First, validate Username
		if(!username || (username === '')){
			result = false;
			htmlContent += '<li>Please provide a Username</li>';
		}

		//Then, validate password
		if(!password || (password === '')){
			result = false;
			htmlContent += '<li>Please provide a Password</li>';
		}

		// If everything is filled in, pass to globals store
		if(result){
			Globals.user.username = username;
			Globals.user.password = password;

			// Set up parameters for web socket request. socketId is the web socket ID for this user
			params = {
				reqType:'1',
				socketId: Globals.user.socketId,
				username,
				password
			};

			/*
				Creates a web service call (because Globals.ws has been setup as a SocketIO web socket in home-anonymous-sign-in).
				Look on Node-RED. Triggers AF-Webhook, AF-WS Prep, then goes to ToDo subflow.
				First parameter is the connectionType used in Node-RED "To Do" subflow, "TD-Connection Type" node.
				This routes through TD1-Authenticate to populate msg.user (the current user's User document from Domino) and msg.users (the list of users from Agilit-e). If authentication fails, it throws node.error, which gets picked up by AF-Process Errors to abort and return the error.
				If all is fine, Node-RED then exits the subflow and goes to AF-Prepare Success. This then calls "to-do-app-response", which triggers processSignInResult below.
			*/
			Globals.ws.emit('to-do-app-requests', params);
		}

		// Finalise
		if(result){
			html = ''; // No validation error. We may have received an error from Node-RED authentication though
		}else{
			// We hit an error validating, post message(s) and trigger callback (to set authenticating=false)
			html = htmlStart + htmlContent + htmlEnd;
			Globals.tempCallback();
		}

		tmpDiv.innerHTML = html;
	}
}

/*
	Handle the result from the websocket response
*/
export const processSignInResult = (data) => {
	let htmlContent = '';
	let htmlStart = '<div><ul>'
	let htmlEnd = '</ul></div>'
	let html = '';
	let tmpDiv = document.getElementById('divMessages');  

	if(data.success){
		// Login was successful. Extract the user's name and list of users to assign a ToDo to. Then dispatch SIGN_IN_USER action in core-app-reducer
		tempData.signInForm = {};
		Globals.user.commonName = data.data.user.commonName;
		Globals.userList = data.data.users;
	
		// This updates user.loggedIn to true, which changes the component appearing in landing-wrapper
		Globals.dispatch({
			type: actions.SIGN_IN_USER
		})
	}else{
		// Node-RED returned an error, so write out those errors and trigger callback (to set authenticating=false)
		Globals.user.username = '';
		Globals.user.password = '';

		for(var x in data.messages){
			htmlContent += `<li>${data.messages[x]}</li>`;
		}

		html = htmlStart + htmlContent + htmlEnd;
		tmpDiv.innerHTML = html;
		Globals.tempCallback();
	}
}