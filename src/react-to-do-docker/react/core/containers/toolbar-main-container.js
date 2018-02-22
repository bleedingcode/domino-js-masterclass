import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { refreshView } from '../core-actions';
import ToolbarMain from '../components/toolbar-main';

const mapStateToProps = (state) => {
  return {
    app:state.main.app,
    title:state.main.title,
    loggedIn:state.main.user.loggedIn,
    theme:state.main.theme
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ refreshView }, dispatch);
}

const ToolbarMainContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolbarMain)

export default ToolbarMainContainer;
