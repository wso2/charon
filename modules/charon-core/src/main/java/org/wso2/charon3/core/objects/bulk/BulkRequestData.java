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
package org.wso2.charon3.core.objects.bulk;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BulkRequestData {

    private List<String> schemas;
    private int failOnErrors;
    private List<BulkRequestContent> userOperationRequests;
    private List<BulkRequestContent> groupOperationRequests;
    private List<BulkRequestContent> roleOperationRequests;

    public BulkRequestData() {

        userOperationRequests = new ArrayList<>();
        groupOperationRequests = new ArrayList<>();
        roleOperationRequests = new ArrayList<>();
        schemas = new ArrayList<>();
    }

    public List<BulkRequestContent> getUserOperationRequests() {
        return userOperationRequests;
    }

    public void setUserOperationRequests(List<BulkRequestContent> userOperationRequests) {
        this.userOperationRequests = userOperationRequests;
    }

    public int getFailOnErrors() {
        return failOnErrors;
    }

    public void setFailOnErrors(int failOnErrors) {
        this.failOnErrors = failOnErrors;
    }

    public List<BulkRequestContent> getGroupOperationRequests() {
        return groupOperationRequests;
    }

    public void setGroupOperationRequests(List<BulkRequestContent> groupOperationRequests) {
        this.groupOperationRequests = groupOperationRequests;
    }

    public List<BulkRequestContent> getRoleOperationRequests() {

        return roleOperationRequests;
    }

    public void setRoleOperationRequests(List<BulkRequestContent> roleOperationRequests) {

        this.roleOperationRequests = roleOperationRequests;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

}
