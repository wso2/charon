/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon3.core.protocol;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.extensions.RoleManager;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.bulk.BulkRequestContent;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;
import org.wso2.charon3.core.protocol.endpoints.RoleResourceManager;
import org.wso2.charon3.core.protocol.endpoints.RoleResourceV2Manager;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class BulkRequestProcessor {
    private UserResourceManager userResourceManager;
    private GroupResourceManager groupResourceManager;
    private RoleResourceManager roleResourceManager;
    private RoleResourceV2Manager roleResourceV2Manager;
    private int failOnError;
    private int errors;
    private UserManager userManager;
    private RoleManager roleManager;
    private RoleV2Manager roleV2Manager;

    public UserResourceManager getUserResourceManager() {
        return userResourceManager;
    }

    public void setUserResourceManager(UserResourceManager userResourceManager) {
        this.userResourceManager = userResourceManager;
    }

    public GroupResourceManager getGroupResourceManager() {
        return groupResourceManager;
    }

    public void setGroupResourceManager(GroupResourceManager groupResourceManager) {
        this.groupResourceManager = groupResourceManager;
    }

    public RoleResourceManager getRoleResourceManager() {

        return roleResourceManager;
    }

    public void setRoleResourceManager(RoleResourceManager roleResourceManager) {

        this.roleResourceManager = roleResourceManager;
    }

    public RoleResourceV2Manager getRoleResourceV2Manager() {

        return roleResourceV2Manager;
    }

    public void setRoleResourceV2Manager(RoleResourceV2Manager roleResourceV2Manager) {

        this.roleResourceV2Manager = roleResourceV2Manager;
    }

    public int getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(int failOnError) {
        this.failOnError = failOnError;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public RoleManager getRoleManager() {

        return roleManager;
    }

    public void setRoleManager(RoleManager roleManager) {

        this.roleManager = roleManager;
    }

    public RoleV2Manager getRoleV2Manager() {

        return roleV2Manager;
    }

    public void setRoleV2Manager(RoleV2Manager roleV2Manager) {

        this.roleV2Manager = roleV2Manager;
    }

    public BulkRequestProcessor() {

        userResourceManager = new UserResourceManager();
        groupResourceManager = new GroupResourceManager();
        roleResourceManager = new RoleResourceManager();
        roleResourceV2Manager = new RoleResourceV2Manager();
        failOnError = 0;
        errors = 0;
        userManager = null;
        roleManager = null;
    }

    public BulkResponseData processBulkRequests(BulkRequestData bulkRequestData) throws BadRequestException {

        BulkResponseData bulkResponseData = new BulkResponseData();

        for (BulkRequestContent bulkRequestContent : bulkRequestData.getUserOperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addUserOperation(getBulkResponseContent(bulkRequestContent, userResourceManager));
            } else {
                if (errors < failOnError) {
                    bulkResponseData.addUserOperation(getBulkResponseContent(bulkRequestContent, userResourceManager));
                }
            }

        }
        Map<String, String> userIdMappings = getUserIdBulkIdMapping(bulkResponseData.getUserOperationResponse());

        for (BulkRequestContent bulkRequestContent : bulkRequestData.getGroupOperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addGroupOperation(
                        getBulkResponseContent(bulkRequestContent, userIdMappings, groupResourceManager));
            } else  {
                if (errors < failOnError) {
                    bulkResponseData.addGroupOperation(
                            getBulkResponseContent(bulkRequestContent, userIdMappings, groupResourceManager));
                }
            }

        }
        // Handle v1 role operations.
        for (BulkRequestContent bulkRequestContent : bulkRequestData.getRoleOperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addRoleOperation(getBulkResponseContent(bulkRequestContent, userIdMappings,
                        roleResourceManager));
            } else {
                if (errors < failOnError) {
                    bulkResponseData.addRoleOperation(getBulkResponseContent(bulkRequestContent,
                            userIdMappings, roleResourceManager));
                }
            }
        }
        // Handle v2 role operations.
        for (BulkRequestContent bulkRequestContent : bulkRequestData.getRoleV2OperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addRoleOperation(getBulkResponseContent(bulkRequestContent, userIdMappings,
                        roleResourceV2Manager));
            } else {
                if (errors < failOnError) {
                    bulkResponseData.addRoleOperation(getBulkResponseContent(bulkRequestContent,
                            userIdMappings, roleResourceV2Manager));
                }
            }
        }
        bulkResponseData.setSchema(SCIMConstants.BULK_RESPONSE_URI);
        return bulkResponseData;
    }

    private BulkResponseContent getBulkResponseContent(BulkRequestContent bulkRequestContent,
                                                       ResourceManager resourceManager) throws BadRequestException {

        return getBulkResponseContent(bulkRequestContent, null, resourceManager);
    }

    private BulkResponseContent getBulkResponseContent(BulkRequestContent bulkRequestContent,
                                                       Map<String, String> userIdMappings,
                                                       ResourceManager resourceManager)
            throws BadRequestException {

        BulkResponseContent bulkResponseContent = null;
        SCIMResponse response;
        processBulkRequestContent(bulkRequestContent, userIdMappings, bulkRequestContent.getMethod());

        switch (bulkRequestContent.getMethod()) {
            case SCIMConstants.OperationalConstants.POST:
                if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_V2_ENDPOINT)) {
                    response = resourceManager.createRole(bulkRequestContent.getData(), roleV2Manager);
                } else if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_ENDPOINT)) {
                    response = resourceManager.createRole(bulkRequestContent.getData(), roleManager);
                } else {
                    response = resourceManager.create(bulkRequestContent.getData(), userManager,
                            null, null);
                }

                bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.POST,
                        bulkRequestContent);
                errorsCheck(response);
                break;

            case SCIMConstants.OperationalConstants.PUT: {
                String resourceId = extractIDFromPath(bulkRequestContent.getPath());
                if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_V2_ENDPOINT)) {
                    resourceId = extractIDFromV2Path(bulkRequestContent.getPath());
                    response = resourceManager.updateWithPUTRole(resourceId, bulkRequestContent.getData(),
                            roleV2Manager);
                } else if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_ENDPOINT)) {
                    response = resourceManager.updateWithPUTRole(resourceId, bulkRequestContent.getData(),
                            roleManager);
                } else {
                    response = resourceManager.updateWithPUT(resourceId, bulkRequestContent.getData(), userManager,
                            null, null);
                }

                bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.PUT,
                        bulkRequestContent);
                errorsCheck(response);
                break;
            }

            case SCIMConstants.OperationalConstants.PATCH: {
                String resourceId = extractIDFromPath(bulkRequestContent.getPath());
                if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_V2_ENDPOINT)) {
                    resourceId = extractIDFromV2Path(bulkRequestContent.getPath());
                    response = resourceManager.updateWithPATCHRole(resourceId, bulkRequestContent.getData(),
                            roleV2Manager);
                } else if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_ENDPOINT)) {
                    response = resourceManager.updateWithPATCHRole(resourceId, bulkRequestContent.getData(),
                            roleManager);
                } else {
                    response = resourceManager.updateWithPATCH(resourceId, bulkRequestContent.getData(), userManager,
                            null, null);
                }

                bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.PATCH,
                        bulkRequestContent);
                errorsCheck(response);
                break;
            }

            case SCIMConstants.OperationalConstants.DELETE: {
                String resourceId = extractIDFromPath(bulkRequestContent.getPath());
                if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_V2_ENDPOINT)) {
                    resourceId = extractIDFromV2Path(bulkRequestContent.getPath());
                    response = resourceManager.deleteRole(resourceId, roleV2Manager);
                } else if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_ENDPOINT)) {
                    response = resourceManager.deleteRole(resourceId, roleManager);
                } else {
                    response = resourceManager.delete(resourceId, userManager);
                }

                bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.DELETE,
                        bulkRequestContent);
                errorsCheck(response);
                break;
            }

            case SCIMConstants.OperationalConstants.GET: {
                String resourceId = extractIDFromGetRequestPath(bulkRequestContent.getPath());
                Map<String, String> queryParams = parseQueryParameters(bulkRequestContent.getPath());
                String attributes = queryParams.get(SCIMConstants.CommonSchemaConstants.ATTRIBUTES);
                String excludeAttributes = queryParams.get(SCIMConstants.CommonSchemaConstants.EXCLUDE_ATTRIBUTES);
                if (StringUtils.isNotBlank(bulkRequestContent.getData())) {
                    if (StringUtils.isBlank(attributes)) {
                        attributes = getAttributesFromData(bulkRequestContent.getData());
                    }
                    if (StringUtils.isBlank(excludeAttributes)) {
                        excludeAttributes = getExcludedAttributesFromData(bulkRequestContent.getData());
                    }
                }
                if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_V2_ENDPOINT)) {
                    resourceId = extractIDFromV2GetRequestPath(bulkRequestContent.getPath());
                    response = resourceManager.getRole(resourceId, roleV2Manager, attributes, excludeAttributes);
                } else if (bulkRequestContent.getPath().contains(SCIMConstants.ROLE_ENDPOINT)) {
                    response = resourceManager.getRole(resourceId, roleManager, attributes, excludeAttributes);
                } else {
                    response = resourceManager.get(resourceId, userManager, attributes, excludeAttributes);
                }

                bulkResponseContent = createBulkResponseContent(response, SCIMConstants.OperationalConstants.GET,
                        bulkRequestContent);
                errorsCheck(response);
                break;
            }
        }
        return bulkResponseContent;
    }

    private String getExcludedAttributesFromData(String data) throws BadRequestException {

        String excludedAttributes = null;
        try {
            JSONObject dataJson = new JSONObject(data);
            if (dataJson.has(SCIMConstants.CommonSchemaConstants.EXCLUDE_ATTRIBUTES)) {
                excludedAttributes = dataJson.getString(SCIMConstants.CommonSchemaConstants.EXCLUDE_ATTRIBUTES);
            }
        } catch (JSONException e) {
            throw new BadRequestException("Error while parsing the data field of the bulk request content",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
        return excludedAttributes;
    }

    private String getAttributesFromData(String data) throws BadRequestException {

        String attributes = null;
        try {
            JSONObject dataJson = new JSONObject(data);
            if (dataJson.has(SCIMConstants.CommonSchemaConstants.ATTRIBUTES)) {
                attributes = dataJson.getString(SCIMConstants.CommonSchemaConstants.ATTRIBUTES);
            }
        } catch (JSONException e) {
            throw new BadRequestException("Error while parsing the data field of the bulk request content",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
        return attributes;
    }

    private String extractIDFromPath(String path) throws BadRequestException {

        String [] parts = path.split("[/]");
        if (parts[2] != null) {
            return parts[2];
        } else {
            throw new BadRequestException
                    ("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
        }
    }

    private String extractIDFromGetRequestPath(String path) throws BadRequestException {

        String[] parts = path.split("[?]");
        if (parts.length == 1) {
            return extractIDFromPath(path);
        }
        if (parts[0] != null) {
            return extractIDFromPath(parts[0]);
        }
        throw new BadRequestException
                ("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
    }

    private static Map<String, String> parseQueryParameters(String path) {

        String[] parts = path.split("[?]");
        if (parts.length < 2) {
            return new HashMap<>();
        }
        return Arrays.stream(parts[1].split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> pair.length > 1 ? pair[1] : ""));
    }

    private String extractIDFromV2Path(String path) throws BadRequestException {

        String [] parts = path.split("[/]");
        if (parts[3] != null) {
            return parts[3];
        }
        throw new BadRequestException
                    ("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
    }

    private String extractIDFromV2GetRequestPath(String path) throws BadRequestException {

        String[] parts = path.split("[?]");
        if (parts.length == 1) {
            return extractIDFromV2Path(path);
        }
        if (parts[0] != null) {
            return extractIDFromV2Path(parts[0]);
        }
        throw new BadRequestException
                ("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
    }

    private BulkResponseContent createBulkResponseContent(SCIMResponse response, String method,
                                                          BulkRequestContent requestContent) {

        BulkResponseContent bulkResponseContent = new BulkResponseContent();
        bulkResponseContent.setScimResponse(response);
        bulkResponseContent.setMethod(method);
        if (response.getHeaderParamMap() != null) {
            bulkResponseContent.setLocation(response.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER));
        }
        bulkResponseContent.setBulkID(requestContent.getBulkID());
        bulkResponseContent.setVersion(requestContent.getVersion());

        return bulkResponseContent;
    }

    private void errorsCheck(SCIMResponse response) {
        if (response.getResponseStatus() != 200 && response.getResponseStatus() != 201 &&
                response.getResponseStatus() != 204) {
            errors++;
        }
    }

    /**
     * This method is used to process the bulk request content.
     * This method will replace the bulk id with the created user id.
     *
     * @param bulkRequestContent   Bulk request content.
     * @param userIDMappings       User id bulk id mapping.
     * @param method               HTTP method.
     * @throws BadRequestException Bad request exception.
     */
    private void processBulkRequestContent(BulkRequestContent bulkRequestContent, Map<String, String> userIDMappings,
                                           String method) throws BadRequestException {

        try {
            if (userIDMappings == null || userIDMappings.isEmpty() ||
                    SCIMConstants.OperationalConstants.DELETE.equals(method)) {
                return;
            }

            JSONObject dataJson = new JSONObject(bulkRequestContent.getData());
            String usersOrMembersKey = getUsersOrMembersKey(bulkRequestContent.getPath());
            JSONArray usersArray = getUserArray(dataJson, method, usersOrMembersKey);

            if (usersArray != null) {
                String bulkIdPrefix =
                        SCIMConstants.OperationalConstants.BULK_ID + SCIMConstants.OperationalConstants.COLON;
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    String userValue = user.getString(SCIMConstants.OperationalConstants.VALUE);
                    if (userValue.startsWith(bulkIdPrefix)) {
                        String userBulkId = userValue.substring(bulkIdPrefix.length());
                        String userId = userIDMappings.get(userBulkId);
                        if (StringUtils.isNotBlank(userId)) {
                            user.put(SCIMConstants.OperationalConstants.VALUE, userId);
                        }
                    }
                }
            }
            bulkRequestContent.setData(dataJson.toString());
        } catch (JSONException e) {
            throw new BadRequestException("Error while parsing the data field of the bulk request content",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private String getUsersOrMembersKey(String path) {

        return path.contains(SCIMConstants.ROLE_ENDPOINT) ? SCIMConstants.RoleSchemaConstants.USERS :
                SCIMConstants.GroupSchemaConstants.MEMBERS;
    }

    /**
     * This method is used to get the user array from the data JSON object.
     *
     * @param dataJson          SCIM data JSON object.
     * @param method            HTTP method.
     * @param usersOrMembersKey Users or members key.
     * @return User array.
     * @throws JSONException    JSON exception.
     */
    private JSONArray getUserArray(JSONObject dataJson, String method, String usersOrMembersKey) throws JSONException {

        if (SCIMConstants.OperationalConstants.PATCH.equals(method)) {
            return getUserArrayForPatch(dataJson, usersOrMembersKey);
        }
        return dataJson.optJSONArray(usersOrMembersKey);
    }

    private JSONArray getUserArrayForPatch(JSONObject dataJson, String usersOrMembersKey) throws JSONException {

        JSONArray operations = dataJson.optJSONArray(SCIMConstants.OperationalConstants.OPERATIONS);
        if (operations == null) {
            return null;
        }
        for (int i = 0; i < operations.length(); i++) {
            JSONObject operation = operations.getJSONObject(i);
            if (isValidOperation(operation, usersOrMembersKey)) {
                if (operation.has(SCIMConstants.OperationalConstants.PATH)) {
                    return operation.optJSONArray(SCIMConstants.OperationalConstants.VALUE);
                }
                JSONObject valueObject = operation.optJSONObject(SCIMConstants.OperationalConstants.VALUE);
                if (valueObject != null) {
                    return valueObject.optJSONArray(usersOrMembersKey);
                }
            }
        }
        return null;
    }

    private boolean isValidOperation(JSONObject operation, String path) throws JSONException {

        String operationType = operation.optString(SCIMConstants.OperationalConstants.OP);
        String operationPath = operation.optString(SCIMConstants.OperationalConstants.PATH, StringUtils.EMPTY);

        return (SCIMConstants.OperationalConstants.ADD.equalsIgnoreCase(operationType) ||
                SCIMConstants.OperationalConstants.REPLACE.equalsIgnoreCase(operationType)) &&
                (operationPath.equals(path) || operationPath.isEmpty());
    }

    /**
     * This method is used to get user id bulk id mapping from the bulk user operation response.
     *
     * @param bulkUserOperationResponse Bulk user operation response.
     * @return Bulk id user id mapping.
     */
    private static Map<String, String> getUserIdBulkIdMapping(List<BulkResponseContent> bulkUserOperationResponse) {

        Map<String, String> userIdMappings = new HashMap<>();
        for (BulkResponseContent bulkResponse : bulkUserOperationResponse) {
            String bulkId = bulkResponse.getBulkID();

            SCIMResponse response = bulkResponse.getScimResponse();
            if (response.getResponseStatus() == ResponseCodeConstants.CODE_CREATED) {
                String locationHeader = response.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER);

                if (locationHeader != null) {
                    String[] locationHeaderParts =
                            locationHeader.split(SCIMConstants.OperationalConstants.URL_SEPARATOR);
                    String userId = locationHeaderParts[locationHeaderParts.length - 1];
                    userIdMappings.put(bulkId, userId);
                }
            }
        }
        return userIdMappings;
    }
}
