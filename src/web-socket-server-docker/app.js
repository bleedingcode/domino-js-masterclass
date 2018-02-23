const express = require('express');
const helmet = require('helmet');
const http = require('http');

const Globals = require('./utils/globals');
const Utils = require('./utils/utilities');
const WS = require('./controllers/web-socket');
const app = express();

app.use(helmet());

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