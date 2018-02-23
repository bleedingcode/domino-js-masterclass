let hostName = location.hostname;
let protocol = location.protocol;
let port = location.port;

let deployment = {
  deployType:"local",
  minikube:{
    apiUrl:protocol + "//" + hostName + ":30010",
    nodeRedUrl:protocol + "//" + hostName + ":30011",
    nodeRedApiUrl:protocol + "//" + hostName + ":30011/api",
    wsUrl:protocol + "//" + hostName + ":30021",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  },
  local:{
    apiUrl:"http//192.168.99.100:30010",
    nodeRedUrl:"http//192.168.99.100:30011",
    nodeRedApiUrl:"http//192.168.99.100:30011/api",
    wsUrl:protocol + "//" + hostName + ":6021",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  }
};

const globals = {
    deployType:deployment.deployType,
    ws:null,
    wsConnected:false,
    dispatch:null,
    user:{
      socketId:"",
      username:"",
      password:"",
      commonName:""
    },
    title:"React To Do Portal",
    version:"0.0.1",
    releaseDate:"22nd February 2018",
    apiUrl:deployment[deployment.deployType].apiUrl,
    imgUrl:deployment[deployment.deployType].imgUrl,
    nodeRedUrl:deployment[deployment.deployType].nodeRedUrl,
    nodeRedApiUrl:deployment[deployment.deployType].nodeRedApiUrl,
    wsUrl:deployment[deployment.deployType].wsUrl
};

export default globals;