#!/bin/bash

# usage: $0 <contest-id>

function die() {
    echo "fatal: $*" >&2
    exit 1
}
function try() {
    "$@" || die "failed: $*"
}

try javac Prepare.java
try javac RunComparator.java
try java Prepare $1
try java RunComparator contest$1
try rm Prepare*.class
try rm RunComparator*.class

