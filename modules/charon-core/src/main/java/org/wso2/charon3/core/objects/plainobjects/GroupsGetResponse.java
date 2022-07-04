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

import org.wso2.charon3.core.objects.Group;

import java.util.List;

/**
 * This class representation can be used to create a GroupsGetResponse type object which carries the total number of
 * groups identified from the filter and the list of groups returned for this request.
 */
public class GroupsGetResponse {

    private int totalGroups;
    private List<Group> groups;

    /**
     * Constructor used to build a response object.
     */
    public GroupsGetResponse(int totalGroups, List<Group> groups) {

        this.totalGroups = totalGroups;
        this.groups = groups;
    }

    public int getTotalGroups() {

        return totalGroups;
    }

    public void setTotalGroups(int totalGroups) {

        this.totalGroups = totalGroups;
    }

    public List<Group> getGroups() {

        return groups;
    }

    public void setGroups(List<Group> filteredGroups) {

        this.groups = filteredGroups;
    }
}
