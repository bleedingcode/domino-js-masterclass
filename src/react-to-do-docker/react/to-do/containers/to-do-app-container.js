import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import ToDoApp from '../components/to-do-app';
import { resetState, fetchAllData } from '../to-do-actions';

const mapStateToProps = (state) => {
  return {
    facet:state.toDo.header.facet
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ resetState, fetchAllData }, dispatch);
}

const ToDoAppContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoApp)

export default ToDoAppContainer;