How apk is generated using the Android apk generator

Part 1: Getting the data from uploaded zip/url

The moment the submit/upload button is pressed, process starts here. The respective email and the data source is stored. The web server and the celery worker run as two separate instances. Data is passed between them via redis. Jobs with payload are added to redis by the web app. A celery task is created with a payload containing the data submitting by the user. In case of a file, the file is saved and the payload contains the path to the file, whereas in case of an API endpoint, the payload contains the url. After that, the page tells the user that his request is being processed. Now, the celery worker will start processing the task in the background starting here which will further call this and subquently it will call the main generator function.
