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
package org.wso2.charon.samples.registration;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.CharonConstants;

public class RegisterTenantSample {

    public static void main(String[] args) {

        //create SCIM Client
        SCIMClient scimClient = new SCIMClient();
        //create a apache wink ClientHandler to intercept and identify response messages
        CharonResponseHandler responseHandler = new CharonResponseHandler();
        responseHandler.setSCIMClient(scimClient);
        //set the handler in wink client config
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.handlers(new ClientHandler[]{responseHandler});
        //create a wink rest client with the above config
        RestClient restClient = new RestClient(clientConfig);

        //create resource endpoint to access RegistrationService
        Resource registrationService = restClient.resource(SampleConstants.REG_SERVICE_ENDPOINT);

        //TODO:enable, disable SSL. For the demo purpose, we make the calls over http

        //send the tenant details in the http headers. TODO: send them in the body encoded in json/xml
        registrationService.header(CharonConstants.TENANT_ADMIN_USER_NAME, SampleConstants.CRED_USER_NAME).
                header(CharonConstants.TENANT_ADMIN_PASSWORD, SampleConstants.CRED_PASSWORD).
                header(CharonConstants.TENANT_DOMAIN, SampleConstants.CRED_TENANT_DOMAIN).
                header(CharonConstants.AUTH_MECHANISM, SCIMConstants.AUTH_TYPE_BASIC).post(String.class, "");

    }

}
