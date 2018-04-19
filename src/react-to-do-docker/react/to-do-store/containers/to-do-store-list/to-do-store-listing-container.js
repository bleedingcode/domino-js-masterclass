import { connect } from 'react-redux';
import { editProfile, deleteProfile, createProfile, filterList } from '../../to-do-store-actions';
import ToDoStoreListing from '../../components/to-do-store-list/to-do-store-listing';

/*
	Wrapper to map state to the to-do-store-listing component
*/

// Function to filter the stores based on title, name or type
const getListing = (listing, listFilter) => {
  listFilter = listFilter.toLowerCase();

  if(listFilter === ""){
    return listing;
  }else{
    return listing.filter(t =>
      ((t.data.title.toLowerCase().indexOf(listFilter) > -1) ||
      (t.data.name.toLowerCase().indexOf(listFilter) > -1) ||
      (t.data.type.toLowerCase().indexOf(listFilter) > -1))
    )
  }
}

// Mapping the state to the properties of the component
const mapStateToProps = (state) => {
  return {
    title:state.toDoStore.header.viewTitle,
    listing: getListing(state.toDoStore.data, state.toDoStore.header.listFilter),
    listFilter:state.toDoStore.header.listFilter,
    theme:state.main.theme,
    state:state.toDoStore
  }
}

// Mapping functions for the component
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

// Mapping properties and functions to the to-do-store-list component
const ToDoStoreListingContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToDoStoreListing)

export default ToDoStoreListingContainer;
