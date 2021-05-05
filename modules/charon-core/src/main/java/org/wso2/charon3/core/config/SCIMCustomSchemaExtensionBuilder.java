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

import org.apache.commons.lang.StringUtils;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.CUSTOM_USER_SCHEMA_URI;

public class SCIMCustomSchemaExtensionBuilder {

    private static SCIMCustomSchemaExtensionBuilder customSchemaExtensionBuilder =
            new SCIMCustomSchemaExtensionBuilder();
    // configuration map
    private Map<String, CustomAttributeSchemaConfig> customConfig;
    // Extension root attribute name.
    String customRootAttributeName = CUSTOM_USER_SCHEMA_URI;
    String customRootAttributeURI = CUSTOM_USER_SCHEMA_URI;
    // built schema map
    private Map<String, AttributeSchema> attributeSchemas;
    // extension root attribute schema
    private AttributeSchema customSchema = null;

    public static SCIMCustomSchemaExtensionBuilder getInstance() {

        return customSchemaExtensionBuilder;
    }

    public AttributeSchema getCustomSchema() {

        return customSchema;
    }

    public String getURI() {

        return CUSTOM_USER_SCHEMA_URI;
    }

    public void buildUserCustomSchemaExtension(List<SCIMCustomAttribute> schemaConfigurations) throws CharonException
            , InternalErrorException {

        readConfiguration(schemaConfigurations);
        for (Map.Entry<String, CustomAttributeSchemaConfig> attributeSchemaConfig :
                customConfig.entrySet()) {
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
        customSchema = attributeSchemas.get(customRootAttributeURI);
    }

    private void readConfiguration(List<SCIMCustomAttribute> schemaConfigurations) throws CharonException {

        attributeSchemas = new HashMap<String, AttributeSchema>();
        customConfig = new HashMap<String, CustomAttributeSchemaConfig>();
        for (SCIMCustomAttribute schemaConfiguration : schemaConfigurations) {
            CustomAttributeSchemaConfig schemaAttributeConfig =
                    new CustomAttributeSchemaConfig
                            (schemaConfiguration.getProperties());
            customConfig.put(schemaAttributeConfig.getURI(), schemaAttributeConfig);
        }
    }


    private String getSubAttributeURI(CustomAttributeSchemaConfig config,
                                      String subAttributeName) {

        if (isRootConfig(config)) {
            return config.getURI() + ":" + subAttributeName;
        } else {
            return config.getURI() + "." + subAttributeName;
        }
    }

    private boolean isRootConfig(CustomAttributeSchemaConfig config) {

        return StringUtils.isNotBlank(customRootAttributeName) && customRootAttributeName.equals(config.getName());
    }

    /*
     * Knows how to build a complex attribute
     *
     * @param config
     */
    private void buildComplexAttributeSchema(CustomAttributeSchemaConfig config)
            throws InternalErrorException {

        if (!attributeSchemas.containsKey(config.getURI())) {
            String[] subAttributes = config.getSubAttributes();
            for (String subAttribute : subAttributes) {
                CustomAttributeSchemaConfig subAttribConfig =
                        customConfig.get(getSubAttributeURI(config, subAttribute));
                if (subAttribConfig == null) {
                    String error = String.format("Error adding subattribute %s to attribute %s. Error in SCIM2 " +
                            "extension schema config format.", subAttribute, config.getURI());
                    throw new InternalErrorException(error);
                }

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
    private void buildComplexSchema(CustomAttributeSchemaConfig config) {

        String[] subAttributeNames = config.getSubAttributes();
        ArrayList<AttributeSchema> subAttributes = new ArrayList<AttributeSchema>();
        for (String subAttributeName : subAttributeNames) {
            subAttributes.add(attributeSchemas.get(getSubAttributeURI(config, subAttributeName)));
        }
        AttributeSchema complexAttribute = createSCIMAttributeSchema(config, subAttributes);
        attributeSchemas.put(config.getURI(), complexAttribute);
    }

    /*
     * Builds simple attribute schema
     *
     * @param config
     */
    private void buildSimpleAttributeSchema(CustomAttributeSchemaConfig config) {

        ArrayList<AttributeSchema> subAttributeList = new ArrayList<AttributeSchema>();
        if (!attributeSchemas.containsKey(config.getURI())) {
            AttributeSchema attributeSchema = createSCIMAttributeSchema(config, subAttributeList);
            attributeSchemas.put(config.getURI(), attributeSchema);
        }
    }

    /*
     * create SCIM Attribute Schema
     * @param attribute
     * @param subAttributeList
     * @return
     */
    public SCIMAttributeSchema createSCIMAttributeSchema(CustomAttributeSchemaConfig
                                                                 attribute, ArrayList<AttributeSchema> subAttributeList)
    {

        return SCIMAttributeSchema.createSCIMAttributeSchema(attribute.getURI(), attribute.getName(),
                attribute.getType(), attribute.getMultiValued(), attribute.description, attribute.required,
                attribute.caseExact, attribute.mutability, attribute.returned, attribute.uniqueness, null,
                null, subAttributeList);
    }


    static class CustomAttributeSchemaConfig {

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
        //A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced
        //only applicable for attributes that are of type "reference"
        //        private ArrayList<SCIMDefinitions.ReferenceType> referenceTypes;

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


        //        public ArrayList<SCIMDefinitions.ReferenceType> getReferenceTypes() {
        //            return referenceTypes;
        //        }

        public boolean hasChildren() {

            return subAttributes != null ? true : false;
        }

        public CustomAttributeSchemaConfig(Map<String, String> attributeConfig) {

            uri = attributeConfig.get(SCIMConfigConstants.ATTRIBUTE_URI);
            name = attributeConfig.get(SCIMConfigConstants.ATTRIBUTE_NAME);
            type = getDefinedDataType(attributeConfig.get(SCIMConfigConstants.DATA_TYPE));
            multiValued = Boolean.valueOf(attributeConfig.get(SCIMConfigConstants.MULTIVALUED));
            description = attributeConfig.get(SCIMConfigConstants.DESCRIPTION);
            required = Boolean.parseBoolean(attributeConfig.get(SCIMConfigConstants.REQUIRED));
            caseExact = Boolean.parseBoolean(attributeConfig.get(SCIMConfigConstants.CASE_EXACT));
            mutability = getDefinedMutability(attributeConfig.get(SCIMConfigConstants.MUTABILITY));
            returned = getDefinedReturned(attributeConfig.get(SCIMConfigConstants.RETURNED));
            uniqueness = getDefinedUniqueness(attributeConfig.get(SCIMConfigConstants.UNIQUENESS));
            String subAttributesString = attributeConfig.get(SCIMConfigConstants.SUB_ATTRIBUTES);
            if (StringUtils.isNotBlank(subAttributesString) && !"null".equalsIgnoreCase(subAttributesString)) {
                subAttributes = subAttributesString.split(" ");
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
            } else {
                type = SCIMDefinitions.DataType.STRING;
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
            } else {
                type = SCIMDefinitions.Mutability.READ_WRITE;
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
            } else {
                type = SCIMDefinitions.Returned.DEFAULT;
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
            } else {
                type = SCIMDefinitions.Uniqueness.NONE;
            }
            return type;
        }


        //        /*
        //         * this builds the relevant reference types according to what has configured in config file
        //         * @param input
        //         * @return
        //         */
        //        private ArrayList<SCIMDefinitions.ReferenceType> setReferenceTypes(JSONArray input) throws
        //        JSONException {
        //            ArrayList<SCIMDefinitions.ReferenceType> referenceTypes = new ArrayList<SCIMDefinitions
        //            .ReferenceType>();
        //            JSONArray referenceTypesList = input;
        //
        //            for (int index = 0; index < referenceTypesList.length(); ++index) {
        //                String referenceValue = (String) referenceTypesList.get(index);
        //
        //                if (referenceValue.equalsIgnoreCase("external")) {
        //                    referenceTypes.add(SCIMDefinitions.ReferenceType.EXTERNAL);
        //                } else if (referenceValue.equalsIgnoreCase("user")) {
        //                    referenceTypes.add(SCIMDefinitions.ReferenceType.USER);
        //                } else if (referenceValue.equalsIgnoreCase("group")) {
        //                    referenceTypes.add(SCIMDefinitions.ReferenceType.GROUP);
        //                } else if (referenceValue.equalsIgnoreCase("uri")) {
        //                    referenceTypes.add(SCIMDefinitions.ReferenceType.URI);
        //                }
        //            }
        //            return referenceTypes;
        //        }
    }
}
