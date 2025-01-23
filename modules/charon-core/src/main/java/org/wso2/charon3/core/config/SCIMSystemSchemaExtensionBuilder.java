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
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.wso2.charon3.core.schema.SCIMConstants.SYSTEM_USER_SCHEMA_URI;

/**
 * This class is to build the extension system schema through the config file.
 */
public class SCIMSystemSchemaExtensionBuilder extends ExtensionBuilder {

    private static final SCIMSystemSchemaExtensionBuilder instance = new SCIMSystemSchemaExtensionBuilder();
    private static final Map<String, ExtensionAttributeSchemaConfig> extensionConfig = new HashMap<>();
    private static final Map<String, AttributeSchema> attributeSchemas = new HashMap<>();
    private AttributeSchema extensionSchema = null;
    String extensionRootAttributeName = null;
    String extensionRootAttributeURI;

    /**
     * Get the instance of the SCIMSystemSchemaExtensionBuilder.
     *
     * @return The instance of the SCIMSystemSchemaExtensionBuilder.
     */
    public static SCIMSystemSchemaExtensionBuilder getInstance() {

        return instance;
    }

    /**
     * Constructor of the SCIMSystemSchemaExtensionBuilder.
     */
    private SCIMSystemSchemaExtensionBuilder() {

        extensionRootAttributeURI = SYSTEM_USER_SCHEMA_URI;
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
     * Build the system schema extension from the config file.
     *
     * @param configFilePath            Path to the config file.
     * @throws CharonException          If an error occurred while reading the config file.
     * @throws InternalErrorException   If an error occurred while building the schema.
     */
    public void buildSystemSchemaExtension(String configFilePath) throws CharonException, InternalErrorException {

        File provisioningConfig = new File(configFilePath);
        try (InputStream configFilePathInputStream = new FileInputStream(provisioningConfig)) {
            buildSystemSchemaExtension(configFilePathInputStream);
        } catch (FileNotFoundException e) {
            throw new CharonException(configFilePath + " file not found!", e);
        } catch (JSONException e) {
            throw new CharonException("Error while parsing " + configFilePath + " file!", e);
        } catch (IOException e) {
            throw new CharonException("Error while closing " + configFilePath + " file!", e);
        }
    }

    /**
     * Build the system schema extension from the input stream.
     *
     * @param inputStream               The input stream.
     * @throws CharonException          If an error occurred while reading the configuration.
     * @throws InternalErrorException   If an error occurred while building the schema.
     */
    public void buildSystemSchemaExtension(InputStream inputStream) throws CharonException, InternalErrorException {

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

        extensionSchema = attributeSchemas.get(extensionRootAttributeURI);
    }

    /**
     * Read the configuration from the input stream.
     *
     * @param inputStream               The input stream.
     * @throws CharonException          If an error occurred while reading the configuration.
     */
    public void readConfiguration(InputStream inputStream) throws CharonException {

        Scanner scanner = new Scanner(inputStream, "utf-8").useDelimiter("\\A");
        String jsonString = scanner.hasNext() ? scanner.next() : "";

        JSONArray attributeConfigArray = new JSONArray(jsonString);

        for (int index = 0; index < attributeConfigArray.length(); ++index) {
            JSONObject rawAttributeConfig = attributeConfigArray.getJSONObject(index);
            ExtensionAttributeSchemaConfig schemaAttributeConfig =
                    new ExtensionAttributeSchemaConfig(rawAttributeConfig);
            if (schemaAttributeConfig.getURI().startsWith(extensionRootAttributeURI)) {
                extensionConfig.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);
            }

            if (extensionRootAttributeURI.equals(schemaAttributeConfig.getURI())) {
                extensionRootAttributeName = schemaAttributeConfig.getName();
            }
        }
    }

    @Override
    public String getURI() {

        return extensionRootAttributeURI;
    }

    @Override
    protected boolean isRootConfig(ExtensionAttributeSchemaConfig config) {

        return StringUtils.isNotBlank(extensionRootAttributeName)
                && extensionRootAttributeName.equals(config.getName());
    }
}
