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

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon3.core.attributes.SCIMCustomAttribute;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.wso2.charon3.core.schema.SCIMConstants.AGENT_SCHEMA_URI;

/**
 * This class is to build the extension agent schema through the config file.
 */
public class SCIMAgentSchemaExtensionBuilder extends ExtensionBuilder {

    private static final SCIMAgentSchemaExtensionBuilder instance = new SCIMAgentSchemaExtensionBuilder();
    private static final Map<String, ExtensionAttributeSchemaConfig> extensionConfig = new java.util.LinkedHashMap<>();
    private static final Map<String, AttributeSchema> attributeSchemas = new HashMap<>();
    private AttributeSchema extensionSchema = null;
    private String extensionRootAttributeName = null;
    private static final String EXTENSION_ROOT_ATTRIBUTE_URI = AGENT_SCHEMA_URI;
    private static final String DELIMITER = "\\A";

    /**
     * Get the instance of the SCIMAgentSchemaExtensionBuilder.
     *
     * @return The instance of the SCIMAgentSchemaExtensionBuilder.
     */
    public static SCIMAgentSchemaExtensionBuilder getInstance() {

        return instance;
    }

    /**
     * Get the extension schema.
     *
     * @return The extension schema.
     */
    public AttributeSchema getExtensionSchema() {

        return extensionSchema;
    }

    /**
     * Build the agent schema extension from the config file.
     *
     * @param configFilePath Path to the config file.
     * @throws CharonException        If an error occurred while reading the config
     *                                file.
     * @throws InternalErrorException If an error occurred while building the
     *                                schema.
     */
    public void buildAgentSchemaExtension(String configFilePath) throws CharonException, InternalErrorException {

        File provisioningConfig = new File(configFilePath);
        try (InputStream configFilePathInputStream = new FileInputStream(provisioningConfig)) {
            buildAgentSchemaExtension(configFilePathInputStream);
        } catch (FileNotFoundException e) {
            throw new CharonException(configFilePath + " file not found!", e);
        } catch (JSONException e) {
            throw new CharonException("Error while parsing " + configFilePath + " file!", e);
        } catch (IOException e) {
            throw new CharonException("Error while closing " + configFilePath + " file!", e);
        }
    }

    /**
     * Build the agent schema extension from the input stream.
     *
     * @param inputStream The input stream.
     * @throws CharonException        If an error occurred while reading the
     *                                configuration.
     * @throws InternalErrorException If an error occurred while building the
     *                                schema.
     */
    public void buildAgentSchemaExtension(InputStream inputStream) throws CharonException, InternalErrorException {

        readConfiguration(inputStream);
        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : extensionConfig.entrySet()) {
            // If there are no children it is a simple attribute, build it.
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas);
            } else {
                // Need to build child schemas first.
                buildComplexAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas, extensionConfig);
            }
        }

        extensionSchema = attributeSchemas.get(EXTENSION_ROOT_ATTRIBUTE_URI);
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

    /**
     * Read the configuration from the input stream.
     *
     * @param inputStream The input stream.
     * @throws CharonException If an error occurred while reading the configuration.
     */
    public void readConfiguration(InputStream inputStream) throws CharonException {

        if (inputStream == null) {
            throw new CharonException("Input stream is null.");
        }
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter(DELIMITER);
        String jsonString = scanner.hasNext() ? scanner.next() : StringUtils.EMPTY;

        JSONArray attributeConfigArray = new JSONArray(jsonString);

        for (int index = 0; index < attributeConfigArray.length(); ++index) {
            JSONObject rawAttributeConfig = attributeConfigArray.getJSONObject(index);
            ExtensionAttributeSchemaConfig schemaAttributeConfig = new ExtensionAttributeSchemaConfig(
                    rawAttributeConfig);
            if (schemaAttributeConfig.getURI().startsWith(EXTENSION_ROOT_ATTRIBUTE_URI)) {
                extensionConfig.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);
            }

            if (EXTENSION_ROOT_ATTRIBUTE_URI.equals(schemaAttributeConfig.getURI())) {
                extensionRootAttributeName = schemaAttributeConfig.getName();
            }
        }
        
    }
    
    @Override
    public String getURI() {

        return EXTENSION_ROOT_ATTRIBUTE_URI;
    }

    @Override
    protected boolean isRootConfig(ExtensionAttributeSchemaConfig config) {

        return StringUtils.equals(extensionRootAttributeName, config.getName());
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
