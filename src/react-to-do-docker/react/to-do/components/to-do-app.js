import React from 'react';
import { connectWebSocket, disconnectWebSocket } from '../to-do-logic';
import tempData from '../../temp-data-store/temp-data';

/*
	Custom Components Import
*/
import ToDoListingContainer from '../containers/to-do-list/to-do-listing-container';
import ToDoFormContainer from '../containers/to-do-form/to-do-form-container';

class ToDoApp extends React.Component {
	componentDidMount(){
		Pace.restart();
		//connectWebSocket();
		this.props.fetchAllData();

		if(tempData.globals.resetToDo){
			this.props.resetState();
			tempData.globals.resetToDo = false;
		}
	}

	// componentWillUnmount(){
	// 	disconnectWebSocket();
	// }

	render(){
		return(
      <div className="container-fluid" style={{marginTop:20}}>
				<div className="row">
  				{ this.props.facet === "view" ? <ToDoListingContainer /> : null }
          { this.props.facet === "form" ? <ToDoFormContainer /> : null }
  			</div>
      </div>
		)
	}
}

export default ToDoApp;
