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
package org.wso2.charon.samples.user.sample05;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

public class UpdateUserSample {

    private static String userID = "8fc12385-d364-48dc-a9b1-cf58efe464a5";

    private static String newDisplayName = "HasiniGT";
    private static String newWorkEmail = "hasini@wso2.com";


    public static void main(String[] args) {
        //retrieve existing user
        try {
            //set the keystore
            SampleConstants.setKeyStore();
            //create SCIM client
            SCIMClient scimClient = new SCIMClient();
            //create a apache wink ClientHandler to intercept and identify response messages
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            //set the handler in wink client config
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            //create a wink rest client with the above config
            RestClient restClient = new RestClient(clientConfig);

            BasicAuthInfo basicAuthInfo = new BasicAuthInfo();
            basicAuthInfo.setUserName(SampleConstants.CRED_USER_NAME);
            basicAuthInfo.setPassword(SampleConstants.CRED_PASSWORD);

            BasicAuthHandler basicAuthHandler = new BasicAuthHandler();
            BasicAuthInfo encodedBasicAuthInfo = (BasicAuthInfo) basicAuthHandler.getAuthenticationToken(basicAuthInfo);

            //create resource endpoint to access a known user resource.
            Resource userResource = restClient.resource(SampleConstants.USER_ENDPOINT + userID);
            String response = userResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON)
                    .get(String.class);
            System.out.println("Retrieved user: " + response);

            //decode User
            User decodedUser = (User) scimClient.decodeSCIMResponse(response, SCIMConstants.JSON, 1);
            //do changes
            decodedUser.setDisplayName(newDisplayName);
            decodedUser.setWorkEmail(newWorkEmail, true);

            //encode updated user
            String updatedUserString = scimClient.encodeSCIMObject(decodedUser, SCIMConstants.JSON);
            //update user
            Resource updateUserResource = restClient.resource(SampleConstants.USER_ENDPOINT + userID);
            String responseUpdated = updateUserResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON)
                    .put(String.class, updatedUserString);
            System.out.println("Updated user: " + responseUpdated);
            //decode the response
        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        } catch (BadRequestException e) {
            System.out.println("Error in decoding the retrieved user...");
        } catch (CharonException e) {
            System.out.println("Error in decoding the retrieved user...");
        }
    }
}
