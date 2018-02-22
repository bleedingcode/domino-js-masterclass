import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { switchMenu } from '../core-actions';
import ToolbarMenuItem from '../components/toolbar-menu-item';

const mapStateToProps = (state, ownProps) => {
  return {
    dataClass:ownProps.dataClass,
    title:ownProps.title,
    dataId:ownProps.dataId,
    theme:state.main.theme
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ switchMenu }, dispatch);
}

const ToolbarMenuItemContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolbarMenuItem)

export default ToolbarMenuItemContainer;
