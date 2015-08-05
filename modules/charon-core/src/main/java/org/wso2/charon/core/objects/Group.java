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
package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Group object which is a collection of attributes as defined by SCIM-Group schema.
 */
public class Group extends AbstractSCIMObject {

    public Group() {
        super();
    }

    /**
     * Set bulkID when going to do the bulk operation
     *
     * @param bulkID
     * @throws CharonException
     */
    public void setBulkID(String bulkID) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.BULK_ID, SCIMSchemaDefinitions.BULK_ID,
                           bulkID, SCIMSchemaDefinitions.DataType.STRING);
    }

    /**
     * Get bulkID
     *
     * @return
     * @throws CharonException
     */
    public String getBulkID() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.BULK_ID);
    }

    /**
     * Set path ex - /Users or /Groups
     *
     * @param path
     * @throws CharonException
     */
    public void setPath(String path) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.PATH, SCIMSchemaDefinitions.PATH,
                           path, SCIMSchemaDefinitions.DataType.STRING);
    }

    /**
     * Get path
     *
     * @return
     * @throws CharonException
     */
    public String getPath() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.PATH);
    }

    /**
     * Set request method ex - POST
     *
     * @param method
     * @throws CharonException
     */
    public void setMethod(String method) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.METHOD, SCIMSchemaDefinitions.METHOD,
                           method, SCIMSchemaDefinitions.DataType.STRING);
    }

    /**
     * Get request method
     *
     * @return
     * @throws CharonException
     */
    public String getMethod() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.METHOD);
    }


    /**
     * *************Methods for manipulating Group attributes*******************
     */
    /**
     * Get the display name of the group.
     *
     * @return
     * @throws CharonException
     */
    public String getDisplayName() throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).getStringValue();
        } else {
            return null;
        }
    }

    //get members - only the ids

    public List<String> getMembers() throws CharonException {
        //retrieve all member ids irrespective of type
        return getMembers(null);
    }

    //get user members - only ids

    public List<String> getUserMembers() throws CharonException {
        return getMembers(SCIMConstants.USER);
    }

    //get group members - only ids

    public List<String> getGroupMembers() throws CharonException {
        return getMembers(SCIMConstants.USER);
    }

    //get members with is and display name - return Map<ID,DisplayName>

    public List<String> getMembersWithDisplayName() {
        List<String> displayNames = new ArrayList<String>();
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            List<Map<String, Object>> values = members.getComplexValues();
            if (values != null) {
                for (Map<String, Object> value : values) {
                    for (Map.Entry<String, Object> entry : value.entrySet()) {
                        if ((SCIMConstants.CommonSchemaConstants.DISPLAY).equals(entry.getKey())) {
                            //add display name to the list
                            displayNames.add((String) entry.getValue());
                        }
                    }
                }
            }
        }
        return displayNames;
    }

    /**
     * If operation type is equal to null this method return user list requested
     * for adding, if operation type is equal to delete then it returns user
     * list requested for deleting
     * 
     * @param operationType
     * @return
     */
    public List<String> getMembersWithDisplayName(String operationType) {
        List<String> displayNames = new ArrayList<String>();
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members =
                                           (MultiValuedAttribute) attributeList.get(SCIMConstants.GroupSchemaConstants.MEMBERS);
            List<Map<String, Object>> values = members.getComplexValues();
            if (values != null) {
                for (Map<String, Object> value : values) {
                    if (operationType == null) {
                        boolean isOperationPresent = false;
                        for (Map.Entry<String, Object> entry : value.entrySet()) {
                            if ((SCIMConstants.CommonSchemaConstants.OPERATION).equals(entry.getKey())) {
                                isOperationPresent = true;
                            }
                        }
                        if (isOperationPresent == false) {
                            for (Map.Entry<String, Object> entry : value.entrySet()) {
                                if ((SCIMConstants.CommonSchemaConstants.DISPLAY).equals(entry.getKey())) {
                                    // add display name to the list
                                    displayNames.add((String) entry.getValue());
                                }
                            }
                        }
                    } else {
                        for (Map.Entry<String, Object> entry : value.entrySet()) {
                            if ((SCIMConstants.CommonSchemaConstants.OPERATION).equals(entry.getKey()) &&
                                entry.getValue() != null && (entry.getValue()).equals(operationType)) {
                                for (Map.Entry<String, Object> entryTemp : value.entrySet()) {
                                    if ((SCIMConstants.CommonSchemaConstants.DISPLAY).equals(entryTemp.getKey())) {
                                        // add display name to the list
                                        displayNames.add((String) entryTemp.getValue());
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return displayNames;
    }

    //get user members with display name - return Map<ID,DisplayName>

    //get group members with display name - Map<ID,DisplayName>

    /**
     * Internal method to retrieve group-member ids given the type of the member.
     * If type is null, all ids returned.
     *
     * @param type
     * @return
     * @throws CharonException
     */
    private List<String> getMembers(String type) throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            return members.getAttributeValuesByType(type);
        } else {
            return null;
        }
    }

    //set member- Map<propertyName,property value>

    public void setMember(Map<String, Object> propertyValues) throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            (attributeList.get(SCIMConstants.GroupSchemaConstants.MEMBERS)).
                    setComplexValue(propertyValues);
        } else {
            MultiValuedAttribute membersAttribute =
                    new MultiValuedAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS);
            membersAttribute.setComplexValue(propertyValues);
            this.attributeList.put(SCIMConstants.GroupSchemaConstants.MEMBERS, membersAttribute);
        }
    }

    //set user member - id

    public void setUserMember(String id) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.USER);
        setMember(propertyValues);
    }

    //remove user member

    public void removeMember(String id) throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            List<Attribute> values = members.getValuesAsSubAttributes();
            for (Attribute value : values) {
                SimpleAttribute valueAttribute =
                        (SimpleAttribute) ((ComplexAttribute) value).getSubAttribute(
                                SCIMConstants.CommonSchemaConstants.VALUE);
                if (id.equals(valueAttribute.getStringValue())) {
                    members.removeAttributeValue(value);
                    break;
                }
            }
        }
    }

    //set group member - id

    public void setGroupMember(String id) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.GROUP);
        setMember(propertyValues);
    }

    //set member - id, displayName, type

    public void setMember(String id, String displayName, String type) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.TYPE, type);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.DISPLAY, displayName);

        setMember(propertyValues);
    }

    //set member - id, display name,

    public void setMember(String id, String displayName) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.DISPLAY, displayName);

        setMember(propertyValues);
    }

    //set member - id

    public void setMember(String id) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        setMember(propertyValues);
    }

    //set userMember - id, display name.

    public void setUserMember(String id, String displayName) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.USER);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.DISPLAY, displayName);
        setMember(propertyValues);
    }

    //set groupMember - id, display name,

    public void setGroupMember(String id, String displayName) throws CharonException {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put(SCIMConstants.CommonSchemaConstants.VALUE, id);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.GROUP);
        propertyValues.put(SCIMConstants.CommonSchemaConstants.DISPLAY, displayName);
        setMember(propertyValues);
    }

    /**
     * Set the display name of the group. If already set, update it.
     *
     * @param displayName
     * @throws CharonException
     */
    public void setDisplayName(String displayName) throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            ((SimpleAttribute) attributeList.get(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).
                    updateValue(displayName, SCIMSchemaDefinitions.DISPLAY_NAME.getType());
        } else {
            SimpleAttribute displayAttribute = new SimpleAttribute(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayName);
            displayAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.DISPLAY_NAME, displayAttribute);
            attributeList.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayAttribute);
        }
    }

    private void setSimpleAttribute(String attributeName, AttributeSchema attributeSchema,
                                    Object value, SCIMSchemaDefinitions.DataType dataType) throws CharonException {
        if (isAttributeExist(attributeName)) {
            //since we check read-only aspect in service provider side, no need to check it here.
            //if (!attributeSchema.getReadOnly()) {
            ((SimpleAttribute) attributeList.get(attributeName)).updateValue(value, dataType);
            /*} else {
                //log info level log that version already set and can't set again.
                throw new CharonException(ResponseCodeConstants.ATTRIBUTE_READ_ONLY);
            }*/
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(
                    attributeName, value);
            /*SimpleAttribute userNameAttribute = new SimpleAttribute(
                    SCIMConstants.UserSchemaConstants.USER_NAME,
                    SCIMConstants.CORE_SCHEMA_URI, userName, DataType.STRING,
                    false, false);*/
            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    attributeSchema, simpleAttribute);
            attributeList.put(attributeName, simpleAttribute);
        }
    }

    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {
        if (isAttributeExist(attributeName)) {
            return ((SimpleAttribute) attributeList.get(attributeName)).getStringValue();
        } else {
            return null;
        }
    }
}
