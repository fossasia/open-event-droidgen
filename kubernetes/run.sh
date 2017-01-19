#!/bin/bash
echo "Deploying Generator v2"
export REDIS_URL=redis://${REDIS_SERVICE_HOST}:${REDIS_SERVICE_PORT}/1
cd apk-generator/v2
celery worker -A app.celery --loglevel=error &
gunicorn -b 0.0.0.0:8080 app:app --enable-stdio-inheritance --log-level "info"
