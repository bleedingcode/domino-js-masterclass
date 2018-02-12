const axios = require('axios');
const Globals = require('./utils/globals');

module.exports = {
  type: "credentials",
  users: function(username) {
      return new Promise(function(resolve) {
        var user = { username: username, permissions: "*" };
        resolve(user);
      });
  },
  authenticate: function(username,password) {
      return new Promise(function(resolve) {
        if(Globals.config === null){
          resolve(null);
        }else{
          var user = null;
          var params = {
            method: Globals.config.authService.method,
            url:Globals.config.authService.url
          };
  
          //Determine User/Pwd properties
          params.url += "?";
          params.url += Globals.config.authService.queryParamNameUser + "=" + username;
          params.url += "&";
          params.url += Globals.config.authService.queryParamNamePassword + "=" + password;
  
          axios.request(params)
          .then(function (response) {
            user = { username: response.data.username, permissions: "*" };
            resolve(user);
          }).catch(function (error) {
            console.log("Authentication Error");
            console.log(error);
            resolve(null);
          });          
        }
      });
  },
  default: function() {
      return new Promise(function(resolve) {
          resolve(null);
      });
  }
}