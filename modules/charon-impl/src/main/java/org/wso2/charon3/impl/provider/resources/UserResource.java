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

package org.wso2.charon3.impl.provider.resources;


import io.swagger.annotations.*;
import org.wso2.charon3.impl.provider.util.SCIMProviderConstants;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.FormatNotSupportedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon3.utils.DefaultCharonManager;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Endpoints of the UserResource in micro service. This will basically captures
 * the requests from the remote clients and hand over the request to respective operation performer.
 *
 */

@Api(value = "scim/v2/Users")
@SwaggerDefinition(
        info = @Info(
                title = "/Users Endpoint Swagger Definition", version = "1.0",
                description = "SCIM 2.0 /Users endpoint",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(
                        name = "WSO2 Identity Server Team",
                        email = "vindula@wso2.com",
                        url = "http://wso2.com"
                ))
)
@Path("/scim/v2/Users")
public class UserResource extends AbstractResource {

    @GET
    @Path("/{id}")
    @Produces({"application/json", "application/scim+json"})

    @ApiOperation(
            value = "Return the user with the given id",
            notes = "Returns HTTP 200 if the user is found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid user is found"),
            @ApiResponse(code = 404, message = "Valid user is not found")})

    public Response getUser(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                            @PathParam(SCIMProviderConstants.ID) String id,
                            @ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                            @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user endpoint and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse scimResponse = userResourceManager.get(id, userManager, attribute, excludedAttributes);
            // needs to check the code of the response and return 200 0k or other error codes
            // appropriately.
            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @ApiOperation(
            value = "Return the user which was created",
            notes = "Returns HTTP 201 if the user is successfully created.")

    @POST
    @Produces({"application/json", "application/scim+json"})
    @Consumes("application/scim+json")

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Valid user is created"),
            @ApiResponse(code = 404, message = "User is not found")})

    public Response createUser(@ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                               @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes,
                               String resourceString) throws CharonException, FormatNotSupportedException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user endpoint and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse response = userResourceManager.create(resourceString, userManager,
                    attribute, excludedAttributes);

            return buildResponse(response);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }

    }

    @DELETE
    @Path("/{id}")
    @Produces({"application/json", "application/scim+json"})
    @ApiOperation(
            value = "Delete the user with the given id",
            notes = "Returns HTTP 204 if the user is successfully deleted.")

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User is deleted"),
            @ApiResponse(code = 404, message = "Valid user is not found")})

    public Response deleteUser(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                               @PathParam(SCIMProviderConstants.ID) String id)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user resource manager and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
            // needs to check the code of the response and return 200 0k or other error codes
            // appropriately.
            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @GET
    @Produces({"application/json", "application/scim+json"})
    @ApiOperation(
            value = "Return users according to the filter, sort and pagination parameters",
            notes = "Returns HTTP 404 if the users are not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid users are found"),
            @ApiResponse(code = 404, message = "Valid users are not found")})

    public Response getUser(@ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                            @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes,
                            @ApiParam(value = SCIMProviderConstants.FILTER_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.FILTER) String filter,
                            @ApiParam(value = SCIMProviderConstants.START_INDEX_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.START_INDEX) int startIndex,
                            @ApiParam(value = SCIMProviderConstants.COUNT_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.COUNT) int count,
                            @ApiParam(value = SCIMProviderConstants.SORT_BY_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.SORT_BY) String sortBy,
                            @ApiParam(value = SCIMProviderConstants.SORT_ORDER_DESC, required = false)
                            @QueryParam(SCIMProviderConstants.SORT_ORDER) String sortOrder)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user resource manager and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse scimResponse = userResourceManager.listWithGET(userManager, filter, startIndex, count,
                    sortBy, sortOrder, attribute, excludedAttributes);

            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @POST
    @Path("/.search")
    @Produces({"application/json", "application/scim+json"})
    @Consumes("application/scim+json")

    @ApiOperation(
            value = "Return users according to the filter, sort and pagination parameters",
            notes = "Returns HTTP 404 if the users are not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid users are found"),
            @ApiResponse(code = 404, message = "Valid users are not found")})

    public Response getUsersByPost(String resourceString)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user resource manager and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse scimResponse = userResourceManager.listWithPOST(resourceString, userManager);

            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @PUT
    @Path("{id}")
    @Produces({"application/json", "application/scim+json"})
    @Consumes("application/scim+json")
    @ApiOperation(
            value = "Return the updated user",
            notes = "Returns HTTP 404 if the user is not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User is updated"),
            @ApiResponse(code = 404, message = "Valid user is not found")})

    public Response updateUser(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                               @PathParam(SCIMProviderConstants.ID) String id,
                               @ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                               @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes,
                               String resourceString) throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            UserManager userManager = DefaultCharonManager.getInstance().getUserManager();

            // create charon-SCIM user endpoint and hand-over the request.
            UserResourceManager userResourceManager = new UserResourceManager();

            SCIMResponse response = userResourceManager.updateWithPUT(
                    id, resourceString, userManager, attribute, excludedAttributes);

            return buildResponse(response);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }


}
