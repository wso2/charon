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
package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.AttributeSchema;

import java.util.Map;

/**
 * Provides a factory interface to create different types of attributes
 * defined in SCIM Schema spec. An implementer can provide a handler to an implementation of this
 * interface to AbstractResourceFactory to customize attribute creation logic.
 */
public interface AttributeFactory {

    public Attribute createSimpleAttribute(String attributeId);

    public Attribute createComplexAttribute(String attributeId);

    public Attribute createMultiValuedAttribute(String attributeId);

    /**
     * Create the attribute given the attribute schema and the attribute object - may be with
     * attribute value set.
     *
     * @param attributeSchema
     * @param attribute
     * @return
     */
    public Attribute createAttribute(AttributeSchema attributeSchema, Attribute attribute)
            throws CharonException;
}
