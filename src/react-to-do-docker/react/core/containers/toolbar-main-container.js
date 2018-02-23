import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { openDrawer, toggleDrawer, switchResourcesMenu, refreshView } from '../core-actions';
import ToolbarMain from '../components/toolbar-main';

const mapStateToProps = (state) => {
  return {
    app:state.main.app,
    title:state.main.title,
    loggedIn:state.main.user.loggedIn,
    drawerOpen:state.main.drawerOpen,
    theme:state.main.theme
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ openDrawer, toggleDrawer }, dispatch);
}

const ToolbarMainContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolbarMain)

export default ToolbarMainContainer;
