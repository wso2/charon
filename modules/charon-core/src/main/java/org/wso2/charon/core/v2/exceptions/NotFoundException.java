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
package org.wso2.charon.core.v2.exceptions;

import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * If the requested resource is not found, this exception is thrown.
 * HTTP error code is: 404 NOT FOUND
 */
public class NotFoundException extends AbstractCharonException {

    public NotFoundException() {
        status = ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND;
        detail = ResponseCodeConstants.DESC_RESOURCE_NOT_FOUND;
    }

    public NotFoundException(String detail) {
        status = ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND;
        this.detail = detail;
    }
}
