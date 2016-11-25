/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.v2.exceptions;

import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * If the request is unparsable, syntactically incorrect, or violates schema., this exception is thrown.
 * HTTP error code is: 400 BAD REQUEST
 */
public class BadRequestException extends AbstractCharonException  {

    //A SCIM detail error keyword.
    protected String scimType;

    public BadRequestException(String scimType) {
        status = ResponseCodeConstants.CODE_BAD_REQUEST;
        detail = ResponseCodeConstants.DESC_BAD_REQUEST;
        this.scimType = scimType;
    }

    public BadRequestException(String details, String scimType) {
        status = ResponseCodeConstants.CODE_BAD_REQUEST;
        this.detail = details;
        this.scimType = scimType;
    }

    public String getScimType() {
        return scimType; }

    public void setScimType(String scimType) {
        this.scimType = scimType; }

}
