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
package org.wso2.charon3.utils.usermanager;


import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.codeutils.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a sample dynamic user store.
 */
public class InMemoryUserManager implements ResourceHandler<User> {

    //in memory user manager stores users
    ConcurrentHashMap<String, User> inMemoryUserList = new ConcurrentHashMap<String, User>();


    @Override
    public User create(User user, Map<String, Boolean> map) throws AbstractCharonException {
        if (inMemoryUserList.get(user.getId()) != null) {
            throw new ConflictException("User with the id : " + user.getId() + "already exists");
        } else {
            inMemoryUserList.put(user.getId(), user);
            return (User) CopyUtil.deepCopy(user);
        }
    }

    @Override
    public User get(String id, Map<String, Boolean> map) throws NotFoundException {
        if (inMemoryUserList.get(id) != null) {
            return (User) CopyUtil.deepCopy(inMemoryUserList.get(id));
        } else {
            throw new NotFoundException("No user with the id : " + id);
        }
    }

    @Override
    public void delete(String id) throws NotFoundException {
        if (inMemoryUserList.get(id) == null) {
            throw new NotFoundException("No user with the id : " + id);
        } else {
            inMemoryUserList.remove(id);
        }
    }

    @Override
    public List<Object> listResources(Node rootNode,
                                      Integer startIndex,
                                      Integer count,
                                      String sortBy,
                                      String sortOrder,
                                      String domainName,
                                      Map<String, Boolean> requiredAttributes) throws NotImplementedException {
        if (sortBy != null || sortOrder != null) {
            throw new NotImplementedException("Sorting is not supported");
        } else if (startIndex != 1) {
            throw new NotImplementedException("Pagination is not supported");
        } else if (rootNode != null) {
            throw new NotImplementedException("Filtering is not supported");
        } else {
            return listUsers(requiredAttributes);
        }
    }

    private List<Object> listUsers(Map<String, Boolean> requiredAttributes) {
        List<Object> userList = new ArrayList<>();
        userList.add(0);
        //first item should contain the number of total results
        for (Map.Entry<String, User> entry : inMemoryUserList.entrySet()) {
            userList.add(entry.getValue());
        }
        userList.set(0, userList.size() - 1);
        return (List<Object>) CopyUtil.deepCopy(userList);
    }

    @Override
    public User update(User user, Map<String, Boolean> map)
        throws NotImplementedException, CharonException, BadRequestException, NotFoundException {
        if (user.getId() != null) {
            inMemoryUserList.replace(user.getId(), user);
            return (User) CopyUtil.deepCopy(user);
        } else {
            throw new NotFoundException("No user with the id : " + user.getId());
        }
    }

    @Override
    public String getResourceEndpoint() {
        return SCIMConstants.USER_ENDPOINT;
    }

    @Override
    public SCIMResourceTypeSchema getResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
    }
}
