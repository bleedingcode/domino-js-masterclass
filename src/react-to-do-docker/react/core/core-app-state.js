import theme from '../themes/domino-js-theme';
import globals from '../globals';

/* 
 * Defines default state settings for the app - use domino-js-theme, close draw by default, title etc
 */

const mainAppState = {
	app:'home',
	previousApp:'',
	title:globals.title,
	drawerOpen:false,
	themeKey:'agilite',
	theme:theme,
	user:{
		loggedIn:false
	}
}

// Export this state holder object
export default mainAppState;
