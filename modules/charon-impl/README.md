
# Welcome to WSO2 Charon demo micro service


This demo micro service has been developed using WSO2 Microservices Framework for Java (MSF4J).

How setup the demo micro service
--------------------------------

1. Run 'mave clean install' from the module directory.
2. Inside the target directory, you can find
   org.wso2.charon.impl-3.0.0-jar-with-dependencies.jar
3. From the target directory run
   'java -jar org.wso2.charon.impl-3.0.0-jar-with-dependencies.jar'
4. Demo scim micro service will start on port 8080


How to send requests
--------------------

1. Open the terminal and run following commands on /Users
   and /Groups endpoint.


Demo scim app supports User create, get, update, patch, list
and Group create, get, update, patch and list.

**User create**
```
curl -v --data '{"schemas":[],"name":{"familyName":"jayawardana","givenName":"vindula"},"userName":"vindula","password":"vindula","emails":[{"value":"vindula@work.com","type":"work"}]}' --h```

**User get**
```
curl -v http://localhost:8080/scim/v2/Users/b6de2019-d491-49ef
```
**Group create**
```
curl -v --data '{"displayName": "engineer","members":[{"value":"316214c0-dd7e-4dc3-bed8-e91227d32597","display": "vindula"}]}' --header "Content-Type:application/scim+json" http://localhost:8080/scim/v2/Groups
```
**Group get**

```
curl -v http://localhost:8080/scim/v2/Groups/b6de2019-d491-49ef
```
Likewise you can run the other commands according to the https://tools.ietf.org/html/rfc7644

