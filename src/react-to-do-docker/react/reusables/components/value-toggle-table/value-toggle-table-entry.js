import React from 'react';
import TextField from 'material-ui/TextField';
import {TableRow, TableRowColumn} from 'material-ui/Table';
import Toggle from 'material-ui/Toggle';

/*
	Custom Components Import
*/
import ActionsAddDelete from '../table-row-actions/actions-add-delete';

class ValueToggleTableEntry extends React.Component {
	constructor(props){
		super(props);

		this.state = {
			entry:props.entry
		}

		//Setup Event Binding
		this.onChange = this.onChange.bind(this);
	}

	onChange(key, value){
		switch(key){
			case "inputValue":
				this.state.entry.value = value;
				break;
			case "inputValueFlag":
				this.state.entry.valueFlag = value;
				break;
		}
	}

	render(){
		return(
			<TableRow
				className="list-entry"
				hoverable={true}
			>
				<TableRowColumn>
					<TextField
						hintText="Provide Value {{m}}"
						fullWidth={true}
						defaultValue={this.state.entry.value}
						onChange={(e, value) => {
							this.onChange("inputValue", value)
						}}
					/>
				</TableRowColumn>
				<TableRowColumn>
					<Toggle
						defaultToggled={this.state.entry.valueFlag}
						onToggle={(e, value) => {
							this.onChange("inputValueFlag", value)
						}}						
					/>
    		</TableRowColumn>
				<TableRowColumn style={{width:150}}>
					<ActionsAddDelete
						trashEnabled={this.props.trashEnabled}
						addEnabled={this.props.addEnabled}
						count={this.props.count}
						index={this.props.index}
						onAdd={() => {
							this.props.onAdd(this.props.index)
						}}
						onDelete={() => {
							this.props.onDelete(this.props.index)
						}}
					/>
        </TableRowColumn>
		 	</TableRow>
		)
	}
}

export default ValueToggleTableEntry;
