/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.protocol.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.endpoints.UserEndpointTest;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.utils.InMemroyUserManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GroupResourceEndpointTest {

    private static Log logger = LogFactory.getLog(UserEndpointTest.class);
    private InMemroyUserManager userManager;
    private static final String TEST_USER_1 = "testUser1";
    private static final String TEST_USER_2 = "testUser2";
    private String user2ID;
    private String user1ID;
    private String groupID;
    private String noUpdateGroupID;
    private String parentGroupID;

    @BeforeTest
    protected void setUp() throws CharonException {
        AbstractResourceEndpoint.registerEncoder(SCIMConstants.JSON, new JSONEncoder());
        AbstractResourceEndpoint.registerDecoder(SCIMConstants.JSON, new JSONDecoder());
    }

    @BeforeMethod
    protected void initEachTest() throws CharonException {
        userManager = new InMemroyUserManager(1, "wso2.org");
        createGroupWithoutMembers();
    }

    private void createGroupWithoutMembers() throws CharonException {
        //should pass according to SCIM 1.1 although fails according to 1.0
        Group group = new Group();
        group.setDisplayName("eng");
        group.setLocation("Test");
        JSONEncoder jsonEncoder = new JSONEncoder();
        String encodedGroup = jsonEncoder.encodeSCIMObject(group);
        GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
        SCIMResponse response = groupREP
                .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_CREATED);
    }

    private void initGroups() {
        user1ID = createUser(TEST_USER_1, "hasini@wso2.com");
        user2ID = createUser(TEST_USER_2, "umesha@gmail.com");
        groupID = createGroup("eng", user1ID);
        noUpdateGroupID = createGroup(InMemroyUserManager.NO_UPDATE_PREFIX + "-eng", user1ID);
        List<String> memberList = new ArrayList<String>();
        memberList.add(user2ID);
        memberList.add(groupID);
        parentGroupID = creatingGroupWithValidMembers("ba", memberList);
    }

    @Test
    public void testGroupEndpoint() throws CharonException {
        testCreatingGroupWithInvalidMembers();
    }

    private String createUser(String userName, String externalId) {
        try {
            User user = new User();
            user.setExternalId(externalId);
            user.setUserName(userName);
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedUser = jsonEncoder.encodeSCIMObject(user);
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            SCIMResponse response = userREP
                    .create(encodedUser, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
            String userString = response.getResponseMessage();
            JSONDecoder decoder = new JSONDecoder();
            User decodedUser = new User();
            decodedUser = (User) decoder
                    .decodeResource(userString, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, decodedUser);
            return decodedUser.getId();
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        }
        return null;
    }

    private String createGroup(String displayName, String memberIDs) {
        try {
            Group group = new Group();
            group.setDisplayName(displayName);
            group.setMember(memberIDs);
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedGroup = jsonEncoder.encodeSCIMObject(group);
            GroupResourceEndpoint gREP = new GroupResourceEndpoint();
            SCIMResponse scimResponse = gREP
                    .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
            String groupString = scimResponse.getResponseMessage();
            JSONDecoder jsonDecoder = new JSONDecoder();
            Group decodedGroup = new Group();
            decodedGroup = (Group) jsonDecoder
                    .decodeResource(groupString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, decodedGroup);
            return decodedGroup.getId();

        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        }
        return null;
    }

    private String creatingGroupWithValidMembers(String groupName, List<String> members) {
        try {
            Group group = new Group();
            group.setDisplayName(groupName);
            for (String member : members) {
                group.setMember(member);
            }
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedGroup = jsonEncoder.encodeSCIMObject(group);
            GroupResourceEndpoint gREP = new GroupResourceEndpoint();
            SCIMResponse scimResponse = gREP
                    .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
            String groupString = scimResponse.getResponseMessage();
            JSONDecoder jsonDecoder = new JSONDecoder();
            Group decodedGroup = new Group();
            decodedGroup = (Group) jsonDecoder
                    .decodeResource(groupString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, decodedGroup);
            Assert.assertEquals(decodedGroup.getMembers().size(), 2);
            String createdGroupID = decodedGroup.getId();
            UserResourceEndpoint uREP = new UserResourceEndpoint();
            SCIMResponse resp = uREP.get(user2ID, SCIMConstants.APPLICATION_JSON, userManager);
            User updatedUser = new User();
            updatedUser = (User) jsonDecoder
                    .decodeResource(resp.getResponseMessage(), SCIMSchemaDefinitions.SCIM_USER_SCHEMA, updatedUser);
            List<String> groups = updatedUser.getGroups();
            Assert.assertEquals(groups.get(0), createdGroupID);

            return createdGroupID;

        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        }
        return null;
    }

    @Test
    public void testCreatingGroupWithInvalidMembers() throws CharonException {
        //add arbitrary id for member
        Group group = new Group();
        group.setDisplayName("wrong");
        group.setMember("abcdefg");
        JSONEncoder jsonEncoder = new JSONEncoder();
        String encodedGroup = jsonEncoder.encodeSCIMObject(group);
        GroupResourceEndpoint gREP = new GroupResourceEndpoint();
        SCIMResponse scimResponse = gREP
                .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertEquals(scimResponse.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDeletingGroup() {
        initGroups();
        try {
            //get earlier member user and see if its groups updated
            GroupResourceEndpoint gREP = new GroupResourceEndpoint();
            gREP.delete(groupID, userManager, SCIMConstants.APPLICATION_JSON);

            UserResourceEndpoint userREP = new UserResourceEndpoint();
            SCIMResponse scimResponse = userREP.get(user1ID, SCIMConstants.APPLICATION_JSON, userManager);
            JSONDecoder jsonDecoder = new JSONDecoder();
            User decodedUser = new User();
            decodedUser = (User) jsonDecoder
                    .decodeResource(scimResponse.getResponseMessage(), SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                            decodedUser);
            Assert.assertEquals(decodedUser.getGroups().size(), 1);

            //get the parent goup and see if it is updated
            SCIMResponse resp = gREP.get(parentGroupID, SCIMConstants.APPLICATION_JSON, userManager);
            Group parentGroup = new Group();
            parentGroup = (Group) jsonDecoder
                    .decodeResource(resp.getResponseMessage(), SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, parentGroup);
            Assert.assertEquals(parentGroup.getMembers().size(), 1);

        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }

    }

    @Test
    public void testDeletingUser() throws BadRequestException, CharonException {
        //get the groups it was a member and test if members updated
        UserResourceEndpoint userREP = new UserResourceEndpoint();
        userREP.delete(user2ID, userManager, SCIMConstants.APPLICATION_JSON);

        GroupResourceEndpoint gREP = new GroupResourceEndpoint();
        SCIMResponse resp = gREP.get(parentGroupID, SCIMConstants.APPLICATION_JSON, userManager);
        JSONDecoder decoder = new JSONDecoder();
        Group group = new Group();
        group = (Group) decoder
                .decodeResource(resp.getResponseMessage(), SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, group);
        Assert.assertNotNull(group);
        Assert.assertEquals(group.getMembers().size(), 0);
    }
    

    @Test
    public void testListedGroups() throws JSONException {
        initGroups();
        GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
        SCIMResponse scimResponse = groupREP.list(userManager, SCIMConstants.APPLICATION_JSON);
        String jsonString = scimResponse.getResponseMessage();
        JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
        int totalResults = (Integer) jsonObject.opt(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS);
        Assert.assertEquals(totalResults, 4);
    }

    @Test
    public void testFilteringGroup() {
        String displayName = "eng";
        GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
        SCIMResponse scimResponse = groupREP
                .listByFilter("displayName Eq" + displayName, userManager, SCIMConstants.APPLICATION_JSON);

        Assert.assertEquals(scimResponse.getResponseCode(), ResponseCodeConstants.CODE_OK);
        //decode listed resource
        SCIMClient scimClient = new SCIMClient();
        try {
            ListedResource listedResource = scimClient
                    .decodeSCIMResponseWithListedResource(scimResponse.getResponseMessage(), SCIMConstants.JSON,
                            SCIMConstants.GROUP_INT);
            List<SCIMObject> groups = listedResource.getScimObjects();
            String name = null;
            //we expect only one result here.
            for (SCIMObject scimObject : groups) {
                name = ((Group) scimObject).getDisplayName();

            }
            Assert.assertEquals(name, displayName);
        } catch (CharonException e) {
            logger.error(e.getDescription());
            Assert.fail("Error in decoding the response recieved for filter user: " + displayName);
        } catch (BadRequestException e) {
            logger.error(e.getDescription());
            Assert.fail("Error in decoding the response recieved for filter user: " + displayName);
        }
    }

    @Test
    public void testUpdateWithPUT() throws IOException {
        initGroups();
        GroupResourceEndpoint groupResourceEndpoint = new GroupResourceEndpoint();
        SCIMResponse response = groupResourceEndpoint
                .updateWithPUT(groupID, "", SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON,
                        userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_BAD_REQUEST);

        response = groupResourceEndpoint
                .updateWithPUT(groupID, getScimResource("group1.json"), SCIMConstants.APPLICATION_JSON,
                        SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_OK);

        response = groupResourceEndpoint
                .updateWithPUT("non-existant-group", getScimResource("group1.json"), SCIMConstants.APPLICATION_JSON,
                        SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);

        response = groupResourceEndpoint
                .updateWithPUT("whatever-group", getScimResource("group1.json"), SCIMConstants.APPLICATION_JSON,
                        SCIMConstants.APPLICATION_JSON, null);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR,
                "User manager not set means internal server error");

        response = groupResourceEndpoint
                .updateWithPUT(noUpdateGroupID, getScimResource("group1.json"), SCIMConstants.APPLICATION_JSON,
                        SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testUpdateWithPATCH() throws IOException {
        initGroups();
        GroupResourceEndpoint groupResourceEndpoint = new GroupResourceEndpoint();
        SCIMResponse response = groupResourceEndpoint
                .updateWithPATCH(groupID, "", SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON,
                        userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_BAD_REQUEST);

        response = groupResourceEndpoint
                .updateWithPATCH(groupID, getScimResource("group1.json"), SCIMConstants.APPLICATION_JSON,
                        SCIMConstants.APPLICATION_JSON, userManager);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_OK);
    }

    private String getScimResource(String resource) throws IOException {
        String fileName1 = this.getClass().getResource("user1.json").getFile();
        String scimString = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        return scimString;
    }

}
