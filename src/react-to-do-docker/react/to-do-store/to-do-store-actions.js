import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';
import { postPendingData } from '../core/core-logic';
import axios from 'axios';

/*
  Enums for the ToDo Store
*/
export const actions = {
	RESET_STATE: 'RESET_STORE_STATE',
	CREATE_DOCUMENT: 'CREATE_STORE_PROFILE',
	EDIT_DOCUMENT: 'EDIT_STORE_PROFILE',
	DELETE_DOCUMENT: 'DELETE_STORE_PROFILE',
	CANCEL_DOCUMENT: 'CANCEL_STORE_PROFILE',
	SAVE_DOCUMENT: 'SAVE_STORE_PROFILE',
	FILTER_LIST: 'FILTER_STORE_LIST',
	FETCH_ALL_DATA: 'FETCH_ALL_STORE_DATA',
	UPDATE_DATA: 'UPDATE_STORE_DATA',
	RESET_LOADING: 'RESET_STORE_LOADING'
}

/*
  QUICK STATE FUNCTIONS
*/
// Used when web socket first connects. Clears all previous state.Probably also used when logging out / back in
export const resetState = () => {
	return {
		type: actions.RESET_STATE
	}
}

// Create a new document
export const createProfile = () => {
	return {
		type: actions.CREATE_DOCUMENT
	}
}

// Edit the document for the relevant id
export const editProfile = (id) => {
	return {
		type: actions.EDIT_DOCUMENT,
		id
	}
}

// Cancel the current document
export const cancelProfile = () => {
	return {
		type: actions.CANCEL_DOCUMENT
	}
}

// Filter the list based on the key
export const filterList = (key, value) => {
	// key = "listFilter", value is what's being filtered on
	return {
		type: actions.FILTER_LIST,
		key,
		value
	}
}

// Update action
export const updateData = (data) => {
	return {
		type: actions.UPDATE_DATA,
		data
	}
}

/*
  ADVANCED STATE FUNCTIONS
*/
export const submitProfile = (state) => {
	return dispatch => {
		let tempArray = state.data.concat();
		let tempIndex = 0;
		let activeEntry = tempData.toDoStore.activeEntry;
		var entry = {};
		var dupArray = null;
		var reqType = "";

		//Finalise formatting of data
		activeEntry.data.title = _.trim(activeEntry.data.title);
		activeEntry.data.name = _.trim(activeEntry.data.name);

		//Change status of record to Pending
		activeEntry.custom.status = 'warning';

		if (activeEntry.custom.isNewDoc) {
			//Add new Entry to State
			activeEntry.custom.isNewDoc = false;
			activeEntry.custom.action = "create";
			tempArray.push(activeEntry);

			//Add Entry to Queue
			tempData.toDoStore.data.push(JSON.parse(JSON.stringify(activeEntry)));
		} else {
			if (activeEntry.custom.isSavedDoc) {
				activeEntry.custom.action = "update";
			} else {
				activeEntry.custom.action = "create";
			}

			//Update Entry in State
			tempIndex = tempArray.findIndex(t => t._id === activeEntry._id);
			tempArray[tempIndex] = activeEntry;

			//Add Entry to queue, or update existing entry in queue
			tempIndex = tempData.toDoStore.data.findIndex(t => t._id === activeEntry._id);

			if (tempIndex > -1) {
				//Update the existing entry
				tempData.toDoStore.data[tempIndex] = activeEntry;
			} else {
				//Add Entry to queue
				tempData.toDoStore.data.push(JSON.parse(JSON.stringify(activeEntry)));
			}
		}

		// Store in state
		dispatch({
			type: actions.SAVE_DOCUMENT,
			tempArray
		})

		dupArray = JSON.parse(JSON.stringify(tempData.toDoStore.data));

		for (var x in dupArray) {
			entry = dupArray[x];
			// Create goes through TD2-2-Create-Record in "To Do" subflow
			// Update goes through TD2-3-Update-Record in "To Do" subflow
			reqType = entry.custom.action === "create" ? "2" : "3";

			let params = {
				reqType: reqType,
				socketId: Globals.user.socketId,
				username: Globals.user.username,
				password: Globals.user.password,
				record: entry
			};

			// Make requests to web service
			Globals.ws.emit('to-do-store-requests', params);
		}

		// Reset
		dupArray = null;
		tempData.toDoStore.data = [];
	}
}

// Function to delete a profile based on the id
export const deleteProfile = (id, state) => {
	return dispatch => {
		let tempEntry = {};
		// try and find the selected document
		let tempIndex = tempData.toDoStore.data.findIndex(t => t._id === id);
		let entry = {};
		let dupArray = null;

		if (tempIndex > -1) {
			// We found it in tempData (it's not yet been pushed to the database), get the entry itself
			tempEntry = tempData.toDoStore.data[tempIndex];

			if (tempEntry.custom.isSavedDoc) {
				//Change the action type of the entry to be "delete"
				tempEntry.custom.action = "delete";
			} else {
				//Remove the entry - it's not been saved yet anyway
				tempData.toDoStore.data.splice(tempIndex, 1);
			}
		} else {
			// Try to find it in state
			tempEntry = state.data.find(t => t._id === id);

			// Change the action type of the entry to be "delete"
			if (tempEntry) {
				tempEntry.custom.action = "delete";
				tempData.toDoStore.data.push(JSON.parse(JSON.stringify(tempEntry)));
			}
		}

		// Trigger the code in the reducer
		dispatch({
			type: actions.DELETE_DOCUMENT,
			id
		})

		dupArray = JSON.parse(JSON.stringify(tempData.toDoStore.data));

		// I guess this processes all changes from all pure functions triggered
		for (var x in dupArray) {
			entry = dupArray[x];

			// Post the deletion to the store
			postPendingData("to-do-store", entry, "delete")
				.then(function (response) {
					if (response.data.success) {
						entry.custom.action = "";
						entry.custom.status = "";

						// We deleted it. Trigger the update in here to delete from the store
						dispatch(updateData(entry));
					}
				})
				.catch(function (error) {
					console.log(error);
					return null;
				});
		}

		dupArray = null;
		tempData.toDoStore.data = [];
	}
}

// Call the web socket server and load all data
export const fetchAllData = () => {
	return dispatch => {
		let params = {
			reqType: "1",
			socketId: Globals.user.socketId,
			username: Globals.user.username,
			password: Globals.user.password
		};

		Globals.ws.emit('to-do-store-requests', params);
	}
}

// Process web service responses to update store
export const processWSResponse = (data) => {
	if (data.success) {
		switch (data.reqType) {
			case "1"://Fetch All Data
				//We need to add custom object to each record
				for (var x in data.data) {
					data.data[x].custom = JSON.parse(JSON.stringify(tempData.toDoStore.dataTemplate.custom));
					data.data[x].custom.isSavedDoc = true;
					data.data[x].custom.isNewDoc = false;
					data.data[x].custom.status = ""
				}

				// Call FETCH_ALL_DATA in reducer, passing across data from REST service
				Globals.dispatch({ type: actions.FETCH_ALL_DATA, payload: data.data });
				break;
			case "2"://Submit Record Reponse
				if (data.success) {
					// We successfully saved the data. Tell that to temp data and update the "view"
					data.data.custom.action = "";
					data.data.custom.isSavedDoc = true;
					data.data.custom.status = "";

					Globals.dispatch(updateData(data.data));
				}
				break;
			case "3"://Update Record Reponse
				if (data.success) {
					// We successfully saved the data. Tell that to temp data and update the "view"
					data.data.custom.action = "";
					data.data.custom.isSavedDoc = true;
					data.data.custom.status = "";

					Globals.dispatch(updateData(data.data));
				}
				break;
		}
	}
}