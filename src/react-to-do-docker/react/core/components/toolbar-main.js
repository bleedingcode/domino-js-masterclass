import React from 'react';
import IconMenu from 'material-ui/IconMenu';
import IconButton from 'material-ui/IconButton';
import RaisedButton from 'material-ui/RaisedButton';
import Menu from 'material-ui/Menu';
import MenuItem from 'material-ui/MenuItem';
import Drawer from 'material-ui/Drawer';;
import Divider from 'material-ui/Divider';
import Subheader from 'material-ui/Subheader';
import Popover from 'material-ui/Popover';
import globals from '../../globals';

import {List, ListItem} from 'material-ui/List';
import {Toolbar, ToolbarGroup, ToolbarSeparator, ToolbarTitle} from 'material-ui/Toolbar';

import ArrowDropDown from 'material-ui/svg-icons/navigation/arrow-drop-down';
import NavigationMenuIcon from 'material-ui/svg-icons/navigation/menu';
import NavigationRefresh from 'material-ui/svg-icons/navigation/refresh';
import EditorFormatListNumbered from 'material-ui/svg-icons/editor/format-list-numbered';
import EditorLinearScale from 'material-ui/svg-icons/editor/linear-scale';
import ContentLowPriority from 'material-ui/svg-icons/content/low-priority';
import ContentFilterList from 'material-ui/svg-icons/content/filter-list';
import ContentReport from 'material-ui/svg-icons/content/report';
import HardwareSecurity from 'material-ui/svg-icons/hardware/security';
import ActionAccountBox from 'material-ui/svg-icons/action/account-box';
import ActionAccountBalance from 'material-ui/svg-icons/action/account-balance';
import AVLibraryBooks from 'material-ui/svg-icons/av/library-books';
import MapsLayers from 'material-ui/svg-icons/maps/layers';
import MapsPersonPin from 'material-ui/svg-icons/maps/person-pin';
import ContentLink from 'material-ui/svg-icons/content/link';
import ContentNextWeek from 'material-ui/svg-icons/content/next-week';

/*
	Custom Components Import
*/
import ToolbarMenuItemContainer from '../containers/toolbar-menu-item-container';

class ToolbarMain extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
			resourcesMenu:false,
			resourcesAnchorEl:null,
			userMenu:false,
			userAnchorEl:null
    };
  }

  render(){
    let theme = this.props.theme;

    const styles = {
		  smallIcon: {
		    width: 36,
		    height: 36,
				fill:theme.white
		  },
		  small: {
		    width: 72
		  }
		};

    return (
      <Toolbar style={{backgroundColor:theme.primary}}>
        {this.props.loggedIn ?
          <ToolbarGroup firstChild={true}>
            <IconButton touch={true} onTouchTap={this.props.openDrawer} iconStyle={{color:theme.white}}>
    					<NavigationMenuIcon />
    				</IconButton>
            <Drawer
    						docked={false}
    						width={300}
    						open={this.props.drawerOpen}
                onRequestChange={(open) => this.props.toggleDrawer()}              
    					>
                <Toolbar style={{backgroundColor:theme.primary}}>
                  <ToolbarTitle text={globals.title} style={{color:theme.white}}/>
                </Toolbar>
                <List>
                  <ToolbarMenuItemContainer
                    title='Home'
                    leftIcon={<ActionAccountBalance />}
                    dataId='home'
                    dataClass={this.props.app === 'home' ? 'active' : ''}
                  />
                </List>
                <Divider style={{color:theme.secondaryLight}} />
                <List>
                  <ListItem
      							primaryText="To Dos"
      							initiallyOpen={true}
      							primaryTogglesNestedList={true}
      							nestedItems={[
                        <ToolbarMenuItemContainer
                        key="to-do-new"
                        title='New'
                        leftIcon={<MapsLayers />}
                        dataId='to-do-new'
                        dataClass={this.props.app === 'to-do-new' ? 'active' : ''}
                      />,
                      <ToolbarMenuItemContainer
                        key="to-do-assigned"
                        title='Assigned'
                        leftIcon={<EditorFormatListNumbered />}
                        dataId='to-do-assigned'
                        dataClass={this.props.app === 'to-do-assigned' ? 'active' : ''}
                      />,
                      <ToolbarMenuItemContainer
                        key="to-do-complete"
                        title='Complete'
                        leftIcon={<ContentFilterList />}
                        dataId='to-do-complete'
                        dataClass={this.props.app === 'to-do-complete' ? 'active' : ''}
                      />,
                      <ToolbarMenuItemContainer
                        key="to-do-overdue"
                        title='Overdue'
                        leftIcon={<ContentLink />}
                        dataId='to-do-overdue'
                        dataClass={this.props.app === 'to-do-overdue' ? 'active' : ''}
                      />                      
      							]}
      						/>
                </List>                
                <List>
                  <ListItem
      							primaryText="Stores"
      							initiallyOpen={false}
      							primaryTogglesNestedList={true}
      							nestedItems={[
                      <ToolbarMenuItemContainer
                        key="stores-all"
                        title='All'
                        leftIcon={<ContentLink />}
                        dataId='stores-all'
                        dataClass={this.props.app === 'stores-all' ? 'active' : ''}
                      />
      							]}
      						/>
                </List>
              </Drawer>
            <ToolbarTitle text={this.props.title} style={{color:theme.white}}/>
          </ToolbarGroup>
        :
          <ToolbarGroup firstChild={true}>
            <ToolbarTitle text={this.props.title} style={{color:theme.white,marginLeft:48}}/>
          </ToolbarGroup>
        }
      </Toolbar>
    )
  }
}

export default ToolbarMain;
