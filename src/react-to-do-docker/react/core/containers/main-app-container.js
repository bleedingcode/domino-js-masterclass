import { connect } from 'react-redux'
import MainApp from '../components/main-app'

const mapStateToProps = (state) => {
  return {
    app:state.main.app
  }
}

const MainAppContainer = connect(
  mapStateToProps
)(MainApp)

export default MainAppContainer
