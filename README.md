### Java compression library
[![Publication](https://img.shields.io/maven-central/v/de.carne/java-compression)](https://search.maven.org/artifact/de.carne/java-compression)
[![Build](https://github.com/hdecarne-github/java-compression/actions/workflows/build-on-linux.yml/badge.svg)](https://github.com/hdecarne-github/java-compression/actions/workflows/build-on-linux.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.carne%3Ajava-compression&metric=coverage)](https://sonarcloud.io/dashboard?id=de.carne%3Ajava-compression)

This project provides pure Java based implementations of several common compression algorithms.
Main goal is to provide decoding support to the [FileScanner](https://www.filescanner.org) project.

Most of the algorithms are ported from [7zip](http://7zip.org). However instead of providing an high-level API for
accessing archives, this library provides the low level tools and bolts to handle encoded data streams.

In order to make it available to other projects and to use a license compatible with the re-used open source
solutions this code is packaged in a separate project.

#### Current status
Currently only decoding for some basic compression formats is contained. Further development will be happen on demand.

#### License
This project is subject to the [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.en.html).
See LICENSE information for details.
