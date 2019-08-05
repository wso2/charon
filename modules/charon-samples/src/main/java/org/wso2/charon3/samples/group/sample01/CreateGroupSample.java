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
package org.wso2.charon3.samples.group.sample01;

import org.apache.log4j.BasicConfigurator;
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
 * SCIM create group sample.
 */
public class CreateGroupSample {

    private static final Logger logger = LoggerFactory.getLogger(CreateGroupSample.class);

    private static String createRequestBody = "{\n" +
            "     \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
            "     \"displayName\": \"Doctors\",\n" +
            "     \"members\": [\n" +
            "       {\n" +
            "         \"value\": \"e01b5773-c8f3-446d-8958-31c603b65660\",\n" +
            "         \"$ref\":\n" +
            "   \"https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646\",\n" +
            "         \"display\": \"Babs Jensen\"\n" +
            "       },\n" +
            "       {\n" +
            "         \"value\": \"902c246b-6245-4190-8e05-00816be7344a\",\n" +
            "         \"$ref\":\n" +
            "   \"https://example.com/v2/Users/902c246b-6245-4190-8e05-00816be7344a\",\n" +
            "         \"display\": \"Mandy Pepperidge\"\n" +
            "       }\n" +
            "     ]\n" +
            "     }";


    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            String url = "http://localhost:8080/scim/v2/Groups";
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
