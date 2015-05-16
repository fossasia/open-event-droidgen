# open-event-android
The Open Event Android Client

The Android client is a generic app that has two parts:
a) A standard configuration file, that sets the details of the app (e.g. color scheme, logo of event, link to JSON app data)
b) The Android app itself
This app uses the json api provided by a server maintained [here](https://github.com/fossasia/open-event-orga-server). 
## Data retrieval
- The orga-server provides the data which is stored in its backend database in a json format.
- The app on startup picks up data from a json file in it's assets folder if the version number of data is -1 which happens when there is no internet connection
- If there is a valid internet connection, the data download starts from the server.
- Also there is a check on the version of data already there in the app's database. If data is stale then only it is downloaded.
- If database is empty then firstly json file in assets is accessed but if internet is available , latest data is downloaded.