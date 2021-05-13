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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class of UserResourceManager.
 */
@PrepareForTest({AbstractResourceManager.class})
public class UserResourceManagerTest extends PowerMockTestCase {

    UserResourceManager userResourceManager = new UserResourceManager();

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
        User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

        return user;
    }

    @DataProvider(name = "dataForGetSuccess")
    public Object[][] dataToGetSuccess() throws CharonException, InternalErrorException, BadRequestException {

        User user = getNewUser();
        String id = user.getId();
        UserManager userManager = mock(UserManager.class);

        return new Object[][]{

                {id, userManager, "userName", null, 200, user}
        };
    }

    @Test(dataProvider = "dataForGetSuccess")
    public void testGetSuccess(String id, Object objectUserManager, String attributes,
                               String excludeAttributes, int expectedScimResponseStatus, Object objectUser)
                               throws BadRequestException, NotFoundException, CharonException {

        UserManager userManager = (UserManager) objectUserManager;
        User user = (User) objectUser;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Users");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(userManager.getUser(id, requiredAttributes)).thenReturn(user);

        SCIMResponse outputScimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);
        JSONObject obj = new JSONObject(outputScimResponse.getResponseMessage());

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
        Assert.assertEquals(obj.getString("id"), id);
    }

    @DataProvider(name = "dataForGetThrowingExceptions")
    public Object[][] dataToGetThrowingExceptions() {

        UserManager userManager = mock(UserManager.class);

        return new Object[][]{

                {"1234", userManager, "userName", null}
        };
    }

    @Test(dataProvider = "dataForGetThrowingExceptions")
    public void testGetThrowingExceptions(String id, Object objectUserManager, String attributes,
                     String excludeAttributes) throws CharonException, BadRequestException, NotFoundException {

        UserManager userManager = (UserManager) objectUserManager;

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                (SCIMResourceTypeSchema)
                        CopyUtil.deepCopy(schema), attributes, excludeAttributes);

        mockStatic(AbstractResourceManager.class);
        when(AbstractResourceManager.getResourceEndpointURL(SCIMConstants.USER_ENDPOINT))
                .thenReturn("https://localhost:9443/scim2/Users");
        when(AbstractResourceManager.getEncoder()).thenReturn(new JSONEncoder());
        when(AbstractResourceManager.getDecoder()).thenReturn(new JSONDecoder());
        when(userManager.getUser(id, requiredAttributes)).thenReturn(null);

        SCIMResponse outputScimResponse = userResourceManager.get(id, userManager, attributes, excludeAttributes);

        Assert.assertNull(outputScimResponse);
    }

    @DataProvider(name = "dataForListWithGetInt")
    public Object[][] dataToGetListInt() throws CharonException, BadRequestException, InternalErrorException {

        getNewUser();
        UserManager userManager = mock(UserManager.class);

        return new Object[][]{

                {userManager, null, 1, 2, null, null, "PRIMARY", "emails", null, 200}
        };
    }

    @Test(dataProvider = "dataForListWithGetInt")
    public void testListWithGetInt(Object objectUserManager, String filter, int startIndexInt, int countInt,
                                   String sortBy, String sortOrder, String domainName, String attributes,
                                   String excludeAttributes, int expectedScimResponseStatus) {

        UserManager userManager = (UserManager) objectUserManager;

        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);

        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

    @DataProvider(name = "dataForListWithGetInteger")
    public Object[][] dataToGetListInteger() throws CharonException, BadRequestException, InternalErrorException {

        getNewUser();
        UserManager userManager = mock(UserManager.class);

        return new Object[][]{

                {userManager, null, 1, 2, null, null, "PRIMARY", "emails", null, 200},
                {userManager, "userName sw Rash", 1, 2, null, null, "PRIMARY", "userName,name.familyName",
                        "emails", 200}
        };
    }

    @Test(dataProvider = "dataForListWithGetInteger")
    public void testListWithGetInteger(Object objectUserManager, String filter, Integer startIndexInt,
                        Integer countInt, String sortBy, String sortOrder, String domainName,
                        String attributes, String excludeAttributes, int expectedScimResponseStatus) {

        UserManager userManager = (UserManager) objectUserManager;

        SCIMResponse outputScimResponse = userResourceManager.listWithGET(userManager, filter, startIndexInt,
                countInt, sortBy, sortOrder, domainName, attributes, excludeAttributes);
        Assert.assertEquals(outputScimResponse.getResponseStatus(), expectedScimResponseStatus);
    }

}
