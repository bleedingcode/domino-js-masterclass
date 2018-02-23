import tempData from '../temp-data-store/temp-data';
import globals from '../globals';
import axios from 'axios';

/*
  PUBLIC FUNCTIONS
*/
export const postPendingData = (key, entry, reqType) => {
  let data = [];
  let url = "";
  let canRun = false;

  switch (key) {
    case "to-do":
      data = tempData.toDo.data;

      if(data.length > 0){
        url = `${globals.apiUrl}/todo/data`;
        canRun = true;
      }
      break;
  }

  if(canRun){
    var config = {
      headers: {"api-key": localStorage.getItem("token"), "Content-Type": "application/json"}
    };

    switch(reqType){
      case "post":
        return axios.post(url, entry, config);
        break;
      case "put":
        config.headers["record-id"] = entry._id;
        return axios.put(url, entry, config);
        break;``
      case "delete":
        config.headers["record-id"] = entry._id;//Cannot pass body content on a delete request
        return axios.delete(url, config);
        break;
    }
  }

  return null;
}
