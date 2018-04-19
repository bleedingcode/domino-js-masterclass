import landingState from './landing-state';
import {actions} from './landing-actions';
import tempData from '../temp-data-store/temp-data';

/*
 * redux reducer, which runs the code behind the landing-actions and returns an updated instance of the landing-state object
*/

const landing = (state = landingState, action) => {
	switch (action.type) {
	case actions.INIT_HOME_PAGE:
		// The only action implemented. Clear the signin data in the store
		tempData.signInForm = {}
	  
		// Once we've loaded the sign-in page, return a new landing-state object with the updated facet to display it
		return Object.assign({}, state, {
			facet:'sign-in'
		});
	default:
		return state
	}
}

export default landing;
