import { EmptyLayout, LayoutRoute, MainLayout } from "./components/Layout";
import React, {Component} from "react";
import { BrowserRouter, Redirect, Switch } from "react-router-dom";
import UploadDownload from "./components/UploadDownload";


import { history } from "./history";
import { PrivateRoute } from "./PrivateRoute"


const getBasename = () => {return `/${process.env.PUBLIC_URL.split("/").pop()}`;};


class Routes extends Component {
  render() {
    return (
      <BrowserRouter basename={getBasename()} history={history}>
        <Switch>
          {/* <LayoutRoute exact path="/register" layout={EmptyLayout} component={Register} /> */}
          <LayoutRoute exact path="/" component={UploadDownload} />
 
        </Switch>       
      </BrowserRouter>
    );
  }
}

export default Routes;
