import tempData from '../temp-data-store/temp-data';

/*
  Enums used by the reducer on the landing page
*/
export const actions = {
	// Used by initHomePage below. Seems long-winded, but this is #BestPractice and ensures we're using strings as little as possible
	INIT_HOME_PAGE:'INIT_HOME_PAGE'
}

// Export a type associated to the function, to allow us to use it in a switch statement
export const initHomePage = () => {
	return {
		type: actions.INIT_HOME_PAGE
	}
}