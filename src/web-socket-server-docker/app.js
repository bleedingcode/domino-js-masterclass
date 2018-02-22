const express = require('express');
const helmet = require('helmet');
const http = require('http');
const bodyParser = require('body-parser');
const morgan = require('morgan');
const cors = require('cors');
const Globals = require('./utils/globals');
const Utils = require('./utils/utilities');
const app = express();

app.use(helmet());
app.use(morgan('combined'));
app.use(cors());

//First Load Config Details
Utils.loadConfig(function(success){
  Globals.configLoaded = success;
  console.log("Config Loaded = " + Globals.configLoaded);

  if(success){
    app.use(bodyParser.text({limit: Globals.config.fileSizeLimit}));
    app.use(bodyParser.json({limit: Globals.config.fileSizeLimit}));
  }else{
    app.use(bodyParser.text());
    app.use(bodyParser.json());
  }
});

//Server Setup
const port = 6021;
const server = http.createServer(app);
const io = require('socket.io')(server);

server.listen(port, function() {
    console.log("Web Socket Server listening on: ", port);

    io.on('connection', function (socket) {
      //Return Session ID back to user when connection is established
      io.sockets.sockets[socket.id].emit("init-user", socket.id);

      socket.on('agilite-aa-portal-chatroom', function (data) {
        Utils.initAAWebhook(data, function(err, result){
          io.sockets.sockets[data.socketId].emit("agilite-aa-portal-chatroom", result);
        });
      });
    });  
});