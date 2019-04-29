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
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.ListedResource;
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
import java.util.Collections;
import java.util.HashMap;
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
                List<Object> tempList = userManager
                        .listGroupsWithGET(rootNode, startIndex, count, sortBy, sortOrder, domainName,
                                requiredAttributes);
                return processGroupList(tempList, encoder, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                // Log the error as well.
                // Throw internal server error.
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

        // Check whether provided sortOrder is valid or not.
        if (sortOrder != null) {
            if (!(sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING) || sortOrder
                    .equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                String error = " Invalid sortOrder value is specified";
                throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
            }
        }
        // If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
        // ascending.
        if (sortOrder == null && sortBy != null) {
            sortOrder = SCIMConstants.OperationalConstants.ASCENDING;
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

            //// Build node for filtering.
            Node rootNode = buildNode(filter, schema);

            // Obtain the json encoder.
            JSONEncoder encoder = getEncoder();

            // Get the URIs of required attributes which must be given a value.
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            // API group should pass a user manager to GroupResourceEndpoint.
            if (userManager != null) {
                List<Object> tempList = userManager
                        .listGroupsWithGET(rootNode, startIndex, count, sortBy, sortOrder, domainName,
                                requiredAttributes);
                return processGroupList(tempList, encoder, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                // Log the error as well.
                // Throw internal server error.
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
     * @param tempList          Filtered user list
     * @param encoder           Json encoder
     * @param attributes        Required attributes
     * @param excludeAttributes Exclude attributes
     * @param startIndex        Starting index
     * @return SCIM response
     * @throws NotFoundException
     * @throws CharonException
     * @throws BadRequestException
     */
    private SCIMResponse processGroupList(List<Object> tempList, JSONEncoder encoder, String attributes,
            String excludeAttributes, int startIndex) throws NotFoundException, CharonException, BadRequestException {

        int totalResults = 0;
        List<Object> returnedGroups;
        if (tempList == null) {
            tempList = Collections.emptyList();
        }
        try {
            totalResults = (int) tempList.get(0);
            tempList.remove(0);
        } catch (IndexOutOfBoundsException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Group result list is empty.");
            }
            totalResults = tempList.size();
        } catch (ClassCastException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Parse error while getting the group result count. Setting result count as: " + tempList.size(),
                        ex);
            }
            totalResults = tempList.size();
        }
        returnedGroups = tempList;

        for (Object group : returnedGroups) {
            // Perform service provider side validation.
            ServerSideValidator
                    .validateRetrievedSCIMObjectInList((Group) group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                            attributes, excludeAttributes);
        }
        // Create a listed resource object out of the returned groups list.
        ListedResource listedResource = createListedResource(returnedGroups, startIndex, totalResults);
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
            searchRequest.setStartIndex(ResourceManagerUtil.processCount(searchRequest.getStartIndexStr()));

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
                List<Object> tempList = userManager.listGroupsWithPost(searchRequest, requiredAttributes);

                totalResults = (int) tempList.get(0);
                tempList.remove(0);

                returnedGroups = tempList;

                for (Object group : returnedGroups) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList((Group) group, schema,
                            searchRequest.getAttributesAsString(), searchRequest.getExcludedAttributesAsString());
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(
                        returnedGroups, searchRequest.getStartIndex(), totalResults);
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


    /*
     * method which corresponds to HTTP PATCH - patch the group
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager,
                                        String attributes, String excludeAttributes) {
        try {
            if (userManager == null) {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //obtain the json decoder.
            JSONDecoder decoder = getDecoder();
            //decode the SCIM User object, encoded in the submitted payload.
            List<PatchOperation> opList = decoder.decodeRequest(scimObjectString);

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            //get the group from the user core
            Group oldGroup = userManager.getGroup(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
            if (oldGroup == null) {
                throw new NotFoundException("No group with the id : " + existingId + " in the user store.");
            }
            //make a copy of the original group
            Group copyOfOldGroup = (Group) CopyUtil.deepCopy(oldGroup);
            //make another copy of original group.
            //this will be used to restore to the original condition if failure occurs.
            Group originalGroup = (Group) CopyUtil.deepCopy(copyOfOldGroup);

            Group newGroup = null;

            for (PatchOperation operation : opList) {

                if (operation.getOperation().equals(SCIMConstants.OperationalConstants.ADD)) {
                    if (newGroup == null) {
                        newGroup = (Group) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), oldGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);

                    } else {
                        newGroup = (Group) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), newGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);

                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REMOVE)) {
                    if (newGroup == null) {
                        newGroup = (Group) PatchOperationUtil.doPatchRemove
                                (operation, oldGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);

                    } else {
                        newGroup = (Group) PatchOperationUtil.doPatchRemove
                                (operation, newGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);
                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REPLACE)) {
                    if (newGroup == null) {
                        newGroup = (Group) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), oldGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);

                    } else {
                        newGroup = (Group) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), newGroup, copyOfOldGroup, schema);
                        copyOfOldGroup = (Group) CopyUtil.deepCopy(newGroup);
                    }
                } else  {
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
                }
            }

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                    ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);


            Group validatedGroup = (Group) ServerSideValidator.validateUpdatedSCIMObject
                    (originalGroup, newGroup, schema);
            newGroup = userManager.updateGroup(originalGroup, validatedGroup, requiredAttributes);

            //encode the newly created SCIM group object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (newGroup != null) {
                //create a deep copy of the group object since we are going to change it.
                Group copiedGroup = (Group) CopyUtil.deepCopy(newGroup);
                //validate before return.
                ServerSideValidator.validateReturnedAttributes(copiedGroup, attributes, excludeAttributes);
                encodedGroup = getEncoder().encodeSCIMObject(copiedGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + newGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated group resource is null.";
                throw new CharonException(error);
            }
            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);

        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException e1 = new CharonException("Error in performing the patch operation on group resource.", e);
            return AbstractResourceManager.encodeSCIMException(e1);
        }
    }

    /*
     * Creates the Listed Resource.
     *
     * @param groups
     * @return
     */
    public ListedResource createListedResource(List<Object> groups, int startIndex, int totalResults)
            throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(totalResults);
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(groups.size());
        for (Object group : groups) {
            Map<String, Attribute> userAttributes = ((Group) group).getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }


}
