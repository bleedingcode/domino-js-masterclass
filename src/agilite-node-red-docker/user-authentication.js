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
            url:Globals.config.authService.url,
            headers:Globals.config.authService.headers,
            data:{credentials:Buffer.from(username + ":" + password).toString('base64')}
          };

          axios.request(params)
          .then(function (response) {
            user = { username: response.data.commonName, permissions: "*" };
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