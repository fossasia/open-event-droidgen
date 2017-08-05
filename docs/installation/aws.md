# AWS EC2 installation

## Phase 1

This phase involves creating the EC2 instance which will hold your app.

* Go to Amazon Web Services console and [select EC2](https://console.aws.amazon.com/ec2/).

* Click on the create button to create an instance. Select Ubuntu 14 x64 as the linux distribution.

* Follow the other steps till you reach the 6th step which is about *configuring Security groups*. There add a rule to accept all HTTP connections. See the screenshot on how
it should look like.

![ec2_security_grp](../_static/images/aws-security-group.png)

* Click Launch in the 7th step and you will be presented with a dialog to create a key. Create a new key and give it a name. In this tutorial, I will use the name 'mykey'.
Then download the key. Keep it safe because if you lose it, you will lose access to the server.

![ec2_create_key](../_static/images/aws-key-pair.png)

* Once the instance is created, you will be forwarded to the instances list. Select the newly created instance and click on Connect button. You will see a dialog with instructions on how to connect to it using ssh.

![connect_ssh_ec2](../_static/images/aws-instance.png)

* In the above case, the command is as follows. So open the terminal in your Downloads directory (which has the downloaded key file) and then run the command you got from the
previous step. In my case, it was -

```sh
chmod 400 mykey.pem
ssh -i "mykey.pem" ubuntu@ec2-52-41-207-116.us-west-2.compute.amazonaws.com
```

* You will be into the server's shell. You will notice a text message stating to install the language pack. So run the following command.

```sh
sudo apt-get install language-pack-en
```


## Phase 2

The second phase is about installing Docker and Compose on our cloud Ubuntu instance.

* The first step is to install Docker. For that, we followed the official [installation instructions for Ubuntu](https://docs.docker.com/engine/installation/linux/ubuntulinux/).

```sh
sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" | sudo tee /etc/apt/sources.list.d/docker.list
sudo apt-get update
apt-cache policy docker-engine
sudo apt-get install -y docker-engine
```

* Then install Docker Compose.

```sh
sudo apt-get -y install python-pip
sudo pip install docker-compose
```

* Finally you will have to add your current group to the `docker` group so that you can run commands without sudo.
([Reference](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html#install_docker))

```sh
sudo usermod -a -G docker $(whoami)
```

* After this step, you will have to re-login. So `exit` the connection and then connect to it with ssh again.


## Phase 3

The third phase is about building the Open Event Android generator on the server and running it. The steps are very similar to the [Docker installation instructions](docker.md) so it is very highly recommended you have a look at it and try to understand the process.

* First we will set the `SERVER_NAME` config. (See [Docker instructions](docker.md) for more info on this)

```sh
export SERVER_NAME="ec2-52-41-207-116.us-west-2.compute.amazonaws.com"
```

* Then we will build the server. This process can take some time.

```sh
git clone https://github.com/fossasia/open-event-android.git && cd open-event-android
docker-compose build
```

* Then start the server.

```sh
docker-compose up
```

* That's it. Visit the public DNS to see your site live. In my case it was http://ec2-52-41-207-116.us-west-2.compute.amazonaws.com/


### References

* [Amazon EC2 Getting Started](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html#ec2-launch-instance_linux)
* [EC2 Docker Getting Started](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html)
* [Open Event Docker Manual](docker.md)


### Notes

* You may want to run the server in daemon mode so that it doesn't exit when the local terminal window is closed. For that use `docker-compose up -d`
