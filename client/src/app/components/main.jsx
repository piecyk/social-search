var React = require('react');
var mui = require('material-ui');
var RaisedButton = mui.RaisedButton;
var Input = mui.Input;
var BackendClient = require('../remote/BackendClient');

var Main = React.createClass({
  getInitialState: function () {
    return {};
  },
  render: function () {

    return (
      <div className="centralized">
        <h3>Welcome to Social Search</h3>
        <Input ref="login" type="text" name="login" placeholder="Login" />
        <Input ref="password" type="password" name="password" placeholder="Password" />
        <RaisedButton label="Login" primary={true} onTouchTap={this.login} />
        {this.state /*FIXME why this.state is undefined during the 1st render*/ && this.state.authFailed ? <p className="warning">Authentication failed.</p> : null}
      </div>
    );
  },

  login: function () {
    var login = this.refs.login.getValue() || "";
    var pass = this.refs.password.getValue() || "";
    var self = this;
    BackendClient.authenticate(login, pass, function (resp) {
      self.setState({"authFailed": false});
      alert("WELCOME");
    });


    //TODO temporary
    this.setState({"authFailed": true});

  }

});

module.exports = Main;
