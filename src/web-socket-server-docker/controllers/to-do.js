const Globals = require('../utils/globals');
const _ = require('lodash');

const fetchAllData = function(data, callback){
    let reqType = data.reqType;

    let result = {
        success:true,
        messages: [],
        reqType: reqType,
        storeList: [],
        data: []
    };

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
  
    //First we get the Stores Ascended
    params.headers["route-key"] = Globals.config.agilite.routeFetchStores;

    _fetchStores(params, function(err, storeList){
        if(err){
            result.success = false;
            result.messages.push(err);
            return callback(result);
        }
    
        result.storeList = storeList;

        //Next, we loop through Store List to fetch To Dos for each Store
        params.headers["route-key"] = Globals.config.agilite.routeFetchToDos;

        _fetchToDos(params, reqType, storeList, [], 0, function(err2, toDoList){
            if(err2){
                result.success = false;
                result.messages.push(err2);
                return callback(result);
            }

            //Sort To Do List
            toDoList = _.orderBy(toDoList, ['text'], ['asc']);

            //Finalize the Result
            result.data = toDoList;

            callback(result);
        });
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
        console.log(response.data);
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
            console.log(err.response);
            callback(err.response.data);
        }else{
            console.log(err.stack);
            callback({success:false, messages:[err.stack], data:{}});
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
            console.log(err.stack);
            callback({success:false, messages:[err.stack], data:{}});
        }
    });
  
    return null;
}
exports.updateRecord = updateRecord;

//Private Functions
const _fetchStores = function(params, callback){
    const Axios = require('axios');
    let storeList = [];
    let isUnauthorized = false;

    Axios.request(params)
    .then(function (response) {
        try {
            if(response.data.data.substring(0, 1) === "<"){
                isUnauthorized = true;
            }
          } catch (error) {}
      
          if(isUnauthorized){
              return callback("Incorrect Username/Password. Please try again", null);
          }else{
            //Fix up Data To only return a list of Stores sorted ascending
            for(var x in response.data.data){
                storeList.push(
                    {
                        text:response.data.data[x].title,
                        value:response.data.data[x].replicaId
                    }
                )
            }
        }          

        storeList = _.orderBy(storeList, ['text'], ['asc']);

        return callback(null, storeList);

    })
    .catch(function (err) {
        if(err.response){
            callback(err.response.data, null);
        }else{
            console.log(err.stack);
            callback(err.stack, null);
        }
    });
}

const _fetchToDos = function(params, reqType, storeList, toDoList, startIndex, callback){
    const Axios = require('axios');
    let result = [];

    params.data.storeId = storeList[startIndex].value;

    switch(reqType){
        case "1"://Fetch All Data New
            params.data.status = "New";
            break;
        case "2"://Fetch All Data Assigned
            params.data.status = "Reassigned";
            break;        
        case "3"://Fetch All Data Complete
            params.data.status = "Complete";
            break;
        case "4"://Fetch All Data Overdue 
            params.data.status = "Overdue";
            break;               
    }

    Axios.request(params)
    .then(function (response) {
        //Fix up Data Entries to be compatible with React
        for(var x in response.data.data){
            toDoList.push(
                {
                    _id:response.data.data[x].metaversalId,
                    data:response.data.data[x]
                }
            )
        }

        startIndex++;

        if(startIndex >= storeList.length){
            callback(null, toDoList);
        }else{
            _fetchToDos(params, reqType, storeList, toDoList, startIndex, callback);
        }
    })
    .catch(function (err) {
        if(err.response){
            callback(err.response.data, null);
        }else{
            console.log(err.stack);
            callback(err.stack, null);
        }
    });
}