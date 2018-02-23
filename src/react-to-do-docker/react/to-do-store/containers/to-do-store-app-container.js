import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import ToDoStoreApp from '../components/to-do-store-app';
import { resetState, fetchAllData } from '../to-do-store-actions';

const mapStateToProps = (state) => {
  return {
    facet:state.toDoStore.header.facet
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ resetState, fetchAllData }, dispatch);
}

const ToDoStoreAppContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoStoreApp)

export default ToDoStoreAppContainer;