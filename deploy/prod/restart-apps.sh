#!/bin/bash

source .env
curl --location "$BE_RESTART_URL" --header "$AUTH_HEADER"
echo
curl --location "$UI_RESTART_URL" --header "$AUTH_HEADER"