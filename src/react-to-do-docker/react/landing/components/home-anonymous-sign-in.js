import React from 'react';
import tempData from '../../temp-data-store/temp-data';

import {Card, CardActions, CardTitle, CardText} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import ActionDone from 'material-ui/svg-icons/action/done';
import ContentClear from 'material-ui/svg-icons/content/clear';
import ContentForward from 'material-ui/svg-icons/content/forward';
import ContentInbox from 'material-ui/svg-icons/content/inbox';
import globals from '../../globals';

class HomeAnonymousSignIn extends React.Component {
  constructor(){
		super();
		this.entry = tempData.signInForm;
	}

	onChange(key, value){
		switch(key){
			case "inputEmail":
				this.entry.email = value;
				break;
			case "inputPassword":
				this.entry.password = value;
				break;
		}
	}

  render(){
    let theme = this.props.theme

    let inputEmail
    let inputPassword

    return(
      <div>
        <div className="col-xs-12">
          <center>
            <img src={globals.imgUrl + "/agilite/agilite-logo-full-web.png"} style={{width:400}} />
            <h1>Welcome to the To Do App Portal</h1>
            <h3>Sign in below to access the Portal</h3>
          </center>
        </div>      
        <form
          className="col-md-6 col-md-offset-3"
          onSubmit={e => {
              e.preventDefault()
              this.props.signInUser()
          }}
          >
          <Card>
            <CardText>
              <div className="row">
              <div className="col-sm-10">
              <TextField
                hintText="Enter your email address"
                floatingLabelText="Email"
                type="email"
                fullWidth={true}
                defaultValue={this.entry.email}
                onChange={(e, value) => {
                  this.onChange("inputEmail", value)
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
            <CardActions>
              <div className="row">
              <div id="divMessages" className="col-xs-11 col-xs-offset-1 messagesError"></div>
              <div className="col-xs-11 col-xs-offset-1">
              <FlatButton
                type="submit"
                icon={<ActionDone />}
                label="Sign In"
                style={{color:theme.successColor}}
              />
              <FlatButton
                icon={<ContentForward />}
                label="Forgot Password"
                secondary={true}
                onTouchTap={e => {
                    e.preventDefault()
                    this.props.initForgotPasswordForm()
                }}
              />     
              <FlatButton
                icon={<ContentInbox />}
                label="Register"
                primary={true}
                onTouchTap={e => {
                    e.preventDefault()
                    this.props.initRegisterForm()
                }}
              />
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
