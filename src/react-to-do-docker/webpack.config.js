var webpack = require('webpack');
var path = require('path');

var BUILD_DIR = path.resolve(__dirname, 'public');
var APP_DIR = path.resolve(__dirname, 'react');

var config = {
  devtool: 'cheap-module-source-map',
  entry: [
    'babel-polyfill',
    APP_DIR + '/core/index.js',
  ],
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        'NODE_ENV': JSON.stringify('production')
      }
    })
  ],
  output: {
    path: BUILD_DIR,
    filename: 'bundle.js'
  },
  module : {
     loaders : [
       {
         test : /\.jsx?$/,
         loaders: [
           'babel-loader?presets[]=es2015,presets[]=stage-0,presets[]=react,plugins[]=transform-runtime,plugins[]=transform-decorators-legacy'
        ],
         include : APP_DIR
       }
     ]
   }
};

module.exports = config;
