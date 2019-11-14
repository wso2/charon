# WSO2 Charon - SCIM 2.0 Open Source Implementation 

|  Branch | Build Status | Travis CI Status |
| :------------ | :------------- | :-------------
| master      | [![Build Status](https://wso2.org/jenkins/job/forked-dependencies/job/wso2-charon/badge/icon)](https://wso2.org/jenkins/job/forked-dependencies/job/wso2-charon/) | [![Travis CI Status](https://travis-ci.org/wso2/charon.svg?branch=master)](https://travis-ci.org/wso2/charon)

WSO2 Charon is an open source implementation of SCIM protocol which is an open standard for Identity Provisioning. Charon comes under Apache 2.0 license. It can be used by any one who wants to add SCIM-based provisioning support for their applications. WSO2 Charon is integrated with WSO2 Identity Server. 
<p align="center">
<img align="middle" src="https://github.com/Vindulamj/Charon-3.0/blob/master/logo.png"  width="350px" height = "120px">
</p>
The following includes a brief introduction on each of the modules.

**Charon-Core**: This is the API that exposes an implementation of the SCIM specification. It can be used by any SCIM service provider or client implementation to support SCIM operations/functionalities. In addition to that, it also allows room for extension points to be plugged in according to the particular server side/client side implementation like user storage.

**Charon-Utils**: This contains a set of default implementations of the extension points. For example: SCIMUserManager. A particular implementation that uses charon-core as SCIM API can use these default implementations as building blocks.

**Charon-Impl**: A reference implementation of SCIM service provider is shipped with this module. Currently it is WSO2 msf4j based micro service that enables the SCIM endpoints to be exposed. This is based on the above two modules: charon-core and charon-utils, and illustrates how any SCIM implementation can utilize the API and supporting module provided by Charon.

**Charon-Samples**: This contains samples illustrating the SCIM use cases. Samples mainly contain the SCIM client side implementations which can be run against a SCIM server, and hence can also be referenced to get to know how the API provided by Charon can be used to implement SCIM client side.

Currently following features are supported.

#### /Users Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT
- [x] Sorting
- [x] Filtering including complex filters
- [x] Querying with POST 
- [x] Update with PATCH 
    - Add
    - Remove
    - Replace


#### /Groups Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Filtering including complex filters
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Sorting
- [x] Update with PUT
- [x] Querying with POST 
- [x] Update with PATCH 
    - Add
    - Remove
    - Replace

#### /Me Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT
- [x] Update with PATCH 
    - Add
    - Remove
    - Replace

#### EnterpriseUser
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT
- [x] Sorting
- [x] Filtering including complex filters
- [x] Querying with POST 
- [x] Update with PATCH 
    - Add
    - Remove
    - Replace

#### /ServiceProviderConfig Endpoint
- [x] Get

#### /ResourceType Endpoint
- [x] Get

#### /Schemas Endpoint
- [x] Get

#### /Bulk Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] Update with PUT
- [x] Update with PATCH 

Following features are being developed.

- [ ] Resource versioning
- [ ] Circular reference in /Bulk endpoint

