/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
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
import org.wso2.charon3.core.objects.Role;
import org.wso2.charon3.core.objects.plainobjects.RolesGetResponse;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.List;
import java.util.Map;

/**
 * OSGi service interface which uses to manage roles.
 */
public interface RoleManager {

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
    Role createRole(Role role) throws CharonException, ConflictException, NotImplementedException, BadRequestException;

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
    Role getRole(String id, Map<String, Boolean> requiredAttributes)
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
     * @param node       Node
     * @param startIndex Start Index
     * @param count      Count
     * @param sortBy     Sort by
     * @param sortOrder  Sort order
     * @return List of roles.
     * @throws CharonException         CharonException.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     */
    RolesGetResponse listRolesWithGET(Node node, Integer startIndex, Integer count, String sortBy, String sortOrder)
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
    Role updateRole(Role oldRole, Role newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException;

    /**
     * List roles with Post.
     *
     * @param searchRequest Search request.
     * @return List of roles.
     * @throws NotImplementedException NotImplementedException.
     * @throws BadRequestException     BadRequestException.
     * @throws CharonException         CharonException.
     */
    RolesGetResponse listRolesWithPost(SearchRequest searchRequest)
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
    default Role patchRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchRole method is not implemented");
    }

    /**
     * Assign/De-assign users from the role.
     *
     * @param oldRole Old role.
     * @param newRole New role.
     * @return Updated role.
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws ConflictException
     * @throws NotFoundException
     */
    default Role updateUsersRole(Role oldRole, Role newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException {

        throw new NotImplementedException("updateUsersRole method is not implemented");
    }

    /**
     * Assign/De-assign users to role via PATCH.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated role.
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws ConflictException
     * @throws NotFoundException
     * @throws ForbiddenException
     */
    default Role patchUsersRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchUsersRole method is not implemented");
    }

    /**
     * Assign/De-assign groups from the role.
     *
     * @param oldRole Old role.
     * @param newRole New role.
     * @return Updated role.
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws ConflictException
     * @throws NotFoundException
     */
    default Role updateGroupsRole(Role oldRole, Role newRole)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException {

        throw new NotImplementedException("updateGroupsRole method is not implemented");
    }

    /**
     * Assign-De-assign groups to role via PATCH.
     *
     * @param roleId          ID of the role.
     * @param patchOperations A map of patch operations.
     * @return Updated role.
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws ConflictException
     * @throws NotFoundException
     * @throws ForbiddenException
     */
    default Role patchGroupsRole(String roleId, Map<String, List<PatchOperation>> patchOperations)
            throws NotImplementedException, BadRequestException, CharonException, ConflictException, NotFoundException,
            ForbiddenException {

        throw new NotImplementedException("patchGroupsRole method is not implemented");
    }
}
