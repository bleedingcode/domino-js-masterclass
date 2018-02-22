import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { signInUser } from '../../core/core-actions';
import { initRegisterForm, initForgotPasswordForm } from '../landing-actions';
import HomeAnonymousSignIn from '../components/home-anonymous-sign-in';

const mapStateToProps = (state) => {
  return {
    theme:state.main.theme
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ signInUser, initForgotPasswordForm, initRegisterForm }, dispatch);
}

const HomeAnonymousSignInContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(HomeAnonymousSignIn)

export default HomeAnonymousSignInContainer;
