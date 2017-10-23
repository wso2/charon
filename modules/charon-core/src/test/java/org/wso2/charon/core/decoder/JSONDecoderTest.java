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
package org.wso2.charon.core.decoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.util.List;

public class JSONDecoderTest {

    @Test
    public void testDecodeSimpleAttributeStringVal() throws JSONException {
        String myAddress = "512/8,High Level Road,\n" + "Pannipitiya.";
        JSONObject encodedAddress = new JSONObject();
        encodedAddress.put("address", myAddress);
    }

    @Test
    public void testDecodeUser() {
        try {
            String fullJSONUser = "{\n" + "  \"schemas\": [\"urn:scim:schemas:core:1.0\"],\n"
                    + "  \"id\": \"2819c223-7f76-453a-919d-413861904646\",\n" + "  \"externalId\": \"701984\",\n"
                    + "  \"userName\": \"bjensen@example.com\",\n" + "  \"name\": {\n"
                    + "    \"formatted\": \"Ms. Barbara J Jensen III\",\n" + "    \"familyName\": \"Jensen\",\n"
                    + "    \"givenName\": \"Barbara\",\n" + "    \"middleName\": \"Jane\",\n"
                    + "    \"honorificPrefix\": \"Ms.\",\n" + "    \"honorificSuffix\": \"III\"\n" + "  },\n"
                    + "  \"displayName\": \"Babs Jensen\",\n" + "  \"nickName\": \"Babs\",\n"
                    + "  \"profileUrl\": \"https://login.example.com/bjensen\",\n" + "  \"emails\": [\n" + "    {\n"
                    + "      \"value\": \"bjensen@example.com\",\n" + "      \"type\": \"work\",\n"
                    + "      \"primary\": true\n" + "    },\n" + "    {\n" + "      \"value\": \"babs@jensen.org\",\n"
                    + "      \"type\": \"home\"\n" + "    }\n" + "  ],\n" + "  \"addresses\": [\n" + "    {\n"
                    + "      \"type\": \"work\",\n" + "      \"streetAddress\": \"100 Universal City Plaza\",\n"
                    + "      \"locality\": \"Hollywood\",\n" + "      \"region\": \"CA\",\n"
                    + "      \"postalCode\": \"91608\",\n" + "      \"country\": \"USA\",\n"
                    + "      \"formatted\": \"100 Universal City Plaza\\nHollywood, CA 91608 USA\",\n"
                    + "      \"primary\": true\n" + "    },\n" + "    {\n" + "      \"type\": \"home\",\n"
                    + "      \"streetAddress\": \"456 Hollywood Blvd\",\n" + "      \"locality\": \"Hollywood\",\n"
                    + "      \"region\": \"CA\",\n" + "      \"postalCode\": \"91608\",\n"
                    + "      \"country\": \"USA\",\n"
                    + "      \"formatted\": \"456 Hollywood Blvd\\nHollywood, CA 91608 USA\"\n" + "    }\n" + "  ],\n"
                    + "  \"phoneNumbers\": [\n" + "    {\n" + "      \"value\": \"555-555-5555\",\n"
                    + "      \"type\": \"work\"\n" + "    },\n" + "    {\n" + "      \"value\": \"555-555-4444\",\n"
                    + "      \"type\": \"mobile\"\n" + "    }\n" + "  ],\n" + "  \"ims\": [\n" + "    {\n"
                    + "      \"value\": \"someaimhandle\",\n" + "      \"type\": \"aim\"\n" + "    }\n" + "  ],\n"
                    + "  \"photos\": [\n" + "    {\n"
                    + "      \"value\": \"https://photos.example.com/profilephoto/72930000000Ccne/F\",\n"
                    + "      \"type\": \"photo\"\n" + "    },\n" + "    {\n"
                    + "      \"value\": \"https://photos.example.com/profilephoto/72930000000Ccne/T\",\n"
                    + "      \"type\": \"thumbnail\"\n" + "    }\n" + "  ],\n" + "  \"userType\": \"\",\n"
                    + "  \"title\": \"Tour Guide\",\n" + "  \"preferredLanguage\":\"en_US\",\n"
                    + "  \"locale\": \"en_US\",\n" + "  \"timezone\": \"America/Los_Angeles\",\n"
                    + "  \"active\":true,\n" + "  \"password\":\"t1meMa$heen\",\n" + "  \"groups\": [\n" + "    {\n"
                    + "      \"display\": \"Tour Guides\",\n" + "      \"value\": \"00300000005N2Y6AA\"\n" + "    },\n"
                    + "    {\n" + "      \"display\": \"Employees\",\n" + "      \"value\": \"00300000005N34H78\"\n"
                    + "    },\n" + "    {\n" + "      \"display\": \"US Employees\",\n"
                    + "      \"value\": \"00300000005N98YT1\"\n" + "    }\n" + "  ],\n" + "  \"x509Certificates\": [\n"
                    + "    {\n"
                    + "      \"value\": \"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFaH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bcFLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5ixO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"\n"
                    + "    }\n" + "  ],\n" + "  \"meta\": {\n" + "    \"created\": \"2010-01-23T04:56:22Z\",\n"
                    + "    \"lastModified\": \"2011-05-13T04:42:34Z\",\n"
                    + "    \"version\": \"W\\/\\\"a330bc54f0671c9\\\"\",\n"
                    + "    \"location\": \"https://example.com/v1/Users/2819c223-7f76-453a-919d-413861904646\"\n"
                    + "  }\n" + "}";
            JSONDecoder jsonDecoder = new JSONDecoder();
            User decodedUser = (User) jsonDecoder
                    .decodeResource(fullJSONUser, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
            Assert.assertEquals("Jensen", decodedUser.getFamilyName());
            Assert.assertEquals("urn:scim:schemas:core:1.0", decodedUser.getSchemaList().get(0));
            Assert.assertEquals("2819c223-7f76-453a-919d-413861904646", decodedUser.getId());
            Assert.assertEquals("701984", decodedUser.getExternalId());
            Assert.assertEquals("bjensen@example.com", decodedUser.getUserName());
            Assert.assertEquals("Ms. Barbara J Jensen III", decodedUser.getFormattedName());
            Assert.assertEquals("Jensen", decodedUser.getFamilyName());
            Assert.assertEquals("Barbara", decodedUser.getGivenName());
            Assert.assertEquals("Jane", decodedUser.getMiddleName());
            Assert.assertEquals("Ms.", decodedUser.getHonorificPrefix());
            Assert.assertEquals("III", decodedUser.getHonorificSuffix());
            Assert.assertEquals("Babs Jensen", decodedUser.getDisplayName());
            Assert.assertEquals("Babs", decodedUser.getNickName());
            Assert.assertEquals("https://login.example.com/bjensen", decodedUser.getProfileURL());
            Assert.assertEquals("bjensen@example.com", decodedUser.getEmails()[0]);
            Assert.assertEquals("babs@jensen.org", decodedUser.getEmails()[1]);
            Assert.assertEquals("bjensen@example.com", decodedUser.getEmailByType("work"));
            Assert.assertEquals("babs@jensen.org", decodedUser.getEmailByType("home"));
            Assert.assertTrue(decodedUser.getActive());

            Assert.assertEquals(decodedUser.getPhoneNumbers("work").get(0), "555-555-5555");
            Assert.assertEquals(decodedUser.getPhoneNumbers("mobile").get(0), "555-555-4444");
            Assert.assertEquals(decodedUser.getIMs("aim").get(0), "someaimhandle");
            Assert.assertNull(decodedUser.getUserType(),
                    "Since the value of user type was empty, this should not be returned from storage");
            Assert.assertEquals(decodedUser.getTitle(), "Tour Guide");
            Assert.assertEquals(decodedUser.getPreferredLanguage(), "en_US");
            Assert.assertEquals(decodedUser.getLocale(), "en_US");
            Assert.assertEquals(decodedUser.getTimeZone(), "America/Los_Angeles");
            Assert.assertTrue(decodedUser.getActive());
            Assert.assertEquals(decodedUser.getPassword(), "t1meMa$heen");
            Assert.assertEquals(decodedUser.getGroups().get(0), "00300000005N2Y6AA");
            Assert.assertEquals(decodedUser.getGroups().get(1), "00300000005N34H78");
            Assert.assertEquals(decodedUser.getGroups().get(2), "00300000005N98YT1");

        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }
    }

    @Test
    public void testDecodeGroup() {
        try {
            String group = "{\n" + "  \"schemas\": [\"urn:scim:schemas:core:1.0\"],\n"
                    + "  \"id\": \"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" + "  \"displayName\": \"Tour Guides\",\n"
                    + "  \"members\": [\n" + "    {\n" + "      \"value\": \"2819c223-7f76-453a-919d-413861904646\",\n"
                    + "      \"display\": \"Babs Jensen\"\n" + "    },\n" + "    {\n"
                    + "      \"value\": \"902c246b-6245-4190-8e05-00816be7344a\",\n"
                    + "      \"display\": \"Mandy Pepperidge\"\n" + "    }\n" + "  ]\n" + "}";
            JSONDecoder jsonDecoder = new JSONDecoder();
            Group decodedGroup = (Group) jsonDecoder
                    .decodeResource(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
            Assert.assertEquals("Tour Guides", decodedGroup.getDisplayName());
            List<String> memberIDs = decodedGroup.getMembers();
            for (String memberID : memberIDs) {
                if ((!"2819c223-7f76-453a-919d-413861904646".equals(memberID))
                        && (!"902c246b-6245-4190-8e05-00816be7344a".equals(memberID))) {
                    Assert.fail("given members do not exist in the group.");
                }
            }
            List<String> displayNames = decodedGroup.getMembersWithDisplayName();
            for (String displayName : displayNames) {
                if ((!"Babs Jensen".equals(displayName)) && (!"Mandy Pepperidge".equals(displayName))) {
                    Assert.fail("given members' display names do not exist in the group.");
                }
            }

            //encode the decoded object and decode again and see
            JSONEncoder jsonEncoder = new JSONEncoder();
            String encodedString = jsonEncoder.encodeSCIMObject(decodedGroup);

            Group secondDecodedGroup = (Group) jsonDecoder
                    .decodeResource(encodedString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
            Assert.assertEquals("Tour Guides", secondDecodedGroup.getDisplayName());
            List<String> newMemberIDs = secondDecodedGroup.getMembers();
            for (String memberID : newMemberIDs) {
                if ((!"2819c223-7f76-453a-919d-413861904646".equals(memberID))
                        && (!"902c246b-6245-4190-8e05-00816be7344a".equals(memberID))) {
                    Assert.fail("given members do not exist in the group.");
                }
            }
            List<String> newDisplayNames = secondDecodedGroup.getMembersWithDisplayName();
            for (String displayName : newDisplayNames) {
                if ((!"Babs Jensen".equals(displayName)) && (!"Mandy Pepperidge".equals(displayName))) {
                    Assert.fail("given members' display names do not exist in the group.");
                }
            }
        } catch (BadRequestException e) {
            Assert.fail(e.getDescription());
        } catch (CharonException e) {
            Assert.fail(e.getDescription());
        }
    }

}
