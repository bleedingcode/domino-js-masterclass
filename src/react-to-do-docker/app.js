var express = require('express');
var app = express();
const http = require('http');

// serve the files out of ./public as our main files
app.use('/public', express.static(__dirname + '/public'));

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/public/index-local.html');
});

//Server Setup
const port = process.env.PORT || 6020;
const server = http.createServer(app);

server.listen(port, function() {
    console.log("React To Do App listening on: ", port);
});
