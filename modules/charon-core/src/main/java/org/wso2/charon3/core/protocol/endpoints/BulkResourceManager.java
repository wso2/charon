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
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.objects.bulk.BulkRequestContent;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * REST API exposed by Charon-Core to perform bulk operations. Any SCIM service provider can call this API perform bulk
 * operations, based on the HTTP requests received by SCIM Client.
 */
public class BulkResourceManager {

    private Logger logger = LoggerFactory.getLogger(BulkResourceManager.class);

    private JSONEncoder encoder = new JSONEncoder();
    private JSONDecoder decoder = new JSONDecoder();

    /**
     * An integer specifying the number of errors that the service provider will accept before the operation is
     * terminated and an error response is returned.  OPTIONAL in a request.  Not valid in a response.<br><br>
     * if not specified the server should process as many operations if possible even if each of them fails
     */
    //    private int failOnErrors = Integer.MAX_VALUE;

    /**
     * the number of errors that actually occurred
     */
    private int errorCount;

    /**
     * a list of the resource managers that can be used to process bulk operations
     */
    private Map<String, ResourceManager> resourceManagerMap;

    public BulkResourceManager(List<ResourceManager> resourceManagerMap) {
        this.resourceManagerMap = createResourceManagerMap(resourceManagerMap);
    }

    /**
     * will create a map from the resource managers to make them more easily accessible and to prevent unnecessary
     * iterations over the resource manager list.<br><br> The key of the map will be the endpoint path of the resource
     * that is handled by the corresponding resource manager e.g: "/Users", "/Groups", "/Roles", "/Clients" etc.
     *
     * @param resourceManagerList
     *     the list that should be converted into a map
     *
     * @return a map of resource managers where the key is the endpoint-path of the resource
     */
    private Map<String, ResourceManager> createResourceManagerMap(List<ResourceManager> resourceManagerList) {
        return resourceManagerList.stream().collect(
            Collectors.toMap(r -> r.getResourceHandler().getResourceEndpoint(), r -> r));
    }

    public SCIMResponse processBulkData(String data) {
        try {
            BulkRequestData bulkRequestData = decoder.decodeBulkData(data);
            int failOnErrors = bulkRequestData.getFailOnErrors();

            BulkResponseData bulkResponseData = new BulkResponseData();
            for (BulkRequestContent bulkRequestContent : bulkRequestData.getOperationRequests()) {
                if (( failOnErrors == 0 && errorCount > 0 ) || ( errorCount > 0 && errorCount >= failOnErrors )) {
                    throw new BadRequestException("bulk request has failed for too many errors: " + errorCount,
                        "too_many_errors");
                }
                Optional<ResourceManager> resourceManagerOptional = findResourceManager(bulkRequestContent);
                if (resourceManagerOptional.isPresent()) {
                    ResourceManager resourceManager = resourceManagerOptional.get();
                    BulkResponseContent responseContent = processOperationRequest(bulkRequestContent, resourceManager);
                    bulkResponseData.addOperationResponse(responseContent);
                }
            }
            //encode the BulkResponseData object
            String finalEncodedResponse = encoder.encodeBulkResponseData(bulkResponseData);

            // create SCIM response message
            Map<String, String> responseHeaders = new HashMap<>();
            // add location header
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            // create the final response
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, finalEncodedResponse, responseHeaders);

        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    private BulkResponseContent processOperationRequest(BulkRequestContent bulkRequestContent,
                                                        ResourceManager resourceManager) throws BadRequestException {

        BulkResponseContent bulkResponseContent = null;

        if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.POST)) {

            SCIMResponse response = resourceManager.create(bulkRequestContent.getData(), null, null);
            bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.POST,
                bulkRequestContent);
            errorsCheck(response);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PUT)) {

            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.updateWithPUT(resourceId, bulkRequestContent.getData(), null, null);
            bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.PUT,
                bulkRequestContent);
            errorsCheck(response);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PATCH)) {

            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.updateWithPATCH(resourceId, bulkRequestContent.getData(), null,
                null);
            bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.PATCH,
                bulkRequestContent);
            errorsCheck(response);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.DELETE)) {
            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.delete(resourceId);
            bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.DELETE,
                bulkRequestContent);
            errorsCheck(response);
        } else {
            bulkResponseContent = new BulkResponseContent();
            BadRequestException badRequestException = new BadRequestException(
                "method '" + bulkRequestContent.getMethod() + "' not supported",
                "unsupported method");
            bulkResponseContent.setScimResponse(AbstractResourceManager.encodeSCIMException(badRequestException));
        }
        return bulkResponseContent;
    }

    private BulkResponseContent createBulkResponseContent(SCIMResponse response,
                                                          String method,
                                                          BulkRequestContent requestContent) {
        BulkResponseContent bulkResponseContent = new BulkResponseContent();

        bulkResponseContent.setScimResponse(response);
        bulkResponseContent.setMethod(method);
        if (response.getHeaderParamMap() != null) {
            bulkResponseContent.setLocation(response.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER));
        }
        bulkResponseContent.setBulkID(requestContent.getBulkID());
        bulkResponseContent.setVersion(requestContent.getVersion());

        return bulkResponseContent;
    }

    private String extractIDFromPath(String path) throws BadRequestException {
        String[] parts = path.split("[/]");
        if (parts.length == 3 && parts[2] != null) {
            return parts[2];
        } else {
            throw new BadRequestException("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
        }
    }


    private void errorsCheck(SCIMResponse response) {
        if (response.getResponseStatus() != 200 && response.getResponseStatus() != 201 &&
            response.getResponseStatus() != 204) {
            errorCount++;
        }
    }

    /**
     * tries to get the resource manager that is capable of processing the given request operation
     *
     * @param bulkRequestContent
     *     the request operation that should be processed
     *
     * @return the resource manager or an empty if no matching resource-manager was found
     */
    private Optional<ResourceManager> findResourceManager(BulkRequestContent bulkRequestContent) {
        String resourceType = getResourceTypeFromPath(bulkRequestContent.getPath());
        ResourceManager resourceManager = resourceManagerMap.get(resourceType);
        if (resourceManager == null) {
            logger.debug("cannot process bulk operation for endpoint '{}', missing resource manager",
                bulkRequestContent.getPath());
        } else {
            logger.trace("will process bulk operation for path '{}' in resource manager: '{}'",
                bulkRequestContent.getPath(), resourceManager);
        }
        return Optional.ofNullable(resourceManager);
    }

    private String getResourceTypeFromPath(String path) {
        String[] pathParts = path.split("/");
        if (pathParts.length < 2) {
            rethrowSupplier(() -> {
                throw new BadRequestException("invalid path argument: " + path);
            }).get();
        }
        return "/" + pathParts[1];
    }

}
