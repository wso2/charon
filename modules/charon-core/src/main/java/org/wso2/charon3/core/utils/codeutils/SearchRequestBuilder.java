package org.wso2.charon3.core.utils.codeutils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.Locale;
import java.util.Optional;

/**
 * author Pascal Knueppel <br> created at: 25.04.2019 - 09:08 <br>.
 * <br>
 * this class can be used to build search-post requests for endpoints ending with /.search
 */
public class SearchRequestBuilder {


    /**
     * An integer indicating the desired maximum number of query results per page.  See Section 3.4.2.4.  OPTIONAL.
     */
    private Integer count;

    /**
     * An integer indicating the 1-based index of the first query result.  See Section 3.4.2.4.  OPTIONAL.
     */
    private Integer startIndex;

    /**
     * A string indicating the order in which the "sortBy" parameter is applied.  Allowed values are "ascending" and.
     * "descending".  See Section 3.4.2.3.  OPTIONAL.
     */
    private SortOrder sortOrder;

    /**
     * A string indicating the attribute whose value SHALL be used to order the returned responses.  The "sortBy".
     * attribute MUST be in standard attribute notation (Section 3.10) form.  See Section 3.4.2.3.  OPTIONAL.
     */
    private String sortBy;

    /**
     * The filter string used to request a subset of resources.  The filter string MUST be a valid filter (Section.
     * 3.4.2.2) expression. OPTIONAL.
     */
    private String filter;

    /**
     * A multi-valued list of strings indicating the names of resource attributes to return in the response, overriding.
     * the set of attributes that would be returned by default.  Attribute names MUST be in standard attribute notation
     * (Section 3.10) form.  See Section 3.9 for additional retrieval query parameters.  OPTIONAL.
     */
    private String attributes;

    /**
     * A multi-valued list of strings indicating the names of resource attributes to be removed from the default set of.
     * attributes to return.  This parameter SHALL have no effect on attributes whose schema "returned" setting is
     * "always" (see Sections 2.2 and 7 of [RFC7643]).  Attribute names MUST be in standard attribute notation (Section
     * 3.10) form.  See Section 3.9 for additional retrieval query parameters.  OPTIONAL.
     */
    private String excludedAttributes;

    private SearchRequestBuilder() {

    }

    /**
     * @return a new builder instance.
     */
    public static SearchRequestBuilder builder() {
        return new SearchRequestBuilder();
    }


    /**
     * @return the search request as json string.
     */
    public String build() {
        JSONObject searchRequest = new JSONObject();
        JSONArray schemas = new JSONArray();
        schemas.put(SCIMConstants.SEARCH_SCHEMA_URI);
        searchRequest.put(SCIMConstants.CommonSchemaConstants.SCHEMAS, schemas);
        Optional.ofNullable(count).ifPresent(integer -> searchRequest.put(SCIMConstants.OperationalConstants.COUNT,
            integer));
        Optional.ofNullable(startIndex)
            .ifPresent(integer -> searchRequest.put(SCIMConstants.OperationalConstants.START_INDEX, integer));
        Optional.ofNullable(filter)
            .ifPresent(string -> searchRequest.put(SCIMConstants.OperationalConstants.FILTER, string));
        Optional.ofNullable(sortBy)
            .ifPresent(string -> searchRequest.put(SCIMConstants.OperationalConstants.SORT_BY, string));
        Optional.ofNullable(sortOrder)
            .ifPresent(enumType -> searchRequest.put(SCIMConstants.OperationalConstants.SORT_ORDER,
                enumType.name().toLowerCase(Locale.ENGLISH)));
        Optional.ofNullable(attributes)
            .ifPresent(string -> searchRequest.put(SCIMConstants.OperationalConstants.ATTRIBUTES, string));
        Optional.ofNullable(excludedAttributes)
            .ifPresent(string -> searchRequest.put(SCIMConstants.OperationalConstants.EXCLUDED_ATTRIBUTES, string));
        return searchRequest.toString();
    }

    /**
     * @see #count.
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @see #count.
     */
    public SearchRequestBuilder setCount(Integer count) {
        this.count = count;
        return this;
    }

    /**
     * @see #startIndex.
     */
    public Integer getStartIndex() {
        return startIndex;
    }

    /**
     * @see #startIndex.
     */
    public SearchRequestBuilder setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    /**
     * @see #sortOrder.
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * @see #sortOrder.
     */
    public SearchRequestBuilder setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * @see #sortBy.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * @see #sortBy.
     */
    public SearchRequestBuilder setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    /**
     * @see #filter.
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @see #filter.
     */
    public SearchRequestBuilder setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * @see #attributes.
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * @see #attributes.
     */
    public SearchRequestBuilder setAttributes(String attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * @see #excludedAttributes.
     */
    public String getExcludedAttributes() {
        return excludedAttributes;
    }

    /**
     * @see #excludedAttributes.
     */
    public SearchRequestBuilder setExcludedAttributes(String excludedAttributes) {
        this.excludedAttributes = excludedAttributes;
        return this;
    }

    /**
     * the allowed values for sortOrder.
     */
    public static enum SortOrder {
        ASCENDING, DESCENDING
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchRequestBuilder)) {
            return false;
        }

        SearchRequestBuilder that = (SearchRequestBuilder) o;

        if (count != null ? !count.equals(that.count) : that.count != null) {
            return false;
        }
        if (startIndex != null ? !startIndex.equals(that.startIndex) : that.startIndex != null) {
            return false;
        }
        if (sortOrder != that.sortOrder) {
            return false;
        }
        if (sortBy != null ? !sortBy.equals(that.sortBy) : that.sortBy != null) {
            return false;
        }
        if (filter != null ? !filter.equals(that.filter) : that.filter != null) {
            return false;
        }
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) {
            return false;
        }
        return excludedAttributes != null ? excludedAttributes.equals(that.excludedAttributes) :
            that.excludedAttributes == null;

    }

    @Override
    public int hashCode() {
        int result = count != null ? count.hashCode() : 0;
        result = 31 * result + (startIndex != null ? startIndex.hashCode() : 0);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        result = 31 * result + (sortBy != null ? sortBy.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (excludedAttributes != null ? excludedAttributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SearchRequestBuilder{" +
            "count=" + count +
            ", startIndex=" + startIndex +
            ", sortOrder=" + sortOrder +
            ", sortBy='" + sortBy + '\'' +
            ", filter='" + filter + '\'' +
            ", attributes='" + attributes + '\'' +
            ", excludedAttributes='" + excludedAttributes + '\'' +
            '}';
    }
}
