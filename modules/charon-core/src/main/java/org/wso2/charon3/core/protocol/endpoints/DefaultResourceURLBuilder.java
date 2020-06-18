/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.protocol.endpoints;

import org.wso2.charon3.core.exceptions.NotFoundException;

import java.util.Map;

/**
 * This is the default implementation of ResourceURLBuilder for performing operations/customizations for the resource
 * endpoints exposed by the server.
 */
public class DefaultResourceURLBuilder implements ResourceURLBuilder {

    // Keeps a map of endpoint uri/url of the exposed resources.
    private Map<String, String> endpointURIMap;

    public String build(String resource) throws NotFoundException {

        if (endpointURIMap != null && endpointURIMap.size() != 0) {
            return endpointURIMap.get(resource);
        } else {
            throw new NotFoundException();
        }
    }

    public void setEndpointURIMap(Map<String, String> endpointURIMap) {

        this.endpointURIMap = endpointURIMap;
    }
}
