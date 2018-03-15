const express = require('express');
const helmet = require('helmet');
const http = require('http');
const bodyParser = require('body-parser');
const morgan = require('morgan');
const cors = require('cors');

const Globals = require('./utils/globals');
const Utils = require('./utils/utilities');
const WS = require('./controllers/web-socket');
const app = express();

app.use(helmet());
app.use(morgan('combined'));
app.use(cors());

app.use(bodyParser.text());
app.use(bodyParser.json());

const router = require('./router');
router(app);

//First Load Config Details
Utils.loadConfig(function(){
  //Server Setup
  const port = 6021;
  const server = http.createServer(app);
  const io = require('socket.io')(server);

  server.listen(port, function() {
    WS.initEvents(io);
    console.log("Web Socket Server listening on: ", port);
  });
});