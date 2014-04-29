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
package org.wso2.charon.core.encoder;

import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.bulk.BulkRequestData;
import org.wso2.charon.core.schema.ResourceSchema;

/**
 * SCIM API which is based on REST style, may support multiple formats of the resource.
 * API users can use this interface to implement their own decoders and register them.
 */
public interface Decoder {

    /**
     * Decode the resource string sent in the SCIM request/response payload.
     *
     * @param scimResourceString
     * @param resourceSchema
     * @param scimObject         @return
     */
    public SCIMObject decodeResource(String scimResourceString,
                                     ResourceSchema resourceSchema, AbstractSCIMObject scimObject)
            throws BadRequestException, CharonException;

    /**
     * Decode the string sent in the SCIM response payload, which is an exception.
     *
     * @param scimExceptionString
     * @return
     */
    public AbstractCharonException decodeException(String scimExceptionString)
            throws CharonException;

    /**
     * Decode the bulk resource string and return BulkRequestData Object
     *
     * @param bulkResourceString
     * @return
     * @throws BadRequestException
     */
    public BulkRequestData decodeBulkData(String bulkResourceString) throws BadRequestException;

}
