/*
 * Copyright (c) 2016-2023, WSO2 LLC. (http://www.wso2.com).
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

import org.wso2.charon3.core.extensions.RoleManager;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;

import java.util.Collections;

/**
 * Interface for SCIM resource endpoints.
 */
public interface ResourceManager {

    /*
     * Method of resource endpoint which is mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param usermanager
     * @return SCIMResponse
     */
    SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes);

    /*
     * Method of resource endpoint which is mapped to HTTP POST request.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param usermanager
     * @return SCIMResponse -
     * From Spec: {Since the server is free to alter and/or ignore POSTed content,
     * returning the full representation can be useful to the client, enabling it to correlate the
     * client and server views of the new Resource. When a Resource is created, its uri must be returned
     * in the response Location header.}
     */
    SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String excludeAttributes);

    /*
     * Method of the ResourceManager that is mapped to HTTP Delete method..
     *
     * @param id
     * @param usermanager
     * @return
     */
    SCIMResponse delete(String id, UserManager userManager);

    /**
     * This method is deprecated
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
     * @since 1.2.21
     * @deprecated Method does not differenticate when the count paramter is not set in the request. Use
     * {@link org.wso2.charon3.core.protocol.endpoints.ResourceManager#listWithGET(UserManager, String, Integer,
     * Integer, String, String, String, String, String)}
     */
    @Deprecated
    SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
            String sortOrder, String domainName, String attributes, String excludeAttributes);

    /*
     * Get resources
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
    default SCIMResponse listWithGET(UserManager userManager, String filter, Integer startIndexInt, Integer countInt,
            String sortBy, String sortOrder, String domainName, String attributes, String excludeAttributes) {

        return null;
    }

    /*
     * query resources
     *
     * @param resourceString
     * @param usermanager
     * @return
     */
    SCIMResponse listWithPOST(String resourceString, UserManager userManager);

    /*
     * To update the user by giving entire attribute set
     *
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String attributes,
            String excludeAttributes);

    /*
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @return
     */
    SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String attributes,
            String excludeAttributes);


    /*
     * Partially updates a resource. This method does not return the updated resource in the response.
     *
     * @param existingId
     * @param scimObjectString
     * @param usermanager
     * @return
     */
    default SCIMResponse updateWithPATCH(String existingId, String patchRequest, UserManager userManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED,
                ResponseCodeConstants.DESC_NOT_IMPLEMENTED, Collections.emptyMap());
    }

    /**
     * GET method to retrieve a specific role.
     *
     * @param id                Resource id.
     * @param roleManager       Role manager.
     * @param attributes        Attributes in the response.
     * @param excludeAttributes Exclude attributes in the response.
     * @return SCIMResponse.
     */
    default SCIMResponse getRole(String id, RoleManager roleManager, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * POST method to create a new role.
     *
     * @param postRequest Post request.
     * @param roleManager Role manager.
     * @return SCIMResponse.
     */
    default SCIMResponse createRole(String postRequest, RoleManager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * DELETE method to delete a specific role.
     *
     * @param id          Resource id.
     * @param roleManager Role manager.
     * @return SCIMResponse.
     */
    default SCIMResponse deleteRole(String id, RoleManager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * GET method to list roles.
     *
     * @param roleManager Role manager.
     * @param filter      Filter to be executed.
     * @param startIndex  Starting index value of the filter.
     * @param count       Number of required results.
     * @param sortBy      SortBy.
     * @param sortOrder   Sorting order.
     * @return SCIMResponse.
     */
    default SCIMResponse listWithGETRole(RoleManager roleManager, String filter, Integer startIndex, Integer count,
            String sortBy, String sortOrder) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * POST method to search roles.
     *
     * @param searchRequest Search request.
     * @param roleManager   Role manager.
     * @return SCIMResponse.
     */
    default SCIMResponse listWithPOSTRole(String searchRequest, RoleManager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PUT method To update a role by giving entire attributes set.
     *
     * @param id          Resource id.
     * @param putRequest  Put request.
     * @param roleManager Role manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPUTRole(String id, String putRequest, RoleManager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PATCH method to partially updates a role.
     *
     * @param id           Resource id.
     * @param patchRequest Patch request.
     * @param roleManager  Role manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPATCHRole(String id, String patchRequest, RoleManager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    // RoleV2 resource management operations.

    /**
     * GET method to retrieve a specific role in roleV2 model.
     *
     * @param id                Resource id.
     * @param roleManager       RoleV2 manager.
     * @param attributes        Attributes in the response.
     * @param excludeAttributes Exclude attributes in the response.
     * @return SCIMResponse.
     */
    default SCIMResponse getRole(String id, RoleV2Manager roleManager, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * POST method to create a new role in roleV2 model.
     *
     * @param postRequest Post request.
     * @param roleManager RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse createRole(String postRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * DELETE method to delete a specific role in roleV2 model.
     *
     * @param id          Resource id.
     * @param roleManager RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse deleteRole(String id, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * GET method to list roles in roleV2 model.
     *
     * @param roleManager       RoleV2 manager.
     * @param filter            Filter to be executed.
     * @param startIndex        Starting index value of the filter.
     * @param count             Number of required results.
     * @param sortBy            SortBy.
     * @param sortOrder         Sorting order.
     * @param attributes        Requested attributes.
     * @param excludeAttributes Requested exclude attributes.
     * @return SCIMResponse.
     */
    default SCIMResponse listWithGETRole(RoleV2Manager roleManager, String filter, Integer startIndex, Integer count,
                                         String sortBy, String sortOrder, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * POST method to search roles in roleV2 model.
     *
     * @param searchRequest Search request.
     * @param roleManager   RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse listWithPOSTRole(String searchRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PUT method To update a role by giving entire attributes set.
     *
     * @param id          Resource id.
     * @param putRequest  Put request.
     * @param roleManager RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPUTRole(String id, String putRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PATCH method to partially updates a role.
     *
     * @param id           Resource id.
     * @param patchRequest Patch request.
     * @param roleManager  RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPATCHRole(String id, String patchRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * GET method to list roles in roleV3 API.
     *
     * @param roleManager       RoleV2 manager.
     * @param filter            Filter to be executed.
     * @param startIndex        Starting index value of the filter.
     * @param count             Number of required results.
     * @param sortBy            SortBy.
     * @param sortOrder         Sorting order.
     * @param attributes        Requested attributes.
     * @param excludeAttributes Requested exclude attributes.
     * @return SCIMResponse.
     */
    default SCIMResponse listWithGETRoleV3(RoleV2Manager roleManager, String filter, Integer startIndex, Integer count,
                                         String sortBy, String sortOrder, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * GET method to retrieve a specific role in roleV3 API.
     *
     * @param id                Resource id.
     * @param roleManager       RoleV2 manager.
     * @param attributes        Attributes in the response.
     * @param excludeAttributes Exclude attributes in the response.
     * @return SCIMResponse.
     */
    default SCIMResponse getRoleV3(String id, RoleV2Manager roleManager, String attributes, String excludeAttributes) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * POST method to create a new role in roleV2 model.
     *
     * @param postRequest Post request.
     * @param roleManager RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse createRoleMeta(String postRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PUT method To update a role by giving entire attributes set.
     *
     * @param id          Resource id.
     * @param putRequest  Put request.
     * @param roleManager RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPUTRoleMeta(String id, String putRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PATCH method to partially updates a role.
     *
     * @param id           Resource id.
     * @param patchRequest Patch request.
     * @param roleManager  RoleV2 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateWithPATCHRoleMeta(String id, String patchRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PUT method to update a Role by giving the User list.
     *
     * @param id          Resource id.
     * @param putRequest  Put request.
     * @param roleManager RoleV3 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateUsersWithPUTRole(String id, String putRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PATCH method to update a user with roleV2 model.
     *
     * @param id           Resource id.
     * @param patchRequest Patch request.
     * @param roleManager  RoleV3 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateUsersWithPATCHRole(String id, String patchRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PUT method tp update a Role by giving the Group list.
     *
     * @param id          Resource id.
     * @param putRequest  Put request.
     * @param roleManager RoleV3 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateGroupsWithPUTRole(String id, String putRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }

    /**
     * PATCH method to update a groups with roleV3 model.
     *
     * @param id           Resource id.
     * @param patchRequest Patch request.
     * @param roleManager  RoleV3 manager.
     * @return SCIMResponse.
     */
    default SCIMResponse updateGroupsWithPATCHRole(String id, String patchRequest, RoleV2Manager roleManager) {

        return new SCIMResponse(ResponseCodeConstants.CODE_NOT_IMPLEMENTED, ResponseCodeConstants.DESC_NOT_IMPLEMENTED,
                Collections.emptyMap());
    }
}
