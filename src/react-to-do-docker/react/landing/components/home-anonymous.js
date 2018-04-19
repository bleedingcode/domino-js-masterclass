import React from 'react';

import HomeLoading from '../components/home-loading';
import HomeAnonymousSignInContainer from '../containers/home-anonymous-sign-in-container';

/*
	Component for anonymous page. If we're loading something, display the loading facet. Otherwise display the sign-in form.
*/

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
