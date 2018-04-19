import React from 'react';
import {ListItem} from 'material-ui/List';

/*
	Individual menu item on the toolbar-main. Whatever is defined as the datatId on the implementation of this component will be used to switch to the relevant "view".
*/

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
