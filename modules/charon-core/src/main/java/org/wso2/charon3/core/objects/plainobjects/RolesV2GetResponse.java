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

package org.wso2.charon3.core.objects.plainobjects;

import org.wso2.charon3.core.objects.RoleV2;

import java.util.List;

/**
 * This class representation can be used to create a RolesV2GetResponse type object which carries the total number of
 * roles identified from the filter and the list of roles returned for this request.
 */
public class RolesV2GetResponse {

    private int totalRoles;
    private List<RoleV2> roles;

    /**
     * Constructor used to build a response object.
     */
    public RolesV2GetResponse(int totalRoles, List<RoleV2> roles) {

        this.totalRoles = totalRoles;
        this.roles = roles;
    }

    /**
     * Get total number of roles.
     *
     * @return Total number of roles.
     */
    public int getTotalRoles() {

        return totalRoles;
    }

    /**
     * Set total number of roles.
     *
     * @param totalRoles Total number of roles.
     */
    public void setTotalRoles(int totalRoles) {

        this.totalRoles = totalRoles;
    }

    /**
     * Get list of roles.
     *
     * @return List of roles.
     */
    public List<RoleV2> getRoles() {

        return roles;
    }

    /**
     * Set list of roles.
     *
     * @param filteredRoles List of roles.
     */
    public void setRoles (List<RoleV2> filteredRoles) {

        this.roles = filteredRoles;
    }
}
