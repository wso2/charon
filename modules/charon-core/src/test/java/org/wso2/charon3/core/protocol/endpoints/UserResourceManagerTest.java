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
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Test class of UserResourceManager.
 */
public class UserResourceManagerTest {

    private static final String SCIM2_ME_ENDPOINT = "https://localhost:9443/scim2/Me";

    private UserResourceManager userResourceManager;
    private UserManager userManager;
    private MockedStatic<AbstractResourceManager> abstractResourceManager;

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

    @DataProvider(name = "dataForGetSuccess")
    public Object[][] dataToGetSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();

        return new Object[][]{
                {id, "userName", null, 200, user}
        };
    }

    @Test(dataProvider = "dataForGetSuccess")
    public void testGetSuccess(String id, String attributes,
                               String excludeAttributes, int expectedScimResponseStatus, Object objectUser)
                               throws CharonException {

        User user = (User) objectUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);

        abstractResourceManager.when(() -> userManager.getUser(id, requiredAttributes)).thenReturn(user);

        SCIMResponse outputScimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
        Assert.assertEquals(obj.getString("id"), id);
    }

    @DataProvider(name = "dataForGetThrowingExceptions")
    public Object[][] dataToGetThrowingExceptions() {

        return new Object[][]{
                {"1234", "userName", null}
        };
    }

    @Test(dataProvider = "dataForGetThrowingExceptions")
    public void testGetThrowingExceptions(String id, String attributes,
                     String excludeAttributes) throws CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        abstractResourceManager.when(() -> AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn(SCIM2_ME_ENDPOINT);
        abstractResourceManager.when(() -> userManager.getUser(id, requiredAttributes)).thenReturn(null);

        SCIMResponse outputScimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);

        Assert.assertNull(outputScimResponse);
    }

    @DataProvider(name = "dataForListWithGetInt")
    public Object[][] dataToGetListInt() {

        return new Object[][]{
                {null, 1, 2, null, null, "PRIMARY", "emails", null, 200}
        };
    }

    @Test(dataProvider = "dataForListWithGetInt")
    public void testListWithGetInt(String filter, int startIndexInt, int countInt,
                                   String sortBy, String sortOrder, String domainName, String attributes,
                                   String excludeAttributes, int expectedScimResponseStatus) {

        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForListWithGetInteger")
    public Object[][] dataToGetListInteger() {

        return new Object[][]{
                {null, 1, 2, null, null, "PRIMARY", "emails", null, 200},
                {"userName sw Rash", 1, 2, null, null, "PRIMARY", "userName,name.familyName",
                        "emails", 200}
        };
    }

    @Test(dataProvider = "dataForListWithGetInteger")
    public void testListWithGetInteger(String filter, Integer startIndexInt,
                        Integer countInt, String sortBy, String sortOrder, String domainName,
                        String attributes, String excludeAttributes, int expectedScimResponseStatus) {


        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    private User getNewUser() throws BadRequestException, CharonException, InternalErrorException {

        String scimObjectString = "{\n" +
                "  \"schemas\": \n" +
                "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
                "    ],\n" +
                " \"name\": {\n" +
                "    \"givenName\": \"Rash\",\n" +
                "    \"familyName\": \"Na\",\n" +
                "    \"formatted\": \"Rash Na\"\n" +
                "  },\n" +
                "  \"userName\": \"rash\",\n" +
                "  \"password\": \"rash123\",\n" +
                "  \"id\": \"123\",\n" +
                "  \"emails\": [\n" +
                "      {\n" +
                "        \"type\": \"home\",\n" +
                "        \"value\": \"rash@gmail.com\",\n" +
                "         \"primary\": true\n" +
                "      }\n" +
                "  ]\n" +
                "}";

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        JSONDecoder decoder = new JSONDecoder();
        return decoder.decodeResource(scimObjectString, schema, new User());
    }

}
