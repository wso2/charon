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
 * Represents the response of a single bulk-operation.
 */
public class BulkResponseContent {

    /**
     * The HTTP response status code for the requested operation. When indicating an error, the "response" attribute
     * MUST contain the detail error response as per Section 3.12.
     */
    private Integer status;

    // @formatter:off
    /**
     * The transient identifier of a newly created resource,
     * unique within a bulk request and created by the client.  The
     * bulkId serves as a surrogate resource id enabling clients to
     * uniquely identify newly created resources in the response and
     * cross-reference new resources in and across operations within a
     * bulk request.  REQUIRED when "method" is "POST"
     */
    // @formatter:on
    private String bulkID;

    // @formatter:off
    /**
     * The current resource version.  Version MAY be used if the
     * service provider supports entity-tags (ETags) (Section 2.3 of
     * [RFC7232]) and "method" is "PUT", "PATCH", or "DELETE".
     */
    // @formatter:on
    private String version;

    /**
     * The resource endpoint URL.  REQUIRED in a response, except in the event of a POST failure.
     */
    private String location;

    /**
     * The HTTP method of the current operation.  Possible values are "POST", "PUT", "PATCH", or "DELETE".  REQUIRED.
     */
    private String method;

    // @formatter:off
    /**
     * The HTTP response body for the specified request
     * operation.  When indicating a response with an HTTP status
     * other than a 200-series response, the response body MUST be
     * included.  For normal completion, the server MAY elect to omit
     * the response body.
     */
    // @formatter:on
    private SCIMResponse scimResponse;


    public BulkResponseContent() {
        status = null;
        bulkID = null;
        scimResponse = null;
        version = null;
        location = null;
        method = null;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
