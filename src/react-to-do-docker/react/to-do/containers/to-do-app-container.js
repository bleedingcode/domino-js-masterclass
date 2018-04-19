import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import ToDoApp from '../components/to-do-app';
import { resetState, fetchAllData } from '../to-do-actions';

/*
	Mapping state and actions to the to-do-app component
*/

// Map state to properties of the component. The app will be e.g. to-do-new, to-do-complete etc. Facet will be for whether to show form or view
const mapStateToProps = (state) => {
  return {
    facet:state.toDo.header.facet,
    app:state.main.app
  }
}

// Map functions to component
const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ resetState, fetchAllData }, dispatch);
}

// Connect properties and functions to the to-do-app component
const ToDoAppContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoApp)

export default ToDoAppContainer;