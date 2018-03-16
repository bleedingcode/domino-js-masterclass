import React from 'react';
import {TableRowColumn, TableRow} from 'material-ui/Table';
import ActionDelete from 'material-ui/svg-icons/action/delete';

const ToDoStoreListEntry = ({ theme, title, name, type, replicaId, className, onEditProfile, onDeleteProfile, onDeleteConfirm }) => (
	<TableRow
		className={`list-entry ${className}`}
		hoverable={true}
	>
		<TableRowColumn>
			<a
				style={{cursor:'pointer'}}
				onClick={e => {
					e.preventDefault();
					onEditProfile();
				}}>
				{title}
			</a>
		</TableRowColumn>
		<TableRowColumn>{name}</TableRowColumn>
		<TableRowColumn>{type}</TableRowColumn>
		<TableRowColumn>{replicaId}</TableRowColumn>
 	</TableRow>
)

export default ToDoStoreListEntry;
