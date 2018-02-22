import React from 'react';

import {Card, CardActions, CardHeader, CardMedia, CardTitle, CardText} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import globals from '../../globals';

const HomeLoggedIn = () => (
  <div className="col-xs-12">
    <div className="col-xs-12">
      <center>
      <img src={globals.imgUrl + "/agilite/agilite-logo-full-web.png"} style={{width:400}} />
        <h1>Welcome to the To Do App Portal</h1>
        <h3>bla bla bla</h3>
      </center>
    </div>
  </div>
)

export default HomeLoggedIn;
