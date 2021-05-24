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
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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
                                   String excludeAttributes, Object objectUser) throws CharonException {

        User user = (User) objectUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> userManager.getMe(name, requiredAttributes)).thenReturn(user);

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
            throws CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        abstractResourceManager.when(() -> userManager.getMe(name, requiredAttributes)).thenReturn(null);

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
            throws CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        abstractResourceManager.when(() -> userManager.getMe(name, requiredAttributes))
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
            throws CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        abstractResourceManager.when(()
                -> userManager.getMe(name, requiredAttributes)).thenThrow(BadRequestException.class);

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
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());

        abstractResourceManager.when(() -> userManager.createMe(any(User.class), any(Map.class))).thenReturn(user);

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
                                                           int expectedScimResponseStatus) {

        User user = (User) objectUser;

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        abstractResourceManager.when(() -> userManager.createMe(any(User.class), any(Map.class))).thenReturn(user);

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
                                              String excludeAttributes, int expectedScimResponseStatus) {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        abstractResourceManager.when(() -> userManager.createMe(any(User.class), any(Map.class))).thenReturn(null);

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
                                              String excludeAttributes, int expectedScimResponseStatus) {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        abstractResourceManager.when(()
                -> userManager.createMe(any(User.class), any(Map.class))).thenThrow(BadRequestException.class);

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
                                                String excludeAttributes, int expectedScimResponseStatus) {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new ConflictException()));
        abstractResourceManager.when(() -> userManager.createMe(any(User.class), any(Map.class)))
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
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());

        abstractResourceManager.when(() -> userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager
                .encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        abstractResourceManager.when(() -> userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        abstractResourceManager.when(() -> userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            attributes, String excludeAttributes, Object objectOLDUser, int expectedScimResponseStatus) {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        abstractResourceManager.when(() -> userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        abstractResourceManager.when(() -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(null);

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
            attributes, String excludeAttributes, int expectedScimResponseStatus) {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        abstractResourceManager.when(() -> userManager.getMe(userName,
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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
            throws BadRequestException, CharonException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        abstractResourceManager.when(()
                -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(validatedUser);

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
                                                             Object objectOLDUser, int expectedScimResponseStatus) {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        abstractResourceManager.when(() -> userManager.updateMe(any(User.class), any(Map.class))).thenReturn(null);

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
                                                       int expectedScimResponseStatus) {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        abstractResourceManager.when(() -> userManager.getMe(existingId,
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
                                                          int expectedScimResponseStatus) {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        abstractResourceManager.when(() -> userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        SCIMResponse scimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }
}
