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
import org.wso2.charon3.core.objects.plainobjects.UsersGetResponse;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test class of UserResourceManager.
 */
public class UserResourceManagerTest {

    private static final String USER_ID = "123";
    private static final String DOMAIN_NAME = "PRIMARY";

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

    private static final String RESOURCE_STRING = "{\n" +
            "  \"schemas\": [\n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"\n" +
            "  ],\n" +
            "  \"attributes\": [\n" +
            "    \"name.familyName\",\n" +
            "    \"userName\"\n" +
            "  ],\n" +
            "  \"filter\": \"userName sw ki and name.familyName co err\",\n" +
            "  \"domain\": \"PRIMARY\",\n" +
            "  \"startIndex\": 1,\n" +
            "  \"count\": 10\n" +
            "}\n";

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
            "    \"formatted\": \"Kim Berry\",\n" +
            "     \"nickName\": \"shaggy\"\n" +
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

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE = "{\n" +
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
            "      \"op\": \"remove\",\n" +
            "      \"value\": {\n" +
            "        \"nickName\": \"shaggy\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],   \n" +
            " \"name\": {\n" +
            "    \"givenName\": \"Kim\",\n" +
            "    \"familyName\": \"Berry\",\n" +
            "    \"formatted\": \"Kim Berry\",\n" +
            "    \"nickName\": \"shaggy\"\n" +
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

    private static final String NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED = "{\n" +
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
            "      \"op\": \"replace\",\n" +
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

    private static final String SCIM2_USER_ENDPOINT = "https://localhost:9443/scim2/User";
    private UserResourceManager userResourceManager;
    private UserManager userManager;
    private MockedStatic<AbstractResourceManager> abstractResourceManager;

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

    @BeforeMethod
    public void setUp() {

        userResourceManager = new UserResourceManager();
        abstractResourceManager = Mockito.mockStatic(AbstractResourceManager.class);
        userManager = mock(UserManager.class);
        abstractResourceManager.when(AbstractResourceManager::getEncoder).thenReturn(new JSONEncoder());
        abstractResourceManager.when(AbstractResourceManager::getDecoder).thenReturn(new JSONDecoder());
    }

    @AfterMethod
    public void tearDown() {

        abstractResourceManager.close();
    }

    /*
     * Contains data to test success path of 'get' method in 'UserResourceManager' class.
     * Format {resource id, attributes, excluded attributes, user object}
     */
    @DataProvider(name = "dataForGetSuccess")
    public Object[][] dataToGetSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();

        return new Object[][]{
                {id, null, null, user},
                {id, null, "emails", user},
                {id, "userName,meta", null, user},
                {id, "userName", "emails", user},
                {id, "userName", "emails,meta", user},
                {id, "userName", "", user},
                {id, "", "emails", user}
        };
    }

    @Test(dataProvider = "dataForGetSuccess")
    public void testGetSuccess(String id, String attributes, String excludeAttributes, Object objectUser)
            throws CharonException, BadRequestException, NotFoundException {

        User user = (User) objectUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        Mockito.when(userManager.getUser(id, requiredAttributes)).thenReturn(user);
        SCIMResponse scimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
        Assert.assertEquals(obj.getString("id"), id);
        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_USER_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
    }

    /*
     * Contains data to test NotFoundException thrown in 'get' method of 'UserResourceManager' class.
     * Format {resource id, attributes, excluded attributes}
     */
    @DataProvider(name = "dataForGetUserNotFoundException")
    public Object[][] dataToGetUserNotFoundException() {

        return new Object[][]{
                {"1234", "userName", "emails"},
                {"1234", "userName", null},
                {"1234", null, "emails"},
                {"1234", null, null},
        };
    }

    @Test(dataProvider = "dataForGetUserNotFoundException")
    public void testGetUserNotFoundException(String id, String attributes, String excludeAttributes)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(() ->
                AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        Mockito.when(userManager.getUser(id, requiredAttributes)).thenReturn(null);
        SCIMResponse scimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);
    }

    /*
     * Contains data to test CharonException thrown in 'get' method of 'UserResourceManager' class.
     * Format {resource id, attributes, excluded attributes, SCIM response status}
     */
    @DataProvider(name = "dataForGetCharonException")
    public Object[][] dataToGetCharonException() {

        return new Object[][]{
                {"1234", "userName", "emails", ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {"1234", "userName", null, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {"1234", null, "emails", ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {"1234", null, null, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForGetCharonException")
    public void testGetUserCharonException(String id, String attributes, String excludeAttributes,
                                           int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.getUser(id, requiredAttributes)).thenThrow(CharonException.class);
        SCIMResponse scimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    /*
     * Contains data to test BadRequestException thrown in 'get' method of 'UserResourceManager' class.
     * Format {resource id, attributes, excluded attributes, SCIM response status}
     */
    @DataProvider(name = "dataForGetBadRequestException")
    public Object[][] dataToGetBadRequestException() {

        return new Object[][]{
                {"1234", "userName", "emails", ResponseCodeConstants.CODE_BAD_REQUEST},
                {"1234", "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST},
                {"1234", null, "emails", ResponseCodeConstants.CODE_BAD_REQUEST},
                {"1234", null, null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForGetBadRequestException")
    public void testGetUserBadRequestException(String id, String attributes, String excludeAttributes,
                                               int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);
        abstractResourceManager.when(()
                -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.getUser(id, requiredAttributes)).thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForListWithGetInt")
    public Object[][] dataToGetListInt() {

        return new Object[][]{
                {null, 1, 2, null, null, DOMAIN_NAME, "emails", null}
        };
    }

    @Test(dataProvider = "dataForListWithGetInt")
    public void testListWithGetInt(String filter, int startIndexInt, int countInt,
                                   String sortBy, String sortOrder, String domainName, String attributes,
                                   String excludeAttributes) {

        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @DataProvider(name = "dataForListWithGetInteger")
    public Object[][] dataToGetListInteger() {

        return new Object[][]{
                {null, 1, 2, null, null, DOMAIN_NAME, "emails", null},
                {"userName sw Rash", 1, 2, null, null, DOMAIN_NAME, "userName,name.familyName",
                        "emails"}
        };
    }

    @Test(dataProvider = "dataForListWithGetInteger")
    public void testListWithGetInteger(String filter, Integer startIndexInt,
                                       Integer countInt, String sortBy, String sortOrder, String domainName,
                                       String attributes, String excludeAttributes) {

        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @DataProvider(name = "dataForTestCreateUserSuccess")
    public Object[][] dataToTestCreateUserSuccess() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null},
                {NEW_USER_SCIM_OBJECT_STRING, "userName", "emails"},
                {NEW_USER_SCIM_OBJECT_STRING, null, "emails"},
                {NEW_USER_SCIM_OBJECT_STRING, null, null}
        };
    }

    @Test(dataProvider = "dataForTestCreateUserSuccess")
    public void testCreateUserSuccess(String scimObjectString, String attributes, String excludeAttributes)
            throws BadRequestException, CharonException, InternalErrorException, ConflictException,
            ForbiddenException {

        User user = getNewUser();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        Mockito.when(userManager.createUser(any(User.class), anyMap())).thenReturn(user);
        SCIMResponse scimResponse = userResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_CREATED);
        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_USER_ENDPOINT + "/" + obj.getString("id");
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
            throws ConflictException, BadRequestException, CharonException, ForbiddenException {

        User user = (User) objectUser;

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.createUser(any(User.class), anyMap())).thenReturn(user);
        SCIMResponse scimResponse = userResourceManager.create(scimObjectString,
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
            throws ConflictException, BadRequestException, CharonException, ForbiddenException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.createUser(any(User.class), anyMap())).thenReturn(null);
        SCIMResponse scimResponse = userResourceManager.create(scimObjectString,
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
            throws ConflictException, BadRequestException, CharonException, ForbiddenException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.createUser(any(User.class), anyMap())).thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = userResourceManager.create(scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestCreateUserConflictException")
    public Object[][] dataToTestCreateUserConflictException() {

        return new Object[][]{
                {NEW_USER_SCIM_OBJECT_STRING, "userName", null, ResponseCodeConstants.CODE_CONFLICT}
        };
    }

    @Test(dataProvider = "dataForTestCreateUserConflictException")
    public void testCreateUserConflictException(String scimObjectString, String attributes,
                                                String excludeAttributes, int expectedScimResponseStatus)
            throws ConflictException, BadRequestException, CharonException, ForbiddenException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(ConflictException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new ConflictException()));
        Mockito.when(userManager.createUser(any(User.class), anyMap()))
                .thenThrow(ConflictException.class);
        SCIMResponse scimResponse = userResourceManager.create(scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestDeleteUserSuccess")
    public Object[][] dataToTestDeleteUserSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        User user = getNewUser();
        String id = user.getId();
        return new Object[][]{
                {id, ResponseCodeConstants.CODE_NO_CONTENT}
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserSuccess")
    public void testDeleteUserSuccess(String id, int expectedScimResponseStatus) {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/" + USER_ID);
        SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
        Assert.assertNull(scimResponse.getResponseMessage());
        Assert.assertNull(scimResponse.getHeaderParamMap());
    }

    @DataProvider(name = "dataForTestDeleteUserFails")
    public Object[][] dataToTestDeleteUserFails()
            throws BadRequestException, CharonException, InternalErrorException {

        String id = getNewUser().getId();
        return new Object[][]{
                {id, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {id, ResponseCodeConstants.CODE_BAD_REQUEST},
                {id, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND},
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserFails")
    public void testDeleteUserFails(String id, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/" + USER_ID);

        if (expectedScimResponseStatus == ResponseCodeConstants.CODE_INTERNAL_ERROR) {

            abstractResourceManager.when(()
                    -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
            SCIMResponse scimResponse = userResourceManager.delete(id, null);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else if (expectedScimResponseStatus == ResponseCodeConstants.CODE_BAD_REQUEST) {

            abstractResourceManager.when(()
                    -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
            doThrow(new BadRequestException()).when(userManager).deleteUser(id);
            SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        } else {

            abstractResourceManager.when(()
                    -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                    .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
            doThrow(new NotFoundException()).when(userManager).deleteUser(id);
            SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
            Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);

        }
    }

    @DataProvider(name = "dataForTestDeleteUserFailsWithCharonException")
    public Object[][] dataToTestDeleteUserCharonExceptionOnly()
            throws BadRequestException, CharonException, InternalErrorException {

        String id = getNewUser().getId();
        return new Object[][]{
                {id, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestDeleteUserFailsWithCharonException")
    public void testDeleteUserCharonExceptionOnly(String id, int expectedScimResponseStatus)
            throws NotFoundException, NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/" + USER_ID);

        doThrow(new CharonException()).when(userManager).deleteUser(id);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTSuccess")
    public Object[][] dataToTestUpdateWithPUTSuccess()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String id = userOld.getId();
        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", "emails",
                        userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, null, "emails",
                        userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, null, null,
                        userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "", "",
                        userNew, userOld, ResponseCodeConstants.CODE_OK},
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTSuccess")
    public void testUpdateWithPUTSuccess(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewUserObject,
                                         Object scimOldUserObject, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        User userNew = (User) scimNewUserObject;
        User userOld = (User) scimOldUserObject;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        Mockito.when(userManager.getUser(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_USER_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public Object[][] dataToTestUpdateWithPUTProvidedUserManagerHandlerIsNull()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String id = userOld.getId();
        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", "emails", userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null, userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, null, "emails", userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR},
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, null, null, userNew,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTProvidedUserManagerHandlerIsNull")
    public void testUpdateWithPUTProvidedUserManagerHandlerIsNull(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewUserObject, Object scimOldUserObject,
                                                                  int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) scimNewUserObject;
        User userOld = (User) scimOldUserObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager
                .encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.getUser(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, scimObjectString, null,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTNotFoundException")
    public Object[][] dataToTestUpdateWithPUTNotFoundException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String id = userOld.getId();
        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_UPDATE, schema, new User());
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userNew, userOld, ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTNotFoundException")
    public void testUpdateWithPUTNoUserExistsWithTheGivenUserName(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimNewUserObject, Object scimOldUserObject,
                                                                  int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) scimNewUserObject;
        User userOld = (User) scimOldUserObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        Mockito.when(userManager.getUser(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTCharonException")
    public Object[][] dataToTestUpdateWithPUTCharonException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String id = userOld.getId();
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null,
                        userOld, ResponseCodeConstants.CODE_INTERNAL_ERROR}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTCharonException")
    public void testUpdateWithPUTUpdatedUserResourceIsNull(String id, String scimObjectString, String
            attributes, String excludeAttributes, Object scimOldUserObject, int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        User userOld = (User) scimOldUserObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.getUser(id, ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(userOld);
        Mockito.when(userManager.updateUser(any(User.class), anyMap())).thenReturn(null);
        SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForTestUpdateWithPUTBadRequestException")
    public Object[][] dataToTestUpdateWithPUTBadRequestException()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING, schema, new User());
        String id = userOld.getId();
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_UPDATE, "userName", null, ResponseCodeConstants.CODE_BAD_REQUEST}
        };
    }

    @Test(dataProvider = "dataForTestUpdateWithPUTBadRequestException")
    public void testUpdateWithPUTBadRequestException(String id, String scimObjectString, String
            attributes, String excludeAttributes, int expectedScimResponseStatus) {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        abstractResourceManager.when(() -> userManager.getUser(id,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, scimObjectString, userManager,
                attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForListWithPOST")
    public Object[][] dataToListWithPOST() throws BadRequestException, CharonException, InternalErrorException {

        List<User> tempList = new ArrayList<>();
        tempList.add(getNewUser());
        return new Object[][]{
                {RESOURCE_STRING, tempList}
        };
    }

    @Test(dataProvider = "dataForListWithPOST")
    public void testListWithPOST(String resourceString, List<User> tempList)
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/.search");
        Mockito.when(userManager.listUsersWithPost(any(SearchRequest.class), anyMap()))
                .thenReturn(new UsersGetResponse(1, tempList));
        SCIMResponse scimResponse = userResourceManager.listWithPOST(resourceString, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
    }

    @Test
    public void testListWithPOSTCharonException() throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.listUsersWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new CharonException());
        SCIMResponse scimResponse = userResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_INTERNAL_ERROR);
    }

    @Test
    public void testListWithPOSTNotImplementedException()
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotImplementedException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotImplementedException()));
        Mockito.when(userManager.listUsersWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new NotImplementedException());
        SCIMResponse scimResponse = userResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_NOT_IMPLEMENTED);
    }

    @Test
    public void testListWithPOSTBadRequestException()
            throws NotImplementedException, BadRequestException, CharonException {

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT + "/.search");
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.listUsersWithPost(any(SearchRequest.class),
                anyMap())).thenThrow(new BadRequestException());
        SCIMResponse scimResponse = userResourceManager.listWithPOST(RESOURCE_STRING, userManager);
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_BAD_REQUEST);
    }

    @DataProvider(name = "dataForUpdateWithPATCH")
    public Object[][] dataToUpdateWithPATCH() throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH, schema, new User());
        String id = userOld.getId();
        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, schema, new User());
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName", "emails", userNew, userOld},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, "userName", null, userNew, userOld},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, null, "emails", userNew, userOld},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_UPDATE, null, null, userNew, userOld}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCH")
    public void testUpdateWithPATCH(String existingId, String scimObjectString, String attributes,
                                    String excludeAttributes, Object scimNewUserObject, Object scimOldUserObject)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        User userNew = (User) scimNewUserObject;
        User userOld = (User) scimOldUserObject;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(scimResponse.getResponseMessage());
        Assert.assertEquals(scimResponse.getResponseStatus(), ResponseCodeConstants.CODE_OK);
        String returnedURI = scimResponse.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);
        String expectedURI = SCIM2_USER_ENDPOINT + "/" + obj.getString("id");
        Assert.assertEquals(returnedURI, expectedURI);
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
                                                                    Object objectNewUser,
                                                                    Object objectOldUser,
                                                                    int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNewUser;
        User userOld = (User) objectOldUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
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
                                                                       Object objectNewUser,
                                                                       Object objectOldUser,
                                                                       int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotFoundException, NotImplementedException {

        User userNew = (User) objectNewUser;
        User userOld = (User) objectOldUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(NotFoundException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new NotFoundException()));
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(null);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
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
                                                             Object objectOldUser, int expectedScimResponseStatus)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        User userOld = (User) objectOldUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(CharonException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new CharonException()));
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(userOld);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(null);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
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
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(() -> AbstractResourceManager.encodeSCIMException(any(BadRequestException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new BadRequestException()));
        Mockito.when(userManager.getUser(existingId, ResourceManagerUtil.getOnlyRequiredAttributesURIs(schema,
                        SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenThrow(BadRequestException.class);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
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
            throws CharonException, BadRequestException, NotFoundException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        abstractResourceManager.when(()
                -> AbstractResourceManager.encodeSCIMException(any(InternalErrorException.class)))
                .thenReturn(getEncodeSCIMExceptionObject(new InternalErrorException()));
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getAllAttributeURIs(schema))).thenReturn(null);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
                null, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
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
                        "emails", userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED, null,
                        "emails", userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REPLACE_AND_UPDATED, null,
                        null, userNew, userOld, ResponseCodeConstants.CODE_OK}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHReplace")
    public void testUpdateWithPATCHReplace(String existingId, String scimObjectString,
                                           String attributes, String excludeAttributes, Object objectNewUser,
                                           Object objectOldUser, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        User userNew = (User) objectNewUser;
        User userOld = (User) objectOldUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForUpdateWithPATCHRemove")
    public Object[][] dataToUpdateWithPATCHRemove()
            throws BadRequestException, CharonException, InternalErrorException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        User userOld = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE, schema, new User());
        String id = userOld.getId();
        User userNew = decoder.decodeResource(NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED,
                schema, new User());
        return new Object[][]{
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED, "userName",
                        "emails", userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED, "userName",
                        null, userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED, null,
                        "emails", userNew, userOld, ResponseCodeConstants.CODE_OK},
                {id, NEW_USER_SCIM_OBJECT_STRING_FOR_PATCH_WITH_REMOVE_AND_UPDATED, "userName",
                        "emails", userNew, userOld, ResponseCodeConstants.CODE_OK}
        };
    }

    @Test(dataProvider = "dataForUpdateWithPATCHRemove")
    public void testUpdateWithPATCHRemove(String existingId, String scimObjectString,
                                          String attributes, String excludeAttributes, Object objectNewUser,
                                          Object objectOldUser, int expectedScimResponseStatus)
            throws BadRequestException, CharonException, NotImplementedException, NotFoundException {

        User userNew = (User) objectNewUser;
        User userOld = (User) objectOldUser;
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_USER_ENDPOINT);
        Mockito.when(userManager.getUser(existingId,
                ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                        schema, SCIMConstants.UserSchemaConstants.USER_NAME, null)))
                .thenReturn(userOld);
        User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(userOld, userNew, schema);
        Mockito.when(userManager.updateUser(any(User.class), anyMap(), anyList())).thenReturn(validatedUser);
        SCIMResponse scimResponse = userResourceManager.updateWithPATCH(existingId, scimObjectString,
                userManager, attributes, excludeAttributes);
        Assert.assertEquals(scimResponse.getResponseStatus(), expectedScimResponseStatus);
    }
}
