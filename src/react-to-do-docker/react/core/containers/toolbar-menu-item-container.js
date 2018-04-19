import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { switchMenu } from '../core-actions';
import ToolbarMenuItem from '../components/toolbar-menu-item';

/* 
 * Toolbar menu item container, for redux
*/

// Pass into the component's properties the theme from the state and some non-state properties defined by the developer on the implementation
const mapStateToProps = (state, ownProps) => {
	return {
		dataClass:ownProps.dataClass,
		title:ownProps.title,
		dataId:ownProps.dataId,
		theme:state.main.theme
	}
}

// Create an action for switchMenu, to change the "view"
const mapDispatchToProps = (dispatch) => {
	return bindActionCreators({ switchMenu }, dispatch);
}

// Connect both to ToolbarMenuItem component
const ToolbarMenuItemContainer = connect(
	mapStateToProps,
	mapDispatchToProps
)(ToolbarMenuItem)

export default ToolbarMenuItemContainer;
