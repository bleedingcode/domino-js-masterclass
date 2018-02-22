import React from 'react';
import CircularProgress from 'material-ui/CircularProgress';

const HomeLoading = () => (
  <div
    className="row" className="divLoading">
    <CircularProgress size={80} thickness={5} />
  </div>
)

export default HomeLoading;
