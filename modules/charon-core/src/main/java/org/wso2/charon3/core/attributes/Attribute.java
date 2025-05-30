/*
 * Copyright (c) 2016-2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon3.core.attributes;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface to represent Attribute defined in SCIM schema spec.
 */

//Attribute is extended from Serializable as later in org.wso2.charon.core.util.CopyUtil,
//it need to be serialized.
public interface Attribute extends Serializable {

    public String getURI();

    public String getName();

    public SCIMDefinitions.DataType getType();

    public Boolean getMultiValued();

    public String getDescription();

    public Boolean getCaseExact();

    public SCIMDefinitions.Mutability getMutability();

    public SCIMDefinitions.Returned getReturned();

    public SCIMDefinitions.Uniqueness getUniqueness();

    public Boolean getRequired();

    public Attribute getSubAttribute(String attributeName) throws CharonException;

    public void deleteSubAttributes() throws CharonException;

    public default String getAttributeProperty(String propertyName) {

        throw new UnsupportedOperationException();
    }

    public default Map<String, String> getAttributeProperties() {

        throw new UnsupportedOperationException();
    }

    public default Map<String, JSONObject> getAttributeJSONProperties() {

        throw new UnsupportedOperationException();
    }

    public default JSONObject getAttributeJSONProperty(String propertyName) {

        throw new UnsupportedOperationException();
    }

    public default Map<String, JSONArray> getAttributeJSONArrays() {

        throw new UnsupportedOperationException();
    }

    public default JSONArray getAttributeJSONArray(String propertyName) {

        throw new UnsupportedOperationException();
    }

    public default void addAttributeJSONArray(String propertyName, JSONArray jsonArray) {

        throw new UnsupportedOperationException();
    }

    public default JSONArray removeAttributeJSONArray(String propertyName) {

        throw new UnsupportedOperationException();
    }
}
