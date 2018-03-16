let hostName = location.hostname;
let protocol = location.protocol;
let port = location.port;

let deployment = {
  deployType:"local",
  minikube:{
    apiUrl:protocol + "//" + hostName + ":30010",
    nodeRedUrl:protocol + "//" + hostName + ":30011",
    wsUrl:protocol + "//" + hostName + ":30021",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  },
  local:{
    apiUrl:"http//192.168.99.100:30010",
    nodeRedUrl:"http//192.168.99.100:30011",
    wsUrl:protocol + "//" + hostName + ":6021",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  },
  azure:{
    apiUrl:protocol + "//" + hostName + ":30010",
    nodeRedUrl:protocol + "//" + hostName + ":30011",
    wsUrl:"http://52.234.229.85:30021",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  }  
};

const globals = {
    deployType:deployment.deployType,
    ws:null,
    dispatch:null,
    tempCallback:null,
    user:{
      socketId:"",
      username:"",
      password:"",
      commonName:""
    },
    appKey:"home",
    userList:[],
    storeList:[],
    title:"React To Do Portal",
    version:"0.0.4",
    releaseDate:"16th March 2018",
    apiUrl:deployment[deployment.deployType].apiUrl,
    imgUrl:deployment[deployment.deployType].imgUrl,
    nodeRedUrl:deployment[deployment.deployType].nodeRedUrl,
    wsUrl:deployment[deployment.deployType].wsUrl
};

export default globals;