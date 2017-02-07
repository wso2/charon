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
 * If client requests the response to be returned in a format that is not supported,
 * FormatNotSupportedException is thrown.
 */
public class FormatNotSupportedException extends AbstractCharonException {

    public FormatNotSupportedException() {
        this.status = ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED;
        this.detail = ResponseCodeConstants.DESC_FORMAT_NOT_SUPPORTED;
    }

    public FormatNotSupportedException(String detail) {
        this.status = ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED;
        this.detail = detail;
    }

}
