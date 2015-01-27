(function () {
  var agent = require('superagent');

  /**
   * Just some basic HTTP GET request as a PoC of integration with superagent.
   * TODO handle CORS ......
   * TODO add promise-based API here later
   */
  module.exports.test = function (ok) {
    return agent.get("http://arcane-harbor-5434.herokuapp.com/api/test").end(function (res) {
      if (res.ok) {
        ok(res.body);
      } else {
        // ... TODO
      }
    });
    ;
  }
})();
