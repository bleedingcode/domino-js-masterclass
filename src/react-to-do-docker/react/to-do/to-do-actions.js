import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';
import { postPendingData } from '../core/core-logic';
import axios from 'axios';
import Moment from 'moment';

/*
  ENUMS
*/
export const actions = {
	RESET_STATE: 'RESET_TODO_STATE',
	CREATE_DOCUMENT: 'CREATE_TODO_PROFILE',
	EDIT_DOCUMENT: 'EDIT_TODO_PROFILE',
	DELETE_DOCUMENT: 'DELETE_TODO_PROFILE',
	CANCEL_DOCUMENT: 'CANCEL_TODO_PROFILE',
	SAVE_DOCUMENT: 'SAVE_TODO_PROFILE',
	FILTER_LIST: 'FILTER_TODO_LIST',
	FETCH_ALL_DATA: 'FETCH_ALL_TODO_DATA',
	UPDATE_DATA: 'UPDATE_TODO_DATA',
	UPDATE_UI_DATA: 'UPDATE_TODO_UI_DATA',
	RESET_LOADING: 'RESET_TODO_LOADING'
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

// Edit a document by id
export const editProfile = (id) => {
	return {
		type: actions.EDIT_DOCUMENT,
		id
	}
}

// Cancel editing
export const cancelProfile = () => {
	return {
		type: actions.CANCEL_DOCUMENT
	}
}

// Filter the view based on a key
export const filterList = (key, value) => {
	// key = "listFilter", value is what's being filtered on
	return {
		type: actions.FILTER_LIST,
		key,
		value
	}
}

// Save a document with changes
export const updateData = (data) => {
	return {
		type: actions.UPDATE_DATA,
		data
	}
}

// This must be being used when updates are done in other web sockets
export const updateUIData = (data) => {
	return {
		type: actions.UPDATE_UI_DATA,
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
		let activeEntry = tempData.toDo.activeEntry;
		var entry = {};
		var dupArray = null;
		var reqType = "";

		//Finalise formatting of data
		activeEntry.data.taskName = _.trim(activeEntry.data.taskName);
		activeEntry.data.description = _.trim(activeEntry.data.description);

		if (activeEntry.data.dueDate) {
			activeEntry.data.dueDate = Moment(activeEntry.data.dueDate).format("YYYY-MM-DD");
		}

		//Change status of record to Pending
		activeEntry.custom.status = 'warning';

		if (activeEntry.custom.isNewDoc) {
			//Add new Entry to State
			activeEntry.custom.isNewDoc = false;
			activeEntry.custom.action = "create";
			tempArray.push(activeEntry);

			//Add Entry to Queue
			tempData.toDo.data.push(JSON.parse(JSON.stringify(activeEntry)));
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
			tempIndex = tempData.toDo.data.findIndex(t => t._id === activeEntry._id);

			if (tempIndex > -1) {
				//Update the existing entry
				tempData.toDo.data[tempIndex] = activeEntry;
			} else {
				//Add Entry to queue
				tempData.toDo.data.push(JSON.parse(JSON.stringify(activeEntry)));
			}
		}

		// Trigger SAVE_DOCUMENT action, passing entry to be saved
		dispatch({
			type: actions.SAVE_DOCUMENT,
			tempArray
		})

		// Get any todos pending update to database
		dupArray = JSON.parse(JSON.stringify(tempData.toDo.data));

		// Trigger REST service via web socket call for them, as create / update
		for (var x in dupArray) {
			entry = dupArray[x];
			reqType = entry.custom.action === "create" ? "5" : "6";

			let params = {
				reqType: reqType,
				socketId: Globals.user.socketId,
				username: Globals.user.username,
				password: Globals.user.password,
				record: entry
			};

			Globals.ws.emit('to-do-requests', params);
		}

		dupArray = null;
		tempData.toDo.data = [];
	}
}

// Delete selected profile
export const deleteProfile = (id, state) => {
	return dispatch => {
		let tempEntry = {};
		let tempIndex = tempData.toDo.data.findIndex(t => t._id === id);
		let entry = {};
		let dupArray = null;

		if (tempIndex > -1) {
			// It's already in temporary data memory
			tempEntry = tempData.toDo.data[tempIndex];

			if (tempEntry.custom.isSavedDoc) {
				//Change the action type of the entry to be "delete"
				tempEntry.custom.action = "delete";
			} else {
				//Remove the entry
				tempData.toDo.data.splice(tempIndex, 1);
			}
		} else {
			// Get it from the store and push to tempData
			tempEntry = state.data.find(t => t._id === id);

			if (tempEntry) {
				tempEntry.custom.action = "delete";
				tempData.toDo.data.push(JSON.parse(JSON.stringify(tempEntry)));
			}
		}

		// Trigger delete from state
		dispatch({
			type: actions.DELETE_DOCUMENT,
			id
		})

		// Get any pending deletions
		dupArray = JSON.parse(JSON.stringify(tempData.toDo.data));

		for (var x in dupArray) {
			entry = dupArray[x];

			// Make call to web socket to delete
			postPendingData("to-do", entry, "delete")
				.then(function (response) {
					if (response.data.success) {
						// It worked. Clear action and status and update state
						entry.custom.action = "";
						entry.custom.status = "";

						dispatch(updateData(entry));
					}
				})
				.catch(function (error) {
					console.log(error);
					return null;
				});
		}

		dupArray = null;
		tempData.toDo.data = [];
	}
}

// Fetch data from relevant view. reqType will map to relevant REST service call needed
export const fetchAllData = (appType) => {
	return dispatch => {
		//Determine Req Type
		let reqType = "";

		switch (appType) {
			case "to-do-new":
				reqType = "1";
				break;
			case "to-do-assigned":
				reqType = "2";
				break;
			case "to-do-complete":
				reqType = "3";
				break;
			case "to-do-overdue":
				reqType = "4";
				break;
		}

		let params = {
			reqType: reqType,
			socketId: Globals.user.socketId,
			username: Globals.user.username,
			password: Globals.user.password
		};

		Globals.ws.emit('to-do-requests', params);
	}
}

// Deal with the response
export const processWSResponse = (data) => {
	if (data.success) {
		switch (data.reqType) {
			case "1"://Fetch All Data - New
			case "2"://Fetch All Data - Assigned
			case "3"://Fetch All Data - Complete
			case "4"://Fetch All Data - Overdue
				Globals.storeList = data.storeList;

				//We need to add custom object to each record
				for (var x in data.data) {
					data.data[x].custom = JSON.parse(JSON.stringify(tempData.toDo.dataTemplate.custom));
					data.data[x].custom.isSavedDoc = true;
					data.data[x].custom.isNewDoc = false;
					data.data[x].custom.status = "";
					data.data[x].data.storeId = data.data[x].data.metaversalId.substring(0, 16);//TODO: Paul needs to provide the Store Id for me
					data.data[x].data.storeName = _getStoreName(data.data[x].data.storeId);
				}

				// Update the state
				Globals.dispatch({ type: actions.FETCH_ALL_DATA, payload: { data: data.data, storeList: data.storeList } });
				break;
			case "5"://Submit Record Reponse
				if (data.success) {
					data.data.custom.action = "";
					data.data.custom.isSavedDoc = true;
					data.data.custom.status = "";
					data.data.data.storeId = data.data.data.metaversalId.substring(0, 16);//TODO: Paul needs to provide the Store Id for me
					data.data.data.storeName = _getStoreName(data.data.data.storeId);

					// We've changed custom properties, trigger updateData to update state
					Globals.dispatch(updateData(data.data));
				}
				break;
			case "6"://Update Record Reponse
				if (data.success) {
					data.data.custom.action = "";
					data.data.custom.isSavedDoc = true;
					data.data.custom.status = "";
					data.data.data.storeId = data.data.data.metaversalId.substring(0, 16);//TODO: Paul needs to provide the Store Id for me
					data.data.data.storeName = _getStoreName(data.data.data.storeId);

					// We've changed custom properties, trigger updateData to update state
					Globals.dispatch(updateData(data.data));
				}
				break;
			case "7"://Update UI Data
				if (data.success) {
					//We need to add custom object to each record
					for (var x in data.data) {
						data.data[x].custom = JSON.parse(JSON.stringify(tempData.toDo.dataTemplate.custom));
						data.data[x].custom.isSavedDoc = true;
						data.data[x].custom.isNewDoc = false;
						data.data[x].custom.status = "";
						data.data[x].data.storeId = data.data[x].data.metaversalId.substring(0, 16);//TODO: Paul needs to provide the Store Id for me
						data.data[x].data.storeName = _getStoreName(data.data[x].data.storeId);
					}

					Globals.dispatch(updateUIData(data.data));
				}
				break;
		}
	}
}

// Internal function to get the store name from the store list
const _getStoreName = (storeId) => {
	let storeName = "";

	for (var x in Globals.storeList) {
		if (Globals.storeList[x].value === storeId) {
			storeName = Globals.storeList[x].text;
			break;
		}
	}

	return storeName;
};