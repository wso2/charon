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
package org.wso2.charon.samples.utils;

import org.apache.wink.client.ClientRequest;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.handlers.ClientHandler;
import org.apache.wink.client.handlers.HandlerContext;

public class BasicAuthClientHandler implements ClientHandler {


    public ClientResponse handle(ClientRequest clientRequest, HandlerContext handlerContext)
            throws Exception {

        clientRequest.getHeaders().add("userName", SampleConstants.CRED_USER_NAME);
        clientRequest.getHeaders().add("password", SampleConstants.CRED_PASSWORD);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    //TODO:better put an exception handler to decode Status code in response headers and throw relevant exceptions. 
}
