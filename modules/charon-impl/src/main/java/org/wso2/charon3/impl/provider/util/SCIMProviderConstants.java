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

package org.wso2.charon3.impl.provider.util;

/**
 * SCIM provider level constants are defined here..
 */
public class SCIMProviderConstants {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ATTRIBUTES = "attributes";
    public static final String EXCLUDE_ATTRIBUTES = "excludedAttributes";
    public static final String FILTER = "filter";
    public static final String START_INDEX = "startIndex";
    public static final String COUNT = "count";
    public static final String SORT_BY = "sortBy";
    public static final String SORT_ORDER = "sortOder";
    public static final String APPLICATION_SCIM_JSON = "application/scim+json";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String ID = "id";
    public static final String DOMAIN = "domain";

    public static final String RESOURCE_STRING = "RESOURCE_STRING";
    public static final String HTTP_VERB = "HTTP_VERB";
    public static final String SEARCH = ".search";

    public static final String ACCEPT_HEADER_DESC =
            "Specify media types which are acceptable for the response.";
    public static final String CONTENT_TYPE_HEADER_DESC =
            "Indicates the media type of the entity-body sent to the recipient.";
    public static final String ID_DESC = "Unique id of the resource type.";
    public static final String ATTRIBUTES_DESC = "SCIM defined attributes parameter.";
    public static final String EXCLUDED_ATTRIBUTES_DESC = "SCIM defined excludedAttribute parameter.";
    public static final String FILTER_DESC = "Filter expression for filtering";
    public static final String COUNT_DESC = "Specifies the desired maximum number of query results per page.";
    public static final String SORT_BY_DESC = "Specifies the attribute whose value\n" +
            "SHALL be used to order the returned responses";
    public static final String SORT_ORDER_DESC = "The order in which the \"sortBy\" parameter is applied.";
    public static final String START_INDEX_DESC = "The 1-based index of the first query result";
    public static final String DOMAIN_DESC = "Domain of the provisioning user";

}
