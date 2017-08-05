# Docker installation

* Get the latest version of docker. See the [official site](https://docs.docker.com/engine/installation/) for installation info for your platform.

* Install the latest version of docker-compose. Windows and Mac users should have docker-compose by default as it is part of Docker toolbox. For Linux users, see the
[official guide](https://docs.docker.com/compose/install/).

* Run `docker` and `docker-compose` in terminal to see if they are properly installed.

* Clone the project and cd into it.

```bash
git clone https://github.com/fossasia/open-event-android.git && cd open-event-android
```

* Then set the required `SERVER_NAME` environment variable. `SERVER_NAME` should the same as the domain on which the server is running and it should not include 'http', 'https',
'www' or the trailing slash (/) in the url. Examples - `domain.com`, `sub.domain.com`, `sub.domain.com:5000` etc

```bash
export SERVER_NAME=localhost;
```

* In the same terminal window, run `docker-compose build` to build Open Event Android App Generator's docker image. This process can take some time.

* After build is done, run `docker-compose up` to start the server.

* Close the application's shell by `exit` command.

* That's it. Go to `localhost` on the web browser and Open Event Android App Generator will be live.


### Updating the Docker image

* To update the Docker image with a more recent version of Open Event Android App Generator, you follow the same steps.

* `docker-compose build` followed by `docker-compose up`.

* That should be all. Open `localhost` in web browser to view the updated Open Event Android App Generator.

#### Version information

This guide was last checked with docker version 1.12.0 and docker-compose version 1.8.0 on a Ubuntu 14.04 x64 system.
