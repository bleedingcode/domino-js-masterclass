import IOClient from 'socket.io-client';
import _ from 'lodash';
import { processWSResponse } from './to-do-store-actions';

import { cancelProfile, submitProfile } from './to-do-store-actions';
import { postPendingData } from '../core/core-logic';
import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

export const connectWebSocket = (callback) => {
  Globals.ws = IOClient.connect(Globals.wsUrl, {reconnect: true});

  Globals.ws.on('connect', function (socket) {
    console.log("Store Web Socket Connected!");

    Globals.ws.on('init-user-session', function (id) {
      Globals.user.socketId = id;
      callback();  
    });
  
    Globals.ws.on('to-do-store-response', processWSResponse);  
  });

  return null;
}

export const disconnectWebSocket = () => {
  console.log("Store Web Socket Disconnected");
  Globals.ws.disconnect();
  Globals.ws = null;

  return true;
}

export const confirmCancel = (dispatch) => {
  dispatch(cancelProfile());
  return true;
}

//Validate Form before submitting
export const validateSubmit = (dispatch, state) => {
  let result = true;
  let result2 = true;
  let htmlContent = "";
  let htmlStart = "<div><ul>"
  let htmlEnd = "</ul></div>"
  let html = "";
  let entry;

  entry = tempData.toDoStore.activeEntry.data;

  //Validate Fields
  if(entry.title === ""){
    result = false;
    htmlContent += "<li>Please provide a Title</li>";
  }

  if(entry.name === ""){
    result = false;
    htmlContent += "<li>Please provide a Name</li>";
  }

  if(entry.type === ""){
    result = false;
    htmlContent += "<li>Please provide a Type</li>";
  }

  /*
   *FINALISE
   */
  let tmpDiv = document.getElementById('divMessages');

  if(result){
    tmpDiv.innerHTML = "";
    dispatch(submitProfile(state));
  }else{
    html = htmlStart + htmlContent + htmlEnd;
    tmpDiv.innerHTML = html;
  }
}