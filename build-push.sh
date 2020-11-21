#!/bin/bash

if [ -z "$ECS_DOCKER_REPO" ]; then
    echo 'ERROR: Variable "ECS_DOCKER_REPO" is undefined.'
    exit 1
fi

aws ecr get-login-password --region eu-west-1 \
    | docker login \
      --username AWS \
      --password-stdin 341577655277.dkr.ecr.eu-west-1.amazonaws.com \
    || exit 1

mvn clean install -DskipTests || exit 1

docker build -t jrk-server . || exit 2
docker tag jrk-server:latest "$ECS_DOCKER_REPO/jrk-server:latest" || exit 3
docker push "$ECS_DOCKER_REPO/jrk-server:latest" || exit 4
