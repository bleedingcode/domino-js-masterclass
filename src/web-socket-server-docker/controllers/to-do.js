const Globals = require('../utils/globals');
const _ = require('lodash');

const fetchAllData = function(connectionType, data, callback){
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
exports.fetchAllData = fetchAllData;

const createRecord = function(connectionType, data, callback){
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
            credentials:Buffer.from(data.username + ":" + data.password).toString('base64'),
            record:data.record.data,
            custom:data.record.custom
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
exports.createRecord = createRecord;

const updateRecord = function(connectionType, data, callback){
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
            credentials:Buffer.from(data.username + ":" + data.password).toString('base64'),
            record:data.record.data,
            custom:data.record.custom
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
exports.updateRecord = updateRecord;
