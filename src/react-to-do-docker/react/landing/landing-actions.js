import tempData from '../temp-data-store/temp-data';

/*
  ENUMS
*/
export const actions = {
  INIT_HOME_PAGE:'INIT_HOME_PAGE'
}

export const initHomePage = () => {
  return {
    type: actions.INIT_HOME_PAGE
  }
}