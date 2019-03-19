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

import org.wso2.charon3.core.exceptions.AbstractCharonException;
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
            throws AbstractCharonException;

    public User getUser(String id, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public void deleteUser(String userId)
            throws AbstractCharonException;

    default List<Object> listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
                                         String domainName, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException {
        return null;
    }

    @Deprecated
    default List<Object> listUsersWithGET(Node node, int startIndex, int count, String sortBy, String sortOrder,
                                         Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException {
        return listUsersWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public List<Object> listUsersWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public User updateUser(User updatedUser, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public User getMe(String userName, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public User createMe(User user, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public void deleteMe(String userName)
            throws AbstractCharonException;

    public User updateMe(User updatedUser, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;


   /* ****************Group manipulation operations.********************/

    public Group createGroup(Group group, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public Group getGroup(String id, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public void deleteGroup(String id)
            throws AbstractCharonException;

    default List<Object> listGroupsWithGET(Node node, int startIndex, int count, String sortBy,
                                          String sortOrder, String domainName, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException {
        return null;
    }

    @Deprecated
    default List<Object> listGroupsWithGET(Node node, int startIndex, int count, String sortBy,
                                          String sortOrder, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException {
        return listGroupsWithGET(node, startIndex, count, sortBy, sortOrder, null, requiredAttributes);
    }

    public Group updateGroup(Group oldGroup, Group newGroup, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;

    public List<Object> listGroupsWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws AbstractCharonException;
}
