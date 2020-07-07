/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Role object which is a collection of attributes defined by SCIM Role schema.
 */
public class Role extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;

    /**
     * List of permissions of the role.
     */
    private List<String> permissions = new ArrayList<>();

    /**
     * Get the display name of the role.
     *
     * @return Display name of the role.
     */
    public String getDisplayName() {

        if (isAttributeExist(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME)) {
            return LambdaExceptionUtils.rethrowSupplier(
                    () -> ((SimpleAttribute) attributeList.get(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME))
                            .getStringValue()).get();
        }
        return null;
    }

    /**
     * Set the display name of the role.
     *
     * @param displayName Display name.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    public void setDisplayName(String displayName) throws CharonException, BadRequestException {

        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME)) {
            ((SimpleAttribute) this.attributeList.get(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME)).
                    updateValue(displayName);
        } else {
            SimpleAttribute displayAttribute = new SimpleAttribute(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME,
                    displayName);
            displayAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY_NAME, displayAttribute);
            this.attributeList.put(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME, displayAttribute);
        }
    }

    /**
     * Deletes the current value of display name and rename it with the given value.
     *
     * @param displayName New display name.
     */
    public void replaceDisplayName(String displayName) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.DISPLAY_NAME, displayName);
    }

    /**
     * Get the users of the role.
     *
     * @return List of users.
     */
    public List<String> getUsers() {

        List<String> userList = new ArrayList<>();
        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.USERS)) {
            MultiValuedAttribute users = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.USERS);
            List<Attribute> subValuesList = users.getAttributeValues();
            for (Attribute subValue : subValuesList) {
                ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                if (subAttributesList != null && subAttributesList
                        .containsKey(SCIMConstants.CommonSchemaConstants.VALUE)) {
                    userList.add((String) ((SimpleAttribute) (subAttributesList
                            .get(SCIMConstants.CommonSchemaConstants.VALUE))).getValue());
                }
            }
        }
        return userList;
    }

    /**
     * Get the users of the role with their display names.
     *
     * @return List of display names.
     */
    public List<String> getUsersWithDisplayName() {

        List<String> displayNames = new ArrayList<>();
        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.USERS)) {
            MultiValuedAttribute users = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.USERS);
            List<Attribute> values = users.getAttributeValues();
            if (values != null) {
                List<Attribute> subValuesList = users.getAttributeValues();
                for (Attribute subValue : subValuesList) {
                    ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                    Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                    if (subAttributesList != null && subAttributesList
                            .containsKey(SCIMConstants.CommonSchemaConstants.DISPLAY)) {
                        displayNames.add((String) ((SimpleAttribute) (subAttributesList
                                .get(SCIMConstants.CommonSchemaConstants.DISPLAY))).getValue());
                    }

                }
                return displayNames;
            }
        }

        return displayNames;
    }

    /**
     * Set a user to the role, where user will have three default attributes such as name, value and $ref.
     *
     * @param user User object.
     * @throws BadRequestException BadRequestException.
     * @throws CharonException     CharonException.
     */
    public void setUser(User user) throws BadRequestException, CharonException {

        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.USERS)) {
            MultiValuedAttribute users = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.USERS);
            ComplexAttribute complexAttribute = processUser(user);
            users.setAttributeValue(complexAttribute);
        } else {
            MultiValuedAttribute users = new MultiValuedAttribute(SCIMConstants.RoleSchemaConstants.USERS);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.USERS, users);
            ComplexAttribute complexAttribute = processUser(user);
            users.setAttributeValue(complexAttribute);
            this.setAttribute(users);
        }
    }

    /**
     * Create user attribute with three default attributes such as name, value and $ref.
     *
     * @param user User object.
     * @return User object as a complex attribute.
     * @throws BadRequestException BadRequestException.
     * @throws CharonException     CharonException.
     */
    private ComplexAttribute processUser(User user) throws BadRequestException, CharonException {

        String userId = user.getId();
        String userName = user.getUserName();
        String reference = user.getLocation();

        ComplexAttribute complexAttribute = new ComplexAttribute();
        complexAttribute.setName(SCIMConstants.RoleSchemaConstants.USERS + "_" + userId + SCIMConstants.DEFAULT);

        if (userId != null) {
            SimpleAttribute valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE,
                    userId);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.USERS_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (userName != null) {
            SimpleAttribute displaySimpleAttribute = new SimpleAttribute(SCIMConstants.RoleSchemaConstants.DISPLAY,
                    userName);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.USERS_DISPLAY,
                    displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }

        if (reference != null) {
            SimpleAttribute referenceSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.REF,
                    reference);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.USERS_REF,
                    referenceSimpleAttribute);
            complexAttribute.setSubAttribute(referenceSimpleAttribute);
        }

        DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.USERS, complexAttribute);
        return complexAttribute;
    }

    /**
     * Get the groups of the role.
     *
     * @return List of groups.
     */
    public List<String> getGroups() {

        List<String> groupList = new ArrayList<>();
        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.GROUPS)) {
            MultiValuedAttribute groups = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.GROUPS);
            List<Attribute> subValuesList = groups.getAttributeValues();
            for (Attribute subValue : subValuesList) {
                ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                if (subAttributesList != null && subAttributesList
                        .containsKey(SCIMConstants.CommonSchemaConstants.VALUE)) {
                    groupList.add((String) ((SimpleAttribute) (subAttributesList
                            .get(SCIMConstants.CommonSchemaConstants.VALUE))).getValue());
                }
            }
        }
        return groupList;
    }

    /**
     * Get the groups of the role with their display names.
     *
     * @return List of display names.
     */
    public List<String> getGroupsWithDisplayName() {

        List<String> displayNames = new ArrayList<>();
        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.GROUPS)) {
            MultiValuedAttribute groups = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.GROUPS);
            List<Attribute> values = groups.getAttributeValues();
            if (values != null) {
                List<Attribute> subValuesList = groups.getAttributeValues();
                for (Attribute subValue : subValuesList) {
                    ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                    Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                    if (subAttributesList != null && subAttributesList
                            .containsKey(SCIMConstants.CommonSchemaConstants.DISPLAY)) {
                        displayNames.add((String) ((SimpleAttribute) (subAttributesList
                                .get(SCIMConstants.CommonSchemaConstants.DISPLAY))).getValue());
                    }
                }
                return displayNames;
            }
        }

        return displayNames;
    }

    /**
     * Set a group to the role, where user will have three default attributes such as name, value and $ref.
     *
     * @param group Group object.
     * @throws BadRequestException BadRequestException.
     * @throws CharonException     CharonException.
     */
    public void setGroup(Group group) throws BadRequestException, CharonException {

        if (this.isAttributeExist(SCIMConstants.RoleSchemaConstants.GROUPS)) {
            MultiValuedAttribute groups = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.RoleSchemaConstants.GROUPS);
            ComplexAttribute complexAttribute = processGroup(group);
            groups.setAttributeValue(complexAttribute);
        } else {
            MultiValuedAttribute groups = new MultiValuedAttribute(SCIMConstants.RoleSchemaConstants.GROUPS);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.GROUPS, groups);
            ComplexAttribute complexAttribute = processGroup(group);
            groups.setAttributeValue(complexAttribute);
            this.setAttribute(groups);
        }
    }

    /**
     * Create group attribute with three default attributes such as name, value and $ref.
     *
     * @param group Group object.
     * @return Group object as a complex attribute.
     * @throws BadRequestException BadRequestException.
     * @throws CharonException     CharonException.
     */
    private ComplexAttribute processGroup(Group group) throws BadRequestException, CharonException {

        String groupID = group.getId();
        String groupName = group.getDisplayName();
        String reference = group.getLocation();

        ComplexAttribute complexAttribute = new ComplexAttribute();
        complexAttribute.setName(SCIMConstants.RoleSchemaConstants.GROUPS + "_" + groupID + SCIMConstants.DEFAULT);

        if (groupID != null) {
            SimpleAttribute valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE,
                    groupID);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.GROUPS_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (groupName != null) {
            SimpleAttribute displaySimpleAttribute = new SimpleAttribute(SCIMConstants.RoleSchemaConstants.DISPLAY,
                    groupName);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.GROUPS_DISPLAY,
                    displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }

        if (reference != null) {
            SimpleAttribute referenceSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.REF,
                    reference);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.GROUPS_REF,
                    referenceSimpleAttribute);
            complexAttribute.setSubAttribute(referenceSimpleAttribute);
        }

        DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.GROUPS, complexAttribute);
        return complexAttribute;
    }

    /**
     * Set the schemas for SCIM role object.
     */
    public void setSchemas() {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();
        List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }
    }

    /**
     * Set the permissions for SCIM role object.
     */
    public void setPermissions(List<String> permissions) {

        this.permissions = permissions;
    }

    /**
     * Get the permissions of SCIM role object.
     */
    public List<String> getPermissions() {

        return this.permissions;
    }

}
