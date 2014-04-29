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
package org.wso2.charon.utils;

import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.exceptions.UnauthorizedException;
import org.wso2.charon.core.extensions.AuthenticationHandler;
import org.wso2.charon.core.extensions.AuthenticationInfo;
import org.wso2.charon.core.extensions.CharonManager;
import org.wso2.charon.core.extensions.TenantDTO;
import org.wso2.charon.core.extensions.TenantManager;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;
import org.wso2.charon.utils.storage.InMemoryTenantManager;
import org.wso2.charon.utils.storage.InMemroyUserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This illustrates what are the core tasks an implementation should take care of,
 * according to their specific implementation, and how the extension points and utils
 * implementation provided by charon can be initialized/utilized here.
 */
public class DefaultCharonManager implements CharonManager {

    /*private static AuthenticationHandler authenticationHandler;
    private static AuthenticationInfo authenticationInfo;*/
    private TenantManager tenantManager;
    private static volatile DefaultCharonManager defaultCharonManager;
    private static Map<String, Encoder> encoderMap = new HashMap<String, Encoder>();
    private static Map<String, Decoder> decoderMap = new HashMap<String, Decoder>();
    private static Map<String, Map> authenticators = new HashMap<String, Map>();
    private static Map<String, String> endpointURLs = new HashMap<String, String>();

    private static Map<Integer, UserManager> userManagers = new ConcurrentHashMap<Integer, UserManager>();
    private static final String INSTANCE = "instance";

    //TODO:should be moved to charon-config
    private static final String USERS_URL = "http://localhost:8080/charonDemoApp/scim/Users";
    private static final String GROUPS_URL = "http://localhost:8080/charonDemoApp/scim/Groups";

    /**
     * Perform initialization.
     */
    private void init() throws CharonException {
        //TODO:read config and init stuff, if nothing in config, make sure to initialize default stuff. 
        tenantManager = new InMemoryTenantManager();

        //if no encoder/decoders provided by the configuration, register defaults.
        encoderMap.put(SCIMConstants.JSON, new JSONEncoder());
        decoderMap.put(SCIMConstants.JSON, new JSONDecoder());

        //create basic auth - authenticator property
        Map<String, Object> basicAuthAuthenticator = new HashMap<String, Object>();
        basicAuthAuthenticator.put(INSTANCE, new BasicAuthHandler());
        basicAuthAuthenticator.put(SCIMConstants.AUTH_PROPERTY_PRIMARY, true);
        //add basic auth authenticator properties to authenticators list.
        authenticators.put(SCIMConstants.AUTH_TYPE_BASIC, basicAuthAuthenticator);
        //register encoder,decoders in AbstractResourceEndpoint, since they are called with in the API
        registerCoders();
        //Define endpoint urls to be used in Location Header
        endpointURLs.put(SCIMConstants.USER_ENDPOINT, USERS_URL);
        endpointURLs.put(SCIMConstants.GROUP_ENDPOINT, GROUPS_URL);
        //register endpoint URLs in AbstractResourceEndpoint since they are called with in the API
        registerEndpointURLs();
        //register a default user manager
        UserManager userManager = new InMemroyUserManager(0, "wso2.com");
        userManagers.put(0, userManager);
    }

    private DefaultCharonManager() throws CharonException {
        init();
    }

    /**
     * Should return the static instance of CharonManager implementation.
     * Read the config and initialize extensions as specified in the config.
     *
     * @return
     */
    public static DefaultCharonManager getInstance() throws CharonException {
        if (defaultCharonManager == null) {
            synchronized (DefaultCharonManager.class) {
                if (defaultCharonManager == null) {
                    defaultCharonManager = new DefaultCharonManager();
                    return defaultCharonManager;
                } else {
                    return defaultCharonManager;
                }
            }
        } else {
            return defaultCharonManager;
        }
    }

    /**
     * Obtain the encoder for the given format.
     *
     * @return
     */
    @Override
    public Encoder getEncoder(String format) throws FormatNotSupportedException {
        if (!encoderMap.containsKey(format)) {
            //Error is logged by the caller.
            throw new FormatNotSupportedException(ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED,
                                                  ResponseCodeConstants.DESC_FORMAT_NOT_SUPPORTED);
        }
        return encoderMap.get(format);
    }

    /**
     * Obtain the decoder for the given format.
     *
     * @return
     */
    @Override
    public Decoder getDecoder(String format) throws FormatNotSupportedException {
        if (!decoderMap.containsKey(format)) {
            //Error is logged by the caller.
            throw new FormatNotSupportedException(ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED,
                                                  ResponseCodeConstants.DESC_FORMAT_NOT_SUPPORTED);
        }
        return decoderMap.get(format);
    }

    /**
     * Obtain the authentication handler, given the authentication mechanism.
     *
     * @return
     */
    @Override
    public AuthenticationHandler getAuthenticationHandler(String authMechanism)
            throws CharonException {
        if (authenticators.size() != 0) {
            Map authenticatorProperties = authenticators.get(authMechanism);
            if (authenticatorProperties != null && authenticatorProperties.size() != 0) {
                return (AuthenticationHandler) authenticatorProperties.get(INSTANCE);
            }
        }
        String error = "Requested authentication mechanism is not supported.";
        throw new CharonException(error);
    }

    /**
     * Obtain the user manager, after identifying the tenantId of the tenantAdminUser
     * who invokes the SCIM API exposed by the service provider.
     *
     * @param tenantAdminUserName
     * @return
     */
    @Override
    public UserManager getUserManager(String tenantAdminUserName) throws CharonException {
        //return the default user manager
        return userManagers.get(0);
        //identify tenant id and domain
        /*int tenantId = tenantManager.getTenantID(tenantAdminUserName);
        String tenantDomain = tenantManager.getTenantDomain(tenantAdminUserName);
        UserManager userManager;
        if ((userManagers != null) && (userManagers.size() != 0)) {
            if (userManagers.get(tenantId) != null) {
                return userManagers.get(tenantId);
            } else {
                userManager = new InMemroyUserManager(tenantId, tenantDomain);
                userManagers.put(tenantId, userManager);
                return userManager;
            }
        } else {
            userManager = new InMemroyUserManager(tenantId, tenantDomain);
            userManagers.put(tenantId, userManager);
            return userManager;
        }*/
    }

    /**
     * Obtain the the instance of registered tenant manager implementation.
     *
     * @return
     */
    @Override
    public TenantManager getTenantManager() {
        return tenantManager;
    }

    /**
     * Create the tenant in the particular tenant manager, given the tenant info.
     * Purpose of registering is, creating a tenant specific storage space in the service provider
     * as well as obtaining credentials to access SCIM SP API for subsequent provisioning activities.
     * Therefore. proper credentials should be returned according to the authentication handler.
     *
     * @param tenantInfo
     * @return
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    @Override
    public AuthenticationInfo registerTenant(TenantDTO tenantInfo) throws CharonException {
        this.getTenantManager().createTenant(tenantInfo);
        //check whether the requested authentication mechanism supported.
        /*if (this.isAuthenticationSupported(tenantInfo.getAuthenticationMechanism())) {

            this.getTenantManager().createTenant(tenantInfo);
            //if auth mechanism is http basic auth, we skip the step of obtaining and returning auth token.
            if (SCIMConstants.AUTH_TYPE_BASIC.equals(tenantInfo.getAuthenticationMechanism())) {
                return null;
            } else {
                //if auth type is oauth, obtain the oauth bearer token and return.
                return this.getAuthenticationHandler(
                        tenantInfo.getAuthenticationMechanism()).getAuthenticationToken(
                        this.getAuthInfo(tenantInfo));
            }

        } else {
            String errorMessage = "Requested authentication mechanism not supported.";
            throw new CharonException(errorMessage);
        }*/
        return null;
    }

    /**
     * Returns true if the registered authenticators support the given authentication mechanism.
     *
     * @param authmechanism
     * @return
     */
    @Override
    public boolean isAuthenticationSupported(String authmechanism) {
        return authenticators.containsKey(authmechanism);
    }

    @Override
    public AuthenticationInfo handleAuthentication(Map<String, String> httpAuthHeaders)
            throws UnauthorizedException {
        try {
            //identify authentication mechanism according to the http headers sent
            String authenticationMechanism = identifyAuthMechanism(httpAuthHeaders);
            //create authentication info according to the auth mechanism
            if (SCIMConstants.AUTH_TYPE_BASIC.equals(authenticationMechanism)) {
                BasicAuthInfo authInfo = new BasicAuthInfo();
                //get authorization header from http headers.
                //put in auth info
                authInfo.setAuthorizationHeader(httpAuthHeaders.get(SCIMConstants.AUTHORIZATION_HEADER));
                //authenticator.isAuthenticated

                //get the authentication handler.
                BasicAuthHandler basicAuthHandler = (BasicAuthHandler) authenticators.get(
                        SCIMConstants.AUTH_TYPE_BASIC).get(INSTANCE);
                //pass a handler of charon manager to auth handler
                basicAuthHandler.setCharonManager(getInstance());
                //if not authenticated only, throw 401 exception.
                if (!basicAuthHandler.isAuthenticated(httpAuthHeaders)) {
                    throw new UnauthorizedException();
                }
                //return auth info if successfully authenticated
                return basicAuthHandler.decodeBasicAuthHeader(httpAuthHeaders.get(
                        SCIMConstants.AUTHORIZATION_HEADER));
            } else if (authenticationMechanism.equals(SCIMConstants.AUTH_TYPE_OAUTH)) {
                //perform authentication according to oauth.
            }
        } catch (CharonException e) {
            throw new UnauthorizedException(e.getDescription());
        }
        return null;
    }

    /**
     * Identify the authentication mechanism, given the http headers sent in the SCIM API access request.
     *
     * @param authHeaders
     * @return
     * @throws CharonException
     */
    public String identifyAuthMechanism(Map<String, String> authHeaders) throws CharonException {
        String authorizationHeader = authHeaders.get(SCIMConstants.AUTHORIZATION_HEADER);
        String authenticationType = authorizationHeader.split(" ")[0];
        if (SCIMConstants.AUTH_TYPE_BASIC.equals(authenticationType)) {
            return SCIMConstants.AUTH_TYPE_BASIC;
        } else if (SCIMConstants.AUTH_TYPE_OAUTH.equals(authenticationType)) {
            return SCIMConstants.AUTH_TYPE_OAUTH;
        } else {
            String error = "Provided authentication headers do not contain supported authentication headers.";
            throw new CharonException(error);
        }
    }

    /**
     * Create the AuthInfo according to the requested authentication mechanism.
     * Now, only used in the case of OAUTH.
     *
     * @param tenantDTO
     * @return
     */
    private AuthenticationInfo getAuthInfo(TenantDTO tenantDTO) {
        AuthenticationInfo authInfo = null;

        if (SCIMConstants.AUTH_TYPE_OAUTH.equals(tenantDTO.getAuthenticationMechanism())) {
            //TODO:create OAUTHInfo out of tenant info submitted, and return.
        }
        return authInfo;

    }

    /**
     * Register encoders and decoders in AbstractResourceEndpoint.
     */
    private void registerCoders() throws CharonException {
        if (!encoderMap.isEmpty()) {
            for (Map.Entry<String, Encoder> encoderEntry : encoderMap.entrySet()) {
                AbstractResourceEndpoint.registerEncoder(encoderEntry.getKey(), encoderEntry.getValue());
            }
        }
        if (!encoderMap.isEmpty()) {
            for (Map.Entry<String, Decoder> decoderEntry : decoderMap.entrySet()) {
                AbstractResourceEndpoint.registerDecoder(decoderEntry.getKey(), decoderEntry.getValue());
            }
        }

    }

    private void registerEndpointURLs() {
        if (endpointURLs != null && endpointURLs.size() != 0) {
            AbstractResourceEndpoint.registerResourceEndpointURLs(endpointURLs);
        }
    }
}

