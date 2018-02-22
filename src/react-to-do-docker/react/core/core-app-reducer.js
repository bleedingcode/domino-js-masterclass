import mainAppState from './core-app-state';
import {actions} from './core-actions';
import tempData from '../temp-data-store/temp-data';

const main = (state = mainAppState, action) => {
  switch (action.type) {
    case actions.SWITCH_MENU:
      let title = state.title;
      var prevApp = "";

      if(action.dataId === null){
        return Object.assign({}, state, mainAppState);
      }

      switch(action.dataId){
        case "home":
          title = "Agilit-e Admin Portal";
          break;
        case "keywords":
          title = "Agilit-e Keywords";
          break;
        case "numbering":
          title = "Agilit-e Numbering";
          break;
        case "connectors":
          title = "Agilit-e Connectors";
          break;
        case "data-mapping":
          title = "Agilit-e Data Mapping";
          break;
        case "templates":
          title = "Agilit-e Templates";
          break;
        case "bpm":
          title = "Agilit-e BPM (Business Process Management)";
          break;
        case "roles":
          title = "Agilit-e Roles";
          break;                    
        case "log-profiles":
          title = "Agilit-e Log Profiles";
          break;
        default:
          prevApp = state.app;
          title = "Agilit-e Administration";
          break;
      }

      return Object.assign({}, state, {
          app: action.dataId,
          previousApp:prevApp,
          title:title,
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
