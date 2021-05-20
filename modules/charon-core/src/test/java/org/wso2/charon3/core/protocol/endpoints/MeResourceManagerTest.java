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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class of MeResourceManager.
 */
@PrepareForTest({AbstractResourceManager.class})
public class MeResourceManagerTest extends PowerMockTestCase {

    private static final String NEWUSERSCIMOBJECTSTRING = "{\n" +
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

    private static final String NEWUSERSCIMOBJECTSTRINGUPDATE = "{\n" +
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

    private static final String NEWUSERSCIMOBJECTSTRINGPATCH = "{\n" +
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

    private static final String NEWUSERSCIMOBJECTSTRINGPATCHUPDATE = "{\n" +
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

    private static final String NEWUSERSCIMOBJECTSTRINGFORPATCHWITHREPLACE = "{\n" +
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

    private static final String NEWUSERSCIMOBJECTSTRINGFORPATCHWITHREPLACEUPDATED = "{\n" +
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

    private static final String ENDPOINT_ME = "https://localhost:9443/scim2/Me";

    private MeResourceManager meResourceManager;
    private UserManager userManager;

    @BeforeMethod
    public void setUp() {

        meResourceManager = new MeResourceManager();
        userManager = mock(UserManager.class);
    }

    @AfterMethod
    public void tearDown() {

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

        return decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
    }

    @DataProvider(name = "dataForGetSuccess")
    public Object[][] dataToGetSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();
        String name = user.getUserName();

        return new Object[][]{
                {id, name, null, null, ResponseCodeConstants.CODE_OK, user},
                {id, name, null, "emails", ResponseCodeConstants.CODE_OK, user},
                {id, name, "username,meta", null, ResponseCodeConstants.CODE_OK, user},
                {id, name, "username", "emails", ResponseCodeConstants.CODE_OK, user}
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
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(userManager.getMe(name, requiredAttributes)).thenReturn(user);

        SCIMResponse outputScimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        //Assertions

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
        Assert.assertEquals(obj.getString("userName"), name);
        Assert.assertEquals(obj.getString("id"), id);

        String returnedURI = outputScimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = ENDPOINT_ME + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);

        if (attributes == null & excludeAttributes == null) {

            Assert.assertTrue(obj.has("emails"));

        } else if (attributes == null & excludeAttributes != null) {

            Assert.assertFalse(obj.has("emails"));

        }

    }

    @DataProvider(name = "dataForGetNotFoundException")
    public Object[][] dataToGetNotFoundException() {

        return new Object[][]{
                {"David", "userName", null, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    @Test(dataProvider = "dataForGetNotFoundException")
    public void testGetUserNotFoundException(String name, String attributes, String excludeAttributes,
                                             int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        when(userManager.getMe(name, requiredAttributes)).thenReturn(null);

        SCIMResponse outputScimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
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
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        when(userManager.getMe(name, requiredAttributes)).thenThrow(CharonException.class);

        SCIMResponse outputScimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

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
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        when(userManager.getMe(name, requiredAttributes)).thenThrow(BadRequestException.class);

        SCIMResponse outputScimResponse = meResourceManager.get(name, userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateSuccess")
    public Object[][] dataToTestCreateSuccess() throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, user, ResponseCodeConstants.CODE_CREATED}
        };
    }

    @Test(dataProvider = "dataForTestCreateSuccess")
    public void testCreateSuccess(String scimObjectString, String attributes, String excludeAttributes,
                                  Object objectUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        User user = (User) objectUser;
        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.createMe(anyObject(), anyObject())).thenReturn(user);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        String returnedURI = outputScimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = ENDPOINT_ME + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);

    }

    //InternalErrorException
    @DataProvider(name = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestCreateProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, user, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestCreateProvidedUserManagerHandlerIsNull")
    public void testCreateProvidedUserManagerHandlerIsNull(String scimObjectString, String attributes,
                                                           String excludeAttributes, Object objectUser,
                                                           int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        User user = (User) objectUser;
        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        when(userManager.createMe(anyObject(), anyObject())).thenReturn(user);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    //InternalErrorException
    @DataProvider(name = "dataForTestCreateNewlyCreatedUserResourceIsNull")
    public Object[][] dataToTestCreateNewlyCreatedUserResourceIsNull() {

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestCreateNewlyCreatedUserResourceIsNull")
    public void testCreateNewlyCreatedUserResourceIsNull(String scimObjectString, String attributes,
                                                         String excludeAttributes,
                                                         int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        when(userManager.createMe(anyObject(), anyObject())).thenReturn(null);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateBadRequestException")
    public Object[][] dataToTestCreatBadRequestException() {

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForTestCreateBadRequestException")
    public void testCreateBadRequestException(String scimObjectString, String attributes,
                                              String excludeAttributes,
                                              int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(BadRequestException.class);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateConflictException")
    public Object[][] dataToTestCreatConflictException() {

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, ResponseCodeConstants.CODE_CONFLICT}
        };
    }

    @Test(dataProvider = "dataForTestCreateConflictException")
    public void testCreateConflictException(String scimObjectString, String attributes,
                                            String excludeAttributes,
                                            int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new ConflictException()));
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(ConflictException.class);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateNotFoundException")
    public Object[][] dataToTestCreateNotFoundException() {

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    @Test(dataProvider = "dataForTestCreateNotFoundException")
    public void testCreateNotFoundException(String scimObjectString, String attributes,
                                            String excludeAttributes,
                                            int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(NotFoundException.class);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestCreateCharonException")
    public Object[][] dataToTestCreateCharonException() {

        return new Object[][]{
                {NEWUSERSCIMOBJECTSTRING, "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestCreateCharonException")
    public void testCreateCharonException(String scimObjectString, String attributes,
                                          String excludeAttributes,
                                          int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, ConflictException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        when(userManager.createMe(anyObject(), anyObject())).thenThrow(CharonException.class);

        SCIMResponse outputScimResponse = meResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestDeleteSuccess")
    public Object[][] dataToTestDeleteSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, ResponseCodeConstants.CODE_NO_CONTENT}
        };
    }

    @Test(dataProvider = "dataForTestDeleteSuccess")
    public void testDeleteSuccess(String userName, int expectedScimResponseStatus)
            throws NotFoundException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);

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
                {username, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {username, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND},
                {username, ResponseCodeConstants.CODE_NOT_IMPLEMENTED},
                {username, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForTestDeleteFails")
    public void testDeleteFails(String userName, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);

        if (expectedScimResponseStatus == ResponseCodeConstants.CODE_INTERNAL_ERROR) {

            when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

            SCIMResponse outputScimResponse = meResourceManager.delete(userName, null);
            Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else if (expectedScimResponseStatus == ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND) {

            doThrow(new NotFoundException()).when(userManager).deleteMe(userName);

            when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

            SCIMResponse outputScimResponse = meResourceManager.delete(userName, userManager);
            Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else if (expectedScimResponseStatus == ResponseCodeConstants.CODE_NOT_IMPLEMENTED) {

            doThrow(new NotImplementedException()).when(userManager).deleteMe(userName);

            when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));

            SCIMResponse outputScimResponse = meResourceManager.delete(userName, userManager);
            Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else if (expectedScimResponseStatus == ResponseCodeConstants.CODE_BAD_REQUEST) {

            doThrow(new BadRequestException()).when(userManager).deleteMe(userName);

            when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

            SCIMResponse outputScimResponse = meResourceManager.delete(userName, userManager);
            Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
        }

    }

    @DataProvider(name = "dataForTestDeleteFailsINCharonExceptionOnly")
    public Object[][] dataToTestDeleteCharonExceptionOnly()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String username = user.getUsername();
        return new Object[][]{
                {username, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestDeleteFailsINCharonExceptionOnly")
    public void testDeleteCharonExceptionOnly(String userName, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);

        doThrow(new CharonException()).when(userManager).deleteMe(userName);

        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        SCIMResponse outputScimResponse = meResourceManager.delete(userName, userManager);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

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

    @Test
    public void testListWithPOST() {

        SCIMResponse outputScimResponse = meResourceManager.listWithPOST(null, userManager);
        Assert.assertNull(outputScimResponse);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTSuccess")
    public Object[][] dataToTestUpdateWithPUTSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGUPDATE, schema, new User());

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null, userNew, userOld, ResponseCodeConstants.CODE_OK}
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

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        String returnedURI = outputScimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = ENDPOINT_ME + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestUpdateWithPUTProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGUPDATE, schema, new User());

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null, userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public void testUpdateWithPUTProvidedUserManagerHandlerIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser, Object objectOLDUser,
                                                                  int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, null,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTNotFoundException")
    public Object[][] dataToTestUpdateWithPUTNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGUPDATE, schema, new User());

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    //NotFoundException
    @Test(dataProvider = "dataForTestUpdateWithPUTNotFoundException")
    public void testUpdateWithPUTNoUserExistsWithTheGivenUserName(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectNEWUser,
                                                                  Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTCharonException")
    public Object[][] dataToTestUpdateWithPUTCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    // CharonException
    @Test(dataProvider = "dataForTestUpdateWithPUTCharonException")
    public void testUpdateWithPUTUpdatedUserResourceIsNull(String userName, String scimObjectString, String
            attributes, String excludeAttributes, Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(null);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTNotImplementedException")
    public Object[][] dataToTestUpdateWithPUTNotImplementedException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null, ResponseCodeConstants.CODE_NOT_IMPLEMENTED}
        };
    }

    //NotImplementedException
    @Test(dataProvider = "dataForTestUpdateWithPUTNotImplementedException")
    public void testUpdateWithPUTNotImplementedException(String userName, String scimObjectString, String
            attributes, String excludeAttributes, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(NotImplementedException.class);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestUpdateWithPUTBadRequestException")
    public Object[][] dataToTestUpdateWithPUTBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRING, schema, new User());
        String name = userOld.getUserName();

        return new Object[][]{
                {name, NEWUSERSCIMOBJECTSTRINGUPDATE, "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    // BadRequestException
    @Test(dataProvider = "dataForTestUpdateWithPUTBadRequestException")
    public void testUpdateWithPUTBadRequestException(String userName, String scimObjectString, String
            attributes, String excludeAttributes, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        when(userManager.getMe(userName,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPUT(userName, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCH")
    public Object[][] dataToUpdateWithPATCH() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, schema, new User());

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName", null, userNew,
                        userOld, ResponseCodeConstants.CODE_OK}
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
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        //Assertions
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

        String returnedURI = outputScimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = ENDPOINT_ME + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);

    }

    @DataProvider(name = "dataForUpdateWithPATCHReplace")
    public Object[][] dataToUpdateWithPATCHReplace()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGFORPATCHWITHREPLACE, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGFORPATCHWITHREPLACEUPDATED, schema, new User());

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGFORPATCHWITHREPLACEUPDATED, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_OK}
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
                .thenReturn(ENDPOINT_ME);
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

    @DataProvider(name = "dataForUpdateWithPATCHProvidedUserManagerHandlerIsNull")
    public Object[][] dataToUpdateWithPATCHInProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, schema, new User());

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForUpdateWithPATCHProvidedUserManagerHandlerIsNull")
    public void testUpdateWithPATCHProvidedUserManagerHandlerIsNull(String existingId, String scimObjectString,
                                                                    String attributes, String excludeAttributes,
                                                                    Object objectNEWUser,
                                                                    Object objectOLDUser,
                                                                    int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHNotFoundException")
    public Object[][] dataToUpdateWithPATCHNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, schema, new User());

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    //NotFoundException
    @Test(dataProvider = "dataForUpdateWithPATCHNotFoundException")
    public void testUpdateWithPATCHNoAssociatedUserExitsInTheUserStore(String existingId, String scimObjectString,
                                                                       String attributes, String excludeAttributes,
                                                                       Object objectNEWUser,
                                                                       Object objectOLDUser,
                                                                       int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userNew = (User) objectNEWUser;
        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);

        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(validatedUser);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHCharonException")
    public Object[][] dataToUpdateWithPATCHCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName",
                        null, userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //CharonException
    @Test(dataProvider = "dataForUpdateWithPATCHCharonException")
    public void testUpdateWithPATCHUpdatedUserResourceIsNull(String existingId, String scimObjectString,
                                                             String attributes, String excludeAttributes,
                                                             Object objectOLDUser, int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException, NotImplementedException {

        User userOld = (User) objectOLDUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);

        when(userManager.updateMe(anyObject(), anyObject())).thenReturn(null);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHBadRequestException")
    public Object[][] dataToUpdateWithPATCHBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName",
                        null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    //BadRequestException
    @Test(dataProvider = "dataForUpdateWithPATCHBadRequestException")
    public void testUpdateWithPATCHBadRequestException(String existingId, String scimObjectString,
                                                       String attributes, String excludeAttributes,
                                                       int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHNotImplementedException")
    public Object[][] dataToUpdateWithPATCHNotImplementedException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName", null,
                        ResponseCodeConstants.CODE_NOT_IMPLEMENTED}
        };
    }

    //NotImplementedException
    @Test(dataProvider = "dataForUpdateWithPATCHNotImplementedException")
    public void testUpdateWithPATCHNotImplementedException(String existingId, String scimObjectString,
                                                           String attributes, String excludeAttributes,
                                                           int expectedScimResponseStatus)
            throws BadRequestException, NotFoundException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(NotImplementedException.class);
        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForUpdateWithPATCHInternalErrorException")
    public Object[][] dataToUpdateWithPATCHInternalErrorException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();

        User userOld = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCH, schema, new User());
        String id = userOld.getId();

        User userNew = decoder.decodeResource(NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, schema, new User());

        return new Object[][]{
                {id, NEWUSERSCIMOBJECTSTRINGPATCHUPDATE, "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    //InternalErrorException
    @Test(dataProvider = "dataForUpdateWithPATCHInternalErrorException")
    public void testUpdateWithPATCHInternalErrorException(String existingId, String scimObjectString,
                                                          String attributes, String excludeAttributes,
                                                          int expectedScimResponseStatus
    )
            throws BadRequestException, NotFoundException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

        mockStatic(AbstractResourceManager.class);

        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(ENDPOINT_ME);
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));

        when(userManager.getMe(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(InternalErrorException.class);

        SCIMResponse outputScimResponse = meResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);

    }

    @DataProvider(name = "dataForTestGetName")
    public Object[][] dataToTestGetUsername()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();

        return new Object[][]{
                {user, NEWUSERSCIMOBJECTSTRING}
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

