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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class of MeResourceManager.
 */
@PrepareForTest({AbstractResourceManager.class})
public class MeResourceManagerTest extends PowerMockTestCase {

    MeResourceManager meResourceManager;
    UserManager userManager;

    @BeforeMethod
    public void setUp() {

        meResourceManager = new MeResourceManager();
        userManager = mock(UserManager.class);
    }

    @AfterMethod
    public void tearDown() {

    }

    private String getNewUserSCIMObjectString() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private String getNewUserSCIMObjectStringUpdated() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private String getNewUserSCIMStringObjectForPATCH() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private String getNewUserSCIMStringObjectForPATCHUpdate() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private String getNewUserSCIMStringObjectForPATCHReplace() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private String getNewUserSCIMStringObjectForPATCHUpdateReplace() {

        return "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "    ],\n" +
                "\"meta\": {\n" +
                "    \"created\": \"2018-08-17T10:34:29Z\",\n" +
                "    \"location\": \"https://localhost:9443/scim2/Me/008bba85-451d-414b-87de-c03b5a1f4217\",\n" +
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
    }

    private User getNewUser() throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        return decoder.decodeResource(scimObjectString, schema, new User());
    }

    @DataProvider(name = "dataForGetSuccess")
    public Object[][] dataToGetSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();
        String name = user.getUserName();

        return new Object[][]{
                {id, name, "userName", null, 200, user}
        };
    }

    @Test(dataProvider = "dataForGetSuccess")
    public void testGetSuccess(String id, String name, String attributes,
                               String excludeAttributes, int expectedScimResponseStatus, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException {

        User user = (User) objectUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(userManager.getMe(name, requiredAttributes)).thenReturn(user);

        SCIMResponse outputScimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
        Assert.assertEquals(obj.getString("userName"), name);
        Assert.assertEquals(obj.getString("id"), id);

    }

    @DataProvider(name = "dataForGetException")
    public Object[][] dataToGetExceptions() {

        return new Object[][]{
                {"Obama", "userName", null}
        };
    }

    @Test(dataProvider = "dataForGetException")
    public void testGetUserNotFoundException(String name, String attributes, String excludeAttributes)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenThrow(NotFoundException.class);
        when(userManager.getMe(name, requiredAttributes)).thenReturn(null);

        //Assertions
        Assert.assertThrows(NotFoundException.class, () -> {
            meResourceManager.get(name, userManager, attributes, excludeAttributes);
        });
    }

    @Test(dataProvider = "dataForGetException")
    public void testGetUserCharonException(String name, String attributes, String excludeAttributes)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenThrow(CharonException.class);
        when(userManager.getMe(name, requiredAttributes)).thenThrow(CharonException.class);

        //Assertions
        Assert.assertThrows(CharonException.class, () -> {
            meResourceManager.get(name, userManager, attributes, excludeAttributes);
        });
    }

    @Test(dataProvider = "dataForGetException")
    public void testGetUserBadRequestException(String name, String attributes, String excludeAttributes)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenThrow(BadRequestException.class);
        when(userManager.getMe(name, requiredAttributes)).thenThrow(BadRequestException.class);

        //Assertions
        Assert.assertThrows(BadRequestException.class, () -> {
            meResourceManager.get(name, userManager, attributes, excludeAttributes);
        });
    }

    @DataProvider(name = "dataForTestCreateSuccess")
    public Object[][] dataToTestCreateSuccess() throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        User user = getNewUser();

        return new Object[][]{
                {scimObjectString, "userName", null, user, 201}
        };
    }

    @Test(dataProvider = "dataForTestCreateSuccess")
    public void testCreateSuccess(String scimObjectString, String attributes, String excludeAttributes,
                                  Object objectUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        User user = (User) objectUser;
        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.createMe(anyObject(), anyObject())).thenReturn(user);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    //InternalErrorException
    @DataProvider(name = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestCreateProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        User user = getNewUser();

        return new Object[][]{
                {scimObjectString, "userName", null, user}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public void testCreateProvidedUserManagerHandlerIsNull(String scimObjectString, String attributes,
                                                           String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        User user = (User) objectUser;
        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenReturn(user);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.create(scimObjectString,
                    null, attributes, excludeAttributes);
        });

    }

    //InternalErrorException
    @DataProvider(name = "dataForTestCreateNewlyCreatedUserResourceIsNull")
    public Object[][] dataToTestCreateNewlyCreatedUserResourceIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        User user = getNewUser();

        return new Object[][]{
                {scimObjectString, "userName", null, user}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestCreateNewlyCreatedUserResourceIsNull")
    public void testCreateNewlyCreatedUserResourceIsNull(String scimObjectString, String attributes,
                                                         String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        //user variable is not used here
        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenReturn(null);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.create(scimObjectString, userManager, attributes,
                    excludeAttributes);
        });

    }

    @DataProvider(name = "dataForTestCreateExceptions")
    public Object[][] dataToTestCreatExceptions()
            throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        User user = getNewUser();

        return new Object[][]{
                {scimObjectString, "userName", null, user}
        };
    }

    @Test(dataProvider = "dataForTestCreateExceptions")
    public void testCreateBadRequestException(String scimObjectString, String attributes,
                                              String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        //user variable is not used here
        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenThrow(BadRequestException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(BadRequestException.class);

        //Assertions
        Assert.assertThrows(BadRequestException.class, () -> {
            meResourceManager.create(scimObjectString, userManager, attributes,
                    excludeAttributes);
        });

    }

    @Test(dataProvider = "dataForTestCreateExceptions")
    public void testCreateConflictException(String scimObjectString, String attributes,
                                            String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        //user variable is not used here
        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenThrow(ConflictException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(ConflictException.class);

        //Assertions
        Assert.assertThrows(ConflictException.class, () -> {
            meResourceManager.create(scimObjectString, userManager, attributes,
                    excludeAttributes);
        });

    }

    @Test(dataProvider = "dataForTestCreateExceptions")
    public void testCreateNotFoundException(String scimObjectString, String attributes,
                                            String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        //user variable is not used here
        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenThrow(NotFoundException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(NotFoundException.class);

        //Assertions
        Assert.assertThrows(NotFoundException.class, () -> {
            meResourceManager.create(scimObjectString, userManager, attributes,
                    excludeAttributes);
        });

    }

    @Test(dataProvider = "dataForTestCreateExceptions")
    public void testCreateCharonException(String scimObjectString, String attributes,
                                          String excludeAttributes, Object objectUser)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        //user variable is not used here
        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenThrow(CharonException.class);
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(CharonException.class);

        //Assertions
        Assert.assertThrows(CharonException.class, () -> {
            meResourceManager.create(scimObjectString, userManager, attributes,
                    excludeAttributes);
        });

    }

    @DataProvider(name = "dataForTestDeleteSuccess")
    public Object[][] dataToTestDeleteSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, 204}
        };
    }

    @Test(dataProvider = "dataForTestDeleteSuccess")
    public void testDeleteSuccess(String userName, int expectedScimResponseStatus)
            throws NotFoundException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");

        SCIMResponse outputScimResponse = meResourceManager.delete(userName, userManager);

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestDeleteFails")
    public Object[][] dataToTestDeleteFails()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username}
        };
    }

    @Test(dataProvider = "dataForTestDeleteFails")
    public void testDeleteInternalErrorException(String userName) throws NotFoundException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.delete(userName, null);
        });
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

        SCIMResponse outputScimResponse = meResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);

        Assert.assertNull(outputScimResponse);
    }

    @DataProvider(name = "dataForTestListWithPost")
    public Object[][] dataToTestListWithPost() {

        return new Object[][]{
                {null},
        };
    }

    @Test(dataProvider = "dataForTestListWithPost")
    public void testListWithPOST(String resourceString) {

        SCIMResponse outputScimResponse = meResourceManager.listWithPOST(resourceString, userManager);
        Assert.assertNull(outputScimResponse);
    }

    @DataProvider(name = "dataForTestUpdateWithPUT")
    public Object[][] dataToTestUpdateWithPUT()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        String scimObjectStringOld = getNewUserSCIMObjectString();

        User userOld = decoder.decodeResource(scimObjectStringOld, schema, new User());
        String name = userOld.getUserName();

        String scimObjectStringNew = getNewUserSCIMObjectStringUpdated();

        User userNew = decoder.decodeResource(scimObjectStringNew, schema, new User());

        return new Object[][]{
                {name, scimObjectStringNew, "userName", null, userNew, userOld, 200}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUT")
    public void testUpdateWithPUTSuccess(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser,
                                         Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTExceptions")
    public Object[][] dataToTestUpdateWithPUTExceptions()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        String scimObjectStringOld = getNewUserSCIMObjectString();

        User userOld = decoder.decodeResource(scimObjectStringOld, schema, new User());
        String name = userOld.getUserName();

        String scimObjectStringNew = getNewUserSCIMObjectStringUpdated();

        User userNew = decoder.decodeResource(scimObjectStringNew, schema, new User());

        return new Object[][]{
                {name, scimObjectStringNew, "userName", null, userNew, userOld}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestUpdateWithPUTExceptions")
    public void testUpdateWithPUTProvidedUserManagerHandlerIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.updateWithPUT(userName, scimObjectString, null,
                    attributes, excludeAttributes);
        });

    }

    //NotFoundException
    @Test(dataProvider = "dataForTestUpdateWithPUTExceptions")
    public void testUpdateWithPUTNoUserExistsWithTheGivenUserName(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenThrow(NotFoundException.class);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        //Assertions
        Assert.assertThrows(NotFoundException.class, () -> {
            meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                    attributes, excludeAttributes);
        });

    }

    // CharonException
    @Test(dataProvider = "dataForTestUpdateWithPUTExceptions")
    public void testUpdateWithPUTUpdatedUserResourceIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        // userNew variable is not used here, since new user is null for this testcase
        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenThrow(CharonException.class);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(null);

        //Assertions
        Assert.assertThrows(CharonException.class, () -> {
            meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                    attributes, excludeAttributes);
        });

    }

    //NotImplementedException
    @Test(dataProvider = "dataForTestUpdateWithPUTExceptions")
    public void testUpdateWithPUTNotImplementedException(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        // userNew, userOld variable is not used here, since new user is null for this testcase
        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenThrow(NotImplementedException.class);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(NotImplementedException.class);

        //Assertions
        Assert.assertThrows(NotImplementedException.class, () -> {
            meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                    attributes, excludeAttributes);
        });

    }

    // BadRequestException
    @Test(dataProvider = "dataForTestUpdateWithPUTExceptions")
    public void testUpdateWithPUTBadRequestException(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        // userNew, userOld variable is not used here, since new user is null for this testcase
        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenThrow(BadRequestException.class);

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        //Assertions
        Assert.assertThrows(BadRequestException.class, () -> {
            meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                    attributes, excludeAttributes);
        });

    }

    @DataProvider(name = "dataForUpdateWithPATCH")
    public Object[][] dataToUpdateWithPATCH() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        String scimObjectStringOld = getNewUserSCIMStringObjectForPATCH();

        User userOld = decoder.decodeResource(scimObjectStringOld, schema, new User());
        String id = userOld.getId();

        String scimObjectStringNew = getNewUserSCIMStringObjectForPATCHUpdate();

        User userNew = decoder.decodeResource(scimObjectStringNew, schema, new User());

        return new Object[][]{
                {id, scimObjectStringNew, "userName", null, userNew, userOld, 200}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCH")
    public void testUpdateWithPATCH(String existingId, String scimObjectString,
                                    String attributes, String excludeAttributes, Object objectNEWUser,
                                    Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHReplace")
    public Object[][] dataToUpdateWithPATCHReplace()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        String scimObjectStringOld = getNewUserSCIMStringObjectForPATCHReplace();

        User userOld = decoder.decodeResource(scimObjectStringOld, schema, new User());
        String id = userOld.getId();

        String scimObjectStringNew = getNewUserSCIMStringObjectForPATCHUpdateReplace();

        User userNew = decoder.decodeResource(scimObjectStringNew, schema, new User());

        return new Object[][]{
                {id, scimObjectStringNew, "userName", null, userNew, userOld, 200}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHReplace")
    public void testUpdateWithPATCHReplace(String existingId, String scimObjectString,
                                           String attributes, String excludeAttributes, Object objectNEWUser,
                                           Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHError")
    public Object[][] dataToUpdateWithPATCHError() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        String scimObjectStringOld = getNewUserSCIMStringObjectForPATCH();

        User userOld = decoder.decodeResource(scimObjectStringOld, schema, new User());
        String id = userOld.getId();

        String scimObjectStringNew = getNewUserSCIMStringObjectForPATCHUpdate();

        User userNew = decoder.decodeResource(scimObjectStringNew, schema, new User());

        return new Object[][]{
                {id, scimObjectStringNew, "userName", null, userNew, userOld}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHProvidedUserManagerHandlerIsNull(String existingId, String scimObjectString,
                                                                    String attributes, String excludeAttributes,
                                                                    Object objectNEWUser,
                                                                    Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    null, attributes, excludeAttributes);
        });
    }

    //NotFoundException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHNoAssociatedUserExitsInTheUserStore(String existingId, String scimObjectString,
                                                                       String attributes, String excludeAttributes,
                                                                       Object objectNEWUser,
                                                                       Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenThrow(NotFoundException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        //Assertions
        Assert.assertThrows(NotFoundException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    userManager, attributes, excludeAttributes);
        });
    }

    //CharonException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHUpdatedUserResourceIsNull(String existingId, String scimObjectString,
                                                             String attributes, String excludeAttributes,
                                                             Object objectNEWUser, Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenThrow(CharonException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(null);

        //Assertions
        Assert.assertThrows(CharonException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    userManager, attributes, excludeAttributes);
        });
    }

    //BadRequestException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHBadRequestException(String existingId, String scimObjectString,
                                                       String attributes, String excludeAttributes,
                                                       Object objectNEWUser,
                                                       Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenThrow(BadRequestException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        //Assertions
        Assert.assertThrows(BadRequestException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    userManager, attributes, excludeAttributes);
        });
    }

    //NotImplementedException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHNotImplementedException(String existingId, String scimObjectString,
                                                           String attributes, String excludeAttributes,
                                                           Object objectNEWUser,
                                                           Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenThrow(NotImplementedException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(NotImplementedException.class);

        //Assertions
        Assert.assertThrows(NotImplementedException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    userManager, attributes, excludeAttributes);
        });
    }

    //InternalErrorException
    @Test(dataProvider = "dataForUpdateWithPATCHError")
    public void testUpdateWithPATCHInternalErrorException(String existingId, String scimObjectString,
                                                          String attributes, String excludeAttributes,
                                                          Object objectNEWUser,
                                                          Object objectOLDUser)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Me");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenThrow(InternalErrorException.class);

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(InternalErrorException.class);

        //Assertions
        Assert.assertThrows(InternalErrorException.class, () -> {
            meResourceManager.updateWithPATCH(existingId, scimObjectString,
                    userManager, attributes, excludeAttributes);
        });
    }

    @DataProvider(name = "dataForTestGetName")
    public Object[][] dataToTestGetUsername()
            throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = getNewUserSCIMObjectString();
        User user = getNewUser();

        return new Object[][]{
                {user, scimObjectString}
        };
    }

    @Test(dataProvider = "dataForTestGetName")
    public void testGetUserName(Object objectUser, String scimObjectString)
            throws CharonException {

        User user = (User) objectUser;
        String response = meResourceManager.getUserName(scimObjectString);
        Assert.assertEquals(user.getUserName(), response);
    }

    @DataProvider(name = "dataForTestGetNameErrorInGettingTheUsernameFromTheAnonymousRequest")
    public Object[][] dataToTestGetUsernameErrorInGettingTheUsernameFromTheAnonymousRequest() {

        String scimObjectString = "{\n" +
                "UserName: John,\n" +
                "}";
        return new Object[][]{
                {scimObjectString}
        };
    }

    @Test(dataProvider = "dataForTestGetNameErrorInGettingTheUsernameFromTheAnonymousRequest")
    public void testGetUserNameErrorInGettingTheUsernameFromTheAnonymousRequest(String scimObjectString)
            throws CharonException {

        String outputScimResponse = meResourceManager.getUserName(scimObjectString);
        //Assertions
        Assert.assertNull(outputScimResponse);
    }

}

