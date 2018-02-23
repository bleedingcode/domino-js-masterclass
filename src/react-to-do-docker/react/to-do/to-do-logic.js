import IOClient from 'socket.io-client';
import _ from 'lodash';

import { cancelProfile, submitProfile } from './to-do-actions';
import { postPendingData } from '../core/core-logic';
import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';

/*
  GLOBAL VARIABLES
*/
let ws = null;

//Establish Web Socket Connection
export const connectWebSocket = () => {
  ws = IOClient.connect(Globals.wsUrl, {reconnect: true});

  ws.on('to-do-responses', function(msg){
    console.log("To Do Responses");
    console.log(msg);
  });

  ws.on('connect', function (socket) {
    console.log("To Do Web Socket Connected!");
  });

  ws.on('init-user-session', function (id) {
    console.log("To Do Init User Session");
    Globals.user.socketId = id;
  });

  ws.on('to-do-requests', function (data) {
    console.log("To Do Requests");
    console.log(data);
  });

  return null;
}

//Disconnect Web Socket Connection when moving away from ToDo App
export const disconnectWebSocket = () => {
  console.log("To Do Web Socket Disconnected");
  ws.disconnect();
  ws = null;

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

  entry = tempData.toDo.activeEntry.data;

  //First Check Key is not blank
  if(entry.taskName === ""){
    result = false;
    htmlContent += "<li>Please provide a Task Name</li>";
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