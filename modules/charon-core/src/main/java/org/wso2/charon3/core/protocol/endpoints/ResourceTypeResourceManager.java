/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.ServerSideValidator.validateResourceTypeSCIMObject;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * The "RESOURCE_TYPES" schema specifies the metadata about a resource type. This is the spec compatible version of
 * ResourceTypeResourceManager
 */
public class ResourceTypeResourceManager extends AbstractResourceManager {

    private static final Logger log = LoggerFactory.getLogger(ResourceTypeResourceManager.class);

    /**
     * Retrieves a single resource type with the specified id
     *
     * @return the
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {

        try {
            ResourceType resourceType = ResourceTypeRegistration.getResourceTypeListCopy().stream().filter(
                rt -> rt.getId().equals(id)).findAny().orElse(null);
            if (resourceType == null) {
                throw new NotFoundException("resource with id '" + id + "' does not exist");
            }
            rethrowConsumer(rt -> validateResourceTypeSCIMObject((AbstractSCIMObject) rt)).accept(resourceType);
            String encodedObject = rethrowSupplier(() -> getEncoder().encodeSCIMObject(resourceType)).get();
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.LOCATION_HEADER,
                rethrowSupplier(() -> getResourceEndpointURL(SCIMConstants.RESOURCE_TYPE_ENDPOINT)).get());
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedObject, responseHeaders);
        } catch (AbstractCharonException ex) {
            return encodeSCIMException(ex);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            CharonException charonException = new CharonException("an unexpected error occured: " + ex.getMessage());
            return encodeSCIMException(charonException);
        }
    }

    @Override
    public SCIMResponse create(String scimObjectString,
                               UserManager userManager,
                               String attributes,
                               String excludeAttributes) {

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
    public SCIMResponse listWithGET(UserManager userManager,
                                    String filter,
                                    int startIndex,
                                    int count,
                                    String sortBy,
                                    String sortOrder,
                                    String domainName,
                                    String attributes,
                                    String excludeAttributes) {

        try {
            List<ResourceType> copiedResourceTypes = ResourceTypeRegistration.getResourceTypeListCopy();
            copiedResourceTypes.forEach(resourceType -> {
                rethrowConsumer(rt -> validateResourceTypeSCIMObject((AbstractSCIMObject) rt)).accept(resourceType);
            });
            //encode the newly created SCIM Resource Type object.
            ListedResource listedResource = new ListedResource();
            listedResource.setTotalResults(ResourceTypeRegistration.getResouceTypeCount());
            copiedResourceTypes.forEach(listedResource::addResource);
            String encodedObject = getEncoder().encodeSCIMObject(listedResource);
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.LOCATION_HEADER,
                getResourceEndpointURL(SCIMConstants.RESOURCE_TYPE_ENDPOINT));
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            //put the uri of the resource type object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedObject, responseHeaders);
        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            CharonException charonException = new CharonException("an unexpected error occured: " + e.getMessage());
            return encodeSCIMException(charonException);
        }
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {

        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId,
                                      String scimObjectString,
                                      UserManager userManager,
                                      String attributes,
                                      String excludeAttributes) {

        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId,
                                        String scimObjectString,
                                        UserManager userManager,
                                        String attributes,
                                        String excludeAttributes) {

        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }
}
