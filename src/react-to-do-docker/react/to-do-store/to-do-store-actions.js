import tempData from '../temp-data-store/temp-data';
import Globals from '../globals';
import { postPendingData } from '../core/core-logic';
import axios from 'axios';

/*
  ENUMS
*/
export const actions = {
  RESET_STATE:'RESET_STORE_STATE',
  CREATE_DOCUMENT: 'CREATE_STORE_PROFILE',
  EDIT_DOCUMENT: 'EDIT_STORE_PROFILE',
  DELETE_DOCUMENT: 'DELETE_STORE_PROFILE',
  CANCEL_DOCUMENT: 'CANCEL_STORE_PROFILE',
  SAVE_DOCUMENT: 'SAVE_STORE_PROFILE',
  FILTER_LIST: 'FILTER_STORE_LIST',
  FETCH_ALL_DATA: 'FETCH_ALL_STORE_DATA',
  UPDATE_DATA: 'UPDATE_STORE_DATA'
}

/*
  QUICK STATE FUNCTIONS
*/
export const resetState = () => {
  return {
    type: actions.RESET_STATE
  }
}

export const createProfile = () => {
  return {
    type: actions.CREATE_DOCUMENT
  }
}

export const editProfile = (id) => {
  return {
    type: actions.EDIT_DOCUMENT,
    id
  }
}

export const cancelProfile = () => {
  return {
    type: actions.CANCEL_DOCUMENT
  }
}

export const filterList = (key, value) => {
  return {
    type: actions.FILTER_LIST,
    key,
    value
  }
}

export const updateData = (data) => {
  return {
    type: actions.UPDATE_DATA,
    data
  }
}

/*
  ADVANCED STATE FUNCTIONS
*/
export const submitProfile = (state) => {
  return dispatch => {
    let tempArray = state.data.concat();
    let tempIndex = 0;
    let activeEntry = tempData.toDoStore.activeEntry;
    var entry = {};
    var dupArray = null;
    var reqType = "";

    //Finalise formatting of data
    activeEntry.data.title = _.trim(activeEntry.data.title);
    activeEntry.data.name = _.trim(activeEntry.data.name);

    //Change status of record to Pending
    tempData.toDoStore.activeEntry.custom.status = 'warning';

    if(activeEntry.custom.isNewDoc){
      //Add new Entry to State
      activeEntry.custom.isNewDoc = false;
      activeEntry.custom.action = "create";
      tempArray.push(activeEntry);

      //Add Entry to Queue
      tempData.toDoStore.data.push(JSON.parse(JSON.stringify(activeEntry)));
    }else{
      if(activeEntry.custom.isSavedDoc){
        activeEntry.custom.action = "update";
      }else{
        activeEntry.custom.action = "create";
      }

      //Update Entry in State
      tempIndex = tempArray.findIndex(t => t._id === activeEntry._id);
      tempArray[tempIndex] = activeEntry;

      //Add Entry to queue, or update existing entry in queue
      tempIndex = tempData.toDoStore.data.findIndex(t => t._id === activeEntry._id);

      if(tempIndex > -1){
        //Update the existing entry
        tempData.toDoStore.data[tempIndex] = activeEntry;
      }else{
        //Add Entry to queue
        tempData.toDoStore.data.push(JSON.parse(JSON.stringify(activeEntry)));
      }
    }

    dispatch({
      type: actions.SAVE_DOCUMENT,
      tempArray
    })

    dupArray = JSON.parse(JSON.stringify(tempData.toDoStore.data));

    for(var x in dupArray){
      entry = dupArray[x];
      reqType = entry.custom.action === "create" ? "2" : "3";

      let params = {
        reqType:reqType,
        socketId: Globals.user.socketId,
        username: Globals.user.username,
        password: Globals.user.password,
        record:entry
      };
      console.log(params);
      Globals.ws.emit('to-do-store-requests', params);
    }

    dupArray = null;
    tempData.toDoStore.data = [];
  }
}

export const deleteProfile = (id, state) => {
  return dispatch => {
    let tempEntry = {};
    let tempIndex = tempData.toDoStore.data.findIndex(t => t._id === id);
    let entry = {};
    let dupArray = null;

    if(tempIndex > -1){
      tempEntry = tempData.toDoStore.data[tempIndex];

      if(tempEntry.custom.isSavedDoc){
        //Change the action type of the entry to be "delete"
        tempEntry.custom.action = "delete";
      }else{
        //Remove the entry
        tempData.toDoStore.data.splice(tempIndex, 1);
      }
    }else{
      tempEntry = state.data.find(t => t._id === id);

      if(tempEntry){
        tempEntry.custom.action = "delete";
        tempData.toDoStore.data.push(JSON.parse(JSON.stringify(tempEntry)));
      }
    }

    dispatch({
      type: actions.DELETE_DOCUMENT,
      id
    })

    dupArray = JSON.parse(JSON.stringify(tempData.toDoStore.data));

    for(var x in dupArray){
      entry = dupArray[x];

      postPendingData("to-do-store", entry, "delete")
      .then(function (response) {
        if(response.data.success){
          entry.custom.action = "";
          entry.custom.status = "";

          dispatch(updateData(entry));
        }
      })
      .catch(function (error) {
        console.log(error);
        return null;
      });
    }

    dupArray = null;
    tempData.toDoStore.data = [];
  }
}

export const fetchAllData = () => {
    return dispatch => {
        let params = {
          reqType:"1",
          socketId: Globals.user.socketId,
          username: Globals.user.username,
          password: Globals.user.password
        };
      
        Globals.ws.emit('to-do-store-requests', params);  
    }
}

export const processWSResponse = (data) => {
  if(data.success){
    switch(data.reqType){
      case "1"://Fetch All Data
        //We need to add custom object to each record
        for(var x in data.data){
          data.data[x].custom = JSON.parse(JSON.stringify(tempData.toDoStore.dataTemplate.custom));
          data.data[x].custom.isSavedDoc = true;
          data.data[x].custom.isNewDoc = false;
          data.data[x].custom.status = ""
        }

        Globals.dispatch({type: actions.FETCH_ALL_DATA, payload:data.data});      
        break;
      case "2"://Submit Record Reponse
        if(data.success){
          data.data.custom.action = "";
          data.data.custom.isSavedDoc = true;
          data.data.custom.status = "";

          dispatch(updateData(data.data));
        }      
        break;
    }
  }
}