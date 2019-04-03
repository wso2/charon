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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;


import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.FormatNotSupportedException;
import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;
import org.wso2.charon3.impl.provider.util.SCIMProviderConstants;
import org.wso2.charon3.utils.DefaultCharonManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Response;

/**
 * Endpoints of the GroupResource in micro service. This will basically captures
 * the requests from the remote clients and hand over the request to respective operation performer.
 *
 */

@Api(value = "scim/v2/Groups")
@SwaggerDefinition(
        info = @Info(
                title = "/Groups Endpoint Swagger Definition", version = "1.0",
                description = "SCIM 2.0 /Groups endpoint",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(
                        name = "WSO2 Identity Server Team",
                        email = "vindula@wso2.com",
                        url = "http://wso2.com"
                ))
)
@Path("/scim/v2/Groups")
public class GroupResource extends AbstractResource {

    @GET
    @Path("/{id}")
    @Produces({"application/json", "application/scim+json"})
    @ApiOperation(
            value = "Return the group with the given id",
            notes = "Returns HTTP 200 if the group is found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid group is found"),
            @ApiResponse(code = 404, message = "Valid group is not found")})

    public Response getGroup(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                             @PathParam(SCIMProviderConstants.ID) String id,
                             @ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                             @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                             @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                             @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse scimResponse = groupResourceManager.get(id, attribute, excludedAttributes);
            // needs to check the code of the response and return 201 Ok or other error codes
            // appropriately.
            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }

    }

    @ApiOperation(
            value = "Return the group which was created",
            notes = "Returns HTTP 201 if the group is successfully created.")

    @POST
    @Produces({"application/json", "application/scim+json"})
    @Consumes("application/scim+json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Valid group is created"),
            @ApiResponse(code = 404, message = "Group is not found")})

    public Response createGroup(@ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                                @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                                @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                                @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes,
                                String resourceString) throws CharonException, FormatNotSupportedException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse response = groupResourceManager.create(resourceString, attribute, excludedAttributes);

            return buildResponse(response);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }

    }

    @DELETE
    @Path("/{id}")
    @Produces({"application/json", "application/scim+json"})
    @ApiOperation(
            value = "Delete the group with the given id",
            notes = "Returns HTTP 204 if the group is successfully deleted.")

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Group is deleted"),
            @ApiResponse(code = 404, message = "Valid group is not found")})

    public Response deleteGroup(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                               @PathParam(SCIMProviderConstants.ID) String id)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse scimResponse = groupResourceManager.delete(id);
            // needs to check the code of the response and return 200 0k or other error codes
            // appropriately.
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
            value = "Return the updated group",
            notes = "Returns HTTP 404 if the group is not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group is updated"),
            @ApiResponse(code = 404, message = "Valid group is not found")})

    public Response updateGroup(@ApiParam(value = SCIMProviderConstants.ID_DESC, required = true)
                               @PathParam(SCIMProviderConstants.ID) String id,
                               @ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.ATTRIBUTES) String attribute,
                               @ApiParam(value = SCIMProviderConstants.EXCLUDED_ATTRIBUTES_DESC, required = false)
                               @QueryParam(SCIMProviderConstants.EXCLUDE_ATTRIBUTES) String excludedAttributes,
                               String resourceString) throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse response = groupResourceManager.updateWithPUT(id, resourceString, attribute,
                                                                       excludedAttributes);

            return buildResponse(response);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @POST
    @Path("/.search")
    @Produces({"application/json", "application/scim+json"})
    @Consumes("application/scim+json")

    @ApiOperation(
            value = "Return groups according to the filter, sort and pagination parameters",
            notes = "Returns HTTP 404 if the groups are not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid groups are found"),
            @ApiResponse(code = 404, message = "Valid groups are not found")})

    public Response getGroupsByPost(String resourceString)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse scimResponse = groupResourceManager.listWithPOST(resourceString);

            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    @GET
    @Produces({"application/json", "application/scim+json"})

    @ApiOperation(
            value = "Return groups according to the filter, sort and pagination parameters",
            notes = "Returns HTTP 404 if the groups are not found.")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valid groups are found"),
            @ApiResponse(code = 404, message = "Valid groups are not found")})

    public Response getGroup(@ApiParam(value = SCIMProviderConstants.ATTRIBUTES_DESC, required = false)
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
                             @QueryParam(SCIMProviderConstants.SORT_ORDER) String sortOrder,
                             @ApiParam(value = SCIMProviderConstants.DOMAIN_DESC, required = false)
                             @QueryParam(value = SCIMProviderConstants.DOMAIN) String domainName)
            throws FormatNotSupportedException, CharonException {

        try {
            // obtain the user store manager
            ResourceHandler<Group> groupHandler = DefaultCharonManager.getInstance().getGroupResourceHandler();

            // create charon-SCIM group endpoint and hand-over the request.
            GroupResourceManager groupResourceManager = new GroupResourceManager(groupHandler);

            SCIMResponse scimResponse = groupResourceManager.listWithGET(filter, startIndex, count,
                    sortBy, sortOrder, domainName, attribute, excludedAttributes);

            return buildResponse(scimResponse);

        } catch (CharonException e) {
            throw new CharonException(e.getDetail(), e);
        }
    }

    /**
     * the group resource manager implementation
     */
    public static class GroupResourceManager extends ResourceManager<Group> {

        public GroupResourceManager(ResourceHandler<Group> resourceHandler) {
            super(resourceHandler);
        }
    }
}

