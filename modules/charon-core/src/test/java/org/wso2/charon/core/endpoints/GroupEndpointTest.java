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
package org.wso2.charon.core.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceEndpoint;
import org.wso2.charon.core.protocol.endpoints.GroupResourceEndpoint;
import org.wso2.charon.core.protocol.endpoints.UserResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.utils.InMemroyUserManager;

import java.util.ArrayList;
import java.util.List;

public class GroupEndpointTest {

    private static Log logger = LogFactory.getLog(UserEndpointTest.class);
    private InMemroyUserManager userManager = new InMemroyUserManager(1, "wso2.org");
    private String user2ID;
    private String user1ID;
    private String groupID;
    private String parentGroupID;

    @Test
    public void testGroupEndpoint() throws CharonException {
        //register encoders and decoders in AbstractResourceEndpoint
        AbstractResourceEndpoint.registerEncoder(SCIMConstants.JSON, new JSONEncoder());
        AbstractResourceEndpoint.registerDecoder(SCIMConstants.JSON, new JSONDecoder());
        testCreateGroupWithOutMembers();
        user1ID = createUser("hasini", "hasini@wso2.com");
        //System.out.println(user1ID);                             GroupEndpointTest
        user2ID = createUser("umesha", "umesha@gmail.com");
        //System.out.println(user2ID);
        groupID = createGroup("eng", user1ID);
        //System.out.println(groupID);
        List<String> memberList = new ArrayList<String>();
        memberList.add(user2ID);
        memberList.add(groupID);
        parentGroupID = testCreatingGroupWithValidMembers("ba", memberList);
        testCreatingGroupWithInvalidMembers();
        testListedGroups(3);
        testFilteringGroup("eng");
    }

    public void testCreateGroupWithOutMembers() {
        //should pass according to SCIM 1.1 although fails according to 1.0
        try {
            Group group = new Group();
            group.setDisplayName("eng");
            group.setLocation("Test");
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedGroup = jsonEncoder.encodeSCIMObject(group);
            GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
            SCIMResponse response = groupREP
                    .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
            Assert.assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_CREATED);
            System.out.println(response.getResponseMessage());
        } catch (CharonException e) {
            Assert.fail("Error creating group");
        }
    }

    public String createUser(String userName, String externalId) {
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

    public String createGroup(String displayName, String memberIDs) {
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

    public String testCreatingGroupWithValidMembers(String groupName, List<String> members) {
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

    public void testCreatingGroupWithInvalidMembers() {
        //add arbitrary id for member
        try {
            Group group = new Group();
            group.setDisplayName("wrong");
            group.setMember("abcdefg");
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedGroup = jsonEncoder.encodeSCIMObject(group);
            GroupResourceEndpoint gREP = new GroupResourceEndpoint();
            SCIMResponse scimResponse = gREP
                    .create(encodedGroup, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);
            Assert.assertEquals(scimResponse.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);

        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }
    }

    public void testDeletingGroup() {
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
            Assert.assertEquals(decodedUser.getGroups().size(), 0);

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

    public void testDeletingUser() {
        try {
            //get the groups it was a member and test if members updated
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            userREP.delete(user2ID, userManager, SCIMConstants.APPLICATION_JSON);

            GroupResourceEndpoint gREP = new GroupResourceEndpoint();
            SCIMResponse resp = gREP.get(parentGroupID, SCIMConstants.APPLICATION_JSON, userManager);
            JSONDecoder decoder = new JSONDecoder();
            Group group = new Group();
            group = (Group) decoder
                    .decodeResource(resp.getResponseMessage(), SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, group);
            Assert.assertEquals(group.getMembers().size(), 0);
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }

    }

    public void testUpdateGroup() {
        //try removing a user from a group and see if updated
    }

    public void testListedGroups(int total) {
        try {
            GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
            SCIMResponse scimResponse = groupREP.list(userManager, SCIMConstants.APPLICATION_JSON);
            String jsonString = scimResponse.getResponseMessage();
            JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
            int totalResults = (Integer) jsonObject.opt(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS);
            Assert.assertEquals(totalResults, total);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testFilteringGroup(String displayName) {
        GroupResourceEndpoint groupREP = new GroupResourceEndpoint();
        SCIMResponse scimResponse = groupREP
                .listByFilter("displayNameEq" + displayName, userManager, SCIMConstants.APPLICATION_JSON);

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
}
