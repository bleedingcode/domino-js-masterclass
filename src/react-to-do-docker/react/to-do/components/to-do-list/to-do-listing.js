import React from 'react';

import LinearProgress from 'material-ui/LinearProgress';
import { Table, TableBody, TableHeader, TableHeaderColumn, TableRowColumn, TableRow } from 'material-ui/Table';
import { Card, CardTitle, CardText } from 'material-ui/Card';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import FlatButton from 'material-ui/FlatButton';
import AVLibraryBooks from 'material-ui/svg-icons/av/library-books';
import Dialog from 'material-ui/Dialog';
import AppBar from 'material-ui/AppBar';

/*
	Custom Components Import
*/
import ToDoListEntry from './to-do-list-entry';

/*
	Component for todo views
*/

class ToDoListing extends React.Component {
	constructor(props) {
		super(props);

		// Hide prompt form
		this.state = this.initState();

		//Setup Event Binding
		this.deleteConfirm = this.deleteConfirm.bind(this);
		this.cancelPrompt = this.cancelPrompt.bind(this);
		this.confirmPrompt = this.confirmPrompt.bind(this);
	}

	// Open dialog to confirm deletion
	deleteConfirm(id, name) {
		var state = {
			promptOpen: true,
			promptName: name,
			promptId: id
		};

		this.setState(state);
	}

	// Cancel dialog for deletion
	cancelPrompt() {
		this.setState(this.initState());
	}

	// Confirm dialog for deletion
	confirmPrompt() {
		// Trigger onDeleteProfile action from container
		this.props.onDeleteProfile(this.state.promptId, this.props.state);
		// Close dialog
		this.setState(this.initState());
	}

	// Set the state of the dialog - closed, no title or selected doc
	initState() {
		var state = {
			promptOpen: false,
			promptName: "",
			promptId: ""
		};

		return state;
	}

	// Emit HTML to the browser
	render() {
		return (
			<div className="col-md-12">
				<Card>
					<AppBar
						showMenuIconButton={false}
						style={{ backgroundColor: this.props.theme.secondaryLight2 }}
						titleStyle={{ color: this.props.theme.black }}
						title={this.props.title}
						iconElementRight={
							<div>
								{this.props.state.header.dataLoaded ?
									<FlatButton
										icon={<AVLibraryBooks color={this.props.theme.primary} />}
										label="New To Do"
										labelStyle={{ color: this.props.theme.primary }}
										onTouchTap={e => {
											e.preventDefault()
											this.props.onCreateProfile()
										}}
									/>
									: null}
							</div>
						}
					/>
					<CardText>
						<Table selectable={false}>
							<TableHeader displaySelectAll={false} adjustForCheckbox={false}>
								<TableRow>
									<TableHeaderColumn colSpan="7">
										<div>
											<div className="col-sm-12">
												<TextField
													hintText="Search To Dos..."
													fullWidth={true}
													value={this.props.listFilter}
													onChange={(e, value) => {
														this.props.onChange("listFilter", value);
													}}
												/>
											</div>
										</div>
									</TableHeaderColumn>
								</TableRow>
								<TableRow>
									<TableHeaderColumn>Task Name</TableHeaderColumn>
									<TableHeaderColumn>Description</TableHeaderColumn>
									<TableHeaderColumn>Author</TableHeaderColumn>
									<TableHeaderColumn>Due Date</TableHeaderColumn>
									<TableHeaderColumn>Responsible Person</TableHeaderColumn>
									<TableHeaderColumn>Priority</TableHeaderColumn>
									<TableHeaderColumn>Store</TableHeaderColumn>
								</TableRow>
							</TableHeader>
							<TableBody displayRowCheckbox={false}>
								{!this.props.state.header.dataLoaded ?
									<TableRow>
										<TableRowColumn colSpan="4">
											<LinearProgress color="#3d6da8" mode="indeterminate" />
										</TableRowColumn>
									</TableRow>
									:
									this.props.listing.map(entry =>
										<ToDoListEntry
											key={entry._id}
											theme={this.props.theme}
											author={entry.data.author}
											taskName={entry.data.taskName}
											description={entry.data.description}
											dueDate={entry.data.dueDate}
											assignedTo={entry.data.assignedTo}
											priority={entry.data.priority}
											storeName={entry.data.storeName}
											className={entry.custom ? entry.custom.status : ""}
											onEditProfile={() => this.props.onEditProfile(entry._id)}
											onDeleteConfirm={() => this.deleteConfirm(entry._id, entry.data.name)}
										/>
									)
								}

							</TableBody>
						</Table>
					</CardText>
				</Card>
				<Dialog
					title="Warning"
					actions={
						[
							<FlatButton
								label="No"
								secondary={true}
								onTouchTap={this.cancelPrompt}
							/>,
							<FlatButton
								label="Yes"
								style={{ color: this.props.theme.successColor }}
								onTouchTap={this.confirmPrompt}
							/>
						]
					}
					modal={true}
					open={this.state.promptOpen}
				>
					{`Are you sure you want to delete To Do Profile: ${this.state.promptName}`}
				</Dialog>
			</div>
		)
	}
}

export default ToDoListing;
