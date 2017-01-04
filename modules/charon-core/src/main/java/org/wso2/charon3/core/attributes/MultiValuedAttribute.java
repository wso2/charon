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
package org.wso2.charon3.core.attributes;

import org.wso2.charon3.core.exceptions.CharonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is a blueprint of MultiValuedAttribute defined in SCIM Core Schema Spec.
 */
public class MultiValuedAttribute extends AbstractAttribute {

    private static final long serialVersionUID = 6106269076155338045L;
    //Multi valued attributes can have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    //Multi valued attributes can have VALUES as an array of primitive values.
    protected List<Object> attributePrimitiveValues = new ArrayList<Object>();

    public MultiValuedAttribute(String attributeName, List<Attribute> attributeValues) {
        this.name = attributeName;
        this.attributeValues = attributeValues;
    }
    public  MultiValuedAttribute(){}

    public MultiValuedAttribute(String attributeName) {
        this.name = attributeName;
    }

    public List<Attribute> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<Attribute> attributeValues) {
        this.attributeValues = attributeValues;
    }

    /*
     * not supported for multivalued attribute
     * @param attributeName
     * @return
     * @throws CharonException
     */
    @Override
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("getSubAttribute method not supported by MultiValuedAttribute.");
    }

    /*
     * clear all sub attribute values
     * @throws CharonException
     */
    @Override
    public void deleteSubAttributes() throws CharonException {
        //here we delete the complex type sub attributes which act as sub values
        attributeValues.clear();;
    }

     /*
     * clear all primitive values
     * @throws CharonException
     */

    public void deletePrimitiveValues() throws CharonException {
        //here we delete primitive values
        attributePrimitiveValues.clear();;
    }

    /**
     * To construct and set a value of a multi-valued attribute, as a complex value containing
     * set of sub attributes.
     */
    public void setComplexValueWithSetOfSubAttributes(Map<String, Attribute> subAttributes) {
        ComplexAttribute complexValue = new ComplexAttribute();
        complexValue.setSubAttributesList(subAttributes);
        this.attributeValues.add(complexValue);
    }

    /*
     * get the attribute primitive values
     * @return
     */
    public List<Object> getAttributePrimitiveValues() {
        return attributePrimitiveValues;
    }

    /*
     * set attribute primitive values
     * @param attributePrimitiveValues
     */
    public void setAttributePrimitiveValues(List<Object> attributePrimitiveValues) {
        this.attributePrimitiveValues = attributePrimitiveValues;
    }

    /*
     * set sub attribute value
     * @param attributeValue
     */
    public void setAttributeValue(Attribute attributeValue) {
        attributeValues.add(attributeValue);
    }

    /*
     * add an attribute primitive value to the list.
     * @param obj
     */
    public void setAttributePrimitiveValue(Object obj) {
        attributePrimitiveValues.add(obj);
    }

}
