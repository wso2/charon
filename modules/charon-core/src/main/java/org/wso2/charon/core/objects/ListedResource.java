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
package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListedResource implements SCIMObject {

    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    /*Once a retrieve/list/filter response is retrieved, store the decoded objects here*/
    protected List<SCIMObject> scimObjects = new ArrayList<SCIMObject>();

    /*List of schemas where the attributes of this resource, are defined.*/
    protected List<String> schemaList = new ArrayList<String>();

    /*Specifies what is the type(i.e User,Group) contained in the listed resource.*/
    protected int listedResourceType;

    public List<SCIMObject> getScimObjects() {
        return scimObjects;
    }

    public void setScimObjects(List<SCIMObject> scimObjects) {
        this.scimObjects = scimObjects;
    }

    public int getListedResourceType() {
        return listedResourceType;
    }

    public void setListedResourceType(int listedResourceType) {
        this.listedResourceType = listedResourceType;
    }

    @Override
    public Attribute getAttribute(String attributeName) throws NotFoundException {
        if (attributeList.containsKey(attributeName)) {
            return attributeList.get(attributeName);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteAttribute(String attributeName) throws NotFoundException {
        if (attributeList.containsKey(attributeName)) {
            attributeList.remove(attributeName);
        }
    }

    @Override
    public List<String> getSchemaList() {
        return schemaList;
    }

    @Override
    public Map<String, Attribute> getAttributeList() {
        return attributeList;
    }

    public void setTotalResults(int totalResults) throws CharonException {
        if (!isAttributeExist(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS, null,
                                        totalResults, SCIMSchemaDefinitions.DataType.INTEGER);
            attributeList.put(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourcesConstants.TOTAL_RESULTS))
                    .updateValue(totalResults, SCIMSchemaDefinitions.DataType.INTEGER);
        }
    }

    public void setResources(Map<String, Attribute> valueWithAttributes) {
        if (!isAttributeExist(SCIMConstants.ListedResourcesConstants.RESOURCES)) {
            MultiValuedAttribute resourcesAttribute =
                    new MultiValuedAttribute(SCIMConstants.ListedResourcesConstants.RESOURCES);
            resourcesAttribute.setComplexValueWithSetOfSubAttributes(valueWithAttributes);
            for (Attribute attribute : valueWithAttributes.values()) {
                if ((attribute.getSchemaName() != null) && (!schemaList.contains(attribute.getSchemaName()))) {
                    schemaList.add(attribute.getSchemaName());
                }
            }
            attributeList.put(SCIMConstants.ListedResourcesConstants.RESOURCES, resourcesAttribute);
        } else {
            ((MultiValuedAttribute) attributeList.get(SCIMConstants.ListedResourcesConstants.RESOURCES))
                    .setComplexValueWithSetOfSubAttributes(valueWithAttributes);
        }
    }

    private boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }


}
