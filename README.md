# Auth0 Anomaly Detection exercise

## Running
To run this service you will need to have sbt installed:
``` brew install sbt```
You will also need to have a JDK installed.

Once sbt is installed it should handle the installation of the appropriate Scala version when running a service for the first time. If this isn't the case please contact me/the repository main contributor.

To run the service, type the following in the same directory this README is located in (i.e blacklist-service):
```sbt run```

The service takes an optional command line argument of a comma-separated list that specifies what files from this [repository](https://github.com/firehol/blocklist-ipsets) to scrape from. If no arguments are supplied it reads the 'service.files' value in the application.conf file - this is located in src/main/resources/application.conf.
```sbt "run firehol_level1.netset,firehol_level4.netset"```
(Hint: the quotation marks are important when giving additional arguments :)!)

The service runs on localhost on port 8080.

## API
Note: Akka-HTTP requires all requests explicitly specify that the Content-Type is application/json.

### IP validation API
| End-point        | Example request                                                                                          | Description                                                                                                                        | Example response                                                                                            |
|------------------|----------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| (POST) /validate | ```{     "ip": "1358", "callingService":"core-auth" }```  Note: callingService is a  non-mandatory field | Takes in an IP and the service that's making the call.  Traverses through all the blacklists the service is  configured to index.  | ``` {       "suppliedIp" :  "1358" ,       "blacklistStatus" :  false ,       "blacklistFile" :  null } ``` |

### Management API
| End-point                       | Example request | Description                             | Example response                                                                        |
|---------------------------------|-----------------|-----------------------------------------|-----------------------------------------------------------------------------------------|
| (POST) /management/force-update | N/A             | Forces an update of the blacklist tree. | ``` {     "message": "Successfully populated tree",     "numberOfEntries": 114064 } ``` |


