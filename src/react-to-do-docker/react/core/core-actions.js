import tempData from '../temp-data-store/temp-data';
import globals from '../globals';

import axios from 'axios';
import {actions as landingActions} from '../landing/landing-actions';
import {toProperCase} from '../utilities';
import validate from 'validate.js';
import _ from 'lodash';

import {loadDataExtended as loadDataExtendedToDo} from '../to-do/to-do-actions';

export const actions = {
  SWITCH_MENU: 'SWITCH_MENU',
  SIGN_IN_USER: 'SIGN_IN_USER',
  SIGN_OUT_USER: 'SIGN_OUT_USER',
  INIT_PREVIOUS_APP: 'INIT_PREVIOUS_APP',
  NULL:'NULL'
}

export const switchMenu = (dataId) => {
  return dispatch => {
    switch (dataId) {
    case "node-red"://Launch Node-RED and Continue
      dataId = null;
      window.open(globals.nodeRedUrl);
    default:
      dispatch({
        type: actions.SWITCH_MENU,
        dataId
      })
    }
  }
}

export const signOutUser = () => {
  return dispatch => {
    localStorage.removeItem('token');
    tempData.userProfile = {};

    dispatch({
      type: actions.SIGN_OUT_USER
    })
    dispatch({
      type: landingActions.INIT_HOME_PAGE
    })
  }
}

export const initPreviousApp = () => {
  return dispatch => {
    dispatch({
      type: actions.INIT_PREVIOUS_APP
    })
  }
}

export const refreshView = (app) => {
  return dispatch => {
    switch(app){
      case "to-do":
        loadDataExtendedToDo(dispatch);
        break;     
    }
  }  
}

export const checkUserToken = (token) => {
    return axios.get(globals.apiUrl, {
      headers:{"api-key":token}
    });
}

export const signInUser = () => {
  return dispatch => {
    let result = true;
    let htmlContent = "";
    let htmlStart = "<div><ul>"
    let htmlEnd = "</ul></div>"
    let html = "";
    let tmpDiv = document.getElementById('divMessages');
    let constraints = {};
    let validMessage = "";

    let email = tempData.signInForm.email;
    let password = tempData.signInForm.password;

    //First, validate email address
    if(!email || (email === "")){
      result = false;
      htmlContent += "<li>Please provide an Email Address</li>";
    }else{
      //Check if value is a valid email address
      constraints = {username: {email:true}};
      validMessage = validate({username: email}, constraints);

      if(validMessage){
          result = false;
          htmlContent += "<li>Email is not a valid address</li>";
      }
    }

    //Then, validate password
    if(!password || (password === "")){
      result = false;
      htmlContent += "<li>Please provide a Password</li>";
    }

    if(result){
      //Finalise formatting of the values
      tempData.signInForm.email = _.toLower(email);

      //Authenticate User
      axios.post(`${globals.apiUrl}/user/authenticate`, tempData.signInForm)
      .then(response => {
        localStorage.setItem('token', response.data.data.apiKey);

        tempData.userProfile = {
          firstName:response.data.data.firstName,
          lastName:response.data.data.lastName,
          email:response.data.data.email,
        };

        tempData.signInForm = {};

        dispatch({
          type: actions.SIGN_IN_USER
        })
      })
      .catch(err => {
        result = false;

        for(var x in err.response.data.messages){
          htmlContent += `<li>${err.response.data.messages[x]}</li>`;
        }

        html = htmlStart + htmlContent + htmlEnd;
        tmpDiv.innerHTML = html;
      })
    }

    //Finalise
    if(result){
      html = "";
    }else{
      html = htmlStart + htmlContent + htmlEnd;
    }

    tmpDiv.innerHTML = html;
  }
}