/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.wso2.charon3.core.protocol.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.PatchOperationUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;
import org.wso2.charon3.core.utils.codeutils.FilterTreeManager;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * implementation for SCIM resource endpoints..
 *
 * @param <R> the scim object type that should be handled by this manager
 */
public class ResourceManager<R extends AbstractSCIMObject> extends AbstractResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private static final String INTERNAL_ERROR_MESSAGE = "an internal error occurred";

    /**
     * the handler that will handle the scim resources.
     */
    private ResourceHandler<R> resourceHandler;

    /**
     * holds the generic type of this implementation.
     */
    private Class<R> genericType;

    public ResourceManager(ResourceHandler<R> resourceHandler) {
        this.resourceHandler = Objects.requireNonNull(resourceHandler, "resource handler must not be null!");
        this.genericType =
            (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Method of resource endpoint which is mapped to HTTP GET request..
     *
     * @param id - unique resource id
     */
    public SCIMResponse get(String id, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            // obtain the correct encoder according to the format requested.
            encoder = getEncoder();
            // returns core-resource schema
            // get the URIs of required attributes which must be given a value
            SCIMResourceTypeSchema schema = resourceHandler.getResourceSchema();
            Map<String, Boolean> requiredAttributes =
                ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema),
                                                                  attributes,
                                                                  excludeAttributes);

            // retrieve the resource
            R resource = resourceHandler.get(id, requiredAttributes);

            // if resource not found, return an error in relevant format.
            if (resource == null) {
                String message = "resource not found in the store.";
                throw new NotFoundException(message);
            }

            ServerSideValidator.validateRetrievedSCIMObjectInList(resource, schema, attributes, excludeAttributes);
            // convert the resource into specific format.
            String encodedResource = encoder.encodeSCIMObject(resource);
            // if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                            getResourceEndpointURL(resourceHandler.getResourceEndpoint()) + "/" + resource.getId());
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedResource, httpHeaders);
        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * Method of resource endpoint which is mapped to HTTP POST request..
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @return SCIMResponse - From Spec: {Since the server is free to alter and/or ignore POSTed content,
     *         returning the full representation can be useful to the client, enabling it to correlate the
     *         client and server views of the new Resource. When a Resource is created, its uri must be returned
     *         in the response Location header.}
     */
    public SCIMResponse create(String scimObjectString, String attributes, String excludeAttributes) {
        try {
            // returns core-resource schema
            SCIMResourceTypeSchema schema = resourceHandler.getResourceSchema();
            // get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema),
                                                                  attributes,
                                                                  excludeAttributes);
            // decode the SCIM resource object, encoded in the submitted payload.
            R resource = getDecoder().decodeResource(scimObjectString, schema, genericType.newInstance());
            // validate decoded resource
            ServerSideValidator.validateCreatedSCIMObject(resource, schema);
            // need to send back the newly created resource in the response payload
            R createdResource = resourceHandler.create(resource, requiredAttributes);

            // encode the newly created SCIM resource object and add id attribute to Location header.
            String encodedResource;
            Map<String, String> httpHeaders = new HashMap<>();
            if (createdResource != null) {
                // need to remove attributes that should never be returned
                ServerSideValidator.validateReturnedAttributes(createdResource, attributes, excludeAttributes);

                encodedResource = getEncoder().encodeSCIMObject(createdResource);
                // add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                                getResourceEndpointURL(resourceHandler.getResourceEndpoint()) + "/" +
                                    createdResource.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String message = "Newly created resource is null..";
                throw new InternalErrorException(message);
            }

            // put the uri of the resource object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedResource, httpHeaders);

        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * Method of the ResourceManager that is mapped to HTTP Delete method...
     *
     * @param id unique resource id
     */
    public SCIMResponse delete(String id) {
        try {
            /* handover the SCIM resource object to the resource resourcemanager provided by the SP for the delete
            operation */
            resourceHandler.delete(id);
            // on successful deletion SCIMResponse only has 204 No Content status code.
            return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);
        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * get resources.
     *
     * @param filter the filter expression
     * @param startIndex index of the first entry
     * @param count number of resources to return in the request
     * @param sortBy a string indicating the attribute whose value SHALL be used to order the returned responses
     * @param sortOrder sortOrder A string indicating the order in which the "sortBy" parameter is applied
     * @param domainName specific parameter for charon idp
     * @param attributes A multi-valued list of strings indicating the names of resource attributes to in the
     *          response, overriding the set of attributes that would be returned by default.
     * @param excludeAttributes A multi-valued list of strings indicating the names of resource attributes to be
     *          removed from the default set of attributes to return
     */
    public SCIMResponse listWithGET(String filter,
                                    Integer startIndex,
                                    Integer count,
                                    String sortBy,
                                    String sortOrder,
                                    String domainName,
                                    String attributes,
                                    String excludeAttributes) {
        try {
            Node rootNode = null;
            if (filter != null) {
                FilterTreeManager filterTreeManager = new FilterTreeManager(filter,
                                                                            resourceHandler.getResourceSchema());
                rootNode = filterTreeManager.buildTree();
            }

            return listResources(startIndex,
                                 count,
                                 sortBy,
                                 sortOrder,
                                 domainName,
                                 attributes,
                                 excludeAttributes,
                                 rootNode);

        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * does the actual work for the methods.
     * {@link #listWithGET(String, Integer, Integer, String, String, String, String, String)} and
     * {@link #listWithGET(String, Integer, Integer, String, String, String, String, String)}
     */
    private SCIMResponse listResources(Integer startIndex,
                                       Integer count,
                                       String sortBy,
                                       String sortOrder,
                                       String domainName,
                                       String attributes,
                                       String excludeAttributes,
                                       Node rootNode) {
        try {
            // According to SCIM 2.0 spec minus values will be considered as 0
            count = ResourceManagerUtil.processCount(count == null ? null : String.valueOf(count));
            // According to SCIM 2.0 spec minus values will be considered as 1
            startIndex = ResourceManagerUtil.processStartIndex(startIndex == null ? null : String.valueOf(startIndex));
            if (sortOrder != null) {
                if (!(sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING) ||
                          sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                    String error = " Invalid sortOrder value is specified";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }
            // If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
            // ascending.
            if (sortOrder == null && sortBy != null) {
                sortOrder = SCIMConstants.OperationalConstants.ASCENDING;
            }

            // unless configured returns core-resource schema or else returns extended resource schema)
            SCIMResourceTypeSchema schema = resourceHandler.getResourceSchema();

            // get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema),
                                                                  attributes,
                                                                  excludeAttributes);

            int totalResults = 0;
            // API resource should pass a resourcemanager resourcemanager to resourceResourceEndpoint.
            List<Object> resources = resourceHandler.listResources(rootNode,
                                                                   startIndex,
                                                                   count,
                                                                   sortBy,
                                                                   sortOrder,
                                                                   domainName,
                                                                   requiredAttributes);

            if (resources == null) {
                resources = Collections.emptyList();
            }

            try {
                totalResults = (int) resources.get(0);
                resources.remove(0);
            } catch (IndexOutOfBoundsException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("resource result list is empty.");
                }
                totalResults = resources.size();
            } catch (ClassCastException ex) {
                logger.debug(
                    "Parse error while getting the resource result count. Setting result count as: " + resources.size(),
                    ex);
                totalResults = resources.size();
            }

            for (Object resource : resources) {
                // perform service provider side validation.
                ServerSideValidator.validateRetrievedSCIMObjectInList((R) resource,
                                                                      schema,
                                                                      attributes,
                                                                      excludeAttributes);
            }
            // create a listed resource object out of the returned resources list.
            ListedResource listedResource = createListedResource(resources, startIndex, totalResults);
            // convert the listed resource into specific format.
            String encodedListedResource = getEncoder().encodeSCIMObject(listedResource);
            // if there are any http headers to be added in the response header.
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);
        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * query resources.
     *
     * @param resourceString the request body
     */
    public SCIMResponse listWithPOST(String resourceString) {
        try {
            // create the search request object
            SearchRequest searchRequest = getDecoder().decodeSearchRequestBody(resourceString,
                                                                               resourceHandler.getResourceSchema());
            searchRequest.setCount(ResourceManagerUtil.processCount(searchRequest.getCountStr()));
            searchRequest.setStartIndex(ResourceManagerUtil.processStartIndex(searchRequest.getStartIndexStr()));

            if (searchRequest.getSchema() != null &&
                    !searchRequest.getSchema().equals(SCIMConstants.SEARCH_SCHEMA_URI)) {
                throw new BadRequestException("Provided schema is invalid", ResponseCodeConstants.INVALID_VALUE);
            }

            return listResources(searchRequest.getStartIndex(),
                                 searchRequest.getCount(),
                                 searchRequest.getSortBy(),
                                 searchRequest.getSortOder(),
                                 searchRequest.getDomainName(),
                                 searchRequest.getExcludedAttributesAsString(),
                                 searchRequest.getExcludedAttributesAsString(),
                                 searchRequest.getFilter());
        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * To update the resource by giving entire attribute set.
     *
     * @param existingId the id of the resource to update
     * @param scimObjectString the request body
     * @param attributes A multi-valued list of strings indicating the names of resource attributes to in the
     *          response, overriding the set of attributes that would be returned by default.
     * @param excludeAttributes A multi-valued list of strings indicating the names of resource attributes to be
     *          removed from the default set of attributes to return
     */

    public SCIMResponse updateWithPUT(String existingId,
                                      String scimObjectString,
                                      String attributes,
                                      String excludeAttributes) {


        try {
            SCIMResourceTypeSchema schema = resourceHandler.getResourceSchema();
            // get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema),
                                                                  attributes,
                                                                  excludeAttributes);
            // decode the SCIM resource object, encoded in the submitted payload.
            R resource = getDecoder().decodeResource(scimObjectString, schema, genericType.newInstance());
            R updatedResource;
            // retrieve the old object
            R oldResource = resourceHandler.get(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
            if (oldResource != null) {
                R validatedResource = (R) ServerSideValidator.validateUpdatedSCIMObject(oldResource, resource, schema);
                updatedResource = resourceHandler.update(validatedResource, requiredAttributes);

            } else {
                String error = "No resource exists with the given id: " + existingId;
                throw new NotFoundException(error);
            }

            // encode the newly created SCIM resource object and add id attribute to Location header.
            String encodedResource;
            Map<String, String> httpHeaders = new HashMap<>();
            if (updatedResource != null) {
                // create a deep copy of the resource object since we are going to change it.
                R copiedResource = (R) CopyUtil.deepCopy(updatedResource);
                // need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedResource, attributes, excludeAttributes);
                encodedResource = getEncoder().encodeSCIMObject(copiedResource);
                // add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                                getResourceEndpointURL(resourceHandler.getResourceEndpoint()) + "/" +
                                    updatedResource.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated resource is null.";
                throw new InternalErrorException(error);
            }

            // put the uri of the resource object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedResource, httpHeaders);

        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }

    /**
     * will update an existing resource with the patch operation.
     *
     * @param existingId the id of the resource that should be updated
     * @param scimObjectString the request body
     */
    public SCIMResponse updateWithPATCH(String existingId,
                                        String scimObjectString,
                                        String attributes,
                                        String excludeAttributes) {
        try {
            // obtain the json decoder.
            JSONDecoder decoder = getDecoder();
            // decode the SCIM resource object, encoded in the submitted payload.
            List<PatchOperation> opList = decoder.decodeRequest(scimObjectString);

            SCIMResourceTypeSchema schema = resourceHandler.getResourceSchema();
            // get the resource from the resource core
            R oldResource = resourceHandler.get(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
            if (oldResource == null) {
                throw new NotFoundException("No resource with the id : " + existingId + " in the store.");
            }
            // make a copy of the original resource
            R copyOfOldResource = (R) CopyUtil.deepCopy(oldResource);
            // make another copy of original resource.
            // this will be used to restore to the original condition if failure occurs.
            R originalResource = (R) CopyUtil.deepCopy(copyOfOldResource);

            R newResource = null;

            for (PatchOperation operation : opList) {

                if (operation.getOperation().equals(SCIMConstants.OperationalConstants.ADD)) {
                    if (newResource == null) {
                        newResource = (R) PatchOperationUtil.doPatchAdd(operation,
                                                                        getDecoder(),
                                                                        oldResource,
                                                                        copyOfOldResource,
                                                                        schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);

                    } else {
                        newResource = (R) PatchOperationUtil.doPatchAdd(operation,
                                                                        getDecoder(),
                                                                        newResource,
                                                                        copyOfOldResource,
                                                                        schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);

                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REMOVE)) {
                    if (newResource == null) {
                        newResource = (R) PatchOperationUtil.doPatchRemove(operation,
                                                                           oldResource,
                                                                           copyOfOldResource,
                                                                           schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);

                    } else {
                        newResource = (R) PatchOperationUtil.doPatchRemove(operation,
                                                                           newResource,
                                                                           copyOfOldResource,
                                                                           schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);
                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REPLACE)) {
                    if (newResource == null) {
                        newResource = (R) PatchOperationUtil.doPatchReplace(operation,
                                                                            getDecoder(),
                                                                            oldResource,
                                                                            copyOfOldResource,
                                                                            schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);

                    } else {
                        newResource = (R) PatchOperationUtil.doPatchReplace(operation,
                                                                            getDecoder(),
                                                                            newResource,
                                                                            copyOfOldResource,
                                                                            schema);
                        copyOfOldResource = (R) CopyUtil.deepCopy(newResource);
                    }
                } else {
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
                }
            }

            // get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema),
                                                                  attributes,
                                                                  excludeAttributes);


            R validatedResource = (R) ServerSideValidator.validateUpdatedSCIMObject(originalResource,
                                                                                    newResource,
                                                                                    schema);
            newResource = resourceHandler.update(validatedResource, requiredAttributes);

            // encode the newly created SCIM resource object and add id attribute to Location header.
            String encodedResource;
            Map<String, String> httpHeaders = new HashMap<>();
            if (newResource != null) {
                // create a deep copy of the resource object since we are going to change it.
                R copiedResource = (R) CopyUtil.deepCopy(newResource);
                // need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedResource, attributes, excludeAttributes);
                encodedResource = getEncoder().encodeSCIMObject(copiedResource);
                // add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                                getResourceEndpointURL(resourceHandler.getResourceEndpoint()) + "/" +
                                    newResource.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated resource is null.";
                throw new CharonException(error);
            }
            // put the URI of the resource object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedResource, httpHeaders);
        } catch (AbstractCharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return encodeSCIMException(new CharonException(INTERNAL_ERROR_MESSAGE));
        }
    }


    /**
     * Creates the Listed Resource..
     *
     * @param resources the list of resources that should be listed to the client
     * @param startIndex the start index (do not confuse with index of the given list)
     * @param totalResults the total number of results
     * @return the listed resource
     */
    public ListedResource createListedResource(List<Object> resources, int startIndex, int totalResults) {
        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(totalResults);
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(resources.size());
        for (Object resource : resources) {
            listedResource.addResource((R) resource);
        }
        return listedResource;
    }

    public ResourceHandler<R> getResourceHandler() {
        return resourceHandler;
    }
}
