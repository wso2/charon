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
package org.wso2.charon.samples.user.sample03;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

public class DeleteUserSample {

    private static String userID = "664a96a5-ed6c-4220-9b3d-9f9e8afe54c6";

    public static void main(String[] args) {

        try {
            //set the keystore
            SampleConstants.setKeyStore();
            ClientConfig clientConfig = new ClientConfig();
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(new SCIMClient());
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            RestClient restClient = new RestClient(clientConfig);

            //create resource endpoint
            Resource userResource = restClient.resource(SampleConstants.USER_ENDPOINT + userID);
            BasicAuthInfo basicAuthInfo = new BasicAuthInfo();
            basicAuthInfo.setUserName(SampleConstants.CRED_USER_NAME);
            basicAuthInfo.setPassword(SampleConstants.CRED_PASSWORD);

            BasicAuthHandler basicAuthHandler = new BasicAuthHandler();
            BasicAuthInfo encodedBasicAuthInfo = (BasicAuthInfo) basicAuthHandler.getAuthenticationToken(basicAuthInfo);

            //enable, disable SSL.
            //had to set content type for the delete request as well, coz wink client sets */* by default.
            String response = userResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    accept(SCIMConstants.APPLICATION_JSON).
                    delete(String.class);

            //decode the response
            System.out.println(response);
        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        }

    }

}
