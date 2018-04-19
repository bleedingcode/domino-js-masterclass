import React from 'react';
import CircularProgress from 'material-ui/CircularProgress';

/*
	Component for "loading" wheel on homepage, displayed during login
*/

const HomeLoading = () => (
  <div
    className="row" className="divLoading">
    <CircularProgress size={80} thickness={5} />
  </div>
)

export default HomeLoading;
