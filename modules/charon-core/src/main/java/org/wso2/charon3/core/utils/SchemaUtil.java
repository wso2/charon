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

package org.wso2.charon3.core.utils;

import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;


import java.util.List;

/**
 * Attribute schema related supportutils can be found here.
 */
public class SchemaUtil {

    /*
     * return the attribute schema for the asked attribute URI
     * @param attributeFullName
     * @param scimObjectType
     * @return
     */
    public static AttributeSchema getAttributeSchema(String attributeFullName, SCIMResourceTypeSchema scimObjectType) {

        ResourceTypeSchema resourceSchema = scimObjectType;

        if (resourceSchema != null) {
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();
            for (AttributeSchema attributeSchema : attributeSchemas) {
                if (attributeFullName.equals(attributeSchema.getName())) {
                    return attributeSchema;
                }
                if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (attributeSchema.getMultiValued()) {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeFullName.equals
                                    (attributeSchema.getName() + "." + subAttributeSchema.getName())) {
                                return subAttributeSchema;
                            }
                        }
                    } else {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeFullName.equals
                                    (attributeSchema.getName() + "." + subAttributeSchema.getName())) {
                                return subAttributeSchema;
                            }
                            if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                // this is only valid for extension schema
                                List<SCIMAttributeSchema> subSubAttributeSchemaList =
                                        subAttributeSchema.getSubAttributeSchemas();
                                for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemaList) {
                                    if (attributeFullName.equals(attributeSchema.getName() + "." +
                                            subAttributeSchema.getName() + "." + subSubAttributeSchema.getName())) {
                                        return subSubAttributeSchema;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return  null;
    }

}
