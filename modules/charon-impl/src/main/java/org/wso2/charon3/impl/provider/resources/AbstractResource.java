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

package org.wso2.charon3.impl.provider.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.FormatNotSupportedException;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.impl.provider.util.SCIMProviderConstants;
import org.wso2.msf4j.Microservice;
import java.util.Map;
import javax.ws.rs.core.Response;


/**
 * Endpoint parent class.
 */

public class AbstractResource implements Microservice {
    private static Logger logger = LoggerFactory.getLogger(AbstractResource.class);

    //identify the output format
    public boolean isValidOutputFormat(String format) {
        if (format == null || "*/*".equals(format) ||
                format.equalsIgnoreCase(SCIMProviderConstants.APPLICATION_JSON)
                || format.equalsIgnoreCase(SCIMProviderConstants.APPLICATION_SCIM_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    //identify the input format
    public boolean isValidInputFormat(String format) {
        String mediaType = format.split(";")[0];
        if (mediaType == null || "*/*".equals(mediaType) ||
                mediaType.equalsIgnoreCase(SCIMProviderConstants.APPLICATION_JSON)
                || mediaType.equalsIgnoreCase(SCIMProviderConstants.APPLICATION_SCIM_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Build an error message for a Charon exception. We go with the
     * JSON encoder as default if not specified.
     *
     * @param e CharonException
     * @param encoder
     * @return
     */
    protected Response handleCharonException(CharonException e, JSONEncoder encoder) {
        if (logger.isDebugEnabled()) {
            logger.debug(e.getMessage(), e);
        }

        return buildResponse(AbstractResourceManager.encodeSCIMException(e));
    }

    /*
     * Build the error response if the requested input or output format is not supported. We go with JSON encoder as
     * the encoder for the error response.
     *
     * @param e
     * @return
     */
    protected Response handleFormatNotSupportedException(FormatNotSupportedException e) {
        if (logger.isDebugEnabled()) {
            logger.debug(e.getMessage(), e);
        }

        // use the default JSON encoder to build the error response.
        return buildResponse(AbstractResourceManager.encodeSCIMException(e));
    }

    /*
     * build the jaxrs response
     * @param scimResponse
     * @return
     */
    public Response buildResponse(SCIMResponse scimResponse) {
        //create a response builder with the status code of the response to be returned.
        Response.ResponseBuilder responseBuilder = Response.status(scimResponse.getResponseStatus());
        //set the headers on the response
        Map<String, String> httpHeaders = scimResponse.getHeaderParamMap();
        if (httpHeaders != null && !httpHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {

                responseBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        //set the payload of the response, if available.
        if (scimResponse.getResponseMessage() != null) {
            responseBuilder.entity(scimResponse.getResponseMessage());
        }
        return responseBuilder.build();
    }

}

