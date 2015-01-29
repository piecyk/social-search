var configuration = require('../configuration').backendClient();
var agent = require('superagent');

/**
 * Just some basic HTTP GET request as a PoC of integration with superagent.
 * TODO add promise-based API here later
 */
module.exports.test = function (ok) {
  return agent.get(configuration.host + "/api/v1/test").end(function (res) {
    if (res.ok) {
      ok(res.body);
    } else {
      // ... TODO
    }
  });
};

module.exports.authenticate = function (_login, _password, _okCallback) {
  return agent
    .post(configuration.host + "/api/v1/authenticate")
    //.set("Content-Type", "application/json")
    .send({login: _login, password: _password})
    .end(function (res) {
      console.log(res);
      if (res.ok) {
        _okCallback(res.body);
      } else {
        // ... TODO
      }
    });
};
