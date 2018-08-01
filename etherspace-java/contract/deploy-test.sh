#!/usr/bin/env bash
rm -rf build/contracts
truffle migration --network development
cp -R build/contracts ../build/