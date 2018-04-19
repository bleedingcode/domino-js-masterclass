import React from 'react';

/*
	Custom Components Import
*/
import HomeAnonymousContainer from '../containers/home-anonymous-container';
import HomeLoggedIn from './home-logged-in';

/*
	This is the main component for the landing page.
	If logged in
		-> display home-logged-in component.
	If not
		-> display home-anonymous container / component
		   if facet == 'loading'
			   -> display home-loading component
		   if facet == 'signin'
			   -> display home-anonymous-signin container / component
			
*/
class LandingWrapper extends React.Component {
	render(){
		return(
			<div className="container-fluid" style={{marginTop:20}}>
				<div className="row">
					{ this.props.loggedIn ? <HomeLoggedIn /> : <HomeAnonymousContainer /> }
    		</div>
			</div>
		)
	}
}

export default LandingWrapper;
