import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware, combineReducers } from 'redux';
import thunk from 'redux-thunk';
import {userVerifiedSuccess, userVerifiedFailed, actions} from './core-actions';
import {actions as landingActions} from '../landing/landing-actions';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import tempData from '../temp-data-store/temp-data';
import {getQueryParams} from '../utilities';
import Globals from '../globals';

/*
  React Tap Plugin for Tap events on mobile
*/
import injectTapEventPlugin from 'react-tap-event-plugin';
injectTapEventPlugin();

/*
	Custom Components Import
*/
import main from './core-app-reducer';
import toDo from '../to-do/to-do-reducer';
import toDoStore from '../to-do-store/to-do-store-reducer';
import landing from '../landing/landing-reducer';
import MainAppContainer from './containers/main-app-container'

//Setup Reducers
const indexReducerWrapper = combineReducers({
	main,
	toDo,
	toDoStore,
	landing
});

//Create Store with Middleware
const createStoreWithMiddleware = applyMiddleware(thunk)(createStore);

let store = createStoreWithMiddleware(indexReducerWrapper,
	window.devToolsExtension ? window.devToolsExtension() : undefined
)

//TODO: We need to see if this is going to work
Globals.dispatch = store.dispatch;

//CHECKS FOR WHEN APP LOADS
const initLogin = () => {
	store.dispatch({type: actions.SIGN_OUT_USER});
	store.dispatch({type: landingActions.INIT_HOME_PAGE});
}

//Check if there are url parameters
var url = new URL(location.href);
var params = getQueryParams(url.search);

if(params.a){
	//We need to perform an action
	switch (params.a) {
	case 'r'://Registration Form
		store.dispatch({type: 'INIT_REGISTER_FORM', key:params.key});

		break;
	case 'pr'://Password Reset
		if(params.key){
			store.dispatch({type: 'INIT_RESET_FORM', key:params.key});
		}else{
			initLogin();
		}

		break;      
	default:
		initLogin();
	}
}else{
	initLogin();
}

ReactDOM.render(
	<Provider store={store}>
		<MuiThemeProvider>
			<MainAppContainer  />
		</MuiThemeProvider>
	</Provider>,
	document.getElementById('react-main')
)