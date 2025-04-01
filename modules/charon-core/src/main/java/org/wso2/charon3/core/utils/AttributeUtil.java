/*
 * Copyright (c) 2016-2025,  WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon3.core.utils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

/**
 * This class acts as an utility class for attributes.
 */
public class AttributeUtil {

    private static final String ATTRIBUTE_EXTENSION_SCHEMA_PREFIX = "urn:ietf:params:scim:schemas:extension";

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
        if (attributeValue instanceof Boolean) {
            attributeStringValue = String.valueOf(attributeValue);
        } else if (attributeValue instanceof Integer) {
            attributeStringValue = String.valueOf(attributeValue);
        } else if (attributeValue instanceof Double) {
            attributeStringValue = String.valueOf(attributeValue);
        } else if (JSONObject.NULL.equals(attributeValue)) {
            attributeStringValue = "";
        } else {
            attributeStringValue = (String) attributeValue;
        }

        switch (dataType) {
            case STRING:
                return attributeStringValue.trim();
            case BOOLEAN:
                return parseBoolean(attributeValue);
            case DECIMAL:
                return Double.parseDouble(attributeStringValue);
            case INTEGER:
                // Return value as it is since the value is an empty string.
                if (StringUtils.isEmpty(attributeStringValue)) {
                    return attributeValue;
                }
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
                return formatDateTime((Instant) attributeValue);
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
    public static Instant parseDateTime(String dateTimeString) throws CharonException {

        Instant localDateTime = null;
        if (StringUtils.isNotEmpty(dateTimeString)) {
            try {
                localDateTime = LocalDateTime.parse(dateTimeString).toInstant(ZoneOffset.UTC);
            } catch (DateTimeException e) {
                try {
                    return OffsetDateTime.parse(dateTimeString).toInstant();
                } catch (DateTimeException dte) {
                    throw new CharonException("Error in parsing date time. " +
                            "Date time should adhere to ISO_OFFSET_DATE_TIME format", e);
                }
            }
        }
        return localDateTime;
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
    public static String formatDateTime(Instant instant) {
        return instant.toString();
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
     * @return
     */
    public static String getAttributeURI(String attributeName, SCIMResourceTypeSchema schema) throws
            BadRequestException {

        boolean isSCIM2ExtensionSchemaAttribute = false;
        /* Validates whether the attribute is from scim2 extension schema by checking the
        ATTRIBUTE_EXTENSION_SCHEMA_PREFIX. */
        if (StringUtils.startsWith(attributeName, ATTRIBUTE_EXTENSION_SCHEMA_PREFIX)) {
            isSCIM2ExtensionSchemaAttribute = true;
        }

        Iterator<AttributeSchema> attributeSchemas = schema.getAttributesList().iterator();
        while (attributeSchemas.hasNext()) {
            AttributeSchema attributeSchema = attributeSchemas.next();

            if (attributeSchema.getName().equalsIgnoreCase(attributeName) || attributeSchema.getURI().equals
                    (attributeName)) {
                return attributeSchema.getURI();
            }
            // check in sub attributes
            String subAttributeURI =
                    checkSCIMSubAttributeURIs(attributeSchema.getSubAttributeSchemas(),
                            attributeSchema, attributeName);
            if (subAttributeURI != null) {
                return subAttributeURI;
            }

            /* If the attribute is requested from extension schema check the attribute name contains the URI of the
            attribute schema, else check the attribute name contains name of the attribute schema for the core
            attributes. */
            if ((((isSCIM2ExtensionSchemaAttribute && attributeName.contains(attributeSchema.getURI()))) ||
                    (!isSCIM2ExtensionSchemaAttribute && attributeName.contains(attributeSchema.getName()))) &&
                    attributeSchema.getMultiValued()) {

                String subAttribute = null;
                if (attributeName.contains(".")) {
                    String[] splittedString = attributeName.split(attributeSchema.getName() + ".", 2);
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
     * @param attributeName   @return
     */
    private static String checkSCIMSubAttributeURIs(List<AttributeSchema> subAttributes,
                                                    AttributeSchema attributeSchema, String attributeName) {
        if (subAttributes != null) {
            Iterator<AttributeSchema> subsIterator = subAttributes.iterator();

            while (subsIterator.hasNext()) {
                AttributeSchema subAttributeSchema = subsIterator.next();
                if ((attributeSchema.getName() + "." + subAttributeSchema.getName()).equalsIgnoreCase(attributeName) ||
                        subAttributeSchema.getURI().equals(attributeName)) {
                    return subAttributeSchema.getURI();
                }
                if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    List<AttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();
                    if (subSubAttributeSchemas != null) {
                        Iterator<AttributeSchema> subSubsIterator = subSubAttributeSchemas.iterator();

                        while (subSubsIterator.hasNext()) {
                            AttributeSchema subSubAttributeSchema = subSubsIterator.next();
                            if ((attributeSchema.getName() + "." + subAttributeSchema.getName() + "." +
                                    subSubAttributeSchema.getName()).equalsIgnoreCase(attributeName) ||
                                    subSubAttributeSchema.getURI().equals(attributeName)) {
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
