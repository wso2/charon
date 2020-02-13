/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.ResourceTypeSchemaConstants.USER_ACCOUNT;
import static org.wso2.charon3.core.schema.SCIMConstants.USER;
import static org.wso2.charon3.core.schema.SCIMConstants.USER_CORE_SCHEMA_URI;

/**
 * The schema resource enables a service
 * provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 */
public class SchemaResourceManager extends AbstractResourceManager {

    private static final Logger log = LoggerFactory.getLogger(SchemaResourceManager.class);

    private static final String ATTRIBUTES = "attributes";

    public SchemaResourceManager() {

    }

    /**
     * Retrieves a SCIM schemas definition.
     *
     * @param id
     * @param userManager
     * @param attributes
     * @param excludeAttributes
     * @return SCIM schemas response.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {

        try {
            List<Attribute> userSchemaAttributes = userManager.getUserSchema();
            return buildSchemasResponse(userSchemaAttributes);
        } catch (BadRequestException | CharonException | NotFoundException | NotImplementedException e) {
            // TODO: 11/7/19 Seperate out user errors & server errors
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /**
     * Builds the SCIM schemas response using the provided user schema attributes.
     *
     * @param userSchemaAttributes
     * @return SCIM schemas response.
     * @throws CharonException
     * @throws NotFoundException
     */
    private SCIMResponse buildSchemasResponse(List<Attribute> userSchemaAttributes) throws CharonException,
            NotFoundException {

        String schemaResponseBody = buildSchemasResponseBody(userSchemaAttributes).toString();
        Map<String, String> responseHeaders = getResponseHeaders();
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, schemaResponseBody, responseHeaders);
    }

    /**
     * Builds the SCIM schemas config json representation using the provided user schema attributes.
     *
     * @param userSchemaAttributeList
     * @return SCIM schemas config json representation.
     * @throws CharonException
     */
    private JSONArray buildSchemasResponseBody(List<Attribute> userSchemaAttributeList) throws CharonException {

        JSONArray rootObject = new JSONArray();
        JSONObject userSchemaObject = buildUserSchema(userSchemaAttributeList);
        rootObject.put(userSchemaObject);
        return rootObject;
    }

    private JSONObject buildUserSchema(List<Attribute> userSchemaAttributeList) throws CharonException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject userSchemaObject = new JSONObject();
            userSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, USER_CORE_SCHEMA_URI);
            userSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, USER);
            userSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, USER_ACCOUNT);

            JSONArray userSchemaAttributeArray = buildUserSchemaAttributeArray(userSchemaAttributeList, encoder);
            userSchemaObject.put(ATTRIBUTES, userSchemaAttributeArray);
            return userSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding user schema", e);
        }
    }

    private JSONArray buildUserSchemaAttributeArray(List<Attribute> userSchemaAttributeList, JSONEncoder encoder)
            throws JSONException {

        JSONArray userSchemaAttributeArray = new JSONArray();

        for (Attribute userShemaAttribute : userSchemaAttributeList) {

            JSONObject userSchemaAttribute;
            if (userShemaAttribute instanceof ComplexAttribute) {
                userSchemaAttribute = encoder.encodeComplexAttributeSchema((ComplexAttribute) userShemaAttribute);
            } else if (userShemaAttribute instanceof SimpleAttribute) {
                userSchemaAttribute = encoder.encodeSimpleAttributeSchema((SimpleAttribute) userShemaAttribute);
            } else {
                userSchemaAttribute = encoder.encodeBasicAttributeSchema(userShemaAttribute);
            }

            userSchemaAttributeArray.put(userSchemaAttribute);
        }

        return userSchemaAttributeArray;
    }

    private Map<String, String> getResponseHeaders() throws NotFoundException {

        Map<String, String> responseHeaders;
        responseHeaders = new HashMap<>();
        responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
        responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(SCIMConstants.SCHEMAS_ENDPOINT));
        return responseHeaders;
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
                                    String sortOrder, String domainName, String attributes, String excludeAttributes) {

        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    /**
     * @param userManager       User manager
     * @param filter            Filter to be executed
     * @param startIndexInt     Starting index value of the filter
     * @param countInt          Number of required results
     * @param sortBy            SortBy
     * @param sortOrder         Sorting order
     * @param domainName        Domain name
     * @param attributes        Attributes in the request
     * @param excludeAttributes Exclude attributes
     * @return SCIM response
     */
    @Override
    public SCIMResponse listWithGET(UserManager userManager, String filter, Integer startIndexInt, Integer countInt,
                                    String sortBy, String sortOrder, String domainName, String attributes,
                                    String excludeAttributes) {

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
}
