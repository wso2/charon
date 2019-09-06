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
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.PayloadTooLargeException;
import org.wso2.charon3.core.objects.bulk.BulkRequestContent;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * REST API exposed by Charon-Core to perform bulk operations. Any SCIM service provider can call this API perform bulk.
 * operations, based on the HTTP requests received by SCIM Client.
 */
public class BulkResourceManager {

    private Logger logger = LoggerFactory.getLogger(BulkResourceManager.class);

    private JSONEncoder encoder = new JSONEncoder();
    private JSONDecoder decoder = new JSONDecoder();

    /**
     * a list of the resource managers that can be used to process bulk operations.
     */
    private Map<String, ResourceManager> resourceManagerMap;

    public BulkResourceManager(List<ResourceManager> resourceManagerMap) {
        this.resourceManagerMap = createResourceManagerMap(resourceManagerMap);
    }

    /**
     * will create a map from the resource managers to make them more easily accessible and to prevent unnecessary.
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
            validatePayload(data);
            BulkRequestData bulkRequestData = decoder.decodeBulkData(data);
            validateMaxOperations(bulkRequestData);
            Integer failOnErrors = bulkRequestData.getFailOnErrors();

            BulkResponseData bulkResponseData = new BulkResponseData();
            int errorCount = 0;
            for (BulkRequestContent bulkRequestContent : bulkRequestData.getOperationRequests()) {
                Optional<ResourceManager> resourceManagerOptional = findResourceManager(bulkRequestContent);
                BulkResponseContent responseContent = null;
                if (isFailOnErrorsExceeded(failOnErrors, errorCount)) {
                    SCIMResponse preconditionFailedResponse = createExceededErrorsScimResponse(failOnErrors);
                    responseContent = createBulkResponseContent(preconditionFailedResponse, bulkRequestContent);

                } else if (resourceManagerOptional.isPresent()) {
                    ResourceManager resourceManager = resourceManagerOptional.get();
                    responseContent = processOperationRequest(bulkRequestContent, resourceManager);
                    errorCount += errorsCheck(responseContent.getScimResponse());
                }

                if (responseContent != null) {
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

    private SCIMResponse createExceededErrorsScimResponse(int failOnErrors) {
        String errorMessage = "Operation was not processed for maximum number of errors (" + failOnErrors + ") was " +
            "exceeded";
        AbstractCharonException error = new AbstractCharonException(ResponseCodeConstants.CODE_PRECONDITION_FAILED,
            errorMessage, null);
        return new SCIMResponse(ResponseCodeConstants.CODE_PRECONDITION_FAILED,
            AbstractResourceManager.getEncoder().encodeSCIMException(error), null);
    }

    private boolean isFailOnErrorsExceeded(Integer failOnErrors, int errorCount) {
        return failOnErrors != null &&
            ((failOnErrors == 0 && errorCount > 0) || (errorCount > 0 && errorCount > failOnErrors));
    }

    /**
     * checks that the maximum payload is not exceeded.
     *
     * @param requestBody
     *     the request body sent by the client
     */
    private void validatePayload(String requestBody) {
        final int currentPayload = requestBody == null ? 0 : requestBody.getBytes(StandardCharsets.UTF_8).length;
        final int maxPayload = CharonConfiguration.getInstance().getBulk().getMaxPayLoadSize();
        if (currentPayload > maxPayload) {
            rethrowSupplier(() -> {
                throw new PayloadTooLargeException(
                    "payload sent is too large. Maximum allowed payload is '" + maxPayload + "' but was '" +
                        currentPayload + "'");
            }).get();
        }
    }

    /**
     * checks that the maximum number of operations are not exceeded.
     *
     * @param bulkRequestData
     *     the decoded bulk request
     */
    private void validateMaxOperations(BulkRequestData bulkRequestData) {
        final int currentOperations = bulkRequestData.getOperationRequests().size();
        final int maximumOperations = CharonConfiguration.getInstance().getBulk().getMaxOperations();
        if (currentOperations > maximumOperations) {
            rethrowSupplier(() -> {
                throw new BadRequestException(
                    "Too many operations. Maximum number of operations is '" + maximumOperations + "' but was '" +
                        currentOperations + "'", ResponseCodeConstants.TOO_MANY);
            }).get();
        }
    }

    private BulkResponseContent processOperationRequest(BulkRequestContent bulkRequestContent,
                                                        ResourceManager resourceManager) throws BadRequestException {

        BulkResponseContent bulkResponseContent = null;

        if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.POST)) {

            SCIMResponse response = resourceManager.create(bulkRequestContent.getData(), null, null);
            bulkResponseContent = createBulkResponseContent(response, bulkRequestContent);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PUT)) {

            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.updateWithPUT(resourceId, bulkRequestContent.getData(), null, null);
            bulkResponseContent = createBulkResponseContent(response, bulkRequestContent);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PATCH)) {

            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.updateWithPATCH(resourceId, bulkRequestContent.getData(), null,
                null);
            bulkResponseContent = createBulkResponseContent(response, bulkRequestContent);

        } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.DELETE)) {
            String resourceId = extractIDFromPath(bulkRequestContent.getPath());
            SCIMResponse response = resourceManager.delete(resourceId);
            bulkResponseContent = createBulkResponseContent(response,
                bulkRequestContent);
        } else {
            bulkResponseContent = new BulkResponseContent();
            bulkResponseContent.setStatus(ResponseCodeConstants.METHOD_NOT_ALLOWED);
            bulkResponseContent.setMethod(bulkRequestContent.getMethod());
            BadRequestException badRequestException = new BadRequestException(
                ResponseCodeConstants.DESC_METHOD_NOT_ALLOWED + ": " + bulkRequestContent.getMethod());
            bulkResponseContent.setScimResponse(AbstractResourceManager.encodeSCIMException(badRequestException));
        }
        return bulkResponseContent;
    }

    private BulkResponseContent createBulkResponseContent(SCIMResponse response,
                                                          BulkRequestContent requestContent) {
        BulkResponseContent bulkResponseContent = new BulkResponseContent();

        bulkResponseContent.setScimResponse(response);
        if (response != null) {
            if (response.getHeaderParamMap() != null) {
                bulkResponseContent.setLocation(response.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER));
            }
            bulkResponseContent.setStatus(response.getResponseStatus());
        }
        bulkResponseContent.setMethod(requestContent.getMethod());
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


    private int errorsCheck(SCIMResponse response) {
        if (response.getResponseStatus() != 200 && response.getResponseStatus() != 201 &&
            response.getResponseStatus() != 204) {
            return 1;
        }
        return 0;
    }

    /**
     * tries to get the resource manager that is capable of processing the given request operation.
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
