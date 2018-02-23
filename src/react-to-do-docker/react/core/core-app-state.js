import theme from '../themes/domino-js-theme';
import globals from '../globals';

const mainAppState = {
	app:'home',
	previousApp:"",
	title:globals.title,
	drawerOpen:false,
	themeKey:'agilite',
	theme:theme,
	user:{
		loggedIn:false
	}
}

export default mainAppState;
