/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon3.core.objects.bulk;


import org.wso2.charon3.core.protocol.SCIMResponse;

/**
 *.
 */
public class BulkResponseContent {
    
    private String bulkID;
    private SCIMResponse scimResponse;
    private String version;
    private String location;
    private String method;


    public BulkResponseContent() {
        bulkID = null;
        scimResponse = null;
        version = null;
        location = null;
        method = null;
    }

    public String getBulkID() {
        return bulkID;
    }

    public void setBulkID(String bulkID) {
        this.bulkID = bulkID;
    }

    public SCIMResponse getScimResponse() {
        return scimResponse;
    }

    public void setScimResponse(SCIMResponse scimResponse) {
        this.scimResponse = scimResponse;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
