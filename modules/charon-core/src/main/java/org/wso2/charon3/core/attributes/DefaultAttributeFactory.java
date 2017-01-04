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

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.util.Date;

/**
 * Default implementation of AttributeFactory according to SCIM Schema spec.
 */
public class DefaultAttributeFactory {

    /*
     * Returns the defined type of attribute with the user defined value
     * included and necessary attribute characteristics set
     * @param attributeSchema - Attribute schema
     * @param attribute - attribute
     * @return Attribute
     */
    public static Attribute createAttribute(AttributeSchema attributeSchema,
                                            AbstractAttribute attribute) throws CharonException, BadRequestException {

        attribute.setMutability(attributeSchema.getMutability());
        attribute.setRequired(attributeSchema.getRequired());
        attribute.setReturned(attributeSchema.getReturned());
        attribute.setCaseExact(attributeSchema.getCaseExact());
        attribute.setMultiValued(attributeSchema.getMultiValued());
        attribute.setDescription(attributeSchema.getDescription());
        attribute.setUniqueness(attributeSchema.getUniqueness());
        attribute.setURI(attributeSchema.getURI());

        //Default attribute factory knows about SCIMAttribute schema
        try {
            //set data type of the attribute value, if simple attribute
            if (attribute instanceof SimpleAttribute) {
                return createSimpleAttribute(attributeSchema, (SimpleAttribute) attribute);
            } else {
                attribute.setType(attributeSchema.getType());
            }
            return attribute;
        } catch (CharonException e) {
            String error = "Unknown attribute schema.";
            throw new CharonException(error);
        } catch (BadRequestException e) {
            String error = "Violation in attribute schema. DataType doesn't match that of the value.";
            throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
        }
    }

    /**
     * Once identified that constructing attribute is a simple attribute & related attribute schema is a
     * SCIMAttributeSchema, perform attribute construction operations specific to Simple Attribute.
     *
     * @param attributeSchema
     * @param simpleAttribute
     * @return SimpleAttribute
     * @throws CharonException
     * @throws BadRequestException
     */
    protected static SimpleAttribute createSimpleAttribute
                    (AttributeSchema attributeSchema, SimpleAttribute simpleAttribute)
            throws CharonException, BadRequestException {
        if (simpleAttribute.getValue() != null) {
            if (isAttributeDataTypeValid(simpleAttribute.getValue(), attributeSchema.getType())) {
                simpleAttribute.setType(attributeSchema.getType());
                return simpleAttribute;
            } else {
                throw new BadRequestException(ResponseCodeConstants.INVALID_VALUE);
            }
        }
        return simpleAttribute;
    }

    /**
     * When an attribute is created with value and data type provided,
     * we need to validate whether they are matching.
     *
     * @param attributeValue
     * @param attributeDataType
     * @return boolean
     * @throws BadRequestException
     */
    protected static boolean isAttributeDataTypeValid(Object attributeValue,
                                                      SCIMDefinitions.DataType attributeDataType)
                                                throws BadRequestException {
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
            case REFERENCE:
                return attributeValue instanceof String;
            case COMPLEX:
                return attributeValue instanceof String;

        }
        throw new BadRequestException(ResponseCodeConstants.INVALID_VALUE);
    }

}
