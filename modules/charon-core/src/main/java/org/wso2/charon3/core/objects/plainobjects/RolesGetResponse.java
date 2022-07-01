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

import org.wso2.charon3.core.objects.Role;

import java.util.List;

/**
 * This class representation can be used to create a RolesGetResponse type object which carries the total number of
 * roles identified from the filter and the list of roles returned for this request.
 */
public class RolesGetResponse {

    private int totalRoles;
    private List<Role> roles;

    /**
     * Constructor used to build a response object when not using cursor pagination.
     */
    public RolesGetResponse(int totalRoles, List<Role> roles) {

        this.totalRoles = totalRoles;
        this.roles = roles;
    }

    public int getTotalRoles() {

        return totalRoles;
    }

    public void setTotalRoles(int totalRoles) {

        this.totalRoles = totalRoles;
    }

    public List<Role> getRoles() {

        return roles;
    }

    public void setRoles (List<Role> filteredRoles) {

        this.roles = filteredRoles;
    }
}
