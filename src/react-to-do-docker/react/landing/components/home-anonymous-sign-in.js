import React from 'react';
import tempData from '../../temp-data-store/temp-data';
import { connectWebSocket, disconnectWebSocket } from '../landing-logic';

import RefreshIndicator from 'material-ui/RefreshIndicator';
import { Card, CardActions, CardTitle, CardText } from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import ActionDone from 'material-ui/svg-icons/action/done';
import ContentClear from 'material-ui/svg-icons/content/clear';
import ContentForward from 'material-ui/svg-icons/content/forward';
import ContentInbox from 'material-ui/svg-icons/content/inbox';
import globals from '../../globals';

/*
	Component for the sign-in form for anonymous login
*/

class HomeAnonymousSignIn extends React.Component {
	constructor() {
		super();
		// We don't want the login data outside this form, so we just use the temp-data store object
		this.entry = tempData.signInForm;
		this.state = { authenticating: false }	// Not currently authenticating the user

		// Bind the two functions below so to the HomeAnonymousSignIn component itself
		this.onChange = this.onChange.bind(this);
		this.authenticate = this.authenticate.bind(this);
	}

	// onChange, write to the temp-data store
	onChange(key, value) {
		switch (key) {
			case "inputUsername":
				this.entry.username = value;
				break;
			case "inputPassword":
				this.entry.password = value;
				break;
		}
	}

	// authenticate method. Sets current state as "authenticating" and calls signInUser() function from core-actions.js
	authenticate() {
		let tempThis = this;
		tempThis.setState({ authenticating: true });

		this.props.signInUser(function () {
			// Once signInUser has run, reset authenticating to false
			tempThis.setState({ authenticating: false });
		});
	}

	// Once component has been mounted, make a web socket connection, in landing-logic
	componentDidMount() {
		connectWebSocket();
	}

	// As the component is being unmounted, disonnect the web socket connection, in landing-logic
	componentWillUnmount() {
		disconnectWebSocket();
	}

	// Emit the HTML to the browser.
	render() {
		let theme = this.props.theme;

		return (
			<div>
				{/* Header */}
				<div className="col-xs-12">
					<center>
						{/*<img src={globals.imgUrl + "/ibm-think-banner.jpg"} style={{ width: 400 }} />*/}
						<img src={globals.imgUrl + "/EngageLogo_Ani.gif"} style={{ width: 400 }} />
						<h1>Welcome to the React To Do App Portal</h1>
						<h3>Sign in below to access the Portal</h3>
					</center>
				</div>
				{/* Authentication form. On submission, call authenticate() method above */}
				<form
					className="col-md-6 col-md-offset-3"
					onSubmit={e => {
						e.preventDefault()
						this.authenticate()
					}}
				>
					<Card>
						<CardText>
							{/* Fields for username and password. No labels required */}
							<div className="row">
								<div className="col-sm-10">
									<TextField
										hintText="Enter your username"
										floatingLabelText="Username"
										fullWidth={true}
										defaultValue={this.entry.username}
										onChange={(e, value) => {
											this.onChange("inputUsername", value)
										}}
									/><br />
									<TextField
										hintText="Enter a password"
										floatingLabelText="Password"
										type="password"
										fullWidth={true}
										defaultValue={this.entry.password}
										onChange={(e, value) => {
											this.onChange("inputPassword", value)
										}}
									/>
								</div>
							</div>
						</CardText>
						{/* Error div, submit button and "loading" indicator */}
						<CardActions>
							<div className="row">
								<div id="divMessages" className="col-xs-11 col-xs-offset-1 messagesError"></div>
								<div className="col-xs-11 col-xs-offset-1">
									<FlatButton
										type="submit"
										disabled={this.state.authenticating}
										icon={<ActionDone />}
										label="Sign In"
										style={{ color: theme.successColor }}
									/>
									{this.state.authenticating ?
										<RefreshIndicator
											size={30}
											left={10}
											top={5}
											loadingColor="#3d6da8"
											status="loading"
											style={{ display: 'inline-block', position: 'relative' }}
										/>
										: null}
								</div>
							</div>
						</CardActions>
					</Card>
				</form>
			</div>
		)
	}
}

export default HomeAnonymousSignIn;
