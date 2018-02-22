import React from 'react';
import {TableRowColumn, TableRow} from 'material-ui/Table';
import ActionDelete from 'material-ui/svg-icons/action/delete';

const ToDoListEntry = ({ theme, author, taskName, description, dueDate, responsiblePerson, priority, className, onEditProfile, onDeleteProfile, onDeleteConfirm }) => (
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
		<TableRowColumn>{responsiblePerson}</TableRowColumn>
		<TableRowColumn>{priority}</TableRowColumn>
		<TableRowColumn>
			<div className="trash-button">
				<a
					onClick={e => {
						e.preventDefault();
						onDeleteConfirm();
					}}>
					<ActionDelete className="trash-enabled" style={{color:theme.dangerColor}} />
				</a>
			</div>
  	</TableRowColumn>
 	</TableRow>
)

export default ToDoListEntry;
