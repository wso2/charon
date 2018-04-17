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
package org.wso2.charon3.core.utils;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class acts as an utility class for attributes.
 */
public class AttributeUtil {

    /*
     * Convert the raw string to SCIM defined data type accordingly
     *
     * @param attributeValue
     * @param dataType
     * @return Object
     */
    public static Object getAttributeValueFromString(Object attributeValue,
                                                     SCIMDefinitions.DataType dataType)
            throws CharonException, BadRequestException {
        if (attributeValue == null) {
            return attributeValue;
        }
        String attributeStringValue = null;
        if (!(attributeValue instanceof Boolean)) {
            if (attributeValue instanceof Integer) {
                attributeStringValue = String.valueOf(attributeValue);
            } else if (attributeValue instanceof Double) {
                attributeStringValue = String.valueOf(attributeValue);
            } else {
                attributeStringValue = (String) attributeValue;
            }
        }

        switch (dataType) {
            case STRING:
                return attributeStringValue.trim();
            case BOOLEAN:
                return parseBoolean(attributeValue);
            case DECIMAL:
                return Double.parseDouble(attributeStringValue);
            case INTEGER:
                return Integer.parseInt(attributeStringValue);
            case DATE_TIME:
                return parseDateTime(attributeStringValue);
            case BINARY:
                return Byte.valueOf(attributeStringValue);
            case REFERENCE:
                return parseReference(attributeStringValue);
            case COMPLEX:
                return parseComplex(attributeStringValue);
        }
        return null;
    }

    /*
     * return the string value of the attribute value
     *
     * @param attributeValue
     * @param dataType
     * @return
     * @throws CharonException
     */
    public static String getStringValueOfAttribute(Object attributeValue,
                                                   SCIMDefinitions.DataType dataType)
            throws CharonException {
        switch (dataType) {
            case STRING:
                return String.valueOf(attributeValue);
            case BOOLEAN:
                return String.valueOf(attributeValue);
            case DECIMAL:
                return String.valueOf(attributeValue);
            case INTEGER:
                return String.valueOf(attributeValue);
            case DATE_TIME:
                return formatDateTime((Date) attributeValue);
            case BINARY:
                return String.valueOf(attributeValue);
            case REFERENCE:
                return String.valueOf(attributeValue);
            case COMPLEX:
                return String.valueOf(attributeValue);
        }
        throw new CharonException("Error in converting attribute value of type: " + dataType + " to string.");
    }

    /*
     * SCIM spec requires date time to be in yyyy-MM-dd'T'HH:mm:ss
     *
     * @param dateTimeString
     */
    public static Date parseDateTime(String dateTimeString) throws CharonException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.DATE_TIME_FORMAT);
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.DATE_TIME_FORMAT2);
                return sdf.parse(dateTimeString);
            } catch (ParseException e1) {
                throw new CharonException("Error in parsing date time. " +
                        "Date time should adhere to the format: " + SCIMConstants.DATE_TIME_FORMAT, e1);
            }
        }
    }

    public static String parseReference(String referenceString) throws CharonException {
        //TODO: Need a better way for doing this. Think of the way to handle reference types
        return referenceString;
    }

    //this method is for the consistency purpose only
    public static String parseComplex(String complexString) {
        return complexString;
    }

    /*
     * SCIM spec requires date time to be adhered to XML Schema Datatypes Specification
     *
     * @param date
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.DATE_TIME_FORMAT);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /*
     * Converts the value to boolean or throw an exception
     *
     * @param booleanValue
     */
    public static Boolean parseBoolean(Object booleanValue) throws BadRequestException {
        try {
            return ((Boolean) booleanValue);
        } catch (Exception e) {
            return Boolean.parseBoolean((String) booleanValue);
        }
    }

    /*
     * Will iterate through <code>{@code SCIMAttributeSchema}</code> objects
     *
     * @param attributeName
     * @param schema
     * @return
     */
    public static String getAttributeURI(String attributeName, SCIMResourceTypeSchema schema) throws
            BadRequestException {

        return getAttributeURI(attributeName, schema, false);
    }

    /*
     * Will iterate through <code>{@code SCIMAttributeSchema}</code> objects
     *
     * @param attributeName
     * @param schema
     * @param isFilter
     * @return
     */
    public static String getAttributeURI(String attributeName, SCIMResourceTypeSchema schema, boolean isFilter) throws
            BadRequestException {

        Iterator<AttributeSchema> attributeSchemas = schema.getAttributesList().iterator();
        while (attributeSchemas.hasNext()) {
            AttributeSchema attributeSchema = attributeSchemas.next();

            if ((isFilter && attributeSchema.getName().equalsIgnoreCase(attributeName)) ||
                    attributeSchema.getName().equals(attributeName) || attributeSchema.getURI().equals(attributeName)) {
                return attributeSchema.getURI();
            }
            // check in sub attributes
            String subAttributeURI = checkSCIMSubAttributeURIs(attributeSchema.getSubAttributeSchemas(),
                    attributeSchema, attributeName, isFilter);
            if (subAttributeURI != null) {
                return subAttributeURI;
            }

            if (attributeName.contains(attributeSchema.getName()) && attributeSchema.getMultiValued()) {

                String subAttribute = null;
                if (attributeName.contains(".")) {
                    String[] splittedString = attributeName.split("\\.", 2);
                    subAttribute = splittedString[1];
                }
                subAttributeURI = attributeSchema.getURI();
                if (subAttribute != null) {
                    subAttributeURI = subAttributeURI + "." + subAttribute;
                    return subAttributeURI;
                }
            }
        }
        String error = "Not a valid attribute name/uri";
        throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
    }

    /*
     * Will iterate through <code>{@code SCIMSubAttributeSchema}</code> objects
     *
     * @param subAttributes
     * @param attributeSchema
     * @param attributeName
     * @param isFilter
     * @return
     */
    private static String checkSCIMSubAttributeURIs(List<SCIMAttributeSchema> subAttributes,
                                                    AttributeSchema attributeSchema, String attributeName,
                                                    boolean isFilter) {
        if (subAttributes != null) {
            Iterator<SCIMAttributeSchema> subsIterator = subAttributes.iterator();

            StringBuilder sb = new StringBuilder();

            while (subsIterator.hasNext()) {
                SCIMAttributeSchema subAttributeSchema = subsIterator.next();

                sb.replace(0, sb.length(), "");
                sb.append(attributeSchema.getName()).append(".").append(subAttributeSchema.getName());

                if ((isFilter && sb.toString().equalsIgnoreCase(attributeName)) ||
                        sb.toString().equals(attributeName) ||
                        subAttributeSchema.getURI().equals(attributeName)) {
                    return subAttributeSchema.getURI();
                }

                if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    List<SCIMAttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();
                    if (subSubAttributeSchemas != null) {
                        Iterator<SCIMAttributeSchema> subSubsIterator = subSubAttributeSchemas.iterator();

                        int attrLength = sb.length();

                        while (subSubsIterator.hasNext()) {
                            SCIMAttributeSchema subSubAttributeSchema = subSubsIterator.next();

                            sb.replace(attrLength, sb.length(), "");
                            sb.append(".").append(subSubAttributeSchema.getName());

                            if ((isFilter && sb.toString().equalsIgnoreCase(attributeName)) ||
                                    sb.toString().equals(attributeName) ||
                                    subAttributeSchema.getURI().equals(attributeName)) {
                                return subSubAttributeSchema.getURI();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
