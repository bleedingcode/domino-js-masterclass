import React from 'react';
import {TableRowColumn, TableRow} from 'material-ui/Table';
import ActionDelete from 'material-ui/svg-icons/action/delete';

/*
	Component for an individual ToDo entry in the "view"
*/

const ToDoListEntry = ({ theme, author, taskName, description, dueDate, assignedTo, priority, storeName, className, onEditProfile, onDeleteProfile, onDeleteConfirm }) => (
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
				{taskName}
			</a>
		</TableRowColumn>
		<TableRowColumn>{description}</TableRowColumn>
		<TableRowColumn>{author}</TableRowColumn>
		<TableRowColumn>{dueDate}</TableRowColumn>
		<TableRowColumn>{assignedTo}</TableRowColumn>
		<TableRowColumn>{priority}</TableRowColumn>
		<TableRowColumn>{storeName}</TableRowColumn>
 	</TableRow>
)

export default ToDoListEntry;
