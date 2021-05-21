/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.charon3.core.config;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.util.ArrayList;
import java.util.Map;

/**
 * Abstract class for extension builds which builds SCIM schema attributes.
 */
public abstract class ExtensionBuilder {

    /**
     * Get rootURI of the schema.
     *
     * @return RootURI.
     */
    public abstract String getURI();

    /**
     * Returns subattribute URI.
     *
     * @param config           ExtensionAttributeSchemaConfig.
     * @param subAttributeName SubAttributeName.
     * @return Subattribute URI.
     */
    protected String getSubAttributeURI(ExtensionAttributeSchemaConfig config, String subAttributeName) {

        if (isRootConfig(config)) {
            return config.getURI() + ":" + subAttributeName;
        } else {
            return config.getURI() + "." + subAttributeName;
        }
    }

    /**
     * Returns true if it is the rootconfig. Else returns false.
     *
     * @param config ExtensionAttributeSchemaConfig
     * @return Returns true if it is the rootconfig. Else returns false.
     */
    protected boolean isRootConfig(ExtensionAttributeSchemaConfig config) {

        return getURI().equals(config.getURI());
    }

    /**
     * Builds complex attributes.
     *
     * @param config           ExtensionAttributeSchemaConfig that needs to be built.
     * @param attributeSchemas Map of all attributeSchemas.
     * @param extensionConfig  Map of all ExtensionAttributeSchemaConfig.
     * @throws InternalErrorException
     */
    protected void buildComplexAttributeSchema(ExtensionAttributeSchemaConfig config,
                                               Map<String, AttributeSchema> attributeSchemas, Map<String,
            ExtensionAttributeSchemaConfig> extensionConfig) throws InternalErrorException {

        if (!attributeSchemas.containsKey(config.getURI())) {
            String[] subAttributes = config.getSubAttributes();
            for (String subAttribute : subAttributes) {
                ExtensionAttributeSchemaConfig subAttribConfig = extensionConfig.get(getSubAttributeURI(config,
                        subAttribute));
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
                        buildSimpleAttributeSchema(subAttribConfig, attributeSchemas);
                    }
                } else {
                    if (!(subAttribConfig.hasChildren())) {
                        String error = "A attribute of complex type should have sub attributes";
                        throw new InternalErrorException(error);
                    } else {
                        // Need to build child schemas first.
                        buildComplexAttributeSchema(subAttribConfig, attributeSchemas, extensionConfig);
                    }
                }
            }
            // Now all sub attributes must be already built.
            buildComplexSchema(config, attributeSchemas);
        }
    }

    /**
     * Has the logic to iterate through child attributes.
     *
     * @param config           ExtensionAttributeSchemaConfig.
     * @param attributeSchemas Map of all attributeSchemas.
     */
    protected void buildComplexSchema(ExtensionAttributeSchemaConfig config,
                                      Map<String, AttributeSchema> attributeSchemas) {

        String[] subAttributeNames = config.getSubAttributes();
        ArrayList<AttributeSchema> subAttributes = new ArrayList<AttributeSchema>();
        for (String subAttributeName : subAttributeNames) {
            subAttributes.add(attributeSchemas.get(getSubAttributeURI(config, subAttributeName)));
        }
        AttributeSchema complexAttribute = createSCIMAttributeSchema(config, subAttributes);
        attributeSchemas.put(config.getURI(), complexAttribute);
    }

    /**
     * Builds simple attribute schema.
     *
     * @param config           ExtensionAttributeSchemaConfig
     * @param attributeSchemas Map of all attributeschemas.
     */
    protected void buildSimpleAttributeSchema(ExtensionAttributeSchemaConfig config,
                                              Map<String, AttributeSchema> attributeSchemas) {

        ArrayList<AttributeSchema> subAttributeList = new ArrayList<AttributeSchema>();
        if (!attributeSchemas.containsKey(config.getURI())) {
            AttributeSchema attributeSchema = createSCIMAttributeSchema(config, subAttributeList);
            attributeSchemas.put(config.getURI(), attributeSchema);
        }
    }

    /**
     * Create SCIM Attribute Schema.
     *
     * @param attribute        ExtensionAttributeSchemaConfig attribute.
     * @param subAttributeList List of subattributes.
     * @return SCIMAttributeSchema
     */
    public SCIMAttributeSchema createSCIMAttributeSchema(ExtensionAttributeSchemaConfig attribute,
                                                         ArrayList<AttributeSchema> subAttributeList) {

        return SCIMAttributeSchema.createSCIMAttributeSchema(attribute.getURI(), attribute.getName(),
                attribute.getType(), attribute.getMultiValued(), attribute.getDescription(), attribute.getRequired(),
                attribute.getCaseExact(), attribute.getMutability(), attribute.getReturned(),
                attribute.getUniqueness(), attribute.canonicalValues, attribute.referenceTypes, subAttributeList);
    }

    /**
     * Custom Attribute config class.
     */
    protected static class ExtensionAttributeSchemaConfig {

        // Unique identifier for the attribute.
        protected String uri;
        // Name of the attribute
        protected String name;
        // Data type of the attribute.
        protected SCIMDefinitions.DataType type;
        // Boolean value indicating the attribute's plurality.
        protected Boolean multiValued;
        // The attribute's human readable description.
        protected String description;
        // A Boolean value that specifies if the attribute is required.
        protected Boolean required;
        // A Boolean value that specifies if the String attribute is case sensitive.
        protected Boolean caseExact;
        // A SCIM defined value that specifies if the attribute's mutability.
        protected SCIMDefinitions.Mutability mutability;
        // A SCIM defined value that specifies when the attribute's value need to be returned.
        protected SCIMDefinitions.Returned returned;
        // A SCIM defined value that specifies the uniqueness level of an attribute.
        protected SCIMDefinitions.Uniqueness uniqueness;
        // A list specifying the contained attributes. OPTIONAL.
        protected String[] subAttributes;
        // A collection of suggested canonical values that MAY be used -OPTIONAL
        protected ArrayList<String> canonicalValues;
        // A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced
        // only applicable for attributes that are of type "reference".
        protected ArrayList<SCIMDefinitions.ReferenceType> referenceTypes;

        public String[] getSubAttributes() {

            return subAttributes.clone();
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

        public ExtensionAttributeSchemaConfig(Map<String, String> attributeConfig) {

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
                canonicalValues =
                        setCanonicalValues(attributeConfigJSON.getJSONArray(SCIMConfigConstants.CANONICAL_VALUES));
                referenceTypes =
                        setReferenceTypes(attributeConfigJSON.getJSONArray(SCIMConfigConstants.REFERENCE_TYPES));
            } catch (JSONException e) {
                throw new CharonException("Error while parsing extension configuration", e);
            }
        }

        /**
         * This builds the relevant data types according to what has configured and returns it.
         *
         * @param input Input value.
         * @return SCIM defined data type.
         */
        protected SCIMDefinitions.DataType getDefinedDataType(String input) {

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

        /**
         * This builds the relevant mutability according to what has configured.
         *
         * @param input Input value.
         * @return SCIM defined mutability value.
         */
        protected SCIMDefinitions.Mutability getDefinedMutability(String input) {

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

        /**
         * This builds the relevant returned type according to what has configured.
         *
         * @param input Input value.
         * @return SCIM defined Return value
         */
        protected SCIMDefinitions.Returned getDefinedReturned(String input) {

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

        /**
         * This builds the relevant uniqueness based on the input.
         *
         * @param input Input value
         * @return SCIM defined Uniqueness value.
         */
        protected SCIMDefinitions.Uniqueness getDefinedUniqueness(String input) {

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

        /**
         * This builds the relevant canonical values based on the input.
         *
         * @param input JSON array input.
         * @return ArrayList of canonical values.
         * @throws JSONException
         */
        protected ArrayList<String> setCanonicalValues(JSONArray input) throws JSONException {

            ArrayList<String> canonicalValues = new ArrayList<String>();
            JSONArray canonicalValuesList = input;
            for (int index = 0; index < canonicalValuesList.length(); ++index) {
                canonicalValues.add((String) canonicalValuesList.get(index));
            }
            return canonicalValues;
        }

        /**
         * This builds the relevant reference types according to the input.
         *
         * @param input JSONArray of input.
         * @return Array:ist of SCIM defined reference types.
         * @throws JSONException
         */
        protected ArrayList<SCIMDefinitions.ReferenceType> setReferenceTypes(JSONArray input) throws JSONException {

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
