import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import ToDoStoreApp from '../components/to-do-store-app';
import { resetState, fetchAllData } from '../to-do-store-actions';

/*
	Mapping state and actions to the to-do-store-app component
*/

// Loading which facet to display from state into component properties. There is a default in the to-do-store-state
const mapStateToProps = (state) => {
  return {
    facet:state.toDoStore.header.facet
  }
}

// Actions available on the component - resetState and fetchAllData
const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ resetState, fetchAllData }, dispatch);
}

// Binding properties and functions to to-do-store-app component
const ToDoStoreAppContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoStoreApp)

export default ToDoStoreAppContainer;