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
package org.wso2.charon3.core.exceptions;

import org.wso2.charon3.core.protocol.ResponseCodeConstants;

/**
 * Operation is not permitted based on the supplied authorization.
 */
public class ForbiddenException extends AbstractCharonException {

    public ForbiddenException() {
        this(ResponseCodeConstants.DESC_FORBIDDEN);
    }

    public ForbiddenException(String exception) {
        super(ResponseCodeConstants.CODE_FORBIDDEN, exception, null);
        detail = exception;
    }

    public ForbiddenException(String exception, String scimType) {

        super(ResponseCodeConstants.CODE_FORBIDDEN, exception, scimType);
    }

}
