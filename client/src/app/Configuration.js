var localConfig = {
  backendClient: function () {
    return {
      host: "http://localhost:8080"
    }
  }
};

var herokuConfig1 = {
  backendClient: function () {
    return {
      host: "http://sheltered-lake-4481.herokuapp.com"
    }
  }
};


module.exports = herokuConfig1;
