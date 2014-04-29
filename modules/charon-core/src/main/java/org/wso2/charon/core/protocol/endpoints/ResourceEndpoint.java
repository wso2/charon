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
package org.wso2.charon.core.protocol.endpoints;

import org.wso2.charon.core.extensions.Storage;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.protocol.SCIMResponse;

/**
 * Interface for SCIM resource endpoints.
 */
public interface ResourceEndpoint {

    /**
     * Method of resource endpoint which is mapped to HTTP GET request.
     *
     * @param id      - unique resource id
     * @param format  - format mentioned in HTTP Content-Type header.
     * @param userManager
     * @return SCIMResponse
     */
    public SCIMResponse get(String id, String format, UserManager userManager);

    /**
     * Method of resource endpoint which is mapped to HTTP POST request.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param inputFormat      - format mentioned in HTTP Content-Type header.
     * @param outputFormat     - format mentioned in HTTP Accept header.
     * @param userManager
     * @return SCIMResponse -
     *         From Spec: {Since the server is free to alter and/or ignore POSTed content,
     *         returning the full representation can be useful to the client, enabling it to correlate the
     *         client and server views of the new Resource. When a Resource is created, its URI must be returned
     *         in the response Location header.}
     */
    public SCIMResponse create(String scimObjectString, String inputFormat, String outputFormat,
                               UserManager userManager);

    /**
     * Method of the ResourceEndpoint that is mapped to HTTP Delete method..
     *
     * @param id
     * @param storage
     * @param outputFormat - required to encode exceptions if any
     * @return
     */
    public SCIMResponse delete(String id, Storage storage, String outputFormat);

    /**
     * Method that maps to HTTP GET with URL query parameter: "attributes=attributeName"
     * This is to list resources with the given attribute name
     *
     * @param searchAttribute
     * @param userManager
     * @param format          @return
     */
    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager,
                                        String format);

    /**
     * Method that maps to HTTP GET with URL query parameter: "filter=filterString"
     * This is to filter a sub set of resources mating the filter string
     *
     * @param filterString
     * @param userManager
     * @param format       @return
     */
    public SCIMResponse listByFilter(String filterString, UserManager userManager, String format);

    /**
     * Method that maps to HTTP GET with URL query parameter: "sortBy=attributeName&sortOrder=ascending"
     * This is to sort the resources in the given criteria
     *
     * @param sortBy
     * @param sortOrder
     * @param usermanager
     * @param format      @return
     */
    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager,
                                   String format);

    /**
     * Method that maps to HTTP GET with URL query parameter: "startIndex=1&count=10"
     * This is to retrieve only a set of resources without overwhelming SP or consumer.
     *
     * @param startIndex
     * @param count
     * @param userManager
     * @param format      @return
     */
    public SCIMResponse listWithPagination(int startIndex, int count, UserManager userManager,
                                           String format);

    /**
     * To list all the resources of resource endpoint.
     * @param userManager
     * @param format
     * @return
     */
    public SCIMResponse list(UserManager userManager, String format);
    
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      String inputFormat,
                                      String outputFormat,
                                      UserManager userManager);


}
