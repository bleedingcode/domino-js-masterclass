const Moment = require('./controllers/moment');

module.exports = function(app){
  //Lodash

  //Moment
  app.get('/moment/now', function(req, res, next){
    Moment.getCurrentDateTime(req, res);
  });  
}