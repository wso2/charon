/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.config;

import org.wso2.charon3.core.attributes.SCIMCustomAttribute;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.AGENT_SCHEMA_URI;

/**
 * This class is to build the extension agent schema through the config file.
 */
public class SCIMAgentSchemaExtensionBuilder extends ExtensionBuilder {

    private static final SCIMAgentSchemaExtensionBuilder INSTANCE = new SCIMAgentSchemaExtensionBuilder();
    private static final String EXTENSION_ROOT_ATTRIBUTE_URI = AGENT_SCHEMA_URI;

    /**
     * Get the instance of the SCIMAgentSchemaExtensionBuilder.
     *
     * @return The instance of the SCIMAgentSchemaExtensionBuilder.
     */
    public static SCIMAgentSchemaExtensionBuilder getInstance() {

        return instance;
    }

    /**
     * Build the agent schema extension from the list of custom attributes.
     *
     * @param attributes List of SCIMCustomAttribute.
     * @return AttributeSchema of the agent schema extension.
     * @throws CharonException        If an error occurred while reading the
     *                                configuration.
     * @throws InternalErrorException If an error occurred while building the
     *                                schema.
     */
    public AttributeSchema buildAgentSchemaExtension(List<SCIMCustomAttribute> attributes) throws CharonException,
            InternalErrorException {

        // Variable attributeSchema is a scim spec defined object format that includes
        // subattributes, etc.
        Map<String, AttributeSchema> attributeSchemas = new HashMap<>();
        Map<String, ExtensionAttributeSchemaConfig> attributeConfigs = new HashMap<>();

        readConfiguration(attributes, attributeConfigs);
        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : attributeConfigs.entrySet()) {
            // If there are no children its a simple attribute, build it.
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas);
            } else {
                // Need to build child schemas first.
                buildComplexAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas, attributeConfigs);
            }
        }
        // Now get the extension schema.
        return attributeSchemas.get(EXTENSION_ROOT_ATTRIBUTE_URI);
    }

    @Override
    public String getURI() {

        return EXTENSION_ROOT_ATTRIBUTE_URI;
    }

    private void readConfiguration(List<SCIMCustomAttribute> schemaConfigurations,
            Map<String, ExtensionAttributeSchemaConfig> attributeConfigs) {

        for (SCIMCustomAttribute schemaConfiguration : schemaConfigurations) {
            ExtensionAttributeSchemaConfig schemaAttributeConfig = new ExtensionAttributeSchemaConfig(
                    schemaConfiguration.getProperties());
            attributeConfigs.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);
        }
    }
}
