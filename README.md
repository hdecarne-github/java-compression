### Java compression library

[![Download](https://api.bintray.com/packages/hdecarne/maven/java-compression/images/download.svg)](https://bintray.com/hdecarne/maven/java-compression/_latestVersion)
[![Build Status](https://travis-ci.org/hdecarne/java-compression.svg?branch=master)](https://travis-ci.org/hdecarne/java-compression)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=de.carne.common:java-compression)](https://sonarcloud.io/dashboard/index/de.carne.common:java-compression)

This project provides pure Java based implementations of several common compression algorithms.
Main goal is to provide decoding support to the [FileScanner](https://www.filescanner.org) project.

Most of the algorithms are ported from [7zip](http://7zip.org). However instead of providing an high-level API for
accessing archives, this library provides the low level tools and bolts to handle encoded data streams.

In order to make it available to other projects and to use a license compatible with the re-used open source
solutions this code is packaged in a separate project.

#### Current status
This project is in it's early phase and further development is needed.

#### License
This project is subject to the [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.en.html).
See LICENSE information for details.
