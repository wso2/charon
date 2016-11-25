/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.v2;

import org.wso2.charon.core.v2.config.CharonConfiguration;
import org.wso2.charon.core.v2.config.SCIMUserSchemaExtensionBuilder;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.protocol.SCIMResponse;
import org.wso2.charon.core.v2.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon.core.v2.protocol.endpoints.ServiceProviderConfigResourceManager;
import org.wso2.charon.core.v2.protocol.endpoints.UserResourceManager;
import org.wso2.charon.core.v2.schema.SCIMConstants;
import org.wso2.charon.core.v2.exceptions.InternalErrorException;
import org.wso2.charon.core.v2.utils.codeutils.SearchRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is only for testing purpose.
 */
public class Test {

   public static void main(String [] args){
       //config charon
       CharonConfiguration.getInstance().setDocumentationURL("http://example.com/help/scim.html");
       CharonConfiguration.getInstance().setBulkSupport(false, 100, 1048576);
       CharonConfiguration.getInstance().setSortSupport(false);
       CharonConfiguration.getInstance().setETagSupport(false);
       CharonConfiguration.getInstance().setChangePasswordSupport(true);
       CharonConfiguration.getInstance().setFilterSupport(true, 100);
       CharonConfiguration.getInstance().setPatchSupport(false);
       Object [] auth1 = {"OAuth Bearer Token",
               "Authentication scheme using the OAuth Bearer Token Standard",
               "http://www.rfc-editor.org/info/rfc6750",
               "http://example.com/help/oauth.html",
               "oauthbearertoken",
               true};
       Object [] auth2 = {"HTTP Basic",
               "Authentication scheme using the HTTP Basic Standard",
               "http://www.rfc-editor.org/info/rfc2617",
               "http://example.com/help/httpBasic.html",
               "httpbasic",
               false};
       ArrayList<Object[]> authList = new ArrayList<Object[]>();
       authList.add(auth1);
       authList.add(auth2);
       CharonConfiguration.getInstance().setAuthenticationSchemes(authList);
       CharonConfiguration.getInstance().setCountValueForPagination(2);

       //------------------------------------------------------------------

       ServiceProviderConfigResourceManager sm= new ServiceProviderConfigResourceManager();


       HashMap hmp=new HashMap<String,String>();
       hmp.put(SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT,"http://localhost:8080/scim/v2/ServiceProviderConfig");
       hmp.put(SCIMConstants.USER_ENDPOINT,"http://localhost:8080/scim/v2/Users");
       sm.setEndpointURLMap(hmp);

       UserResourceManager um =new UserResourceManager();

       //-----Extension User schema support------
       SCIMUserSchemaExtensionBuilder extensionBuilder= new SCIMUserSchemaExtensionBuilder();
       try {
           extensionBuilder.buildUserSchemaExtension("/home/vindula/Desktop/Charon-3.0/scim-schema-extension.config");
       } catch (CharonException e) {
           e.printStackTrace();
       } catch (InternalErrorException e) {
           e.printStackTrace();
       }


       String array ="{\n" +
               "  \"schemas\":\n" +
               "    [\"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
               "      \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"],\n" +
               "  \"externalId\": \"701984\",\n" +
               "  \"userName\": \"Bihan\",\n" +
               "  \"name\": {\n" +
               "    \"formatted\": \"Ms. Barbara J Jensen, III\",\n" +
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
               "      \"streetAddress\": \"100 Universal City Plaza\",\n" +
               "      \"locality\": \"Hollywood\",\n" +
               "      \"region\": \"CA\",\n" +
               "      \"postalCode\": \"91608\",\n" +
               "      \"country\": \"USA\",\n" +
               "      \"formatted\": \"100 Universal City Plaza\\nHollywood, CA 91608 USA\",\n" +
               "      \"type\": \"work\",\n" +
               "      \"primary\": true\n" +
               "    },\n" +
               "{\n" +
               "      \"streetAddress\": \"456 Hollywood Blvd\",\n" +
               "      \"locality\": \"Hollywood\",\n" +
               "      \"region\": \"CA\",\n" +
               "      \"postalCode\": \"91608\",\n" +
               "      \"country\": \"USA\",\n" +
               "      \"formatted\": \"456 Hollywood Blvd\\nHollywood, CA 91608 USA\",\n" +
               "      \"type\": \"home\"\n" +
               "     }\n" +
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
               "      \"value\":\n" +
               "        \"https://photos.example.com/profilephoto/72930000000Ccne/F\",\n" +
               "      \"type\": \"photo\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"value\":\n" +
               "        \"https://photos.example.com/profilephoto/72930000000Ccne/T\",\n" +
               "      \"type\": \"thumbnail\"\n" +
               "    }\n" +
               "  ],\n"+
               " \"userType\": \"Employee\",\n" +
               "  \"title\": \"Tour Guide\",\n" +
               "  \"preferredLanguage\": \"en-US\",\n" +
               "  \"locale\": \"en-US\",\n" +
               "  \"timezone\": \"America/Los_Angeles\",\n" +
               "  \"active\":true,\n" +
               "  \"password\": \"time\",\n" +
               "  \"groups\": [\n" +
               "    {\n" +
               "      \"value\": \"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
               "      \"$ref\": \"../Groups/e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
               "      \"display\": \"Tour Guides\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"value\": \"fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
               "      \"$ref\": \"../Groups/fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
               "      \"display\": \"Employees\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"value\": \"71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7\",\n" +
               "      \"$ref\": \"../Groups/71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7\",\n" +
               "      \"display\": \"US Employees\"\n" +
               "    }\n" +
               "  ],\n" +
               "  \"x509Certificates\": [\n" +
               "    {\n" +
               "      \"value\":\n" +
               "       \"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFaMH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtl eGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIw IAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0B AQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc 1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5i PSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZ zidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3 DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDr SGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNV HRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZp Y2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAU dGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJt Ng5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1R C4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"\n" +
               "    }\n" +
               "  ],\n" +
               "}";

       String attributes="nickName,photos.value,EnterpriseUser.manager.value";
       String excludeAttributes="externalId,emails.value,EnterpriseUser.manager";

       //----CREATE USER --------
       SCIMResponse res=um.create(array,new SCIMUserManager(),null, excludeAttributes);


       //-----GET USER  ---------
       //SCIMResponse res= um.get("b1fac6b4-85ae-4945-bfe1-1360b771b6e4",new SCIMUserManager(),null,null);

       //-----DELETE USER  ---------
       //SCIMResponse res= um.delete("cf712155-e974-42ae-9e57-6c42f7bbadad",new SCIMUserManager());

       //-----LIST USER  ---------
      // SCIMResponse res= um.list(new SCIMUserManager(),attributes,null);

       //-----LIST USER WITH PAGINATION ---------
       //SCIMResponse res= um.listWithPagination(1,7,new SCIMUserManager(),null,null);

       //-----UPDATE USER VIA PUT ---------
       //SCIMResponse res= um.updateWithPUT("042e8851-718c-469f-9a2b-548bf08830db",array,new SCIMUserManager(),null,null);

       //-----FILTER AT USER ENDPOINT ---------
       String filter ="wso2Extension.dogs.value eq johan@wso2.com";
       //SCIMResponse res= um.listByFilter(filter, new SCIMUserManager(), attributes, null);

       //-----LIST USERS WITH SORT ---------
       //SCIMResponse res= um.listBySort(null,"AsCEnding",new SCIMUserManager(),attributes,null);


       String patch_Request = "{\n" +
               "     \"schemas\":\n" +
               "       [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\n" +
               "     \"Operations\":[{\n" +
               "       \"op\":\"add\",\n" +
               "       \"value\":{\n" +
               "         \"emails\":[\n" +
               "           {\n" +
               "             \"value\":\"babs@jensen.org\",\n" +
               "             \"type\":\"home\"\n" +
               "           }\n" +
               "         ],\n" +
               "         \"nickname\":\"Babs\"\n" +
               "       }\n"+
               "     }]\n" +
               "   }";

       //-----UPDATE USERS WITH PATCH ---------
       //SCIMResponse res= um.updateWithPATCH("3f0dda41-ce08-497c-917e-12b803acdfb3", patch_Request, new SCIMUserManager(),null, null);


       //-----SERVICE PROVIDER CONFIG  ---------
       //SCIMResponse res= sm.get(null, null, null, null);

       //-----RESOURCE TYPE CONFIG  ---------
       //SCIMResponse res= rm.get(null, null, null, null);


       String x = "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"],\n" +
               "     \"attributes\": [\"displayName\", \"userName\"],\n" +
               "     \"filter\":\n" +
               "       \"displayName sw \\\"smith\\\"\",\n" +
               "     \"startIndex\": 1,\n" +
               "     \"count\": 10}";
       //-----LIST USER  ---------
       //SCIMResponse res= um.listUsersWithPOST(x ,new SCIMUserManager());

       System.out.println(res.getResponseStatus());
       System.out.println("");
       System.out.println(res.getHeaderParamMap());
       System.out.println("");
       System.out.println(res.getResponseMessage());
   }

}
