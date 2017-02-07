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

import org.json.JSONException;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.CopyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The "RESOURCE_TYPE" schema specifies the metadata about a resource type.
 */
public class ResourceTypeResourceManager extends AbstractResourceManager {

    /*
     * Retrieves a resource type
     *
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {
        return getResourceType();
    }

    /*
     * return RESOURCE_TYPE schema
     *
     * @return
     */
    private SCIMResponse getResourceType() {
        JSONEncoder encoder = null;
        try {
            //obtain the json encoder
            encoder = getEncoder();
            //obtain the json decoder
            JSONDecoder decoder = getDecoder();

            // get the service provider config schema
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getResourceTypeResourceSchema();
            //create a string in json format for user resource type with relevant values
            String scimUserObjectString = encoder.buildUserResourceTypeJsonBody();
            //create a string in json format for group resource type with relevant values
            String scimGroupObjectString = encoder.buildGroupResourceTypeJsonBody();
            //build the user abstract scim object
            AbstractSCIMObject userResourceTypeObject = (AbstractSCIMObject) decoder.decodeResource(
                    scimUserObjectString, schema, new AbstractSCIMObject());
            //add meta data
            userResourceTypeObject = ServerSideValidator.validateResourceTypeSCIMObject(userResourceTypeObject);
            //build the group abstract scim object
            AbstractSCIMObject groupResourceTypeObject = (AbstractSCIMObject) decoder.decodeResource(
                    scimGroupObjectString, schema, new AbstractSCIMObject());
            //add meta data
            groupResourceTypeObject = ServerSideValidator.validateResourceTypeSCIMObject(groupResourceTypeObject);
            //build the root abstract scim object
            AbstractSCIMObject resourceTypeObject = buildCombinedResourceType(userResourceTypeObject,
                    groupResourceTypeObject);
            //encode the newly created SCIM Resource Type object.
            String encodedObject;
            Map<String, String> responseHeaders = new HashMap<String, String>();

            if (resourceTypeObject != null) {
                //create a deep copy of the resource type object since we are going to change it.
                AbstractSCIMObject copiedObject = (AbstractSCIMObject) CopyUtil.deepCopy(resourceTypeObject);
                encodedObject = encoder.encodeSCIMObject(copiedObject);
                //add location header
                responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.RESOURCE_TYPE_ENDPOINT));
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            } else {
                String error = "Newly created User resource is null.";
                throw new InternalErrorException(error);
            }
            //put the uri of the resource type object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK,
                    encodedObject, responseHeaders);
        } catch (CharonException e) {
            return encodeSCIMException(e);
        } catch (BadRequestException e) {
            return encodeSCIMException(e);
        } catch (InternalErrorException e) {
            return encodeSCIMException(e);
        } catch (NotFoundException e) {
            return encodeSCIMException(e);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public SCIMResponse create(String scimObjectString, UserManager userManager, String attributes, String
            excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse delete(String id, UserManager userManager) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, int startIndex, int count, String sortBy,
                                    String sortOrder, String attributes, String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse listWithPOST(String resourceString, UserManager userManager) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString, UserManager userManager, String
            attributes, String excludeAttributes) {
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    /*
     * This combines the user and group resource type AbstractSCIMObjects and build a
     * one root AbstractSCIMObjects
     *
     * @param userObject
     * @param groupObject
     * @return
     * @throws CharonException
     */
    private AbstractSCIMObject buildCombinedResourceType(AbstractSCIMObject userObject, AbstractSCIMObject groupObject)
            throws CharonException {
        Map<String, Attribute> userObjectAttributeList = userObject.getAttributeList();
        Map<String, Attribute> groupObjectAttributeList = groupObject.getAttributeList();

        AbstractSCIMObject rootObject = new AbstractSCIMObject();
        MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(
                SCIMConstants.ResourceTypeSchemaConstants.RESOURCE_TYPE);
        ComplexAttribute userComplexAttribute = new ComplexAttribute();

        for (Attribute attribute : userObjectAttributeList.values()) {
            userComplexAttribute.setSubAttribute(attribute);
        }
        multiValuedAttribute.setAttributeValue(userComplexAttribute);
        ComplexAttribute groupComplexAttribute = new ComplexAttribute();
        for (Attribute attribute : groupObjectAttributeList.values()) {
            groupComplexAttribute.setSubAttribute(attribute);
        }
        multiValuedAttribute.setAttributeValue(groupComplexAttribute);
        rootObject.setAttribute(multiValuedAttribute);
        rootObject.setSchema(SCIMConstants.RESOURCE_TYPE_SCHEMA_URI);
        return rootObject;
    }
}
