[![Stories in Ready](https://badge.waffle.io/piecyk/social-search.png?label=ready&title=Ready)](https://waffle.io/piecyk/social-search)
# social-search

[![Build Status](https://travis-ci.org/piecyk/social-search.svg?branch=master)](https://travis-ci.org/piecyk/social-search)
[![Coverage Status](https://coveralls.io/repos/piecyk/social-search/badge.svg)](https://coveralls.io/r/piecyk/social-search)


See also the [Social Search Hybrid mobile app](https://github.com/michalradziwon/social-search-hybrid-client) repository.


TODO:
* Scala REST back-end layer + one-click heroku deployment script
* Hybrid HTML5+JS Mobile app + build scripts
* Mobile client integration with back-end layer (with simple authentication)
* Cordova integration + build scripts
* Back-end integration with external RESTish services
* Simple back-end storage (key-value store)
* Very simple server-to-client notifications (no need for websockets/long-polling at this stage)


##Follow these steps to get started:

1. Git-clone this repository.
2. Change directory into your clone.
3. Launch SBT: $ sbt

```sh
$ git clone https://github.com/piecyk/social-search.git
$ cd social-search
$ sbt
```

sbt commnads: test, re-start, re-stop.

Type in sbt re-start your app should now be running on [localhost:8080](http://localhost:8080/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```


## Hybrid Mobile app info page - [here](https://github.com/piecyk/social-search/blob/master/client/README.md)
