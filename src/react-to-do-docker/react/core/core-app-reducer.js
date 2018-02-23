import mainAppState from './core-app-state';
import {actions} from './core-actions';
import tempData from '../temp-data-store/temp-data';

const main = (state = mainAppState, action) => {
  switch (action.type) {
    case actions.OPEN_DRAWER:
      return Object.assign({}, state, {
          drawerOpen:true
      });
    case actions.TOGGLE_DRAWER:
      let status = !state.drawerOpen;
      return Object.assign({}, state, {
          drawerOpen:status
      });    
    case actions.SWITCH_MENU:
      let title = state.title;
      let app = state.app;
      var prevApp = "";

      if(action.dataId === null){
        return Object.assign({}, state, {
            drawerOpen:false
        });
      }

      switch(action.dataId){
        case "home":
          app = "home";
          title = "React To Do Portal";
          break;
        case "stores-all":
          app = "to-do-store";
          title = "Stores";
          break;
        case "to-do-all":
          app = "to-do"; 
          title = "To Dos";
          break;
        default:
          prevApp = state.app;
          break;
      }

      return Object.assign({}, state, {
          app,
          previousApp:prevApp,
          title:title,
          drawerOpen:false,
          landingPage:{
            facet:'index'
          }
      });
    case actions.SIGN_IN_USER:
      return Object.assign({}, state, {
          user: {
            loggedIn:true
          }
      });
    case actions.SIGN_OUT_USER:
      tempData.globals.resetToDo = true;

      return Object.assign({}, state, mainAppState);
    case actions.INIT_PREVIOUS_APP:
      var prevApp = state.previousApp;

      return Object.assign({}, state, {
          app:prevApp,
          previousApp:''
      });
    default:
      return state
  }
}

export default main;
