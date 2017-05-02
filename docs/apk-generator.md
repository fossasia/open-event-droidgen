TODO : Outline the working of apk-generator. 

[https://github.com/fossasia/open-event-android/issues/1433](https://github.com/fossasia/open-event-android/issues/1433)

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
    
<img width="1008" alt="screen shot 2017-04-27 at 12 59 00 pm" src="https://cloud.githubusercontent.com/assets/12716067/25472568/68d05186-2b49-11e7-99ce-f44e99648323.png">

2. ### Direct download 
On clicking the Download button `/app/<string:identifier>/download` url is hit with `GET` method `app_download(identifier)` in `open-event-android/apk-generator/v2/app/views/api.py`
identifier is the argument which is the filename of the file.
It is downloaded to the location with path as  `BASE_DIR/app/static/releases/filename.apk`
If the above file is not present then the download is aborted otherwise it returns the absolute path of the downloaded file.

<img width="1061" alt="screen shot 2017-04-27 at 12 56 26 pm" src="https://cloud.githubusercontent.com/assets/12716067/25472498/1fd66178-2b49-11e7-9344-2e1291f8e803.png">

![ezgif-2-380502cc24](https://cloud.githubusercontent.com/assets/12716067/25478083/703b47dc-2b5d-11e7-9956-9d11d7ce5956.gif)
