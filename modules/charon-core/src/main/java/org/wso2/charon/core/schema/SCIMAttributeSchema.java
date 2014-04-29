/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.schema;

import java.util.Arrays;
import java.util.List;

/**
 * This defines the attributes schema as in SCIM Spec.
 * Unless otherwise specified are optional, modifiable by Consumers, and of type String.
 */
public class SCIMAttributeSchema implements AttributeSchema {
    //Absolute URI of the attribute
    private String attributeURI;
    //name of the attribute
    private String name;
    //data type of the attribute
    private SCIMSchemaDefinitions.DataType type;
    //Boolean value indicating the attribute's plurality.
    private Boolean multiValued;
    //String value specifying the child XML element name; e.g., the 'emails' attribute value is 'email
    private String multiValuedAttributeChildName;
    //The attribute's human readable description
    private String description;
    //The attribute's associated schema; e.g., urn:scim:schemas:core:1.0.
    private String schema;
    //A Boolean value that specifies if the attribute is mutable.
    private Boolean readOnly;
    //A Boolean value that specifies if the attribute is required
    private Boolean required;
    //A Boolean value that specifies if the String attribute is case sensitive
    private Boolean caseExact;
    //A list specifying the contained attributes. OPTIONAL.
    List<SCIMSubAttributeSchema> subAttributes;
    // A list specifying the contained attributes
    List<SCIMAttributeSchema> attributes;

    public static SCIMAttributeSchema createSCIMAttributeSchema(String uri,
                                                                String name,
                                                                SCIMSchemaDefinitions.DataType type,
                                                                Boolean multiValued,
                                                                String multiValuedAttributeChildName,
                                                                String description,
                                                                String schema,
                                                                Boolean readOnly, Boolean required,
                                                                Boolean caseExact,
                                                                SCIMSubAttributeSchema... subAttributes) {

        /*if this is multivalued attribute, add the common sub attributes of a multivalued attribute*/
        //TODO:need to add the canonical values of type sub attribute.
        if (multiValued) {
            //check if multi valued attribute already contains sub attributes.
            if (subAttributes != null) {
                SCIMSubAttributeSchema[] combinedSubAttributeSchema =
                        new SCIMSubAttributeSchema[subAttributes.length + 5];
                SCIMSubAttributeSchema[] multiValuedProperties = {SCIMSchemaDefinitions.TYPE,
                                                                  SCIMSchemaDefinitions.PRIMARY,
                                                                  SCIMSchemaDefinitions.DISPLAY,
                                                                  SCIMSchemaDefinitions.VALUE,
                                                                  SCIMSchemaDefinitions.OPERATION};
                //combine common sub attributes and specific sub attributes passed in,
                System.arraycopy(subAttributes, 0, combinedSubAttributeSchema, 0, subAttributes.length);
                System.arraycopy(multiValuedProperties, 0, combinedSubAttributeSchema, subAttributes.length, 5);
                return new SCIMAttributeSchema(uri, name, type, multiValued, multiValuedAttributeChildName, description,
                                               schema, readOnly, required, caseExact, combinedSubAttributeSchema);
            } else {
                return new SCIMAttributeSchema(uri, name, type, multiValued, multiValuedAttributeChildName, description,
                                               schema, readOnly, required, caseExact, SCIMSchemaDefinitions.TYPE,
                                               SCIMSchemaDefinitions.PRIMARY, SCIMSchemaDefinitions.DISPLAY,
                                               SCIMSchemaDefinitions.VALUE, SCIMSchemaDefinitions.OPERATION);
            }
        } else {

            return new SCIMAttributeSchema(uri, name, type, multiValued, multiValuedAttributeChildName, description,
                                           schema, readOnly, required, caseExact, subAttributes);
        }

    }
    
    public static SCIMAttributeSchema createSCIMAttributeSchema(String uri,
                                                                String name,
                                                                SCIMSchemaDefinitions.DataType type,
                                                                String description,
                                                                String schema,
                                                                Boolean readOnly, Boolean required,
                                                                Boolean caseExact,
                                                                SCIMAttributeSchema... attributes) {
    	return new SCIMAttributeSchema(uri, name, type, description, schema, readOnly, required, caseExact, attributes);
    }

    private SCIMAttributeSchema(String uri, String name, SCIMSchemaDefinitions.DataType type,
                                Boolean multiValued,
                                String multiValuedAttributeChildName, String description,
                                String schema,
                                Boolean readOnly, Boolean required, Boolean caseExact,
                                SCIMSubAttributeSchema... subAttributes) {
        this.attributeURI = uri;
        this.name = name;
        this.type = type;
        this.multiValued = multiValued;
        this.multiValuedAttributeChildName = multiValuedAttributeChildName;
        this.description = description;
        this.schema = schema;
        this.readOnly = readOnly;
        this.required = required;
        this.caseExact = caseExact;
        if (subAttributes != null) {

            this.subAttributes = Arrays.asList(subAttributes);
        }
    }
    
	private SCIMAttributeSchema(String uri, String name, SCIMSchemaDefinitions.DataType type,
	                            String description, String schema, Boolean readOnly, Boolean required,
	                            Boolean caseExact, SCIMAttributeSchema... attributes) {
		this.attributeURI = uri;
		this.name = name;
		this.type = type;
		this.multiValued = false;
		this.multiValuedAttributeChildName = null;
		this.description = description;
		this.schema = schema;
		this.readOnly = readOnly;
		this.required = required;
		this.caseExact = caseExact;
		if (attributes != null) {
			this.attributes = Arrays.asList(attributes);
		}
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SCIMSchemaDefinitions.DataType getType() {
        return type;
    }

    public void setType(SCIMSchemaDefinitions.DataType type) {
        this.type = type;
    }

    public Boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(Boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getMultiValuedAttributeChildName() {
        return multiValuedAttributeChildName;
    }

    public void setMultiValuedAttributeChildName(String multiValuedAttributeChildName) {
        this.multiValuedAttributeChildName = multiValuedAttributeChildName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getURI() {
        return attributeURI;
    }

    public void setURI(String uri) {
        attributeURI = uri;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
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

    public List<SCIMSubAttributeSchema> getSubAttributes() {
        return subAttributes;
    }

    public void setSubAttributes(List<SCIMSubAttributeSchema> subAttributes) {
        this.subAttributes = subAttributes;
    }
    
    public List<SCIMAttributeSchema> getAttributes() {
    	return attributes;
    }
    
    public void setAttributes(List<SCIMAttributeSchema> attributes) {
    	this.attributes = attributes;
    }

}
