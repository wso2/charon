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
package org.wso2.charon3.core.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This declares the SCIM resources schema as specified in SCIM core specification 2.0.
 */

public class SCIMResourceTypeSchema implements ResourceTypeSchema, Serializable {

    private static final long serialVersionUID = 6106269076155338045L;
    //The core schema for the resource type is identified using the following schemas URIs
    //e.g.: for 'User' - urn:ietf:params:scim:schemasList:core:2.0:User
    private List<String> schemasList;
    //set of attributeList in the schema
    private ArrayList<AttributeSchema> attributeList = new ArrayList<AttributeSchema>();

    private SCIMResourceTypeSchema(List<String> schemas, AttributeSchema[] attributeSchemas) {
        this.schemasList = schemas;
        if (attributeSchemas != null) {
            for (AttributeSchema attributeSchema : attributeSchemas) {
                if (attributeSchema != null) {
                    this.attributeList.add(attributeSchema);
                }
            }
        }
    }

    /*
     * Create a SCIMResourceTypeSchema according to the schema id and set of attributeList
     *
     * @param schemas          - json encoded string of user info
     * @param attributeSchemas - SCIM defined user schema
     * @return SCIMResourceTypeSchema
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                                  AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeSchema(schemas, attributeSchemas);

    }

    /*
     * schema list contains the specified schema?
     *
     * @param schema
     * @return
     */
    public Boolean isSchemaAvailable(String schema) {
        if (schemasList.contains(schema)) {
            return true;
        } else {
            return false;
        }
    }

    public List<String> getSchemasList() {
        return schemasList;
    }

    public void setSchemasList(String schema) {
        this.schemasList.add(schema);
    }

    public ArrayList<AttributeSchema> getAttributesList() {
        return attributeList;
    }

    public void setAttributeList(ArrayList attributeList) {
        this.attributeList = attributeList;
    }
}
