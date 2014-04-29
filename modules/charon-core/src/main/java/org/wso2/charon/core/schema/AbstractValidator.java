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
package org.wso2.charon.core.schema;

import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;

import java.util.List;
import java.util.Map;

/**
 * Contains common functionality to any SCIM object schema validator.
 * TODO:If all required attributes are there, if additional attributes not there
 * according to the schema,and do this after creating the resource by decoder and user manager
 * and client.
 */
public abstract class AbstractValidator /*implements SchemaValidator*/ {

    /**
     * Validate SCIMObject for required attributes given the object and the corresponding schema.
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSCIMObjectForRequiredAttributes(AbstractSCIMObject scimObject,
                                                               ResourceSchema resourceSchema)
            throws CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for required attributes.
            if (attributeSchema.getRequired()) {
                if (!attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Required attribute " + attributeSchema.getName() + " is missing in the SCIM Object.";
                    throw new CharonException(error);
                }
            }
            //check for required sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMSubAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

                if (subAttributesSchemaList != null) {
                    for (SCIMSubAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getRequired()) {

                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) == null) {
                                    String error = "Required sub attribute: " + subAttributeSchema.getName()
                                                   + " is missing in the SCIM Attribute: " + attribute.getName();
                                    throw new CharonException(error);
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getValuesAsSubAttributes();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) == null) {
                                            String error = "Required sub attribute: " + subAttributeSchema.getName()
                                                           + " is missing in the SCIM Attribute: " + attribute.getName();
                                            throw new CharonException(error);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public static void validateSchemaList(AbstractSCIMObject scimObject,
                                          SCIMResourceSchema resourceSchema) {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        //get the schema list of the object
        List<String> schemaList = scimObject.getSchemaList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for schema.
            if (attributeList.containsKey(attributeSchema.getName())) {
                if (!schemaList.contains(attributeSchema.getSchema())) {
                    schemaList.add(attributeSchema.getSchema());
                }
            }
        }
    }

    //TODO: go through attribute list and check if all attributes are defined in SCIMObject schema.
}
