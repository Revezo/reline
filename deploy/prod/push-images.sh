#!/bin/sh
docker push wukashr/reline-backend:$1
docker push wukashr/reline-backend:latest
docker push wukashr/reline-ui:$1 wukashr/reline-ui:latest
docker push wukashr/reline-ui:latest
