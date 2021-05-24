/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.charon3.core.config;

import org.wso2.charon3.core.attributes.SCIMCustomAttribute;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This builds the custom user schema.
 */
public class SCIMCustomSchemaExtensionBuilder extends ExtensionBuilder {

    private static SCIMCustomSchemaExtensionBuilder customSchemaExtensionBuilder =
            new SCIMCustomSchemaExtensionBuilder();
    private String rootAttributeURI;

    private SCIMCustomSchemaExtensionBuilder() {

    }

    public static SCIMCustomSchemaExtensionBuilder getInstance() {

        return customSchemaExtensionBuilder;
    }

    /**
     * Set the rootAttribute URI of the customschema.
     *
     * @param schemaUri RootAttribute.
     */
    public void setURI(String schemaUri) {

        this.rootAttributeURI = schemaUri;
    }

    @Override
    public String getURI() {

        return rootAttributeURI;
    }

    /**
     * Builds Custom Attribute Schema and returns it. It takes list of SCIMCustAttributes and reads the properties
     * and converts it to Custom AttributeSchema.
     *
     * @param attributes List of SCIMCustomAttributes.
     * @return Attribute Schema of the custom schema URI.
     * @throws CharonException
     * @throws InternalErrorException
     */
    public AttributeSchema buildUserCustomSchemaExtension(List<SCIMCustomAttribute> attributes) throws CharonException,
            InternalErrorException {

        // Variable attributeSchema is a scim spec defined object format that includes subattributes, etc.
        Map<String, AttributeSchema> attributeSchemas = new HashMap<>();
        Map<String, ExtensionAttributeSchemaConfig> attributeConfigs = new HashMap<>();

        readConfiguration(attributes, attributeConfigs);
        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig :
                attributeConfigs.entrySet()) {
            // If there are no children its a simple attribute, build it.
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas);
            } else {
                // Need to build child schemas first.
                buildComplexAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas, attributeConfigs);
            }
        }
        // Now get the extension schema.
        return  attributeSchemas.get(rootAttributeURI);
    }

    /**
     * This method reads the list of custom schema configurations along with properties and converts it to a
     * ExtensionAttributeSchemaConfig data model which is a intermediate data model.
     *
     * @param schemaConfigurations List of custom schema attributes.
     * @param attributeConfigs     Map of ExtensionAttributeSchemaConfigs.
     */
    private void readConfiguration(List<SCIMCustomAttribute> schemaConfigurations, Map<String,
            ExtensionAttributeSchemaConfig> attributeConfigs) {

        for (SCIMCustomAttribute schemaConfiguration : schemaConfigurations) {
            ExtensionAttributeSchemaConfig schemaAttributeConfig =
                    new ExtensionAttributeSchemaConfig
                            (schemaConfiguration.getProperties());
            attributeConfigs.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);
        }
    }
}
