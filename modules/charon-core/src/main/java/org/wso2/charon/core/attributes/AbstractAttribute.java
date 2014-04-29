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
package org.wso2.charon.core.attributes;

import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

/**
 * This class abstracts out the common characteristics of different types of attributes defined in
 * SCIM Core Schema Spec.
 * Unless otherwise specified, attributes are required, modifiable by Consumers, and of type String.
 * TODO:Can achieve more decoupling if we define an interface for attribute schema. And pass the
 * relevant impl of it into the constructor when creating the attribute.
 */
public abstract class AbstractAttribute implements Attribute {

    /*whether attribute is readonly to consumers*/
    protected boolean readOnly = false;
    /*The schema where attribute is defined.*/
    protected String schema;
    /*Name of the attribute*/
    protected String attributeName;
    /*Whether attribute is required or mandatory*/
    boolean required = true;
    /*attribute uri according to the attribute notation defined in core-schema spec*/
    String attributeURI;
    
    public String getAttributeURI() {
        return attributeURI;
    }

    public void setAttributeURI(String attributeURI) {
        this.attributeURI = attributeURI;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Create the attribute with the given name. Attribute name can be set only when creating the
     * attribute.
     *
     * @param attributeName Name of the attribute
     */
    public AbstractAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Create the attribute with given name and schema name.
     * @param attributeName Name of the attribute
     * @param schema schema in which the attribute is defined.
     */
    public AbstractAttribute(String attributeName, String schema){
        this.attributeName = attributeName;
        this.schema = schema;
    }

    /**
     * Create attribute with given name, schema name,whether it is readOnly and required.
     * @param attributeName Name of the attribute
     * @param schema schema in which the attribute is defined
     * @param readOnly whether attribute is readOnly
     * @param optional whether attribute is required
     */
    public AbstractAttribute(String attributeName, String schema, boolean readOnly,
                             boolean optional){
        this.attributeName = attributeName;
        this.schema = schema;
        this.readOnly = readOnly;
        this.required = optional;
    }

    /**
     * Default Constructor.
     */
    public AbstractAttribute() {
    }

    /**
     * Get the name of the attribute.
     *
     * @return Name of the attribute.
     */
    public String getName() {

        return attributeName;
    }

    /**
     * Get the attribute value. This abstract method should be implemented in respective attribute
     * types.
     *
     * @return Value of the attribute.
     */
    //public abstract Object getValue();

    /**
     * Update the attribute with given value.
     *
     * @param value New value to be updated. This abstract method should be implemented in
     * respective attribute types.
     */
    //public abstract void updateValue(Object value);

    /**
     * Get the schema where the attribute is defined.
     */
    public String getSchemaName() {
        return schema;
    }
    
    public void setSchemaName(String schema) {
        this.schema = schema;
    }

    public void setDataType(SCIMSchemaDefinitions.DataType dataType){
        //Applicable only for SimpleAttribute.
    }

    /**
     * Remove a sub attribute from the complex attribute given the sub attribute name.
     * No implementation for this in Abstract attribute. This is implemented in complex attribute.
     *
     * @param attributeName
     */
    public void removeSubAttribute(String attributeName) {

    }
}
