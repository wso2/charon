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

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents Multi-Valued Attribute as defined in SCIM spec.
 * Values of this type of attribute can take the form of a complex attribute having several
 * properties for an attribute value.
 */
public class MultiValuedAttribute extends AbstractAttribute {

    /*Following are characteristics of an attribute VALUE if it is multi-valued attribute.*/

    /*//Type of the value of an multivalued attribute goes as another attribute.
    protected String multivaluedAttributeType;
    //whether this is the primary value
    protected String multiValuedAttributePrimary;
    //usually used in a PATCH operation of an attribute.
    protected String operation;*/

    //array of string values for a multi-valued attribute
    protected List<String> stringAttributeValues;

    //Multi valued attributes can also have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    /**
     * Create the attribute with the given name. Attribute name can be set only when creating the
     * attribute.
     *
     * @param attributeName Name of the attribute
     */
    public MultiValuedAttribute(String attributeName) {
        super(attributeName);
    }

    public MultiValuedAttribute(String attributeName, List<Attribute> attributeValues) {
        this.attributeName = attributeName;
        this.attributeValues = attributeValues;
    }

    /**
     * Create the attribute with given name and schema name.
     *
     * @param attributeName Name of the attribute
     * @param schema        schema in which the attribute is defined.
     */
    public MultiValuedAttribute(String attributeName, String schema) {
        super(attributeName, schema);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Add complex type value to multi-valued attribute given the properties of the value.
     *
     * @param multiValueAttributeProperties
     */
    public void setComplexValue(Map<String, Object> multiValueAttributeProperties)
            throws CharonException {
        //attribute value as a complex attribute.
        ComplexAttribute attributeValue = new ComplexAttribute();
        for (Map.Entry<String, Object> entry : multiValueAttributeProperties.entrySet()) {
            if ((SCIMConstants.CommonSchemaConstants.TYPE).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.TYPE, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.VALUE).equals(entry.getKey())) {
                SimpleAttribute valueAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, entry.getValue());
                valueAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.VALUE, valueAttribute);
                attributeValue.setSubAttribute(valueAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.DISPLAY).equals(entry.getKey())) {
                SimpleAttribute displayAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, entry.getValue());
                displayAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.DISPLAY, displayAttribute);
                attributeValue.setSubAttribute(displayAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.PRIMARY).equals(entry.getKey())) {
                SimpleAttribute primaryAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.PRIMARY, entry.getValue());
                primaryAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.PRIMARY, primaryAttribute);
                attributeValue.setSubAttribute(primaryAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.OPERATION).equals(entry.getKey())) {
                SimpleAttribute operationAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.OPERATION, entry.getValue());
                operationAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.OPERATION, operationAttribute);
                attributeValue.setSubAttribute(operationAttribute);
            }
            //TODO: if a multi valued attribute contains more sub attributes than the ones mentioned above,
            //need to have a separate method to handle that.
        }
        if (attributeValue.getSubAttributes() != null && !attributeValue.getSubAttributes().isEmpty()) {
            this.attributeValues.add(attributeValue);
        }
    }

    /**
     * To construct and set a value of a multi-valued attribute, as a complex value containing
     * set of sub attributes.
     */
    public void setComplexValueWithSetOfSubAttributes(Map<String, Attribute> subAttributes) {
        ComplexAttribute complexValue = new ComplexAttribute();
        complexValue.setSubAttributes(subAttributes);
        this.attributeValues.add(complexValue);
    }

    /**
     * Get all the values of given type. If null is set to 'type' parameter,
     * return all values.
     *
     * @param type
     */
    public List<String> getAttributeValuesByType(String type) throws CharonException {
        List<String> values = new ArrayList<String>();
        if (attributeValues != null && !attributeValues.isEmpty()) {
            for (Attribute attributeValue : attributeValues) {
                if (type != null) {
                    SimpleAttribute typeAttribute =
                            (SimpleAttribute) attributeValue.getSubAttribute(
                                    SCIMConstants.CommonSchemaConstants.TYPE);
                    if (typeAttribute != null && type.equals(typeAttribute.getStringValue())) {
                        SimpleAttribute valueAttribute =
                                (SimpleAttribute) attributeValue.getSubAttribute(
                                        SCIMConstants.CommonSchemaConstants.VALUE);
                        if (valueAttribute != null) {
                            values.add(valueAttribute.getStringValue());
                        }
                    }
                } else {
                    SimpleAttribute valueAttribute =
                            (SimpleAttribute) attributeValue.getSubAttribute(
                                    SCIMConstants.CommonSchemaConstants.VALUE);
                    if (valueAttribute != null) {
                        values.add(valueAttribute.getStringValue());
                    }
                }
            }
        }
        return values;
    }

    /**
     * Get attribute values of complex type
     *
     * @return
     */
    public List<Attribute> getValuesAsSubAttributes() {
        return attributeValues;
    }

    /**
     * Set the values as set of sub attributes.
     *
     * @param subAttributes
     */
    public void setValuesAsSubAttributes(List<Attribute> subAttributes) {
        this.attributeValues = subAttributes;
    }

    /**
     * There can be multivalued attributes whose value is an array of strings. eg: schemas attribute.
     * Set the attribute values in such a multivalued attribute.
     *
     * @return
     */
    public List<String> getValuesAsStrings() {
        return stringAttributeValues;
    }

    /**
     * There can be multivalued attributes whose value is an array of strings. eg: schemas attribute.
     * Set the attribute values in such a multivalued attribute.
     *
     * @param attributeValues
     */
    public void setValuesAsStrings(List<String> attributeValues) {
        this.stringAttributeValues = attributeValues;
    }

    /**
     * Get the type of the given attribute value.
     *
     * @param value
     * @return
     */
    public String getAttributeTypeOfValue(Object value) throws NotFoundException, CharonException {
        for (Attribute attributeValue : attributeValues) {
            //get the 'value' attribute
            Attribute valueAttribute = ((ComplexAttribute) attributeValue).getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.VALUE);
            //get the 'type' attribute
            Attribute typeAttribute = ((ComplexAttribute) attributeValue).getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.TYPE);
            //get the value of 'value' attribute
            String attrValue = ((SimpleAttribute) (valueAttribute)).getStringValue();
            //compare
            if (value.equals(attrValue)) {
                return ((SimpleAttribute) typeAttribute).getStringValue();
            }
        }
        throw new NotFoundException();
    }

    /**
     * Set one value on the MultiValuedAttribute. Value goes as a complex attribute consisting of
     * sub attributes corresponding to the given characteristics of the value which are passed as
     * parameters to the method.
     *
     * @param type            - type of the value, i.e 'work', 'home'
     * @param primary         - whether this value is the primary value
     * @param display         - human readable format of the attribute - read only
     * @param value           - actual value
     * @param dataTypeOfValue - data type of the value, i.e Sting, boolean
     */
    /*public void setAttributeValue(String type, boolean primary, String display, Object value,
                                  DataType dataTypeOfValue)
            throws CharonException {
        //one value of the multi-valued attribute that goes as a complex attribute.
        ComplexAttribute attributeValue = new ComplexAttribute(null);
        //'type' of the value
        SimpleAttribute typeValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.TYPE, null, type,
                DataType.STRING);
        attributeValue.setSubAttribute(typeValue, null);
        //set primary
        SimpleAttribute primaryValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.PRIMARY, null, primary,
                DataType.BOOLEAN);
        attributeValue.setSubAttribute(primaryValue, null);
        //set display
        SimpleAttribute displayValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.DISPLAY, null, display,
                DataType.STRING, true, true);
        attributeValue.setSubAttribute(displayValue, null);
        //actual value with provided data type
        SimpleAttribute actualValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.VALUE, null, value, dataTypeOfValue);
        attributeValue.setSubAttribute(actualValue, null);
        //canonicalize value before adding to the multi valued attribute
        canonicalizeAttributeValue(attributeValue);
        //add value to the multi valued attribute
        attributeValues.add(actualValue);
    }*/

    /**
     * Set the value as a simple attribute value.
     *
     * @param value
     * @param dataType
     * @throws CharonException
     */
    public void setSimpleAttributeValue(Object value, DataType dataType)
            throws CharonException {
        SimpleAttribute simpleAttributeValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.VALUE, null, value, dataType);
        //canonicalizeAttributeValue(simpleAttributeValue);
        attributeValues.add(simpleAttributeValue);
    }

    /**
     * Get the primary value of multi valued attribute
     *
     * @return
     */
    public Object getPrimaryValue() throws CharonException {
        for (Attribute attributeValue : attributeValues) {
            if (attributeValue instanceof ComplexAttribute) {
                SimpleAttribute primaryValue = (SimpleAttribute) (
                        (ComplexAttribute) attributeValue).getSubAttribute(
                        SCIMConstants.CommonSchemaConstants.PRIMARY);
                boolean primary = primaryValue.getBooleanValue();
                if (primary) {
                    SimpleAttribute value = (SimpleAttribute) (
                            (ComplexAttribute) attributeValue).getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    return value.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Get a value of multi valued attribute by specifying its type.
     *
     * @param type
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public Object getAttributeValueByType(String type) throws CharonException {
        for (Attribute attributeValue : attributeValues) {
            if (attributeValue instanceof ComplexAttribute) {
                SimpleAttribute typeValue = (SimpleAttribute) (
                        (ComplexAttribute) attributeValue).getSubAttribute(
                        SCIMConstants.CommonSchemaConstants.TYPE);
                String typeAttrVal = typeValue.getStringValue();
                if (type.equals(typeAttrVal)) {
                    SimpleAttribute value = (SimpleAttribute) (
                            (ComplexAttribute) attributeValue).getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    return value.getValue();
                }
            }
        }
        return null;
    }

    public void canonicalizeAttributeValue(Attribute attribute) {
        //iterate through attribute values.
        //compare type and value - in one case, with the given attribute. If same, print error and
        //do not add the given attribute to the values.
        //This is done in DefaultAttributeFactory
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

    /**
     * Applies to complex attributes only. Retrieve the sub attribute given the sub attribute name.
     *
     * @param attributeName
     * @return
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    @Override
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("Error: getSubAttribute method not supported by MultiValuedAttribute.");
    }

    public void removeAttributeValue(Attribute attributeValue) {
        attributeValues.remove(attributeValue);
    }

    /**
     * To get the list of complex values of a sub multivalued attribute, with sub attributes of each value
     * with each attribute's(simple attribute) name and value as a map
     *
     * @return
     */
    public List<Map<String, Object>> getComplexValues() {
        List complexValues = new ArrayList();
        for (Attribute attributeValue : attributeValues) {
            if (attributeValue instanceof ComplexAttribute) {
                Map<String, Object> subAttributeValues = new HashMap<String, Object>();
                Map<String, Attribute> subAttributes =
                        ((ComplexAttribute) attributeValue).getSubAttributes();
                for (Map.Entry<String, Attribute> attributeEntry : subAttributes.entrySet()) {
                    subAttributeValues.put(attributeEntry.getKey(),
                                           ((SimpleAttribute) attributeEntry.getValue()).getValue());
                }
                complexValues.add(subAttributeValues);
            }
        }
        if (!complexValues.isEmpty()) {
            return complexValues;
        } else {
            return null;
        }
    }
}
