import { connect } from 'react-redux'
import MainApp from '../components/main-app'

/* 
 * Container for main-app Component. Containers separate data/state from UI
 */

// Pass into the component's properties the "app", or the "view" being displayed, set via switchMenu
const mapStateToProps = (state) => {
	return {
		app:state.main.app
	}
}

// Connect the mapStateToProps function to the main-app component
const MainAppContainer = connect(
	mapStateToProps
)(MainApp)

export default MainAppContainer
