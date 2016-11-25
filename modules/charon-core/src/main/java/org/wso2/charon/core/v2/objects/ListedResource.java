/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.charon.core.v2.objects;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.exceptions.NotFoundException;
import org.wso2.charon.core.v2.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the listed resource object which is a collection of resources.
 **/

public class ListedResource implements SCIMObject {

    /*List of schemas which the resource is associated with*/
    protected List<String> schemaList = new ArrayList<String>();
    //Specifies the total number of results matching the client query
    protected int totalResults = 0;
    //number of query results returned in a query response page
    protected int itemsPerPage;
    //The 1-based index of the first result in the
    //current set of query results
    protected int startIndex;
    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    public int getTotalResults() {
        return totalResults;
    }

    /*
     * set the total results of the listed resource
     * @param totalResults
     */
    public void setTotalResults(int totalResults) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS, totalResults);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no characteristics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS))
                    .setValue(totalResults);
        }
    }

    public void setSchemaList(List<String> schemaList) {
        this.schemaList = schemaList;
    }

    @Override
    public Attribute getAttribute(String attributeName) throws NotFoundException {
        return null;
    }

    @Override
    public void deleteAttribute(String attributeName) throws NotFoundException {

    }

    @Override
    public List<String> getSchemaList() {
        return schemaList;
    }

    @Override
    public Map<String, Attribute> getAttributeList() {
       return attributeList;
    }

    public void setSchema(String schema) {
        schemaList.add(schema);
    }

    /*
     * set the listed resources
     * @param valueWithAttributes
     */
    public void setResources(Map<String, Attribute> valueWithAttributes) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.RESOURCES)) {
            MultiValuedAttribute resourcesAttribute =
                    new MultiValuedAttribute(SCIMConstants.ListedResourceSchemaConstants.RESOURCES);
            resourcesAttribute.setComplexValueWithSetOfSubAttributes(valueWithAttributes);
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.RESOURCES, resourcesAttribute);
        } else {
            ((MultiValuedAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.RESOURCES))
                    .setComplexValueWithSetOfSubAttributes(valueWithAttributes);
        }
    }

    protected boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /*
     * paginated listed resource items per page settings
     * @param itemsPerPage
     */
    public void setItemsPerPage(int itemsPerPage) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE, itemsPerPage);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no characteristics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE))
                    .setValue(itemsPerPage);
        }
    }

    public int getStartIndex() {
        return startIndex; }

    /*
     *  paginated listed resource start index settings
     * @param startIndex
     */
    public void setStartIndex(int startIndex) {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.START_INDEX)) {
            SimpleAttribute totalResultsAttribute =
                    new SimpleAttribute(SCIMConstants.ListedResourceSchemaConstants.START_INDEX, startIndex);
            //No need to let the Default attribute factory to handle the attribute, as this is
            //not officially defined as SCIM attribute, hence have no charactersitics defined
            //TODO: may be we can let the default attribute factory to handle it?
            attributeList.put(SCIMConstants.ListedResourceSchemaConstants.START_INDEX, totalResultsAttribute);
        } else {
            ((SimpleAttribute) attributeList.get(SCIMConstants.ListedResourceSchemaConstants.START_INDEX))
                    .setValue(startIndex);
        }
    }
}
