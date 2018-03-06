const Globals = require('../utils/globals');

const fetchAllData = function(data, callback){
    const Axios = require('axios');
    const _ = require('lodash');

    let reqType = data.reqType;
    let isUnauthorized = false;
    let resultData = [];
    let resultData2 = [];

    let params = {
        method:"post",
        url:Globals.config.agilite.apiUrl + Globals.config.agilite.urlSuffixConnectors,
        headers:{
          "Content-Type":"application/json",
          "api-key": Globals.config.agilite.apiKey,
          "profile-key": Globals.config.agilite.dominoProfileKey
        },
        data:{
          credentials:Buffer.from(data.username + ":" + data.password).toString('base64')
        }
    };
  
    switch(data.reqType){
        case "1"://Fetch All Data New
            params.headers["route-key"] = Globals.config.agilite.routeFetchToDosNew;
            break;
        case "2"://Fetch All Data Assigned
            params.headers["route-key"] = Globals.config.agilite.routeFetchToDosAssigned;
            break;        
        case "3"://Fetch All Data Complete
            params.headers["route-key"] = Globals.config.agilite.routeFetchToDosComplete;
            break;
        case "4"://Fetch All Data Overdue 
            params.headers["route-key"] = Globals.config.agilite.routeFetchToDosOverdue;
            break;               
    }

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
                        _id:response.data.data[x].metaversalId,
                        data:response.data.data[x]
                    }
                )
        }

        response.data.data = resultData;

        //Fetch Store List Next
        params.headers["route-key"] = Globals.config.agilite.routeFetchStores;

        Axios.request(params)
        .then(function (response2) {
            //Fix up Data To only return a list of Stores sorted ascending
            for(var x in response2.data.data){
                    resultData2.push(
                        {
                            text:response2.data.data[x].title,
                            value:response2.data.data[x].replicaId
                        }
                    )
            }
    
            resultData2 = _.orderBy(resultData2, ['text'], ['asc']);
            response.data.storeList = resultData2;
    
            response.data.reqType = reqType;
            callback(response.data);
        })
        .catch(function (err2) {
            if(err2.response){
                callback(err2.response.data);
            }else{
                callback({success:false, messages:[err], data:{}});
            }
        });        
      }
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
          "route-key": Globals.config.agilite.routeCreateToDo
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
                _id:response.data.data.metaversalId,
                custom:customObject,
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
          "route-key": Globals.config.agilite.routeUpdateToDo
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
                _id:response.data.data.metaversalId,
                custom:customObject,
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