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
package org.wso2.charon.core.protocol.endpoints;

import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;

import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is an abstract layer for all the resource endpoints to abstract out common
 * operations. And and entry point for CharonManager implementations to pass handlers to the
 * implementations of extension points.
 */
public abstract class AbstractResourceEndpoint implements ResourceEndpoint {

    //Keeps a map of supported encoders of SCIM server side.
    private static Map<String, Encoder> encoderMap = new ConcurrentHashMap<String, Encoder>();

    //Keeps a map of supported encoders of SCIM server side.
    private static Map<String, Decoder> decoderMap = new ConcurrentHashMap<String, Decoder>();

    //Keeps  a map of endpoint urls of the exposed resources.
    private static Map<String, String> endpointURLMap;

    private static Log log = LogFactory.getLog(AbstractResourceEndpoint.class);

    /**
     * Returns the encoder given the encoding format.
     *
     * @param format
     * @return
     * @throws FormatNotSupportedException
     */
    public Encoder getEncoder(String format)
            throws FormatNotSupportedException, CharonException {
        //if the requested format not supported, return an error.
        if (!encoderMap.containsKey(format)) {
            //Error is logged by the caller.
            throw new FormatNotSupportedException();
        }
        return encoderMap.get(format);
    }

    public Decoder getDecoder(String format)
            throws FormatNotSupportedException, CharonException {

        //if the requested format not supported, return an error.
        if ((format == null) && (!decoderMap.containsKey(format))) {
            //Error is logged by the caller.
            throw new FormatNotSupportedException();
        }
        return decoderMap.get(format);

    }

    /**
     * Register encoders to be supported by SCIM Server Side, which will be used in Charon-API.
     *
     * @param format  - format that the registering encoder supports.
     * @param encoder
     */
    public static void registerEncoder(String format, Encoder encoder) throws CharonException {
        if (encoderMap.containsKey(format)) {
            //log a warn message.
            String warnMessage = "Encoder for the given format is already registered.";
            log.warn(warnMessage);
        } else {
            encoderMap.put(format, encoder);
        }
    }

    /**
     * Register decoders to be supported by SCIM Server Side, which will be used in Charon-API.
     *
     * @param format
     * @param decoder
     * @throws CharonException
     */
    public static void registerDecoder(String format, Decoder decoder) throws CharonException {
        if (decoderMap.containsKey(format)) {
            //log a warn message.
            String warnMessage = "Decoder for the given format is already registered.";
            log.warn(warnMessage);
        } else {
            decoderMap.put(format, decoder);
        }
    }

    /**
     * Endpoint URLs defined in configuration needs to be registered here for the API to use them
     * in Location header etc.
     *
     * @param endpointURLs
     */
    public static void registerResourceEndpointURLs(Map<String, String> endpointURLs) {
        endpointURLMap = endpointURLs;
    }

    public static String getResourceEndpointURL(String resource) {
        if (endpointURLMap != null && endpointURLMap.size() != 0) {
            return endpointURLMap.get(resource);
        } else {
            return null;
        }
    }

    /**
     * Util method to encode the exception and construct the SCIMResponse, given the encoder
     * and the exception
     *
     * @param encoder
     * @param exception
     * @return
     */
    public static SCIMResponse encodeSCIMException(Encoder encoder,
                                                   AbstractCharonException exception) {
        Map<String, String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.identifyContentType(encoder.getFormat()));
        return new SCIMResponse(exception.getCode(), encoder.encodeSCIMException(exception), httpHeaders);
    }
}
