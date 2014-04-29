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
package org.wso2.charon.samples.group.sample05;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

public class UpdateGroupSample {
    private static String groupID = "5df03149-bf48-4192-8c38-5d1b841b83fb";
    private static String oldMember = "28849d61-4dbe-4f5e-a2dd-cdc01d85e315";
    private static String newMember = "cef02a51-e488-4b08-baee-2a1ee909011a";
    private static String newDisplayName = "QA";

    public static void main(String[] args) {

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
            Resource groupResource = restClient.resource(SampleConstants.GROUP_ENDPOINT + groupID);
            String response = groupResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON)
                    .get(String.class);

            System.out.println("Retrieved group: "+response);
            //decode retrieved group
            Group decodedGroup = (Group) scimClient.decodeSCIMResponse(response, SCIMConstants.JSON, 2);

            decodedGroup.setDisplayName(newDisplayName);
            decodedGroup.removeMember(oldMember);
            decodedGroup.setGroupMember(newMember);

            String updatedGroupString = scimClient.encodeSCIMObject(decodedGroup, SCIMConstants.JSON);

            Resource updateGroupResource = restClient.resource(SampleConstants.GROUP_ENDPOINT + groupID);
            String responseUpdated = updateGroupResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON)
                    .put(String.class, updatedGroupString);
            System.out.println("Updated group: " + responseUpdated);
            //decode the response
            //System.out.println(response);
        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        } catch (BadRequestException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CharonException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
