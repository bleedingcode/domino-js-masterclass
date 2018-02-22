import React from 'react';
import TextField from 'material-ui/TextField';
import {TableRow, TableRowColumn} from 'material-ui/Table';

/*
	Custom Components Import
*/
import ActionsAddDelete from '../table-row-actions/actions-add-delete';

class SingleValueTableEntry extends React.Component {
	constructor(props){
		super(props);

		this.state = {
			value:props.entry
		}

		//Setup Event Binding
		//this.onChange = this.onChange.bind(this);
	}

	// onChange(key, value, index){
	// 	switch(key){
	// 		case "inputValue":
	// 			this.state.entry = value;
	// 			break;
	// 	}
	// }

	render(){
		return(
			<TableRow
				className="list-entry"
				hoverable={true}
			>
				<TableRowColumn>
					<TextField
						hintText="Provide a Value"
						fullWidth={true}
						defaultValue={this.state.value}
						onChange={(e, value) => {
							this.state.value = value;
							this.props.onChange(value, this.props.index)
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

export default SingleValueTableEntry;
