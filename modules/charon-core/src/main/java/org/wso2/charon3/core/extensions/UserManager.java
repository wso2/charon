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
package org.wso2.charon3.core.extensions;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.Cursor;
import org.wso2.charon3.core.objects.plainobjects.GroupsGetResponse;
import org.wso2.charon3.core.objects.plainobjects.UsersGetResponse;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.List;
import java.util.Map;

/**
 * This is the interface for usermanager extension.
 * An implementation can plugin their own user manager-(either LDAP based, DB based etc)
 * by implementing this interface and mentioning it in configuration.
 */
public interface UserManager {

        /***************User Manipulation operations.*******************/

    public User createUser(User user, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, BadRequestException, ForbiddenException;

    public User getUser(String id, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException;

    public void deleteUser(String userId)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException;

    /**
     * List users with Get using offset pagination.
     *
     * @param node               Tree node built based on the filtering conditions.
     * @param startIndex         Start Index.
     * @param count              Count.
     * @param sortBy             Sort by.
     * @param sortOrder          Sort order.
     * @param domainName         Domain name.
     * @param requiredAttributes Required user attributes.
     * @return Users with requested attributes.
     * @throws CharonException         Error while listing users.
     * @throws NotImplementedException Operation note implemented.
     * @throws BadRequestException     Bad request.
     */
    default UsersGetResponse listUsersWithGET(Node node, Integer startIndex, Integer count, String sortBy,
                                         String sortOrder, String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {

        return null;
    }

    /**
     * List users with Get using cursor pagination.
     *
     * @param node               Tree node built based on the filtering conditions.
     * @param cursor             Cursor value for pagination and the Pagination direction.
     * @param count              Count.
     * @param sortBy             Sort by.
     * @param sortOrder          Sort order.
     * @param domainName         Domain name.
     * @param requiredAttributes Required user attributes.
     * @return Users with requested attributes.
     * @throws CharonException         Error while listing users.
     * @throws NotImplementedException Operation note implemented.
     * @throws BadRequestException     Bad request.
     */
    default UsersGetResponse listUsersWithGET(Node node, Cursor cursor, Integer count, String sortBy, String sortOrder,
                                              String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {

        return null;
    }

    /**
     * This method is deprecated.
     *
     * @since 1.2.21
     * @deprecated Method does not handle when the count is not specified in the request and when the count specified
     * is zero.
     * Use
     * {@link org.wso2.charon3.core.extensions.UserManager#listUsersWithGET(Node, Integer, Integer, String, String,
     * String, Map) } method.
     */
    @Deprecated
    default UsersGetResponse listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
                                              String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {

        return null;
    }

    @Deprecated
    default UsersGetResponse listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
                                         Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {

        return listUsersWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public UsersGetResponse listUsersWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException;

    public User updateUser(User updatedUser, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, CharonException, BadRequestException, NotFoundException;

    /**
     * Identify user claims to be updated and update the user in user store.
     *
     * @param updatedUser                    Updated user.
     * @param requiredAttributes             URIs of required attributes which must be given a value.
     * @param allSimpleMultiValuedAttributes Simple multi-valued attributes defined in SCIM schema.
     * @return Updated user stored in the user store.
     * @throws CharonException         Charon exception.
     * @throws BadRequestException     Bad request exception.
     * @throws NotFoundException       Not found exception.
     * @throws NotImplementedException Functionality no implemented exception.
     */
    default User updateUser(User updatedUser, Map<String, Boolean> requiredAttributes,
                            List<String> allSimpleMultiValuedAttributes)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException {

        throw new NotImplementedException(
                "Updating simple multi-valued attributes independently from simple attributes is not supported");
    }

    public User getMe(String userName, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException, NotImplementedException;

    public User createMe(User user, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, BadRequestException, ForbiddenException, NotImplementedException;

    public void deleteMe(String userName)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException;

    public User updateMe(User updatedUser, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, CharonException, BadRequestException, NotFoundException;


   /* ****************Group manipulation operations.********************/

    public Group createGroup(Group group, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, NotImplementedException, BadRequestException;

    public Group getGroup(String id, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException;

    public void deleteGroup(String id)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException;

    default GroupsGetResponse listGroupsWithGET(Node node, Integer startIndex, Integer count, String sortBy,
                               String sortOrder, String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return null;
    }

    /**
     * This method is deprecated.
     *
     * @since 1.2.21
     * @deprecated Method does not handle when the count is not specified in the request and when the count specified
     * is zero. Use
     * {@link org.wso2.charon3.core.extensions.UserManager#listGroupsWithGET(Node, Integer, Integer, String, String,
     * String, Map)} method.
     */
    @Deprecated
    default GroupsGetResponse listGroupsWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
            String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return null;
    }

    @Deprecated
    default GroupsGetResponse listGroupsWithGET(Node node, int startIndex, int count, String sortBy,
                                          String sortOrder, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return listGroupsWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public Group updateGroup(Group oldGroup, Group newGroup, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException;

    /**
     *
     * Updates the group.
     *
     * @param oldGroup
     * @param newGroup
     * 
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws NotFoundException
     */
    default void updateGroup(Group oldGroup, Group newGroup)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException {

        throw new NotImplementedException();
    }

    /**
     * Updates the group via PATCH.
     *
     * @param  groupId                 ID of the group.
     * @param  currentGroupName        Current name of the group.
     * @param  patchOperations         A map of patch operations.
     * @param  requiredAttributes      Attributes to be returned in the response.
     * @return Updated group.
     * @throws CharonException         Charon exception.
     * @throws BadRequestException     Bad request exception.
     * @throws NotFoundException       Not found exception.
     * @throws NotImplementedException Functionality no implemented exception.
     */
    default Group patchGroup(String groupId, String currentGroupName, Map<String, List<PatchOperation>> patchOperations,
                             Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException {

        throw new NotImplementedException();
    }

    public GroupsGetResponse listGroupsWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException;

    default List<Attribute> getCoreSchema() throws CharonException, NotImplementedException, BadRequestException {

        throw new NotImplementedException();
    }

    default List<Attribute> getUserSchema() throws CharonException, NotImplementedException, BadRequestException {

        throw new NotImplementedException();
    }

    /**
     * Retrieve schema of the enterprise user.
     *
     * @return List of attributes of enterprise user schema.
     * @throws CharonException
     * @throws NotImplementedException
     * @throws BadRequestException
     */
    default List<Attribute> getEnterpriseUserSchema() throws CharonException, NotImplementedException,
            BadRequestException {

        throw new NotImplementedException();
    }

    /**
     * Return Custom schema.
     * @return Custom schema.
     * @throws CharonException
     * @throws NotImplementedException
     * @throws BadRequestException
     */
    default AttributeSchema getCustomUserSchemaExtension() throws CharonException, NotImplementedException,
            BadRequestException {

        return null;
    }

    /**
     * Returns list of attributes in custom schema.
     *
     * @return List of attributes in custom schema.
     * @throws CharonException
     * @throws NotImplementedException
     * @throws BadRequestException
     */
    default List<Attribute> getCustomUserSchemaAttributes() throws CharonException, NotImplementedException,
            BadRequestException {

        return null;
    }
 }
