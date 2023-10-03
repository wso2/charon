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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.Collections;
import java.util.HashMap;
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
    public SCIMResponse deleteRole(String id, RoleV2Manager roleManager) {

        return null;
    }

    @Override
    public SCIMResponse listWithGETRole(RoleV2Manager roleManager, String filter, Integer startIndexInt,
                                        Integer countInt, String sortBy, String sortOrder) {

        return null;
    }

    @Override
    public SCIMResponse listWithPOSTRole(String searchRequest, RoleV2Manager roleManager) {

        return null;
    }

    @Override
    public SCIMResponse updateWithPUTRole(String id, String putRequest, RoleV2Manager roleManager) {

        return null;
    }

    @Override
    public SCIMResponse updateWithPATCHRole(String id, String patchRequest, RoleV2Manager roleManager) {

        return null;
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

}
