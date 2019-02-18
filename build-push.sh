#!/bin/bash

if [ -z "$ECS_DOCKER_REPO" ]; then
    echo 'ERROR: Variable "ECS_DOCKER_REPO" is undefined.'
    exit 1
fi

eval $(aws ecr get-login | sed 's/-e none //')

mvn clean install || exit 1

docker build -t jrk-server . || exit 2
docker tag jrk-server:latest "$ECS_DOCKER_REPO/jrk-server:latest" || exit 3
docker push "$ECS_DOCKER_REPO/jrk-server:latest" || exit 4
