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
package org.wso2.charon.core.encoder;

import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.objects.bulk.BulkData;
import org.wso2.charon.core.protocol.endpoints.UserResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JSONEncoderTest {

    //test encoding of a simple attribute with string value.
    @Test
    public void testEncodeSimpleAttributeStringVal() {
        try {
            JSONEncoder jsonEncoder = new JSONEncoder();
            //create and encode a simple attribute with string value.
            SimpleAttribute simpleAttribute = new SimpleAttribute(SCIMConstants.UserSchemaConstants.USER_NAME);
            simpleAttribute.setValue("hasini");

            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.USER_NAME, simpleAttribute);
            JSONObject jsonObject = new JSONObject();
            jsonEncoder.encodeSimpleAttribute(simpleAttribute, jsonObject);
            String encodedString = jsonObject.toString();
            //Assert encoded String
            Assert.assertEquals("{\"userName\":\"hasini\"}", encodedString);

            //test whether properly encoded
            JSONObject decodedObject = new JSONObject(new JSONTokener(encodedString));
            String userName = (String) decodedObject.opt(SCIMConstants.UserSchemaConstants.USER_NAME);
            Assert.assertEquals("hasini", userName);

        } catch (CharonException e) {
            Assert.fail("Simple attribute creation failed.");
        } catch (JSONException e) {
            Assert.fail("Simple attribute encoding failed.");
        }
    }

    //test encoding of a simple attribute with DateTime Value.
    @Test
    public void testEncodeSimpleAttributeDateTimeVal() {
        try {
            JSONEncoder jsonEncoder = new JSONEncoder();
            //create and encode a simple attribute with string value.
            SimpleAttribute simpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.CREATED);
            Date now = new Date();
            simpleAttribute.setValue(now);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.CREATED, simpleAttribute);
            JSONObject jsonObject = new JSONObject();
            jsonEncoder.encodeSimpleAttribute(simpleAttribute, jsonObject);
            String encodedString = jsonObject.toString();

            //Assert encoded string
            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);
            Assert.assertEquals("{\"created\":\"" + sdf.format(now) + "\"}", encodedString);

            //test whether properly encoded
            JSONObject decodedObject = new JSONObject(new JSONTokener(encodedString));
            String dateTimeString = (String) decodedObject.opt(SCIMConstants.CommonSchemaConstants.CREATED);

            Assert.assertEquals(sdf.format(now), dateTimeString);

        } catch (CharonException e) {
            Assert.fail("Simple attribute creation failed.");
        } catch (JSONException e) {
            Assert.fail("Simple attribute encoding failed.");
        }
    }

    //test encoding a multivalued attribute: Members attribute of Group schema
    @Test
    public void testEncodeMultivaluedAttribute() {
        try {
            //group members
            String group1DisplayName = "eng";
            String group1Value = UUID.randomUUID().toString();

            String group2DisplayName = "qa";
            String group2Value = UUID.randomUUID().toString();

            //user members
            String user1DisplayName = "hasini";
            String user1Value = UUID.randomUUID().toString();

            String user2DisplayName = "umesha";
            String user2Value = UUID.randomUUID().toString();

            Group testGroup = new Group();
            testGroup.setMember(group1Value, group1DisplayName, SCIMConstants.GROUP);
            testGroup.setMember(group2Value, group2DisplayName, SCIMConstants.GROUP);
            testGroup.setMember(user1Value, user1DisplayName, SCIMConstants.USER);
            testGroup.setMember(user2Value, user2DisplayName, SCIMConstants.USER);

            JSONEncoder jsonEncoder = new JSONEncoder();
            JSONObject jsonObject = new JSONObject();
            jsonEncoder.encodeMultiValuedAttribute(
                    ((MultiValuedAttribute) testGroup.getAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS)), jsonObject);

            //Assert encoded string
            Assert.assertEquals("{\"members\":[{\"value\":\"" + group1Value + "\",\"display\":\"eng\"," +
                                "\"type\":\"Group\"},{\"value\":\"" + group2Value + "\",\"display\":\"qa\",\"type\":\"Group\"}," +
                                "{\"value\":\"" + user1Value + "\",\"display\":\"hasini\",\"type\":\"User\"}," +
                                "{\"value\":\"" + user2Value + "\",\"display\":\"umesha\",\"type\":\"User\"}]}", jsonObject.toString());

            JSONObject decodedObject = new JSONObject(new JSONTokener(jsonObject.toString()));
            JSONArray membersAttribute = (JSONArray) decodedObject.opt(SCIMConstants.GroupSchemaConstants.MEMBERS);
            for (int i = 0; i < membersAttribute.length(); i++) {
                JSONObject member = (JSONObject) membersAttribute.get(i);
                String type = member.optString(SCIMConstants.CommonSchemaConstants.TYPE);
                if (SCIMConstants.GROUP.equals(type)) {
                    String value = member.optString(SCIMConstants.CommonSchemaConstants.VALUE);
                    if (group1Value.equals(value)) {
                        Assert.assertEquals(group1DisplayName, member.optString(
                                SCIMConstants.CommonSchemaConstants.DISPLAY));
                    } else if (group2Value.equals(value)) {
                        Assert.assertEquals(group2DisplayName, member.optString(
                                SCIMConstants.CommonSchemaConstants.DISPLAY));
                    }
                } else if (SCIMConstants.USER.equals(type)) {
                    String value = member.optString(SCIMConstants.CommonSchemaConstants.VALUE);
                    if (user1Value.equals(value)) {
                        Assert.assertEquals(user1DisplayName, member.optString(
                                SCIMConstants.CommonSchemaConstants.DISPLAY));
                    } else if (user2Value.endsWith(value)) {
                        Assert.assertEquals(user2DisplayName, member.optString(
                                SCIMConstants.CommonSchemaConstants.DISPLAY));
                    }
                }
            }

        } catch (CharonException e) {
            Assert.fail("Error in creating multi valued attribute: Group->Members");
        } catch (NotFoundException e) {
            Assert.fail("Error in setting the attribute: Group->Members");
        } catch (JSONException e) {
            Assert.fail("Multivalued attribute encoding failed.");
        }
    }

    //test encoding a complex attribute: User->Meta attribute
    @Test
    public void testEncodeComplexAttribute() {
        try {
            User testUser = new User();
            Date createdDate = new Date();
            testUser.setCreatedDate(createdDate);
            Date lastModified = new Date();
            testUser.setLastModified(lastModified);

            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);

            String version = "v1";
            String location = "http://appserver.stratoslive.wso2.com/t/charon.com/webapps/charonDemoApp/scim/Users/";
            List<String> attributesToBeRemoved = new ArrayList<String>();
            attributesToBeRemoved.add(SCIMConstants.UserSchemaConstants.DISPLAY_NAME);
            attributesToBeRemoved.add(SCIMConstants.UserSchemaConstants.USER_NAME);

            testUser.setVersion(version);
            testUser.setLocation(location);
            testUser.setAttributesOfMeta(attributesToBeRemoved);

            ComplexAttribute metaAttribute = (ComplexAttribute) testUser.getAttribute(
                    SCIMConstants.CommonSchemaConstants.META);

            JSONEncoder jsonEncoder = new JSONEncoder();
            JSONObject encodedObject = new JSONObject();
            jsonEncoder.encodeComplexAttribute(metaAttribute, encodedObject);

            //Assert encoded sting
            Assert.assertEquals("{\"meta\":{\"lastModified\":\"" + sdf.format(lastModified) + "\"," +
                                "\"created\":\"" + sdf.format(createdDate) + "\"," +
                                "\"location\":\"http://appserver.stratoslive.wso2.com/t/charon.com/webapps/charonDemoApp/scim/Users/\"," +
                                "\"attributes\":[\"displayName\",\"userName\"],\"version\":\"v1\"}}", encodedObject.toString());

            JSONObject decodedObject = new JSONObject(new JSONTokener(encodedObject.toString()));
            JSONObject metaObject = decodedObject.optJSONObject(SCIMConstants.CommonSchemaConstants.META);
            String created = (String) metaObject.opt(SCIMConstants.CommonSchemaConstants.CREATED);

            Assert.assertEquals(sdf.format(createdDate), created);

            String locationString = (String) metaObject.opt(SCIMConstants.CommonSchemaConstants.LOCATION);

            Assert.assertEquals(location, locationString);

            JSONArray attributes = metaObject.optJSONArray(SCIMConstants.CommonSchemaConstants.ATTRIBUTES);
            if (!((SCIMConstants.UserSchemaConstants.DISPLAY_NAME.equals(attributes.get(0)) ||
                   (SCIMConstants.UserSchemaConstants.USER_NAME.equals(attributes.get(0)))))) {
                Assert.fail();
            }
            if (!((SCIMConstants.UserSchemaConstants.DISPLAY_NAME.equals(attributes.get(1)) ||
                   (SCIMConstants.UserSchemaConstants.USER_NAME.equals(attributes.get(1)))))) {
                Assert.fail();
            }

        } catch (CharonException e) {
            Assert.fail("Error in creating complex attribute: User->Meta");
        } catch (JSONException e) {
            Assert.fail("Complex attribute encoding failed.");
        } catch (NotFoundException e) {
            Assert.fail("Error in setting the attribute: User->Meta");
        }
    }

    @Test
    public void testEncodeListedResource() {
        try {
            List<User> users = new ArrayList<User>();

            User user1 = new User();
            String user1ID = UUID.randomUUID().toString();
            Date user1CreatedDate = new Date();
            Date user1LastModified = new Date();

            user1.setId(user1ID);
            user1.setExternalId("hasini");
            user1.setCreatedDate(user1CreatedDate);
            user1.setLastModified(user1LastModified);
            user1.setLocation("http://10.200.3.120:9763/charonDemoApp/scim/Users/");
            user1.setVersion("v1");
            user1.setActive(false);

            users.add(user1);

            User user2 = new User();
            String user2ID = UUID.randomUUID().toString();
            Date user2CreatedDate = new Date();
            Date user2LastModified = new Date();

            user2.setId(user2ID);
            user2.setExternalId("umesha");
            user2.setCreatedDate(user2CreatedDate);
            user2.setLastModified(user2LastModified);
            user2.setLocation("http://10.200.3.120:9763/charonDemoApp/scim/Users/");
            user2.setVersion("v1");
            user2.setActive(true);

            users.add(user2);
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            ListedResource listedResource = userREP.createListedResource(users);

            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedString = jsonEncoder.encodeSCIMObject(listedResource);

            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);
            //Assert encoded string
            Assert.assertEquals("{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"totalResults\":2," +
                                "\"Resources\":[{\"id\":\"" + user1ID + "\",\"active\":false,\"externalId\":\"hasini\"," +
                                "\"meta\":{\"lastModified\":\"" + sdf.format(user1LastModified) + "\",\"created\":\"" + sdf.format(user1CreatedDate) + "\"," +
                                "\"location\":\"http://10.200.3.120:9763/charonDemoApp/scim/Users/\",\"version\":\"v1\"}}," +
                                "{\"id\":\"" + user2ID + "\",\"active\":true,\"externalId\":\"umesha\"," +
                                "\"meta\":{\"lastModified\":\"" + sdf.format(user2LastModified) + "\",\"created\":\"" + sdf.format(user2LastModified) + "\"," +
                                "\"location\":\"http://10.200.3.120:9763/charonDemoApp/scim/Users/\",\"version\":\"v1\"}}]}", encodedString);

        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (NotFoundException e) {
            Assert.fail(e.getDescription());
        }


    }
    //check how boolean is encoded :i.e it can't be represented within double quotes.

    @Test
    public void testEncodeBulkData() {
        try {
            JSONEncoder jsonEncoder = new JSONEncoder();
            BulkData bulkData = new BulkData();
            bulkData.setFailOnErrors(2);
            bulkData.addSchemas("urn:scim:schemas:core:1.0");

            //creating user one
            User user1 = new User();
            String user1ID = UUID.randomUUID().toString();
            Date user1CreatedDate = new Date();
            Date user1LastModified = new Date();

            user1.setId(user1ID);
            user1.setExternalId("Hasini");
            user1.setCreatedDate(user1CreatedDate);
            user1.setLastModified(user1LastModified);
            user1.setLocation("http://10.200.3.120:9763/charonDemoApp/scim/Users/");
            user1.setVersion("v1");
            user1.setActive(false);
            user1.setBulkID("asbv23");
            user1.setPath("/Users");
            user1.setMethod("POST");

            //creating user two
            User user2 = new User();
            String user2ID = UUID.randomUUID().toString();
            Date user2CreatedDate = new Date();
            Date user2LastModified = new Date();

            user2.setId(user2ID);
            user2.setExternalId("Prabath");
            user2.setCreatedDate(user2CreatedDate);
            user2.setLastModified(user2LastModified);
            user2.setLocation("http://10.200.3.120:9763/charonDemoApp/scim/Users/");
            user2.setVersion("v1");
            user2.setActive(true);
            user2.setBulkID("scdgs");
            user2.setPath("/Users");
            user2.setMethod("POST");

            //creating user two
            User user3 = new User();
            String user3ID = UUID.randomUUID().toString();
            Date user3CreatedDate = new Date();
            Date user3LastModified = new Date();

            user3.setId(user3ID);
            user3.setExternalId("Dinuka");
            user3.setCreatedDate(user3CreatedDate);
            user3.setLastModified(user3LastModified);
            user3.setLocation("http://10.200.3.120:9763/charonDemoApp/scim/Users/");
            user3.setVersion("v1");
            user3.setActive(true);
            user3.setBulkID("123");
            user3.setPath("/Users");
            user3.setMethod("POST");

            bulkData.addUser(user1);
            bulkData.addUser(user2);
            bulkData.addUser(user3);

            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);
            String encodedString = jsonEncoder.encodeBulkData(bulkData);
            String response = "{\"failOnErrors\":2,\"schemas\":[\"urn:scim:schemas:core:1.0\"]," +
                              "\"Operations\":[{\"data\":{\"id\":\"" + user1ID + "\"," +
                              "\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"path\":\"/Users\",\"active\":false," +
                              "\"method\":\"POST\",\"externalId\":\"Hasini\",\"meta\":{\"lastModified\":\"" + sdf.format(user1LastModified) + "\"," +
                              "\"created\":\"" + sdf.format(user1CreatedDate) + "\",\"location\":\"http://10.200.3.120:9763/charonDemoApp/scim/Users/\"," +
                              "\"version\":\"v1\"},\"bulkId\":\"asbv23\"},\"path\":\"/Users\",\"method\":\"POST\"," +
                              "\"bulkId\":\"asbv23\"},{\"data\":{\"id\":\"" + user2ID + "\"," +
                              "\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"path\":\"/Users\",\"active\":true," +
                              "\"method\":\"POST\",\"externalId\":\"Prabath\",\"meta\":{\"lastModified\":\"" + sdf.format(user2LastModified) + "\"," +
                              "\"created\":\"" + sdf.format(user2CreatedDate) + "\",\"location\":\"http://10.200.3.120:9763/charonDemoApp/scim/Users/\"," +
                              "\"version\":\"v1\"},\"bulkId\":\"scdgs\"},\"path\":\"/Users\",\"method\":\"POST\"," +
                              "\"bulkId\":\"scdgs\"},{\"data\":{\"id\":\"" + user3ID + "\"," +
                              "\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"path\":\"/Users\",\"active\":true," +
                              "\"method\":\"POST\",\"externalId\":\"Dinuka\",\"meta\":{\"lastModified\":\"" + sdf.format(user3LastModified) + "\"," +
                              "\"created\":\"" + sdf.format(user3CreatedDate) + "\",\"location\":\"http://10.200.3.120:9763/charonDemoApp/scim/Users/\"," +
                              "\"version\":\"v1\"},\"bulkId\":\"123\"},\"path\":\"/Users\",\"method\":\"POST\",\"bulkId\":\"123\"}]}";

            Assert.assertEquals(response, encodedString);

        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }
    }


}
