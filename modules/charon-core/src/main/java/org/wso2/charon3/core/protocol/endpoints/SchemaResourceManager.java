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
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
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
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER;
import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.EnterpriseUserSchemaConstants.ENTERPRISE_USER_DESC;
import static org.wso2.charon3.core.schema.SCIMConstants.GROUP_CORE_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.ResourceTypeSchemaConstants.GROUP;
import static org.wso2.charon3.core.schema.SCIMConstants.ResourceTypeSchemaConstants.SCHEMA;
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
            List<Attribute> groupSchemaAttributes = userManager.getGroupSchema();

            Map<String, List<Attribute>> schemas = new HashMap<>();
            schemas.put(USER_CORE_SCHEMA_URI, userSchemaAttributes);
            schemas.put(GROUP_CORE_SCHEMA_URI, groupSchemaAttributes);
            schemas.put(ENTERPRISE_USER_SCHEMA_URI, userEnterpriseSchemaAttributes);

            JSONArray extSchemasArr = userManager.getCustomExtensionSchemas();

            return buildSchemasResponse(schemas, extSchemasArr);
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
    private SCIMResponse buildSchemasResponse(Map<String, List<Attribute>> schemas, JSONArray extSchemasJson)
            throws CharonException, NotFoundException, BadRequestException {

        String schemaResponseBody = buildSchemasResponseBody(schemas, extSchemasJson).toString();
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
    private JSONObject buildSchemasResponseBody(Map<String, List<Attribute>> schemas, JSONArray extSchemasArr)
            throws CharonException, BadRequestException, NotFoundException {

        JSONObject rootObject = new JSONObject();
        rootObject.put(SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS, extSchemasArr.length() + 3);

        JSONArray schemaAttributeArray = new JSONArray();
        schemaAttributeArray.put(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        rootObject.put(SCIMConstants.CommonSchemaConstants.SCHEMAS, schemaAttributeArray);

        JSONArray resourcesRootObject = new JSONArray();
        if (schemas.get(USER_CORE_SCHEMA_URI) != null) {
            JSONObject userSchemaObject = buildUserSchema(schemas.get(USER_CORE_SCHEMA_URI));
            resourcesRootObject.put(userSchemaObject);
        }

        if (schemas.get(GROUP_CORE_SCHEMA_URI) != null) {
            JSONObject groupSchemaObject = buildGroupSchema(schemas.get(GROUP_CORE_SCHEMA_URI));
            resourcesRootObject.put(groupSchemaObject);
        }

        if (schemas.get(ENTERPRISE_USER_SCHEMA_URI) != null) {
            JSONObject enterpriseUserSchemaObject = buildEnterpriseUserSchema(schemas.get(ENTERPRISE_USER_SCHEMA_URI));
            resourcesRootObject.put(enterpriseUserSchemaObject);
        }

        for (int count = 0; count < extSchemasArr.length(); count++) {
            resourcesRootObject.put(extSchemasArr.getJSONObject(count));
        }

        rootObject.put(SCIMConstants.ListedResourceSchemaConstants.RESOURCES, resourcesRootObject);

        return rootObject;
    }

    /**
     * Builds a JSON object containing enterprise user schema attribute information.
     *
     * @param enterpriseUserSchemaList Attribute list of SCIM enterprise user schema
     * @return JSON object of enterprise user schema
     * @throws CharonException
     */
    private JSONObject buildEnterpriseUserSchema(List<Attribute> enterpriseUserSchemaList) throws CharonException,
            BadRequestException, NotFoundException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject enterpriseUserSchemaObject = new JSONObject();
            enterpriseUserSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, ENTERPRISE_USER_SCHEMA_URI);
            enterpriseUserSchemaObject.put(SCIMConstants.EnterpriseUserSchemaConstants.NAME, ENTERPRISE_USER);
            enterpriseUserSchemaObject.put(SCIMConstants.
                    EnterpriseUserSchemaConstants.DESCRIPTION, ENTERPRISE_USER_DESC);

            JSONArray enterpriseUserAttributeArray = buildSchemaAttributeArray(enterpriseUserSchemaList, encoder,
                    ENTERPRISE_USER_SCHEMA_URI);
            enterpriseUserSchemaObject.put(ATTRIBUTES, enterpriseUserAttributeArray);
            return enterpriseUserSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding enterprise user schema.", e);
        }
    }

    private JSONObject buildUserSchema(List<Attribute> userSchemaAttributeList) throws CharonException,
            BadRequestException, NotFoundException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject userSchemaObject = new JSONObject();
            userSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, USER_CORE_SCHEMA_URI);
            userSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, USER);
            userSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, USER_ACCOUNT);

            JSONArray userSchemaAttributeArray = buildSchemaAttributeArray(userSchemaAttributeList, encoder,
                    USER_CORE_SCHEMA_URI);
            userSchemaObject.put(ATTRIBUTES, userSchemaAttributeArray);
            return userSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding user schema", e);
        }
    }

    private JSONObject buildGroupSchema(List<Attribute> groupSchemaAttributeList) throws CharonException,
            BadRequestException, NotFoundException {

        try {
            JSONEncoder encoder = getEncoder();

            JSONObject groupSchemaObject = new JSONObject();
            groupSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, GROUP_CORE_SCHEMA_URI);
            groupSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, GROUP);
            groupSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, GROUP);

            JSONArray groupSchemaAttributeArray = buildSchemaAttributeArray(groupSchemaAttributeList, encoder,
                    GROUP_CORE_SCHEMA_URI);
            groupSchemaObject.put(ATTRIBUTES, groupSchemaAttributeArray);
            return groupSchemaObject;
        } catch (JSONException e) {
            throw new CharonException("Error while encoding user schema", e);
        }
    }

    private JSONArray buildSchemaAttributeArray(List<Attribute> schemaAttributeList, JSONEncoder encoder,
                                                String schemaURI)
            throws JSONException, CharonException, BadRequestException, NotFoundException {

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

        ComplexAttribute metaAttribute = createMetaAttribute();
        String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                SCIMConstants.SCHEMAS_ENDPOINT), schemaURI);
        SimpleAttribute locationAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.LOCATION,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.LOCATION, location));
        SimpleAttribute resourceTypeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.RESOURCE_TYPE,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE, SCHEMA));
        metaAttribute.setSubAttribute(locationAttribute);
        metaAttribute.setSubAttribute(resourceTypeAttribute);

        JSONObject metaSchemaAttribute = new JSONObject();

        encoder.encodeComplexAttribute(metaAttribute, metaSchemaAttribute);

        schemaAttributeArray.put(metaSchemaAttribute);


        return schemaAttributeArray;
    }

    /**
     * crete the meta attribute of the scim object
     *
     */
    protected ComplexAttribute createMetaAttribute() throws CharonException, BadRequestException {
        ComplexAttribute metaAttribute =
                (ComplexAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.META,
                        new ComplexAttribute(SCIMConstants.CommonSchemaConstants.META));
        return metaAttribute;
    }

    private String createLocationHeader(String location, String resourceID) {
        String locationString = location + "/" + resourceID;
        return locationString;
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
