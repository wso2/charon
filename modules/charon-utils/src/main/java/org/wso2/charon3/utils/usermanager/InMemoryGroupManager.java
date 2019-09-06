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


import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.Group;
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
public class InMemoryGroupManager implements ResourceHandler<Group> {

    //in memory user manager stores users
    ConcurrentHashMap<String, Group> inMemoryGroupList = new ConcurrentHashMap<String, Group>();

    @Override
    public Group create(Group group, Map<String, Boolean> map) {
        inMemoryGroupList.put(group.getId(), group);
        return (Group) CopyUtil.deepCopy(group);
    }

    @Override
    public Group get(String id, Map<String, Boolean> map) throws NotFoundException {
        if (inMemoryGroupList.get(id) != null) {
            return (Group) CopyUtil.deepCopy(inMemoryGroupList.get(id));
        } else {
            throw new NotFoundException("No user with the id : " + id);
        }
    }

    @Override
    public void delete(String id) throws NotFoundException {
        if (inMemoryGroupList.get(id) == null) {
            throw new NotFoundException("No user with the id : " + id);
        } else {
            inMemoryGroupList.remove(id);
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
            return listGroups(requiredAttributes);
        }
    }

    private List<Object> listGroups(Map<String, Boolean> requiredAttributes) {
        List<Object> groupList = new ArrayList<>();
        groupList.add(0, 0);
        for (Group group : inMemoryGroupList.values()) {
            groupList.add(group);
        }
        groupList.set(0, groupList.size() - 1);
        return (List<Object>) CopyUtil.deepCopy(groupList);
    }

    @Override
    public Group update(Group group, Map<String, Boolean> map) throws NotFoundException {
        if (group.getId() != null) {
            inMemoryGroupList.replace(group.getId(), group);
            return (Group) CopyUtil.deepCopy(group);
        } else {
            throw new NotFoundException("No user with the id : " + group.getId());
        }
    }

    @Override
    public String getResourceEndpoint() {
        return SCIMConstants.GROUP_ENDPOINT;
    }

    @Override
    public SCIMResourceTypeSchema getResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA;
    }
}
