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
package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.exceptions.NotFoundException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SCIM Object is a collection of attributes that may come from different schemas.
 * This interface supports that concept, which will be implemented by SCIM objects.
 * In server-side, there should be a way to map the storage to these attributes.
 */
//SCIMObject is extended from Serializable as later in org.wso2.charon.core.util.CopyUtil, it need to be serialized.
public interface SCIMObject extends Serializable {

    public Attribute getAttribute(String attributeName) throws NotFoundException;

    public void deleteAttribute(String attributeName) throws NotFoundException;

    public List<String> getSchemaList();

    public Map<String, Attribute> getAttributeList();

}
