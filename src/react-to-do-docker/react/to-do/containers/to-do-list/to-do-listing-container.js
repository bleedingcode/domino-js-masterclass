import { connect } from 'react-redux';
import { editProfile, deleteProfile, createProfile, filterList } from '../../to-do-actions';
import ToDoListing from '../../components/to-do-list/to-do-listing';

const getListing = (listing, listFilter) => {
  listFilter = listFilter.toLowerCase();

  if(listFilter === ""){
    return listing;
  }else{
    return listing.filter(t =>
      ((t.data.taskName.toLowerCase().indexOf(listFilter) > -1) ||
      (t.data.description.toLowerCase().indexOf(listFilter) > -1) ||
      (t.data.dueDate.toLowerCase().indexOf(listFilter) > -1) ||
      (t.data.priority.toLowerCase().indexOf(listFilter) > -1))
    )
  }
}

const mapStateToProps = (state) => {
  return {
    title:state.toDo.header.viewTitle,
    listing: getListing(state.toDo.data, state.toDo.header.listFilter),
    listFilter:state.toDo.header.listFilter,
    theme:state.main.theme,
    state:state.toDo
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    onCreateProfile: () => {
      dispatch(createProfile())
    },
    onEditProfile: (id) => {
      dispatch(editProfile(id))
    },
    onDeleteProfile: (id, state) => {
      dispatch(deleteProfile(id, state))
    },
    onChange: (key, value) => {
      dispatch(filterList(key, value))
    }
  }
}

const ToDoListingContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoListing)

export default ToDoListingContainer;
