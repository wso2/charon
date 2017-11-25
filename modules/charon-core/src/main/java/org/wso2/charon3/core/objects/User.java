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
package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;


/**
 * Represents the User object which is a collection of attributes defined by SCIM User-schema.
 */
public class User extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;

    /*
     * return userName of the user
     * @return
     * @throws CharonException
     */
    public String getUserName() throws CharonException {
        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.USER_NAME);
    }

    /*
     * set the userName of the user
     * @param userName
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setUserName(String userName) throws CharonException, BadRequestException {
        this.setSimpleAttribute(SCIMConstants.UserSchemaConstants.USER_NAME,
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME, userName);
    }

    /*
     * return the password of the user
     * @return
     * @throws CharonException
     */
    public String getPassword() throws CharonException {
        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.PASSWORD);
    }

    /*
     * set the password of the user
     * @param password
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setPassword(String password) throws CharonException, BadRequestException {
        setSimpleAttribute(SCIMConstants.UserSchemaConstants.PASSWORD, SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PASSWORD,
                password);
    }

    /*
     * set simple attribute in the scim object
     * @param attributeName
     * @param attributeSchema
     * @param value
     * @throws CharonException
     * @throws BadRequestException
     */
    private void setSimpleAttribute(String attributeName,
                                    AttributeSchema attributeSchema, Object value)
            throws CharonException, BadRequestException {
        if (this.isAttributeExist(attributeName)) {
            ((SimpleAttribute) this.attributeList.get(attributeName)).updateValue(value);
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(attributeName, value);
            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.
                            createAttribute(attributeSchema, simpleAttribute);
            this.attributeList.put(attributeName, simpleAttribute);
        }

    }

    /*
     * return simple attribute's string value
     * @param attributeName
     * @return
     * @throws CharonException
     */
    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {
        return this.isAttributeExist(attributeName) ?
                ((SimpleAttribute) this.attributeList.get(attributeName)).getStringValue() : null;
    }

    /*
     * set the associated groups of the user
     * @param type
     * @param value
     * @param display
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setGroup(String type, String value, String display) throws CharonException, BadRequestException {
        SimpleAttribute typeSimpleAttribute = null;
        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute = null;
        ComplexAttribute complexAttribute = new ComplexAttribute();
        if (type != null) {
            typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, type);
            typeSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE, typeSimpleAttribute);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
        }

        if (value != null) {
            valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value);
            valueSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (display != null) {
            displaySimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display);
            displaySimpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY, displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }
        if (complexAttribute.getSubAttributesList().size() != 0) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (typeSimpleAttribute != null && typeSimpleAttribute.getValue() != null) {
                typeVal = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.GROUPS + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, complexAttribute);
            setGroup(complexAttribute);
        }
    }

    private void setGroup(ComplexAttribute groupPropertiesAttribute) throws CharonException, BadRequestException {
        MultiValuedAttribute groupsAttribute;

        if (this.attributeList.containsKey(SCIMConstants.UserSchemaConstants.GROUPS)) {
            groupsAttribute = (MultiValuedAttribute) this.attributeList.get(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
        } else {
            groupsAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
            groupsAttribute = (MultiValuedAttribute)
                    DefaultAttributeFactory.createAttribute(
                            SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, groupsAttribute);
            this.attributeList.put(SCIMConstants.UserSchemaConstants.GROUPS, groupsAttribute);
        }

    }

    /*
     * set the schemas of the user
     */
    public void setSchemas() {
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        java.util.List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }
    }

}
