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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is to build the extension user schema though the config file.
 */
public class SCIMUserSchemaExtensionBuilder {

    private static SCIMUserSchemaExtensionBuilder configReader = new SCIMUserSchemaExtensionBuilder();
    // configuration map
    private static Map<String, ExtensionAttributeSchemaConfig> extensionConfig =
            new HashMap<String, ExtensionAttributeSchemaConfig>();

    private static Map<String, Map<String, AttributeSchema>> extensionSchemaMap =
            new HashMap<String, Map<String, AttributeSchema>>();

    // extension root attribute name
    String extensionRootAttributeName = null;
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

    /*
     * Logic goes here
     * @throws CharonException
     */
    public void buildUserSchemaExtension(String configFilePath) throws CharonException, InternalErrorException {
        readConfiguration(configFilePath);

        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : extensionConfig.entrySet()) {
            // if there are no children its a simple attribute, build it
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue());
            } else {
                // need to build child schemas first
                buildComplexAttributeSchema(attributeSchemaConfig.getValue());
            }
        }
        // now get the extension schema
        /*
         * Assumption : Final config in the configuration file is the extension
         * root attribute
         */
        extensionSchema = attributeSchemas.get(extensionRootAttributeName);
    }

    public void buildUserSchemaExtension(String configFilePath, String tenantId) throws CharonException,
            InternalErrorException {
        extensionConfig.clear();
        attributeSchemas.clear();
        readConfiguration(configFilePath);

        for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : extensionConfig.entrySet()) {
            // if there are no children its a simple attribute, build it
            if (!attributeSchemaConfig.getValue().hasChildren()) {
                buildSimpleAttributeSchema(attributeSchemaConfig.getValue());
            } else {
                // need to build child schemas first
                buildComplexAttributeSchema(attributeSchemaConfig.getValue());
            }
        }
        // now get the extension schema
        /*
         * Assumption : Final config in the configuration file is the extension
         * root attribute
         */
        extensionSchema = attributeSchemas.get(extensionRootAttributeName);

        Map<String, AttributeSchema> schemaMap = extensionSchemaMap.get(tenantId);
        if (schemaMap == null) {
          schemaMap = new HashMap<String, AttributeSchema>();
        }
        schemaMap.put(extensionSchema.getURI(), extensionSchema);
        extensionSchemaMap.put(tenantId, schemaMap);
    }

    public static Map<String, AttributeSchema> getExtensionSchema(String tenantId) {
        return  extensionSchemaMap.get(tenantId);
    }

    /*
     * This method reads configuration file and stores in the memory as an
     * configuration map
     *
     * @param configFilePath
     * @throws CharonException
     */
    private void readConfiguration(String configFilePath) throws CharonException {
        File provisioningConfig = new File(configFilePath);
        try {
            InputStream inputStream = new FileInputStream(provisioningConfig);
            //Scanner scanner = new Scanner(new FileInputStream(provisioningConfig));
            Scanner scanner = new Scanner(inputStream, "utf-8").useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";

            JSONArray attributeConfigArray = new JSONArray(jsonString);

            for (int index = 0; index < attributeConfigArray.length(); ++index) {
                JSONObject attributeConfig = attributeConfigArray.getJSONObject(index);
                ExtensionAttributeSchemaConfig attrubteConfig =
                        new ExtensionAttributeSchemaConfig(attributeConfig);
                extensionConfig.put(attrubteConfig.getName(), attrubteConfig);

                /**
                 * NOTE: Assume last config is the root config
                 */
                if (index == attributeConfigArray.length() - 1) {
                    extensionRootAttributeName = attrubteConfig.getName();
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            throw new CharonException(SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file not found!",
                    e);
        } catch (JSONException e) {
            throw new CharonException("Error while parsing " +
                    SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
        } catch (IOException e) {
            throw new CharonException("Error while closing " +
                    SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
        }
    }


    /*
     * Knows how to build a complex attribute
     *
     * @param config
     */
    private void buildComplexAttributeSchema(ExtensionAttributeSchemaConfig config) throws InternalErrorException {
        if (!attributeSchemas.containsKey(config.getName())) {
            String[] subAttributes = config.getSubAttributes();
            for (String subAttribute : subAttributes) {
                ExtensionAttributeSchemaConfig subAttribConfig = extensionConfig.get(subAttribute);
                if (!subAttribConfig.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (subAttribConfig.hasChildren()) {
                        String error = "A attribute of primitive type can not have sub attributes";
                        throw new InternalErrorException(error);
                    } else {
                        buildSimpleAttributeSchema(subAttribConfig);
                    }
                } else {
                    if (!(subAttribConfig.hasChildren())) {
                        String error = "A attribute of complex type should have sub attributes";
                        throw new InternalErrorException(error);
                    } else {
                        // need to build child schemas first
                        buildComplexAttributeSchema(subAttribConfig);
                    }
                }
            }
            // now all sub attributes must be already built
            buildComplexSchema(config);
        }
    }

    /*
     * Has the logic to iterate through child attributes
     *
     * @param config
     */
    private void buildComplexSchema(ExtensionAttributeSchemaConfig config) {
        String[] subAttributeNames = config.getSubAttributes();
        ArrayList<AttributeSchema> subAttributes = new ArrayList<AttributeSchema>();
        for (String subAttributeName : subAttributeNames) {
            subAttributes.add(attributeSchemas.get(subAttributeName));
        }
        AttributeSchema complexAttribute =
                createSCIMAttributeSchema(config, subAttributes);
        attributeSchemas.put(config.getName(), complexAttribute);
    }

    /*
     * Builds simple attribute schema
     *
     * @param config
     */
    private void buildSimpleAttributeSchema(ExtensionAttributeSchemaConfig config) {
        ArrayList<AttributeSchema> subAttributeList = new ArrayList<AttributeSchema>();
        if (!attributeSchemas.containsKey(config.getName())) {
            AttributeSchema attributeSchema =
                    createSCIMAttributeSchema(config, subAttributeList);
            attributeSchemas.put(config.getName(), attributeSchema);
        }

    }

    /*
     * create SCIM Attribute Schema
     * @param attribute
     * @param subAttributeList
     * @return
     */
    public SCIMAttributeSchema createSCIMAttributeSchema(ExtensionAttributeSchemaConfig attribute,
                                                         ArrayList<AttributeSchema> subAttributeList) {

        return SCIMAttributeSchema.createSCIMAttributeSchema
                (attribute.getURI(), attribute.getName(), attribute.getType(),
                        attribute.getMultiValued(), attribute.description, attribute.required, attribute.caseExact,
                        attribute.mutability, attribute.returned, attribute.uniqueness,
                        attribute.canonicalValues, attribute.referenceTypes, subAttributeList);
    }


    static class ExtensionAttributeSchemaConfig {
        //unique identifier for the attribute
        private String uri;
        //name of the attribute
        private String name;
        //data type of the attribute
        private SCIMDefinitions.DataType type;
        //Boolean value indicating the attribute's plurality.
        private Boolean multiValued;
        //The attribute's human readable description
        private String description;
        //A Boolean value that specifies if the attribute is required
        private Boolean required;
        //A Boolean value that specifies if the String attribute is case sensitive
        private Boolean caseExact;
        //A SCIM defined value that specifies if the attribute's mutability.
        private SCIMDefinitions.Mutability mutability;
        //A SCIM defined value that specifies when the attribute's value need to be returned.
        private SCIMDefinitions.Returned returned;
        //A SCIM defined value that specifies the uniqueness level of an attribute.
        private SCIMDefinitions.Uniqueness uniqueness;
        //A list specifying the contained attributes. OPTIONAL.
        private String[] subAttributes;
        //A collection of suggested canonical values that MAY be used -OPTIONAL
        private ArrayList<String> canonicalValues;
        //A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced
        //only applicable for attributes that are of type "reference"
        private ArrayList<SCIMDefinitions.ReferenceType> referenceTypes;

        public String[] getSubAttributes() {
            return subAttributes;
        }

        public String getURI() {
            return uri;
        }

        public void setURI(String uri) {
            this.uri = uri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SCIMDefinitions.DataType getType() {
            return type;
        }

        public void setType(SCIMDefinitions.DataType type) {
            this.type = type;
        }

        public Boolean getMultiValued() {
            return multiValued;
        }

        public void setMultiValued(Boolean multiValued) {
            this.multiValued = multiValued;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Boolean getCaseExact() {
            return caseExact;
        }

        public void setCaseExact(Boolean caseExact) {
            this.caseExact = caseExact;
        }

        public SCIMDefinitions.Mutability getMutability() {
            return mutability;
        }

        public void setMutability(SCIMDefinitions.Mutability mutability) {
            this.mutability = mutability;
        }

        public SCIMDefinitions.Returned getReturned() {
            return returned;
        }

        public void setReturned(SCIMDefinitions.Returned returned) {
            this.returned = returned;
        }

        public SCIMDefinitions.Uniqueness getUniqueness() {
            return uniqueness;
        }

        public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness) {
            this.uniqueness = uniqueness;
        }

        public ArrayList<String> getCanonicalValues() {
            return canonicalValues;
        }


        public ArrayList<SCIMDefinitions.ReferenceType> getReferenceTypes() {
            return referenceTypes;
        }

        public boolean hasChildren() {
            return subAttributes != null ? true : false;
        }

        public ExtensionAttributeSchemaConfig(JSONObject attributeConfigJSON) throws CharonException {
            try {
                uri = attributeConfigJSON.getString(SCIMConfigConstants.ATTRIBUTE_URI);
                name = attributeConfigJSON.getString(SCIMConfigConstants.ATTRIBUTE_NAME);
                type = getDefinedDataType(attributeConfigJSON.getString(SCIMConfigConstants.DATA_TYPE));
                multiValued = attributeConfigJSON.getBoolean(SCIMConfigConstants.MULTIVALUED);
                description = attributeConfigJSON.getString(SCIMConfigConstants.DESCRIPTION);
                required = Boolean.parseBoolean(attributeConfigJSON.getString(SCIMConfigConstants.REQUIRED));
                caseExact = Boolean.parseBoolean(attributeConfigJSON.getString(SCIMConfigConstants.CASE_EXACT));
                mutability = getDefinedMutability(attributeConfigJSON.getString(SCIMConfigConstants.MUTABILITY));
                returned = getDefinedReturned(attributeConfigJSON.getString(SCIMConfigConstants.RETURNED));
                uniqueness = getDefinedUniqueness(attributeConfigJSON.getString(SCIMConfigConstants.UNIQUENESS));
                String subAttributesString = attributeConfigJSON.getString(SCIMConfigConstants.SUB_ATTRIBUTES);
                if (!"null".equalsIgnoreCase(subAttributesString)) {
                    subAttributes = subAttributesString.split(" ");
                }
                canonicalValues = setCanonicalValues(
                        attributeConfigJSON.getJSONArray(SCIMConfigConstants.CANONICAL_VALUES));
                referenceTypes = setReferenceTypes(
                        attributeConfigJSON.getJSONArray(SCIMConfigConstants.REFERENCE_TYPES));

            } catch (JSONException e) {
                throw new CharonException("Error while parsing extension configuration", e);
            }

        }

        /*
         * this builds the relevant data types according to what has configured in config file
         * @param input
         * @return
         */
        private SCIMDefinitions.DataType getDefinedDataType(String input) {
            SCIMDefinitions.DataType type = null;
            if ("STRING".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.STRING;
            } else if ("INTEGER".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.INTEGER;
            } else if ("DECIMAL".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.DECIMAL;
            } else if ("BOOLEAN".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.BOOLEAN;
            } else if ("DATETIME".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.DATE_TIME;
            } else if ("BINARY".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.BINARY;
            } else if ("REFERENCE".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.REFERENCE;
            } else if ("COMPLEX".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.DataType.COMPLEX;
            }
            return type;
        }

        /*
         * this builds the relevant mutability according to what has configured in config file
         * @param input
         * @return
         */
        private SCIMDefinitions.Mutability getDefinedMutability(String input) {
            SCIMDefinitions.Mutability type = null;
            if ("readWrite".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Mutability.READ_WRITE;
            } else if ("readOnly".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Mutability.READ_ONLY;
            } else if ("immutable".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Mutability.IMMUTABLE;
            } else if ("writeOnly".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Mutability.WRITE_ONLY;
            }
            return type;
        }

        /*
         * this builds the relevant returned type according to what has configured in config file
         * @param input
         * @return
         */
        private SCIMDefinitions.Returned getDefinedReturned(String input) {
            SCIMDefinitions.Returned type = null;
            if ("always".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Returned.ALWAYS;
            } else if ("never".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Returned.NEVER;
            } else if ("default".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Returned.DEFAULT;
            } else if ("request".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Returned.REQUEST;
            }
            return type;
        }

        /*
         * this builds the relevant uniqueness according to what has configured in config file
         * @param input
         * @return
         */
        private SCIMDefinitions.Uniqueness getDefinedUniqueness(String input) {
            SCIMDefinitions.Uniqueness type = null;
            if ("none".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Uniqueness.NONE;
            } else if ("server".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Uniqueness.SERVER;
            } else if ("global".equalsIgnoreCase(input)) {
                type = SCIMDefinitions.Uniqueness.GLOBAL;
            }
            return type;
        }

        /*
         * this builds the relevant canonical values according to what has configured in config file
         * @param input
         * @return
         */
        private ArrayList<String> setCanonicalValues(JSONArray input) throws JSONException {
            ArrayList<String> canonicalValues = new ArrayList<String>();
            JSONArray canonicalValuesList = input;
            for (int index = 0; index < canonicalValuesList.length(); ++index) {
                canonicalValues.add((String) canonicalValuesList.get(index));
            }
            return canonicalValues;
        }

        /*
         * this builds the relevant reference types according to what has configured in config file
         * @param input
         * @return
         */
        private ArrayList<SCIMDefinitions.ReferenceType> setReferenceTypes(JSONArray input) throws JSONException {
            ArrayList<SCIMDefinitions.ReferenceType> referenceTypes = new ArrayList<SCIMDefinitions.ReferenceType>();
            JSONArray referenceTypesList = input;

            for (int index = 0; index < referenceTypesList.length(); ++index) {
                String referenceValue = (String) referenceTypesList.get(index);

                if (referenceValue.equalsIgnoreCase("external")) {
                    referenceTypes.add(SCIMDefinitions.ReferenceType.EXTERNAL);
                } else if (referenceValue.equalsIgnoreCase("user")) {
                    referenceTypes.add(SCIMDefinitions.ReferenceType.USER);
                } else if (referenceValue.equalsIgnoreCase("group")) {
                    referenceTypes.add(SCIMDefinitions.ReferenceType.GROUP);
                } else if (referenceValue.equalsIgnoreCase("uri")) {
                    referenceTypes.add(SCIMDefinitions.ReferenceType.URI);
                }
            }
            return referenceTypes;
        }
    }

}

