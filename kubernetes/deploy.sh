#!/bin/bash
export DIR=${BASH_SOURCE%/*}

if [ "$1" = "delete" ]; then
    echo "Clearing the cluster."
    kubectl delete -f ${DIR}/yamls/generator
    echo "Done. The project was removed from the cluster."
elif [ "$1" = "create" ]; then
    echo "Deploying the project to kubernetes cluster"
    kubectl create -f ${DIR}/yamls/generator
    echo "Done. The project was deployed to kubernetes. :)"
elif [ "$1" = "standalone-create" ]; then
    echo "Deploying the project to kubernetes cluster"
    kubectl create -R -f ${DIR}/yamls/standalone/redis
    kubectl create -f ${DIR}/yamls/standalone/web/00-namespace.yml
    kubectl create -f ${DIR}/yamls/generator/android-deployment.yml
    kubectl create -f ${DIR}/yamls/generator/android-service.yml
    kubectl create -f ${DIR}/yamls/standalone/web/ingress-notls.yml
    echo "Done. The project was deployed to kubernetes. :)"
elif [ "$1" = "standalone-delete" ]; then
    echo "Clearing the cluster."
    kubectl delete -f ${DIR}/yamls/standalone/web/00-namespace.yml
    kubectl delete -f ${DIR}/yamls/standalone/redis/00-namespace.yml
    echo "Done. The project was removed from the cluster."
fi
