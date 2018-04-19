import { connect } from 'react-redux';
import { confirmCancel, validateSubmit } from '../../to-do-logic';
import ToDoForm from '../../components/to-do-form/to-do-form';

/*
	Wrapper to map state and actions to the to-do-form component
*/

// Load properties into the component from state
const mapStateToProps = (state) => {
  return {
    state:state.toDo,
    theme:state.main.theme,
    storeList:state.toDo.storeList
  }
}

// Load actions for component
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

// Load properties and functions into the to-do-form
const ToDoFormContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoForm)

export default ToDoFormContainer;