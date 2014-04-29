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
package org.wso2.charon.core.objects.bulk;

import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BulkData {
    private List<String> schemas;
    private int failOnErrors = 2;
    List<User> userList;
    List<Group> groupList;

    public List<String> getSchemas() {
        return schemas;
    }

    public void addSchemas(String schema) {
        this.schemas.add(schema);
    }

    public int getFailOnErrors() {
        return failOnErrors;
    }

    public void setFailOnErrors(int failOnErrors) {
        this.failOnErrors = failOnErrors;
    }

    public BulkData() {
        schemas = new ArrayList<String>();
        userList = new ArrayList<User>();
        groupList = new ArrayList<Group>();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void addUser(User user) {
        this.userList.add(user);
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void addGroup(Group group) {
        this.groupList.add(group);
    }
}
