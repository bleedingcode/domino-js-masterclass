import React from 'react';

/*
	Custom Components Import
*/
import ToolbarMainContainer from '../containers/toolbar-main-container';
import LandingWrapperContainer from '../../landing/containers/landing-wrapper-container';
import ToDoAppContainer from '../../to-do/containers/to-do-app-container';
import ToDoStoreAppContainer from '../../to-do-store/containers/to-do-store-app-container';

/*
The main component for our app, including toolbar and each page
*/
class MainApp extends React.Component {

	// Render the page, including which "app" or "view" in the Domino sense to display
	render() {
		return (
			<div>
				<ToolbarMainContainer />
				{this.props.app === 'home' ? <LandingWrapperContainer /> : null}
				{this.props.app === 'to-do-new' ? <ToDoAppContainer /> : null}
				{this.props.app === 'to-do-complete' ? <ToDoAppContainer /> : null}
				{this.props.app === 'to-do-overdue' ? <ToDoAppContainer /> : null}
				{this.props.app === 'to-do-store' ? <ToDoStoreAppContainer /> : null}
			</div>
		);
	}
}

export default MainApp;