const http = require('http');
const bodyParser = require('body-parser');
const cors = require('cors');
const express = require('express');
const router = require('./router'); 
const app = express();
const port = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());
router(app);

let server = http.createServer(app);

server.listen(port, function() {
    console.log("NodeJS API Quickstart listening on Port: ", port);
}); 