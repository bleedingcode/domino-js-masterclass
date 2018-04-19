import { connect } from 'react-redux';
import { confirmCancel, validateSubmit } from '../../to-do-store-logic';
import ToDoStoreForm from '../../components/to-do-store-form/to-do-store-form';

/*
	Wrapper to map state to the to-do-store-form component
*/

// Mapping the state to properties of the component
const mapStateToProps = (state) => {
  return {
    state:state.toDoStore,
    theme:state.main.theme
  }
}

// Actions for the to-do-store-form, calling functions in to-do-store-logic
const mapDispatchToProps = (dispatch) => {
  return {
    onCancelClick: () => {
      confirmCancel(dispatch)
    },
    onSubmitClick: (state) => {
      validateSubmit(dispatch, state)
    }
  }
}

// Loading properties and functions into the to-do-store-form
const ToDoStoreFormContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoStoreForm)

export default ToDoStoreFormContainer;
