const Enums = require('./enums');
const fs = require('fs');
const path = require('path');
const Globals = require('./globals');

const generateBase64Authorization = function(username, password){
  let result = "Basic ";
  let token = new Buffer(username + ":" + password).toString('base64');
  result += token;

  return result;
};

const mustacheConvert = function(data, params){
  const Mustache = require('mustache');
  return Mustache.render(data, params);
};

const returnVariableType = function(input){
  let stringConstructor = "test".constructor;
  let arrayConstructor = [].constructor;
  let objectConstructor = {}.constructor;
  let booleanConstructor = true.constructor;

  let result = null;

  if (input === undefined) {
    result = "undefined";
  }else if(input === null){
    result = null;
  }else if (input.constructor === stringConstructor) {
    result = "String";
  }else if (input.constructor === arrayConstructor) {
    result = "Array";
  }else if (input.constructor === objectConstructor) {
    result = "Object";
  }else if (input.constructor === booleanConstructor) {
    result = "Boolean";
  }

  return result;
};

const loadConfig = function(callback){
  const Globals = require('./globals');
  const path = require('path');
  const fs = require('fs');

  let result = "";
  let filePath = "";

  try {    
    filePath = path.join(__dirname, "../config.json/config.json");

    fs.readFile(filePath, 'utf8', function (err,data) {
      if (err) {
        console.log("Config File Not Found");
        return callback();
      }

      try {
        result = JSON.parse(data);
        Globals.config = result;
        console.log("Config Loaded Successfully");
      } catch (e) {
        console.log("Parsing Config as JSON Failed");
      }

      return callback();
    });
  } catch (e) {
    console.log(e.stack);
    return callback();
  }

  return null;
};

const authenticateUser = function(connectionType, data, callback){
  const Axios = require('axios');

  let params = {
      method:"post",
      url:Globals.config.nodeRedWebhook,
      headers:{
        "Content-Type":"application/json",
        "connection-type": connectionType,
        "req-type":data.reqType
      },
      data:{
        credentials:Buffer.from(data.username + ":" + data.password).toString('base64')
      }
  };

  Axios.request(params)
  .then(function (response) {
    callback(response.data);
  })
  .catch(function (err) {
      if(err.response){
          callback(err.response.data);
      }else{
        console.log(err.stack);
        callback({success:false, messages:[err.stack], data:{}});
      }
  });

  return null;
}

exports.generateBase64Authorization = generateBase64Authorization;
exports.mustacheConvert = mustacheConvert;
exports.returnVariableType = returnVariableType;
exports.loadConfig = loadConfig;
exports.authenticateUser = authenticateUser;