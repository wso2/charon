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
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.config.SCIMConfigConstants;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.PayloadTooLargeException;
import org.wso2.charon3.core.extensions.RoleManager;
import org.wso2.charon3.core.extensions.RoleV2Manager;
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

    public SCIMResponse processBulkData(String data, UserManager userManager, RoleManager roleManager,
                                        RoleV2Manager roleV2Manager) {

        bulkRequestProcessor.setRoleManager(roleManager);
        bulkRequestProcessor.setRoleV2Manager(roleV2Manager);
        return processBulkData(data, userManager);
    }

    public SCIMResponse processBulkData(String data, UserManager userManager, RoleManager roleManager) {

        bulkRequestProcessor.setRoleManager(roleManager);
        return processBulkData(data, userManager);
    }

    public SCIMResponse processBulkData(String data, UserManager userManager) {

        BulkResponseData bulkResponseData;
        try {
            // Get encoder and decoder from AbstractResourceEndpoint
            encoder = getEncoder();
            decoder = getDecoder();

            BulkRequestData bulkRequestDataObject;
            // Decode the request.
            bulkRequestDataObject = decoder.decodeBulkData(data);

            bulkRequestProcessor.setFailOnError(bulkRequestDataObject.getFailOnErrors());
            bulkRequestProcessor.setUserManager(userManager);

            int maxOperationCount =
                    (Integer) CharonConfiguration.getInstance().getConfig().get(SCIMConfigConstants.MAX_OPERATIONS);
            int totalOperationCount = bulkRequestDataObject.getUserOperationRequests().size() +
                    bulkRequestDataObject.getGroupOperationRequests().size() +
                    bulkRequestDataObject.getRoleOperationRequests().size() +
                    bulkRequestDataObject.getRoleV2OperationRequests().size();
            if (totalOperationCount > maxOperationCount) {
                throw new PayloadTooLargeException(
                        String.format(ResponseCodeConstants.ERROR_DESC_MAX_OPERATIONS_EXCEEDED,
                                totalOperationCount,
                                maxOperationCount));
            }

            // Get bulk response data.
            bulkResponseData = bulkRequestProcessor.processBulkRequests(bulkRequestDataObject);
            //encode the BulkResponseData object
            String finalEncodedResponse = encoder.encodeBulkResponseData(bulkResponseData);

            // Create SCIM response message.
            Map<String, String> responseHeaders = new HashMap<>();
            //add location header
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            // Create the final response.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, finalEncodedResponse, responseHeaders);

        } catch (CharonException | BadRequestException | InternalErrorException | PayloadTooLargeException e) {
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
