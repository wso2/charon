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
public class BulkResponseData {

    private List<String> schemas;
    private List<BulkResponseContent> userOperationResponse;
    private List<BulkResponseContent> groupOperationResponse;
    private List<BulkResponseContent> roleOperationResponse;

    public BulkResponseData() {

        userOperationResponse = new ArrayList<>();
        groupOperationResponse = new ArrayList<>();
        roleOperationResponse = new ArrayList<>();
        schemas = new ArrayList<>();
    }

    public List<BulkResponseContent> getUserOperationResponse() {
        return userOperationResponse;
    }

    public void setUserOperationResponse(List<BulkResponseContent> userOperationResponse) {

        this.userOperationResponse = userOperationResponse;
    }

    public List<BulkResponseContent> getGroupOperationResponse() {

        return groupOperationResponse;
    }

    public List<BulkResponseContent> getRoleOperationResponse() {

        return roleOperationResponse;
    }

    public void setRoleOperationResponse(List<BulkResponseContent> roleOperationResponse) {

        this.roleOperationResponse = roleOperationResponse;
    }

    public void addUserOperation(BulkResponseContent bulkResponseContent) {

        userOperationResponse.add(bulkResponseContent);
    }

    public void addGroupOperation(BulkResponseContent bulkResponseContent) {

        groupOperationResponse.add(bulkResponseContent);
    }

    public void addRoleOperation(BulkResponseContent bulkResponseContent) {

        roleOperationResponse.add(bulkResponseContent);
    }

    public void setGroupOperationResponse(List<BulkResponseContent> groupOperationResponse) {

        this.groupOperationResponse = groupOperationResponse;
    }

    public List<String> getSchemas() {

        return schemas;
    }

    public void setSchema(String schema) {

        if (schema != null) {
            schemas.add(schema);
        }
    }
}
