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
package org.wso2.charon.core.v2.protocol.endpoints;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.config.CharonConfiguration;
import org.wso2.charon.core.v2.encoder.JSONDecoder;
import org.wso2.charon.core.v2.encoder.JSONEncoder;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.exceptions.ConflictException;
import org.wso2.charon.core.v2.exceptions.InternalErrorException;
import org.wso2.charon.core.v2.exceptions.NotFoundException;
import org.wso2.charon.core.v2.exceptions.NotImplementedException;
import org.wso2.charon.core.v2.extensions.UserManager;
import org.wso2.charon.core.v2.objects.Group;
import org.wso2.charon.core.v2.objects.ListedResource;
import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;
import org.wso2.charon.core.v2.protocol.SCIMResponse;
import org.wso2.charon.core.v2.schema.SCIMConstants;
import org.wso2.charon.core.v2.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.v2.schema.SCIMResourceTypeSchema;
import org.wso2.charon.core.v2.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.v2.schema.ServerSideValidator;
import org.wso2.charon.core.v2.utils.CopyUtil;
import org.wso2.charon.core.v2.utils.ResourceManagerUtil;
import org.wso2.charon.core.v2.utils.codeutils.FilterTreeManager;
import org.wso2.charon.core.v2.utils.codeutils.Node;
import org.wso2.charon.core.v2.utils.codeutils.SearchRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on GroupResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on Group ,
 * based on the HTTP requests received by SCIM Client.
 */

public class GroupResourceManager extends AbstractResourceManager {

    /*
     * Retrieves a group resource given an unique group id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param userManager
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

            //API user should pass a UserManager storage to GroupResourceEndpoint.
            //retrieve the group from the provided storage.
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
     * resource, format and the handler to storage.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param userManager
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
            //handover the SCIM User object to the group storage provided by the SP.
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
     * @param userManager - userManager instance defined by the external implementor of charon
     * @return
     */
    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            if (userManager != null) {
            /*handover the SCIM User object to the user storage provided by the SP for the delete operation*/
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

    /**
     * Method to list the groups at the /Groups endpoint
     *
     * @param userManager
     * @param filter
     * @param startIndex
     * @param count
     * @param sortBy
     * @param sortOrder
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex,
                                    int count, String sortBy, String sortOrder,
                                    String attributes, String excludeAttributes) {

        FilterTreeManager filterTreeManager = null;
        Node rootNode = null;
        JSONEncoder encoder = null;
        try {
            //A value less than one shall be interpreted as 1
            if (startIndex < 1) {
                startIndex = 1;
            }
            //If count is not set, server default should be taken
            if (count == 0) {
                count = CharonConfiguration.getInstance().getCountValueForPagination();
            }

            //check whether provided sortOrder is valid or not
            if (sortOrder != null) {
                if (!(sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING)
                        || sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                    String error = " Invalid sortOrder value is specified";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }
            //If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
            // ascending.
            if (sortOrder == null && sortBy != null) {
                sortOrder = SCIMConstants.OperationalConstants.ASCENDING;
            }

            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            if (filter != null) {
                filterTreeManager = new FilterTreeManager(filter, schema);
                rootNode = filterTreeManager.buildTree();
            }

            //obtain the json encoder
            encoder = getEncoder();

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            List<Object> returnedGroups;
            int totalResults = 0;
            //API group should pass a UserManager storage to GroupResourceEndpoint.
            if (userManager != null) {
                List<Object> tempList = userManager.listGroupsWithGET(rootNode, startIndex, count,
                        sortBy, sortOrder, requiredAttributes);

                totalResults = (int) tempList.get(0);
                tempList.remove(0);
                returnedGroups = tempList;

                //if groups not found, return an error in relevant format.
                if (returnedGroups.isEmpty()) {
                    String error = "Groups not found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

                for (Object group : returnedGroups) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList((Group) group, SCIMSchemaDefinitions
                                    .SCIM_GROUP_SCHEMA,
                            attributes, excludeAttributes);
                }
                //create a listed resource object out of the returned groups list.
                ListedResource listedResource = createListedResource(returnedGroups, startIndex, totalResults);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> responseHeaders = new HashMap<String, String>();
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //log the error as well.
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
    }

    /*
     * this facilitates the querying using HTTP POST
     * @param resourceString
     * @param userManager
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

            if (searchRequest.getSchema() != null && !searchRequest.getSchema().equals(SCIMConstants
                    .SEARCH_SCHEMA_URI)) {
                throw new BadRequestException("Provided schema is invalid", ResponseCodeConstants.INVALID_VALUE);
            }

            //A value less than one shall be interpreted as 1
            if (searchRequest.getStartIndex() < 1) {
                searchRequest.setStartIndex(1);
            }
            //If count is not set, server default should be taken
            if (searchRequest.getCount() == 0) {
                searchRequest.setCount(CharonConfiguration.getInstance().getCountValueForPagination());
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
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                List<Object> tempList = userManager.listGroupsWithPost(searchRequest, requiredAttributes);

                totalResults = (int) tempList.get(0);
                tempList.remove(0);

                returnedGroups = tempList;

                //if user not found, return an error in relevant format.
                if (returnedGroups.isEmpty()) {
                    String error = "No resulted users are found in the user store.";
                    //throw resource not found.
                    throw new NotFoundException(error);
                }

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
     * @param userManager
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
                Group oldGroup = userManager.getGroup(existingId, null);
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

    @Override
    public SCIMResponse updateWithPATCH(
            String existingId, String scimObjectString, UserManager userManager,
            String attributes, String excludeAttributes) {
        return null;
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
