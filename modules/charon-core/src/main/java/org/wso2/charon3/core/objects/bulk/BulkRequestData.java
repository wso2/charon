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
 * represents the data of a bulk request
 */
public class BulkRequestData {

    /**
     * the schemas that were added in the schema-tag in the request
     */
    private List<String> schemas;

    /**
     * number of errors that are allowed to happen before the whole procedure should fail
     */
    private int failOnErrors;

    /**
     * the list of bulk operations to execute
     */
    private List<BulkRequestContent> operationRequests;

    public BulkRequestData() {
        schemas = new ArrayList<>();
        operationRequests = new ArrayList<>();
    }

    public List<BulkRequestContent> getOperationRequests() {
        return operationRequests;
    }

    public void setOperationRequests(List<BulkRequestContent> operationRequests) {
        this.operationRequests = operationRequests;
    }

    public int getFailOnErrors() {
        return failOnErrors;
    }

    public void setFailOnErrors(int failOnErrors) {
        this.failOnErrors = failOnErrors;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

}
