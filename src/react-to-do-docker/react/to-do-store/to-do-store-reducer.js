import toDoStoreState from './to-do-store-state';
import tempData from '../temp-data-store/temp-data';
import {actions} from './to-do-store-actions';

const toDoStore = (state = toDoStoreState, action) => {
  switch (action.type) {
    case actions.RESET_STATE:
      return Object.assign({}, state, toDoStoreState)
    case actions.CREATE_DOCUMENT:
      var tempEntry = JSON.parse(JSON.stringify(tempData.toDoStore.dataTemplate));

      tempEntry._id = new Date().getTime().toString();
      tempEntry.custom.tempId = tempEntry._id;
      tempData.toDoStore.activeEntry = tempEntry;

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'form'
        }
      })
    case actions.EDIT_DOCUMENT:
      var tempEntry = state.data.find(t => t._id === action.id);

      tempData.toDoStore.activeEntry = JSON.parse(JSON.stringify(tempEntry));

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
      tempData.toDoStore.activeEntry = {};

      return Object.assign({}, state, {
        header:{
          ...state.header,
          facet:'view'
        }
      })
    case actions.SAVE_DOCUMENT:
      tempData.toDoStore.activeEntry = {};

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
          ...state.header,
          dataLoaded:true
        },
        data:action.payload
      })
    case actions.RESET_LOADING:
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

export default toDoStore;
