---
title: GCE Kubernetes
---

# Mode 1 - Dependent on API Server Kubernetes deployment

- Follow all steps at https://github.com/fossasia/open-event-orga-server/blob/nextgen/docs/installation/gce-kubernetes.md (Ensure you use the config in the `nextgen` branch)
- Now switch back to this repository. (open-event-android)
- Get another domain name for the android generator deployment. (or you can use a subdomain of another domain.)
- Add the **External IP Address One** as an `A` record to your domain's DNS Zone.
- Add your domain name to `kubernetes/yamls/generator/ingress-notls.yml` & `kubernetes/yamls/generator/ingress-tls.yml`. (replace `droidgen.eventyay.com`)
- Run the command to start the deployment
    ```bash
    ./kubernetes/deploy.sh create
    ```
- Once deployed, your instance will be accessible at your domain name.

# Mode 2 - Standalone deployment

> Note: this mode is untested.

## Setup

- If you don’t already have a Google Account (Gmail or Google Apps), you must [create one](https://accounts.google.com/SignUp). Then, sign-in to Google Cloud Platform console ([console.cloud.google.com](http://console.cloud.google.com/)) and create a new project:


- Store your project ID into a variable as many commands below use it:

    ```
    export PROJECT_ID="your-project-id"
    ```

- Next, [enable billing](https://console.cloud.google.com/billing) in the Cloud Console in order to use Google Cloud resources and [enable the Container Engine API](https://console.cloud.google.com/project/_/kubernetes/list).

- Install [Docker](https://docs.docker.com/engine/installation/), and [Google Cloud SDK](https://cloud.google.com/sdk/).

- Finally, after Google Cloud SDK installs, run the following command to install `kubectl`:

    ```
    gcloud components install kubectl
    ```

- Choose a [Google Cloud Project zone](https://cloud.google.com/compute/docs/regions-zones/regions-zones) to run your service. We will be using us-west1-a. This is configured on the command line via:

    ```
    gcloud config set compute/zone us-west1-a
    ```

## Create your Kubernetes Cluster

- Create a cluster via the `gcloud` command line tool:

    ```
    gcloud container clusters create droidgen-cluster --cluster-version=1.6.4 --num-nodes=1
    ```

- Get the credentials for `kubectl` to use.

    ```
    gcloud container clusters get-credentials droidgen-cluster
    ```

## Deploy our pods, services and deployments

- From the project directory, use the provided deploy script to deploy our application from the defined configuration files that are in the `kubernetes` directory.

    ```
    ./kubernetes/deploy.sh standalone-create
    ```
- Get the IP address of your deployment by running. (You may need to execute it multiple times until the IP Address is assigned)

    ```
    kubectl get ingress --namespace web android-generator-notls
    ```
- Wait for a few mins (5-10m) before you move on to the next step.    
- The generator will be accessible at the above mentioned IP address.