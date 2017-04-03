# plants-at-home

Webapp to show you where crops are planted.

## Overview

Shows the user in which countries crops are harvested.
The informations are based on public data from the
[FAOSTAT](http://www.fao.org/faostat/) site. For the map the [OpenLayers](http://openlayers.org/) library is used.

## Setup

[![Build Status](https://travis-ci.org/zouroboros/plants-at-home.svg?branch=master)](https://travis-ci.org/zouroboros/plants-at-home)

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2017

Distributed under the MIT License.
