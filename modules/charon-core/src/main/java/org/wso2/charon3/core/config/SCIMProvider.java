/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.charon3.core.config;

import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SCIMProvider {

    private String id;
    private Map<String, String> properties = new HashMap<>();
    private List<PatchOperation> patchOperationList = new ArrayList<>();

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProperty(String name, String value) {

        if (!this.properties.containsKey(name)) {
            this.properties.put(name, value);
        }

    }

    public String getProperty(String key) {

        return this.properties.containsKey(key) ? this.properties.get(key) : null;
    }

    public void setPatchOperationsList(List<PatchOperation> patchOperationsList) {

        this.patchOperationList = patchOperationsList;
    }

    public void addPatchOperation(PatchOperation patchOperation) {

        this.patchOperationList.add(patchOperation);
    }

    public List<PatchOperation> getPatchOperationList() {

        return patchOperationList;
    }
}
