import toDoState from './to-do-state';
import tempData from '../temp-data-store/temp-data';
import {actions} from './to-do-actions';
import Moment from 'moment';
import Globals from '../globals';

const toDo = (state = toDoState, action) => {
  switch (action.type) {
    case actions.RESET_STATE:
      return Object.assign({}, state, toDoState)
    case actions.CREATE_DOCUMENT:
      var tempEntry = JSON.parse(JSON.stringify(tempData.toDo.dataTemplate));

      tempEntry._id = new Date().getTime().toString();
      tempEntry.custom.tempId = tempEntry._id;
      tempEntry.data.author = Globals.user.commonName;
      
      tempData.toDo.activeEntry = tempEntry;

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'form'
        }
      })
    case actions.EDIT_DOCUMENT:
      var tempEntry = state.data.find(t => t._id === action.id);

      tempData.toDo.activeEntry = JSON.parse(JSON.stringify(tempEntry));

      if(tempData.toDo.activeEntry.data.dueDate !== ""){
        tempData.toDo.activeEntry.data.dueDate = Moment(tempData.toDo.activeEntry.data.dueDate).toDate();
      }

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'form'
        }
      })
    case actions.DELETE_DOCUMENT:
      var tmpIndex = state.data.findIndex(t => t._id === action.id);
      var tmpArray = state.data.concat();
      tmpArray.splice(tmpIndex, 1);

      return Object.assign({}, state, {
        header:{
          ...state.header
        },
        data:tmpArray
      })
    case actions.CANCEL_DOCUMENT:
      tempData.toDo.activeEntry = {};

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'view'
        }
      })
    case actions.SAVE_DOCUMENT:
      tempData.toDo.activeEntry = {};

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'view'
        },
        data:action.tempArray
      })
    case actions.FILTER_LIST:
      var listFilter = state.header.listFilter;

      switch(action.key){
        case "listFilter":
          listFilter = action.value;
          break;
      }

      return Object.assign({}, state, {
        header:{
          ...state.header,
          listFilter:listFilter
        }
      })
    case actions.FETCH_ALL_DATA:
	// Pass all the data fetched via Node-RED into the app and confirm we're ready for "New" button to display
      return Object.assign({}, state, {
        header:{
          ...state.header,
          dataLoaded:true
        },
		data:action.payload.data,
		// Also update storeList from database
        storeList:action.payload.storeList
      })
    case actions.RESET_LOADING:
		// Set that data is not yet loaded. This is done on switching a menu in core-actions and is used to hide "New" button
      return Object.assign({}, state, {
        header:{
          ...state.header,
          dataLoaded:false
        }
      })
    case actions.UPDATE_DATA:
      var tempIndex = 0;
      var entry = {};
      var tempArray = state.data.concat();
      var canDelete = false;

      entry = action.data;

      //We need to see if we have to find doc by its temp id or main id
      tempIndex = -1;

      if(entry.custom.tempId){
        tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry.custom.tempId.toLowerCase());
      }

      if(tempIndex < 0){
        tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry._id.toLowerCase());
      }

      if(tempIndex >= 0){
        //Check if the doc needs to be updated or removed
        switch(Globals.appKey){
          case "to-do-new":
            if(entry.data.status !== "Active"){
              canDelete = true;
            }
            break;
          case "to-do-complete":
            if(entry.data.status !== "Complete"){
              canDelete = true;
            }
            break;  
          case "to-do-overdue":
            if(entry.data.status !== "Overdue"){
              canDelete = true;
            }
            break;                          
        }

        if(canDelete){
          tempArray.splice(tempIndex, 1);
        }else{
          tempArray[tempIndex] = JSON.parse(JSON.stringify(entry));
        }
      }

      return Object.assign({}, state, {
        header:{
          ...state.header
        },
        data:tempArray
      })
    case actions.UPDATE_UI_DATA:
      var tempIndex = 0;
      var entry = {};
      var tempArray = state.data.concat();
      var canDelete = false;

      //Loop Through List
      for(var x in action.data){
        entry = action.data[x];
        tempIndex = -1;
        canDelete = false;

        if(entry.custom.tempId){
          tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry.custom.tempId.toLowerCase());
        }

        if(tempIndex < 0){
          tempIndex = tempArray.findIndex(t => t._id.toLowerCase() === entry._id.toLowerCase());
        }

        if(tempIndex >= 0){
          //Check if the doc needs to be updated or removed
          switch(Globals.appKey){
            case "to-do-new":
              if(entry.data.status !== "Active"){
                canDelete = true;
              }
              break;
            case "to-do-complete":
              if(entry.data.status !== "Complete"){
                canDelete = true;
              }
              break;  
            case "to-do-overdue":
              if(entry.data.status !== "Overdue"){
                canDelete = true;
              }
              break;                          
          }

          if(canDelete){
            tempArray.splice(tempIndex, 1);
          }else{
            tempArray[tempIndex] = JSON.parse(JSON.stringify(entry));
          }
        }else{
          //It's a new doc that needs to be inserted if it's the correct type
          switch(Globals.appKey){
            case "to-do-new":
              if(entry.data.status === "Active"){
                tempArray.push(JSON.parse(JSON.stringify(entry)));
              }
              break;
            case "to-do-complete":
              if(entry.data.status === "Complete"){
                tempArray.push(JSON.parse(JSON.stringify(entry)));
              }
              break;  
            case "to-do-overdue":
              if(entry.data.status === "Overdue"){
                tempArray.push(JSON.parse(JSON.stringify(entry)));
              }
              break;                          
          }
        }
      }

      return Object.assign({}, state, {
        header:{
          ...state.header
        },
        data:tempArray
      })      
    default:
      return state
  }
}

export default toDo;
