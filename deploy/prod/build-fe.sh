#!/bin/sh
docker build -t wukashr/reline-ui:"$1" -t wukashr/reline-ui:latest ../../ui
