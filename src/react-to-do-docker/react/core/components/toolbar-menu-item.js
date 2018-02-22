import React from 'react';
import {ListItem} from 'material-ui/List';

const ToolbarMenuItem = ({ dataClass, title, dataId, leftIcon, theme, switchMenu }) => (
	<ListItem
		innerDivStyle={dataClass === "active" ? {backgroundColor:theme.secondaryLight} : null}
		primaryText={title}
		leftIcon={leftIcon}
		onTouchTap={e => {
			e.preventDefault();
			switchMenu(dataId);
		}}
	/>
)

export default ToolbarMenuItem;
