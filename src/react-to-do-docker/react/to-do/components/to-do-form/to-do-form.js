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

class ToDoForm extends React.Component {
	constructor(props){
		super(props);

		this.state = {
			entry:tempData.toDo.activeEntry,
			priority:tempData.toDo.activeEntry.data.priority,
			storeId:tempData.toDo.activeEntry.data.storeId
		};

		this.setPriority = this.setPriority.bind(this);
		this.setStoreId = this.setStoreId.bind(this);
	}

	setPriority(value){
		this.setState({priority:value});
	}

	setStoreId(value){
		this.setState({storeId:value});
	}

	onChange(key, value){
		switch(key){
			case "inputStoreId":
				this.state.entry.data.storeId = value;
				this.setStoreId(value);
				break;			
			case "inputTaskName":
				this.state.entry.data.taskName = value;
				break;
			case "inputDescription":
				this.state.entry.data.description = value;
				break;
			case "inputDueDate":
				this.state.entry.data.dueDate = value;
				break;
			case "inputResponsiblePerson":
				this.state.entry.data.responsiblePerson = value;
				break;
			case "inputPriority":
				this.state.entry.data.priority = value;
				this.setPriority(value);
				break;
		}
	}

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
						title="To Do Profile"
					/>
					<CardText>
						<div className="row">
							<div className="col-md-6" style={{marginTop:20}}>
								<SelectField
									floatingLabelText="Store"
									floatingLabelFixed={true}
									value={this.state.entry.data.storeId}
									autoWidth={true}
									onChange={(e, index, value) => {
										this.onChange("inputStoreId", value)
									}}
								>
									<MenuItem value={""} primaryText="-Select-" />
									{this.props.storeList.map(entry =>
										<MenuItem key={entry.value} value={entry.value} primaryText={entry.text} />
									)}									
								</SelectField>							
								<TextField
									hintText="Task Name"
									floatingLabelText="Task Name"
									fullWidth={true}
									defaultValue={this.state.entry.data.taskName}
									onChange={(e, value) => {
										this.onChange("inputTaskName", value)
									}}
								/><br />
								<TextField
									hintText="A detailed description for this To Do"
									floatingLabelText="Description (optional)"
									fullWidth={true}
									defaultValue={this.state.entry.data.description}
									multiLine={true}
									rowsMax={5}
									onChange={(e, value) => {
										this.onChange("inputDescription", value)
									}}
								/><br />
								<TextField
									hintText="Provide a Due Date"
									floatingLabelText="Due Date (optional)"
									fullWidth={true}
									defaultValue={this.state.entry.data.dueDate}
									onChange={(e, value) => {
										this.onChange("inputDueDate", value)
									}}
								/><br />
								<TextField
									hintText="Provide a Responsible Person"
									floatingLabelText="Responsible Person"
									fullWidth={true}
									defaultValue={this.state.entry.data.responsiblePerson}
									onChange={(e, value) => {
										this.onChange("inputResponsiblePerson", value)
									}}
								/><br />
								<SelectField
									floatingLabelText="Priority"
									floatingLabelFixed={true}
									value={this.state.entry.data.priority}
									autoWidth={true}
									onChange={(e, index, value) => {
										this.onChange("inputPriority", value)
									}}
								>
									<MenuItem value={""} primaryText="-Select-" />
									<MenuItem value={"Low"} primaryText="Low" />
									<MenuItem value={"Medium"} primaryText="Medium" />
									<MenuItem value={"High"} primaryText="High" />
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

export default ToDoForm;
