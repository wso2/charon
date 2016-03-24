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
package org.wso2.charon.core.util;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMResourceSchema;
import org.wso2.charon.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSubAttributeSchema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AttributeUtil {

    /**
     * We need to parse the string values of an attribute to the real data type of that attribute.
     *
     * @param attributeStringValue
     * @param dataType
     * @return
     * @throws CharonException
     */
    public static Object getAttributeValueFromString(String attributeStringValue,
                                                     SCIMSchemaDefinitions.DataType dataType)
            throws CharonException {
        switch (dataType) {
            case STRING:
                return attributeStringValue.trim();
            case BOOLEAN:
                return Boolean.parseBoolean(attributeStringValue);
            case DECIMAL:
                return Double.parseDouble(attributeStringValue);
            case INTEGER:
                return Integer.parseInt(attributeStringValue);
            case DATE_TIME:
                return parseDateTime(attributeStringValue);
            case BINARY:
                return new Byte(attributeStringValue);

        }
        throw new CharonException("Error in converting string value to attribute type: " + dataType);

    }

    public static String getStringValueOfAttribute(Object attributeValue,
                                                   SCIMSchemaDefinitions.DataType dataType)
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
        }
        throw new CharonException("Error in converting attribute value of type: " + dataType + " to string.");
    }

    /**
     * SCIM spec requires date time to be adhered to XML Schema Datatypes Specification
     *
     * @param dateTimeString
     * @return
     * @throws java.text.ParseException
     */
    public static Date parseDateTime(String dateTimeString) throws CharonException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            throw new CharonException("Error in parsing date time. " +
                                      "Date time should adhere to the format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    /**
     * SCIM spec requires date time to be adhered to XML Schema Datatypes Specification
     *
     * @param date
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

	/**
	 * Get fully qualified attribute URI, given the attribute name
	 * 
	 * @param attributeName
	 * @return
	 */
	public static String getAttributeURI(String attributeName) {

		SCIMResourceSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
		Iterator<AttributeSchema> attributeSchemas = schema.getAttributesList().iterator();

		while (attributeSchemas.hasNext()) {
			AttributeSchema attributeSchema = attributeSchemas.next();
			if (attributeSchema.getName().equalsIgnoreCase(attributeName)) {
				return attributeSchema.getURI();
			}
			// check in sub attributes
			String subAttributeURI =
			                         checkSCIMSubAttributeURIs(((SCIMAttributeSchema) attributeSchema).getSubAttributes(),
			                                                   attributeName);
			if (subAttributeURI != null) {
				return subAttributeURI;
			}
			// check in attributes
			String attributeURI =
			                      checkSCIMAttributeURIs(((SCIMAttributeSchema) attributeSchema).getAttributes(),
			                                             attributeName);
			if (attributeURI != null) {
				return attributeURI;
			}
		}

		return null;
	}
    
	/**
	 * Will iterate through <code>{@code SCIMAttributeSchema}</code> objects
	 * 
	 * @param attributeSchemas
	 * @param attributeName
	 * @return
	 */
	private static String checkSCIMAttributeURIs(List<SCIMAttributeSchema> attributeSchemas,
	                                             String attributeName) {
		if (attributeSchemas != null) {
			Iterator<SCIMAttributeSchema> attribIterator = attributeSchemas.iterator();

			while (attribIterator.hasNext()) {
				SCIMAttributeSchema attributeSchema = attribIterator.next();
				if (attributeSchema.getName().equalsIgnoreCase(attributeName)) {
					return attributeSchema.getURI();
				}
				// check in sub attributes
				String subAttributeURI =
				                         checkSCIMSubAttributeURIs(((SCIMAttributeSchema) attributeSchema).getSubAttributes(),
				                                                   attributeName);
				if (subAttributeURI != null) {
					return subAttributeURI;
				}
				// check in attributes
				String attributeURI =
				                      checkSCIMAttributeURIs(((SCIMAttributeSchema) attributeSchema).getAttributes(),
				                                             attributeName);
				if (attributeURI != null) {
					return attributeURI;
				}
			}
		}
		return null;
	}
    
    /**
     * Will iterate through <code>{@code SCIMSubAttributeSchema}</code> objects
     * @param subAttributes
     * @param attributeName
     * @return
     */
    private static String checkSCIMSubAttributeURIs(List<SCIMSubAttributeSchema> subAttributes, String attributeName) {
    	if (subAttributes != null) {
    		Iterator<SCIMSubAttributeSchema> subsIterator = subAttributes.iterator();
    		
			while(subsIterator.hasNext()) {
				SCIMSubAttributeSchema subAttribSchema = subsIterator.next();
				if(subAttribSchema.getName().equalsIgnoreCase(attributeName)) {
					return subAttribSchema.getURI();
				}
			}
		}
		return null;
    }
}
