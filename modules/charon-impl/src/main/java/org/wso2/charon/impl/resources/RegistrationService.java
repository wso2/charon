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
package org.wso2.charon.impl.resources;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.extensions.AuthenticationInfo;
import org.wso2.charon.core.extensions.CharonManager;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.utils.CharonConstants;
import org.wso2.charon.utils.DefaultCharonManager;
import org.wso2.charon.utils.storage.TenantInfo;
import org.wso2.charon.utils.jaxrs.JAXRSResponseBuilder;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Additional/Helper REST API to register SCIM consumers as tenants.
 * In a cloud environment, there will be multiple tenants.
 * In a stand alone impl, there will be only one tenant.
 */
@Path("/RegistrationService")
public class RegistrationService {
    /**
     * Register at SCIM SP and obtain authentication token in the response, according to the relevant
     * authentication mechanism that is requested.
     * i.e: if basic auth is used, no need to return, if oauth is used, return oauth bearer token.
     *
     * @param adminUserName
     * @param adminPassword
     * @param tenantDomain
     * @param authMechanism
     * @return
     */
    @POST
    public Response registerTenant(
            @HeaderParam(CharonConstants.TENANT_ADMIN_USER_NAME) String adminUserName,
            @HeaderParam(CharonConstants.TENANT_ADMIN_PASSWORD) String adminPassword,
            @HeaderParam(CharonConstants.TENANT_DOMAIN) String tenantDomain,
            @HeaderParam(CharonConstants.AUTH_MECHANISM) String authMechanism) {
        try {
            CharonManager charonManger = DefaultCharonManager.getInstance();

            TenantInfo tenantInfo = new TenantInfo();
            tenantInfo.setAuthenticationMechanism(authMechanism);
            tenantInfo.setTenantAdminUserName(adminUserName);
            tenantInfo.setTenantAdminPassword(adminPassword);
            tenantInfo.setTenantDomain(tenantDomain);
            AuthenticationInfo authInfo = charonManger.registerTenant(tenantInfo);
            /*for the moment, we get the auth token as a string and return it in response,
            in a more generalized way, we can encode authentication info and return it int the response.*/
            String authToken = null;
            if (authInfo != null) {
                authToken = authInfo.getAuthenticationToken();
            }
            SCIMResponse successResponse = new SCIMResponse(ResponseCodeConstants.CODE_OK, authToken);
            return new JAXRSResponseBuilder().buildResponse(successResponse);

        } catch (CharonException e) {
            SCIMResponse faultyResponse = new SCIMResponse(
                    ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR, e.getDescription());
            return new JAXRSResponseBuilder().buildResponse(faultyResponse);
        }
    }

}
