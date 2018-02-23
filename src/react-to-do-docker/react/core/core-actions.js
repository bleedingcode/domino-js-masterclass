import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

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
  OPEN_DRAWER: 'OPEN_DRAWER',
  TOGGLE_DRAWER: 'TOGGLEDRAWER',  
  INIT_PREVIOUS_APP: 'INIT_PREVIOUS_APP',
  NULL:'NULL'
}

export const switchMenu = (dataId) => {
  return dispatch => {
    switch (dataId) {
    case "node-red"://Launch Node-RED and Continue
      dataId = null;
      window.open(Globals.nodeRedUrl);
    default:
      dispatch({
        type: actions.SWITCH_MENU,
        dataId
      })
    }
  }
}

export const openDrawer = () => {
  return {
    type: actions.OPEN_DRAWER
  }
}

export const toggleDrawer = () => {
  return {
    type: actions.TOGGLE_DRAWER
  }
}

export const initPreviousApp = () => {
  return dispatch => {
    dispatch({
      type: actions.INIT_PREVIOUS_APP
    })
  }
}

export const signInUser = () => {
  return dispatch => {
    let result = true;
    let htmlContent = "";
    let htmlStart = "<div><ul>"
    let htmlEnd = "</ul></div>"
    let html = "";
    let tmpDiv = document.getElementById('divMessages');
    let params = {};
    let username = tempData.signInForm.username;
    let password = tempData.signInForm.password;

    //First, validate Username
    if(!username || (username === "")){
      result = false;
      htmlContent += "<li>Please provide a Username</li>";
    }

    //Then, validate password
    if(!password || (password === "")){
      result = false;
      htmlContent += "<li>Please provide a Password</li>";
    }

    if(result){
      Globals.user.username = username;
      Globals.user.password = password;

      params = {
        reqType:"1",
        socketId: Globals.user.socketId,
        username,
        password
      };

      Globals.ws.emit('to-do-app-requests', params);
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

export const processSignInResult = (data) => {
  let htmlContent = "";
  let htmlStart = "<div><ul>"
  let htmlEnd = "</ul></div>"
  let html = "";
  let tmpDiv = document.getElementById('divMessages');  

  if(data.success){
    tempData.signInForm = {};
    Globals.user.commonName = data.data.commonName;

    Globals.dispatch({
      type: actions.SIGN_IN_USER
    })

  }else{
    Globals.user.username = "";
    Globals.user.password = "";

    for(var x in data.messages){
      htmlContent += `<li>${data.messages[x]}</li>`;
    }

    html = htmlStart + htmlContent + htmlEnd;
    tmpDiv.innerHTML = html;
  }
}