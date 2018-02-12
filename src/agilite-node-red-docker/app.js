var express = require('express');
var app = express();
var http = require('http').Server(app);
var RED = require("node-red");

// Create the settings object - see default settings.js file for other options
var settings = {
    httpAdminRoot:"/",
    httpNodeRoot: "/api",
    userDir:__dirname + "/flows/",
    flowFile: 'flows.json',
    httpRequestTimeout: 300000,
    adminAuth: require("./user-authentication"),
    functionGlobalContext: {},
    editorTheme:{
      page: {
          title: "Node-RED (Agilit-e)"
      },
      header:{
        title:"Node-RED (Agilite-e)"
      }
    },
    editorTheme:{
      projects:{
        enabled:true
      }
    },
    flowFilePretty: true
};

// Initialise the runtime with a server and settings
RED.init(http,settings);

// Serve the editor UI from /red
app.use(settings.httpAdminRoot,RED.httpAdmin);

// Serve the http nodes UI from /api
app.use(settings.httpNodeRoot,RED.httpNode);

//Server Setup
const port = 6050;

http.listen(port, function() {
  RED.start();
  console.log("Node-RED Docker listening on: ", port);
});
