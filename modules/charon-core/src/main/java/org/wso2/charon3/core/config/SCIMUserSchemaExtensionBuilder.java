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

/**
 * This class is to build the extension user schema though the config file.
 */
public class SCIMUserSchemaExtensionBuilder extends ExtensionBuilder {

    private static SCIMUserSchemaExtensionBuilder configReader = new SCIMUserSchemaExtensionBuilder();
    // configuration map
    private static Map<String, ExtensionAttributeSchemaConfig> extensionConfig = new HashMap<>();
    // Extension root attribute name.
    String extensionRootAttributeName = null;
    String extensionRootAttributeURI = null;
    // built schema map
    private static Map<String, AttributeSchema> attributeSchemas = new HashMap<String, AttributeSchema>();
    // extension root attribute schema
    private AttributeSchema extensionSchema = null;

    public static SCIMUserSchemaExtensionBuilder getInstance() {
        return configReader;
    }

    public AttributeSchema getExtensionSchema() {
        return extensionSchema;
    }

    public void buildUserSchemaExtension(String configFilePath) throws CharonException, InternalErrorException {

        File provisioningConfig = new File(configFilePath);

        try (InputStream inputStream = new FileInputStream(provisioningConfig)) {

            buildUserSchemaExtension(inputStream);

        } catch (FileNotFoundException e) {
            throw new CharonException(SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file not found!",
                    e);
        } catch (IOException e) {
            throw new CharonException("Error while closing " +
                    SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
        }
    }

    /*
     * Logic goes here
     * @throws CharonException
     */
    public void buildUserSchemaExtension(InputStream inputStream) throws CharonException, InternalErrorException {

        readConfiguration(inputStream);

        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : extensionConfig.entrySet()) {
            // if there are no children its a simple attribute, build it
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas);
            } else {
                // need to build child schemas first
                buildComplexAttributeSchema(attributeSchemaConfig.getValue(), attributeSchemas, extensionConfig);
            }
        }
        // now get the extension schema
        /*
         * Assumption : Final config in the configuration file is the extension
         * root attribute
         */
        extensionSchema = attributeSchemas.get(extensionRootAttributeURI);
    }

    /*
     * This method reads configuration file and stores in the memory as an
     * configuration map
     *
     * @param configFilePath
     * @throws CharonException
     */
    private void readConfiguration(InputStream inputStream) throws CharonException {

        try {
            Scanner scanner = new Scanner(inputStream, "utf-8").useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";

            JSONArray attributeConfigArray = new JSONArray(jsonString);

            for (int index = 0; index < attributeConfigArray.length(); ++index) {
                JSONObject rawAttributeConfig = attributeConfigArray.getJSONObject(index);
                ExtensionAttributeSchemaConfig schemaAttributeConfig =
                        new ExtensionAttributeSchemaConfig(rawAttributeConfig);
                extensionConfig.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);

                /**
                 * NOTE: Assume last config is the root config
                 */
                if (index == attributeConfigArray.length() - 1) {
                    extensionRootAttributeURI = schemaAttributeConfig.getURI();
                    extensionRootAttributeName = schemaAttributeConfig.getName();
                }
            }
        } catch (JSONException e) {
            throw new CharonException("Error while parsing " +
                    SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
        }
    }


    @Override
    public String getURI() {

        return extensionRootAttributeURI;
    }

    protected boolean isRootConfig(ExtensionAttributeSchemaConfig config) {

        return StringUtils.isNotBlank(extensionRootAttributeName) &&
                extensionRootAttributeName.equals(config.getName());
    }
}

