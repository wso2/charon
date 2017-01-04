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
package org.wso2.charon3.core.protocol.endpoints;

import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;

/**
 * Interface for SCIM resource endpoints.
 */
public interface ResourceManager {

    /*
     * Method of resource endpoint which is mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param userManager
     * @return SCIMResponse
     */
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes);

    /*
     * Method of resource endpoint which is mapped to HTTP POST request.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param userManager
     * @return SCIMResponse -
     * From Spec: {Since the server is free to alter and/or ignore POSTed content,
     * returning the full representation can be useful to the client, enabling it to correlate the
     * client and server views of the new Resource. When a Resource is created, its uri must be returned
     * in the response Location header.}
     */
    public SCIMResponse create(String scimObjectString, UserManager userManager,
                               String attributes, String excludeAttributes);

    /*
     * Method of the ResourceManager that is mapped to HTTP Delete method..
     *
     * @param id
     * @param userManager
     * @return
     */
    public SCIMResponse delete(String id, UserManager userManager);

    /*
     * get resources
     *
     * @param userManager
     * @param filter
     * @param startIndex
     * @param count
     * @param sortBy
     * @param sortOrder
     * @param attributes
     * @param excludeAttributes
     * @return
     */
    public SCIMResponse listWithGET(UserManager userManager, String filter,
                                    int startIndex, int count, String sortBy, String sortOrder,
                                    String attributes, String excludeAttributes);

    /*
     * query resources
     *
     * @param resourceString
     * @param userManager
     * @return
     */
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager);

    /*
     * To update the user by giving entire attribute set
     *
     * @param existingId
     * @param scimObjectString
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return
     */

    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      UserManager userManager, String attributes, String excludeAttributes);

    /*
     * @param existingId
     * @param scimObjectString
     * @param userManager
     * @return
     */
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString,
                                        UserManager userManager, String attributes, String excludeAttributes);


}
