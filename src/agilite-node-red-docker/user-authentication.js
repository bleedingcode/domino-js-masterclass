const axios = require('axios');

module.exports = {
  type: "credentials",
  users: function(username) {
      console.log("Running Users - " + username);
      return new Promise(function(resolve) {
        var user = { username: username, permissions: "*" };
        resolve(user);
      });
  },
  authenticate: function(username,password) {
    console.log("Running Authenticate - " + username + " - " + password);
      return new Promise(function(resolve) {
        var params = {
          method: "get",
          url: "https://agilite-node-red.eu-gb.mybluemix.net/user/login?username=" + username + "&password=" + password
        };

        axios.request(params)
        .then(function (response) {
          var user = { username: response.data.username, permissions: "*" };
          console.log(user);
          resolve(user);
        }).catch(function (error) {
          resolve(null);
        });
      });
  },
  default: function() {
      console.log("Running Default");
      return new Promise(function(resolve) {
          // Resolve with the user object for the default user.
          // If no default user exists, resolve with null.
          resolve(null);
      });
  }
}