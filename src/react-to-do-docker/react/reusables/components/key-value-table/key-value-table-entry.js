import React from 'react';
import TextField from 'material-ui/TextField';
import {TableRow, TableRowColumn} from 'material-ui/Table';

/*
	Custom Components Import
*/
import ActionsAddDelete from '../table-row-actions/actions-add-delete';

class KeyValueTableEntry extends React.Component {
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
			case "inputKey":
				this.state.entry.paramKey = value;
				break;
			case "inputValue":
				this.state.entry.paramValue = value;
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
						hintText="Provide a Key {{m}}"
						fullWidth={true}
						defaultValue={this.state.entry.paramKey}
						onChange={(e, value) => {
							this.onChange("inputKey", value)
						}}
					/>
				</TableRowColumn>
				<TableRowColumn>
					<TextField
						hintText="Provide a Value for the Key {{m}}"
						fullWidth={true}
						defaultValue={this.state.entry.paramValue}
						onChange={(e, value) => {
							this.onChange("inputValue", value)
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

export default KeyValueTableEntry;
