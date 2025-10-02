#!/usr/bin/env bash

set -euo pipefail

CURRENT_PATH=$(dirname $0)


captionColor=`tput setaf 6`
reset=`tput sgr0`

"${CURRENT_PATH}"/stop-env.sh

# Delete images from the previous builds
echo "${captionColor}Delete images from the previous builds${reset}"

# Many conditions below to avoid warnings from commands, that argument list is empty.
if [[ $(docker images -f "dangling=true" -q) ]]; then
    docker rmi $(docker images -f "dangling=true" -q)
fi

if [[ $(docker ps --no-trunc -aqf "status=exited") ]]; then
    docker ps --no-trunc -aqf "status=exited" | xargs docker rm
fi

if [[ $(docker images --no-trunc -aqf "dangling=true") ]]; then
    docker images --no-trunc -aqf "dangling=true" | xargs docker rmi
fi

if [[ $(docker volume ls -qf "dangling=true") ]]; then
    docker volume ls -qf "dangling=true" | xargs docker volume rm
fi

echo "${captionColor}Starting local docker  environment${reset}"

docker-compose -f "${CURRENT_PATH}"/docker-compose.yml -p exchange-rates-app up --build -d
