import { connect } from 'react-redux';
import LandingWrapper from '../components/landing-wrapper';

const mapStateToProps = (state) => {
  return {
    loggedIn:state.main.user.loggedIn
  }
}

const LandingWrapperContainer = connect(
  mapStateToProps
)(LandingWrapper)

export default LandingWrapperContainer;
