import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware, combineReducers } from 'redux';
import thunk from 'redux-thunk';
import {userVerifiedSuccess, userVerifiedFailed, checkUserToken, actions} from './core-actions';
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
import landing from '../landing/landing-reducer';
import MainAppContainer from './containers/main-app-container'

//Setup Reducers
const indexReducerWrapper = combineReducers({
  main,
  toDo,
  landing
});

//Create Store with Middleware
const createStoreWithMiddleware = applyMiddleware(thunk)(createStore);

let store = createStoreWithMiddleware(indexReducerWrapper,
	window.devToolsExtension ? window.devToolsExtension() : undefined
)

//CHECKS FOR WHEN APP LOADS
//Check if User can Auto Sign In
const initLogin = () => {
  localStorage.removeItem('token');
  tempData.userProfile = {};
  tempData.adminData = {};

  store.dispatch({type: actions.SIGN_OUT_USER});
  store.dispatch({type: landingActions.INIT_HOME_PAGE});

  // const token = localStorage.getItem('token');

  // let userCheck = checkUserToken(token)
  // .then(response => {
  //   tempData.adminData = response.data.adminData;
  //   store.dispatch({type: actions.SIGN_IN_USER})
  // })
  // .catch(response => {
  //   localStorage.removeItem('token');
  //   tempData.userProfile = {};
  //   tempData.adminData = {};

  //   store.dispatch({type: actions.SIGN_OUT_USER});
  //   store.dispatch({type: landingActions.INIT_HOME_PAGE});
  // })
}

//Check if there are url parameters
var url = new URL(location.href);
var params = getQueryParams(url.search);

if(params.a){
  //We need to perform an action
  switch (params.a) {
    case "r"://Registration Form
      store.dispatch({type: "INIT_REGISTER_FORM", key:params.key});

      break;
    case "pr"://Password Reset
    if(params.key){
      store.dispatch({type: "INIT_RESET_FORM", key:params.key});
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