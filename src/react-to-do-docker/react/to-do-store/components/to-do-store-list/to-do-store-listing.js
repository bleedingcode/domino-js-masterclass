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
import ToDoStoreListEntry from './to-do-store-list-entry';

/*
	Component for the list of ToDo stores. Note: there's functionality here for deleting, but no button is added on entries to allow the user to trigger it.
*/
class ToDoStoreListing extends React.Component {
	constructor() {
		super();

		// Initialise state for prompt
		this.state = this.initState();

		// Setup Event Binding
		this.deleteConfirm = this.deleteConfirm.bind(this);
		this.cancelPrompt = this.cancelPrompt.bind(this);
		this.confirmPrompt = this.confirmPrompt.bind(this);
	}

	// Check to make sure the user wants to delete this store
	deleteConfirm(id, name) {
		var state = {
			promptOpen: true,
			promptName: name,
			promptId: id
		};

		this.setState(state);
	}

	// User cancelled delete confirmation
	cancelPrompt() {
		this.setState(this.initState());
	}

	// User confirmed delete confirmation
	confirmPrompt() {
		// Call function setup in container, which triggers deleteProfile action
		this.props.onDeleteProfile(this.state.promptId, this.props.state);
		this.setState(this.initState());
	}

	// Hide any prompt for confirmation
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
										label="New Store"
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
									<TableHeaderColumn colSpan="4">
										<div>
											<div className="col-sm-12">
												<TextField
													hintText="Search Stores..."
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
									<TableHeaderColumn>Title</TableHeaderColumn>
									<TableHeaderColumn>Name</TableHeaderColumn>
									<TableHeaderColumn>Type</TableHeaderColumn>
									<TableHeaderColumn>Id</TableHeaderColumn>
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
										<ToDoStoreListEntry
											key={entry._id}
											theme={this.props.theme}
											title={entry.data.title}
											name={entry.data.name}
											type={entry.data.type}
											replicaId={entry.data.replicaId}
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
				{/* Confirmation dialog */}
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
					{`Are you sure you want to delete Store: ${this.state.promptName}`}
				</Dialog>
			</div>
		)
	}
}

export default ToDoStoreListing;
