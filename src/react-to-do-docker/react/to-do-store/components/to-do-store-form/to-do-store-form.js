import React from 'react';
import tempData from '../../../temp-data-store/temp-data';

import {Tabs, Tab} from 'material-ui/Tabs';
import {Card, CardActions, CardText} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import ActionDone from 'material-ui/svg-icons/action/done';
import ContentClear from 'material-ui/svg-icons/content/clear';
import AppBar from 'material-ui/AppBar';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';

/*
	Form for a ToDo Store (NSF containing ToDos)
*/

class ToDoStoreForm extends React.Component {
	constructor(){
		super();

		// Load state from tempData.toDoStore
		// toDoStore.activeEntry is the entry being created / edited
		// toDoStore.activeEntry.custom is the customTemplate fields that get used for processing purposes, but are not stored into the database. So things like whether it's a new doc / saved doc / id etc get set depending on whether it's create or edit
		this.state = {
			entry:tempData.toDoStore.activeEntry,
			type:tempData.toDoStore.activeEntry.data.type,
			isNewDoc:tempData.toDoStore.activeEntry.custom.isNewDoc
		};

		// Bind the setType function to the component itself. Not sure why onChange function isn't bound though
		this.setType = this.setType.bind(this);
	}

	// Change the type in the state. Should this be in a reducer?
	setType(value){
		this.setState({type:value});
	}

	// onChange event for each field
	onChange(key, value){
		switch(key){
			case "inputTitle":
				this.state.entry.data.title = value;
				break;
			case "inputName":
				this.state.entry.data.name = value;
				break;
			case "inputType":
				// inputType set on both the data and the top-level
				this.state.entry.data.type = value;
				this.setType(value);
				break;
		}
	}

	// Emit the HTML to the browser. Form's submit property is bound to onSubmitClick in to-do-store-form-container
	render(){
		return(
			<form
				className="col-md-12"
				onSubmit={e => {
            		e.preventDefault()
					this.props.onSubmitClick(this.props.state)
        		}}
			>
				<Card>
					<AppBar
						showMenuIconButton={false}
						style={{backgroundColor:this.props.theme.secondaryLight2}}
            			titleStyle={{color:this.props.theme.black}}
						title="Store Profile"
					/>
					<CardText>
						<div className="row">
							<div className="col-md-6" style={{marginTop:20}}>
								<TextField
									hintText="Provide a Title"
									floatingLabelText="Title"
									fullWidth={true}
									defaultValue={this.state.entry.data.title}
									onChange={(e, value) => {
										this.onChange("inputTitle", value)
									}}
								/><br />
								<TextField
									hintText="Provide a Name"
									floatingLabelText="Name"
									disabled={!this.state.isNewDoc}
									fullWidth={true}
									defaultValue={this.state.entry.data.name}
									onChange={(e, value) => {
										this.onChange("inputName", value)
									}}
								/><br />
								<SelectField
									floatingLabelText="Type"
									floatingLabelFixed={true}
									disabled={!this.state.isNewDoc}
									value={this.state.entry.data.type}
									autoWidth={true}
									onChange={(e, index, value) => {
										this.onChange("inputType", value)
									}}
								>
									<MenuItem value={""} primaryText="-Select-" />
									<MenuItem value={"Personal"} primaryText="Personal" />
									<MenuItem value={"Team"} primaryText="Team" />
								</SelectField>							
							</div>
						</div>
					</CardText>
					<CardActions>
						<div className="row">
              				<div id="divMessages" className="col-xs-12 messagesError"></div>
							<div className="col-xs-12">
								<FlatButton
									type="submit"
									icon={<ActionDone />}
									label="Submit"
									style={{color:this.props.theme.successColor}}
								/>
								<FlatButton
									icon={<ContentClear />}
									label="Cancel"
									secondary={true}
									onTouchTap={e => {
										e.preventDefault()
										this.props.onCancelClick()
									}}
								/>
							</div>
          				</div>
					</CardActions>
				</Card>
			</form>
		)
	}
}

export default ToDoStoreForm;
