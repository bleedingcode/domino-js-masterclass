import React from 'react';

import {Table, TableBody, TableHeader, TableHeaderColumn, TableRow} from 'material-ui/Table';
import {Card, CardTitle, CardText} from 'material-ui/Card';
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

class ToDoStoreListing extends React.Component {
  constructor(){
		super();

		this.state = this.initState();

    //Setup Event Binding
    this.deleteConfirm = this.deleteConfirm.bind(this);
    this.cancelPrompt = this.cancelPrompt.bind(this);
    this.confirmPrompt = this.confirmPrompt.bind(this);
	}

	deleteConfirm(id, name){
    var state = {
      promptOpen:true,
      promptName:name,
      promptId:id
    };

    this.setState(state);
  }

  cancelPrompt(){
    this.setState(this.initState());
  }

  confirmPrompt(){
    this.props.onDeleteProfile(this.state.promptId, this.props.state);
    this.setState(this.initState());
  }

  initState(){
    var state = {
      promptOpen:false,
      promptName:"",
      promptId:""
    };

    return state;
  }

  render(){
    return(
      <div className="col-md-12">
        <Card>
          <AppBar
						showMenuIconButton={false}
            style={{backgroundColor:this.props.theme.secondaryLight2}}
            titleStyle={{color:this.props.theme.black}}
						title={this.props.title}
            iconElementRight={
              <FlatButton
                icon={<AVLibraryBooks color={this.props.theme.primary} />}
                label="New Store"
                labelStyle={{color:this.props.theme.primary}}
                onTouchTap={e => {
                    e.preventDefault()
                    this.props.onCreateProfile()
                }}
              />
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
                  <TableHeaderColumn></TableHeaderColumn>
                </TableRow>
              </TableHeader>
              <TableBody>
                {this.props.listing.map(entry =>
    				      <ToDoStoreListEntry
    				      	key={entry._id}
                    theme={this.props.theme}
                    title={entry.data.title}
                    name={entry.data.name}
                    type={entry.data.type}
                    className={entry.custom ? entry.custom.status : ""}
    				        onEditProfile={() => this.props.onEditProfile(entry._id)}
                    onDeleteConfirm={() => this.deleteConfirm(entry._id, entry.data.name)}
    				      />
    				    )}
              </TableBody>
            </Table>
          </CardText>
        </Card>
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
                style={{color:this.props.theme.successColor}}
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
