import { connect } from 'react-redux';
import HomeAnonymous from '../components/home-anonymous';

const mapStateToProps = (state) => {
  return {
    facet:state.landing.facet
  }
}

const HomeAnonymousContainer = connect(
  mapStateToProps
)(HomeAnonymous)

export default HomeAnonymousContainer;
