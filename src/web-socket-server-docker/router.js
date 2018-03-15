const WS = require('./controllers/web-socket');

module.exports = function(app){
  //execute
  app.post('/execute', function(req, res, next){
    WS.execute(req, res);
  });  
}