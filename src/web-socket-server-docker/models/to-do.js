const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const templateSchema = new Schema({
  createdBy:String,
  modifiedBy:String,
  data:{
    isActive:Boolean,
    key:{
      unique:true,
      type:String,
      trim:true
    },
    description:{
      type:String,
      trim:true
    },
    groupName:{
      type:String,
      trim:true
    },
    data:String,
    mode:String,
    theme:String
  }
}, {timestamps:true});

module.exports = templateSchema;
