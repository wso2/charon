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

import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceEndpoint;
import org.wso2.charon.core.protocol.endpoints.UserResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.utils.InMemroyUserManager;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class UserEndpointTest {
    private static Log logger = LogFactory.getLog(UserEndpointTest.class);
    InMemroyUserManager inMemroyUserManager = new InMemroyUserManager(1, "wso2.org");
    String id;

    @Test
    public void testUserEndpoint() throws CharonException {
        //register encoders and decoders in AbstractResourceEndpoint
        AbstractResourceEndpoint.registerEncoder(SCIMConstants.JSON, new JSONEncoder());
        AbstractResourceEndpoint.registerDecoder(SCIMConstants.JSON, new JSONDecoder());
        testCreatingUser("hasini");
        testRetrievingUser();
        testCreatingUser("umesha");
        testListingUsers(2);
        testDeletingUser();
        testRetrievingDeletedUser();
        testAttributeURIs();
        testFilterUsers("hasini");
    }

    //@Test
    public void testCreatingUser(String externalId) {
        try {
            String testExternalID = "\"externalId\": " + externalId + ",";
            String userName = "\"userName\": " + externalId + ",";
            String userResourceString = "{\n" +
                                        "  \"schemas\": [\"urn:scim:schemas:core:1.0\"],\n" +
                                        "  \"id\": \"2819c223-7f76-453a-919d-413861904646\",\n" +
                                        testExternalID +
                                        /*"  \"externalId\":" + externalId +*/
                                        userName +
                                        "  \"name\": {\n" +
                                        "    \"formatted\": \"Ms. Barbara J Jensen III\",\n" +
                                        "    \"familyName\": \"Jensen\",\n" +
                                        "    \"givenName\": \"Barbara\",\n" +
                                        "    \"middleName\": \"Jane\",\n" +
                                        "    \"honorificPrefix\": \"Ms.\",\n" +
                                        "    \"honorificSuffix\": \"III\"\n" +
                                        "  },\n" +
                                        "  \"displayName\": \"Babs Jensen\",\n" +
                                        "  \"nickName\": \"Babs\",\n" +
                                        "  \"profileUrl\": \"https://login.example.com/bjensen\",\n" +
                                        "  \"emails\": [\n" +
                                        "    {\n" +
                                        "      \"value\": \"bjensen@example.com\",\n" +
                                        "      \"type\": \"work\",\n" +
                                        "      \"primary\": true\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"value\": \"babs@jensen.org\",\n" +
                                        "      \"type\": \"home\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"addresses\": [\n" +
                                        "    {\n" +
                                        "      \"type\": \"work\",\n" +
                                        "      \"streetAddress\": \"100 Universal City Plaza\",\n" +
                                        "      \"locality\": \"Hollywood\",\n" +
                                        "      \"region\": \"CA\",\n" +
                                        "      \"postalCode\": \"91608\",\n" +
                                        "      \"country\": \"USA\",\n" +
                                        "      \"formatted\": \"100 Universal City Plaza\\nHollywood," +
                                        " CA 91608 USA\",\n" +
                                        "      \"primary\": true\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"type\": \"home\",\n" +
                                        "      \"streetAddress\": \"456 Hollywood Blvd\",\n" +
                                        "      \"locality\": \"Hollywood\",\n" +
                                        "      \"region\": \"CA\",\n" +
                                        "      \"postalCode\": \"91608\",\n" +
                                        "      \"country\": \"USA\",\n" +
                                        "      \"formatted\": \"456 Hollywood Blvd\\nHollywood, CA 91608 USA\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"phoneNumbers\": [\n" +
                                        "    {\n" +
                                        "      \"value\": \"555-555-5555\",\n" +
                                        "      \"type\": \"work\"\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"value\": \"555-555-4444\",\n" +
                                        "      \"type\": \"mobile\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"ims\": [\n" +
                                        "    {\n" +
                                        "      \"value\": \"someaimhandle\",\n" +
                                        "      \"type\": \"aim\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"photos\": [\n" +
                                        "    {\n" +
                                        "      \"value\": \"https://photos.example.com/profilephoto/72930000000Ccne" +
                                        "/F\",\n" +
                                        "      \"type\": \"photo\"\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"value\": \"https://photos.example.com/profilephoto/72930000000Ccne" +
                                        "/T\",\n" +
                                        "      \"type\": \"thumbnail\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"userType\": \"Employee\",\n" +
                                        "  \"title\": \"Tour Guide\",\n" +
                                        "  \"preferredLanguage\":\"en_US\",\n" +
                                        "  \"locale\": \"en_US\",\n" +
                                        "  \"timezone\": \"America/Los_Angeles\",\n" +
                                        "  \"password\":\"t1meMa$heen\",\n" +
                                        "  \"groups\": [\n" +
                                        "    {\n" +
                                        "      \"display\": \"Tour Guides\",\n" +
                                        "      \"value\": \"00300000005N2Y6AA\"\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"display\": \"Employees\",\n" +
                                        "      \"value\": \"00300000005N34H78\"\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "      \"display\": \"US Employees\",\n" +
                                        "      \"value\": \"00300000005N98YT1\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"x509Certificates\": [\n" +
                                        "    {\n" +
                                        "      \"value\": \"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMA" +
                                        "kGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29" +
                                        "tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0" +
                                        "MzFaH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleG" +
                                        "FtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZ" +
                                        "IhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A" +
                                        "MIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bcFLgzdLI8auoR+cC9/" +
                                        "Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5ixO8SL7I7SDhcBVJhqVqr3Hg" +
                                        "llEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1O" +
                                        "s7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5" +
                                        "jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GP" +
                                        "e1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmV" +
                                        "yYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYD" +
                                        "VR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81Ss" +
                                        "FnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz" +
                                        "1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1GFi" +
                                        "BZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"\n" +
                                        "    }\n" +
                                        "  ],\n" +
                                        "  \"meta\": {\n" +
                                        "    \"created\": \"2010-01-23T04:56:22Z\",\n" +
                                        "    \"lastModified\": \"2011-05-13T04:42:34Z\",\n" +
                                        "    \"version\": \"W\\/\\\"a330bc54f0671c9\\\"\",\n" +
                                        "    \"location\": \"https://example.com/v1/Users/2819c223-7f76-453a-91" +
                                        "9d-413861904646\"\n" +
                                        "  }\n" +
                                        "}";
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            SCIMResponse scimResponse = userREP.create(userResourceString, SCIMConstants.APPLICATION_JSON,
                                                       SCIMConstants.APPLICATION_JSON, inMemroyUserManager);
            Assert.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseCode());


            JSONDecoder decoder = new JSONDecoder();
            User user = (User) decoder.decodeResource(scimResponse.getResponseMessage(),
                                                      SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                      new User());
            id = user.getId();

            SimpleDateFormat sdf = new SimpleDateFormat(SCIMConstants.dateTimeFormat);

            String responseMessage = scimResponse.getResponseMessage();
            Assert.assertEquals(2607, responseMessage.length());
            Assert.assertTrue(responseMessage, responseMessage.contains("\"schemas\":[\"urn:scim:schemas:core:1.0\"]"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"ims\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"emails\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"phoneNumbers\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"x509Certificates\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"addresses\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"photos\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"addresses\":[{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"meta\":{"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"name\":{"));

            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"someaimhandle\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"babs@jensen.org\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"bjensen@example.com\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"555-555-5555\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"555-555-4444\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"value\":\"https://photos.example" +
                                                                        ".com/profilephoto/72930000000Ccne/T\""));
            Assert.assertTrue(responseMessage, responseMessage.contains
                    ("\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCk" +
                     "NhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0M" +
                     "zFaFw0xMjEwMDQwNjI0MzFaH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtc" +
                     "GxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4" +
                     "YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56A" +
                     "K02bcFLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5ixO8SL7I7SDhcBVJhqVqr3Hg" +
                     "llEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLr" +
                     "T51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/" +
                     "bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyY" +
                     "XRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnz" +
                     "sNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1Z" +
                     "gI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1GFiBZ" +
                     "+GNF/cAYKcMtGcrs2i97ZkJMo=\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"work\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"aim\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"home\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"mobile\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"photo\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"type\":\"thumbnail\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"preferredLanguage\":\"en_US\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"userType\":\"Employee\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"title\":\"Tour Guide\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"nickName\":\"Babs\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"middleName\":\"Jane\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"formatted\":\"Ms. Barbara J Jensen III\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"formatted\":\"100 Universal City " +
                                                                        "Plaza\\nHollywood, CA 91608 USA"));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"timezone\":\"America/Los_Angeles\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"region\":\"CA\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"streetAddress\":\"100 Universal City " +
                                                                        "Plaza\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"postalCode\":\"91608\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"locality\":\"Hollywood\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"country\":\"USA\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"displayName\":\"Babs Jensen\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"profileUrl\":\"https://login.example" +
                                                                        ".com/bjensen\""));

            Assert.assertTrue(responseMessage, responseMessage.contains("\"location\":\"null/" + user.getId()));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"externalId\":\"" + externalId));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"userName\":\"" + externalId));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"lastModified\":\""
                                                                        + sdf.format(user.getLastModified()) + "\""));
            Assert.assertTrue(responseMessage, responseMessage.contains("\"created\":\""
                                                                        + sdf.format(user.getCreatedDate()) + "\""));
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }
    }

    //@Test
    public void testRetrievingUser() {
        UserResourceEndpoint userREP = new UserResourceEndpoint();
        SCIMResponse scimResponse = userREP.get(id, SCIMConstants.APPLICATION_JSON, inMemroyUserManager);
        Assert.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseCode());
    }

    public void testListingUsers(int total) {
        try {
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            SCIMResponse scimResponse = userREP.list(inMemroyUserManager, SCIMConstants.APPLICATION_JSON);
            String jsonString = scimResponse.getResponseMessage();
            JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
            int totalResults = (Integer) jsonObject.opt(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS);
            Assert.assertEquals(total, totalResults);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testDeletingUser() {
        UserResourceEndpoint userREP = new UserResourceEndpoint();
        SCIMResponse scimResponse = userREP.delete(id, inMemroyUserManager, SCIMConstants.APPLICATION_JSON);
        Assert.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseCode());
    }

    public void testRetrievingDeletedUser() {
        UserResourceEndpoint userREP = new UserResourceEndpoint();
        SCIMResponse scimResponse = userREP.get(id, SCIMConstants.APPLICATION_JSON, inMemroyUserManager);
        Assert.assertEquals(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND, scimResponse.getResponseCode());
    }

    //@Test
    public void testCreateUserWithGroup() {
        try {
            User user = new User();
            user.setId("hasini");
            user.setExternalId("hasinig");
            user.setUserName("hasinig");
            user.setGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, "myGroup", null);
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedUser = jsonEncoder.encodeSCIMObject(user);
            UserResourceEndpoint userREP = new UserResourceEndpoint();
            SCIMResponse response = userREP.create(encodedUser, SCIMConstants.APPLICATION_JSON
                    , SCIMConstants.APPLICATION_JSON, inMemroyUserManager);
            if ((ResponseCodeConstants.CODE_CREATED) == response.getResponseCode()) {
                JSONDecoder jsonDecoder = new JSONDecoder();
                User decoderUser = new User();
                decoderUser = (User) jsonDecoder.decodeResource(response.getResponseMessage(),
                                                                SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                                decoderUser);
                if (decoderUser.getGroups() != null && !decoderUser.getGroups().isEmpty()) {
                    Assert.fail("Groups attribute of user is not checked for read-only.");
                }
            } else {
                Assert.fail("User was not created successfully.");
            }
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        }

    }

    //@Test
    public void testAttributeURIs() {
        try {
            User scimUser = new User();
            scimUser.setUserName("hasini");
            scimUser.setHomeEmail("abc_home.com", true);
            scimUser.setWorkEmail("abc_work.com", false);
            scimUser.setGivenName("hasini");
            scimUser.setFamilyName("gunasinghe");
            scimUser.setPhoneNumber("456456", "mobile", false);
            scimUser.setPhoneNumber("777777", "home", true);

            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedSCIMUser = jsonEncoder.encodeSCIMObject(scimUser);
            UserResourceEndpoint userResourceEndpoint = new UserResourceEndpoint();
            SCIMResponse scimResponse = userResourceEndpoint.create(
                    encodedSCIMUser, SCIMConstants.APPLICATION_JSON,
                    SCIMConstants.APPLICATION_JSON, new InMemroyUserManager(0, ""));
            String userString = scimResponse.getResponseMessage();
            JSONDecoder jsonDecoder = new JSONDecoder();
            User user = (User) jsonDecoder.decodeResource(
                    userString, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
            Map<String, Attribute> attributes = user.getAttributeList();
            Attribute userName = attributes.get(SCIMConstants.UserSchemaConstants.USER_NAME);
            //test if URI is set for simple attribute
            Assert.assertEquals(SCIMConstants.USER_NAME_URI, userName.getAttributeURI());
            //test if URI is set for sub attributes of complex attributes
            Attribute nameAttribute = attributes.get(SCIMConstants.UserSchemaConstants.NAME);

            Assert.assertEquals(SCIMConstants.NAME_GIVEN_NAME_URI, nameAttribute.getSubAttribute(
                    SCIMConstants.UserSchemaConstants.GIVEN_NAME).getAttributeURI());
            //test if URI is set for multivalued attribute root and sub

            //root
            Attribute emailAttribute = attributes.get(SCIMConstants.UserSchemaConstants.EMAILS);
            //sub
            Assert.assertEquals(SCIMConstants.EMAILS_URI, emailAttribute.getAttributeURI());
            List<Attribute> attributeList = ((MultiValuedAttribute) emailAttribute).getValuesAsSubAttributes();
            for (Attribute attribute : attributeList) {
                Map<String, Attribute> subAttributes = ((ComplexAttribute) attribute).getSubAttributes();
                SimpleAttribute typeAttribute = (SimpleAttribute) subAttributes.get(
                        SCIMConstants.CommonSchemaConstants.TYPE);
                String typeValue = (String) typeAttribute.getValue();
                if (SCIMConstants.UserSchemaConstants.WORK.equals(typeValue)) {
                    SimpleAttribute valueAttribute = (SimpleAttribute) subAttributes.get(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    Assert.assertEquals(SCIMConstants.WORK_EMAIL_URI, valueAttribute.getAttributeURI());
                }
            }


        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        }
    }

    //@Test
    public void testFilterUsers(String userName) {
        UserResourceEndpoint userREP = new UserResourceEndpoint();
        SCIMResponse scimResponse = userREP.listByFilter("userNameEq" + userName,
                                                         inMemroyUserManager, SCIMConstants.APPLICATION_JSON);
        Assert.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseCode());
        //decode listed resource
        SCIMClient scimClient = new SCIMClient();
        try {
            ListedResource listedResource = scimClient.decodeSCIMResponseWithListedResource(
                    scimResponse.getResponseMessage(), SCIMConstants.JSON, SCIMConstants.USER_INT);
            List<SCIMObject> users = listedResource.getScimObjects();
            String name = null;
            //we expect only one result here.
            for (SCIMObject scimObject : users) {
                name = ((User) scimObject).getUserName();

            }
            Assert.assertEquals(userName, name);
        } catch (CharonException e) {
            logger.error(e.getDescription());
            Assert.fail("Error in decoding the response recieved for filter user: " + userName);
        } catch (BadRequestException e) {
            logger.error(e.getDescription());
            Assert.fail("Error in decoding the response recieved for filter user: " + userName);
        }
    }
}
