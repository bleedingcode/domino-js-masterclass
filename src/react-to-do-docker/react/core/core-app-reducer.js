import mainAppState from './core-app-state';
import { actions } from './core-actions';
import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

/*
 The reducer, which contains all our applications actions. These should all be "pure functions", meaning they do not change (mutate) an object. Instead they take an object containing the previous state and returns a copy of that object updated to the new state. E.g. an "approve" function would not change the object being approved. It would create a new copy of that object, set its status to "Approved" and return the new object.
 
 Object.assign() does this. We pass in an empty object for a start, pass in the current state, then define new property values. The current state (param 2) gets copied into a new object (param 1), and properties of the new object changed according to param 3. The new object (param 1) is returned.

 This means processes can be replayed as required. See https://redux.js.org/basics/reducers
*/

// Reducers for updating mainAppState in core-app-state
const main = (state = mainAppState, action) => {
	switch (action.type) {
		case actions.OPEN_DRAWER:
			// Set drawer status to open, triggered from toolbar-main
			return Object.assign({}, state, {
				drawerOpen: true
			});
		case actions.TOGGLE_DRAWER:
			// Set drawer status to opposite of current, triggered from toolbar-main
			let status = !state.drawerOpen;
			return Object.assign({}, state, {
				drawerOpen: status
			});
		case actions.SWITCH_MENU:
			// Change the current page being displayed, triggered from toolbar-menu-item
			let title = state.title;
			let app = state.app;
			var prevApp = '';

			if (action.dataId === null) {
				// Just close the drawer
				return Object.assign({}, state, {
					drawerOpen: false
				});
			}

			// Change the app or "view" depending on the dataId property of the toolbar-menu-item
			switch (action.dataId) {
				case 'home':
					app = 'home';
					title = 'React To Do Portal';
					break;
				case 'stores-all':
					app = 'to-do-store';
					title = 'Stores';
					break;
				case 'to-do-new':
					app = action.dataId;
					title = 'To Dos - Active';
					break;
				case 'to-do-complete':
					app = action.dataId;
					title = 'To Dos - Complete';
					break;
				case 'to-do-overdue':
					app = action.dataId;
					title = 'To Dos - Overdue';
					break;
				default:
					// Unanticipated new page
					prevApp = state.app;
					break;
			}

			// Globals also holds the current page
			Globals.appKey = app;

			// Return an updated state with current page, previous page, new title, draw closed and landing page set to display the index
			return Object.assign({}, state, {
				app,
				previousApp: prevApp,
				title: title,
				drawerOpen: false,
				landingPage: {
					facet: 'index'
				}
			});
		case actions.SIGN_IN_USER:
			// User has successfully signed in, called from core-actions processSignInResult
			return Object.assign({}, state, {
				user: {
					loggedIn: true
				}
			});
		case actions.SIGN_OUT_USER:
			// User has successfully signed out, reser todos. Not implemented for this app
			tempData.globals.resetToDo = true;

			// Reset current state to initial state
			return Object.assign({}, state, mainAppState);
		case actions.INIT_PREVIOUS_APP:
			// Roll back to previous page. Not implemented for this app
			var prevAppInt = state.previousApp;

			return Object.assign({}, state, {
				app: prevAppInt,
				previousAppInt: ''
			});
		default:
			// No change
			return state
	}
}

export default main;
