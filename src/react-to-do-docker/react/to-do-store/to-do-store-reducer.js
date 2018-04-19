import toDoStoreState from './to-do-store-state';
import tempData from '../temp-data-store/temp-data';
import { actions } from './to-do-store-actions';

/*
	Reducer to update state for todo store
*/

const toDoStore = (state = toDoStoreState, action) => {
	switch (action.type) {
		// Reload the state
		case actions.RESET_STATE:
			return Object.assign({}, state, toDoStoreState)
		case actions.CREATE_DOCUMENT:
			// load the default entry
			var tempEntry = JSON.parse(JSON.stringify(tempData.toDoStore.dataTemplate));

			// Create a temp entry in memory, assigning a dummy id, set to *now*. Then set this new blank entry as the activeEntry
			tempEntry._id = new Date().getTime().toString();
			tempEntry.custom.tempId = tempEntry._id;
			tempData.toDoStore.activeEntry = tempEntry;

			// Tell the app we need to load the form facet
			return Object.assign({}, state, {
				header: {
					...state.header,
					facet: 'form'
				}
			})
		case actions.EDIT_DOCUMENT:
			// Load the entry selected. Custom fields will get set when initially loading the data, so it's marked as saved and not new. action.id gets passed in from to-do-store-actions
			var tempEntry = state.data.find(t => t._id === action.id);

			// Set it as the activeEntry
			tempData.toDoStore.activeEntry = JSON.parse(JSON.stringify(tempEntry));

			// Tell the app we need to load the form facet
			return Object.assign({}, state, {
				header: {
					...state.header,
					facet: 'form'
				}
			})
		case actions.DELETE_DOCUMENT:
			// Get the current entry
			var tmpIndex = state.data.findIndex(t => t._id === action.id);
			var tmpArray = state.data.concat();
			tmpArray.splice(tmpIndex, 1);

			return Object.assign({}, state, {
				header: {
					...state.header
				},
				data: tmpArray
			})
		case actions.CANCEL_DOCUMENT:
			// Clear the active entry
			tempData.toDoStore.activeEntry = {};

			// Tell the app to load the view facet
			return Object.assign({}, state, {
				header: {
					...state.header,
					facet: 'view'
				}
			})
		case actions.SAVE_DOCUMENT:
			// Clear the active entry
			tempData.toDoStore.activeEntry = {};

			// Tell the app to load the view facet, selecting the relevant document??
			return Object.assign({}, state, {
				header: {
					...state.header,
					facet: 'view'
				},
				data: action.tempArray
			})
		case actions.FILTER_LIST:
			var listFilter = state.header.listFilter;

			switch (action.key) {
				case "listFilter":
					listFilter = action.value;
					break;
			}

			return Object.assign({}, state, {
				header: {
					...state.header,
					listFilter: listFilter
				}
			})
		case actions.FETCH_ALL_DATA:
			// Pass all the data fetched via Node-RED into the app and confirm we're ready for "New" button to display
			return Object.assign({}, state, {
				header: {
					...state.header,
					dataLoaded: true
				},
				data: action.payload
			})
		case actions.RESET_LOADING:
			// Set that data is not yet loaded. This is done on switching a menu in core-actions and is used to hide "New" button
			return Object.assign({}, state, {
				header: {
					...state.header,
					dataLoaded: false
				}
			})
		case actions.UPDATE_DATA:
			var tempIndex = 0;
			var entry = {};
			var tempArray = state.data.concat();

			entry = action.data;

			//We need to see if we have to find doc by its temp id or main id
			tempIndex = -1;

			// Loop through the data and see if we can find entry.custom.tempId
			if (entry.custom.tempId) {
				tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry.custom.tempId.toLowerCase());
			}

			if (tempIndex < 0) {
				tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry._id.toLowerCase());
			}

			if (tempIndex >= 0) {
				tempArray[tempIndex] = JSON.parse(JSON.stringify(entry));
			}

			return Object.assign({}, state, {
				header: {
					...state.header
				},
				data: tempArray
			})
		default:
			return state
	}
}

export default toDoStore;
