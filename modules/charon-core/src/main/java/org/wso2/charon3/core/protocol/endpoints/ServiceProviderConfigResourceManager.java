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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.utils.CopyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The service provider configuration resource enables a service provider to discover SCIM specification features in a
 * standardized form as well as provide additional implementation details to clients.
 */
public class ServiceProviderConfigResourceManager extends AbstractResourceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderConfigResourceManager.class);

    /**
     * @return the service provider configuration
     */
    public SCIMResponse getServiceProviderConfig () {
        try {

            //encode the newly created SCIM service provider config object and add id attribute to Location header.
            String encodedObject;
            Map<String, String> responseHeaders = new HashMap<String, String>();

            if (CharonConfiguration.getInstance() != null) {
                //create a deep copy of the service provider config object since we are going to change it.
                AbstractSCIMObject copiedObject = (AbstractSCIMObject) CopyUtil.deepCopy(
                    CharonConfiguration.getInstance());
                encodedObject = getEncoder().encodeSCIMObject(copiedObject);
                //add location header
                responseHeaders.put(SCIMConstants.LOCATION_HEADER,
                    getResourceEndpointURL(SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT));
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }
            //put the uri of the service provider config object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedObject, responseHeaders);
        } catch (AbstractCharonException e) {
            log.debug(e.getMessage(), e);
            return encodeSCIMException(e);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(e.getMessage()));
        }
    }
}
