import landingState from './landing-state';
import {actions} from './landing-actions';
import tempData from '../temp-data-store/temp-data';

const landing = (state = landingState, action) => {
  switch (action.type) {
    case actions.INIT_HOME_PAGE:
      tempData.signInForm = {}
      
      return Object.assign({}, state, {
        facet:"sign-in"
      });
    default:
      return state
  }
}

export default landing;
