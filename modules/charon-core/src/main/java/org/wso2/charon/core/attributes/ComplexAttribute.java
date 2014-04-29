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

import java.util.HashMap;
import java.util.Map;

import org.wso2.charon.core.exceptions.CharonException;


/**
 * This constructs a complex attribute as defined in SCIM Core schema.
 */
public class ComplexAttribute extends AbstractAttribute {

    /*If it is a complex attribute, has a list of sub attributes.*/
    protected Map<String, Attribute> subAttributes = new HashMap<String, Attribute>();
    
    /*Complex attribute can have a list of attributes */
    protected Map<String, Attribute> attributes = new HashMap<String, Attribute>();

    /**
     * Retrieve the map of sub attributes.
     *
     * @return
     */
    public Map<String, Attribute> getSubAttributes() {
        return subAttributes;
    }

    /**
     * Set the map of sub attributes.
     *
     * @param subAttributes
     */
    public void setSubAttributes(Map<String, Attribute> subAttributes) {
        this.subAttributes = subAttributes;
    }
    
    /**
     * Returns the attributes of the attribute
     * @return
     */
    public Map<String, Attribute> getAttributes() {
    	return attributes;
    }
    
    /**
     * 
     * @param attributes
     */
    public void setAttributes(Map<String, Attribute> attributes) {
    	this.attributes = attributes;
    }

    /**
     * Retrieve one attribute given the attribute name.
     *
     * @param attributeName
     * @return
     */
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        if (subAttributes.containsKey(attributeName)) {
            return subAttributes.get(attributeName);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param attributeName
     * @return
     * @throws CharonException
     */
    public Attribute getAttribute(String attributeName) throws CharonException {
    	if(attributes.containsKey(attributeName)) {
    		return attributes.get(attributeName);
    	} else {
    		return null;
    	}
    }

    /**
     * Applies to multi-valued attributes only.
     * Add complex type value to multi-valued attribute given the properties of the value.
     *
     * @param multiValueAttributeProperties
     */
    @Override
    public void setComplexValue(Map<String, Object> multiValueAttributeProperties)
            throws CharonException {
        throw new CharonException("Error: setComplexValue method is not supported by ComplexAttribute.");
    }


    /**
     * Set a sub attribute on the complex attribute.
     *
     * @param subAttribute
     * @throws CharonException
     */
    public void setSubAttribute(Attribute subAttribute)
            throws CharonException {
        subAttributes.put(subAttribute.getName(), subAttribute);
    }

    /**
     * 
     * @param attribute
     * @throws CharonException
     */
    public void setAttribute(Attribute attribute) throws CharonException {
    	attributes.put(attribute.getName(), attribute);
    }

    /**
     * Remove a sub attribute from the complex attribute given the sub attribute name.
     *
     * @param attributeName
     */
    public void removeSubAttribute(String attributeName) {
        if (subAttributes.containsKey(attributeName)) {
            subAttributes.remove(attributeName);
        }
    }
    
    /**
     * Remove a sub attribute from the complex attribute given the sub attribute name.
     *
     * @param attributeName
     */
    public void removeAttribute(String attributeName) {
        if (attributes.containsKey(attributeName)) {
            attributes.remove(attributeName);
        }
    }

    public boolean isSubAttributeExist(String attributeName) {
        return subAttributes.containsKey(attributeName);
    }
    
    public boolean isAttributeExist(String attributeName) {
        return attributes.containsKey(attributeName);
    }

    /**
     * Get the attribute value. This abstract method should be implemented in respective attribute
     * types.
     *
     * @return Value of the attribute.
     */
/*
    @Override
    public Object getValue() {
        //this is not implemented for complex attributes.
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
*/

    /**
     * Update the attribute with given value.
     *
     * @param value New value to be updated. This abstract method should be implemented in
     *              respective attribute types.
     */
/*
    @Override
    public void updateValue(Object value) {
        //this is not implemented for complex attributes.
    }
*/

    /**
     * Construct complex attribute with attribute name.
     *
     * @param attributeName
     */
    public ComplexAttribute(String attributeName) {
        super(attributeName);
    }

    /**
     * Construct complex attribute with attribute name and attribute schema.
     *
     * @param attributeName
     * @param attributeSchema
     */
    public ComplexAttribute(String attributeName, String attributeSchema) {
        super(attributeName, attributeSchema);
    }

    /**
     * Create attribute with given name, schema name,whether it is readOnly and required.
     *
     * @param attributeName Name of the attribute
     * @param schema        schema in which the attribute is defined
     * @param readOnly      whether attribute is readOnly
     * @param optional      whether attribute is required
     * @param attributeMap  Attributes of the attribute
     */
    public ComplexAttribute(String attributeName, String schema, boolean readOnly,
                            Map<String, Attribute> subAttributeMap,
                            boolean optional, Map<String, Attribute> attributeMap) {
        super(attributeName, schema, readOnly, optional);
        subAttributes = subAttributeMap;
        attributes = attributeMap;
    }

    /**
     * Construct complex attribute with attribute name, attribute schema and sub attribute list.
     *
     * @param attributeName
     * @param attributeSchema
     * @param subAttributeMap
     * @param attributeMap TODO
     */
    public ComplexAttribute(String attributeName, String attributeSchema,
                            Map<String, Attribute> subAttributeMap, Map<String, Attribute> attributeMap) {
        super(attributeName, attributeSchema);
        this.subAttributes = subAttributeMap;
        this.attributes = attributeMap;
    }

    /**
     * It should be possible to create complex attribute without an explicit attribute name
     * in case it is used as a attribute value of a multi valued attribute.
     */
    public ComplexAttribute() {

    }

    /**
     * Validate whether the attribute adheres to the SCIM schema.
     *
     * @param attribute
     * @return
     */
    public boolean validate(Attribute attribute) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
