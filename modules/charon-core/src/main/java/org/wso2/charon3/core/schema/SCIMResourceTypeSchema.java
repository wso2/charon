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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This declares the SCIM resources schema as specified in SCIM core specification 2.0.
 */

public class SCIMResourceTypeSchema implements ResourceTypeSchema, Serializable {

    private static final long serialVersionUID = 6106269076155338045L;

    /**
     * The core schema for the resource type is identified using the following schemas URIs
     * e.g.: for 'User' - urn:ietf:params:scim:schemasList:core:2.0:User
     */
    private List<String> schemasList;

    /**
     * set of attributeList in the schema
     */
    private List<AttributeSchema> attributeList = new ArrayList<>();

    /**
     * a list of extensions that can be added to this schema representation
     */
    private Set<SCIMResourceTypeExtensionSchema> extensions = new HashSet<>();

    /**
     * an optional name for schemata
     */
    private String name;

    /**
     * an optional description for schemata
     */
    private String description;

    protected SCIMResourceTypeSchema(List<String> schemas,
                                     List<SCIMResourceTypeExtensionSchema> extensions,
                                     AttributeSchema[] attributeSchemas) {
        this.schemasList = schemas;
        if (extensions != null) {
            this.extensions.addAll(extensions);
        }
        if (attributeSchemas != null) {
            this.attributeList.addAll(Arrays.asList(attributeSchemas));
        }
    }

    /**
     * Create a SCIMResourceTypeSchema according to the schema id and set of attributeList
     *
     * @param schemas          - json encoded string of user info
     * @param attributeSchemas - SCIM defined user schema
     * @return SCIMResourceTypeSchema
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                                  String name,
                                                                  String description,
                                                                  AttributeSchema... attributeSchemas) {
        SCIMResourceTypeSchema resourceTypeSchema = new SCIMResourceTypeSchema(schemas, null, attributeSchemas);
        resourceTypeSchema.setName(name);
        resourceTypeSchema.setDescription(description);
        return resourceTypeSchema;
    }

    /**
     * Create a SCIMResourceTypeSchema according to the schema id and set of attributeList
     *
     * @param schemas          - json encoded string of user info
     * @param attributeSchemas - SCIM defined user schema
     * @return SCIMResourceTypeSchema
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                                  AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeSchema(schemas, null, attributeSchemas);
    }

    /**
     * creates a new resource type schema together with a resource type schema extension
     *
     * @param schemas          - json encoded string of user info
     * @param extensions       - a list of extension schemas for this resource type schema
     * @param attributeSchemas - SCIM defined user schema
     * @return
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                                  List<SCIMResourceTypeExtensionSchema> extensions,
                                                                  AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeSchema(schemas, extensions, attributeSchemas);
    }

    /**
     * creates a new resource type schema together with a resource type schema extension
     *
     * @param schemas          - json encoded string of user info
     * @param extensions       - a list of extension schemas for this resource type schema
     * @param attributeSchemas - SCIM defined user schema
     * @return
     */
    public static SCIMResourceTypeSchema createSCIMResourceSchema(List<String> schemas,
                                                                  List<SCIMResourceTypeExtensionSchema> extensions,
                                                                  String name,
                                                                  String description,
                                                                  AttributeSchema... attributeSchemas) {
        SCIMResourceTypeSchema resourceTypeSchema = new SCIMResourceTypeSchema(schemas, extensions, attributeSchemas);
        resourceTypeSchema.setName(name);
        resourceTypeSchema.setDescription(description);
        return resourceTypeSchema;
    }

    /**
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

    public List<AttributeSchema> getAttributesList() {
        return attributeList;
    }

    public void setAttributeList(List attributeList) {
        this.attributeList = attributeList;
    }

    /**
     * @see #extensions
     */
    @Override
    public Set<SCIMResourceTypeExtensionSchema> getExtensions() {
        return extensions;
    }

    /**
     * @see #extensions
     */
    public void setExtensions(Set<SCIMResourceTypeExtensionSchema> extensions) {
        this.extensions = extensions;
    }

    /**
     * adds a new extension to this schema
     */
    public void addExtension(SCIMResourceTypeExtensionSchema extension) {
        if (this.extensions == null) {
            this.extensions = new HashSet<>();
        }
        if (extension != null) {
            this.extensions.add(extension);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
