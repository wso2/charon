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
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.PatchOperationUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class of MeResourceManager.
 */
public class MeResourceManagerTest {

    private static final String NEW_USER_SCIM_OBJECT_STRING = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"john@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_USER_SCIM_OBJECT_STRING_UPDATE = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"john2@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            "  \"Operations\": [\n" +
            "    {\n" +
            "      \"op\": \"add\",\n" +
            "      \"value\": {\n" +
            "        \"nickName\": \"shaggy\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],   \n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"john@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            "  \"Operations\": [\n" +
            "    {\n" +
            "      \"op\": \"add\",\n" +
            "      \"value\": {\n" +
            "        \"nickName\": \"shaggy\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],   \n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"johnnew@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            "  \"Operations\": [\n" +
            "    {\n" +
            "      \"op\": \"replace\",\n" + //REPLACE Operation
            "      \"value\": {\n" +
            "        \"nickName\": \"shaggy\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],   \n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"john@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED = "{\n" +
            "  \"schemas\": \n" +
            "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "    ],\n" +
            "\"meta\": {\n" +
            "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"location\": \"ENDPOINT/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
            "    \"lastModified\": \"2018-08-17T10:34:29Z\",\n" +
            "    \"resourceType\": \"User\"\n" +
            "},\n" +
            "  \"Operations\": [\n" +
            "    {\n" +
            "      \"op\": \"replace\",\n" + //REPLACE Operation
            "      \"value\": {\n" +
            "        \"nickName\": \"shaggy\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],   \n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\"\n" +
            "  },\n" +
            "  \"userName\": \"kimjohn  \",\n" +
            "  \"password\": \"kim123\",\n" +
            "  \"id\": \"123\",\n" +
            "  \"emails\": [\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"value\": \"johnnew@gmail.com\",\n" +
            "         \"primary\": true\n" +
            "      }\n" +
            "  ]\n" +
            "}";

    private static final String SCIM2_ME_ENDPOINT = "https://localhost:9443/scim2/Me";

    private MeResourceManager meResourceManager;
    private UserManager userManager;
    private MockedStatic<AbstractResourceManager> abstractResourceManager;

    @BeforeMethod
    public void setUp() {

        meResourceManager = new MeResourceManager();
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

    private User getNewUser() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        return decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
    }

    @DataProvider(name = "dataForGetUserSuccess")
    public Object[][] dataToGetUserSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();
        String name = user.getUserName();

        return new Object[][]{
                {id, name, null, null, user},
                {id, name, null, "emails", user},
                {id, name, "username,meta", null, user},
                {id, name, "username", "emails", user}
        };
    }

    @Test(dataProvider = "dataForGetUserSuccess")
    public void testGetUserSuccess(String id, String name, String attributes,
                                   String excludeAttributes, Object objectUser)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        User user = (User) objectUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        when(userManager.getMe(name, requiredAttributes)).thenReturn(user);

        SCIMResponse scimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());

        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
        Assert.assertEquals(obj.getString("userName"), name);
        Assert.assertEquals(obj.getString("id"), id);

        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_ME_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
        if (attributes == null & excludeAttributes == null) {

            Assert.assertTrue(obj.has("emails"));

        } else if (attributes == null & excludeAttributes != null) {

            Assert.assertFalse(obj.has("emails"));

        }
    }

    @DataProvider(name = "dataForGetUserNotFoundException")
    public Object[][] dataToGetUserNotFoundException() {

        return new Object[][]{
                {"David", "userName", null},
                {"David", "userName", "emails"}
        };
    }

    @Test(dataProvider = "dataForGetUserNotFoundException")
    public void testGetUserNotFoundException(String name, String attributes, String excludeAttributes)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        when(userManager.getMe(name, requiredAttributes)).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);

        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);
    }

    @DataProvider(name = "dataForGetCharonException")
    public Object[][] dataToGetCharonException() {

        return new Object[][]{
                {"David", "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForGetCharonException")
    public void testGetUserCharonException(String name, String attributes, String excludeAttributes,
                                           int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        when(userManager.getMe(name, requiredAttributes))
                .thenThrow(CharonException.class);

        SCIMResponse scimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForGetBadRequestException")
    public Object[][] dataToGetCharonBadRequestException() {

        return new Object[][]{
                {"David", "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForGetBadRequestException")
    public void testGetUserBadRequestException(String name, String attributes, String excludeAttributes,
                                               int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        when(userManager.getMe(name, requiredAttributes)).thenThrow(BadRequestException.class);

        SCIMResponse scimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestCreateUserSuccess")
    public Object[][] dataToTestCreateUserSuccess() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null},
                {NEW_USER_SCIM_OBJECT_STRING, "userName", "emails"}
        };
    }

    @Test(dataProvider = "dataForTestCreateUserSuccess")
    public void testCreateUserSuccess(String scimObjectString, String attributes, String excludeAttributes)
            throws BadRequestException, CharonException, InternalErrorException, ConflictException,
            ForbiddenException, NotImplementedException {

        User user = getNewUser();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        when(userManager.createMe(any(User.class), anyMap())).thenReturn(user);

        SCIMResponse scimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());

        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_CREATED);

        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_ME_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestCreateProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null, user, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public void testCreateProvidedUserManagerHandlerIsNull(String scimObjectString, String attributes,
                                                           String excludeAttributes, Object objectUser,
                                                           int expectedScimResponseStatus)
            throws ConflictException, BadRequestException, CharonException, ForbiddenException,
            NotImplementedException {

        User user = (User) objectUser;

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        when(userManager.createMe(any(User.class), anyMap())).thenReturn(user);

        SCIMResponse scimResponse = meResourceManager.create(scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestCreatedUserResourceIsNull")
    public Object[][] dataToTestCreatedUserResourceIsNull() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestCreatedUserResourceIsNull")
    public void testCreatedUserResourceIsNull(String scimObjectString, String attributes,
                                              String excludeAttributes, int expectedScimResponseStatus)
            throws ConflictException, BadRequestException, CharonException, ForbiddenException,
            NotImplementedException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        when(userManager.createMe(any(User.class), anyMap())).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateBadRequestException")
    public Object[][] dataToTestCreatBadRequestException() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForTestCreateBadRequestException")
    public void testCreateBadRequestException(String scimObjectString, String attributes,
                                              String excludeAttributes, int expectedScimResponseStatus)
            throws ConflictException, BadRequestException, CharonException, ForbiddenException,
            NotImplementedException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        when(userManager.createMe(any(User.class), anyMap())).thenThrow(BadRequestException.class);

        SCIMResponse scimResponse = meResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestCreateUserConflictException")
    public Object[][] dataToTestCreatUserConflictException() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null, ResponseCodeConstants.CODE_CONFLICT}
        };
    }

    @Test(dataProvider = "dataForTestCreateUserConflictException")
    public void testCreateUserConflictException(String scimObjectString, String attributes,
                                                String excludeAttributes, int expectedScimResponseStatus)
            throws ConflictException, BadRequestException, CharonException, ForbiddenException,
            NotImplementedException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new ConflictException()));
        when(userManager.createMe(any(User.class), anyMap()))
                .thenThrow(ConflictException.class);

        SCIMResponse scimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestDeleteUserSuccess")
    public Object[][] dataToTestDeleteUserSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, ResponseCodeConstants.CODE_NO_CONTENT}
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserSuccess")
    public void testDeleteUserSuccess(String userName, int expectedScimResponseStatus) {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        SCIMResponse scimResponse = meResourceManager.delete(userName, userManager);

        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestDeleteUserFails")
    public Object[][] dataToTestDeleteUserFails()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {username, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND},
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserFails")
    public void testDeleteUserFails(String userName, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        if (expectedScimResponseStatus == ResponseCodeConstants.CODE_INTERNAL_ERROR) {

            abstractResourceManager.when(()
                    -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

            SCIMResponse scimResponse = meResourceManager.delete(userName, null);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else {

            doThrow(new NotFoundException()).when(userManager).deleteMe(userName);

            abstractResourceManager.when(()
                    -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

            SCIMResponse scimResponse = meResourceManager.delete(userName, userManager);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
        }
    }

    @DataProvider(name = "dataForTestDeleteUserFailsINCharonExceptionOnly")
    public Object[][] dataToTestDeleteUserCharonExceptionOnly()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserFailsINCharonExceptionOnly")
    public void testDeleteUserCharonExceptionOnly(String userName, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        doThrow(new CharonException()).when(userManager).deleteMe(userName);

        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        SCIMResponse scimResponse = meResourceManager.delete(userName, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForListWithGet")
    public Object[][] dataToListWithGet() {

        return new Object[][]{
                {null, 1, 2, null, null, "PRIMARY", "emails", null},
                {null, 1, 2, null, null, "PRIMARY", "userName", null},
                {null, 1, 2, null, null, "PRIMARY", "id", null}
        };
    }

    @Test(dataProvider = "dataForListWithGet")
    public void testListWithGet(String filter, int startIndexInt, int countInt,
                                String sortBy, String sortOrder, String domainName, String attributes,
                                String excludeAttributes) {

        SCIMResponse scimResponse = meResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);

        Assert.assertNull(scimResponse);
    }

    @Test
    public void testListWithPOST() {

        SCIMResponse scimResponse = meResourceManager.listWithPOST(null, userManager);
        Assert.assertNull(scimResponse);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTSuccess")
    public Object[][] dataToTestUpdateWithPUTSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());

        return new Object[][]{
                {name, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_OK}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTSuccess")
    public void testUpdateWithPUTSuccess(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser,
                                         Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());

        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_ME_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestUpdateWithPUTProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());

        return new Object[][]{
                {name, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null, userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public void testUpdateWithPUTProvidedUserManagerHandlerIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser,
                                                                  int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager
                .encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, null,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTNotFoundException")
    public Object[][] dataToTestUpdateWithPUTNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());

        return new Object[][]{
                {name, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTNotFoundException")
    public void testUpdateWithPUTNoUserExistsWithTheGivenUserName(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser,
                                                                  Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTCharonException")
    public Object[][] dataToTestUpdateWithPUTCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String name = userOld.getUserName();

        return new Object[][]{
                {name, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTCharonException")
    public void testUpdateWithPUTUpdatedUserResourceIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectOLDUser, int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTBadRequestException")
    public Object[][] dataToTestUpdateWithPUTBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String name = userOld.getUserName();

        return new Object[][]{
                {name, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTBadRequestException")
    public void testUpdateWithPUTBadRequestException(String userName, String scimObjectString, String
            attributes, String excludeAttributes, int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        SCIMResponse scimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCH")
    public Object[][] dataToUpdateWithPATCH() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new User());

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName", null, userNew,
                        userOld, ResponseCodeConstants.CODE_OK}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCH")
    public void testUpdateWithPATCH(String existingId, String scimObjectString,
                                    String attributes, String excludeAttributes, Object objectNEWUser,
                                    Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());

        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_ME_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForUpdateWithPATCHReplace")
    public Object[][] dataToUpdateWithPATCHReplace()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED,
                schema, new User());

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_OK}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHReplace")
    public void testUpdateWithPATCHReplace(String existingId, String scimObjectString,
                                           String attributes, String excludeAttributes, Object objectNEWUser,
                                           Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);

        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHProvidedUserManagerHandlerIsNull")
    public Object[][] dataToUpdateWithPATCHInProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new User());

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHProvidedUserManagerHandlerIsNull")
    public void testUpdateWithPATCHProvidedUserManagerHandlerIsNull(String existingId, String scimObjectString,
                                                                    String attributes, String excludeAttributes,
                                                                    Object objectNEWUser,
                                                                    Object objectOLDUser,
                                                                    int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHNotFoundException")
    public Object[][] dataToUpdateWithPATCHNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new User());

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHNotFoundException")
    public void testUpdateWithPATCHNoAssociatedUserExitsInTheUserStore(String existingId, String scimObjectString,
                                                                       String attributes, String excludeAttributes,
                                                                       Object objectNEWUser,
                                                                       Object objectOLDUser,
                                                                       int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(validatedUser);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHCharonException")
    public Object[][] dataToUpdateWithPATCHCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName",
                        null, userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHCharonException")
    public void testUpdateWithPATCHUpdatedUserResourceIsNull(String existingId, String scimObjectString,
                                                             String attributes, String excludeAttributes,
                                                             Object objectOLDUser, int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(any(User.class), anyMap())).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHBadRequestException")
    public Object[][] dataToUpdateWithPATCHBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName",
                        null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHBadRequestException")
    public void testUpdateWithPATCHBadRequestException(String existingId, String scimObjectString,
                                                       String attributes, String excludeAttributes,
                                                       int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHInternalErrorException")
    public Object[][] dataToUpdateWithPATCHInternalErrorException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE,
                        "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHInternalErrorException")
    public void testUpdateWithPATCHInternalErrorException(String existingId, String scimObjectString,
                                                          String attributes, String excludeAttributes,
                                                          int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestGetName")
    public Object[][] dataToTestGetUsername()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();

        return new Object[][]{
                {user, NEW_USER_SCIM_OBJECT_STRING}
        };
    }

    @Test(dataProvider = "dataForTestGetName")
    public void testGetUserName(Object objectUser, String scimObjectString)
            throws CharonException {

        User user = (User) objectUser;
        String scimResponse = meResourceManager.getUserName(scimObjectString);
        Assert.assertEquals(user.getUserName(), scimResponse);
    }

    @DataProvider(name = "dataForTestGetNameErrorInGettingTheUsernameFromTheAnonymousRequest")
    public Object[][] dataToTestGetUsernameErrorInGettingTheUsernameFromTheAnonymousRequest() {

        String scimObjectString = "{\n" +
                "InvalidUserName: John,\n" +
                "}";
        return new Object[][]{
                {scimObjectString}
        };
    }

    @Test(dataProvider = "dataForTestGetNameErrorInGettingTheUsernameFromTheAnonymousRequest")
    public void testGetUserNameErrorInGettingTheUsernameFromTheAnonymousRequest(String scimObjectString)
            throws CharonException {

        String scimResponse = meResourceManager.getUserName(scimObjectString);
        Assert.assertNull(scimResponse);
    }

    @DataProvider(name = "syncedAttributeCases")
    public Object[][] provideSyncedAttributeCases() {

        SCIMAttributeSchema countrySchema = createSCIMAttributeSchema(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:country", "country");
        SCIMAttributeSchema regionSchema = createSCIMAttributeSchema("urn:scim:wso2:schema:region", "region");
        SCIMAttributeSchema addressStreetSchema = createSCIMAttributeSchema(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:address.street",
                "address.street");
        SCIMAttributeSchema locationStreetSchema = createSCIMAttributeSchema(
                "urn:scim:wso2:schema:location.street", "location.street");

        User oldUserWithoutCountry = new User();
        oldUserWithoutCountry.replaceUsername("testuser");
        oldUserWithoutCountry.replaceUserType("employee");
        oldUserWithoutCountry.replacePreferredLanguage("en");
        oldUserWithoutCountry.replaceDisplayName("Test User");

        Map<String, String> case1Attributes = new HashMap<>();
        case1Attributes.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:country",
                "urn:scim:wso2:schema:region");
        case1Attributes.put("urn:scim:wso2:schema:region",
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:country");

        List<Object[]> testCases = new ArrayList<>();

        List<Map<String, String>> operations1 = new ArrayList<>();
        Map<String, String> operation1 = new HashMap<>();
        operation1.put("operationType", "add");
        operation1.put("schemaPrefix", "enterprise");
        operation1.put("attribute", "country");
        operation1.put("value", "Sri Lanka");
        operations1.add(operation1);
        testCases.add(new Object[]{ case1Attributes, operations1, 1, 0, oldUserWithoutCountry });

        List<Map<String, String>> operations2 = new ArrayList<>();
        Map<String, String> operation2a = new HashMap<>();
        operation2a.put("operationType", "add");
        operation2a.put("schemaPrefix", "enterprise");
        operation2a.put("attribute", "country");
        operation2a.put("value", "Sri Lanka");
        operations2.add(operation2a);

        Map<String, String> operation2b = new HashMap<>();
        operation2b.put("operationType", "add");
        operation2b.put("schemaPrefix", "wso2");
        operation2b.put("attribute", "region");
        operation2b.put("value", "Sri Lanka");
        operations2.add(operation2b);
        testCases.add(new Object[]{ case1Attributes, operations2, 1, 0, oldUserWithoutCountry });

        User oldUserWithBothAttributes = new User();
        oldUserWithBothAttributes.replaceUsername("testuser");
        oldUserWithBothAttributes.replaceSimpleAttribute(countrySchema, "Sri Lanka");
        oldUserWithBothAttributes.replaceSimpleAttribute(regionSchema, "Sri Lanka");

        List<Map<String, String>> operations3 = new ArrayList<>();
        Map<String, String> operation3 = new HashMap<>();
        operation3.put("operationType", "replace");
        operation3.put("schemaPrefix", "enterprise");
        operation3.put("attribute", "country");
        operation3.put("value", "USA");
        operations3.add(operation3);
        testCases.add(new Object[]{ case1Attributes, operations3, 1, 0, oldUserWithBothAttributes });

        Map<String, String> case4Attributes = new HashMap<>();
        case4Attributes.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:address.street",
                "urn:scim:wso2:schema:location.street");
        case4Attributes.put("urn:scim:wso2:schema:location.street",
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:address.street");

        User oldUserWithSubAttribute = new User();
        oldUserWithSubAttribute.replaceUsername("testuser");
        oldUserWithSubAttribute.replaceSimpleAttribute(countrySchema, "Sri Lanka");
        oldUserWithSubAttribute.replaceSimpleAttribute(regionSchema, "Asia");
        oldUserWithSubAttribute.replaceSimpleAttribute(addressStreetSchema, "456 Old Street");
        oldUserWithSubAttribute.replaceSimpleAttribute(locationStreetSchema, "456 Old Street");

        List<Map<String, String>> operations4 = new ArrayList<>();
        Map<String, String> operation4 = new HashMap<>();
        operation4.put("operationType", "replace");
        operation4.put("schemaPrefix", "enterprise");
        operation4.put("attribute", "address.street");
        operation4.put("value", "123 New Street");
        operations4.add(operation4);
        testCases.add(new Object[]{ case4Attributes, operations4, 0, 1, oldUserWithSubAttribute });

        User oldUserWithRemovableAttribute = new User();
        oldUserWithRemovableAttribute.replaceUsername("testuser");
        oldUserWithRemovableAttribute.replaceSimpleAttribute(countrySchema, "Sri Lanka");
        oldUserWithRemovableAttribute.replaceSimpleAttribute(regionSchema, "Sri Lanka");

        List<Map<String, String>> operations5 = new ArrayList<>();
        Map<String, String> operation5 = new HashMap<>();
        operation5.put("operationType", "remove");
        operation5.put("schemaPrefix", "enterprise");
        operation5.put("attribute", "country");
        operations5.add(operation5);

        testCases.add(new Object[]{ case1Attributes, operations5, 1, 0, oldUserWithRemovableAttribute });

        return testCases.toArray(new Object[0][]);
    }

    @Test(dataProvider = "syncedAttributeCases")
    void testSyncedAttributesAreDeletedCorrectly(
            Map<String, String> syncedAttributes,
            List<Map<String, String>> operations,
            int expectedDeleteSubAttributeCalls,
            int expectedDeleteSubSubAttributeCalls,
            User oldUser
    ) throws Exception {

        MockedStatic<ResourceManagerUtil> mockedResourceManagerUtil = Mockito.mockStatic(ResourceManagerUtil.class);
        mockedResourceManagerUtil.when(() -> ResourceManagerUtil.getAllAttributeURIs(any())).thenReturn(null);
        MockedStatic<PatchOperationUtil> mockedPatchOperationUtil = Mockito.mockStatic(PatchOperationUtil.class,
                Mockito.CALLS_REAL_METHODS);

        when(userManager.getSyncedUserAttributes()).thenReturn(syncedAttributes);
        when(userManager.getMe(anyString(), any())).thenReturn(oldUser);

        String validScimObjectString;
        if (!operations.isEmpty()) {
            validScimObjectString = generateValidScimObjectString(operations);
        } else {
            throw new IllegalArgumentException("Operations list cannot be empty");
        }
        User newUser = mock(User.class);
        mockedPatchOperationUtil.when(() -> PatchOperationUtil.doPatchAdd(any(), any(), any(), any(), any()))
                .thenReturn(newUser);
        mockedPatchOperationUtil.when(() -> PatchOperationUtil.doPatchReplace(any(), any(), any(), any(), any()))
                .thenReturn(newUser);
        mockedPatchOperationUtil.when(() -> PatchOperationUtil.doPatchRemove(any(), any(), any(), any()))
                .thenReturn(newUser);

        when(userManager.getMe(anyString(), any())).thenReturn(oldUser);

        meResourceManager.updateWithPATCH("12345", validScimObjectString, userManager, null, null);

        verify(newUser, times(expectedDeleteSubAttributeCalls)).deleteSubAttribute(anyString(), anyString());
        verify(newUser, times(expectedDeleteSubSubAttributeCalls))
                .deleteSubSubAttribute(anyString(), anyString(), anyString());

        mockedResourceManagerUtil.close();
        mockedPatchOperationUtil.close();
    }

    private static String generateValidScimObjectString(List<Map<String, String>> operations) {
        StringBuilder operationsJson = new StringBuilder("{ \"Operations\": [");

        for (int i = 0; i < operations.size(); i++) {
            Map<String, String> operation = operations.get(i);
            String op = operation.get("operationType");
            String schemaPrefix = operation.get("schemaPrefix");
            String attribute = operation.get("attribute");
            String value = operation.get("value");

            String schema = schemaPrefix.equals("wso2")
                    ? "urn:scim:wso2:schema" : "urn:ietf:params:scim:schemas:extension:" + schemaPrefix + ":2.0:User";

            if (i > 0) {
                operationsJson.append(", ");
            }

            if ("remove".equals(op)) {
                operationsJson.append("{ \"op\": \"").append(op).append("\", \"path\": \"")
                        .append(schema).append(":").append(attribute).append("\" }");
            } else {
                operationsJson.append("{ \"op\": \"").append(op).append("\", \"value\": { \"")
                        .append(schema).append("\": { \"").append(attribute).append("\": ")
                        .append(value == null ? "\"\"" : "\"" + value + "\"")
                        .append(" } } } ");
            }
        }
        operationsJson.append(" ] } ");
        return operationsJson.toString();
    }

    private SCIMAttributeSchema createSCIMAttributeSchema(String uri, String name) {

        return SCIMAttributeSchema.createSCIMAttributeSchema(
                uri, name, SCIMDefinitions.DataType.STRING, false, "Description", false, false,
                SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                SCIMDefinitions.Uniqueness.NONE, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
    }
}
