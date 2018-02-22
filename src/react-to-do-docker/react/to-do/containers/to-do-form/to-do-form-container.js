import { connect } from 'react-redux';
import { confirmCancel, validateSubmit } from '../../to-do-logic';
import ToDoForm from '../../components/to-do-form/to-do-form';


const mapStateToProps = (state) => {
  return {
    state:state.toDo,
    theme:state.main.theme
  }
}

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

const ToDoFormContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoForm)

export default ToDoFormContainer;
