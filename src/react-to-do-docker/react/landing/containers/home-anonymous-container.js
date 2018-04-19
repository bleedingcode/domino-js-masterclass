import { connect } from 'react-redux';
import HomeAnonymous from '../components/home-anonymous';

/*
	Wrapper to map state to the home-anonymous component
*/

// Pass the facet from state into the home-anonymous component's properties
const mapStateToProps = (state) => {
  return {
    facet:state.landing.facet
  }
}

// Connect the mapStateToProps function to the home-anonymous component
const HomeAnonymousContainer = connect(
  mapStateToProps
)(HomeAnonymous)

export default HomeAnonymousContainer;
