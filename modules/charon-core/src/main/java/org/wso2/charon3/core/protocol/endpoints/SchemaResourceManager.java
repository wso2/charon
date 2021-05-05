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

import org.apache.commons.lang.StringUtils;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.CUSTOM_USER;
import static org.wso2.charon3.core.schema.SCIMConstants.CUSTOM_USER_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.CustomUserSchemaConstants.CUSTOM_USER_DESC;
import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER;
import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.EnterpriseUserSchemaConstants.ENTERPRISE_USER_DESC;
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
            List<Attribute> userEnterpriseSchemaAttributes = userManager.getEnterpriseUserSchema();
            List<Attribute> userCustomSchemaAttributes = userManager.getCustomUserSchema();

            Map<String, List<Attribute>> schemas = new HashMap<>();
            if (StringUtils.isBlank(id)) {
                schemas.put(USER_CORE_SCHEMA_URI, userSchemaAttributes);
                schemas.put(ENTERPRISE_USER_SCHEMA_URI, userEnterpriseSchemaAttributes);
                schemas.put(CUSTOM_USER_SCHEMA_URI, userCustomSchemaAttributes);
                return buildSchemasResponse(schemas);
            }
            switch (id) {
                case USER_CORE_SCHEMA_URI:
                    schemas.put(USER_CORE_SCHEMA_URI, userSchemaAttributes);
                    break;
                case ENTERPRISE_USER_SCHEMA_URI:
                    schemas.put(ENTERPRISE_USER_SCHEMA_URI, userEnterpriseSchemaAttributes);
                    break;
                case CUSTOM_USER_SCHEMA_URI:
                    schemas.put(CUSTOM_USER_SCHEMA_URI, userCustomSchemaAttributes);
                    break;
                default:
                    // https://tools.ietf.org/html/rfc7643#section-8.7
                    throw new NotImplementedException("only user, enterprise and custom schema are supported");
            }
            return buildSchemasResponse(schemas);
        } catch (BadRequestException | CharonException | NotFoundException | NotImplementedException e) {
            // TODO: 11/7/19 Seperate out user errors & server errors
            return AbstractResourceManager.encodeSCIMException(e);
        }
    }

    /**
     * Builds the SCIM schemas response using the provided schemas.
     *
     * @param schemas Map of retrieved SCIM schemas
     * @return SCIM schemas response.
     * @throws CharonException
     * @throws NotFoundException
     */
    private SCIMResponse buildSchemasResponse(Map<String, List<Attribute>> schemas) throws CharonException,
            NotFoundException {

        String schemaResponseBody = buildSchemasResponseBody(schemas).toString();
        Map<String, String> responseHeaders = getResponseHeaders();
        return new SCIMResponse(ResponseCodeConstants.CODE_OK, schemaResponseBody, responseHeaders);
    }

    /**
     * Builds the SCIM schemas config json representation using the provided schemas.
     *
     * @param schemas Map of retrieved schemas
     * @return SCIM schemas config json representation.
     * @throws CharonException
     */
    private JSONArray buildSchemasResponseBody(Map<String, List<Attribute>> schemas) throws CharonException {

        JSONArray rootObject = new JSONArray();
        if (schemas.get(USER_CORE_SCHEMA_URI) != null) {
            JSONObject userSchemaObject = buildUserSchema(schemas.get(USER_CORE_SCHEMA_URI));
            rootObject.put(userSchemaObject);
        }
        if (schemas.get(ENTERPRISE_USER_SCHEMA_URI) != null) {
            JSONObject enterpriseUserSchemaObject = buildEnterpriseUserSchema(schemas.get(ENTERPRISE_USER_SCHEMA_URI));
            rootObject.put(enterpriseUserSchemaObject);
        }
        if (schemas.get(CUSTOM_USER_SCHEMA_URI) != null) {
            JSONObject customUserSchemaObject = buildCustomUserSchema(schemas.get(CUSTOM_USER_SCHEMA_URI));
            rootObject.put(customUserSchemaObject);
        }
        return rootObject;
    }

    /**
     * Builds a JSON object containing enterprise user schema attribute information.
     *
     * @param enterpriseUserSchemaList Attribute list of SCIM enterprise user schema
     * @return JSON object of enterprise user schema
     * @throws CharonException
     */
    private JSONObject buildEnterpriseUserSchema(List<Attribute> enterpriseUserSchemaList) throws CharonException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject enterpriseUserSchemaObject = new JSONObject();
            enterpriseUserSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, ENTERPRISE_USER_SCHEMA_URI);
            enterpriseUserSchemaObject.put(SCIMConstants.EnterpriseUserSchemaConstants.NAME, ENTERPRISE_USER);
            enterpriseUserSchemaObject.put(SCIMConstants.
                    EnterpriseUserSchemaConstants.DESCRIPTION, ENTERPRISE_USER_DESC);

            JSONArray enterpriseUserAttributeArray = buildSchemaAttributeArray(enterpriseUserSchemaList, encoder);
            enterpriseUserSchemaObject.put(ATTRIBUTES, enterpriseUserAttributeArray);
            return enterpriseUserSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding enterprise user schema.", e);
        }
    }

    private JSONObject buildCustomUserSchema(List<Attribute> customUserSchemaList) throws CharonException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject customUserSchemaObject = new JSONObject();
            customUserSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, CUSTOM_USER_SCHEMA_URI);
            customUserSchemaObject.put(SCIMConstants.CustomUserSchemaConstants.NAME, CUSTOM_USER);
            customUserSchemaObject.put(SCIMConstants.CustomUserSchemaConstants.DESCRIPTION, CUSTOM_USER_DESC);

            JSONArray customUserAttributeArray = buildSchemaAttributeArray(customUserSchemaList, encoder);
            customUserSchemaObject.put(ATTRIBUTES, customUserAttributeArray);
            return customUserSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding custom user schema.", e);
        }
    }

    private JSONObject buildUserSchema(List<Attribute> userSchemaAttributeList) throws CharonException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject userSchemaObject = new JSONObject();
            userSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, USER_CORE_SCHEMA_URI);
            userSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, USER);
            userSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, USER_ACCOUNT);

            JSONArray userSchemaAttributeArray = buildSchemaAttributeArray(userSchemaAttributeList, encoder);
            userSchemaObject.put(ATTRIBUTES, userSchemaAttributeArray);
            return userSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding user schema", e);
        }
    }

    private JSONArray buildSchemaAttributeArray(List<Attribute> schemaAttributeList, JSONEncoder encoder)
            throws JSONException {

        JSONArray schemaAttributeArray = new JSONArray();

        for (Attribute schemaAttribute : schemaAttributeList) {

            JSONObject schemaJSONAttribute;
            if (schemaAttribute instanceof ComplexAttribute) {
                schemaJSONAttribute = encoder.encodeComplexAttributeSchema((ComplexAttribute) schemaAttribute);
            } else if (schemaAttribute instanceof SimpleAttribute) {
                schemaJSONAttribute = encoder.encodeSimpleAttributeSchema((SimpleAttribute) schemaAttribute);
            } else {
                schemaJSONAttribute = encoder.encodeBasicAttributeSchema(schemaAttribute);
            }

            schemaAttributeArray.put(schemaJSONAttribute);
        }

        return schemaAttributeArray;
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

        try {
            if (!StringUtils.equals(existingId, CUSTOM_USER_SCHEMA_URI)) {
                throw new NotImplementedException("Updating attribute of custom schema is supported");
            }
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
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

    @Override
    public SCIMResponse create(String schemaId, String tenantDomain, String scimObjectString, UserManager userManager, String attributes,
                                String excludeAttributes) {

        try {
            if (!StringUtils.equals(schemaId, CUSTOM_USER_SCHEMA_URI)) {
                throw new NotImplementedException("Creating attributes in " + schemaId + " is not supported");
            }
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }

    @Override
    public SCIMResponse delete(String schemaId, String tenantDomain, String attributeUri, UserManager userManager){

        try {
            if (!StringUtils.equals(schemaId, CUSTOM_USER_SCHEMA_URI)) {
                throw new NotImplementedException("Deleting attributes in " + schemaId + " is not supported");
            }
        } catch (NotImplementedException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        }
        String error = "Request is undefined";
        BadRequestException badRequestException = new BadRequestException(error, ResponseCodeConstants.INVALID_PATH);
        return encodeSCIMException(badRequestException);
    }
}
