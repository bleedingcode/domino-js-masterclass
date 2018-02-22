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
	      		author:"",
				taskName:"",
				description:"",
				dueDate:"",
				responsiblePerson:"",
				priority:"",
				status:"New"
	    	}
		},
		activeEntry:{},
		data:[]
	},	
	signInForm:{
		email:"",
		password:""
	}
}

export default tempData;
