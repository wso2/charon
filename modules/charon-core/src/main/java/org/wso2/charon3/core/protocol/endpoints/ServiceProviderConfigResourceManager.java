/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.charon3.core.protocol.endpoints;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.CopyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The service provider configuration resource enables a service
 * provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 */
public class ServiceProviderConfigResourceManager extends AbstractResourceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderConfigResourceManager.class);

    public ServiceProviderConfigResourceManager() {
    }

    /*
     * Retrieves a service provider config
     *
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return getServiceProviderConfig();
    }

    private SCIMResponse getServiceProviderConfig() {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            // get the service provider config schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance()
                    .getServiceProviderConfigResourceSchema();
            //create a string in json format with relevant values
            String scimObjectString = encoder.buildServiceProviderConfigJsonBody(CharonConfiguration.getInstance()
                    .getConfig());
            //decode the SCIM service provider config object, encoded in the submitted payload.
            AbstractSCIMObject serviceProviderConfigObject = (AbstractSCIMObject) decoder.decodeResource(
                    scimObjectString, schema, new AbstractSCIMObject());

            //encode the newly created SCIM service provider config object and add id attribute to Location header.
            String encodedObject;
            Map<String, String> responseHeaders = new HashMap<String, String>();

            if (serviceProviderConfigObject != null) {
                //create a deep copy of the service provider config object since we are going to change it.
                AbstractSCIMObject copiedObject = (AbstractSCIMObject) CopyUtil.deepCopy(serviceProviderConfigObject);
                encodedObject = encoder.encodeSCIMObject(copiedObject);
                //add location header
                responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT));
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }
            //put the uri of the service provider config object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK,
                    encodedObject, responseHeaders);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String
            excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, Integer startIndex, Integer count,
                                    String sortBy, String sortOrder, String domainName, String attributes,
                                    String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }


    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }
}
