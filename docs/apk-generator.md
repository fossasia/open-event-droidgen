## How apk is generated using the Android apk generator

## **Getting the data from uploaded zip/url**

![Apk Generator](images/androidgenerator.png)

The web server and the celery worker run as two separate instances. Data is passed between them via redis. Jobs with payload are added to redis by the web app.
The moment the `GENERATE ANDROID APP` button is pressed, a `POST` request through function `process` is being made in the file `open-event-android/apk-generator/v2/app/views/__init__.py` where the respective email and the data source is stored.

![Generator Process](images/generator.jpeg)

A celery task is created with a payload containing the data submitting by the user. In case of a file, it is saved and the payload contains the path to the file, whereas in case of an API endpoint, the payload contains the url.
After that, the page tells the user that his request is being processed.Now, the celery worker will start processing the task in the background with the help of the function `generate_app_task` which will further call function `generate_app_task_base` both in the file `open-event-android/apk-generator/v2/app/tasks/__init__.py`. An object of the class `generator` is created in `generate_app_task_base` function which is used to call the `generate` method that returns the apk url.

![Celery Task](images/celery_task.jpeg)

## **Generating the app**

### Normalizing the data

First, the `normalize` function of [generator.py](../apk-generator/v2/app/generator/generator.py) is called with the creator email, API endpoint URL and the zip file as arguments. The task of normalize function is to replace the specific data inside app source files like app name, event name and logo, etc to customize the app according to the provided event data.
- Firstly, it checks if API endpoint or payload zip is provided or not, and sends error to the client if it isn't
- Then according to the type of data provided, it prepares to load the data:  
    - URL Endpoint:
        - Saves the API link for further use
        - Creates the assets directory in application
        - Loads the `/event` data from endpoint into JSON  
    - Payload Zip:
        - Extracts the zip
        - Saves the event JSON data from file
- Loads the required event data from the JSON
- Parses and downloads the event logo and background

### Building the app

When the `generate` function is called, the app build process takes place in the following steps:
- Prepares the source by removing the previously present files like logos and JSON files in assets
- Generates the app package name and `config.json` containing the
    - Creator Email
    - App name
    - API link
- Resizes the logo and background images in various Android DPIs
- Replaces the static string throughout the project with the event specific ones
- Loads the asset JSON files of in `assets` folder
- Prepares the build tools by loading its version and path
- *Executes the [build script](../apk-generator/v2/scripts/build_apk.sh)* which does the following:
    - Build the release version of the app
    - Sign the app with key using the key path, store password and alias loaded from environment variables
    - Zipalign the app
- Copies the generated release apk in a public `app` folder and generate apk url
- Return the generated apk URL and optionally notifies socket.IO client about the completion of process

## **Delivery Options**

After generation of the app, we have two option for delivery of the apk

 1. ### Email
  For email we use Marrow Mailer. We have a class Notification for sending emails at 
 `open-event-android/apk-generator/v2/app/utils/notification.py` and functions in this class is called in `apk-generator/v2/app/generator/generator.py`
  
notification.py has two functions 
- send  (under development)
- send_mail_via_smtp_  (for sending mails)
    
![Mailing Code](images/mailcode.png)

2. ### Direct download 
On clicking the Download button `/app/<string:identifier>/download` url is hit with `GET` method `app_download(identifier)` in `open-event-android/apk-generator/v2/app/views/api.py`
identifier is the argument which is the filename of the file.
It is downloaded to the location with path as  `BASE_DIR/app/static/releases/filename.apk`
If the above file is not present then the download is aborted otherwise it returns the absolute path of the downloaded file.

![App Download Code](images/downloadapp.png)

![Webapp Working](images/webapp.gif)
