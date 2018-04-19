/* 
 * Object with defaults for temporary data store
 */

/* 
 * Object to hold custom settings for our app's validation and management of a new ToDo
 */
const customTemplate = {
	action:'',
	tempIndex:0,
	tempId:'',
	isSavedDoc:false,
	isNewDoc:true,
	validationPassed:true,
	errorMessages:[],
	status:'new'
};

/* 
 * Our temp data store
 */
const tempData = {
	// TODO: Check what this does
	globals:{
		resetToDo:true
	},
	// An object to hold a ToDo (a Notes document in the backend)
	toDo:{
		dataTemplate:{
			_id:'',
			custom:customTemplate,
			data:{
				metaversalId:'',
				author:'',
				storeId:'',
				taskName:'',
				description:'',
				dueDate:null,
				priority:'',
				assignedTo:'',
				status:'New'
			}
		},
		activeEntry:{},
		data:[]
	},	
	// An object to hold a ToDo store (an NSF in the backend)
	toDoStore:{
		dataTemplate:{
			_id:'',
			custom:customTemplate,
			data:{
				replicaId:'',
				title:'',
				name:'',
				type:''
			}
		},
		activeEntry:{},
		data:[]
	},	
	// Holding details to authenticate against the backend (e.g. Domino)
	signInForm:{
		username:'',
		password:''
	}
}

export default tempData;
