/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.protocol.endpoints;

// TODO check the comment. possible to move this out from charon, since it's an extended resource

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.objects.plainobjects.RolesV2GetResponse;
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
import java.util.List;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on RoleResourceV2.
 */
public class RoleResourceV2Manager extends AbstractResourceManager {

    private static final Logger LOG = LoggerFactory.getLogger(RoleResourceV2Manager.class);

    @Override
    public SCIMResponse getRole(String id, RoleV2Manager roleManager, String attributes, String excludeAttributes) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), attributes,
                            excludeAttributes);

            RoleV2 role = roleManager.getRole(id, requiredAttributes);
            if (role == null) {
                String message = "Role id: " + id + " not found in the system.";
                throw new NotFoundException(message);
            }
            ServerSideValidator.validateRetrievedSCIMObject(role, schema, attributes, excludeAttributes);
            // TODO
//            ServerSideValidator.validateRetrievedSCIMRoleObject(role, attributes, excludeAttributes);
            String encodedRole = encoder.encodeSCIMObject(role);
            Map<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedRole, httpHeaders);
        } catch (NotFoundException | BadRequestException | CharonException | NotImplementedException |
                 InternalErrorException e) {
            return encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse createRole(String postRequest, RoleV2Manager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();

            RoleV2 role = decoder.decodeResource(postRequest, schema, new RoleV2());
            ServerSideValidator.validateCreatedSCIMObject(role, SCIMSchemaDefinitions.SCIM_ROLE_V2_SCHEMA);

            RoleV2 createdRole = roleManager.createRole(role);
            String encodedRole;
            Map<String, String> httpHeaders = new HashMap<>();
            if (createdRole != null) {
                encodedRole = encoder.encodeSCIMObject(createdRole);
                httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                        getResourceEndpointURL(SCIMConstants.ROLE_V2_ENDPOINT) + "/" + createdRole.getId());
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
    public SCIMResponse deleteRole(String id, RoleV2Manager roleManager) {

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
    public SCIMResponse listWithGETRole(RoleV2Manager roleManager, String filter, Integer startIndexInt,
                                        Integer countInt, String sortBy, String sortOrder) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            Integer count = ResourceManagerUtil.processCount(countInt);
            Integer startIndex = ResourceManagerUtil.processStartIndex(startIndexInt);
            sortOrder = resolveSortOrder(sortOrder, sortBy);
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
            // Build node for filtering.
            Node rootNode = buildNode(filter, schema);
            JSONEncoder encoder = getEncoder();

            RolesV2GetResponse rolesResponse = roleManager.listRolesWithGET(rootNode, startIndex, count, sortBy,
                    sortOrder);
            return processRoleList(rolesResponse, encoder, startIndex);
        } catch (CharonException | InternalErrorException | BadRequestException | NotImplementedException e) {
            return encodeSCIMException(e);
        } catch (IOException e) {
            String error = "Error in tokenization of the input filter.";
            CharonException charonException = new CharonException(error);
            return AbstractResourceManager.encodeSCIMException(charonException);
        }
    }

    @Override
    public SCIMResponse listWithPOSTRole(String searchRequest, RoleV2Manager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();

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

            RolesV2GetResponse rolesResponse = roleManager.listRolesWithPost(searchRequestObject);

            for (RoleV2 role : rolesResponse.getRoles()) {
                ServerSideValidator.validateRetrievedSCIMObjectInList(role, schema,
                        searchRequestObject.getAttributesAsString(),
                        searchRequestObject.getExcludedAttributesAsString());
            }
            // Create a listed resource object out of the returned users list.
            ListedResource listedResource = createListedResource(rolesResponse, searchRequestObject.getStartIndex());
            String encodedListedResource = encoder.encodeSCIMObject(listedResource);
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);
        } catch (CharonException | InternalErrorException | BadRequestException | NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    @Override
    public SCIMResponse updateWithPUTRole(String id, String putRequest, RoleV2Manager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            JSONDecoder decoder = getDecoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
            Map<String, Boolean> requestAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);
            RoleV2 role = decoder.decodeResource(putRequest, schema, new RoleV2());
            RoleV2 updatedRole;

            // Retrieve the old object.
            RoleV2 oldRole = roleManager.getRole(id, requestAttributes);
            if (oldRole != null) {
                RoleV2 newRole = (RoleV2) ServerSideValidator.validateUpdatedSCIMObject(oldRole, role, schema);
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
    public SCIMResponse updateWithPATCHRole(String id, String patchRequest, RoleV2Manager roleManager) {

        try {
            if (roleManager == null) {
                String error = "Provided role manager handler is null.";
                throw new InternalErrorException(error);
            }
            JSONEncoder encoder = getEncoder();
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
            Map<String, Boolean> requestAttributes = ResourceManagerUtil.getAllAttributeURIs(schema);

            List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);

            if (!isUpdateAllUsersOperationFound(opList)) {
                return updateWithPatchOperations(id, opList, roleManager, schema, encoder);
            }

            RoleV2 oldRole = roleManager.getRole(id, requestAttributes);
            if (oldRole == null) {
                throw new NotFoundException("No role with the id : " + id + " exists in the system.");
            }
            // Make a copy of original role. This will be used to restore to the original condition if failure occurs.
            RoleV2 originalRole = (RoleV2) CopyUtil.deepCopy(oldRole);
            RoleV2 patchedRole = doPatchRole(oldRole, schema, patchRequest);
            RoleV2 updatedRole = roleManager.updateRole(originalRole, patchedRole);
            return getScimResponse(encoder, updatedRole);
        } catch (NotFoundException | BadRequestException | NotImplementedException | CharonException | ConflictException
                 | InternalErrorException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException ex = new CharonException("Error in performing the patch operation on role resource.", e);
            return AbstractResourceManager.encodeSCIMException(ex);
        }
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
     * @param rolesResponse Response made of the filtered role list and total number of users.
     * @param encoder       Json encoder.
     * @param startIndex    Starting index.
     * @return SCIM response.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    private SCIMResponse processRoleList(RolesV2GetResponse rolesResponse, JSONEncoder encoder, int startIndex)
            throws CharonException, BadRequestException {

        if (rolesResponse == null) {
            rolesResponse = new RolesV2GetResponse(0, Collections.emptyList());
        }
        if (rolesResponse.getRoles() == null) {
            rolesResponse.setRoles(Collections.emptyList());
        }
        for (RoleV2 role : rolesResponse.getRoles()) {
            ServerSideValidator.validateSCIMObjectForRequiredAttributes(role,
                    SCIMSchemaDefinitions.SCIM_ROLE_V2_SCHEMA);
        }
        // Create a listed resource object out of the returned groups list.
        ListedResource listedResource = createListedResource(rolesResponse, startIndex);
        // Convert the listed resource into specific format.
        String encodedListedResource = encoder.encodeSCIMObject(listedResource);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, responseHeaders);
    }

    protected ListedResource createListedResource(RolesV2GetResponse rolesResponse, int startIndex) {

        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(rolesResponse.getTotalRoles());
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(rolesResponse.getRoles().size());
        for (RoleV2 role : rolesResponse.getRoles()) {
            Map<String, Attribute> userAttributes = role.getAttributeList();
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }

    private SCIMResponse getScimResponse(JSONEncoder encoder, RoleV2 updatedRole)
            throws CharonException, NotFoundException, InternalErrorException {

        String encodedRole;
        Map<String, String> httpHeaders = new HashMap<>();
        if (updatedRole != null) {
            // Create a deep copy of the user object since we are going to change it.
            RoleV2 copiedRole = (RoleV2) CopyUtil.deepCopy(updatedRole);
            encodedRole = encoder.encodeSCIMObject(copiedRole);
            // Add location header
            httpHeaders.put(SCIMConstants.LOCATION_HEADER,
                    getResourceEndpointURL(SCIMConstants.ROLE_V2_ENDPOINT) + "/" + updatedRole.getId());
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        } else {
            String error = "Updated Role resource is null.";
            throw new InternalErrorException(error);
        }
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedRole, httpHeaders);
    }

    /**
     * Check whether it is required to update all users with the patch operation.
     *
     * @param patchOperations Patch operation.
     * @return Whether it is required to update all users or not with the patch operation.
     * @throws JSONException JSONException
     */
    private boolean isUpdateAllUsersOperationFound(List<PatchOperation> patchOperations) throws JSONException {

        for (PatchOperation patchOperation : patchOperations) {
            String operation = patchOperation.getOperation();
            String path = patchOperation.getPath();
            JSONObject valuesJson = null;
            if (StringUtils.isBlank(path)) {
                valuesJson = (JSONObject) patchOperation.getValues();
            }
            if (SCIMConstants.OperationalConstants.REPLACE.equals(operation) &&
                    (SCIMConstants.RoleSchemaConstants.USERS.equals(path) ||
                            (valuesJson != null && valuesJson.has(SCIMConstants.RoleSchemaConstants.USERS)))) {
                return true;
            } else if (SCIMConstants.OperationalConstants.REMOVE.equals(operation) &&
                    (SCIMConstants.RoleSchemaConstants.USERS).equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the role based on the operations defined in the patch request. The updated role information is sent
     * back in the response.
     *
     * @param existingRoleId SCIM2 ID of the existing role.
     * @param opList         List of patch operations.
     * @param roleManager    Role Manager.
     * @param schema         SCIM resource schema.
     * @return SCIM Response.
     */
    private SCIMResponse updateWithPatchOperations(String existingRoleId, List<PatchOperation> opList,
                                                   RoleV2Manager roleManager, SCIMResourceTypeSchema schema,
                                                   JSONEncoder encoder) {

        try {
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

            // Process the Role patch operation and update the patch operation object with required values.
            processRolePatchOperations(patchOperations, schema);
            RoleV2 updatedRole = roleManager.patchRole(existingRoleId, patchOperations);
            return getScimResponse(encoder, updatedRole);
        } catch (NotFoundException | BadRequestException | NotImplementedException | ConflictException |
                 CharonException | InternalErrorException | ForbiddenException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /**
     * Process the Role patch operation and update the patch operation object with required values.
     *
     * @param patchOperations Patch operation.
     * @param schema          SCIM Resource Type Schema.
     * @throws CharonException         CharonException.
     * @throws BadRequestException     BadRequestException.
     * @throws NotImplementedException NotImplementedException.
     * @throws JSONException           JSONException.
     */
    private void processRolePatchOperations(Map<String, List<PatchOperation>> patchOperations,
                                            SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, JSONException {

        for (PatchOperation patchOperation : patchOperations.get(SCIMConstants.OperationalConstants.REPLACE)) {
            processPatchOperation(schema, patchOperation);
        }

        for (PatchOperation patchOperation : patchOperations.get(SCIMConstants.OperationalConstants.ADD)) {
            processPatchOperation(schema, patchOperation);
        }

        for (PatchOperation patchOperation : patchOperations.get(SCIMConstants.OperationalConstants.REMOVE)) {
            processRemovePatchOperation(patchOperation);
        }
    }

    private RoleV2 doPatchRole(RoleV2 oldRole, SCIMResourceTypeSchema roleSchema, String patchRequest)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        // Make a copy of the original group.
        RoleV2 originalRole = (RoleV2) CopyUtil.deepCopy(oldRole);
        RoleV2 copyOfOldRole = (RoleV2) CopyUtil.deepCopy(oldRole);

        RoleV2 patchedRole = null;
        List<PatchOperation> opList = getDecoder().decodeRequest(patchRequest);
        for (PatchOperation operation : opList) {
            switch (operation.getOperation()) {
                case SCIMConstants.OperationalConstants.ADD:
                    if (patchedRole == null) {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchAdd(operation, getDecoder(), oldRole, copyOfOldRole, roleSchema);
                    } else {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchAdd(operation, getDecoder(), patchedRole, copyOfOldRole, roleSchema);

                    }
                    copyOfOldRole = (RoleV2) CopyUtil.deepCopy(patchedRole);
                    break;
                case SCIMConstants.OperationalConstants.REMOVE:
                    if (patchedRole == null) {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchRemove(operation, oldRole, copyOfOldRole, roleSchema);

                    } else {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchRemove(operation, patchedRole, copyOfOldRole, roleSchema);
                    }
                    copyOfOldRole = (RoleV2) CopyUtil.deepCopy(patchedRole);
                    break;
                case SCIMConstants.OperationalConstants.REPLACE:
                    if (patchedRole == null) {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchReplace(operation, getDecoder(), oldRole, copyOfOldRole, roleSchema);

                    } else {
                        patchedRole = (RoleV2) PatchOperationUtil
                                .doPatchReplace(operation, getDecoder(), patchedRole, copyOfOldRole, roleSchema);
                    }
                    copyOfOldRole = (RoleV2) CopyUtil.deepCopy(patchedRole);
                    break;
                default:
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
            }
        }
        return (RoleV2) ServerSideValidator.validateUpdatedSCIMObject(originalRole, patchedRole, roleSchema);
    }

    private void processPatchOperation(SCIMResourceTypeSchema schema, PatchOperation patchOperation)
            throws BadRequestException, CharonException {

        if (patchOperation.getValues() == null) {
            throw new BadRequestException("The value is not provided to perform patch " +
                    patchOperation.getOperation() + " operation.", ResponseCodeConstants.INVALID_SYNTAX);
        }

        if (patchOperation.getPath() != null) {
            switch (patchOperation.getPath()) {
                case SCIMConstants.RoleSchemaConstants.DISPLAY_NAME: {
                    String valuesProperty = (String) patchOperation.getValues();
                    JSONObject attributePrefixedJson = new JSONObject();

                    attributePrefixedJson.put(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME, valuesProperty);
                    patchOperation.setValues(attributePrefixedJson);
                    break;
                }
                case SCIMConstants.RoleSchemaConstants.USERS: {
                    setPatchOperationValue(patchOperation, SCIMConstants.RoleSchemaConstants.USERS);
                    break;
                }
                case SCIMConstants.RoleSchemaConstants.GROUPS: {
                    setPatchOperationValue(patchOperation, SCIMConstants.RoleSchemaConstants.GROUPS);
                    break;
                }
                case SCIMConstants.RoleSchemaConstants.PERMISSIONS: {
                    setPatchOperationValue(patchOperation, SCIMConstants.RoleSchemaConstants.PERMISSIONS);
                    break;
                }
                default:
                    throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
            }
            patchOperation.setPath(null);
        }
        processValueAttributeOfOperation(schema, patchOperation);
    }

    private static void processRemovePatchOperation(PatchOperation patchOperation)
            throws NotImplementedException, BadRequestException {

        if (SCIMConstants.RoleSchemaConstants.DISPLAY_NAME.equalsIgnoreCase(patchOperation.getPath())) {
            throw new BadRequestException("Can not remove a required attribute");
        }

        if (patchOperation.getPath() == null) {
            throw new BadRequestException("No path value specified for remove operation",
                    ResponseCodeConstants.NO_TARGET);
        }

        String path = patchOperation.getPath();
        // Split the path to extract the filter if present.
        String[] parts = path.split("[\\[\\]]");

        if (ArrayUtils.isEmpty(parts) || !(SCIMConstants.RoleSchemaConstants.USERS.equalsIgnoreCase(parts[0]) ||
                SCIMConstants.RoleSchemaConstants.GROUPS.equalsIgnoreCase(parts[0]) ||
                SCIMConstants.RoleSchemaConstants.PERMISSIONS.equalsIgnoreCase(parts[0]))) {
            throw new BadRequestException(parts[0] + " is not a valid attribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }

        if (SCIMConstants.RoleSchemaConstants.USERS.equalsIgnoreCase(parts[0])) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.USERS);
        } else if (SCIMConstants.RoleSchemaConstants.GROUPS.equalsIgnoreCase(parts[0])) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.GROUPS);
        } else {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.PERMISSIONS);
        }

        if (parts.length != 1) {
            Map<String, String> patchObject = new HashMap<>();
            patchObject.put(SCIMConstants.RoleSchemaConstants.DISPLAY, null);
            patchObject.put(SCIMConstants.CommonSchemaConstants.VALUE, null);

            // Currently we only support simple filters here.
            String[] filterParts = parts[1].split(" ");

            if (filterParts.length != 3 || !patchObject.containsKey(filterParts[0])) {
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
            patchObject.put(filterParts[0], filterParts[2]);
            patchOperation.setValues(patchObject);
        }
    }

    private static void setPatchOperationValue(PatchOperation patchOperation, String permissions) {

        JSONArray valuesPropertyJson = (JSONArray) patchOperation.getValues();
        JSONObject attributePrefixedJson = new JSONObject();
        attributePrefixedJson.put(permissions, valuesPropertyJson);
        patchOperation.setValues(attributePrefixedJson);
    }

    private void processValueAttributeOfOperation(SCIMResourceTypeSchema schema, PatchOperation patchOperation)
            throws CharonException, BadRequestException {

        AbstractSCIMObject attributeHoldingSCIMObject = getDecoder().decode(patchOperation.getValues().toString(),
                schema);
        if (attributeHoldingSCIMObject == null) {
            throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
        }

        Map<String, Attribute> attributeList = attributeHoldingSCIMObject.getAttributeList();

        if (attributeList.containsKey(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME)) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME);
            patchOperation.setValues(
                    ((SimpleAttribute) attributeList.get(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME))
                            .getStringValue());
        } else if (attributeList.containsKey(SCIMConstants.RoleSchemaConstants.USERS)) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.USERS);
            patchOperation.setValues(transformAttributeToMap((MultiValuedAttribute) attributeList
                    .get(SCIMConstants.RoleSchemaConstants.USERS)));
        } else if (attributeList.containsKey(SCIMConstants.RoleSchemaConstants.GROUPS)) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.GROUPS);
            patchOperation.setValues(transformAttributeToMap((MultiValuedAttribute) attributeList
                    .get(SCIMConstants.RoleSchemaConstants.GROUPS)));
        } else if (attributeList.containsKey(SCIMConstants.RoleSchemaConstants.PERMISSIONS)) {
            patchOperation.setAttributeName(SCIMConstants.RoleSchemaConstants.PERMISSIONS);
            patchOperation.setValues((transformAttributeToMap((MultiValuedAttribute) attributeList.
                    get(SCIMConstants.RoleSchemaConstants.PERMISSIONS))));
        }
    }

    private List<Map<String, String>> transformAttributeToMap(MultiValuedAttribute multiValuedAttribute)
            throws CharonException {

        List<Map<String, String>> memberList = new ArrayList<>();
        List<Attribute> subValuesList = multiValuedAttribute.getAttributeValues();
        for (Attribute subValue : subValuesList) {
            ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
            Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();

            Map<String, String> member = new HashMap<>();
            if (subAttributesList.get(SCIMConstants.CommonSchemaConstants.VALUE) != null) {
                member.put(SCIMConstants.CommonSchemaConstants.VALUE, ((SimpleAttribute)
                        (subAttributesList.get(SCIMConstants.CommonSchemaConstants.VALUE))).getStringValue());
            }

            if (subAttributesList.get(SCIMConstants.CommonSchemaConstants.DISPLAY) != null) {
                member.put(SCIMConstants.CommonSchemaConstants.DISPLAY, ((SimpleAttribute)
                        (subAttributesList.get(SCIMConstants.CommonSchemaConstants.DISPLAY))).getStringValue());
            }
            memberList.add(member);
        }
        return memberList;
    }
}
