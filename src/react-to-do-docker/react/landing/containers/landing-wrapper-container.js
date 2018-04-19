import { connect } from 'react-redux';
import LandingWrapper from '../components/landing-wrapper';

/*
	Wrapper to map the application's state and the landing-wrapper component
*/

// Pass into the component's properties whether or not the user is logged in
const mapStateToProps = (state) => {
  return {
    loggedIn:state.main.user.loggedIn
  }
}

// Connect the mapStateToProps function to the landing-wrapper component
const LandingWrapperContainer = connect(
  mapStateToProps
)(LandingWrapper)

export default LandingWrapperContainer;
