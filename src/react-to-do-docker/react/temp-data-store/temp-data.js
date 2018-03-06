const customTemplate = {
	action:"",
	tempIndex:0,
	tempId:"",
	isSavedDoc:false,
	isNewDoc:true,
	validationPassed:true,
	errorMessages:[],
	status:'new'
};

const tempData = {
	globals:{
		resetToDo:true
	},
	toDo:{
		dataTemplate:{
			_id:"",
			custom:customTemplate,
			data:{
				metaversalId:"",
	      		author:"",
				taskName:"",
				description:"",
				dueDate:"",
				priority:"",
				assignedTo:"",
				status:"New"
	    	}
		},
		activeEntry:{},
		data:[]
	},	
	toDoStore:{
		dataTemplate:{
			_id:"",
			custom:customTemplate,
			data:{
				replicaId:"",
	      		title:"",
				name:"",
				type:""
	    	}
		},
		activeEntry:{},
		data:[]
	},	
	signInForm:{
		username:"",
		password:""
	}
}

export default tempData;
