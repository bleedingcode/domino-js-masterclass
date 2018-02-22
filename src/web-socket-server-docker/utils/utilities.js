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
  let deployType = "";
  let result = "";
  let filePath = "";

  try {
    //Check if Deploy Type can be resolved for Bluemix
    deployType = process.env.DEPLOY_TYPE;

    if(!deployType){
      //Try Local
      require('dotenv').config()
      deployType = process.env.DEPLOY_TYPE;

      //Default deployType to MiniKube if still not defined
      if(!deployType){
        deployType = Enums.DEPLOY_TYPE_MINIKUBE;
      }
    }

    //If we get here, Get Config Details
    filePath = path.join(__dirname, "../config_" + deployType + ".js");

    fs.readFile(filePath, 'utf8', function (err,data) {
      if (err) {
        console.log("Config File Not Found");
        callback(false);
      }

      try {
        result = JSON.parse(data);
        Globals.config = result;
      } catch (e) {
        console.log("Parsing Config as JSON Failed");
        callback(false);
      }

      callback(true);
    });
  } catch (e) {
    callback(false);
  }

  return null;
};

const initAAWebhook = function(params, callback){
  const Axios = require('axios');
  
  let data = {
      sessionId:params.socketId,
      userId:params.userId,
      username:params.username,
      content:params.msg
  };

  var params = {
      method:"post",
      url:params.webhookURL,
      headers:{"Content-Type":"application/json"},
      data:data
  };
  
  Axios.request(params)
  .then(function (response) {
      console.log(response.data);
      if(response.data.success){
          callback(null, response.data);
      }else{
          callback(response.data.message, null);
      }
  })
  .catch(function (err) {
      console.log(err);
      if(err.response){
          callback(err.response.data.message, null);
      }else{
          callback(err, null);
      }
  });

  return null;
}

exports.generateBase64Authorization = generateBase64Authorization;
exports.mustacheConvert = mustacheConvert;
exports.returnVariableType = returnVariableType;
exports.loadConfig = loadConfig;
exports.initAAWebhook = initAAWebhook;