
# Welcome to WSO2 Charon demo micro service


This demo micro service has been developed using WSO2 Microservices Framework for Java (MSF4J).

How setup the demo micro service
--------------------------------

1. Run 'mvn clean install' from the module directory.
2. Inside the target directory, you can find
   org.wso2.charon.impl-3.x.x-jar-with-dependencies.jar
3. From the target directory run
   'java -jar org.wso2.charon.impl-3.x.x-jar-with-dependencies.jar'
4. Demo SCIM micro service will start on port 8080 or 9090. 


How to send requests
--------------------

1. Open the terminal and run following commands on /Users
   and /Groups endpoint.


Demo SCIM app supports User create, get, update, patch, list
and Group create, get, update, patch and list.

**User create**
```
curl -v --data '{"schemas":[],"name":{"familyName":"jayawardana","givenName":"vindula"},"userName":"vindula","password":"vindula","emails":[{"value":"vindula@work.com","type":"work"}]}' --header "Content-Type:application/scim+json" http://localhost:<port>/scim/v2/Users
```
**User get**
```
curl -v http://localhost:<port>/scim/v2/Users/<User_ID>
```
**Group create**
```
curl -v --data '{"displayName": "engineer","members":[{"value":"316214c0-dd7e-4dc3-bed8-e91227d32597","display": "vindula"}]}' --header "Content-Type:application/scim+json" http://localhost:<port>/scim/v2/Groups
```
**Group get**
```
curl -v http://localhost:<port>/scim/v2/Groups/<Group_ID>
```

Likewise you can run the other commands according to the https://tools.ietf.org/html/rfc7644

