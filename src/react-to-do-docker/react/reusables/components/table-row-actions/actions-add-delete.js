import React from 'react';
import {red500, green500} from 'material-ui/styles/colors';
import ActionDelete from 'material-ui/svg-icons/action/delete';
import ContentAdd from 'material-ui/svg-icons/content/add';

class ActionsAddDelete extends React.Component {
	render(){
		return(
			<div>
        {this.props.trashEnabled ?
          <div className="trash-button">
            <a onClick={e => {
                e.preventDefault()
                this.props.onDelete(this.props.index)
              }}>
              <ActionDelete className="trash-enabled" style={{color:red500}} />
            </a>
          </div>
        :null}

        {this.props.addEnabled ?
          <div className="add-button">
            <a onClick={e => {
                e.preventDefault()
                this.props.onAdd(this.props.index)
              }}
            >
              <ContentAdd style={{color:green500}} />
            </a>
          </div>
        :null}
		 	</div>
		)
	}
}

export default ActionsAddDelete;
