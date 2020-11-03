/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.charon3.core.utils.codeutils;

/**
 * This class represents the PATCH operations which are in the body of PATCH request.
 */
public class PatchOperation implements Comparable<PatchOperation> {

    private String operation;
    private String path;
    private Object values;
    private String attributeName;
    private int executionOrder;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getValues() {
        return values;
    }

    public void setValues(Object values) {
        this.values = values;
    }

    public String getAttributeName() {

        return attributeName;
    }

    public void setAttributeName(String attributeName) {

        this.attributeName = attributeName;
    }

    public int getExecutionOrder() {

        return executionOrder;
    }

    public void setExecutionOrder(int executionOrder) {

        this.executionOrder = executionOrder;
    }

    @Override
    public int compareTo(PatchOperation anotherPatchOperation) {

        return this.getExecutionOrder() - anotherPatchOperation.getExecutionOrder();
    }

    @Override
    public boolean equals(Object obj) {

        return super.equals(obj);
    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }
}
