const Globals = require('../utils/globals');

const fetchAllData = function(data, callback){
    const Axios = require('axios');
    let reqType = data.reqType;
    let isUnauthorized = false;
    let resultData = [];

    let params = {
        method:"post",
        url:Globals.config.agilite.apiUrl + Globals.config.agilite.urlSuffixConnectors,
        headers:{
          "Content-Type":"application/json",
          "api-key": Globals.config.agilite.apiKey,
          "profile-key": Globals.config.agilite.dominoProfileKey,
          "route-key": Globals.config.agilite.routeFetchStores
        },
        data:{
          credentials:Buffer.from(data.username + ":" + data.password).toString('base64')
        }
    };
  
    Axios.request(params)
    .then(function (response) {
      try {
        if(response.data.data.substring(0, 1) === "<"){
            isUnauthorized = true;
        }
      } catch (error) {}
  
      if(isUnauthorized){
        response.data.success = false;
        response.data.messages.push("Incorrect Username/Password. Please try again");
      }else{
        //Fix up Data Entries to be compatible with React
        for(var x in response.data.data){
                resultData.push(
                    {
                        _id:response.data.data[x].replicaId,
                        data:response.data.data[x]
                    }
                )
        }

        response.data.data = resultData;
      }

      response.data.reqType = reqType;

      callback(response.data);
    })
    .catch(function (err) {
        if(err.response){
            callback(err.response.data);
        }else{
            callback({success:false, messages:[err], data:{}});
        }
    });
  
    return null;
  }
  exports.fetchAllData = fetchAllData;

const createRecord = function(data, callback){
    const Axios = require('axios');
    let customObject = data.record.custom;
    let reqType = data.reqType;
    let resultData = {};
    let isUnauthorized = false;

    let params = {
        method:"post",
        url:Globals.config.agilite.apiUrl + Globals.config.agilite.urlSuffixConnectors,
        headers:{
          "Content-Type":"application/json",
          "api-key": Globals.config.agilite.apiKey,
          "profile-key": Globals.config.agilite.dominoProfileKey,
          "route-key": Globals.config.agilite.routeCreateStore
        },
        data:{
          credentials:Buffer.from(data.username + ":" + data.password).toString('base64'),
          record:data.record.data
        }
    };
  
    Axios.request(params)
    .then(function (response) {
        try {
            if(response.data.data.substring(0, 1) === "<"){
                isUnauthorized = true;
            }
        } catch (error) {}

        if(isUnauthorized){
            response.data.success = false;
            response.data.messages.push("Incorrect Username/Password. Please try again");
        }else{
            resultData = {
                _id:response.data.data.replicaId,
                data:response.data.data
            }
    
            response.data.data = resultData;
        }

        response.data.reqType = reqType;
        callback(response.data);
    })
    .catch(function (err) {
        if(err.response){
            callback(err.response.data);
        }else{
            callback({success:false, messages:[err], data:{}});
        }
    });
  
    return null;
}
exports.createRecord = createRecord;

const updateRecord = function(data, callback){
    const Axios = require('axios');
    let customObject = data.record.custom;
    let reqType = data.reqType;
    let resultData = {};
    let isUnauthorized = false;

    let params = {
        method:"post",
        url:Globals.config.agilite.apiUrl + Globals.config.agilite.urlSuffixConnectors,
        headers:{
          "Content-Type":"application/json",
          "api-key": Globals.config.agilite.apiKey,
          "profile-key": Globals.config.agilite.dominoProfileKey,
          "route-key": Globals.config.agilite.routeUpdateStore
        },
        data:{
          credentials:Buffer.from(data.username + ":" + data.password).toString('base64'),
          record:data.record.data
        }
    };
  
    Axios.request(params)
    .then(function (response) {
        try {
            if(response.data.data.substring(0, 1) === "<"){
                isUnauthorized = true;
            }
        } catch (error) {}

        if(isUnauthorized){
            response.data.success = false;
            response.data.messages.push("Incorrect Username/Password. Please try again");
        }else{
            resultData = {
                _id:response.data.data.replicaId,
                data:response.data.data
            }
    
            response.data.data = resultData;
        }

        response.data.reqType = reqType;
        callback(response.data);
    })
    .catch(function (err) {
        if(err.response){
            callback(err.response.data);
        }else{
            callback({success:false, messages:[err], data:{}});
        }
    });
  
    return null;
}
exports.updateRecord = updateRecord;