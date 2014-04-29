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
package org.wso2.charon.core.objects.bulk;

import org.wso2.charon.core.protocol.SCIMResponse;

/**
 *
 */
public class BulkResponseContent {
    private String bulkID;
    private SCIMResponse scimResponse;
    private String responseCode;
    private String description;
    private String method;


    public BulkResponseContent() {
        bulkID = null;
        scimResponse = null;
        responseCode = null;
        description = null;
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "===========Bulk Response Content==========="
               + "\n BulkID: " + bulkID + " Response Code: " + responseCode + " Description: " + description;
    }
}
