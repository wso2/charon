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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BulkRequestData {
    private List<String> schemas;
    private int failOnErrors = 2;
    private List<BulkRequestContent> userCreatingRequests;
    private List<BulkRequestContent> groupCreatingRequests;

    public BulkRequestData() {
        userCreatingRequests = new ArrayList<BulkRequestContent>();
        groupCreatingRequests = new ArrayList<BulkRequestContent>();
        schemas = new ArrayList<String>();
    }

    public List<BulkRequestContent> getUserCreatingRequests() {
        return userCreatingRequests;
    }

    public void setUserCreatingRequests(List<BulkRequestContent> userCreatingRequests) {
        this.userCreatingRequests = userCreatingRequests;
    }

    public int getFailOnErrors() {
        return failOnErrors;
    }

    public void setFailOnErrors(int failOnErrors) {
        this.failOnErrors = failOnErrors;
    }

    public List<BulkRequestContent> getGroupCreatingRequests() {
        return groupCreatingRequests;
    }

    public void setGroupCreatingRequests(List<BulkRequestContent> groupCreatingRequests) {
        this.groupCreatingRequests = groupCreatingRequests;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    @Override
    public String toString() {
        String bulkObject = "--------------------------------";
        bulkObject = bulkObject + "\n" + "FailOnErrors :" + failOnErrors;
        bulkObject = bulkObject + "\n" + "===========User Creating Requests==========";
        for (BulkRequestContent data : userCreatingRequests) {
            bulkObject = bulkObject + "\n" + data.getData();
        }
        bulkObject = bulkObject + "\n" + "===========Group Creating Requests==========";
        for (BulkRequestContent groupData : groupCreatingRequests) {
            bulkObject = bulkObject + "\n" + groupData.getData();
        }
        bulkObject = bulkObject + "\n" + "--------------------------------";
        return bulkObject;
    }

}
