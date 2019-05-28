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

import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a response of a bulk request.
 */
public class BulkResponseData {

    private List<String> schemas;

    /**
     * the responses of the bulk-operations.
     */
    private List<BulkResponseContent> operationResponseList;

    public BulkResponseData() {
        operationResponseList = new ArrayList<>();
        schemas = new ArrayList<>();
        setSchema(SCIMConstants.BULK_RESPONSE_URI);
    }

    public List<BulkResponseContent> getOperationResponseList() {
        return operationResponseList;
    }

    public void setOperationResponseList(List<BulkResponseContent> operationResponseList) {
        this.operationResponseList = operationResponseList;
    }

    public void addOperationResponse(BulkResponseContent operationResponse) {
        this.operationResponseList.add(operationResponse);
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public final void setSchema(String schema) {
        if (schema != null && !this.schemas.contains(schema)) {
            schemas.add(schema);
        }
    }
}
