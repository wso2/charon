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
package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.Map;

/**
 * Represents the listed resource object which is a collection of resources.
 **/

public class ListedResource extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;

    /**
     * @return the total results value of this listed resource
     */
    public int getTotalResults() {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS)) {
            return 0;
        } else {
            String totalResultsString = SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS;
            SimpleAttribute totalResultsAttribute = ((SimpleAttribute) attributeList.get(totalResultsString));
            return (Integer) totalResultsAttribute.getValue();
        }
    }

    /**
     * set the total results of the listed resource
     *
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

    /**
     * @return the items per page value of this listed resource
     */
    public int getItemsPerPage() {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE)) {
            return 0;
        } else {
            String itemsPerPageString = SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE;
            SimpleAttribute itemsPerPageAttribute = ((SimpleAttribute) attributeList.get(itemsPerPageString));
            return (Integer) itemsPerPageAttribute.getValue();
        }
    }

    /**
     * paginated listed resource items per page settings
     *
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

    /**
     * @return the start index value of this listed resource
     */
    public int getStartIndex() {
        if (!isAttributeExist(SCIMConstants.ListedResourceSchemaConstants.START_INDEX)) {
            return 0;
        } else {
            String startIndexString = SCIMConstants.ListedResourceSchemaConstants.START_INDEX;
            SimpleAttribute startIndexAttribute = ((SimpleAttribute) attributeList.get(startIndexString));
            return (Integer) startIndexAttribute.getValue();
        }
    }

    /**
     * paginated listed resource start index settings
     *
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

    /**
     * set the listed resources
     *
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
}
