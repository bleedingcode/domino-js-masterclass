import tempData from '../temp-data-store/temp-data';
import globals from '../globals';
import axios from 'axios';  // Promise based HTTP client for the browser and node.js

/*
 * Public functions to make external REST service calls
*/
export const postPendingData = (key, entry, reqType) => {
	let data = [];
	let url = '';
	let canRun = false;

	// We're only accepting one key - "to-do"
	switch (key) {
	case 'to-do':
		data = tempData.toDo.data;

		if(data.length > 0){
			url = `${globals.apiUrl}/todo/data`;
			canRun = true;
		}
		break;
	}

	if(canRun){
		// We have data, time to push. First set some headers - the api-key is the authentication token
		var config = {
			headers: {'api-key': localStorage.getItem('token'), 'Content-Type': 'application/json'}
		};

		switch(reqType){
		case 'post':  // New ToDo
			return axios.post(url, entry, config);
		case 'put':   // Update to an existing ToDo
			config.headers['record-id'] = entry._id;
			return axios.put(url, entry, config);
		case 'delete':  // Deletion of an existing ToDo
			config.headers['record-id'] = entry._id;//Cannot pass body content on a delete request
			return axios.delete(url, config);
		}
	}

	return null;
}
