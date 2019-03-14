#!/usr/bin/env bash

# set .bash_profile or .profile
if [ -f ~/.bash_profile ]; then
PROFILE_NAME=~/.bash_profile
else
PROFILE_NAME=~/.profile
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd  "$DIR"

protoc --java_out=. msg.proto

cp -R com/wb/msg ../HttpServer/src/com/wb/
cp -R com/wb/msg ../Http_BD/src/com/wb/