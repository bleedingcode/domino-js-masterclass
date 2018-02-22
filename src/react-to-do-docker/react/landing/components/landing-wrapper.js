import React from 'react';

/*
	Custom Components Import
*/
import HomeAnonymousContainer from '../containers/home-anonymous-container';
import HomeLoggedIn from './home-logged-in';

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
