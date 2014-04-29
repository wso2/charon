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
public class BulkResponseData {
    private List<String> schemas;
    private List<BulkResponseContent> userCreatingResponse;
    private List<BulkResponseContent> groupCreatingResponse;

    public BulkResponseData() {
        userCreatingResponse = new ArrayList<BulkResponseContent>();
        groupCreatingResponse = new ArrayList<BulkResponseContent>();
        schemas = new ArrayList<String>();
    }

    public List<BulkResponseContent> getUserCreatingResponse() {
        return userCreatingResponse;
    }

    public void setUserCreatingResponse(List<BulkResponseContent> userCreatingResponse) {
        this.userCreatingResponse = userCreatingResponse;
    }

    public List<BulkResponseContent> getGroupCreatingResponse() {
        return groupCreatingResponse;
    }

    public void setGroupCreatingResponse(List<BulkResponseContent> groupCreatingResponse) {
        this.groupCreatingResponse = groupCreatingResponse;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }
}
