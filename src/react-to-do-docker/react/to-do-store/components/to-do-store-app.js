import React from 'react';
import { connectWebSocket, disconnectWebSocket } from '../to-do-store-logic';
import tempData from '../../temp-data-store/temp-data';
import Globals from '../../globals';

/*
	Custom Components Import
*/
import ToDoStoreListingContainer from '../containers/to-do-store-list/to-do-store-listing-container';
import ToDoStoreFormContainer from '../containers/to-do-store-form/to-do-store-form-container';

class ToDoStoreApp extends React.Component {
	componentDidMount(){
		let props = this.props;
		//Pace.restart();

		connectWebSocket(function(){
			props.fetchAllData();

			if(tempData.globals.resetToDoStore){
				props.resetState();
				tempData.globals.resetToDoStore = false;
			}
		});
	}

	componentWillUnmount(){
		disconnectWebSocket();
	}

	render(){
		return(
		<div className="container-fluid" style={{marginTop:20}}>
			<div className="row">
				{ this.props.facet === "view" ? <ToDoStoreListingContainer /> : null }
				{ this.props.facet === "form" ? <ToDoStoreFormContainer /> : null }
			</div>
		</div>
		)
	}
}

export default ToDoStoreApp;
