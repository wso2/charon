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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.BulkRequestProcessor;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform bulk operations.
 * Any SCIM service provider can call this API perform bulk operations,
 * based on the HTTP requests received by SCIM Client.
 */
public class BulkResourceManager extends AbstractResourceManager {

    private static final Log logger = LogFactory.getLog(BulkResourceManager.class);
    private JSONEncoder encoder;
    private JSONDecoder decoder;
    private BulkRequestProcessor bulkRequestProcessor;

    public BulkResourceManager() {
        bulkRequestProcessor = new BulkRequestProcessor();
    }


    public SCIMResponse processBulkData(String data, UserManager userManager) {
        BulkResponseData bulkResponseData;

        try {
            //Get encoder and decoder from AbstractResourceEndpoint
            encoder = getEncoder();
            decoder = getDecoder();

            BulkRequestData bulkRequestDataObject;
            //decode the request
            bulkRequestDataObject = decoder.decodeBulkData(data);

            bulkRequestProcessor.setFailOnError(bulkRequestDataObject.getFailOnErrors());
            bulkRequestProcessor.setUserManager(userManager);

            //Get bulk response data
            bulkResponseData = bulkRequestProcessor.processBulkRequests(bulkRequestDataObject);
            //encode the BulkResponseData object
            String finalEncodedResponse = encoder.encodeBulkResponseData(bulkResponseData);

            //careate SCIM response message
            Map<String, String> responseHeaders = new HashMap<String, String>();
            //add location header
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            //create the final response
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, finalEncodedResponse, responseHeaders);

        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }


    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String
            excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        return null;
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
                                    String sortOrder, String domainName, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        return null;
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        return null;
    }
}
