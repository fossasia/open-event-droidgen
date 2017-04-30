TODO : Outline the working of apk-generator. 

[https://github.com/fossasia/open-event-android/issues/1433](https://github.com/fossasia/open-event-android/issues/1433)


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
