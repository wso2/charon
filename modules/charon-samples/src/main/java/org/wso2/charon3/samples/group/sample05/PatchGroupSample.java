/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.charon3.samples.group.sample05;


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
import java.util.Scanner;

/**
 * SCIM patch group sample..
 */
public class PatchGroupSample {

    private static final Logger logger = LoggerFactory.getLogger(PatchGroupSample.class);

    private static String patchRequestBody = "{ \"schemas\":\n" +
            "      [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\n" +
            "     \"Operations\":[\n" +
            "       {\n" +
            "        \"op\":\"replace\",\n" +
            "        \"path\":\"members[value eq 902c246b-6245-4190-8e05-00816be7344a].display\",\n" +
            "        \"value\":\"Vindula\"\n" +
            "       },\n" +
            "     ]\n" +
            "   }";

    public static void main(String[] args) {
        //get the id of the user
        Scanner reader = new Scanner(System.in, "UTF-8");  // Reading from System.in
        System.out.print("Enter the group ID : ");
        String id = reader.next();
        try {
            String url = "http://localhost:8080/scim/v2/Groups/"  + id;;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("PATCH");
            con.setRequestProperty("Content-Type", "application/scim+json");


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(patchRequestBody);
                wr.flush();
                wr.close();
            } finally {
                wr.close();
            }

            int responseCode = con.getResponseCode();

            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
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
