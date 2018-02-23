import { connect } from 'react-redux';
import { confirmCancel, validateSubmit } from '../../to-do-store-logic';
import ToDoStoreForm from '../../components/to-do-store-form/to-do-store-form';


const mapStateToProps = (state) => {
  return {
    state:state.toDoStore,
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

const ToDoStoreFormContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoStoreForm)

export default ToDoStoreFormContainer;
