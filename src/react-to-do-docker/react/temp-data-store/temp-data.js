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
	toDoStore:{
		dataTemplate:{
			_id:"",
			custom:customTemplate,
			data:{
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
