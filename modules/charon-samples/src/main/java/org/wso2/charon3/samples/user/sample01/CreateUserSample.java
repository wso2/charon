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
package org.wso2.charon3.samples.user.sample01;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * SCIM create user sample.
 */
public class CreateUserSample {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserSample.class);

    private static String createRequestBody = "{\n" +
            "  \"schemas\":\n" +
            "   [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "  \"externalId\": \"701984\",\n" +
            "  \"userName\": \"bjensen@example.com\",\n" +
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
            "  ],\n" +
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
            "       \"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWEx" +
            "FDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFaM" +
            "H8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtl eGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhc" +
            "mJhcmEgSiBKZW5zZW4gSUlJMSIw IAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0B AQEFAAOCA" +
            "Q8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc 1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5P" +
            "hVbXIPMB5vAPKpzz5i PSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZ zidPl8HZ7DhXkZIRtJ" +
            "wBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3 DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbL" +
            "J4zJQBmDr SGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNV HRMEAjAAMCwGCWCGSAGG+EIBDQQ" +
            "fFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZp Y2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAU" +
            " dGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJt Ng5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3" +
            "J7j1ZgI8rAbOkNngX8+pKfTiDz1R C4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF" +
            "/cAYKcMtGcrs2i97ZkJMo=\"\n" +
            "    }\n" +
            "  ],\n" +
            "}";



    public static void main(String[] args) {
        try {
            String url = "http://localhost:8080/scim/v2/Users";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/scim+json");


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(createRequestBody);
                wr.flush();
                wr.close();
            } finally {
                wr.close();
            }

            int responseCode = con.getResponseCode();

            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_CREATED) { // success
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(
                        con.getErrorStream(), "UTF-8"));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //printing result from response
            logger.info("Response Code : " + responseCode);
            logger.info("Response Message : " + con.getResponseMessage());
            logger.info("Response Content : " + response.toString());


        } catch (ProtocolException e) {
            logger.error(e.getMessage());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
