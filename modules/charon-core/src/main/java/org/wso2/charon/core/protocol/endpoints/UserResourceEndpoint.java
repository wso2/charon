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
import org.wso2.charon.core.objects.*;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMResourceSchema;
import org.wso2.charon.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.ServerSideValidator;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.wso2.charon.core.util.AttributeUtil;
import org.wso2.charon.core.util.CopyUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API exposed by Charon-Core to perform operations on UserResource.
 * Any SCIM service provider can call this API perform relevant CRUD operations on USER ,
 * based on the HTTP requests received by SCIM Client.
 */
public class UserResourceEndpoint extends AbstractResourceEndpoint {
    private Log logger = LogFactory.getLog(UserResourceEndpoint.class);

    /**
     * Retrieves a user resource given an unique user id. Mapped to HTTP GET request.
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

            /*API user should pass a UserManager impl to UserResourceEndpoint.
            retrieve the user from the provided UM handler.*/
            User user = ((UserManager) userManager).getUser(id);

            //if user not found, return an error in relevant format.
            if (user == null) {
                String error = "User not found in the user store.";
                //TODO:log the error.
                throw new ResourceNotFoundException(error);
            }
            
            SCIMResourceSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObject(user, schema);
            //convert the user into requested format.
            String encodedUser = encoder.encodeSCIMObject(user);
            //if there are any http headers to be added in the response header.
            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (FormatNotSupportedException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            //if requested format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            //Inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
    }

    /**
     * Create User in the service provider given the submitted payload that contains the SCIM user
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

        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            //obtain the decoder matching the submitted format.
            Decoder decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

            SCIMResourceSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            
            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

            //validate the created user
            ServerSideValidator.validateCreatedSCIMObject(user, schema);
            /*handover the SCIM User object to the user storage provided by the SP.
            need to send back the newly created user in the response payload*/
            User createdUser = userManager.createUser(user);

            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (createdUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(createdUser);

                //need to remove password before returning
                ServerSideValidator.removePasswordOnReturn(copiedUser);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + createdUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, outputFormat);

            } else {
                //TODO:log the error
                String error = "Newly created User resource is null..";
                throw new InternalServerException(error);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED, encodedUser, httpHeaders);

        } catch (FormatNotSupportedException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            //if the submitted format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            logger.error(e.getDescription(), e.getCause());
            //e.printStackTrace();
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (BadRequestException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (InternalServerException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (DuplicateResourceException e) {
            logger.error(e.getDescription());
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
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

    public SCIMResponse delete(String id, Storage storage, String outputFormat) {
        Encoder encoder = null;
        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            if (storage instanceof UserManager) {
                //delete user
                ((UserManager) storage).deleteUser(id);

            } else {
                String error = "Provided storage handler is not an implementation of UserManager";
                //log the error as well.
                //throw internal server error.
                throw new InternalServerException(error);
            }
            //on successful deletion SCIMResponse only has 200 OK status code.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, null, null);
        } catch (InternalServerException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (FormatNotSupportedException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            e.printStackTrace();
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
    }

    /**
     * Supports listBy userName and externalID
     *
     * @param searchAttribute
     * @param userManager
     * @param format          @return
     */

    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager,
                                        String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public SCIMResponse listByFilter(String filterString, UserManager userManager, String format) {
        Encoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder(SCIMConstants.identifyFormat(format));
            String trimmedFilter = filterString.trim();
            //verify filter string. We currently support only equal operation
            if (!(trimmedFilter.contains("eq") || trimmedFilter.contains("Eq"))) {
                throw new BadRequestException("Given filter operation is not supported.");
            }
            String[] filterParts = null;
            if (trimmedFilter.contains("eq")) {
                filterParts = trimmedFilter.split("eq");
            } else if (trimmedFilter.contains("Eq")) {
                filterParts = trimmedFilter.split("Eq");
            }
            if (filterParts == null || filterParts.length != 2) {
                //filter query param is not properly splitted. Hence Throwing unsupported operation exception:400
                throw new BadRequestException("Filter operation is not recognized.");
            }

            String filterAttribute = filterParts[0];
            String filterOperation = "eq";
            String filterValue = filterParts[1];

            //obtain attributeURI given the attribute name
            String filterAttributeURI = AttributeUtil.getAttributeURI(filterAttribute);

            if (filterAttributeURI == null) {
                throw new BadRequestException("Filter attribute is not supported..");
            }

            List<User> returnedUsers;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedUsers = userManager.listUsersByFilter(filterAttributeURI, filterOperation,
                                                              filterValue);

                //if user not found, return an error in relevant format.
                if (returnedUsers == null || returnedUsers.isEmpty()) {
                    String error = "Users not found in the user store for the filter: " + filterString;
                    //log error.
                    //throw resource not found.
                    throw new ResourceNotFoundException(error);
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(returnedUsers);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, httpHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //log the error as well.
                //throw internal server error.
                throw new InternalServerException(error);
            }

        } catch (FormatNotSupportedException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //if requested format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (InternalServerException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (BadRequestException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
    }


    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager,
                                   String format) {
        //If a value for sortBy is provided and no sortOrder is specified,
        // the sortOrder SHALL default to ascending
        // For all attribute types, if there is no data for the specified sortBy value
        // they are sorted via the 'sortOrder' parameter;
        // i.e., they are ordered last if ascending and first if descending.
        return null;
    }


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

    public SCIMResponse list(UserManager userManager, String format) {
        Encoder encoder = null;
        try {
            //obtain the correct encoder according to the format requested.
            encoder = getEncoder(SCIMConstants.identifyFormat(format));

            List<User> returnedUsers;
            //API user should pass a UserManager storage to UserResourceEndpoint.
            if (userManager != null) {
                returnedUsers = userManager.listUsers();

                //if user not found, return an error in relevant format.
                if (returnedUsers == null || returnedUsers.isEmpty()) {
                    String error = "Users not found in the user store.";
                    //log error.
                    //throw resource not found.
                    throw new ResourceNotFoundException(error);
                }
                //create a listed resource object out of the returned users list.
                ListedResource listedResource = createListedResource(returnedUsers);
                //convert the listed resource into specific format.
                String encodedListedResource = encoder.encodeSCIMObject(listedResource);
                //if there are any http headers to be added in the response header.
                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, format);
                return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedListedResource, httpHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                //log the error as well.
                //throw internal server error.
                throw new InternalServerException(error);
            }

        } catch (FormatNotSupportedException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //if requested format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (InternalServerException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
    }


    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      String inputFormat,
                                      String outputFormat, UserManager userManager
    ) {
        //needs to validate the incoming object. eg: id can not be set by the consumer.

        Encoder encoder = null;
        Decoder decoder = null;

        try {
            //obtain the encoder matching the requested output format.
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            //obtain the decoder matching the submitted format.
            decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));
            
            SCIMResourceSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());
            User updatedUser = null;
            if (userManager != null) {
                //retrieve the old object
                User oldUser = userManager.getUser(existingId);
                if (oldUser != null) {
					User validatedUser =
					                     (User) ServerSideValidator.validateUpdatedSCIMObject(oldUser, user,
					                                                                          schema);
                    updatedUser = userManager.updateUser(validatedUser);

                } else {
                    String error = "No user exists with the given id: " + existingId;
                    //log the error as well.
                    //throw internal server error.
                    throw new ResourceNotFoundException(error);
                }

            } else {
                String error = "Provided user manager handler is null.";
                //log the error as well.
                //throw internal server error.
                throw new InternalServerException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(updatedUser);
                //need to remove password before returning
                ServerSideValidator.removePasswordOnReturn(copiedUser);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + updatedUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, outputFormat);

            } else {
                //TODO:log the error
                String error = "Updated User resource is null..";
                throw new InternalServerException(error);
            }

            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (FormatNotSupportedException e) {
            e.printStackTrace();
            //if the submitted format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            e.printStackTrace();
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (BadRequestException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (InternalServerException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }
    }

    public ListedResource createListedResource(List<User> users)
            throws CharonException, NotFoundException {
        ListedResource listedResource = new ListedResource();
        listedResource.setTotalResults(users.size());
        for (User user : users) {
            //Map<String, Attribute> attributesOfUserResource = new HashMap<String, Attribute>();
            Map<String, Attribute> userAttributes = user.getAttributeList();

            /*attributesOfUserResource.put(SCIMConstants.CommonSchemaConstants.ID,
            user.getAttribute(SCIMConstants.CommonSchemaConstants.ID));
            //
            attributesOfUserResource.put(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID,
            user.getAttribute(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID));
                attributesOfUserResource.put(SCIMConstants.CommonSchemaConstants.META,
            user.getAttribute(SCIMConstants.CommonSchemaConstants.META));*/
            listedResource.setResources(userAttributes);
        }
        return listedResource;
    }

    //TODO: set last modified date with put/patch/bulk update operations.
    //TODO:userName,externalID can not be updated.
    //TODO:should provide a get method to refer to resource with externalID:
    //Consumer to refer to the Resource with its own identifier, obviating the need to store a local
    //mapping between the local identifier of the Resource and the identifier used by the Service Provider
}
