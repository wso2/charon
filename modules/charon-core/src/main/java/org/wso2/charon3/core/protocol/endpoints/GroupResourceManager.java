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

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.plainobjects.GroupsGetResponse;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.PatchOperationUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;
import org.wso2.charon3.core.utils.codeutils.FilterTreeManager;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * REST API exposed by Charon-Core to perform operations on GroupResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on Group ,
 * based on the HTTP requests received by SCIM Client.
 */

public class GroupResourceManager extends AbstractResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(GroupResourceManager.class);

    /*
     * Retrieves a group resource given an unique group id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param usermanager
     * @param attributes
     * @param excludeAttributes
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder();
            // returns core-group schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            //API user should pass a usermanager usermanager to GroupResourceEndpoint.
            //retrieve the group from the provided usermanager.
            Group group = ((UserManager) userManager).getGroup(id, requiredAttributes);

            //if group not found, return an error in relevant format.
            if (group == null) {
                String message = "Group not found in the user store.";
                throw new NotFoundException(message);
            }

            ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema, attributes, excludeAttributes);
            //convert the group into specific format.
            String encodedGroup = encoder.encodeSCIMObject(group);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Create group in the service provider given the submitted payload that contains the SCIM group
     * resource, format and the handler to usermanager.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param usermanager
     * @param  attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager,
                               String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            decoder = getDecoder();
            // returns core-group schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);
            //decode the SCIM group object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString, schema, new Group());
            //validate decoded group
            ServerSideValidator.validateCreatedSCIMObject(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
            //handover the SCIM User object to the group usermanager provided by the SP.
            Group createdGroup;
            //need to send back the newly created group in the response payload
            createdGroup = ((UserManager) userManager).createGroup(group, requiredAttributes);

            //encode the newly created SCIM group object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (createdGroup != null) {
                ServerSideValidator.validateReturnedAttributes(createdGroup, attributes, excludeAttributes);
                encodedGroup = encoder.encodeSCIMObject(createdGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + createdGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String message = "Newly created Group resource is null..";
                throw new InternalErrorException(message);
            }

            //put the uri of the Group object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedGroup, httpHeaders);

        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (ConflictException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Method of the ResourceManager that is mapped to HTTP Delete method..
     *
     * @param id - unique resource id
     * @param usermanager - usermanager instance defined by the external implementor of charon
     * @return
     */
    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            if (userManager != null) {
            /*handover the SCIM User object to the user usermanager provided by the SP for the delete operation*/
                userManager.deleteGroup(id);
                //on successful deletion SCIMResponse only has 204 No Content status code.
                return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        }
    }

    /*
     * Method to list the groups at the /Groups endpoint
     *
     * @param usermanager
     * @param filter
     * @param startIndex
     * @param count
     * @param sortBy
     * @param sortOrder
     * @param domainName
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
            String sortOrder, String domainName, String attributes, String excludeAttributes) {

        //According to SCIM 2.0 spec minus values will be considered as 0
        if (count < 0) {
            count = 0;
        }
        //According to SCIM 2.0 spec minus values will be considered as 1
        if (startIndex < 1) {
            startIndex = 1;
        }
        try {
            // Resolving sorting order.
            sortOrder = resolveSortOrder(sortOrder, sortBy);

            // Unless configured returns core-user schema or else returns extended user schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);

            // Build node for filtering.
            JSONEncoder encoder = getEncoder();

            // Get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            // API group should pass a user manager to GroupResourceEndpoint.
            if (userManager != null) {
                GroupsGetResponse groupsResponse = userManager.listGroupsWithGET(rootNode, startIndex,
                        count, sortBy, sortOrder, domainName, requiredAttributes);
                return processGroupList(groupsResponse, encoder, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                if (logger.isDebugEnabled()) {
                    logger.error(error);
                }
                throw new InternalErrorException(error);
            }
        } catch (CharonException | NotFoundException | InternalErrorException | BadRequestException |
                NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
    }

    /**
     * Resolves the sorting order of the filter.
     *
     * @param sortOrder Sort order in the request.
     * @param sortBy    SortBy in the request.
     * @return Resolved sorting order.
     * @throws BadRequestException Invalid sorting order.
     */
    private String resolveSortOrder(String sortOrder, String sortBy) throws BadRequestException {

        // Check whether the provided sortOrder is valid or not.
        if (sortOrder != null) {
            if (!(SCIMConstants.OperationalConstants.ASCENDING.equalsIgnoreCase(sortOrder)
                    || SCIMConstants.OperationalConstants.DESCENDING.equalsIgnoreCase(sortOrder))) {
                String error = " Invalid sortOrder value is specified";
                throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
            }
        } else {
            // If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
            // ascending.
            if (sortBy != null) {
                sortOrder = SCIMConstants.OperationalConstants.ASCENDING;
            }
        }
        return sortOrder;
    }

    /**
     * Build Node for filtering.
     *
     * @param filter Filter in the request.
     * @param schema Schema
     * @return Node
     * @throws BadRequestException
     * @throws IOException
     */
    private Node buildNode(String filter, SCIMResourceTypeSchema schema) throws BadRequestException, IOException {

        if (filter != null) {
            FilterTreeManager filterTreeManager = new FilterTreeManager(filter, schema);
            return filterTreeManager.buildTree();
        }
        return null;
    }

    /**
     * Method to list groups at the Groups endpoint.
     * In the method, when the count is zero, the response will get zero results. When the count value is not
     * specified (null) a default number of values for response will be returned. Any negative value to the count
     * will return all the groups.
     *
     * @param userManager       User manager
     * @param filter            Filter to be executed
     * @param startIndexInt     Starting index value of the filter
     * @param countInt          Number of required results
     * @param sortBy            SortBy
     * @param sortOrder         Sorting order
     * @param domainName        Domain name
     * @param attributes        Attributes in the request
     * @param excludeAttributes Exclude attributes
     * @return SCIM response
     */
    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, Integer startIndexInt, Integer countInt,
            String sortBy, String sortOrder, String domainName, String attributes, String excludeAttributes) {

        FilterTreeManager filterTreeManager;
        try {
            Integer count = ResourceManagerUtil.processCount(countInt);
            Integer startIndex = ResourceManagerUtil.processStartIndex(startIndexInt);

            // Resolving sorting order.
            sortOrder = resolveSortOrder(sortOrder, sortBy);

            // Unless configured returns core-user schema or else returns extended user schema.
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);

            // Obtain the json encoder.
            JSONEncoder encoder = getEncoder();

            // Get the URIs of required attributes which must be given a value.
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            // API group should pass a user manager to GroupResourceEndpoint.
            if (userManager != null) {
                GroupsGetResponse groupsResponse = userManager.listGroupsWithGET(rootNode, startIndex, count,
                        sortBy, sortOrder, domainName, requiredAttributes);
                return processGroupList(groupsResponse, encoder, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                if (logger.isDebugEnabled()) {
                    logger.debug(error);
                }
                throw new InternalErrorException(error);
            }
        } catch (CharonException | NotFoundException | InternalErrorException | BadRequestException |
                NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
    }

    /**
     * Method to process a list and return a SCIM response.
     *
     * @param groupsResponse    Response made of a list of groups and the total number of groups.
     * @param encoder           Json encoder
     * @param attributes        Required attributes
     * @param excludeAttributes Exclude attributes
     * @param startIndex        Starting index
     * @return SCIM response
     * @throws NotFoundException
     * @throws CharonException
     * @throws BadRequestException
     */
    private SCIMResponse processGroupList(GroupsGetResponse groupsResponse, JSONEncoder encoder, String attributes,
            String excludeAttributes, int startIndex) throws NotFoundException, CharonException, BadRequestException {

        if (groupsResponse == null) {
            groupsResponse = new GroupsGetResponse(0, Collections.emptyList());
        }
        if (groupsResponse.getGroups() == null) {
            groupsResponse.setGroups(Collections.emptyList());
        }
        for (Group group : groupsResponse.getGroups()) {
            // Perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObjectInList(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                            attributes, excludeAttributes);
        }
        // Create a listed resource object out of the returned groups list.
        ListedResource listedResource = createListedResource(groupsResponse, startIndex);
        // Convert the listed resource into specific format.
        String encodedListedResource = encoder.encodeSCIMObject(listedResource);
        // If there are any http headers to be added in the response header.
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);
    }

    /*
     * this facilitates the querying using HTTP POST
     * @param resourceString
     * @param usermanager
     * @return
     */

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        JSONEncoder encoder = null;
        JSONDecoder decoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            decoder = getDecoder();

            // return core group schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();

            //create the search request object
            SearchRequest searchRequest = decoder.decodeSearchRequestBody(resourceString, schema);
            searchRequest.setCount(ResourceManagerUtil.processCount(searchRequest.getCountStr()));
            searchRequest.setStartIndex(ResourceManagerUtil.processStartIndex(searchRequest.getStartIndexStr()));

            if (searchRequest.getSchema() != null && !searchRequest.getSchema().equals(SCIMConstants
                    .SEARCH_SCHEMA_URI)) {
                throw new BadRequestException("Provided schema is invalid", ResponseCodeConstants.INVALID_VALUE);
            }

            //check whether provided sortOrder is valid or not
            if (searchRequest.getSortOder() != null) {
                if (!(searchRequest.getSortOder().equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING)
                        || searchRequest.getSortOder().equalsIgnoreCase(SCIMConstants.OperationalConstants
                        .DESCENDING))) {
                    String error = " Invalid sortOrder value is specified";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }
            //If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
            // ascending.
            if (searchRequest.getSortOder() == null && searchRequest.getSortBy() != null) {
                searchRequest.setSortOder(SCIMConstants.OperationalConstants.ASCENDING);
            }

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), searchRequest.getAttributesAsString(),
                    searchRequest.getExcludedAttributesAsString());

            List<Object> returnedGroups;
            int totalResults = 0;
            //API user should pass a usermanager usermanager to UserResourceEndpoint.
            if (userManager != null) {
                GroupsGetResponse groupsResponse = userManager.listGroupsWithPost(searchRequest, requiredAttributes);

                for (Group group : groupsResponse.getGroups()) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(group, schema,
                            searchRequest.getAttributesAsString(), searchRequest.getExcludedAttributesAsString());
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(groupsResponse, searchRequest.getStartIndex());
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> responseHeaders = new HashMap<String, String>();
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /*
     * method which corresponds to HTTP PUT - delete the group
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      UserManager userManager, String attributes, String excludeAttributes) {
        //needs to validate the incoming object. eg: id can not be set by the consumer.

        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder.
            decoder = getDecoder();

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);
            //decode the SCIM User object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString, schema, new Group());
            Group updatedGroup = null;
            if (userManager != null) {
                //retrieve the old object
                Group oldGroup = userManager.getGroup(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
                if (oldGroup != null) {
                    Group newGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject(oldGroup, group, schema);
                    updatedGroup = userManager.updateGroup(oldGroup, newGroup, requiredAttributes);

                } else {
                    String error = "No user exists with the given id: " + existingId;
                    throw new NotFoundException(error);
                }

            } else {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedGroup != null) {
                //create a deep copy of the user object since we are going to change it.
                Group copiedGroup = (Group) CopyUtil.deepCopy(updatedGroup);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedGroup, attributes, excludeAttributes);
                encodedGroup = encoder.encodeSCIMObject(copiedGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + updatedGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated Group resource is null.";
                throw new InternalErrorException(error);
            }

            //put the uri of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);

        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    /**
     * Updates the group based on the operations defined in the patchRequest. The updated group information is sent
     * back in the response.
     * @param existingId        SCIM2 ID of the existing group.
     * @param patchRequest      SCIM2 patch request.
     * @param userManager       SCIM UserManager that handles the persistence layer.
     * @param attributes        Attributes to return in the response.
     * @param excludeAttributes Attributes to exclude in the response.
     * @return SCIM Response.
     */
    public SCIMResponse updateWithPATCH(String existingId, String patchRequest, UserManager userManager,
                                        String attributes, String excludeAttributes) {
        try {
            if (userManager == null) {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);

            List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);

            if (!isDeleteAllUsersOperationFound(opList)) {
                return updateWithPatchForAddRemoveOperations(existingId, opList, userManager, attributes,
                        excludeAttributes);
            }

            // Get the group from the user core
            Group oldGroup = userManager.getGroup(existingId, requiredAttributes);
            if (oldGroup == null) {
                throw new NotFoundException("No group with the id : " + existingId + " in the user store.");
            }

            Group originalGroup = (Group) CopyUtil.deepCopy(oldGroup);
            Group patchedGroup = doPatchGroup(oldGroup, schema, patchRequest);

            Group updatedGroup = userManager.updateGroup(originalGroup, patchedGroup, requiredAttributes);
            if (updatedGroup != null) {
                // Create a deep copy of the group object since we are going to change it.
                Group copyOfUpdatedGroup = (Group) CopyUtil.deepCopy(updatedGroup);
                ServerSideValidator.validateReturnedAttributes(copyOfUpdatedGroup, attributes, excludeAttributes);

                String encodedGroup = getEncoder().encodeSCIMObject(copyOfUpdatedGroup);
                Map<String, String> httpHeaders = new HashMap<>();
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + updatedGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
            } else {
                String error = "Updated group resource is null.";
                throw new CharonException(error);
            }
        } catch (NotFoundException | BadRequestException | NotImplementedException | CharonException |
                InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException e1 = new CharonException("Error in performing the patch operation on group resource.", e);
            return AbstractResourceManager.encodeSCIMException(e1);
        }
    }

    private boolean isDeleteAllUsersOperationFound(List<PatchOperation> patchOperations) throws JSONException {

        for (PatchOperation patchOperation : patchOperations) {
            String operation = patchOperation.getOperation();
            String path = patchOperation.getPath();
            if (!(patchOperation.getValues() instanceof String)) {
                JSONObject valuesJson = (JSONObject) patchOperation.getValues();
                if (operation.equals(SCIMConstants.OperationalConstants.REPLACE) &&
                        ((path != null && path.equals(SCIMConstants.GroupSchemaConstants.MEMBERS)) ||
                                (valuesJson != null && valuesJson.has(SCIMConstants.GroupSchemaConstants.MEMBERS)))) {
                    return true;
                } else if (operation.equals(SCIMConstants.OperationalConstants.REMOVE) && path != null
                        && path.equals(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates the group based on the operations defined in the patchRequest. The updated group information is sent
     * back in the response.
     *
     * @param existingGroupId   SCIM2 ID of the existing group.
     * @param opList            List of patch operations.
     * @param userManager       SCIM UserManager that handles the persistence layer.
     * @param attributes        Attributes to return in the response.
     * @param excludeAttributes Attributes to exclude in the response.
     * @return SCIM Response.
     */
    public SCIMResponse updateWithPatchForAddRemoveOperations(String existingGroupId, List<PatchOperation> opList,
                                                              UserManager userManager, String attributes,
                                                              String excludeAttributes) {

        try {
            Map<String, List<PatchOperation>> patchOperations = buildPatchOperationsMap(opList);

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            String groupName = getGroupName(userManager, existingGroupId);

            processGroupPatchOperations(patchOperations, schema);

            // Get the URIs of required attributes which must be given a value.
            Map<String, Boolean> requiredAttributes =
                    ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            Group updatedGroup = userManager.patchGroup(existingGroupId, groupName, patchOperations,
                    requiredAttributes);

            if (updatedGroup != null) {
                // Create a deep copy of the group object since we are going to change it.
                Group copyOfUpdatedGroup = (Group) CopyUtil.deepCopy(updatedGroup);
                ServerSideValidator.validateReturnedAttributes(copyOfUpdatedGroup, attributes, excludeAttributes);
                // Encode the updated group object and add id attribute to Location header.
                String encodedGroup = getEncoder().encodeSCIMObject(copyOfUpdatedGroup);

                Map<String, String> httpHeaders = new HashMap<>();
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                        getResourceEndpointURL(SCIMConstants.GROUP_ENDPOINT) + "/" + updatedGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
            } else {
                String error = "Updated group resource is null.";
                throw new CharonException(error);
            }
        } catch (NotFoundException | BadRequestException | NotImplementedException | CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException e1 = new CharonException("Error in performing the patch operation on group resource.", e);
            return AbstractResourceManager.encodeSCIMException(e1);
        }
    }

    private Map<String, List<PatchOperation>> buildPatchOperationsMap(List<PatchOperation> opList)
            throws BadRequestException {

        Map<String, List<PatchOperation>> patchOperations = new HashMap<>();

        patchOperations.put(SCIMConstants.OperationalConstants.ADD, new ArrayList<>());
        patchOperations.put(SCIMConstants.OperationalConstants.REMOVE, new ArrayList<>());
        patchOperations.put(SCIMConstants.OperationalConstants.REPLACE, new ArrayList<>());

        for (PatchOperation patchOperation : opList) {
            switch (patchOperation.getOperation()) {
                case SCIMConstants.OperationalConstants.ADD:
                    patchOperations.get(SCIMConstants.OperationalConstants.ADD).add(patchOperation);
                    break;
                case SCIMConstants.OperationalConstants.REMOVE:
                    patchOperations.get(SCIMConstants.OperationalConstants.REMOVE).add(patchOperation);
                    break;
                case SCIMConstants.OperationalConstants.REPLACE:
                    patchOperations.get(SCIMConstants.OperationalConstants.REPLACE).add(patchOperation);
                    break;
                default:
                    throw new BadRequestException("Unknown operation: " + patchOperation.getOperation(),
                            ResponseCodeConstants.INVALID_SYNTAX);
            }
        }
        return patchOperations;
    }

    private void updateWithPatchForAddRemoveOperations(String existingGroupId, List<PatchOperation> opList,
                                                       UserManager userManager) throws BadRequestException,
            NotImplementedException, NotFoundException, CharonException {

        Map<String, List<PatchOperation>> patchOperations = buildPatchOperationsMap(opList);
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        String groupName = getGroupName(userManager, existingGroupId);
        processGroupPatchOperations(patchOperations, schema);
        userManager.patchGroup(existingGroupId, groupName, patchOperations);
    }

    private String getGroupName(UserManager userManager, String groupId)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException {

        HashMap<String, Boolean> requiredAttributes = new HashMap<>();
        requiredAttributes.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME_URI, true);
        Group group = userManager.getGroup(groupId, requiredAttributes);

        if (group != null) {
            return group.getDisplayName();
        } else {
            throw new NotFoundException("Group not found for ID: " + groupId);
        }
    }

    private void processGroupPatchOperations(Map<String, List<PatchOperation>> patchOperations,
                                             SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, JSONException {

        Iterator<PatchOperation> replaceOperationIterator = patchOperations
                .get(SCIMConstants.OperationalConstants.REPLACE).iterator();

        while (replaceOperationIterator.hasNext()) {
            PatchOperation replaceOperation = replaceOperationIterator.next();
            PatchOperation addOperation = new PatchOperation();

            addOperation.setOperation(SCIMConstants.OperationalConstants.ADD);
            addOperation.setExecutionOrder(replaceOperation.getExecutionOrder());

            if (replaceOperation.getPath() != null && replaceOperation.getValues() != null) {
                addOperation.setValues(replaceOperation.getValues());
                addOperation.setPath(replaceOperation.getPath());
                patchOperations.get(SCIMConstants.OperationalConstants.ADD).add(addOperation);
            } else if (replaceOperation.getValues() != null) {
                AbstractSCIMObject attributeHoldingSCIMObject = getDecoder().decode(replaceOperation.getValues()
                        .toString(), schema);

                if (attributeHoldingSCIMObject == null) {
                    throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
                }

                Map<String, Attribute> attributeList = attributeHoldingSCIMObject.getAttributeList();

                if (!attributeList.containsKey(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
                    throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
                }

                addOperation.setValues(replaceOperation.getValues());
                patchOperations.get(SCIMConstants.OperationalConstants.ADD).add(addOperation);
            }
            replaceOperationIterator.remove();
        }

        for (PatchOperation patchOperation : patchOperations.get(SCIMConstants.OperationalConstants.ADD)) {

            if (patchOperation.getPath() != null) {
                if (patchOperation.getPath().equals(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME) &&
                        patchOperation.getValues() != null) {
                    JSONObject attributePrefixedJson = new JSONObject();
                    if (patchOperation.getValues() instanceof String) {
                        attributePrefixedJson.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME,
                                patchOperation.getValues());
                    } else {
                        JSONObject valuesPropertyJson = (JSONObject) patchOperation.getValues();
                        attributePrefixedJson.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, valuesPropertyJson);
                    }
                    patchOperation.setValues(attributePrefixedJson);
                } else if (patchOperation.getPath().equals(SCIMConstants.GroupSchemaConstants.MEMBERS) &&
                        patchOperation.getValues() != null) {
                    JSONObject valuesPropertyJson = (JSONObject) patchOperation.getValues();
                    JSONObject attributePrefixedJson = new JSONObject();

                    attributePrefixedJson.put(SCIMConstants.GroupSchemaConstants.MEMBERS, valuesPropertyJson);
                    patchOperation.setValues(attributePrefixedJson);
                } else {
                    throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
                }
                patchOperation.setPath(null);
            }
            processValueAttributeOfOperation(schema, patchOperation);
        }

        for (PatchOperation patchOperation : patchOperations.get(SCIMConstants.OperationalConstants.REMOVE)) {
            if (patchOperation.getPath() == null) {
                throw new BadRequestException("No path value specified for remove operation",
                        ResponseCodeConstants.NO_TARGET);
            }

            String path = patchOperation.getPath();
            // Split the path to extract the filter if present.
            String[] parts = path.split("[\\[\\]]");

            if (!SCIMConstants.GroupSchemaConstants.MEMBERS.equalsIgnoreCase(parts[0])) {
                throw new BadRequestException(parts[0] + " is not a valid attribute.",
                        ResponseCodeConstants.INVALID_SYNTAX);
            }
            patchOperation.setAttributeName(SCIMConstants.GroupSchemaConstants.MEMBERS);

            if (parts.length != 1) {
                Map<String, String> memberObject = new HashMap<>();
                memberObject.put(SCIMConstants.GroupSchemaConstants.DISPLAY, null);
                memberObject.put(SCIMConstants.GroupSchemaConstants.VALUE, null);

                // Currently we only support simple filters here.
                String[] filterParts = parts[1].split(" ");

                if (filterParts.length != 3 || !memberObject.containsKey(filterParts[0])) {
                    throw new BadRequestException("Invalid filter", ResponseCodeConstants.INVALID_SYNTAX);
                }

                if (!filterParts[1].equalsIgnoreCase((SCIMConstants.OperationalConstants.EQ).trim())) {
                    throw new NotImplementedException("Only Eq filter is supported");
                }
                /*
                According to the specification filter attribute value specified with quotation mark, so we need to
                remove it if exists.
                */
                filterParts[2] = filterParts[2].replaceAll("^\"|\"$", "");
                memberObject.put(filterParts[0], filterParts[2]);
                patchOperation.setValues(memberObject);
            }
        }
    }

    private void processValueAttributeOfOperation(SCIMResourceTypeSchema schema, PatchOperation patchOperation)
            throws CharonException, BadRequestException {

        AbstractSCIMObject attributeHoldingSCIMObject = getDecoder().decode(patchOperation.getValues().toString(),
                schema);
        if (attributeHoldingSCIMObject == null) {
            throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
        }

        Map<String, Attribute> attributeList = attributeHoldingSCIMObject.getAttributeList();

        if (attributeList.containsKey(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            patchOperation.setAttributeName(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME);
            patchOperation.setValues(
                    ((SimpleAttribute) attributeList.get(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME))
                            .getStringValue());
        } else if (attributeList.containsKey(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            patchOperation.setAttributeName(SCIMConstants.GroupSchemaConstants.MEMBERS);
            patchOperation.setValues(transformMembersAttributeToMap((MultiValuedAttribute) attributeList
                    .get(SCIMConstants.GroupSchemaConstants.MEMBERS)));
        }
    }

    private List<Map<String, String>> transformMembersAttributeToMap(MultiValuedAttribute multiValuedMembersAttribute)
            throws CharonException, BadRequestException {

        List<Map<String, String>> memberList = new ArrayList<>();
        List<Attribute> subValuesList = multiValuedMembersAttribute.getAttributeValues();
        for (Attribute subValue : subValuesList) {
            ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
            Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();

            if (!subAttributesList.containsKey(SCIMConstants.CommonSchemaConstants.VALUE) ||
                    !subAttributesList.containsKey(SCIMConstants.CommonSchemaConstants.DISPLAY)) {
                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
            } else if (StringUtils.isEmpty(((SimpleAttribute)
                        subAttributesList.get(SCIMConstants.CommonSchemaConstants.VALUE)).getStringValue())) {
                throw new BadRequestException(ResponseCodeConstants.INVALID_VALUE);
            }

            Map<String, String> member = new HashMap<>();
            member.put(SCIMConstants.CommonSchemaConstants.VALUE, ((SimpleAttribute)
                    (subAttributesList.get(SCIMConstants.CommonSchemaConstants.VALUE))).getStringValue());
            member.put(SCIMConstants.CommonSchemaConstants.DISPLAY, ((SimpleAttribute)
                    (subAttributesList.get(SCIMConstants.CommonSchemaConstants.DISPLAY))).getStringValue());
            memberList.add(member);
        }
        return memberList;
    }

    /**
     * Updates the group based on the operations defined in the patchRequest. The updated group information is not
     * sent back in the response.
     *
     * @param existingGroupId SCIM2 ID of the existing group
     * @param patchRequest    SCIM2 patch request
     * @param userManager     SCIM UserManager that handles the persistence layer.
     * @return
     */
    @Override
    public SCIMResponse updateWithPATCH(String existingGroupId, String patchRequest, UserManager userManager) {

        try {
            if (userManager == null) {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            Map<String, Boolean> requestAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);

            List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);

            if (!isDeleteAllUsersOperationFound(opList)) {
                updateWithPatchForAddRemoveOperations(existingGroupId, opList, userManager);
            } else {
                Group oldGroup = userManager.getGroup(existingGroupId, requestAttributes);
                if (oldGroup == null) {
                    throw new NotFoundException("No group with the id : " + existingGroupId
                            + " exists in the user store.");
                }

                // Make a copy of original group. This will be used to restore the original condition if failure occurs.
                Group originalGroup = (Group) CopyUtil.deepCopy(oldGroup);
                Group patchedGroup = doPatchGroup(oldGroup, schema, patchRequest);

                userManager.updateGroup(originalGroup, patchedGroup);
            }

            // Build the 204 response.
            Map<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, httpHeaders);
        } catch (NotFoundException | BadRequestException | NotImplementedException | CharonException |
                InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException ex = new CharonException("Error in performing the patch operation on group resource.", e);
            return AbstractResourceManager.encodeSCIMException(ex);
        }
    }


    private Group doPatchGroup(Group oldGroup, SCIMResourceTypeSchema groupSchema, String patchRequest)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        // Make a copy of the original group.
        Group originalGroup = (Group) CopyUtil.deepCopy(oldGroup);
        Group copyOfOldGroup = (Group) CopyUtil.deepCopy(oldGroup);

        Group patchedGroup = null;
        List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);
        for (PatchOperation operation : opList) {
            switch (operation.getOperation()) {
                case SCIMConstants.OperationalConstants.ADD:
                    if (patchedGroup == null) {
                        patchedGroup = (Group) PatchOperationUtil
                                .doPatchAdd(operation, getDecoder(), oldGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);
                    } else {
                        patchedGroup = (Group) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), patchedGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);

                    }
                    break;
                case SCIMConstants.OperationalConstants.REMOVE:
                    if (patchedGroup == null) {
                        patchedGroup = (Group) PatchOperationUtil.doPatchRemove
                                (operation, oldGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);

                    } else {
                        patchedGroup = (Group) PatchOperationUtil.doPatchRemove
                                (operation, patchedGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);
                    }
                    break;
                case SCIMConstants.OperationalConstants.REPLACE:
                    if (patchedGroup == null) {
                        patchedGroup = (Group) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), oldGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);

                    } else {
                        patchedGroup = (Group) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), patchedGroup, copyOfOldGroup, groupSchema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(patchedGroup);
                    }
                    break;
                default:
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
            }
        }

        return (Group) ServerSideValidator.validateUpdatedSCIMObject(originalGroup, patchedGroup, groupSchema);
    }

    /**
     * Creates the Listed Resource.
     *
     * @param groupsResponses   Response made of a list of groups and the total number of groups.
     * @param startIndex        Starting index value.
     * @return
     */
    public ListedResource createListedResource(GroupsGetResponse groupsResponses, int startIndex)
            throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(groupsResponses.getTotalGroups());
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(groupsResponses.getGroups().size());
        for (Group group : groupsResponses.getGroups()) {
            Map<String, Attribute> userAttributes = group.getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }


}
