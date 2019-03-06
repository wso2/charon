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

import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.PatchOperationUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A client MAY use a URL of the form "<base-uri>/Me" as a uri alias for
 * the User or other resource associated with the currently
 * authenticated subject for any SCIM operation.
 */

public class MeResourceManager extends AbstractResourceManager {


    @Override
    public SCIMResponse get(String userName, UserManager userManager, String attributes, String excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //get the URIs of required attributes which must be given a value

            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);

            /*API user should pass a usermanager impl to UserResourceEndpoint.
            retrieve the user from the provided UM handler.*/
            User user = ((UserManager) userManager).getMe(userName, requiredAttributes);

            //if user not found, return an error in relevant format.
            if (user == null) {
                String error = "User not found in the user store.";
                throw new NotFoundException(error);
            }
            //perform service provider side validation.
            ServerSideValidator.validateRetrievedSCIMObject(user, schema, attributes, excludeAttributes);
            //convert the user into requested format.
            String encodedUser = encoder.encodeSCIMObject(user);
            //if there are any http headers to be added in the response header.
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT) + "/" + user.getId());
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, responseHeaders);

        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException ex) {
            return AbstractResourceManager.encodeSCIMException(new CharonException(ex.getMessage(), ex));
        }
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String
            excludeAttributes) {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();

            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);
            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());
            //validate the created user.
            ServerSideValidator.validateCreatedSCIMObject(user, schema);

            User createdUser;

            if (userManager != null) {
            /*handover the SCIM User object to the user usermanager provided by the SP.
            need to send back the newly created user in the response payload*/
                createdUser = userManager.createMe(user, requiredAttributes);
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> responseHeaders = new HashMap<String, String>();

            if (createdUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(createdUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + createdUser.getId());
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }

            //put the uri of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_CREATED,
                    encodedUser, responseHeaders);

        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException ex) {
            return AbstractResourceManager.encodeSCIMException(new CharonException(ex.getMessage(), ex));
        }
    }

    @Override
    public SCIMResponse delete(String userName, UserManager userManager) {
        JSONEncoder encoder = null;
        try {
            if (userManager != null) {
            /*handover the SCIM User object to the user usermanager provided by the SP for the delete operation*/
                userManager.deleteMe(userName);
                //on successful deletion SCIMResponse only has 204 No Content status code.
                return new SCIMResponse(ResponseCodeConstants.CODE_NO_CONTENT, null, null);
            } else {
                String error = "Provided user manager handler is null.";
                //throw internal server error.
                throw new InternalErrorException(error);
            }
        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException ex) {
            return AbstractResourceManager.encodeSCIMException(new CharonException(ex.getMessage(), ex));
        }
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter,
                                    int startIndex, int count, String sortBy, String sortOrder,
                                    String domainName, String attributes, String excludeAttributes) {
        return null;
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        return null;
    }


    @Override
    public SCIMResponse updateWithPUT(String userName, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        //needs to validate the incoming object. eg: id can not be set by the consumer.

        JSONEncoder encoder = null;
        JSONDecoder decoder = null;

        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder.
            decoder = getDecoder();

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil.getOnlyRequiredAttributesURIs(
                    (SCIMResourceTypeSchema)
                    CopyUtil.deepCopy(schema), attributes, excludeAttributes);
            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

            User updatedUser = null;
            if (userManager != null) {
                //retrieve the old object
                User oldUser = userManager.getMe(userName, ResourceManagerUtil.getAllAttributeURIs(schema));
                if (oldUser != null) {
                    User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject(oldUser, user, schema);
                    updatedUser = userManager.updateMe(validatedUser, requiredAttributes);

                } else {
                    String error = "No user exists with the given userName: " + userName;
                    throw new NotFoundException(error);
                }

            } else {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (updatedUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(updatedUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = encoder.encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + updatedUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated User resource is null.";
                throw new CharonException(error);
            }

            //put the uri of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException ex) {
            return AbstractResourceManager.encodeSCIMException(new CharonException(ex.getMessage(), ex));
        }
    }

    /**
     * Update the user resource by sequence of operations.
     *
     * @param existingId
     * @param scimObjectString
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return
     */

    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager,
                                        String attributes, String excludeAttributes) {
        try {
            if (userManager == null) {
                String error = "Provided user manager handler is null.";
                throw new InternalErrorException(error);
            }
            //obtain the json decoder.
            JSONDecoder decoder = getDecoder();
            //decode the SCIM User object, encoded in the submitted payload.
            List<PatchOperation> opList = decoder.decodeRequest(scimObjectString);

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //get the user from the user core
            User oldUser = userManager.getMe(existingId, ResourceManagerUtil.getAllAttributeURIs(schema));
            if (oldUser == null) {
                throw new NotFoundException("No associated user exits in the user store.");
            }
            //make a copy of the original user
            User copyOfOldUser = (User) CopyUtil.deepCopy(oldUser);
            //make another copy of original user.
            //this will be used to restore to the original condition if failure occurs.
            User originalUser = (User) CopyUtil.deepCopy(copyOfOldUser);

            User newUser = null;

            for (PatchOperation operation : opList) {

                if (operation.getOperation().equals(SCIMConstants.OperationalConstants.ADD)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchAdd
                                (operation, getDecoder(), newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REMOVE)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchRemove(operation, oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchRemove(operation, newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);
                    }
                } else if (operation.getOperation().equals(SCIMConstants.OperationalConstants.REPLACE)) {
                    if (newUser == null) {
                        newUser = (User) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), oldUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);

                    } else {
                        newUser = (User) PatchOperationUtil.doPatchReplace
                                (operation, getDecoder(), newUser, copyOfOldUser, schema);
                        copyOfOldUser = (User) CopyUtil.deepCopy(newUser);
                    }
                } else  {
                    throw new BadRequestException("Unknown operation.", ResponseCodeConstants.INVALID_SYNTAX);
                }
            }

            //get the URIs of required attributes which must be given a value
            Map<String, Boolean> requiredAttributes =
                    ResourceManagerUtil.getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema)
                            CopyUtil.deepCopy(schema), attributes, excludeAttributes);


            User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject
                    (originalUser, newUser, schema);
            newUser = userManager.updateMe(validatedUser, requiredAttributes);

            //encode the newly created SCIM user object and add id attribute to Location header.
            String encodedUser;
            Map<String, String> httpHeaders = new HashMap<String, String>();
            if (newUser != null) {
                //create a deep copy of the user object since we are going to change it.
                User copiedUser = (User) CopyUtil.deepCopy(newUser);
                //need to remove password before returning
                ServerSideValidator.validateReturnedAttributes(copiedUser, attributes, excludeAttributes);
                encodedUser = getEncoder().encodeSCIMObject(copiedUser);
                //add location header
                httpHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.USER_ENDPOINT) + "/" + newUser.getId());
                httpHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Updated User resource is null.";
                throw new CharonException(error);
            }
            //put the URI of the User object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedUser, httpHeaders);

        } catch (AbstractCharonException e) {
            return encodeSCIMException(e);
        } catch (RuntimeException e) {
            CharonException e1 = new CharonException("Error in performing the patch operation on user resource.", e);
            return encodeSCIMException(e1);
        }
    }


    public String getUserName(String scimObjectString) throws CharonException {
        try {
            //obtain the json encoder
            JSONDecoder decoder = getDecoder();
            //obtain the schema corresponding to user
            // unless configured returns core-user schema or else returns extended user schema)
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            //decode the SCIM User object, encoded in the submitted payload.
            User user = (User) decoder.decodeResource(scimObjectString, schema, new User());

            return user.getUserName();

        } catch (BadRequestException | InternalErrorException | CharonException e) {
            throw new CharonException("Error in getting the username from the anonymous request");
        }
    }
}
