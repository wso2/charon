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

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.utils.codeutils.Node;
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
            throws CharonException, ConflictException, BadRequestException;

    public User getUser(String id, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException;

    public void deleteUser(String userId)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException;

    default List<Object> listUsersWithGET(Node node, Integer startIndex, Integer count, String sortBy, String sortOrder,
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
    default List<Object> listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
            String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return null;
    }

    @Deprecated
    default List<Object> listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
                                         Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return listUsersWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public List<Object> listUsersWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException;

    public User updateUser(User updatedUser, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, CharonException, BadRequestException, NotFoundException;

    public User getMe(String userName, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException;

    public User createMe(User user, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, BadRequestException;

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

    default List<Object> listGroupsWithGET(Node node, Integer startIndex, Integer count, String sortBy,
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
    default List<Object> listGroupsWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
            String domainName, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return null;
    }

    @Deprecated
    default List<Object> listGroupsWithGET(Node node, int startIndex, int count, String sortBy,
                                          String sortOrder, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        return listGroupsWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public Group updateGroup(Group oldGroup, Group newGroup, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException;

    public List<Object> listGroupsWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException;


    default List<Map<String, String>> getUserSchema() throws CharonException, NotImplementedException,
            BadRequestException {
        throw new NotImplementedException();
    }
}
