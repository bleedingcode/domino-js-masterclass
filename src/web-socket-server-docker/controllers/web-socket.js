const Store = require('./store');
const ToDo = require('./to-do');
const Utils = require('../utils/utilities');

const initEvents = function(io){
    io.on('connection', function (socket) {
        //Return Session ID back to user when connection is established
        io.sockets.sockets[socket.id].emit("init-user-session", socket.id);

        //Process requests related to To Do App in General
        socket.on('to-do-app-requests', function (data) {
            switch(data.reqType){
                case "1"://Sign In User
                    Utils.authenticateUser(data, function(result){
                        io.sockets.sockets[data.socketId].emit("to-do-app-response", result);
                    });
                    break;
            }
        });

        //Process requests related to To Do Store
        socket.on('to-do-store-requests', function (data) {
            switch(data.reqType){
                case "1"://Fetch All Data
                    Store.fetchAllData(data, function(result){
                        io.sockets.sockets[data.socketId].emit("to-do-store-response", result);
                    });
                    break;
                case "2"://Create Record
                    Store.createRecord(data, function(result){
                        io.sockets.sockets[data.socketId].emit("to-do-store-response", result);
                    });
                    break;
                case "3"://Update Record
                    Store.updateRecord(data, function(result){
                        console.log(result);
                        io.sockets.sockets[data.socketId].emit("to-do-store-response", result);
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
                    ToDo.fetchAllData(data, function(result){
                        io.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
                    break;
                case "5"://Create Record
                    ToDo.createRecord(data, function(result){
                        io.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
                    break;
                case "6"://Update Record
                    ToDo.updateRecord(data, function(result){
                        console.log(result);
                        io.sockets.sockets[data.socketId].emit("to-do-response", result);
                    });
                    break;                                       
            }
        });        
    }); 
    return null;
};

exports.initEvents = initEvents;