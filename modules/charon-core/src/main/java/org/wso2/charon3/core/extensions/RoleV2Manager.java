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

package org.wso2.charon3.core.extensions;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.objects.plainobjects.RolesV2GetResponse;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.List;
import java.util.Map;

/**
 * OSGi service interface which uses to manage roleV2.
 */
public interface RoleV2Manager {

    /**
     * Create a role.
     *
     * @param role Role Object.
     * @return Role.
     * @throws CharonException         CharonException.
     * @throws ConflictException       ConflictException.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     */
    RoleV2 createRole(RoleV2 role)
            throws CharonException, ConflictException, NotImplementedException, BadRequestException;

    /**
     * Get the role for the given ID.
     *
     * @param id                 Role ID.
     * @param requiredAttributes Required Attributes.
     * @return Role.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     * @throws CharonException         CharonException.
     * @throws NotFoundException       NotFoundException.
     */
    RoleV2 getRole(String id, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException;

    /**
     * Delete the given role.
     *
     * @param id Role ID.
     * @throws NotFoundException       NotFoundException.
     * @throws CharonException         CharonException.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     */
    void deleteRole(String id) throws NotFoundException, CharonException, NotImplementedException, BadRequestException;

    /**
     * List roles with Get.
     *
     * @param node               Node
     * @param startIndex         Start Index
     * @param count              Count
     * @param sortBy             Sort by
     * @param sortOrder          Sort order
     * @param requiredAttributes Required attributes
     * @return List of roles.
     * @throws CharonException         CharonException.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     */
    RolesV2GetResponse listRolesWithGET(Node node, Integer startIndex, Integer count, String sortBy, String sortOrder,
                                        List<String> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException;

    /**
     * Update the role.
     *
     * @param oldRole Old role.
     * @param newRole new role.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     * @throws CharonException         CharonException.
     * @throws ConflictException       ConflictException.
     * @throws NotFoundException       NotFoundException.
     */
    RoleV2 updateRole(RoleV2 oldRole, RoleV2 newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException;

    /**
     * List roles with Post.
     *
     * @param searchRequest      Search request.
     * @param requiredAttributes Required attributes.
     * @return List of roles.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     * @throws CharonException         CharonException.
     */
    RolesV2GetResponse listRolesWithPost(SearchRequest searchRequest, List<String> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException;

    /**
     * Updates the role via PATCH.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated group.
     * @throws CharonException         Charon exception.
     * @throws BadRequestException     Bad request exception.
     * @throws NotFoundException       Not found exception.
     * @throws NotImplementedException Functionality no implemented exception.
     */
    RoleV2 patchRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException;

    /**
     * Create a role with only metadata information.
     * Users and Groups will not be assigned during the role creation.
     *
     * @param role Role Object.
     * @return Role.
     * @throws CharonException         CharonException.
     * @throws ConflictException       ConflictException.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     */
    default RoleV2 createRoleMeta(RoleV2 role)
            throws CharonException, ConflictException, NotImplementedException, BadRequestException {

        throw new NotImplementedException("createRoleMeta method is not implemented.");
    }

    /**
     * Update the role metadata information.
     * This method will not assign or de-assign users or groups to the role.
     *
     * @param oldRole Old role.
     * @param newRole new role.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     * @throws CharonException         CharonException.
     * @throws ConflictException       ConflictException.
     * @throws NotFoundException       NotFoundException.
     */
    default RoleV2 updateRoleMeta(RoleV2 oldRole, RoleV2 newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException {

        throw new NotImplementedException("updateRoleMeta method is not implemented.");
    }

    /**
     * Updates the role metadata via PATCH.
     * This method will not assign or de-assign users or groups to the role.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated group.
     * @throws CharonException         Charon exception.
     * @throws BadRequestException     Bad request exception.
     * @throws NotFoundException       Not found exception.
     * @throws NotImplementedException Functionality no implemented exception.
     */
    default RoleV2 patchRoleMeta(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchRoleMeta method is not implemented.");
    }

    /**
     * Assign/De-assign users from the role.
     *
     * @param oldRole Old role.
     * @param newRole New role.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException
     * @throws BadRequestException     BadRequestException
     * @throws CharonException         CharonException
     * @throws ConflictException       ConflictException
     * @throws NotFoundException       NotFoundException
     */
    default RoleV2 updateUsersOfRole(RoleV2 oldRole, RoleV2 newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException {

        throw new NotImplementedException("updateUsersRole method is not implemented.");
    }

    /**
     * Assign/De-assign users to role via PATCH.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException
     * @throws BadRequestException     BadRequestException
     * @throws CharonException         CharonException
     * @throws ConflictException       ConflictException
     * @throws NotFoundException       NotFoundException
     * @throws ForbiddenException      ForbiddenException
     */
    default RoleV2 patchUsersOfRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchUsersRole method is not implemented.");
    }

    /**
     * Assign/De-assign groups from the role.
     *
     * @param oldRole Old role.
     * @param newRole New role.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException
     * @throws BadRequestException     BadRequestException
     * @throws CharonException         CharonException
     * @throws ConflictException       ConflictException
     * @throws NotFoundException       NotFoundException
     */
    default RoleV2 updateGroupsOfRole(RoleV2 oldRole, RoleV2 newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException {

        throw new NotImplementedException("updateGroupsOfRole method is not implemented.");
    }

    /**
     * Assign-De-assign groups to role via PATCH.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated role.
     * @throws NotImplementedException NotImplementedException
     * @throws BadRequestException     BadRequestException
     * @throws CharonException         CharonException
     * @throws ConflictException       ConflictException
     * @throws NotFoundException       NotFoundException
     * @throws ForbiddenException      ForbiddenException
     */
    default RoleV2 patchGroupsOfRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchGroupsRole method is not implemented.");
    }
}
