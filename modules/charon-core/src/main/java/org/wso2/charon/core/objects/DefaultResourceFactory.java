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
package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.ResourceSchema;

//TODO:This is not used anymore.. Remove this class.
/**
 * This resource factory class is to handle factory aspect of constructing  a resource given a
 * SCIMObject and the corresponding resource schema.
 */
public class DefaultResourceFactory {

    /**
     * When a scim resource is constructed from a decoded json/xml string, user this method.
     * This unifies the creation of SCIM object by taking into consideration all common tasks related to
     * SCIM object creation.
     *
     * @param resourceSchema
     * @param abstractSCIMObject
     * @return
     * @throws CharonException
     */
    public static SCIMObject createSCIMObject(ResourceSchema resourceSchema,
                                              AbstractSCIMObject abstractSCIMObject)
            throws CharonException {
        //perform and actions related to constructing the SCIMObject
        //for the moment return the SCIMObject
        
        //Validate the constructed SCIMObject against the schema
        //AbstractValidator.validateSCIMObjectForRequiredAttributes(abstractSCIMObject, resourceSchema);
        return abstractSCIMObject;
    }

    /**
     * Set the attribute in a SCIM object. Used when constructing and setting attributes from
     * decoded json/xml strings. This layer filters out setting READ-ONLY attributes.
     *
     * @param scimObject
     * @param attribute
     */
    public static void setAttribute(AbstractSCIMObject scimObject, AbstractAttribute attribute) {
        //add the attribute to attribute map if not already existing and if not read-only.
       /* if (!scimObject.isAttributeExist(attribute.getName())) {
            if (!attribute.isReadOnly()) {
                scimObject.getAttributeList().put(attribute.getName(), attribute);
            }
        }*/
        //update the schemas list if any new schema used in the attribute, and create schemas array.
        /*if (!scimObject.isSchemaExists(attribute.getSchemaName())) {
            scimObject.getSchemaList().add(attribute.getSchemaName());
        }*/

    }

}
