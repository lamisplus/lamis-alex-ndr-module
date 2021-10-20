
import React from 'react';
import { Route, Redirect } from 'react-router-dom';


export const PrivateRoute = ({ component: Component, roles,  ...rest }) => (
    <Route {...rest} render={props => (
        
        // <Redirect to={{ pathname: '/unauthorised', state: { from: props.location } }} /> 
         <Component {...props} />
        //  <Redirect to={{ pathname: '/login', state: { from: props.location } }} />
    )} />
)