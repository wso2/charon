/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.schema;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.Role;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.utils.CopyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.REFERENCE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.IMMUTABLE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.READ_ONLY;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.READ_WRITE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.ALWAYS;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.DEFAULT;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.NONE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.SERVER;

@PrepareForTest({AbstractResourceManager.class})
public class ServerSideValidatorTest extends PowerMockTestCase {

    @DataProvider(name = "dataForValidateCreatedSCIMObject")
    public Object[][] dataToValidateCreatedSCIMObject() throws InstantiationException, IllegalAccessException {

        User user1 = User.class.newInstance();
        user1.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        user1.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttribute = new SimpleAttribute("nickName", "shaggy");
        simpleAttribute.setMutability(SCIMDefinitions.Mutability.READ_WRITE);
        simpleAttribute.setRequired(false);
        user1.setAttribute(simpleAttribute);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        user1.setAttribute(complexAttributeMeta);

        SimpleAttribute simpleAttributeEmailType = new SimpleAttribute("type", "home");
        SimpleAttribute simpleAttributeEmailValue = new SimpleAttribute("value", "rash@gmail.com");

        Map<String, Attribute> valuesMap = new HashMap<>();
        valuesMap.put("type", simpleAttributeEmailType);
        valuesMap.put("value", simpleAttributeEmailValue);

        ComplexAttribute complexAttributeEmail = new ComplexAttribute("emails_rash@gmail.com_home");
        complexAttributeEmail.setType(COMPLEX);
        complexAttributeEmail.setMutability(READ_WRITE);
        complexAttributeEmail.setRequired(false);
        complexAttributeEmail.setSubAttributesList(valuesMap);

        MultiValuedAttribute multiValuedAttributeEmail = new MultiValuedAttribute("emails");
        multiValuedAttributeEmail.setType(SCIMDefinitions.DataType.COMPLEX);
        multiValuedAttributeEmail.setMultiValued(true);
        multiValuedAttributeEmail.setMutability(READ_WRITE);
        multiValuedAttributeEmail.setAttributeValue(complexAttributeEmail);
        user1.setAttribute(multiValuedAttributeEmail);

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        AttributeSchema subSubAttributeSchema =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema);
        AttributeSchema subAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses",
                        "addresses", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null,
                        subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        ArrayList<AttributeSchema> subAttributeSchemaList1 = new ArrayList<>();
        subAttributeSchemaList1.add(subAttributeSchema3);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails",
                        "emails", COMPLEX,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList1);
        SCIMResourceTypeSchema userResourceSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        Group group1 = Group.class.newInstance();
        group1.setSchema("urn:ietf:params:scim:schemas:core:2.0:Group");

        SimpleAttribute simpleAttributeGroup = new SimpleAttribute("displayName", "manager");
        group1.setAttribute(simpleAttributeGroup);

        SimpleAttribute simpleAttributeDisplay = new SimpleAttribute("display", "rashN");
        SimpleAttribute simpleAttributeValue = new SimpleAttribute(
                "value", "008bba85-451d-414b-87de-c03b5a1f4218");

        Map<String, Attribute> valuesMapGroup = new HashMap<>();
        valuesMapGroup.put("display", simpleAttributeDisplay);
        valuesMapGroup.put("value", simpleAttributeValue);

        ComplexAttribute complexAttributeMembers = new ComplexAttribute(
                "members_008bba85-451d-414b-87de-c03b5a1f4218_default");
        complexAttributeMembers.setType(COMPLEX);
        complexAttributeMembers.setMutability(READ_WRITE);
        complexAttributeMembers.setRequired(false);
        complexAttributeMembers.setSubAttributesList(valuesMapGroup);

        MultiValuedAttribute multiValuedAttributeMembers = new MultiValuedAttribute("members");
        multiValuedAttributeMembers.setType(SCIMDefinitions.DataType.COMPLEX);
        multiValuedAttributeMembers.setMultiValued(true);
        multiValuedAttributeMembers.setMutability(READ_WRITE);
        multiValuedAttributeMembers.setAttributeValue(complexAttributeMembers);
        group1.setAttribute(multiValuedAttributeMembers);

        List<String> schemasListGroup = new ArrayList<>();
        schemasListGroup.add("urn:ietf:params:scim:schemas:core:2.0:Group");

        AttributeSchema subAttributeSchemaGroup =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:meta.resourceType",
                        "resourceType", STRING, false, "", false, true,
                        READ_ONLY, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchemaGroup1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:Group:members.value",
                        "value", STRING, false, "", true, false,
                        IMMUTABLE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaListGroup = new ArrayList<>();
        subAttributeSchemaListGroup.add(subAttributeSchemaGroup);
        ArrayList<AttributeSchema> subAttributeSchemaListGroup1 = new ArrayList<>();
        subAttributeSchemaListGroup1.add(subAttributeSchemaGroup1);
        AttributeSchema attributeSchemaGroup1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:id", "id", STRING,
                        false, "", false, true,
                        READ_ONLY, ALWAYS, SERVER, null, null, null);
        AttributeSchema attributeSchemaGroup2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:externalId", "externalId", STRING,
                        false, "", false, true,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema attributeSchemaGroup3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:meta", "meta", COMPLEX,
                        false, "", false, false,
                        READ_ONLY, DEFAULT, NONE, null, null, subAttributeSchemaListGroup);
        AttributeSchema attributeSchemaGroup4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:Group:displayName", "displayName", STRING,
                        false, "", true, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema attributeSchemaGroup5 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:Group:members", "members", COMPLEX,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaListGroup1);
        SCIMResourceTypeSchema groupResourceSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasListGroup, attributeSchemaGroup1,
                        attributeSchemaGroup2, attributeSchemaGroup3, attributeSchemaGroup4, attributeSchemaGroup5);

        Role role1 = Role.class.newInstance();
        role1.setSchema("urn:ietf:params:scim:schemas:extension:2.0:Role");

        List<String> permissions = new ArrayList<>();
        permissions.add("/permission/admin/login");
        role1.setPermissions(permissions);

        SimpleAttribute simpleAttributeRole = new SimpleAttribute("displayName", "loginRole");
        role1.setAttribute(simpleAttributeRole);

        MultiValuedAttribute multiValuedAttributePermissions = new MultiValuedAttribute("permissions");
        multiValuedAttributePermissions.setType(SCIMDefinitions.DataType.REFERENCE);
        multiValuedAttributePermissions.setMultiValued(true);
        multiValuedAttributePermissions.setMutability(READ_WRITE);
        List<Object> permissionsPrimitive = new ArrayList<>();
        permissionsPrimitive.add("/permission/admin/login");
        multiValuedAttributePermissions.setAttributePrimitiveValues(permissionsPrimitive);
        role1.setAttribute(multiValuedAttributePermissions);

        MultiValuedAttribute multiValuedAttributeGroups = new MultiValuedAttribute("groups");
        multiValuedAttributeGroups.setType(COMPLEX);
        multiValuedAttributeGroups.setMultiValued(true);
        multiValuedAttributeGroups.setMutability(READ_WRITE);

        SimpleAttribute simpleAttributeGroupValue = new SimpleAttribute(
                "value", "57ed28f8-a76c-4ebf-b6e2-c345270f9879");

        Map<String, Attribute> valuesMapForGroups = new HashMap<>();
        valuesMapForGroups.put("value", simpleAttributeGroupValue);

        ComplexAttribute complexAttributeGroup = new ComplexAttribute(
                "groups_57ed28f8-a76c-4ebf-b6e2-c345270f9879_default");
        complexAttributeGroup.setType(COMPLEX);
        complexAttributeGroup.setMutability(READ_WRITE);
        complexAttributeGroup.setRequired(false);
        complexAttributeGroup.setSubAttributesList(valuesMapForGroups);
        multiValuedAttributeGroups.setAttributeValue(complexAttributeGroup);
        role1.setAttribute(multiValuedAttributeGroups);

        MultiValuedAttribute multiValuedAttributeUsers = new MultiValuedAttribute("users");
        multiValuedAttributeUsers.setType(COMPLEX);
        multiValuedAttributeUsers.setMultiValued(true);
        multiValuedAttributeUsers.setMutability(READ_WRITE);

        SimpleAttribute simpleAttributeUserValue = new SimpleAttribute(
                "value", "f11fc5ba-8684-44a7-a38b-5e2de0ff1751");

        Map<String, Attribute> valuesMapForUsers = new HashMap<>();
        valuesMapForUsers.put("value", simpleAttributeUserValue);

        ComplexAttribute complexAttributeUsers = new ComplexAttribute(
                "users_f11fc5ba-8684-44a7-a38b-5e2de0ff1751_default");
        complexAttributeUsers.setType(COMPLEX);
        complexAttributeUsers.setMutability(READ_WRITE);
        complexAttributeUsers.setRequired(false);
        complexAttributeUsers.setSubAttributesList(valuesMapForUsers);
        multiValuedAttributeUsers.setAttributeValue(complexAttributeGroup);
        role1.setAttribute(multiValuedAttributeUsers);

        List<String> schemasListRole = new ArrayList<>();
        schemasListRole.add("urn:ietf:params:scim:schemas:extension:2.0:Role");

        AttributeSchema subAttributeSchemaRole =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:users.value",
                        "value", STRING, false, "", false, false,
                        IMMUTABLE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchemaRole1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:groups.value",
                        "value", STRING, false, "", false, false,
                        IMMUTABLE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaListRole = new ArrayList<>();
        subAttributeSchemaListRole.add(subAttributeSchemaRole);
        ArrayList<AttributeSchema> subAttributeSchemaListRole1 = new ArrayList<>();
        subAttributeSchemaListRole1.add(subAttributeSchemaRole1);
        AttributeSchema attributeSchemaRole1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:id", "id", STRING,
                        false, "", false, true,
                        READ_ONLY, ALWAYS, SERVER, null, null, null);
        AttributeSchema attributeSchemaRole2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:displayName", "displayName", STRING,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema attributeSchemaRole3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:users", "users", COMPLEX,
                        false, "", false, false,
                        READ_ONLY, DEFAULT, NONE, null, null, subAttributeSchemaListRole);
        AttributeSchema attributeSchemaRole4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:groups", "groups", COMPLEX,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaListRole1);
        AttributeSchema attributeSchemaRole5 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:Role:permissions", "permissions", REFERENCE,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        SCIMResourceTypeSchema roleResourceSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasListRole, attributeSchemaRole1,
                        attributeSchemaRole2, attributeSchemaRole3, attributeSchemaRole4, attributeSchemaRole5);

        return new Object[][]{

                {user1, userResourceSchema, "SUCCESS"},
                {group1, groupResourceSchema, "SUCCESS"},
                {user1, groupResourceSchema, "BAD_REQUEST"},
                {group1, userResourceSchema, "CHARON_EXCEPTION"},
                {role1, roleResourceSchema, "SUCCESS"}

        };
    }

    @Test(dataProvider = "dataForValidateCreatedSCIMObject")
    public void testValidateCreatedSCIMObject(Object objectScimObject, Object objectResourceSchema, Object objectExpect)
            throws CharonException, BadRequestException, NotFoundException {

        AbstractSCIMObject scimObject = (AbstractSCIMObject) objectScimObject;
        SCIMResourceTypeSchema resourceSchema = (SCIMResourceTypeSchema) objectResourceSchema;
        String expect = (String) objectExpect;

        mockStatic(AbstractResourceManager.class);
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/Users");
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.GROUP_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/Groups");
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.ROLE_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/Roles");
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/ServiceProviderConfig");
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.RESOURCE_TYPE_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/ResourceTypes");

        String result = "";

        try {
            ServerSideValidator.validateCreatedSCIMObject(scimObject, resourceSchema);
            result = "SUCCESS";
        } catch (BadRequestException e) {
            result = "BAD_REQUEST";
        } catch (CharonException e) {
            result = "CHARON_EXCEPTION";
        }

        Assert.assertEquals(expect, result);
    }

    @DataProvider(name = "dataForValidateUpdatedSCIMObject")
    public Object[][] dataToValidateUpdatedSCIMObject() throws InstantiationException, IllegalAccessException,
            CharonException {

        User oldObject1 = User.class.newInstance();
        oldObject1.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        oldObject1.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttributeId = new SimpleAttribute("id", "229d3f0d-a07b-4052-bf4d-3071ecafed04");
        simpleAttributeId.setMutability(READ_ONLY);
        simpleAttributeId.setRequired(false);
        oldObject1.setAttribute(simpleAttributeId);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        oldObject1.setAttribute(complexAttributeMeta);

        User newObject1 = (User) CopyUtil.deepCopy(oldObject1);
        SimpleAttribute simpleAttributeNickName = new SimpleAttribute("nickName", "shaggy");
        newObject1.setAttribute(simpleAttributeNickName);

        User oldObject2 = (User) CopyUtil.deepCopy(oldObject1);

        SimpleAttribute simpleAttributeEmailType = new SimpleAttribute("type", "home");
        SimpleAttribute simpleAttributeEmailValue = new SimpleAttribute("value", "rash@gmail.com");

        Map<String, Attribute> valuesMap = new HashMap<>();
        valuesMap.put("type", simpleAttributeEmailType);
        valuesMap.put("value", simpleAttributeEmailValue);

        ComplexAttribute complexAttributeEmail = new ComplexAttribute("emails_rash@gmail.com_home");
        complexAttributeEmail.setType(COMPLEX);
        complexAttributeEmail.setMutability(READ_WRITE);
        complexAttributeEmail.setRequired(false);
        complexAttributeEmail.setSubAttributesList(valuesMap);

        MultiValuedAttribute multiValuedAttributeEmail = new MultiValuedAttribute("emails");
        multiValuedAttributeEmail.setType(SCIMDefinitions.DataType.COMPLEX);
        multiValuedAttributeEmail.setMultiValued(true);
        multiValuedAttributeEmail.setMutability(READ_WRITE);
        multiValuedAttributeEmail.setAttributeValue(complexAttributeEmail);
        oldObject2.setAttribute(multiValuedAttributeEmail);

        User newObject2 = (User) CopyUtil.deepCopy(oldObject2);
        newObject2.setAttribute(simpleAttributeNickName);

        AbstractSCIMObject oldObject3 = AbstractSCIMObject.class.newInstance();
        oldObject3.setSchema("urn:ietf:params:scim:api:messages:2.0:ListResponse");
        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "User");
        oldObject3.setAttribute(simpleAttribute1);
        SimpleAttribute simpleAttribute2 = new SimpleAttribute("name", "User");
        oldObject3.setAttribute(simpleAttribute2);
        SimpleAttribute simpleAttribute3 = new SimpleAttribute("endpoint", "/Users");
        oldObject3.setAttribute(simpleAttribute3);
        oldObject3.setAttribute(complexAttributeMeta);

        AbstractSCIMObject newObject3 = (AbstractSCIMObject) CopyUtil.deepCopy(oldObject3);
        SimpleAttribute simpleAttributeDescription = new SimpleAttribute("description", "User Account");
        newObject3.setAttribute(simpleAttributeDescription);

        List<String> schemasList1 = new ArrayList<>();
        schemasList1.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList1.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        AttributeSchema subAttributeSchemaValue =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaListEmails = new ArrayList<>();
        subAttributeSchemaListEmails.add(subAttributeSchemaValue);
        AttributeSchema attributeSchemaCustom =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute1",
                        "attribute1", STRING, true, "", true, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema attributeSchemaEmail =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails",
                        "emails", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaListEmails);
        SCIMResourceTypeSchema scimResourceTypeSchema1 = SCIMResourceTypeSchema.createSCIMResourceSchema(
                schemasList1, attributeSchemaCustom, attributeSchemaEmail);

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        AttributeSchema subSubAttributeSchema =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema);
        AttributeSchema subAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses",
                        "addresses", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null,
                        subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        ArrayList<AttributeSchema> subAttributeSchemaList1 = new ArrayList<>();
        subAttributeSchemaList1.add(subAttributeSchema3);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails",
                        "emails", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList1);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        return new Object[][]{

                {oldObject1, newObject1, scimResourceTypeSchema1, "BAD_REQUEST"},
                {oldObject2, newObject2, scimResourceTypeSchema, "SUCCESS"},
                {oldObject3, newObject3, scimResourceTypeSchema, "CHARON_EXCEPTION"}
        };
    }

    @Test(dataProvider = "dataForValidateUpdatedSCIMObject")
    public void testValidateUpdatedSCIMObject(Object objectOldObject, Object objectNewObject,
                                              Object objectResourceSchema, Object objectExpect)
            throws CharonException, BadRequestException {

        AbstractSCIMObject oldObject = (AbstractSCIMObject) objectOldObject;
        AbstractSCIMObject newObject = (AbstractSCIMObject) objectNewObject;
        SCIMResourceTypeSchema resourceSchema = (SCIMResourceTypeSchema) objectResourceSchema;
        String expect = (String) objectExpect;

        String result = "";

        try {
            ServerSideValidator.validateUpdatedSCIMObject(oldObject, newObject, resourceSchema);
            result = "SUCCESS";
        } catch (BadRequestException e) {
            result = "BAD_REQUEST";
        } catch (CharonException e) {
            result = "CHARON_EXCEPTION";
        }

        Assert.assertEquals(expect, result);
    }

    @DataProvider(name = "dataForValidateResourceTypeSCIMObject")
    public Object[][] dataToValidateResourceTypeSCIMObject() throws CharonException,
            InstantiationException, IllegalAccessException {

        AbstractSCIMObject userResourceTypeObject = AbstractSCIMObject.class.newInstance();
        userResourceTypeObject.setSchema("urn:ietf:params:scim:api:messages:2.0:ListResponse");
        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "User");
        userResourceTypeObject.setAttribute(simpleAttribute1);
        SimpleAttribute simpleAttribute2 = new SimpleAttribute("name", "User");
        userResourceTypeObject.setAttribute(simpleAttribute2);
        SimpleAttribute simpleAttribute3 = new SimpleAttribute("endpoint", "/Users");
        userResourceTypeObject.setAttribute(simpleAttribute3);

        AbstractSCIMObject groupResourceTypeObject = AbstractSCIMObject.class.newInstance();
        groupResourceTypeObject.setSchema("urn:ietf:params:scim:api:messages:2.0:ListResponse");
        SimpleAttribute simpleAttribute4 = new SimpleAttribute("id", "Group");
        groupResourceTypeObject.setAttribute(simpleAttribute4);
        SimpleAttribute simpleAttribute5 = new SimpleAttribute("name", "Group");
        groupResourceTypeObject.setAttribute(simpleAttribute5);
        SimpleAttribute simpleAttribute6 = new SimpleAttribute("endpoint", "/Groups");
        groupResourceTypeObject.setAttribute(simpleAttribute6);

        return new Object[][]{

                {userResourceTypeObject, "https://localhost:9443/scim2/ResourceTypes/User", "ResourceType"},
                {groupResourceTypeObject, "https://localhost:9443/scim2/ResourceTypes/Group", "ResourceType"}
        };
    }

    @Test(dataProvider = "dataForValidateResourceTypeSCIMObject")
    public void testValidateResourceTypeSCIMObject(Object objectScimObject, Object objectExpectedLocation,
                                                   Object objectExpectedResourceType)
                                                    throws NotFoundException, BadRequestException, CharonException {

        AbstractSCIMObject scimObject = (AbstractSCIMObject) objectScimObject;
        String expectedLocation = (String) objectExpectedLocation;
        String expectedResourceType = (String) objectExpectedResourceType;

        mockStatic(AbstractResourceManager.class);
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.RESOURCE_TYPE_ENDPOINT)).thenReturn(
                "https://localhost:9443/scim2/ResourceTypes");

        AbstractSCIMObject outputScimObject = ServerSideValidator.validateResourceTypeSCIMObject(scimObject);
        Assert.assertEquals(outputScimObject.getLocation(), expectedLocation);
        Assert.assertEquals(outputScimObject.getResourceType(), expectedResourceType);
    }
}
