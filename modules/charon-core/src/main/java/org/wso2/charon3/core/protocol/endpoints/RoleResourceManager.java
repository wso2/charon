/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.charon3.core.extensions.RoleManager;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.Role;
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
 * REST API exposed by Charon-Core to perform operations on RoleResource.
 */
public class RoleResourceManager extends AbstractResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(RoleResourceManager.class);

    @Override
    public SCIMResponse getRole(String id, RoleManager roleManager, String attributes, String excludeAttributes) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            Role role = roleManager.getRole(id, requiredAttributes);
            if (role == null) {
                String message = "Role id: " + id + " not found in the system.";
                throw new NotFoundException(message);
            }

            ServerSideValidator.validateRetrievedSCIMObject(role, schema, attributes, excludeAttributes);
            ServerSideValidator.validateRetrievedSCIMRoleObject(role, attributes, excludeAttributes);

            String encodedRole = encoder.encodeSCIMObject(role);
            Map<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedRole, httpHeaders);

        } catch (NotFoundException | BadRequestException | CharonException | NotImplementedException
                | InternalErrorException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse createRole(String postRequest, RoleManager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();

            Role role = decoder.decodeResource(postRequest, schema, new Role());
            ServerSideValidator.validateCreatedSCIMObject(role, SCIMSchemaDefinitions.SCIM_ROLE_SCHEMA);

            Role createdRole = roleManager.createRole(role);
            String encodedRole;
            Map<String, String> httpHeaders = new HashMap<>();
            if (createdRole != null) {
                encodedRole = encoder.encodeSCIMObject(createdRole);
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                        getResourceEndpointURL(SCIMConstants.ROLE_ENDPOINT) + "/" + createdRole.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            } else {
                String message = "Newly created Role resource is null.";
                throw new InternalErrorException(message);
            }
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedRole, httpHeaders);

        } catch (InternalErrorException | BadRequestException | ConflictException | CharonException | NotFoundException
                | NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse deleteRole(String id, RoleManager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            roleManager.deleteRole(id);
            return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);

        } catch (InternalErrorException | CharonException | NotFoundException | NotImplementedException
                | BadRequestException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse listWithGETRole(RoleManager roleManager, String filter, Integer startIndexInt, Integer countInt,
            String sortBy, String sortOrder) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            Integer count = ResourceManagerUtil.processCount(countInt);
            Integer startIndex = ResourceManagerUtil.processStartIndex(startIndexInt);
            sortOrder = resolveSortOrder(sortOrder, sortBy);
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();
            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);
            JSONEncoder encoder = getEncoder();

            List<Object> rolesList = roleManager.listRolesWithGET(rootNode, startIndex, count, sortBy, sortOrder);
            return processRoleList(rolesList, encoder, startIndex);

        } catch (CharonException | InternalErrorException | BadRequestException | NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter.";
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
     * @throws BadRequestException BadRequestException.
     */
    private String resolveSortOrder(String sortOrder, String sortBy) throws BadRequestException {

        // Check whether the provided sortOrder is valid or not.
        if (sortOrder != null) {
            if (!(SCIMConstants.OperationalConstants.ASCENDING.equalsIgnoreCase(sortOrder)
                    || SCIMConstants.OperationalConstants.DESCENDING.equalsIgnoreCase(sortOrder))) {
                String error = "Invalid sortOrder value is specified.";
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
     * @param schema Schema.
     * @return Node.
     * @throws BadRequestException BadRequestException.
     * @throws IOException         IOException.
     */
    private Node buildNode(String filter, SCIMResourceTypeSchema schema) throws BadRequestException, IOException {

        if (filter != null) {
            FilterTreeManager filterTreeManager = new FilterTreeManager(filter, schema);
            return filterTreeManager.buildTree();
        }
        return null;
    }

    /**
     * Method to process a list and return a SCIM response.
     *
     * @param roleList   Filtered role list.
     * @param encoder    Json encoder.
     * @param startIndex Starting index.
     * @return SCIM response.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    private SCIMResponse processRoleList(List<Object> roleList, JSONEncoder encoder, int startIndex)
            throws CharonException, BadRequestException {

        int totalResults = 0;
        if (roleList == null) {
            roleList = Collections.emptyList();
        } else {
            if (roleList.size() >= 1) {
                if (roleList.get(0) instanceof Integer) {
                    totalResults = (int) roleList.get(0);
                    roleList.remove(0);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "First element in the list is not an int. Setting result count as: " + roleList.size());
                    }
                    totalResults = roleList.size();
                }
            }
        }
        for (Object role : roleList) {
            ServerSideValidator
                    .validateSCIMObjectForRequiredAttributes((Role) role, SCIMSchemaDefinitions.SCIM_ROLE_SCHEMA);
        }
        // Create a listed resource object out of the returned groups list.
        ListedResource listedResource = createListedResource(roleList, startIndex, totalResults);
        // Convert the listed resource into specific format.
        String encodedListedResource = encoder.encodeSCIMObject(listedResource);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);
    }

    protected ListedResource createListedResource(List<Object> roles, int startIndex, int totalResults) {

        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(totalResults);
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(roles.size());
        for (Object role : roles) {
            Map<String, Attribute> userAttributes = ((Role) role).getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }

    @Override
    public SCIMResponse listWithPOSTRole(String searchRequest, RoleManager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();

            // Create the search request object.
            SearchRequest searchRequestObject = decoder.decodeSearchRequestBody(searchRequest, schema);
            searchRequestObject.setCount(ResourceManagerUtil.processCount(searchRequestObject.getCountStr()));
            searchRequestObject
                    .setStartIndex(ResourceManagerUtil.processStartIndex(searchRequestObject.getStartIndexStr()));

            if (searchRequestObject.getSchema() != null && !searchRequestObject.getSchema()
                    .equals(SCIMConstants.SEARCH_SCHEMA_URI)) {
                throw new BadRequestException("Provided schema is invalid.", ResponseCodeConstants.INVALID_VALUE);
            }

            // Check whether provided sortOrder is valid or not.
            if (searchRequestObject.getSortOder() != null) {
                if (!(searchRequestObject.getSortOder().equalsIgnoreCase(SCIMConstants.OperationalConstants.ASCENDING)
                        || searchRequestObject.getSortOder()
                        .equalsIgnoreCase(SCIMConstants.OperationalConstants.DESCENDING))) {
                    String error = " Invalid sortOrder value is specified";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }
            //If a value for "sortBy" is provided and no "sortOrder" is specified, "sortOrder" SHALL default to
            // ascending.
            if (searchRequestObject.getSortOder() == null && searchRequestObject.getSortBy() != null) {
                searchRequestObject.setSortOder(SCIMConstants.OperationalConstants.ASCENDING);
            }

            List<Object> rolesList = roleManager.listRolesWithPost(searchRequestObject);
            int totalResults = (int) rolesList.get(0);
            rolesList.remove(0);
            List<Object> returnedRoles = rolesList;

            for (Object role : returnedRoles) {
                ServerSideValidator.validateRetrievedSCIMObjectInList((Role) role, schema,
                        searchRequestObject.getAttributesAsString(),
                        searchRequestObject.getExcludedAttributesAsString());
            }
            // Create a listed resource object out of the returned users list.
            ListedResource listedResource = createListedResource(returnedRoles, searchRequestObject.getStartIndex(),
                    totalResults);
            String encodedListedResource = encoder.encodeSCIMObject(listedResource);
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);

        } catch (CharonException | InternalErrorException | BadRequestException | NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse updateWithPUTRole(String id, String putRequest, RoleManager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();
            Map<String, Boolean> requestAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);
            Role role = decoder.decodeResource(putRequest, schema, new Role());
            Role updatedRole;

            // Retrieve the old object.
            Role oldRole = roleManager.getRole(id, requestAttributes);
            if (oldRole != null) {
                Role newRole = (Role) ServerSideValidator.validateUpdatedSCIMObject(oldRole, role, schema);
                updatedRole = roleManager.updateRole(oldRole, newRole);
            } else {
                String error = "No role exists with the given id: " + id;
                throw new NotFoundException(error);
            }
            return getScimResponse(encoder, updatedRole);

        } catch (NotFoundException | BadRequestException | CharonException | ConflictException | InternalErrorException
                | NotImplementedException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse updateWithPATCHRole(String id, String patchRequest, RoleManager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager handler is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceSchema();
            Map<String, Boolean> requestAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);

            Role oldRole = roleManager.getRole(id, requestAttributes);
            if (oldRole == null) {
                throw new NotFoundException("No role with the id : " + id + " exists in the system.");
            }
            // Make a copy of original group. This will be used to restore to the original condition if failure occurs.
            Role originalRole = (Role) CopyUtil.deepCopy(oldRole);
            Role patchedRole = doPatchRole(oldRole, schema, patchRequest);
            Role updatedRole = roleManager.updateRole(originalRole, patchedRole);
            return getScimResponse(encoder, updatedRole);

        } catch (NotFoundException | BadRequestException | NotImplementedException | CharonException | ConflictException
                | InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException ex = new CharonException("Error in performing the patch operation on role resource.", e);
            return AbstractResourceManager.encodeSCIMException(ex);
        }
    }

    private SCIMResponse getScimResponse(JSONEncoder encoder, Role updatedRole)
            throws CharonException, NotFoundException, InternalErrorException {

        String encodedRole;
        Map<String, String> httpHeaders = new HashMap<>();
        if (updatedRole != null) {
            // Create a deep copy of the user object since we are going to change it.
            Role copiedRole = (Role) CopyUtil.deepCopy(updatedRole);
            encodedRole = encoder.encodeSCIMObject(copiedRole);
            // Add location header
            httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                    getResourceEndpointURL(SCIMConstants.ROLE_ENDPOINT) + "/" + updatedRole.getId());
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        } else {
            String error = "Updated Role resource is null.";
            throw new InternalErrorException(error);
        }
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedRole, httpHeaders);
    }

    private Role doPatchRole(Role oldRole, SCIMResourceTypeSchema roleSchema, String patchRequest)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        // Make a copy of the original group.
        Role originalRole = (Role) CopyUtil.deepCopy(oldRole);
        Role copyOfOldRole = (Role) CopyUtil.deepCopy(oldRole);

        Role patchedRole = null;
        List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);
        for (PatchOperation operation : opList) {
            switch (operation.getOperation()) {
            case SCIMConstants.OperationalConstants.ADD:
                if (patchedRole == null) {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchAdd(operation, getDecoder(), oldRole, copyOfOldRole, roleSchema);
                } else {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchAdd(operation, getDecoder(), patchedRole, copyOfOldRole, roleSchema);

                }
                copyOfOldRole = (Role) CopyUtil.deepCopy(patchedRole);
                break;
            case SCIMConstants.OperationalConstants.REMOVE:
                if (patchedRole == null) {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchRemove(operation, oldRole, copyOfOldRole, roleSchema);

                } else {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchRemove(operation, patchedRole, copyOfOldRole, roleSchema);
                }
                copyOfOldRole = (Role) CopyUtil.deepCopy(patchedRole);
                break;
            case SCIMConstants.OperationalConstants.REPLACE:
                if (patchedRole == null) {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchReplace(operation, getDecoder(), oldRole, copyOfOldRole, roleSchema);

                } else {
                    patchedRole = (Role) PatchOperationUtil
                            .doPatchReplace(operation, getDecoder(), patchedRole, copyOfOldRole, roleSchema);
                }
                copyOfOldRole = (Role) CopyUtil.deepCopy(patchedRole);
                break;
            default:
                throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
            }
        }
        return (Role) ServerSideValidator.validateUpdatedSCIMObject(originalRole, patchedRole, roleSchema);
    }

    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes,
            String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
            String sortOrder, String domainName, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, Integer startIndexInt, Integer countInt,
            String sortBy, String sortOrder, String domainName, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager,
            String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager,
            String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String patchRequest, UserManager userManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }
}
