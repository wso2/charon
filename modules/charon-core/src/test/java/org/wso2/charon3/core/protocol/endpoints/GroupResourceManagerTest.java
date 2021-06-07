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

package org.wso2.charon3.core.protocol.endpoints;

import org.json.JSONObject;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test class of GroupResourceManager.
 */
public class GroupResourceManagerTest {

    private static final String GROUP_ID = "71239";
    private static final String SCIM2_GROUP_ENDPOINT = "https://localhost:9443/scim2/Groups";

    private static final String NEW_GROUP_SCIM_OBJECT_STRING = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:ListResponse\"\n" +
            "  ],\n" +
            "  \"displayName\": \"PRIMARY/manager\",\n" +
            "      \"meta\": {\n" +
            "       \"created\": \"2019-08-26T14:27:36\",\n" +
            "        \"location\": \"https://localhost:9443/scim2/Groups/7bac6a86-1f21-4937-9fb1-5be4a93ef469\",\n" +
            "        \"lastModified\": \"2019-08-26T14:27:36\"\n" +
            "      },\n" +
            "      \"members\": [\n" +
            "        {\n" +
            "          \"$ref\": \"https://localhost:9443/scim2/Users/3a12bae9-4386-44be-befd-caf349297f45\",\n" +
            "          \"display\": \"kim\",\n" +
            "          \"value\": \"008bba85-451d-414b-87de-c03b5a1f4217\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"id\": \"" + GROUP_ID + "\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_GROUP_SCIM_OBJECT_STRING_UPDATED = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:ListResponse,\"\n" +
            "  ],\n" +
            "  \"displayName\": \"PRIMARY/manager_sales\",\n" +
            "      \"meta\": {\n" +
            "       \"created\": \"2019-08-26T14:27:36\",\n" +
            "        \"location\": \"https://localhost:9443/scim2/Groups/7bac6a86-1f21-4937-9fb1-5be4a93ef469\",\n" +
            "        \"lastModified\": \"2019-08-26T14:28:36\"\n" +
            "      },\n" +
            "      \"members\": [\n" +
            "        {\n" +
            "          \"$ref\": \"https://localhost:9443/scim2/Users/3a12bae9-4386-44be-befd-caf349297f45\",\n" +
            "          \"display\": \"kim\",\n" +
            "          \"value\": \"008bba85-451d-414b-87de-c03b5a1f4217\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"id\": \"" + GROUP_ID + "\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String SCIM2_PATCH_REQUEST_STRING = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "  ],\n" +
            "  \"Operations\": [\n" +
            "    {\n" +
            "      \"op\": \"add\",\n" +
            "      \"value\": {\n" +
            "        \"members\": [\n" +
            "          {\n" +
            "            \"display\": \"kris\",\n" +
            "            \"value\": \"409ca90b-2ba6-4474-9a45-2cf7376e6e43\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_GROUP_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:ListResponse,\"\n" +
            "  ],\n" +
            "  \"displayName\": \"PRIMARY/manager_sales\",\n" +
            "      \"meta\": {\n" +
            "       \"created\": \"2019-08-26T14:27:36\",\n" +
            "        \"location\": \"https://localhost:9443/scim2/Groups/7bac6a86-1f21-4937-9fb1-5be4a93ef469\",\n" +
            "        \"lastModified\": \"2019-08-26T14:28:36\"\n" +
            "      },\n" +
            "      \"members\": [\n" +
            "        {\n" +
            "          \"$ref\": \"https://localhost:9443/scim2/Users/3a12bae9-4386-44be-befd-caf349297f45\",\n" +
            "          \"display\": \"kim\",\n" +
            "          \"value\": \"008bba85-451d-414b-87de-c03b5a1f4217\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"$ref\": \"https://localhost:9443/scim2/Users/3a12bae9-4386-44be-befd-caf349123445\",\n" +
            "          \"display\": \"Kris\",\n" +
            "          \"value\": \"008bba85-451d-414b-87de-c03b5a112347\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"id\": \"" + GROUP_ID + "\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String RESOURCE_STRING = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"\n" +
            "  ],\n" +
            "  \"startIndex\": 1,\n" +
            "  \"filter\": \"displayName eq manager\"\n" +
            "}";

    private GroupResourceManager groupResourceManager;
    private UserManager userManager;
    private MockedStatic<AbstractResourceManager> abstractResourceManager;

    @BeforeMethod
    public void setUp() {

        groupResourceManager = new GroupResourceManager();
        abstractResourceManager = Mockito.mockStatic(AbstractResourceManager.class);
        userManager = mock(UserManager.class);

        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
    }

    @AfterMethod
    public void tearDown() {

        abstractResourceManager.close();
    }

    private SCIMResponse getEncodeSCIMExceptionObject(AbstractCharonException exception) {

        JSONEncoder encoder = new JSONEncoder();
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(exception.getStatus(), encoder.encodeSCIMException(exception), responseHeaders);
    }

    private Group getNewGroup() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        return decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
    }

    @DataProvider(name = "dataForGetGroupSuccess")
    public Object[][] dataToGetGroupSuccess() throws CharonException, InternalErrorException, BadRequestException {

        Group group = getNewGroup();
        String id = group.getId();

        return new Object[][]{
                {id, null, null, group},
                {id, null, "members", group},
                {id, "displayName", null, group},
                {id, "displayName", "members", group},
        };
    }

    @Test(dataProvider = "dataForGetGroupSuccess")
    public void testGetGroupSuccess(String id, String attributes, String excludeAttributes, Object objectGroup)
            throws CharonException, NotImplementedException, BadRequestException, NotFoundException {

        Group group = (Group) objectGroup;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        Mockito.when(userManager.getGroup(id, requiredAttributes)).thenReturn(group);
        SCIMResponse scimResponse = groupResourceManager.get(id, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        if (attributes != null) {
            Assert.assertTrue(obj.has(attributes));
        }
        if (excludeAttributes != null) {
            Assert.assertFalse(obj.has(excludeAttributes));
        }
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @Test
    public void testGetGroupSuccessSpecial()
            throws BadRequestException, CharonException, InternalErrorException,
            NotImplementedException, NotFoundException {

        Group group = getNewGroup();
        String id = group.getId();
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), "", "");

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        Mockito.when(userManager.getGroup(id, requiredAttributes)).thenReturn(group);
        SCIMResponse scimResponse = groupResourceManager.get(id, userManager, "", "");
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        Assert.assertFalse(obj.has("displayNames"));
        Assert.assertFalse(obj.has("meta"));
        Assert.assertFalse(obj.has("members"));
        Assert.assertTrue(obj.has("id"));
    }

    @DataProvider(name = "dataForGetGroupExceptions")
    public Object[][] dataToGetGroupExceptions() {

        return new Object[][]{
                {"123", "members", null},
                {"123", "members", "meta"}
        };
    }

    @Test(dataProvider = "dataForGetGroupExceptions")
    public void testGetGroupNotFoundException(String id, String attributes, String excludeAttributes)
            throws CharonException, NotImplementedException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        Mockito.when(userManager.getGroup(id, requiredAttributes)).thenReturn(null);
        SCIMResponse scimResponse = groupResourceManager.get(id, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);
    }

    @Test(dataProvider = "dataForGetGroupExceptions")
    public void testGetUserCharonException(String id, String attributes, String excludeAttributes)
            throws CharonException, NotImplementedException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.getGroup(id, requiredAttributes)).thenThrow(CharonException.class);
        SCIMResponse scimResponse = groupResourceManager.get(id, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestCreateGroupSuccess")
    public Object[][] dataToTestCreateGroupSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        Group group = getNewGroup();

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "id", null, group}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupSuccess")
    public void testCreateGroupSuccess(String scimObjectString, String attributes,
                                       String excludeAttributes, Object objectGroup)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        Group group = (Group) objectGroup;

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenReturn(group);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = "null/" + obj.getString("id");
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_CREATED);
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForTestCreateGroupNewlyCreatedGroupResourceIsNull")
    public Object[][] dataToTestCreateGroupNewlyCreatedGroupResourceIsNull() {

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "id", null}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupNewlyCreatedGroupResourceIsNull")
    public void testCreateGroupNewlyCreatedGroupResourceIsNull(String scimObjectString,
                                                               String attributes, String excludeAttributes)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenReturn(null);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestCreateGroupBadRequestException")
    public Object[][] dataToTestCreatGroupBadRequestException() {

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "id", null}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupBadRequestException")
    public void testCreateGroupBadRequestException(String scimObjectString, String attributes,
                                                   String excludeAttributes)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_BAD_REQUEST);
    }

    @DataProvider(name = "dataForTestCreateGroupConflictException")
    public Object[][] dataToTestCreatGroupConflictException() {

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "id", null}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupConflictException")
    public void testCreateGroupConflictException(String scimObjectString, String attributes, String excludeAttributes)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new ConflictException()));
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenThrow(ConflictException.class);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_CONFLICT);
    }

    @DataProvider(name = "dataForTestCreateGroupCharonException")
    public Object[][] dataToTestCreateGroupCharonException() {

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "userName", null}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupCharonException")
    public void testCreateGroupCharonException(String scimObjectString, String attributes, String excludeAttributes)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenThrow(CharonException.class);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestCreateGroupNotImplementedException")
    public Object[][] dataToTestCreateGroupNotImplementedException() {

        return new Object[][]{
                {NEW_GROUP_SCIM_OBJECT_STRING, "userName", null}
        };
    }

    @Test(dataProvider = "dataForTestCreateGroupNotImplementedException")
    public void testCreateGroupNotImplementedException(String scimObjectString, String attributes,
                                                       String excludeAttributes)
            throws ConflictException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));
        Mockito.when(userManager.createGroup(any(Group.class), anyMap())).thenThrow(NotImplementedException.class);
        SCIMResponse scimResponse = groupResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NOT_IMPLEMENTED);
    }

    @Test
    public void testDeleteGroupSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        Group group = getNewGroup();
        String id = group.getId();

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        SCIMResponse scimResponse = groupResourceManager.delete(id, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NO_CONTENT);
    }

    @DataProvider(name = "dataForTestDeleteGroupFails")
    public Object[][] dataToTestDeleteGroupFails()
            throws BadRequestException, CharonException, InternalErrorException {

        Group group = getNewGroup();
        String id = group.getId();
        return new Object[][]{
                {id, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {id, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND},
        };
    }

    @Test(dataProvider = "dataForTestDeleteGroupFails")
    public void testDeleteGroupFails(String id, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);

        if (expectedScimResponseStatus == ResponseCodeConstants.CODE_INTERNAL_ERROR) {

            abstractResourceManager
                    .when(() -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
            SCIMResponse scimResponse = groupResourceManager.delete(id, null);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else {

            doThrow(new NotFoundException()).when(userManager).deleteGroup(id);
            abstractResourceManager.when(() ->
                    AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
            SCIMResponse scimResponse = groupResourceManager.delete(id, userManager);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
        }
    }

    @Test
    public void testDeleteGroupCharonExceptionOnly()
            throws NotFoundException, NotImplementedException, BadRequestException,
            CharonException, InternalErrorException {

        Group group = getNewGroup();
        String id = group.getId();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        doThrow(new CharonException()).when(userManager).deleteGroup(id);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        SCIMResponse scimResponse = groupResourceManager.delete(id, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTSuccess")
    public Object[][] dataToTestUpdateGroupWithPUTSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        Group groupNew = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, schema, new Group());
        return new Object[][]{
                {id, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupNew, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTSuccess")
    public void testUpdateGroupWithPUTSuccess(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewGroupObject, Object scimOldGroupObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        Group groupNew = (Group) scimNewGroupObject;
        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        Mockito.when(userManager.getGroup(id,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Group validatedGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject(groupOld, groupNew, schema);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap()))
                .thenReturn(validatedGroup);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        String returnedURI = scimResponse.getHeaderParamMap().get("Location");
        String expectedURI = "null/" + obj.getString("id");
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestUpdateGroupWithPUTProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String name = groupOld.getId();
        Group groupNew = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, schema, new Group());
        return new Object[][]{
                {name, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupNew, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTProvidedUserManagerHandlerIsNull")
    public void testUpdateGroupWithPUTProvidedUserManagerHandlerIsNull(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewGroupObject, Object scimOldGroupObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        Group groupNew = (Group) scimNewGroupObject;
        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Group validatedGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject(groupOld, groupNew, schema);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap())).thenReturn(validatedGroup);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, null,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTUpdatedGroupResourceIsNull")
    public Object[][] dataToTestUpdateWithGroupPUTUpdatedGroupResourceIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String name = groupOld.getId();

        return new Object[][]{
                {name, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTUpdatedGroupResourceIsNull")
    public void testUpdateGroupWithPUTUpdatedGroupResourceIsNull(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimOldGroupObject)
            throws NotImplementedException, BadRequestException, NotFoundException, CharonException {

        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap())).thenReturn(null);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTNotFoundException")
    public Object[][] dataToTestUpdateGroupWithPUTNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        Group groupNew = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, schema, new Group());
        return new Object[][]{
                {id, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupNew, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTNotFoundException")
    public void testUpdateGroupWithPUTNoUserExistsWithTheGivenUserName(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewGroupObject, Object scimOldGroupObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        Group groupNew = (Group) scimNewGroupObject;
        Group groupOld = (Group) scimOldGroupObject;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);
        Group validatedGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject(groupOld, groupNew, schema);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap())).thenReturn(validatedGroup);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTCharonException")
    public Object[][] dataToTestUpdateGroupWithPUTCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        return new Object[][]{
                {id, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTCharonException")
    public void testUpdateWithPUTCharonException(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimOldGroupObject)
            throws NotImplementedException, BadRequestException, NotFoundException, CharonException {

        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class),
                anyMap())).thenThrow(CharonException.class);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTBadRequestException")
    public Object[][] dataToTestUpdateGroupWithPUTBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        return new Object[][]{
                {id, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTBadRequestException")
    public void testUpdateGroupWithPUTBadRequestException(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimOldGroupObject)
            throws CharonException, NotImplementedException, BadRequestException, NotFoundException {

        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap()))
                .thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_BAD_REQUEST);
    }

    @DataProvider(name = "dataForTestUpdateGroupWithPUTNotImplementedException")
    public Object[][] dataToTestUpdateGroupWithPUTNotImplementedException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        return new Object[][]{
                {id, NEW_GROUP_SCIM_OBJECT_STRING_UPDATED, "id", null, groupOld}
        };
    }

    @Test(dataProvider = "dataForTestUpdateGroupWithPUTNotImplementedException")
    public void testUpdateGroupWithPUTNotImplementedException(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimOldGroupObject)
            throws NotImplementedException, BadRequestException, NotFoundException, CharonException {

        Group groupOld = (Group) scimOldGroupObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/" + GROUP_ID);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));
        Mockito.when(userManager.getGroup(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(groupOld);
        Mockito.when(userManager.updateGroup(any(Group.class), any(Group.class), anyMap()))
                .thenThrow(NotImplementedException.class);
        SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NOT_IMPLEMENTED);
    }

    @DataProvider(name = "dataForUpdateWithPATCH")
    public Object[][] dataToUpdateWithPATCH() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        Group groupNew = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new Group());
        return new Object[][]{
                {id, SCIM2_PATCH_REQUEST_STRING, "userName", "emails", groupNew, groupNew},
                {id, SCIM2_PATCH_REQUEST_STRING, "userName", null, groupNew, groupNew},
                {id, SCIM2_PATCH_REQUEST_STRING, null, "emails", groupNew, groupNew},
                {id, SCIM2_PATCH_REQUEST_STRING, null, null, groupNew, groupNew}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCH")
    public void testUpdateWithPATCH(String existingId, String patchRequest, String attributes,
                                    String excludeAttributes, Object scimNewGroupObject, Object scimOldGroupObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        Group groupNew = (Group) scimNewGroupObject;
        Group groupOld = (Group) scimOldGroupObject;
        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        Mockito.when(userManager.getGroup(anyString(), anyMap())).thenReturn(groupOld);
        Mockito.when(userManager.patchGroup(anyString(), anyString(), anyMap(), anyMap())).thenReturn(groupNew);
        SCIMResponse scimResponse = groupResourceManager.updateWithPATCH(existingId, patchRequest,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @DataProvider(name = "dataForUpdateWithPATCHOverride")
    public Object[][] dataToUpdateWithPATCHOverride()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        Group groupOld = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING, schema, new Group());
        String id = groupOld.getId();
        Group groupNew = decoder.decodeResource(NEW_GROUP_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new Group());
        return new Object[][]{
                {id, SCIM2_PATCH_REQUEST_STRING, groupNew, groupNew}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHOverride")
    public void testUpdateWithPATCHOverride(String existingId, String patchRequest,
                                            Object scimNewGroupObject, Object scimOldGroupObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        Group groupNew = (Group) scimNewGroupObject;
        Group groupOld = (Group) scimOldGroupObject;

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT);
        Mockito.when(userManager.getGroup(anyString(), anyMap())).thenReturn(groupOld);
        Mockito.when(userManager.patchGroup(anyString(), anyString(), anyMap(), anyMap())).thenReturn(groupNew);
        SCIMResponse scimResponse = groupResourceManager.updateWithPATCH(existingId, patchRequest, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NO_CONTENT);
    }

    @DataProvider(name = "dataForListWithGET")
    public Object[][] dataToListWithGET() {

        return new Object[][]{
                {null, 1, 2, null, null, "PRIMARY", "members", null},
                {"displayName sw PRIMARY/manager", 1, 2, null, null, "PRIMARY", "displayName", "members"},
                {"displayName sw PRIMARY/manager", 1, 2, null, null, "PRIMARY", "displayName", null},
                {"displayName sw PRIMARY/manager", 1, 2, null, null, "PRIMARY", null, "members"},
                {"displayName sw PRIMARY/manager", 1, 2, null, null, "PRIMARY", null, null}
        };
    }

    @Test(dataProvider = "dataForListWithGET")
    public void testListWithGET(String filter, Integer startIndexInt, Integer countInt,
                                String sortBy, String sortOrder, String domainName,
                                String attributes, String excludeAttributes) {

        SCIMResponse scimResponse = groupResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @Test(dataProvider = "dataForListWithGET")
    public void testListWithGETProvidedUserManagerHandlerIsNull(String filter, Integer startIndexInt, Integer countInt,
                                                                String sortBy, String sortOrder, String domainName,
                                                                String attributes, String excludeAttributes) {

        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        SCIMResponse scimResponse = groupResourceManager.listWithGET(null, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @DataProvider(name = "dataForListWithPOST")
    public Object[][] dataToListWithPOST() throws BadRequestException, CharonException, InternalErrorException {

        List<Object> tempList = new ArrayList<>();
        tempList.add(1);
        tempList.add(getNewGroup());
        return new Object[][]{
                {RESOURCE_STRING, tempList}
        };
    }

    @Test(dataProvider = "dataForListWithPOST")
    public void testListWithPOST(String resourceString, List<Object> tempList)
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/.search");
        Mockito.when(userManager.listGroupsWithPost(any(SearchRequest.class), anyMap())).thenReturn(tempList);
        SCIMResponse scimResponse = groupResourceManager.listWithPOST(resourceString, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @Test
    public void testListWithPOSTCharonException()
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.listGroupsWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new CharonException());
        SCIMResponse scimResponse = groupResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @Test
    public void testListWithPOSTNotImplementedException()
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));
        Mockito.when(userManager.listGroupsWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new NotImplementedException());
        SCIMResponse scimResponse = groupResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NOT_IMPLEMENTED);
    }

    @Test
    public void testListWithPOSTBadRequestException()
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_GROUP_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.listGroupsWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new BadRequestException());
        SCIMResponse scimResponse = groupResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_BAD_REQUEST);
    }
}
