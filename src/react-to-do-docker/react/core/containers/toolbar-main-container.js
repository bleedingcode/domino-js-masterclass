import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { openDrawer, toggleDrawer, switchResourcesMenu, refreshView } from '../core-actions';
import ToolbarMain from '../components/toolbar-main';

/* 
 * Container for ToolbarMain component allowing us to separate UI from state
 */

/* 
 * Via redux, pushes variables from state.main (core-app-state.js) to properties of the toolbar-main component
 */
const mapStateToProps = (state) => {
	return {
		app: state.main.app,
		title: state.main.title,
		loggedIn: state.main.user.loggedIn,
		drawerOpen: state.main.drawerOpen,
		theme: state.main.theme
	}
}

// Create actions for opening the drawer and toggling the drawer. These become accessible via this.props.openDrawer and this.props.toggleDrawer
const mapDispatchToProps = (dispatch) => {
	return bindActionCreators({ openDrawer, toggleDrawer }, dispatch);
}

// Connect both to ToolbarMain component
const ToolbarMainContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(ToolbarMain)

export default ToolbarMainContainer;
