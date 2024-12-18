#!/bin/sh
docker build --build-arg version="$1" -t wukashr/reline-backend:"$1" -t wukashr/reline-backend:latest ../../backend
