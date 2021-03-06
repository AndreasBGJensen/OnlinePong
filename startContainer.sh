#!/bin/bash
# Script to build a docker image and deploy in a container

imageName="onlinepong"
containerName="onlinepong"
    
printf "\nCreating image '$imageName'\n"
docker build -t "$imageName" .
# Check if build succeded ($? is result of last command)
if [ $? -ne 0 ]; then
    printf "Build error"
    exit 1
fi

# Stop container
printf "\nStopping existing container\n"
docker stop "$containerName" && docker rm "$containerName"

# Start container
printf "\nStarting container '$containerName'\n"
docker run --name "$containerName" -t -d -p 20000:20000 "$imageName"
if [ $? -eq 0 ]; then
    printf "\nSuccess!" 
fi