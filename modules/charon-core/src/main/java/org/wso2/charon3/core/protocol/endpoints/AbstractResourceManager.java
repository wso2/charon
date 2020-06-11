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
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an abstract layer for all the resource endpoints to abstract out common
 * operations. And an entry point for initiating the charon from the outside.
 */
public abstract class AbstractResourceManager implements ResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceManager.class);

    private static JSONEncoder encoder = new JSONEncoder();

    private static JSONDecoder decoder = new JSONDecoder();

    private static ResourceURLBuilder resourceURLBuilder = new DefaultResourceURLBuilder();

    /*
     * Returns the encoder for json.
     *
     * @return JSONEncoder - An json encoder for encoding data
     * @throws CharonException
     */
    public static JSONEncoder getEncoder() throws CharonException {
        return encoder;
    }

    /*
     * Returns the decoder for json.
     *
     *
     * @return JSONDecoder - An json decoder for decoding data
     * @throws CharonException
     */
    public static JSONDecoder getDecoder() throws CharonException {
        return decoder;
    }

    /**
     * Sets the resource URL builder implementation.
     *
     * @param resourceURLBuilder ResourceURLBuilder.
     */
    public static void setResourceURLBuilder(ResourceURLBuilder resourceURLBuilder) {

        AbstractResourceManager.resourceURLBuilder = resourceURLBuilder;
    }

    /*
     * Returns the endpoint according to the resource.
     *
     * @param resource -Resource type
     * @return endpoint URL
     * @throws NotFoundException
     */
    public static String getResourceEndpointURL(String resource) throws NotFoundException {

        return resourceURLBuilder.build(resource);
    }

    public static void setEndpointURLMap(Map<String, String> endpointURLMap) {

        if (resourceURLBuilder instanceof DefaultResourceURLBuilder) {
            ((DefaultResourceURLBuilder) resourceURLBuilder).setEndpointURIMap(endpointURLMap);
        }
    }

    /*
     * Returns SCIM Response object after json encoding the exception
     *
     * @param exception - exception message
     * @return SCIMResponse
     */
    public static SCIMResponse encodeSCIMException(AbstractCharonException exception) {
        logger.debug(exception.getDetail(), exception);
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(exception.getStatus(), encoder.encodeSCIMException(exception), responseHeaders);
    }

}
