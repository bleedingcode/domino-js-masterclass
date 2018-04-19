import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { signInUser } from '../../core/core-actions';
import HomeAnonymousSignIn from '../components/home-anonymous-sign-in';

/*
	Wrapper to map state to the home-anonymous-sign-in component
*/

// Pass the theme from state into the home-anonymous-sign-in component's properties
const mapStateToProps = (state) => {
  return {
    theme:state.main.theme
  }
}

// Pass the action for signing in to the dispatch() function, which talks to state
const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ signInUser }, dispatch);
}

// Connect the mapStateToProps and mapDispatchToProps functions to the home-anonymous-sign-in component
const HomeAnonymousSignInContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(HomeAnonymousSignIn)

export default HomeAnonymousSignInContainer;
