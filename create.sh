#!/bin/bash

export __dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

pushd ${__dir}
mvn clean install compile assembly:single
popd
