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
package org.wso2.charon.samples.bulk;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.objects.bulk.BulkData;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

/**
 * Created with IntelliJ IDEA.
 * User: dinuka
 * Date: 1/2/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateUsers {
    //user details
    private static String userName = "HasiniG";
    private static String externalID = "hasini@wso2.com";
    private static String[] emails = {"hasini@gmail.com", "hasinig@yahoo.com"};
    private static String displayName = "Hasini";
    private static String password = "dummyPW1";
    private static String language = "Sinhala";
    private static String phone_number = "0772508354";
    private static String bulk_id1 = "bulkIDUser1";
    private static String method1 = "POST";

    private static String userName2 = "Dinuka DMP";
    private static String externalID2 = "dinukam@wso2.com";
    private static String[] emails2 = {"dinuka.malalanayake@gmail.com", "dinuka_malalanayake@yahoo.com"};
    private static String displayName2 = "Dinuka";
    private static String password2 = "myPassword";
    private static String language2 = "Sinhala";
    private static String phone_number2 = "0772508354";
    private static String bulk_id2 = "bulkIDUser2";
    private static String method2 = "POST";

    private static String userName3 = "PrabathS";
    private static String externalID3 = "prabath@wso2.com";
    private static String[] emails3 = {"prabath@gmail.com", "prabath@yahoo.com"};
    private static String displayName3 = "Prabath";
    private static String password3 = "myPassword";
    private static String language3 = "Sinhala";
    private static String phone_number3 = "0772508354";
    private static String bulk_id3 = "bulkIDUser3";
    private static String method3 = "POST";

    public static void main(String[] args) {

        try {
            //set the keystore
            SampleConstants.setKeyStore();
            //create SCIM client
            SCIMClient scimClient = new SCIMClient();
            //create a user according to SCIM User Schema

            //encode the user in JSON format
            String encodedUser;
            //====================
            BulkData bulkData = scimClient.createBulkRequestData();
            bulkData.setFailOnErrors(2);
            bulkData.addSchemas("urn:scim:schemas:core:1.0");

            User scimUser1 = scimClient.createUser();
            scimUser1.setUserName(userName);
            scimUser1.setExternalId(externalID);
            scimUser1.setEmails(emails);
            scimUser1.setDisplayName(displayName);
            scimUser1.setPassword(password);
            scimUser1.setPreferredLanguage(language);
            scimUser1.setPhoneNumber(phone_number, null, false);
            scimUser1.setBulkID(bulk_id1);
            scimUser1.setPath(SCIMConstants.CommonSchemaConstants.USERS_PATH);
            scimUser1.setMethod(method1);

            User scimUser2 = scimClient.createUser();
            scimUser2.setUserName(userName2);
            scimUser2.setExternalId(externalID2);
            scimUser2.setEmails(emails2);
            scimUser2.setDisplayName(displayName2);
            scimUser2.setPassword(password2);
            scimUser2.setPreferredLanguage(language2);
            scimUser2.setPhoneNumber(phone_number2, null, false);
            scimUser2.setBulkID(bulk_id2);
            scimUser2.setPath(SCIMConstants.CommonSchemaConstants.USERS_PATH);
            scimUser2.setMethod(method2);

            User scimUser3 = scimClient.createUser();
            scimUser3.setUserName(userName3);
            scimUser3.setExternalId(externalID3);
            scimUser3.setEmails(emails3);
            scimUser3.setDisplayName(displayName3);
            scimUser3.setPassword(password3);
            scimUser3.setPreferredLanguage(language3);
            scimUser3.setPhoneNumber(phone_number3, null, false);
            scimUser3.setBulkID(bulk_id3);
            scimUser3.setPath(SCIMConstants.CommonSchemaConstants.USERS_PATH);
            scimUser3.setMethod(method3);

            bulkData.addUser(scimUser1);
            bulkData.addUser(scimUser2);
            bulkData.addUser(scimUser3);

            String encodedString = scimClient.encodeSCIMObject(bulkData, SCIMConstants.JSON);

            System.out.println(encodedString);

            //create a apache wink ClientHandler to intercept and identify response messages
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            //set the handler in wink client config
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            //create a wink rest client with the above config
            RestClient restClient = new RestClient(clientConfig);
            //create resource endpoint to access User resource
            Resource userResource = restClient.resource(SampleConstants.BULK_ENDPOINT);

            BasicAuthInfo basicAuthInfo = new BasicAuthInfo();
            basicAuthInfo.setUserName(SampleConstants.CRED_USER_NAME);
            basicAuthInfo.setPassword(SampleConstants.CRED_PASSWORD);

            BasicAuthHandler basicAuthHandler = new BasicAuthHandler();
            BasicAuthInfo encodedBasicAuthInfo = (BasicAuthInfo) basicAuthHandler.getAuthenticationToken(basicAuthInfo);

            //System.out.println(encodedUser);
            //TODO:enable, disable SSL. For the demo purpose, we make the calls over http
            //send previously registered SCIM consumer credentials in http headers.
            String response = userResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedString);

            //decode the response
            System.out.println(response);

        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        } catch (CharonException e) {
            e.printStackTrace();
        } catch (BadRequestException e) {
            e.printStackTrace();
        }
    }
}
