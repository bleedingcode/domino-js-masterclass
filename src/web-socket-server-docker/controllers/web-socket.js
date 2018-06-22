const Store = require('./store');
const ToDo = require('./to-do');
const Utils = require('../utils/utilities');
let IOServer = null;

const initEvents = function(io){
    IOServer = io;

    IOServer.on('connection', function (socket) {
        //Return Session ID back to user when connection is established
        IOServer.sockets.sockets[socket.id].emit("init-user-session", socket.id);

        //Process requests related to To Do App in General
        socket.on('to-do-app-requests', function (data) {
            switch(data.reqType){
                case "1"://Sign In User
                    Utils.authenticateUser("to-do-app-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-app-response", result);
                    });
                    break;
            }
        });

        //Process requests related to To Do Store
        socket.on('to-do-store-requests', function (data) {
            switch(data.reqType){
                case "1"://Fetch All Data
                    Store.fetchAllData("to-do-store-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-store-response", result);
                    });
                    break;
                case "2"://Create Record
                    Store.createRecord("to-do-store-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-store-response", result);
                    });
                    break;
                case "3"://Update Record
                    Store.updateRecord("to-do-store-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-store-response", result);
                    });
                    break;                                       
            }
        });

        //Process requests related to To Dos
        socket.on('to-do-requests', function (data) {
            switch(data.reqType){
                case "1"://Fetch All Data New
                case "2"://Fetch All Data Assigned
                case "3"://Fetch All Data Complete
                case "4"://Fetch All Data Overdue
                    ToDo.fetchAllData("to-do-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
                    break;
                case "5"://Create Record
                    ToDo.createRecord("to-do-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
                    break;
                case "6"://Update Record
                    ToDo.updateRecord("to-do-requests", data, function(result){
                        IOServer.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
					break;
            }
        });        
    }); 
    return null;
};
exports.initEvents = initEvents;

const execute = function(req, res){  
    var message = "";

    try{
        if(!req.body.event){
            message = "No Web Socket event found in the request body's 'event' property";
            return res.status(400).send({success:false, messages:[message], data:{}});
        }

        if(req.body.socketId){
            //Needs to be sent to a specific user
            IOServer.sockets.sockets[req.body.socketId].emit(req.body.event, req.body.data);
        }else{
            //Needs to be broadcasted
            IOServer.emit(req.body.event, req.body.data);
        }

        res.status(200).send({success:true, messages:[], data:{}});
    }catch(e){
        res.status(400).send({success:false, messages:[e.stack], data:{}});
    }
  
    return null;
  };
  exports.execute = execute;