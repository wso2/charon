/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;

/**
 * Represents the RoleV2 object which is a custom SCIM2 resource.
 */
public class RoleV2 extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;

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

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.USERS,
                SCIMConstants.CommonSchemaConstants.VALUE);
    }

    /**
     * Get the users of the role with their display names.
     *
     * @return List of display names.
     */
    public List<String> getUsersWithDisplayName() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.USERS,
                SCIMConstants.CommonSchemaConstants.DISPLAY);
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

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.GROUPS,
                SCIMConstants.CommonSchemaConstants.VALUE);
    }

    /**
     * Get the groups of the role with their display names.
     *
     * @return List of display names.
     */
    public List<String> getGroupsWithDisplayName() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.GROUPS,
                SCIMConstants.CommonSchemaConstants.DISPLAY);
    }

    /**
     * Set a group to the role, where group will have three default attributes such as name, value and $ref.
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
     * Get the permission values of SCIM role object.
     */
    public List<String> getPermissionValues() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.PERMISSIONS,
                SCIMConstants.CommonSchemaConstants.VALUE);
    }

    /**
     * Get the display names of the permissions of the role.
     *
     * @return List of display names.
     */
    public List<String> getPermissionDisplayNames() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(SCIMConstants.RoleSchemaConstants.PERMISSIONS,
                SCIMConstants.CommonSchemaConstants.DISPLAY);
    }

    /**
     * Get the permissions of SCIM role object.
     */
    public List<MultiValuedComplexType> getPermissions() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ROLE_V2_PERMISSIONS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_DISPLAY;
        SCIMAttributeSchema refDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_REF;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, null, null,
                refDefinition).orElse(Collections.emptyList());
    }

    /**
     * Set the permissions for SCIM role object.
     */
    public void setPermissions(List<MultiValuedComplexType> permissionsList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ROLE_V2_PERMISSIONS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_DISPLAY;
        SCIMAttributeSchema refDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.PERMISSIONS_REF;
        addMultivaluedComplexAtribute(permissionsList, complexDefinition, valueDefinition, displayDefinition,
                null, null, refDefinition);
    }

    /**
     * Get associated applications' value list of the role.
     */
    public List<String> getAssociatedApplicationValues() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(
                SCIMConstants.RoleSchemaConstants.ASC_APPLICATIONS,
                SCIMConstants.CommonSchemaConstants.VALUE);
    }

    /**
     * Get associated applications' display names list of the role.
     */
    public List<String> getAssociatedApplicationDisplayNames() {

        return getSubAttributeValuesListInMultiValuedComplexAttribute(
                SCIMConstants.RoleSchemaConstants.ASC_APPLICATIONS, SCIMConstants.CommonSchemaConstants.DISPLAY);
    }

    /**
     * Get associated applications of the role.
     */
    public List<MultiValuedComplexType> getAssociatedApplications() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASSOCIATED_APPLICATIONS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_DISPLAY;
        SCIMAttributeSchema refDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_REF;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, null, null,
                refDefinition).orElse(Collections.emptyList());
    }

    /**
     * Set associated applications of the role.
     */
    public void setAssociatedApplications(List<MultiValuedComplexType> associatedApplicationsList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASSOCIATED_APPLICATIONS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_DISPLAY;
        SCIMAttributeSchema refDefinition = SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.ASC_APPLICATIONS_REF;
        addMultivaluedComplexAtribute(associatedApplicationsList, complexDefinition, valueDefinition,
                displayDefinition, null, null, refDefinition);
    }

    /**
     * Get the audience of the role.
     */
    public ComplexAttribute getAudience() {

        if (!isAudienceAttributeExist()) {
            return null;
        }
        return (ComplexAttribute) attributeList.get(SCIMConstants.RoleSchemaConstants.AUDIENCE);
    }

    /**
     * Get the audience value of the role.
     */
    public String getAudienceValue() {

        return getSubAttributeOfComplexAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE,
                SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_VALUE);
    }

    /**
     * Get the audience display name of the role.
     */
    public String getAudienceDisplayName() {

        return getSubAttributeOfComplexAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE,
                SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_DISPLAY);
    }

    /**
     * Get the audience type of the role.
     */
    public String getAudienceType() {

        return getSubAttributeOfComplexAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE,
                SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_TYPE);
    }

    /**
     * Get the audience value of the role.
     *
     * @param audienceValue   Unique ID of the audience.
     * @param audienceDisplay Display name of the audience.
     * @param audienceType    Type of the audience.
     * @throws BadRequestException If the audience attribute values are not accepted.
     * @throws CharonException     If error occurred while setting the audience attributes.
     */
    public void setAudience(String audienceValue, String audienceDisplay, String audienceType)
            throws BadRequestException, CharonException {

        ComplexAttribute audienceAttribute =
                (ComplexAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE,
                        new ComplexAttribute(SCIMConstants.RoleSchemaConstants.AUDIENCE));
        if (isAudienceAttributeExist()) {
            return;
        }
        attributeList.put(SCIMConstants.RoleSchemaConstants.AUDIENCE, audienceAttribute);

        SimpleAttribute audienceValueAttribute = (SimpleAttribute) DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_VALUE,
                        new SimpleAttribute(SCIMConstants.RoleSchemaConstants.VALUE, audienceValue));
        SimpleAttribute audienceDisplayAttribute = (SimpleAttribute) DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_DISPLAY,
                        new SimpleAttribute(SCIMConstants.RoleSchemaConstants.DISPLAY, audienceDisplay));
        SimpleAttribute audienceTypeAttribute = (SimpleAttribute) DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SCIMRoleSchemaDefinition.AUDIENCE_TYPE,
                        new SimpleAttribute(SCIMConstants.RoleSchemaConstants.TYPE, audienceType));

        getAudience().setSubAttribute(audienceValueAttribute);
        getAudience().setSubAttribute(audienceDisplayAttribute);
        getAudience().setSubAttribute(audienceTypeAttribute);
    }

    protected boolean isAudienceAttributeExist() {

        return attributeList.containsKey(SCIMConstants.RoleSchemaConstants.AUDIENCE);
    }


    /**
     * Set the systemRole attribute of the meta attribute.
     *
     * @param isSystemRole Whether this is a read only system role.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    public void setSystemRole(boolean isSystemRole) throws CharonException, BadRequestException {

        // Create the systemRole attribute as defined in schema.
        SimpleAttribute systemRoleAttribute = (SimpleAttribute) DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SYSTEM_ROLE,
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.SYSTEM_ROLE, isSystemRole));

        // Check whether the meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            // Check whether the systemRole attribute already exist.
            if (metaAttribute.isSubAttributeExist(systemRoleAttribute.getName())) {
                String error = "Tried to modify a read only attribute.";
                throw new CharonException(error);
            }
            metaAttribute.setSubAttribute(systemRoleAttribute);
        } else {
            // Create the meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(systemRoleAttribute);
        }
    }

    private List<String> getSubAttributeValuesListInMultiValuedComplexAttribute(String multiValuedAttributeName,
                                                                                String subAttributeName) {

        if (!this.isAttributeExist(multiValuedAttributeName)) {
            return Collections.emptyList();
        }
        MultiValuedAttribute multiValuedAttribute =
                (MultiValuedAttribute) this.attributeList.get(multiValuedAttributeName);
        List<Attribute> subValuesList = multiValuedAttribute.getAttributeValues();
        if (subValuesList == null) {
            return Collections.emptyList();
        }
        return getSubAttributeValues(subAttributeName, subValuesList);
    }

    private List<String> getSubAttributeValues(String subAttributeName, List<Attribute> subValuesList) {

        List<String> subAttributeValues = new ArrayList<>();
        for (Attribute subValue : subValuesList) {
            ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
            Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
            if (subAttributesList == null || !subAttributesList.containsKey(subAttributeName)) {
                continue;
            }
            subAttributeValues.add((String) ((SimpleAttribute) (subAttributesList.get(subAttributeName))).getValue());
        }
        return subAttributeValues;
    }

    private String getSubAttributeOfComplexAttribute(SCIMAttributeSchema complexAttributeSchema,
                                                     SCIMAttributeSchema subAttributeSchema) {

        return getComplexAttribute(complexAttributeSchema).map(
                complexAttribute -> getSimpleAttribute(subAttributeSchema, complexAttribute)
                        .map(rethrowFunction(SimpleAttribute::getStringValue))
                        .orElse(null)).orElse(null);
    }
}
