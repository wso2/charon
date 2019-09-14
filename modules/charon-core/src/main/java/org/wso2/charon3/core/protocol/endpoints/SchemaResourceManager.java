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
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The schema resource enables a service
 * provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 */
public class SchemaResourceManager extends AbstractResourceManager {

    private static final Logger log = LoggerFactory.getLogger(SchemaResourceManager.class);

    private static final String ATTRIBUTES = "attributes";
    private static final String MUTABILITY = "mutability";
    private static final String MULTIVALUED = "multiValued";
    private static final String CASE_EXACT = "caseExact";
    private static final String RETURNED = "returned";
    private static final String UNIQUENESS = "uniqueness";

    public SchemaResourceManager() {

    }

    /*
     * Retrieves a schema
     *
     * @return SCIM response to be returned.
     */
    @Override
    public SCIMResponse get(String id, UserManager userManager, String attributes, String excludeAttributes) {

        try {
            if (userManager != null) {

                // Handover the retrieving supported schema definition usermanager provided by the SP.
                List<Map<String, String>> userSchemaAttributes = userManager.getUserSchema();

                // If there are any http headers to be added in the response header.
                Map<String, String> responseHeaders = new HashMap<String, String>();
                responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
                responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(
                        SCIMConstants.SCHEMAS_ENDPOINT));

                return new SCIMResponse(ResponseCodeConstants.CODE_OK,
                        buildSchemaJsonBody(userSchemaAttributes).toString(), responseHeaders);

            } else {
                String error = "Provided user manager handler is null.";
                log.error(error);
                throw new InternalErrorException(error);
            }

        } catch (NotFoundException e) {
            return AbstractResourceManager.encodeSCIMException(e);
        } catch (CharonException | NotImplementedException | BadRequestException | InternalErrorException
                | JSONException e) {
            return AbstractResourceManager.encodeSCIMException(new CharonException("Error while encoding"));
        }
    }
    /*
     * Build the service provider config json representation
     * @param config
     * @return
     */
    public JSONArray buildSchemaJsonBody(List<Map<String, String>> userSchemaAttributeList) throws JSONException {

        JSONArray rootObject = new JSONArray();
        JSONObject userSchemaObject = getUserSchemaJsonBody(userSchemaAttributeList);
        rootObject.put(userSchemaObject);
        return rootObject;
    }

    private JSONObject getUserSchemaJsonBody(List<Map<String, String>> userSchemaAttributeList) throws JSONException {

        JSONObject userSchemaObject = new JSONObject();
        userSchemaObject.put(SCIMConstants.CommonSchemaConstants.ID, "urn:ietf:params:scim:schemas:core:2.0:User");
        userSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, "User");
        userSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, "User Account");

        JSONArray attributesSchemesArray = new JSONArray();

        // Add static username schema definition
        JSONObject usernameSchemaObject = getUsernameSchema();
        attributesSchemesArray.put(usernameSchemaObject);

        // Add dynamic user schema definitions
        for (Map<String, String> userShemaAttributes : userSchemaAttributeList) {

            JSONObject scimAttributeSchemaObject = new JSONObject();
            for (Map.Entry<String, String> entry : userShemaAttributes.entrySet()) {
                scimAttributeSchemaObject.put(entry.getKey(), entry.getValue());
            }
            attributesSchemesArray.put(userShemaAttributes);
        }

        userSchemaObject.put(ATTRIBUTES, attributesSchemesArray);
        return userSchemaObject;
    }

    private JSONObject getUsernameSchema() throws JSONException {
        JSONObject usernameSchemaObject = new JSONObject();

        usernameSchemaObject.put(SCIMConstants.UserSchemaConstants.NAME, "userName");
        usernameSchemaObject.put(SCIMConstants.CommonSchemaConstants.TYPE, "string");
        usernameSchemaObject.put(MULTIVALUED, "false");
        usernameSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION, "Unique identifier for the " +
                "User, typically used by the user to directly authenticate to the service provider. Each User MUST " +
                "include a non-empty userName value.  This identifier MUST be unique across the service provider's " +
                "entire set of Users. REQUIRED.");
        usernameSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED, "false");
        usernameSchemaObject.put(CASE_EXACT, "false");
        usernameSchemaObject.put(MUTABILITY, "readWrite");
        usernameSchemaObject.put(RETURNED, "default");
        usernameSchemaObject.put(UNIQUENESS, "server");

        return usernameSchemaObject;
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
