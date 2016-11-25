package org.wso2.charon.core.v2.utils.codeutils;

import java.util.ArrayList;

/**
 * this corresponds to the /.search request object
 *
 * Clients MAY execute queries without passing parameters on the URL by
 using the HTTP POST verb combined with the "/.search" path extension.
 The inclusion of "/.search" on the end of a valid SCIM endpoint SHALL
 be used to indicate that the HTTP POST verb is intended to be a query
 operation.

 */
public class SearchRequest {

    private String schema;
    private ArrayList<String> attributes;
    private ArrayList<String> excludedAttributes;
    private int count;
    private int startIndex;
    private Node filter;
    private String sortBy;
    private String sortOder;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<String> getExcludedAttributes() {
        return excludedAttributes;
    }

    public void setExcludedAttributes(ArrayList<String> excludedAttributes) {
        this.excludedAttributes = excludedAttributes;
    }

    public void setCount(int count) {
        this.count = count;
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

    public String getAttributesAsString(){
        String attributes = null;
        for (String attributeValue : this.attributes){
            attributes = attributes + "," + attributeValue;
        }
        return  attributes;
    }

    public String getExcludedAttributesAsString(){
        String excludedAttributes = null;
        for (String attributeValue : this.excludedAttributes){
            excludedAttributes = excludedAttributes + "," + attributeValue;
        }
        return  excludedAttributes;
    }
}
