let hostName = location.hostname;
let protocol = location.protocol;
let port = location.port;

let deployment = {
  deployType:"minikube",
  minikube:{
    apiUrl:protocol + "//" + hostName + ":30010",
    nodeRedUrl:protocol + "//" + hostName + ":30011",
    nodeRedApiUrl:protocol + "//" + hostName + ":30011/api",
    imgUrl:protocol + "//" + hostName + ":" + port + "/public/images"    
  },
};

const globals = {
    deployType:deployment.deployType,
    title:"React To Do",
    version:"0.0.1",
    releaseDate:"22nd February 2018",
    apiUrl:deployment[deployment.deployType].apiUrl,
    imgUrl:deployment[deployment.deployType].imgUrl,
    nodeRedUrl:deployment[deployment.deployType].nodeRedUrl,
    nodeRedApiUrl:deployment[deployment.deployType].nodeRedApiUrl
};

export default globals;
