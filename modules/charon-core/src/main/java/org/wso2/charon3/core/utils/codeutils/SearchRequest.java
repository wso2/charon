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

import java.util.List;

/**
 * this corresponds to the /.search request object
 * <p>
 * Clients MAY execute queries without passing parameters on the URL by
 * using the HTTP POST verb combined with the "/.search" path extension.
 * The inclusion of "/.search" on the end of a valid SCIM endpoint SHALL
 * be used to indicate that the HTTP POST verb is intended to be a query
 * operation.
 */
public class SearchRequest {

    private String schema;
    private List<String> attributes = null;
    private List<String> excludedAttributes = null;
    private int count;
    private int startIndex;
    private String countStr;
    private String startIndexStr;
    private Node filter;
    private String sortBy;
    private String sortOder;
    private String domainName;

    public String getCountStr() {
        return countStr;
    }

    public void setCountStr(String countStr) {
        this.countStr = countStr;
    }

    public String getStartIndexStr() {
        return startIndexStr;
    }

    public void setStartIndexStr(String startIndexStr) {
        this.startIndexStr = startIndexStr;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getExcludedAttributes() {
        return excludedAttributes;
    }

    public void setExcludedAttributes(List<String> excludedAttributes) {
        this.excludedAttributes = excludedAttributes;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public Node getFilter() {
        return filter;
    }

    public void setFilter(Node filter) {
        this.filter = filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOder() {
        return sortOder;
    }

    public void setSortOder(String sortOder) {
        this.sortOder = sortOder;
    }

    public String getAttributesAsString() {
        String attributes = null;
        StringBuffer str = new StringBuffer();
        for (String attributeValue : this.attributes) {
            str.append(",").append(attributeValue);
        }
        attributes = str.toString();
        if (attributes.equals("")) {
            return null;
        }
        return attributes;
    }

    public String getExcludedAttributesAsString() {
        String excludedAttributes = null;
        StringBuffer str = new StringBuffer();
        for (String attributeValue : this.excludedAttributes) {
            str.append(",").append(attributeValue);
        }
        excludedAttributes = str.toString();
        if (excludedAttributes.equals("")) {
            return null;
        }
        return excludedAttributes;
    }

    public String getDomainName() {

        return domainName;
    }

    public void setDomainName(String domain) {

        this.domainName = domain;
    }
}
