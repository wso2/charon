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
package org.wso2.charon.core.exceptions;

import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.SCIMConstants;

/**
 * Represents Exception thrown on the front end when a server error occurs.
 * Similar to HTTP 5xx.
 */
public class InternalServerException extends AbstractCharonException {

    public InternalServerException() {
        super(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR, ResponseCodeConstants.DESC_INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String description) {
        super( ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR, description);
    }

    public InternalServerException(String description, Throwable t) {
        super(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR, description, t);
    }

    public InternalServerException(int code, String description) {
        super(code, description);
    }
}
