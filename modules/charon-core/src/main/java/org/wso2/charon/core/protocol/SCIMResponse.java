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
package org.wso2.charon.core.protocol;

import org.wso2.charon.core.exceptions.CharonException;

import java.util.Map;

/**
 * Represents a SCIMResponse to be returned for every operation of SCIM REST API
 */
public class SCIMResponse {

    protected int responseCode;
    protected String responseMessage;
    //If there are any HTTP header parameters to be set in response other than response code,
    protected Map<String, String> headerParamMap;

    /**
     * Constructor with two param
     *
     * @param responseCode
     * @param responseMessage
     */
    public SCIMResponse(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    /**
     * Constructor with three params
     *
     * @param responseCode
     * @param responseMessage
     * @param headerMap
     */
    public SCIMResponse(int responseCode, String responseMessage,
                        Map<String, String> headerMap) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.headerParamMap = headerMap;
    }

    public Map<String, String> getHeaderParameterMap() {
        return headerParamMap;
    }

    public void setHeaderParamMap(Map<String, String> headerParamMap) {
        this.headerParamMap = headerParamMap;
    }

    public String getHeaderParameterValue(String parameterName) throws CharonException {
        if (headerParamMap.containsKey(parameterName)) {
            return headerParamMap.get(parameterName);
        }
        throw new CharonException("Requested HTTP header parameter not found.");
    }

    public void setHTTPHeaderParameter(String headerParam, String paramValue) {
        if (this.headerParamMap.containsKey(headerParam)) {
            //TODO:print a warning and override the value.
            headerParamMap.remove(headerParam);
            headerParamMap.put(headerParam, paramValue);

        } else {
            headerParamMap.put(headerParam, paramValue);

        }
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
