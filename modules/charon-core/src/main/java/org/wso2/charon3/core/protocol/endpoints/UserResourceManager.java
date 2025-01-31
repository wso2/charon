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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.UsersGetResponse;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on UserResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on USER ,
 * based on the HTTP requests received by SCIM Client.
 */

public class UserResourceManager extends AbstractResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(UserResourceManager.class);


    public UserResourceManager() {

    }

    /*
     * Retrieves a user resource given an unique user id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param usermanager - usermanager instance defined by the external implementor of charon
     * @return SCIM response to be returned.
     */
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = getSchema(userManager);

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            /*API user should pass a usermanager impl to UserResourceEndpoint.
            retrieve the user from the provided UM handler.*/
            User user = ((UserManager) userManager).getUser(id, requiredAttributes);

            //if user not found, return an error in relevant format.
            if (user == null) {
                String error = "User not found in the user store.";
                throw new NotFoundException(error);
            }
            //perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObject(user, schema, attributes, excludeAttributes);
            //convert the user into requested format.
            String encodedUser = encoder.encodeSCIMObject(user);
            //if there are any http headers to be added in the response header.
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT) + "/" + user.getId());
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, responseHeaders);

        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /*
     * Returns SCIMResponse based on the sucess or failure of the create user operation
     *
     * @param scimObjectString -raw string containing user info
     * @return usermanager - usermanager instance defined by the external implementor of charon
     */
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes,
            String excludeAttributes) {

        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = getSchema(userManager);
            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());
            //validate the created user.
            ServerSideValidator.validateCreatedSCIMObject(user, schema);
            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);
            User createdUser;

            if (userManager != null) {
            /*handover the SCIM User object to the user usermanager provided by the SP.
            need to send back the newly created user in the response payload*/
                createdUser = userManager.createUser(user, requiredAttributes);
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> responseHeaders = new HashMap<String, String>();

            if (createdUser != null) {
                // TODO: Until handled properly, assume a not-null user without a user ID is created when a workflow
                //  engagement in involved with user addition flow. Hence, respond with 202 Accepted. See issue :
                //  https://github.com/wso2/product-is/issues/10442 for more info.
                if (StringUtils.isBlank(createdUser.getId())) {
                    return new SCIMResponse(ResponseCodeConstants.CODE_ACCEPTED, null, null);
                }
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(createdUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + createdUser.getId());
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }

            //put the uri of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED,
                    encodedUser, responseHeaders);

        } catch (CharonException e) {
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getStatus() == -1) {
                e.setStatus(ResponseCodeConstants.CODE_INTERNAL_ERROR);
            }
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (ConflictException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (ForbiddenException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /*
     * Method of the ResourceManager that is mapped to HTTP Delete method..
     *
     * @param id          - unique resource id
     * @param usermanager - usermanager instance defined by the external implementor of charon
     * @return
     */

    public SCIMResponse delete(String id, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            if (userManager != null) {
            /*handover the SCIM User object to the user usermanager provided by the SP for the delete operation*/
                userManager.deleteUser(id);
                //on successful deletion SCIMResponse only has 204 No Content status code.
                return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /*
     * To list all the resources of resource endpoint.
     *
     * @param userManager
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
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
            String sortOrder, String domainName, String attributes, String excludeAttributes) {

        try {
            // According to SCIM 2.0 spec minus values will be considered as 0.
            if (count < 0) {
                count = 0;
            }
            // According to SCIM 2.0 spec minus values will be considered as 1.
            if (startIndex < 1) {
                startIndex = 1;
            }

            // Resolve sorting order.
            sortOrder = resolveSortOrder(sortOrder, sortBy);

            // Unless configured returns core-user schema or else returns extended user schema.
            SCIMResourceTypeSchema schema = getSchema(userManager);;

            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);

            // Obtain the json encoder.
            JSONEncoder encoder = getEncoder();

            // Get the URIs of required attributes which must be given a value.
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            // API user should pass a user manager to UserResourceEndpoint.
            if (userManager != null) {
                UsersGetResponse usersGetResponse = userManager
                        .listUsersWithGET(rootNode, startIndex, count, sortBy, sortOrder, domainName,
                                requiredAttributes);

                return processUserList(usersGetResponse, encoder, schema, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                // Log the error as well.
                // Throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException | NotFoundException | InternalErrorException | BadRequestException |
                NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
    }

    /**
     * Method to list users at the Users endpoint.
     * In the method, when the count is zero, the response will get zero results. When the count value is not
     * specified (null) a default number of values for response will be returned. Any negative value to the count
     * will return all the users.
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

        try {
            Integer count = ResourceManagerUtil.processCount(countInt);
            Integer startIndex = ResourceManagerUtil.processStartIndex(startIndexInt);

            // Resolve sorting order.
            sortOrder = resolveSortOrder(sortOrder, sortBy);

            // Unless configured returns core-user schema or else returns extended user schema).
            SCIMResourceTypeSchema schema = getSchema(userManager);

            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);

            // Obtain the json encoder.
            JSONEncoder encoder = getEncoder();

            // Get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            // API user should pass a user manager to UserResourceEndpoint.
            if (userManager != null) {
                UsersGetResponse usersGetResponse = userManager
                        .listUsersWithGET(rootNode, startIndex, count, sortBy, sortOrder, domainName,
                                requiredAttributes);
                return processUserList(usersGetResponse, encoder, schema, attributes, excludeAttributes, startIndex);
            } else {
                String error = "Provided user manager handler is null.";
                // Log the error as well.
                // Throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (CharonException | NotFoundException | InternalErrorException | BadRequestException |
                NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
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
     * Resolves the sorting order of the filter.
     *
     * @param sortOrder Sort order in the request.
     * @param sortBy    SortBy in the request.
     * @return Resolved sorting order.
     * @throws BadRequestException Invalid sorting order.
     */
    private String resolveSortOrder(String sortOrder, String sortBy) throws BadRequestException {

        if (sortOrder != null) {
            if (!(sortOrder.equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING) || sortOrder
                    .equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                String error = "Invalid sortOrder value is specified";
                throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
            }
        }
        // If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
        // ascending.
        if (sortOrder == null && sortBy != null) {
            return SCIMConstants.OperationalConstants.ASCENDING;
        }
        return sortOrder;
    }

    /**
     * Method to process a user list and return a SCIM response.
     *
     * @param usersGetResponse  Filtered user list and total user count.
     * @param encoder           Json encoder
     * @param schema            Schema
     * @param attributes        Required attributes
     * @param excludeAttributes Exclude attributes
     * @param startIndex        Starting index
     * @return SCIM response
     * @throws NotFoundException
     * @throws CharonException
     * @throws BadRequestException
     */
    private SCIMResponse processUserList(UsersGetResponse usersGetResponse, JSONEncoder encoder,
            SCIMResourceTypeSchema schema, String attributes, String excludeAttributes, int startIndex)
            throws NotFoundException, CharonException, BadRequestException {

        if (usersGetResponse == null) {
            usersGetResponse = new UsersGetResponse(0, Collections.emptyList());
        }
        if (usersGetResponse.getUsers() == null) {
            usersGetResponse.setUsers(Collections.emptyList());
        }
        for (User user : usersGetResponse.getUsers()) {
            // Perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObjectInList(user, schema, attributes, excludeAttributes);
        }
        // Create a listed resource object out of the returned users list.
        ListedResource listedResource = createListedResource(usersGetResponse, startIndex);
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

    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        JSONEncoder encoder = null;
        JSONDecoder decoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            decoder = getDecoder();

            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = getSchema(userManager);
            //create the search request object
            SearchRequest searchRequest = decoder.decodeSearchRequestBody(resourceString, schema);

            searchRequest.setCount(ResourceManagerUtil.processCount(searchRequest.getCountStr()));
            searchRequest.setStartIndex(ResourceManagerUtil.processStartIndex(searchRequest.getStartIndexStr()));

            //check whether provided sortOrder is valid or not
            if (searchRequest.getSortOder() != null) {
                if (!(searchRequest.getSortOder().equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING)
                        || searchRequest.getSortOder().equalsIgnoreCase(SCIMConstants.OperationalConstants
                        .DESCENDING))) {
                    String error = "Invalid sortOrder value is specified";
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

            //API user should pass a usermanager usermanager to UserResourceEndpoint.
            if (userManager != null) {
                UsersGetResponse usersGetResponse = userManager.listUsersWithPost(searchRequest, requiredAttributes);
                for (User user : usersGetResponse.getUsers()) {
                    //perform service provider side validation.
                    ServerSideValidator.validateRetrievedSCIMObjectInList(user, schema,
                            searchRequest.getAttributesAsString(), searchRequest.getExcludedAttributesAsString());
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(
                        usersGetResponse, searchRequest.getStartIndex());
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
     * To update the user by giving entire attribute set
     *
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @return
     */
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager,
                                      String attributes, String excludeAttributes) {
        //needs to validate the incoming object. eg: id can not be set by the consumer.

        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder.
            decoder = getDecoder();

            SCIMResourceTypeSchema schema = getSchema(userManager);

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());
            User updatedUser = null;
            if (userManager != null) {
                //retrieve the old object
                User oldUser = userManager.getUser(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
                if (oldUser != null) {
                    User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(oldUser, user, schema);
                    updatedUser = userManager.updateUser(validatedUser, requiredAttributes);

                } else {
                    String error = "No user exists with the given id: " + existingId;
                    throw new NotFoundException(error);
                }

            } else {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(updatedUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + updatedUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated User resource is null.";
                throw new CharonException(error);
            }

            //put the uri of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (BadRequestException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /**
     * Update the user resource by sequence of operations.
     *
     * @param existingId
     * @param scimObjectString
     * @param userManager
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

            SCIMResourceTypeSchema schema = getSchema(userManager);
            List<String> allSimpleMultiValuedAttributes = ResourceManagerUtil.getAllSimpleMultiValuedAttributes(schema);

            //get the user from the user core
            User oldUser = userManager.getUser(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
            if (oldUser == null) {
                throw new NotFoundException("No user with the id : " + existingId + " in the user store.");
            }
            //make a copy of the original user
            User copyOfOldUser = (User) CopyUtil.deepCopy(oldUser);
            //make another copy of original user.
            //this will be used to restore to the original condition if failure occurs.
            User originalUser = (User) CopyUtil.deepCopy(copyOfOldUser);

            User newUser = null;

            Map<String, String> syncedAttributes = userManager.getSyncedUserAttributes();
            for (PatchOperation operation : opList) {

                if (operation.getOperation().equals(SCIMConstants.OperationalConstants.ADD)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REMOVE)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchRemove(operation, oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchRemove(operation, newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);
                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REPLACE)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);
                    }
                } else {
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
                }

                List<String> scimAttributes = determineScimAttributes(operation);

                for (String scimAttribute : scimAttributes) {
                    String syncedAttribute = syncedAttributes.get(scimAttribute);

                    if (syncedAttribute == null) {
                        continue;
                    }

                    int lastColonIndex = syncedAttribute.lastIndexOf(':');
                    String baseAttributeName = (lastColonIndex != -1)
                            ? syncedAttribute.substring(0, lastColonIndex)
                            : StringUtils.EMPTY;
                    String subAttributeName = (lastColonIndex != -1)
                            ? syncedAttribute.substring(lastColonIndex + 1)
                            : syncedAttribute;
                    String[] subAttributes = subAttributeName.split("\\.");

                    switch (subAttributes.length) {
                        case 1:
                            newUser.deleteSubAttribute(baseAttributeName, subAttributes[0]);
                            break;
                        case 2:
                            newUser.deleteSubSubAttribute(baseAttributeName, subAttributes[0], subAttributes[1]);
                            break;
                        default:
                            break;
                    }

                    copyOfOldUser = (User) CopyUtil.deepCopy(newUser);
                    syncedAttributes.remove(syncedAttribute);
                }
            }

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                    ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);


            User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject
                    (originalUser, newUser, schema);
            try {
                newUser = userManager.updateUser(validatedUser, requiredAttributes, allSimpleMultiValuedAttributes);
            } catch (NotImplementedException e) {
                newUser = userManager.updateUser(validatedUser, requiredAttributes);
            }

            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (newUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(newUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = getEncoder().encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + newUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated User resource is null.";
                throw new CharonException(error);
            }
            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);
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
            CharonException e1 = new CharonException("Error in performing the patch operation on user resource.", e);
            return AbstractResourceManager.encodeSCIMException(e1);
        }
    }

    /*
     * Creates the Listed Resource.
     *
     * @param usersGetResponse
     * @param startIndex
     * @return
     * @throws CharonException
     * @throws NotFoundException
     */
    protected ListedResource createListedResource(UsersGetResponse usersGetResponse, int startIndex)
            throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(usersGetResponse.getTotalUsers());
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(usersGetResponse.getUsers().size());
        for (User user : usersGetResponse.getUsers()) {
            Map<String, Attribute> userAttributes = user.getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }

    private SCIMResourceTypeSchema getSchema(UserManager userManager) throws BadRequestException,
            NotImplementedException, CharonException {

        SCIMResourceTypeSchema schema;
        if (userManager != null) {
            // Unless configured returns core-user schema or else returns extended user schema.
            schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema(userManager);
        } else {
            // Unless configured returns core-user schema or else returns extended user schema.
            schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        }
        return schema;
    }

    private static List<String> determineScimAttributes(PatchOperation operation) {

        List<String> attributes = new ArrayList<>();
        String path = operation.getPath();
        Object values = operation.getValues();

        if (values instanceof JSONObject) {
            extractScimAttributes((JSONObject) values, path, attributes);
        } else if (values instanceof JSONArray) {
            extractScimAttributes((JSONArray) values, path, attributes);
        } else if (values != null) {
            attributes.add(path);
        }

        return attributes.isEmpty() && path != null ? Collections.singletonList(path) : attributes;
    }

    private static void extractScimAttributes(JSONObject jsonObject, String basePath, List<String> attributes) {

        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = jsonObject.get(key);
            String separator = ".";
            if (defaultScimSchemas().contains(basePath)) {
                separator = ":";
            }
            String newPath = (basePath != null) ? basePath + separator + key : key;

            if (value instanceof JSONObject) {
                extractScimAttributes((JSONObject) value, newPath, attributes);
            } else if (value instanceof JSONArray) {
                extractScimAttributes((JSONArray) value, newPath, attributes);
            } else {
                attributes.add(newPath);
            }
        }
    }

    private static void extractScimAttributes(JSONArray jsonArray, String basePath, List<String> attributes) {

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                extractScimAttributes((JSONObject) value, basePath, attributes);
            } else if (!(value instanceof JSONArray)) {
                attributes.add(basePath);
            }
        }
    }

    private static List<String> defaultScimSchemas() {

        return Arrays.asList(
            SCIMConstants.CORE_SCHEMA_URI,
            SCIMConstants.USER_CORE_SCHEMA_URI,
            SCIMConstants.ENTERPRISE_USER_SCHEMA_URI,
            SCIMConstants.SYSTEM_USER_SCHEMA_URI
        );
    }
}
