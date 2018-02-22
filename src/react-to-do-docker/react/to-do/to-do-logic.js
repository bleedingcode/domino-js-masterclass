import IOClient from 'socket.io-client';
import _ from 'lodash';

import { cancelProfile, submitProfile } from './to-do-actions';
import { postPendingData } from '../core/core-logic';
import tempData from '../temp-data-store/temp-data';

/*
  GLOBAL VARIABLES
*/
let toDoSocket = null;

//Establish Web Socket Connection
export const connectWebSocket = () => {
  toDoSocket = IOClient.connect({reconnect: true});

  toDoSocket.on('TestResult', function(msg){
    console.log("Result = " + msg);
  });

  toDoSocket.on('connect', function (socket) {
      console.log('Connected!');
      toDoSocket.emit('Test', 'Test Message');
  });

  return true;
}

//Disconnect Web Socket Connection when moving away from ToDo App
export const disconnectWebSocket = () => {
  console.log("To Do Web Socket Disconnected");
  toDoSocket.disconnect();
  toDoSocket = null;

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