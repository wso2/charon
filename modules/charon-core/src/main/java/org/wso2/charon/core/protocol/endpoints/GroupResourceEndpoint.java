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

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.DuplicateResourceException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.exceptions.InternalServerException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.exceptions.ResourceNotFoundException;
import org.wso2.charon.core.extensions.Storage;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.ServerSideValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.util.AttributeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST API exposed by Charon-Core to perform operations on UserResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on USER ,
 * based on the HTTP requests received by SCIM Client.
 */
public class GroupResourceEndpoint extends AbstractResourceEndpoint implements ResourceEndpoint {
    private Log logger = LogFactory.getLog(GroupResourceEndpoint.class);

    /**
     * Retrieves a group resource given an unique group id. Mapped to HTTP GET request.
     *
     * @param id          - unique resource id
     * @param format      - requested format of the response.
     * @param userManager
     * @return SCIM response to be returned.
     */
    public SCIMResponse get(String id, String format, UserManager userManager) {

        Encoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder(SCIMConstants.identifyFormat(format));

            //API user should pass a UserManager storage to UserResourceEndpoint.
            //retrieve the user from the provided storage.
            Group group = ((UserManager) userManager).getGroup(id);

            //TODO:needs a validator to see that the User returned by the custom user manager
            // adheres to SCIM spec.

            //if user not found, return an error in relevant format.
            if (group == null) {
                String message = "Group not found in the user store.";
                throw new ResourceNotFoundException(message);
            }
            ServerSideValidator.validateRetrievedSCIMObject(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
            //convert the user into specific format.
            String encodedGroup = encoder.encodeSCIMObject(group);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);
        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    /**
     * Create group in the service provider given the submitted payload that contains the SCIM group
     * resource, format and the handler to storage.
     *
     * @param scimObjectString - Payload of HTTP request, which contains the SCIM object.
     * @param inputFormat      - format of the submitted content
     * @param outputFormat     - format mentioned in HTTP Accept header.
     * @param userManager
     * @return
     */
    public SCIMResponse create(String scimObjectString, String inputFormat, String outputFormat,
                               UserManager userManager) {

        //needs to validate the incoming object. eg: id can not be set by the consumer.

        Encoder encoder = null;
        Decoder decoder = null;

        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            //obtain the decoder matching the submitted format.
            decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

            //decode the SCIM group object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString,
                                                         SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                                                         new Group());
            //validate decoded group
            ServerSideValidator.validateCreatedSCIMObject(group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
            //handover the SCIM User object to the group storage provided by the SP.
            Group createdGroup;
            //need to send back the newly created group in the response payload
            createdGroup = ((UserManager) userManager).createGroup(group);

            //encode the newly created SCIM group object and add id attribute to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (createdGroup != null) {

                encodedGroup = encoder.encodeSCIMObject(createdGroup);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.GROUP_ENDPOINT) + "/" + createdGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, outputFormat);

            } else {
                String message = "Newly created Group resource is null..";
                throw new InternalServerException(message);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedGroup, httpHeaders);

        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    /**
     * Method of the ResourceEndpoint that is mapped to HTTP Delete method..
     *
     * @param id
     * @param storage
     * @param outputFormat
     * @return
     */
    @Override
    public SCIMResponse delete(String id, Storage storage, String outputFormat) {
        Encoder encoder = null;
        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            if (storage instanceof UserManager) {
                //delete group
                ((UserManager) storage).deleteGroup(id);

            } else {
                String message = "Provided storage handler is not an implementation of UserManager";
                throw new InternalServerException(message);
            }
            //on successful deletion SCIMResponse only has 200 OK status code.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, null, null);
        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    /**
     * Supports list by displayName and externalID
     *
     * @param searchAttribute
     * @param userManager
     * @param format          @return
     */
    @Override
    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager,
                                        String format) {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listByFilter(String filterString, UserManager userManager, String format) {
        Encoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder(SCIMConstants.identifyFormat(format));
            String trimmedFilter = filterString.trim();
            //verify filter string. We currently support only equal operation
            if (!(trimmedFilter.contains("eq") || trimmedFilter.contains("Eq"))) {
                String message = "Given filter operation is not supported.";
                throw new BadRequestException(message);
            }
            String[] filterParts = null;
            if (trimmedFilter.contains("eq")) {
                filterParts = trimmedFilter.split("eq");
            } else if (trimmedFilter.contains("Eq")) {
                filterParts = trimmedFilter.split("Eq");
            }
            if (filterParts == null || filterParts.length != 2) {
                //filter query param is not properly splitted. Hence Throwing unsupported operation exception:400
                String message = "Filter operation is not recognized";
                throw new BadRequestException(message);
            }

            String filterAttribute = filterParts[0].trim();
            String filterOperation = "eq";
            String filterValue = filterParts[1].trim();
            if (filterValue.charAt(0) == '\"') {
                filterValue = filterValue.substring(1, filterValue.length() - 1);
            }

            //obtain attributeURI given the attribute name
            String filterAttributeURI = AttributeUtil.getAttributeURI(filterAttribute);

            if (filterAttributeURI == null) {
                String message = "Filter attribute is not supported..";
                throw new BadRequestException(message);
            }
            List<Group> returnedGroups;
            //API user should pass a UserManager storage to GroupResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroupsByFilter(filterAttribute, filterOperation,
                                                                filterValue);

                //if Group not found, return an error in relevant format.
                if ((returnedGroups == null) || (returnedGroups.isEmpty())) {
                    String message = "Groups not found in the user store for the filter: " + filterString;
                    throw new ResourceNotFoundException(message);
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(returnedGroups);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, httpHeaders);

            } else {
                String message = "Provided user manager handler is null.";
                throw new InternalServerException(message);
            }

        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    @Override
    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager,
                                   String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listWithPagination(int startIndex, int count, UserManager userManager,
                                           String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * To list all the resources of resource endpoint.
     *
     * @param userManager
     * @param format
     * @return
     */
    @Override
    public SCIMResponse list(UserManager userManager, String format) {
        Encoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder(SCIMConstants.identifyFormat(format));

            List<Group> returnedGroups;
            //API user should pass a UserManager storage to GroupResourceEndpoint.
            if (userManager != null) {
                returnedGroups = userManager.listGroups();

                //if Group not found, return an error in relevant format.
                if ((returnedGroups == null) || (returnedGroups.isEmpty())) {
                    String message = "Groups not found in the user store.";
                    throw new ResourceNotFoundException(message);
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(returnedGroups);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, httpHeaders);

            } else {
                String message = "Provided user manager handler is null.";
                throw new InternalServerException(message);
            }

        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    /**
     * complete update of group resource
     * @param existingId
     * @param scimObjectString
     * @param inputFormat
     * @param outputFormat
     * @param userManager
     * @return
     */
    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, String inputFormat,
                                      String outputFormat, UserManager userManager) {
        return updateGroup(existingId, scimObjectString, inputFormat, outputFormat, userManager,
                           SCIMConstants.UPDATE_WITH_PUT);
    }

    /**
     * partial update of group resource
     * @param existingId
     * @param scimObjectString
     * @param inputFormat
     * @param outputFormat
     * @param userManager
     * @return
     */
    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, String inputFormat,
                                        String outputFormat, UserManager userManager) {
        return updateGroup(existingId, scimObjectString, inputFormat, outputFormat, userManager,
                           SCIMConstants.UPDATE_WITH_PATCH);
    }

    private ListedResource createListedResource(List<Group> groups) throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setTotalResults(groups.size());
        for (Group group : groups) {
            if (group != null) {
                Map<String, Attribute> attributesOfGroupResource = group.getAttributeList();
                listedResource.setResources(attributesOfGroupResource);
            }
        }
        return listedResource;
    }

    private SCIMResponse updateGroup(String existingId, String scimObjectString, String inputFormat,
                                     String outputFormat, UserManager userManager, String updateType){
        Encoder encoder = null;
        Decoder decoder = null;

        try {
            // obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            // obtain the decoder matching the submitted format.
            decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

            // decode the SCIM Group object, encoded in the submitted payload.
            Group group = (Group) decoder.decodeResource(scimObjectString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                                                         new Group());
            Group updatedGroup = null;
            if (userManager != null) {
                // retrieve the old object
                Group oldGroup = userManager.getGroup(existingId);
                if (oldGroup != null) {
                    Group validatedGroup = (Group) ServerSideValidator.
                            validateUpdatedSCIMObject(oldGroup, group, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
                    if(SCIMConstants.UPDATE_WITH_PUT.equals(updateType)){
                        updatedGroup = userManager.updateGroup(oldGroup, validatedGroup);
                    }else if(SCIMConstants.UPDATE_WITH_PATCH.equals(updateType)){
                        updatedGroup = userManager.patchGroup(oldGroup, validatedGroup);
                    }

                } else {
                    String message = "No group exists with the given id: " + existingId;
                    throw new ResourceNotFoundException(message);
                }

            } else {
                String message = "Provided User manager handler is null.";
                throw new InternalServerException(message);
            }
            // encode the newly created SCIM group object and add id attribute
            // to Location header.
            String encodedGroup;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedGroup != null) {

                encodedGroup = encoder.encodeSCIMObject(updatedGroup);
                // add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(SCIMConstants.USER_ENDPOINT) +
                                                               "/" + updatedGroup.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, outputFormat);

            } else {
                String message = "Updated User resource is null..";
                throw new InternalServerException(message);
            }

            // put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedGroup, httpHeaders);

        } catch (Exception e) {
            return handleException(e, encoder);
        }
    }

    private SCIMResponse handleException(Exception e, Encoder encoder) {

        try {
            if (e instanceof FormatNotSupportedException) {
                throw (FormatNotSupportedException) e;
            } else if (e instanceof CharonException) {
                throw (CharonException) e;
            } else if (e instanceof BadRequestException) {
                throw (BadRequestException) e;
            } else if (e instanceof InternalServerException) {
                throw (InternalServerException) e;
            } else if (e instanceof DuplicateResourceException) {
                throw (DuplicateResourceException) e;
            } else {
                throw (ResourceNotFoundException) e;
            }
        } catch (FormatNotSupportedException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            // if the submitted format not supported, encode exception and set
            // it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        } catch (CharonException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            // we have charon exceptions also, instead of having only internal
            // server error
            // exceptions,
            // because inside API code throws CharonException.
            if (ex.getCode() == -1) {
                ex.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        } catch (BadRequestException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        } catch (InternalServerException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        } catch (DuplicateResourceException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        } catch (ResourceNotFoundException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, ex);
        }
    }
}
