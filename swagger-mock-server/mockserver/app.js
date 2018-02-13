'use strict';
var express = require('express');
var middleware = require('swagger-express-middleware');
var path = require('path');
var app = express();
var FileDataStore = middleware.FileDataStore;
var store = new FileDataStore();
middleware(path.join(__dirname, '../../todo_rest_specs/todo_swagger.yaml'),
 app, function(err, middleware) {
  app.use(
    middleware.metadata(),
    middleware.CORS(),
    middleware.files(),
    middleware.parseRequest(),
    middleware.validateRequest(),
    middleware.mock(store)
  );
  app.listen(8081, function() {
    console.log('The Swagger mock server is now running at http://localhost:8000');
  });
});