const moment = require('moment');

const getCurrentDateTime = function(req, res){
  return res.status(200).send(moment());
};

exports.getCurrentDateTime = getCurrentDateTime;