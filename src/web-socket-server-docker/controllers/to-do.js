const templateSchema = require('../models/numbering');
const generalTemplate = require("../data-templates/general");
const dbCon = require('../services/dbConnection');
const Utils = require('../utils/utilities');
const Enums = require('../utils/enums');
const Log = require('./log');
const async = require('async');

//Create Queue Manager for Numbering
const numberingQueue = async.queue(function(task, callback) {
  _generateNumberExtended(task.param1, task.param2, function(result){
    callback(null, result);
  });
});

numberingQueue.drain = function() {
  console.log('all items have been processed');
};

const getAll = function(req, res){
  let apiResult = JSON.parse(JSON.stringify(generalTemplate.apiResult));
  let authDb = null;
  let Schema = null;

  try {
    apiResult.logEntry = Log.setupLogEntry(req);
    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING, templateSchema);

    Schema.find({}, function(err, records) {
      if(err){
        apiResult.statusCode = 400;
        apiResult.messages.push(err);
      }else{
        apiResult.data = records;
      }

      //Nullify Objects
      authDb = null;
      Schema = null;

      Utils.processApiResponse(req, res, apiResult);
    });
  } catch (e) {
    apiResult.statusCode = 400;
    apiResult.messages.push(e.stack);
    Utils.processApiResponse(req, res, apiResult);
  }

  return null;
};

const createRecord = function(req, res){
  const dataTemplate = require("../data-templates/numbering");

  let apiResult = JSON.parse(JSON.stringify(generalTemplate.apiResult));
  let authDb = null;
  let Schema = null;
  let data = {};
  let entry = {};
  let model = {};
  let customObject = null;

  try{
    apiResult.logEntry = Log.setupLogEntry(req);
    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING, templateSchema);

    //Get Data from Request
    data = req.body;

    //Validate Data Object properties
    if(!req.body.data){
      apiResult.statusCode = 400;
      apiResult.messages.push("Invalid Request Body JSON data provided");         
    }

    if(apiResult.statusCode === 422 || apiResult.statusCode === 400){
      return Utils.processApiResponse(req, res, apiResult);
    }

    //Validate required values
    if(Utils.returnVariableType(req.body.data.isActive) !== "Boolean"){
      apiResult.statusCode = 422;
      apiResult.messages.push("Boolean property 'isActive' not properly defined");
    }

    if(!req.body.data.key){
      apiResult.statusCode = 422;
      apiResult.messages.push("Please provide a valid Profile 'key'");
    }

    if(!req.body.data.name){
      apiResult.statusCode = 422;
      apiResult.messages.push("Please provide a valid Profile 'name'");
    }

    if(apiResult.statusCode === 422 || apiResult.statusCode === 400){
      return Utils.processApiResponse(req, res, apiResult);
    }

    //Default optional properties
    if(!req.body.data.description){
      req.body.data.description = "";
    }

    if(!req.body.data.groupName){
      req.body.data.groupName = "";
    }

    if(!req.body.data.prefix){
      req.body.data.prefix = "";
    }

    if(!req.body.data.suffix){
      req.body.data.suffix = "";
    }

    if(!req.body.data.startAt){
      req.body.data.startAt = 1;
    }
   
    if(!req.body.data.incrementBasedOn){
      req.body.data.incrementBasedOn = "";
    }

    if(!req.body.data.minLength){
      req.body.data.minLength = 1;
    }

    //Check if there's a custom object that needs to be returned back to user
    if(data.custom){
      customObject = JSON.parse(JSON.stringify(data.custom));
    }

    entry = JSON.parse(JSON.stringify(dataTemplate.core));

    entry.createdBy = req.user.firstName + " " + req.user.lastName;
    entry.modifiedBy = req.user.firstName + " " + req.user.lastName;
    entry.data = data.data;

    model = new Schema(entry);

    Schema.create(model, function(err, record) {
      if(err){
        apiResult.statusCode = 400;

        if(err.code === 11000){//Duplicate Key Found
          apiResult.messages.push("Duplicate Profile Key found. Please revise");
        }else{
          apiResult.messages.push(err);
        }
      }else{
        if(customObject){
          entry = JSON.parse(JSON.stringify(record));
          entry.custom = customObject;
        }else{
          entry = record;
        }

        apiResult.data = entry;
      }

      //Nullify Objects
      authDb = null;
      Schema = null;
      model = null;

      Utils.processApiResponse(req, res, apiResult);
    });
  }catch(e){
    apiResult.statusCode = 400;
    apiResult.messages.push(e.stack);
    Utils.processApiResponse(req, res, apiResult);
  }

  return null;
};

const updateRecord = function(req, res){
  let apiResult = JSON.parse(JSON.stringify(generalTemplate.apiResult));
  let authDb = null;
  let Schema = null;
  let id = "";
  let data = {};
  let entry = {};
  let customObject = null;

  try{
    apiResult.logEntry = Log.setupLogEntry(req);
    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING, templateSchema);

    //Check for record-id in header
    if(req.headers["record-id"] !== undefined){
      id = req.headers["record-id"];
    }else{
      apiResult.statusCode = 400;
      apiResult.messages.push("No Id was specified in the 'record-id' header parameter");
      return Utils.processApiResponse(req, res, apiResult);
    }

    //Get Data from Request
    data = req.body;

    //Validate Data Object properties
    if(!req.body.data){
      apiResult.statusCode = 400;
      apiResult.messages.push("Invalid Request Body JSON data provided");     
    }

    if(apiResult.statusCode === 422 || apiResult.statusCode === 400){
      return Utils.processApiResponse(req, res, apiResult);
    }

    //Validate required values
    if(Utils.returnVariableType(req.body.data.isActive) !== "Boolean"){
      apiResult.statusCode = 422;
      apiResult.messages.push("Boolean property 'isActive' not properly defined");
    }

    if(!req.body.data.key){
      apiResult.statusCode = 422;
      apiResult.messages.push("Please provide a valid Profile 'key'");
    }

    if(!req.body.data.name){
      apiResult.statusCode = 422;
      apiResult.messages.push("Please provide a valid Profile 'name'");
    }

    if(apiResult.statusCode === 422 || apiResult.statusCode === 400){
      return Utils.processApiResponse(req, res, apiResult);
    }

    //Default optional properties
    if(!req.body.data.description){
      req.body.data.description = "";
    }

    if(!req.body.data.groupName){
      req.body.data.groupName = "";
    }

    if(!req.body.data.prefix){
      req.body.data.prefix = "";
    }

    if(!req.body.data.suffix){
      req.body.data.suffix = "";
    }

    if(!req.body.data.startAt){
      req.body.data.startAt = 1;
    }
   
    if(!req.body.data.incrementBasedOn){
      req.body.data.incrementBasedOn = "";
    }

    if(!req.body.data.minLength){
      req.body.data.minLength = 1;
    }

    //Check if there's a custom object that needs to be returned back to user
    if(data.custom){
      customObject = JSON.parse(JSON.stringify(data.custom));
    }

    entry = {
      _id: id,
      modifiedBy: req.user.firstName + " " + req.user.lastName,
      data: data.data
    };

    Schema.findByIdAndUpdate(id, entry, {new:true,upsert:true}, function(err, record) {
      if(err){
        apiResult.statusCode = 400;

        if(err.kind && (err.kind === "ObjectId")){
          apiResult.messages.push("Record with id: '" + id + "' cannot be found");
        }else{
          apiResult.messages.push(err);
        }
      }else{
        if(customObject){
          entry = JSON.parse(JSON.stringify(record));
          entry.custom = customObject;
        }else{
          entry = record;
        }

        apiResult.data = entry;
      }

      //Nullify Objects
      id = "";
      authDb = null;
      Schema = null;

      Utils.processApiResponse(req, res, apiResult);
    });
  }catch(e){
    apiResult.statusCode = 400;
    apiResult.messages.push(e.stack);
    Utils.processApiResponse(req, res, apiResult);
  }

  return null;
};

const deleteRecord = function(req, res){
  const numberingCounterSchema = require('../models/numbering-counter');

  let apiResult = JSON.parse(JSON.stringify(generalTemplate.apiResult));
  let authDb = null;
  let Schema = null;
  let id = "";
  let NumberingCounter = null;

  try{
    apiResult.logEntry = Log.setupLogEntry(req);
    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING, templateSchema);

    //Check for record-id in header
    if(req.headers["record-id"] !== undefined){
      id = req.headers["record-id"];
    }else{
      apiResult.statusCode = 400;
      apiResult.messages.push("No Id was specified in the 'record-id' header parameter");
      return Utils.processApiResponse(req, res, apiResult);
    }

    Schema.findByIdAndRemove(id, function(err){
      if(err){
        apiResult.statusCode = 400;

        if(err.kind && (err.kind === "ObjectId")){
          apiResult.messages.push("Record with id: '" + id + "' cannot be found");
        }else{
          apiResult.messages.push(err);
        }

        Utils.processApiResponse(req, res, apiResult);
      }else{
        //Delete Numbering Counters
        NumberingCounter = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING_COUNTER, numberingCounterSchema);
        NumberingCounter.remove({ 'profileId': id }, function(){
          //Nullify Objects
          id = "";
          authDb = null;
          Schema = null;
          NumberingCounter = null;
        });

        Utils.processApiResponse(req, res, apiResult);
      }
    });
  }catch(e){
    apiResult.statusCode = 400;
    apiResult.messages.push(e.stack);
    Utils.processApiResponse(req, res, apiResult);
  }

  return null;
};

const generateNumber = function(req, res){
  numberingQueue.push({param1:req, param2:res}, function(err, apiResult) {
    return Utils.processApiResponse(req, res, apiResult);
  });  
}

const _generateNumberExtended = function(req, res, callback){
  let apiResult = JSON.parse(JSON.stringify(generalTemplate.apiResult));
  let authDb = null;
  let Schema = null;
  let profileKey = "";
  let prefix = "";
  let suffix = "";
  let dataOnly = false;

  try {
    apiResult.logEntry = Log.setupLogEntry(req);

    //Make sure there is a profile key
    if(req.headers["profile-key"] !== undefined){
      profileKey = req.headers["profile-key"];
    }else{
      apiResult.statusCode = 400;
      apiResult.messages.push("No Profile Key was specified in the 'profile-key' header parameter");
      return callback(apiResult);
    }

    //Check if Data Only needs to be returned
    if(req.headers["data-only"] !== undefined){
      dataOnly = req.headers["data-only"] === "true" ? true : false;
    }

    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(req.user.teamId + "_" + Enums.MODEL_NUMBERING, templateSchema);

    Schema.findOne({ 'data.key': profileKey, 'data.isActive':true }, function(err, doc) {
      if(err){
        apiResult.statusCode = 400;
        apiResult.messages.push(err);
        return callback(apiResult);
      }else if(!doc){
        apiResult.statusCode = 400;
        apiResult.messages.push("An active Numbering Profile cannot be found - " + profileKey);
        return callback(apiResult);
      }else{
        //Generate Prefix and Suffix Values
        if(doc.data.prefix !== ""){
          prefix = Utils.mustacheConvert(doc.data.prefix, req.body);
        }

        if(doc.data.suffix !== ""){
          suffix = Utils.mustacheConvert(doc.data.suffix, req.body);
        }

        _getCounterDocByKeys(req.user.teamId, doc._id, prefix, suffix, doc, function(err2, newNumber){
          if(err2){
            apiResult.statusCode = 400;
            apiResult.messages.push(err2);
          }else{
            apiResult.data = newNumber;

            if(dataOnly){
              apiResult.contentType = "text/plain";
            }
          }

          //Nullify Objects
          authDb = null;
          Schema = null;
          profileKey = null;
          prefix = null;
          suffix = null;
          dataOnly = null;

          return callback(apiResult);
        });
      }
    });
  } catch (e) {
    apiResult.statusCode = 400;
    apiResult.messages.push(e.stack);
    return callback(apiResult);
  }

  return null;
};

//PRIVATE FUNCTIONS
const _getCounterDocByKeys = function(teamId, profileId, prefix, suffix, profileDoc, callback){
  const numberingCounterSchema = require('../models/numbering-counter');

  let authDb = null;
  let Schema = null;
  let tmpPrefix = "";
  let tmpSuffix = "";

  try {
    switch(profileDoc.data.incrementBasedOn){
      case "1"://Prefix
        tmpPrefix = prefix;
        break;
      case "2"://Suffix
        tmpSuffix = suffix;
        break;
      case "3"://Both
        tmpPrefix = prefix;
        tmpSuffix = suffix;
        break;
    }

    authDb = dbCon.getDbConnection(Enums.DB_NUMBERING);
    Schema = authDb.model(teamId + "_" + Enums.MODEL_NUMBERING_COUNTER, numberingCounterSchema);

    Schema.findOne({ 'profileId': profileId, 'prefix': tmpPrefix, 'suffix':tmpSuffix }, function(err, doc) {
      if(err){
        callback(err, null);
      }else if(!doc){
        _createNumberingCounter(profileId, tmpPrefix, tmpSuffix, prefix, suffix, profileDoc, Schema, function(err2, newNumber){
          callback(err2, newNumber);
        });
      }else{
        _updateNumberingCounter(prefix, suffix, profileDoc, doc, Schema, function(err2, newNumber){
          callback(err2, newNumber);
        });
      }
    });
  } catch (e) {
    callback(e.stack, null);
  }

  return null;
};

const _createNumberingCounter = function(profileId, tmpPrefix, tmpSuffix, prefix, suffix, profileDoc, Schema, callback){
  let newNumber = "";
  let numberingCounter = null;

  try{
    let counterDoc = {
      profileId:profileId,
      prefix:tmpPrefix,
      suffix:tmpSuffix,
      lastNumberUsed:(profileDoc.data.startAt-1)
    };

    newNumber = _generateNewNumber(profileDoc, counterDoc, prefix, suffix);

    if(!newNumber){
      return callback("An error occurred generating a new Number", null);
    }

    numberingCounter = new Schema(counterDoc);

    Schema.create(numberingCounter, function(err) {
      callback(err, newNumber);
    });
  }catch(e){
    callback(e.stack, null);
  }

  return null;
};

const _updateNumberingCounter = function(prefix, suffix, profileDoc, counterDoc, Schema, callback){
  let newNumber = "";
  let numberingCounter = null;

  try{
    newNumber = _generateNewNumber(profileDoc, counterDoc, prefix, suffix);

    if(!newNumber){
      return callback("An error occurred generating a new Number", null);
    }

    numberingCounter = new Schema(counterDoc);

    Schema.findByIdAndUpdate(counterDoc._id, counterDoc, {new:true,upsert:true}, function(err){
      callback(err, newNumber);
    });
  }catch(e){
    callback(e.stack, null);
  }

  return null;
};

const _generateNewNumber = function(profileDoc, counterDoc, prefix, suffix){
  let result = null;
  let newNumber = "";

  try{
    counterDoc.lastNumberUsed = counterDoc.lastNumberUsed + 1;

    //Format the character length of the number and apply prefix/suffix
    newNumber = Utils.padValue(counterDoc.lastNumberUsed, profileDoc.data.minLength);
    result = prefix + newNumber + suffix;
  }catch(e){
    result = null;
  }

  return result;
};

exports.getAll = getAll;
exports.createRecord = createRecord;
exports.updateRecord = updateRecord;
exports.deleteRecord = deleteRecord;
exports.generateNumber = generateNumber;
