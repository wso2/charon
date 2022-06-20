/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.objects.plainobjects;

import org.wso2.charon3.core.objects.User;
import java.util.List;


public class UsersGetResponse {

    private int totalUsers;
    private List<User> users;

    /**
     * Constructor used to build a response object when not using cursor pagination.
     */
    public UsersGetResponse(int totalUsers, List<User> users) {

        this.totalUsers = totalUsers;
        this.users = users;
    }

    public int getTotalUsers() {

        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {

        this.totalUsers = totalUsers;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> filteredUsers) {
        this.users = filteredUsers;
    }
}