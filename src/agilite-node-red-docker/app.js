const express = require('express');
const app = express();
const http = require('http').Server(app);
const RED = require("node-red");
const Utils = require('./utils/utilities');

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

Utils.loadConfig(function(){
  http.listen(port, function() {
    RED.start();
    console.log("Agilit-e Node-RED Docker listening on: ", port);
  });
});
