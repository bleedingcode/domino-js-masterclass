import React from 'react';

import HomeLoading from '../components/home-loading';
import HomeAnonymousSignInContainer from '../containers/home-anonymous-sign-in-container';

class HomeAnonymous extends React.Component {
	render() {
  	return (
      <div>
			{ this.props.facet === 'loading' ? <HomeLoading /> : null }
			{ this.props.facet === 'sign-in' ? <HomeAnonymousSignInContainer /> : null }
      </div>
  	);
	}
}

export default HomeAnonymous;
