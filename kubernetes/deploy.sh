#!/bin/bash
echo "Deploying the generator to the kubernetes cluster"
export DIR=${BASH_SOURCE%/*}
# Start generator deployment
kubectl create -f ${DIR}/yamls/generator
echo "Done. The generator was deployed to kubernetes. :)"
