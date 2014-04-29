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
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSubAttributeSchema;

import java.util.Date;
import java.util.List;

/**
 * Default implementation of AttributeFactory according to SCIM Schema spec V1.
 */
public class DefaultAttributeFactory /*implements AttributeFactory*/ {

    //TODO: remove instance of and do it in polymorphic way

    /**
     * Create the attribute given the attribute schema and the attribute object - may be with
     * attribute value set.
     *
     * @param attributeSchema
     * @param attribute
     * @return
     */
    public static Attribute createAttribute(AttributeSchema attributeSchema,
                                            AbstractAttribute attribute)
            throws CharonException {

        attribute.setReadOnly(attributeSchema.getReadOnly());
        attribute.setRequired(attributeSchema.getRequired());

        //Default attribute factory knows about SCIMAttribute schema
        if (attributeSchema instanceof SCIMAttributeSchema) {
            return createSCIMAttribute((SCIMAttributeSchema) attributeSchema, attribute);
        } else if (attributeSchema instanceof SCIMSubAttributeSchema) {
            return createSCIMSubAttribute((SCIMSubAttributeSchema) attributeSchema, attribute);
        }
        String error = "Unknown attribute schema..";
        //log error
        throw new CharonException(error);
    }

    protected static Attribute createSCIMAttribute(SCIMAttributeSchema attributeSchema,
                                                   AbstractAttribute attribute)
            throws CharonException {
        //things like set the attribute properties according to the schema
        //if multivalued, check if it is simple-multivalued or complex multivalued..
        //if complex-multi-valued, ignore the names of complex attributes. Consider only the names of
        //sub attributes of the complex attribute.

        //do common tasks related to creating an attribute and identify the type of the attribute
        //and then call separate methods on creating each type of attribute
        //see whether a read only attribute is trying to be modified.
        attribute.setSchemaName(attributeSchema.getSchema());

        //set data type of the attribute value, if simple attribute
        if (attribute instanceof SimpleAttribute) {
            return createSimpleAttribute(attributeSchema, (SimpleAttribute) attribute);
        }
        if (attribute instanceof MultiValuedAttribute) {
            return createMultiValuedAttribute(attributeSchema, (MultiValuedAttribute) attribute);
        }
        //validate the created attribute against the schema.
        return attribute;
    }

    protected static Attribute createSCIMSubAttribute(SCIMSubAttributeSchema attributeSchema,
                                                      AbstractAttribute attribute)
            throws CharonException {
        //check things like if it is a sub attribute like "operation" in a multivalued attribute,
        //only allowed value is delete likewise.
        if (attribute instanceof SimpleAttribute) {
            return createSimpleAttribute(attributeSchema, (SimpleAttribute) attribute);
        }
        return attribute;
    }

    /**
     * Once identified that constructing attribute is a simple attribute & related attribute schema is a
     * SCIMAttributeSchema, perform attribute construction operations specific to Simple Attribute.
     *
     * @param attributeSchema
     * @param simpleAttribute
     * @return
     */
    protected static SimpleAttribute createSimpleAttribute(AttributeSchema attributeSchema,
                                                           SimpleAttribute simpleAttribute)
            throws CharonException {
        simpleAttribute.setAttributeURI(attributeSchema.getURI());
        if (simpleAttribute.getValue() != null) {
            if (isAttributeDataTypeValid(simpleAttribute.getValue(), attributeSchema.getType())) {

                simpleAttribute.dataType = attributeSchema.getType();
                return simpleAttribute;
            } else {
                String error = "Violation in attribute shcema. DataType doesn't match that of the value.";
                throw new CharonException(error);
            }
        } else {
            return simpleAttribute;
        }
    }

    /**
     * Once identified that constructing attribute as a multivalued attribute, perform specific operations
     * in creating a multi valued attribute. Such as canonicalization, and validating primary is not
     * repeated etc.
     *
     * @param attributeSchema
     * @param multiValuedAttribute
     * @return
     * @throws CharonException
     */
    protected static MultiValuedAttribute createMultiValuedAttribute(
            SCIMAttributeSchema attributeSchema, MultiValuedAttribute multiValuedAttribute)
            throws CharonException {
        multiValuedAttribute.setAttributeURI(attributeSchema.getURI());
        return validateMultiValuedAttribute(attributeSchema, multiValuedAttribute);
    }

    /**
     * When an attribute is created with value and data type provided, we need to validate whether
     * they are matching.
     *
     * @param attributeValue
     * @param attributeDataType
     * @return
     * @throws CharonException
     */
    protected static boolean isAttributeDataTypeValid(Object attributeValue,
                                                      SCIMSchemaDefinitions.DataType attributeDataType)
            throws CharonException {
        switch (attributeDataType) {
            case STRING:
                return attributeValue instanceof String;
            case BOOLEAN:
                return attributeValue instanceof Boolean;
            case DECIMAL:
                return attributeValue instanceof Double;
            case INTEGER:
                return attributeValue instanceof Integer;
            case DATE_TIME:
                return attributeValue instanceof Date;
            case BINARY:
                return attributeValue instanceof Byte[];

        }
        throw new CharonException(ResponseCodeConstants.MISMATCH_IN_REQUESTED_DATATYPE);
    }

    /**
     * Add a sub attribute built by decoder to a complex attribute. Factory checks whether the any
     * schema violations happen when adding a decoded sub attribute to a complex attribute.
     *
     * @param parentAttribute
     * @param subAttribute
     */
    public static void setSubAttribute(ComplexAttribute parentAttribute,
                                       AbstractAttribute subAttribute) throws CharonException {
        //for the moment only check is whether a read-only attribute is trying to be added.
        /*if (!subAttribute.isReadOnly()) {
            parentAttribute.setSubAttribute(subAttribute);
        }*/
    }

    public static MultiValuedAttribute validateMultiValuedAttribute(
            SCIMAttributeSchema attributeSchema, MultiValuedAttribute multiValuedAttribute)
            throws CharonException {
        /*List<Attribute> attributeValues = multiValuedAttribute.getValuesAsSubAttributes();
        if (attributeValues != null && !attributeValues.isEmpty()) {
            //if value is complex attribute, compare it with other values to canonicalize and
            //validate primary property.
            for (Attribute attribute : attributeValues) {
                if (attribute instanceof SimpleAttribute) {
                    continue;
                }
                for (Attribute otherAttribute : attributeValues) {
                    //do not compare the same attribute
                    if (attribute == otherAttribute) {
                        continue;
                    }
                    ComplexAttribute complexAttribute = (ComplexAttribute) attribute;
                    ComplexAttribute complexOtherAttribute = (ComplexAttribute) otherAttribute;
                    SimpleAttribute type = null;
                    if (complexAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.TYPE) != null) {
                        type = (SimpleAttribute) complexAttribute.getSubAttribute(
                                SCIMConstants.CommonSchemaConstants.TYPE);
                    }
                    SimpleAttribute typeOther = null;
                    if (complexOtherAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.TYPE) != null) {
                        typeOther = (SimpleAttribute) complexAttribute.getSubAttribute(
                                SCIMConstants.CommonSchemaConstants.TYPE);
                    }
                    //we assume value sub attribute is always there in a non null complex value of a
                    // multi-valued attribute.
                    SimpleAttribute value = (SimpleAttribute) complexAttribute.getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    SimpleAttribute valueOther = (SimpleAttribute) complexOtherAttribute.getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    //canonicalize and remove one if two equal found
                    if (type != null && typeOther != null && value != null && valueOther != null) {
                        if ((type.getStringValue().equals((typeOther).getStringValue())) &&
                            (value.getValue().equals(valueOther.getValue()))) {
                            attributeValues.remove(otherAttribute);
                        }
                    }
                    //canonicalize based on case for email, ims,
                    if (attributeSchema.getName().equals(SCIMConstants.UserSchemaConstants.EMAILS) ||
                        attributeSchema.getName().equals(SCIMConstants.UserSchemaConstants.IMS)) {
                        //remove white spaces from the two
                        value.setValue(value.getStringValue().replaceAll("\\s+", ""));
                        valueOther.setValue(valueOther.getStringValue().replaceAll("\\s+", ""));
                        if ((value.getStringValue().toUpperCase()).equals(valueOther.getStringValue().toUpperCase())) {
                            attributeValues.remove(otherAttribute);
                        }
                        value.setValue(value.getStringValue().toLowerCase());
                    }

                    //see if primary is repeated more than once and remove repeated if so,
                    SimpleAttribute primary = null;
                    if (complexAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.PRIMARY) != null) {
                        primary = (SimpleAttribute) complexAttribute.getSubAttribute(
                                SCIMConstants.CommonSchemaConstants.PRIMARY);
                    }
                    SimpleAttribute primaryOther = null;
                    if (complexOtherAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.PRIMARY) != null) {
                        primaryOther = (SimpleAttribute) complexOtherAttribute.getSubAttribute(
                                SCIMConstants.CommonSchemaConstants.PRIMARY);
                    }
                    if (primary != null && primaryOther != null) {
                        if (primary.getBooleanValue() && primaryOther.getBooleanValue()) {
                            //remove primary from one
                            complexOtherAttribute.removeSubAttribute(SCIMConstants.CommonSchemaConstants.PRIMARY);
                        }
                    }
                }
            }
        }*/
        return multiValuedAttribute;
    }
}
