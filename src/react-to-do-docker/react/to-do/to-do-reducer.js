import toDoState from './to-do-state';
import tempData from '../temp-data-store/temp-data';
import {actions} from './to-do-actions';

const toDo = (state = toDoState, action) => {
  switch (action.type) {
    case actions.RESET_STATE:
      return Object.assign({}, state, toDoState)
    case actions.CREATE_DOCUMENT:
      var tempEntry = JSON.parse(JSON.stringify(tempData.toDo.dataTemplate));

      tempEntry._id = new Date().getTime().toString();
      tempEntry.custom.tempId = tempEntry._id;
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
      return Object.assign({}, state, {
        header:{
          ...state.header
        },
        data:action.payload
      })
    case actions.UPDATE_DATA:
      var tempIndex = 0;
      var entry = {};
      var tempArray = state.data.concat();

      entry = action.data;

      //We need to see if we have to find doc by its temp id or main id
      tempIndex = -1;

      if(entry.custom.tempId){
        tempIndex = tempArray.findIndex(t => t._id === entry.custom.tempId);
      }

      if(tempIndex < 0){
        tempIndex = tempArray.findIndex(t => t._id === entry._id);
      }

      if(tempIndex >= 0){
        tempArray[tempIndex] = JSON.parse(JSON.stringify(entry));
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
