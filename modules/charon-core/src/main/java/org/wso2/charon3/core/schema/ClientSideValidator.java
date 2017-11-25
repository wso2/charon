/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.charon3.core.schema;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;

/**
 * This is to perform SCIM client side validation according to SCIM 2.0 spec.
 */
public class ClientSideValidator extends AbstractValidator {

    //validate scim object received from the server side.
    public static void validateRetrievedSCIMObject(AbstractSCIMObject scimObject,
                                                   SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException {
        validateSCIMObjectForRequiredAttributes(scimObject, schema);
        validateSchemaList(scimObject, schema);
    }
}
