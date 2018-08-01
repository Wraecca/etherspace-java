#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "Please input network (development/kovan/mainnet)!"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Please input mnemonic!"
    exit 1
fi

export MNEMONIC=$2

GIT_HASH="$(git log --pretty=format:'%h' -n 1)"
echo 'Git Hash: '$GIT_HASH

echo 'truffle migration...'
truffle migration --network $1 -f 1 --to 2

if [ "$?" != 0 ]; then
    echo 'Deploy error'
    exit 2
fi

echo 'Done ...'