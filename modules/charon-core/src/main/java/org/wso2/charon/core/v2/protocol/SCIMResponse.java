/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.charon.core.v2.protocol;

import java.util.Map;

/**
 * Represents a SCIMResponse to be returned for every operation of SCIM REST API.
 */
public class SCIMResponse {

    protected int responseStatus;
    protected String responseMessage;

    //If there are any HTTP header parameters to be set in response other than response code,
    protected Map<String, String> headerParamMap;

    /*
     * Constructor with three params
     *
     * @param responseStatus - HTTP status code corresponding to the operation status
     * @param responseMessage - json encoded string for detailed response message
     * @param headerMap - HTTP headers (eg-Content-type)
     */
    public SCIMResponse(int responseStatus, String responseMessage, Map<String, String> headerMap) {
        this.responseStatus = responseStatus;
        this.responseMessage = responseMessage;
        this.headerParamMap = headerMap;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Map<String, String> getHeaderParamMap() {
        return headerParamMap;
    }

}
