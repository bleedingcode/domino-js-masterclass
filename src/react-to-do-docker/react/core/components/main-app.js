import React from 'react';

/*
	Custom Components Import
*/
import ToolbarMainContainer from '../containers/toolbar-main-container';
import LandingWrapperContainer from '../../landing/containers/landing-wrapper-container';
import ToDoAppContainer from '../../to-do/containers/to-do-app-container';

class MainApp extends React.Component {
	render() {
  	return (
	  <div>
	  	<ToolbarMainContainer />
			{ this.props.app === 'home' ? <LandingWrapperContainer /> : null }
			{ this.props.app === 'to-do' ? <ToDoAppContainer /> : null }
	  </div>
  	);
	}
}

export default MainApp;
