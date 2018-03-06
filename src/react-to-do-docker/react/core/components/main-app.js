import React from 'react';

/*
	Custom Components Import
*/
import ToolbarMainContainer from '../containers/toolbar-main-container';
import LandingWrapperContainer from '../../landing/containers/landing-wrapper-container';
import ToDoAppContainer from '../../to-do/containers/to-do-app-container';
import ToDoStoreAppContainer from '../../to-do-store/containers/to-do-store-app-container';

class MainApp extends React.Component {
	render() {
  	return (
	  <div>
	  	<ToolbarMainContainer />
			{ this.props.app === 'home' ? <LandingWrapperContainer /> : null }
			{ this.props.app === 'to-do-new' ? <ToDoAppContainer /> : null }
			{ this.props.app === 'to-do-assigned' ? <ToDoAppContainer /> : null }
			{ this.props.app === 'to-do-complete' ? <ToDoAppContainer /> : null }
			{ this.props.app === 'to-do-overdue' ? <ToDoAppContainer /> : null }
			{ this.props.app === 'to-do-store' ? <ToDoStoreAppContainer /> : null }
	  </div>
  	);
	}
}

export default MainApp;