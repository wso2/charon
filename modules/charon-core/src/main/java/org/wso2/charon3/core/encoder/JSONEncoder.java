/*
 * Copyright (c) 2016-2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon3.core.encoder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.config.SCIMConfigConstants;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.Role;
import org.wso2.charon3.core.objects.SCIMObject;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.utils.AttributeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.config.SCIMConfigConstants.CASE_EXACT;
import static org.wso2.charon3.core.config.SCIMConfigConstants.DESCRIPTION;
import static org.wso2.charon3.core.config.SCIMConfigConstants.MULTIVALUED;
import static org.wso2.charon3.core.config.SCIMConfigConstants.MUTABILITY;
import static org.wso2.charon3.core.config.SCIMConfigConstants.REQUIRED;
import static org.wso2.charon3.core.config.SCIMConfigConstants.RETURNED;
import static org.wso2.charon3.core.config.SCIMConfigConstants.SUB_ATTRIBUTES;
import static org.wso2.charon3.core.config.SCIMConfigConstants.UNIQUENESS;
import static org.wso2.charon3.core.schema.SCIMConstants.CommonSchemaConstants.TYPE;
import static org.wso2.charon3.core.schema.SCIMConstants.UserSchemaConstants.NAME;

/**
 * This encodes the in the json format.
 */

public class JSONEncoder {

    private String format;
    private static final Logger logger = LoggerFactory.getLogger(JSONEncoder.class);

    public JSONEncoder() {
        format = SCIMConstants.JSON;
    }

    public String getFormat() {
        return format;
    }

    /*
     * return encoded string from scim object
     * @param scimObject
     * @return
     * @throws CharonException
     */
    public String encodeSCIMObject(SCIMObject scimObject) throws CharonException {
        //root json object containing the encoded SCIM Object.
        JSONObject rootObject;
        rootObject = this.getSCIMObjectAsJSONObject(scimObject);
        return rootObject.toString();
    }

    /*
     * encode scim exceptions
     * @param exception
     * @return
     */
    public String encodeSCIMException(AbstractCharonException exception) {
        //outer most json object
        JSONObject rootErrorObject = new JSONObject();
        //JSON Object containing the error code and error message
        JSONObject errorObject = new JSONObject();

        try {
            //construct error object with details in the exception
            JSONArray arrSchemas = new JSONArray(Collections.singletonList(exception.getSchemas()));
            errorObject.put(ResponseCodeConstants.SCHEMAS, arrSchemas);
            if (exception.getScimType() != null) {
                errorObject.put(ResponseCodeConstants.SCIM_TYPE, String.valueOf(exception.getScimType()));
            }
            errorObject.put(ResponseCodeConstants.DETAIL, String.valueOf(exception.getDetail()));
            errorObject.put(ResponseCodeConstants.STATUS, String.valueOf(exception.getStatus()));
            //construct the full json obj.
            rootErrorObject = errorObject;

        } catch (JSONException e) {
            //usually errors occur rarely when encoding exceptions. and no need to pass them to clients.
            //sufficient to log them in server side back end.
            logger.error("SCIMException encoding error");
        }
        return rootErrorObject.toString();
    }
    /*
     * Make JSON object from given SCIM object.
     *
     * @param scimObject
     * @return the resulting string after encoding.
     */
    public JSONObject getSCIMObjectAsJSONObject(SCIMObject scimObject) throws CharonException {
        //root json object containing the encoded SCIM Object.
        JSONObject rootObject = new JSONObject();
        try {
            //encode schemas
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                    (scimObject.getSchemaList()).toArray(), rootObject);
            //encode attribute list
            Map<String, Attribute> attributes = scimObject.getAttributeList();
            if (attributes != null && !attributes.isEmpty()) {
                for (Attribute attribute : attributes.values()) {
                    //using instanceof instead of polymorphic way, in order to make encoder pluggable.
                    if (attribute instanceof SimpleAttribute) {
                        encodeSimpleAttribute((SimpleAttribute) attribute, rootObject);

                    } else if (attribute instanceof ComplexAttribute) {
                        encodeComplexAttribute((ComplexAttribute) attribute, rootObject);

                    } else if (attribute instanceof MultiValuedAttribute) {
                        encodeMultiValuedAttribute((MultiValuedAttribute) attribute, rootObject);
                    }
                }
            }

            // Encode permissions of the role.
            if (scimObject instanceof Role && !((Role) scimObject).getPermissions().isEmpty()) {
                this.encodeArrayOfValues(SCIMConstants.RoleSchemaConstants.PERMISSIONS,
                        (((Role) scimObject).getPermissions()).toArray(), rootObject);
            }

        } catch (JSONException e) {
            String errorMessage = "Error in encoding resource..";
            //TODO:log the error
            throw new CharonException(errorMessage);
        }
        return rootObject;
    }

    /*
     * encode array of values
     * @param arrayName
     * @param arrayValues
     * @param rootObject
     * @throws JSONException
     */
    public void encodeArrayOfValues(String arrayName, Object[] arrayValues,
                                    JSONObject rootObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object arrayValue : arrayValues) {
            jsonArray.put(arrayValue);
        }
        rootObject.put(arrayName, jsonArray);
    }

    /*
     * Encode the simple attribute and include it in root json object to be returned.
     *
     * @param attribute
     * @param rootObject
     */
    public void encodeSimpleAttribute(SimpleAttribute attribute, JSONObject rootObject)
            throws JSONException {
        if (attribute.getValue() != null) {
            //if type is DateTime, convert before encoding.
            if (attribute.getType() != null && attribute.getType() == SCIMDefinitions.DataType.DATE_TIME) {
                rootObject.put(attribute.getName(),
                        AttributeUtil.formatDateTime((Instant) attribute.getValue()));
                return;
            }
            rootObject.put(attribute.getName(), attribute.getValue());
        }
    }

    /**
     * Encode the simple attribute and returns the json object.
     *
     * @param simpleAttribute
     * @return json object of the simple attribute.
     * @throws JSONException
     */
    public JSONObject encodeSimpleAttributeSchema(SimpleAttribute simpleAttribute) throws JSONException {

        return encodeBasicAttributeSchema(simpleAttribute);
    }

    /**
     * Encode the attribute schema and return the json object.
     *
     * @param attribute
     * @return json object of the attribute schema.
     * @throws JSONException
     */
    public JSONObject encodeBasicAttributeSchema(Attribute attribute) throws JSONException {

        JSONObject attributeSchema = new JSONObject();
        attributeSchema.put(NAME, attribute.getName());
        attributeSchema.put(TYPE, attribute.getType());
        attributeSchema.put(MULTIVALUED, attribute.getMultiValued());
        attributeSchema.put(DESCRIPTION, attribute.getDescription());
        attributeSchema.put(REQUIRED, attribute.getRequired());
        attributeSchema.put(CASE_EXACT, attribute.getCaseExact());
        attributeSchema.put(MUTABILITY, attribute.getMutability());
        attributeSchema.put(RETURNED, attribute.getReturned());
        attributeSchema.put(UNIQUENESS, attribute.getUniqueness());

        Map<String, String> customAttributes = attribute.getAttributeProperties();
        for (Map.Entry<String, String> entry : customAttributes.entrySet()) {
            attributeSchema.put(entry.getKey(), entry.getValue());
        }

        Map<String, JSONObject> customJSONProperties = attribute.getAttributeJSONProperties();
        for (Map.Entry<String, JSONObject> entry: customJSONProperties.entrySet()) {
            attributeSchema.put(entry.getKey(), entry.getValue());
        }

        Map<String, JSONArray> customJSONPropertyArrays = attribute.getAttributeJSONPropertyArrays();
        for (Map.Entry<String, JSONArray> entry: customJSONPropertyArrays.entrySet()) {
            attributeSchema.put(entry.getKey(), entry.getValue());
        }

        return attributeSchema;
    }

    /*
     * Encode the complex attribute and include it in root json object to be returned.
     *
     * @param complexAttribute
     * @param rootObject
     */
    public void encodeComplexAttribute(ComplexAttribute complexAttribute, JSONObject rootObject)
            throws JSONException {
        JSONObject subObject = new JSONObject();
        Map<String, Attribute> attributes = complexAttribute.getSubAttributesList();
        for (Attribute attributeValue : attributes.values()) {
            //using instanceof instead of polymorphic way, in order to make encoder pluggable.
            if (attributeValue instanceof SimpleAttribute) {
                //most of the time, this if condition is hit according to current SCIM spec.
                encodeSimpleAttribute((SimpleAttribute) attributeValue, subObject);

            } else if (attributeValue instanceof MultiValuedAttribute) {
                encodeMultiValuedAttribute((MultiValuedAttribute) attributeValue, subObject);
            } else if (attributeValue instanceof ComplexAttribute) {
                encodeComplexAttribute((ComplexAttribute) attributeValue, subObject);
            }
            rootObject.put(complexAttribute.getName(), subObject);
        }

    }

    /**
     * Encode the complex attribute schema and return the json object.
     *
     * @param complexAttribute
     * @return json object of the complex attribute schema.
     * @throws JSONException
     */
    public JSONObject encodeComplexAttributeSchema(ComplexAttribute complexAttribute) throws JSONException {

        JSONObject complexAttributeSchema = encodeBasicAttributeSchema(complexAttribute);
        Map<String, Attribute> subAttributesMap = complexAttribute.getSubAttributesList();

        if (subAttributesMap != null) {
            JSONArray subAttributesSchemaArray = new JSONArray();

            for (Map.Entry<String, Attribute> subAttribute : subAttributesMap.entrySet()) {
                JSONObject subAttributesSchema = encodeBasicAttributeSchema(subAttribute.getValue());
                subAttributesSchemaArray.put(subAttributesSchema);
            }

            complexAttributeSchema.put(SUB_ATTRIBUTES, subAttributesSchemaArray);
        }

        return complexAttributeSchema;
    }


    /*
     * When an attribute value (of a complex or multivalued attribute) becomes a simple attribute itself,
     * encode it and put it in json array.
     *
     * @param attributeValue
     * @param jsonArray
     * @throws JSONException
     */
    protected void encodeSimpleAttributeValue(SimpleAttribute attributeValue, JSONArray jsonArray)
            throws JSONException {
        if (attributeValue.getValue() != null) {
            JSONObject attributeValueObject = new JSONObject();
            //if type is DateTime, convert before encoding.
            if (attributeValue.getType() != null &&
                    attributeValue.getType() == SCIMDefinitions.DataType.DATE_TIME) {
                attributeValueObject.put(attributeValue.getName(),
                        AttributeUtil.formatDateTime((Instant) attributeValue.getValue()));
                return;
            }
            attributeValueObject.put(attributeValue.getName(), attributeValue.getValue());
            jsonArray.put(attributeValueObject);
        }
    }

    /*
     * Encode the simple attribute and include it in root json object to be returned.
     *
     * @param multiValuedAttribute
     * @param jsonObject
     */
    public void encodeMultiValuedAttribute(MultiValuedAttribute multiValuedAttribute,
                                           JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        //TODO:what if values are set as list of string values.For the moment it is ok, since only schemas
        //attribute has such values and we handle it separately in encoding.
        List<Object> stringAttributeValues = multiValuedAttribute.getAttributePrimitiveValues();
        List<Attribute> attributeValues = multiValuedAttribute.getAttributeValues();
        //if values are strings,
        if (attributeValues != null && !attributeValues.isEmpty()) {
            for (Attribute attributeValue : attributeValues) {
                if (attributeValue instanceof SimpleAttribute) {
                    encodeSimpleAttributeValue((SimpleAttribute) attributeValue, jsonArray);

                } else if (attributeValue instanceof ComplexAttribute) {

                    encodeComplexAttributeValue((ComplexAttribute) attributeValue, jsonArray);
                }
            }
        }
        if (stringAttributeValues != null && !stringAttributeValues.isEmpty()) {
            for (Object arrayValue : stringAttributeValues) {
                if (arrayValue instanceof SCIMObject) {
                    try {
                        jsonArray.put(getSCIMObjectAsJSONObject((SCIMObject) arrayValue));
                    } catch (CharonException e) {
                        throw new JSONException(e);
                    }
                } else {
                    jsonArray.put(arrayValue);
                }
            }
        }
        jsonObject.put(multiValuedAttribute.getName(), jsonArray);
    }

    /*
     * When an attribute value (of a multivalued attribute) becomes a complex attribute,
     * use this method to encode it.
     *
     * @param attributeValue
     * @param jsonArray
     */
    protected void encodeComplexAttributeValue(ComplexAttribute attributeValue,
                                               JSONArray jsonArray) throws JSONException {
        JSONObject subObject = new JSONObject();
        Map<String, Attribute> subAttributes = attributeValue.getSubAttributesList();
        for (Attribute value : subAttributes.values()) {
            //using instanceof instead of polymorphic way, in order to make encoder pluggable.
            if (value instanceof SimpleAttribute) {
                encodeSimpleAttribute((SimpleAttribute) value, subObject);

            } else if (value instanceof ComplexAttribute) {
                encodeComplexAttribute((ComplexAttribute) value, subObject);

            } else if (value instanceof MultiValuedAttribute) {
                encodeMultiValuedAttribute((MultiValuedAttribute) value, subObject);
            }
        }
        jsonArray.put(subObject);
    }

    /*
     * Build the service provider config json representation
     * @param config
     * @return
     */
    public String buildServiceProviderConfigJsonBody(HashMap<String, Object> config) throws JSONException {
        JSONObject rootObject = new JSONObject();

        JSONObject bulkObject = new JSONObject();
        bulkObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.BULK));
        bulkObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_OPERATIONS,
                config.get(SCIMConfigConstants.MAX_OPERATIONS));
        bulkObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_PAYLOAD_SIZE,
                config.get(SCIMConfigConstants.MAX_PAYLOAD_SIZE));

        JSONObject filterObject = new JSONObject();
        filterObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.FILTER));
        filterObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_RESULTS,
                config.get(SCIMConfigConstants.MAX_RESULTS));

        JSONObject patchObject = new JSONObject();
        patchObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.PATCH));

        JSONObject sortObject = new JSONObject();
        sortObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.SORT));

        JSONObject etagObject = new JSONObject();
        etagObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.ETAG));

        JSONObject changePasswordObject = new JSONObject();
        changePasswordObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                config.get(SCIMConfigConstants.CHNAGE_PASSWORD));

        JSONArray authenticationSchemesArray = new JSONArray();
        ArrayList<Object[]> values = (ArrayList<Object[]>) config.get(SCIMConfigConstants.AUTHENTICATION_SCHEMES);

        for (int i = 0; i < values.size(); i++) {
            JSONObject authenticationSchemeObject = new JSONObject();
            Object [] value = values.get(i);
            authenticationSchemeObject.put(
                    SCIMConstants.ServiceProviderConfigSchemaConstants.NAME, value[0]);
            authenticationSchemeObject.put(
                    SCIMConstants.ServiceProviderConfigSchemaConstants.DESCRIPTION, value[1]);
            authenticationSchemeObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SPEC_URI, value[2]);
            authenticationSchemeObject.put(
                    SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI, value[3]);
            authenticationSchemeObject.put(
                    SCIMConstants.ServiceProviderConfigSchemaConstants.TYPE, value[4]);
            authenticationSchemeObject.put(
                    SCIMConstants.ServiceProviderConfigSchemaConstants.PRIMARY, value[5]);
            authenticationSchemesArray.put(
                    authenticationSchemeObject);
        }

        rootObject.put(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                new JSONArray().put(SCIMConstants.SERVICE_PROVIDER_CONFIG_SCHEMA_URI));
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI,
                config.get(SCIMConfigConstants.DOCUMENTATION_URL));
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.BULK,
                bulkObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.FILTER,
                filterObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.PATCH,
                patchObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.CHANGE_PASSWORD,
                changePasswordObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.SORT,
                sortObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.ETAG,
                etagObject);
        rootObject.put(SCIMConstants.ServiceProviderConfigSchemaConstants.AUTHENTICATION_SCHEMAS,
                authenticationSchemesArray);

        return rootObject.toString();

    }

    /**
     * Build the user resource type json representation.
     * @return json representation of user resource type.
     */
    public String buildUserResourceTypeJsonBody() throws JSONException {

        JSONObject userResourceTypeObject = new JSONObject();
        userResourceTypeObject.put(
                SCIMConstants.CommonSchemaConstants.SCHEMAS, SCIMConstants.RESOURCE_TYPE_SCHEMA_URI);
        userResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.ID, SCIMConstants.USER);
        userResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.NAME, SCIMConstants.USER);
        userResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT, SCIMConstants.USER_ENDPOINT);
        userResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION,
                SCIMConstants.ResourceTypeSchemaConstants.USER_ACCOUNT);
        userResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.SCHEMA, SCIMConstants.USER_CORE_SCHEMA_URI);

        if (SCIMResourceSchemaManager.getInstance().isExtensionSet()) {
            JSONArray schemaExtensions = new JSONArray();
            JSONObject extensionSchemaObject = new JSONObject();
            extensionSchemaObject.put(
                    SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA,
                    SCIMResourceSchemaManager.getInstance().getExtensionURI());
            extensionSchemaObject.put(
                    SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED,
                    SCIMResourceSchemaManager.getInstance().getExtensionRequired());
            schemaExtensions.put(extensionSchemaObject);

            JSONObject systemSchemaObject = new JSONObject();
            systemSchemaObject.put(
                    SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA,
                    SCIMResourceSchemaManager.getInstance().getSystemSchemaExtensionURI());
            systemSchemaObject.put(
                    SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED,
                    SCIMResourceSchemaManager.getInstance().getSystemSchemaExtensionRequired());
            schemaExtensions.put(systemSchemaObject);

            // Add custom user schema as a schema extension.
            if (StringUtils.isNotBlank(SCIMResourceSchemaManager.getInstance().getCustomSchemaExtensionURI())) {
                JSONObject customSchemaObject = new JSONObject();
                customSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA,
                        SCIMResourceSchemaManager.getInstance().getCustomSchemaExtensionURI());
                customSchemaObject.put(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED, false);
                schemaExtensions.put(customSchemaObject);
            }

            userResourceTypeObject.put(
                    SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS, schemaExtensions);
            
        }

        return userResourceTypeObject.toString();
    }

    /**
     *  Build the group resource type json representation.
     * @return
     */
    public String buildGroupResourceTypeJsonBody() throws JSONException {
        JSONObject groupResourceTypeObject = new JSONObject();

        groupResourceTypeObject.put(
                SCIMConstants.CommonSchemaConstants.SCHEMAS, SCIMConstants.RESOURCE_TYPE_SCHEMA_URI);
        groupResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.ID, SCIMConstants.GROUP);
        groupResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.NAME, SCIMConstants.GROUP);
        groupResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT, SCIMConstants.GROUP_ENDPOINT);
        groupResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION,
                SCIMConstants.ResourceTypeSchemaConstants.GROUP);
        groupResourceTypeObject.put(
                SCIMConstants.ResourceTypeSchemaConstants.SCHEMA, SCIMConstants.GROUP_CORE_SCHEMA_URI);
        return groupResourceTypeObject.toString();
    }

    /**
     * Encode given bulkResponseData object and return the encoded string.
     *
     * @param bulkResponseData Bulk response data.
     * @return Encoded bulk response.
     * @throws InternalErrorException InternalErrorException.
     */
    public String encodeBulkResponseData(BulkResponseData bulkResponseData) throws InternalErrorException {

        String encodedString;
        List<BulkResponseContent> userResponseDataList = bulkResponseData.getUserOperationResponse();
        List<BulkResponseContent> groupResponseDataList = bulkResponseData.getGroupOperationResponse();
        List<BulkResponseContent> roleResponseDataList = bulkResponseData.getRoleOperationResponse();
        JSONObject rootObject = new JSONObject();

        //encode schemas
        try {
            //set the [schemas]
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                    bulkResponseData.getSchemas().toArray(), rootObject);

            //[Operations] - multi value attribute
            ArrayList<JSONObject> operationResponseList = new ArrayList<>();

            for (BulkResponseContent userOperationResponse : userResponseDataList) {
                encodeResponseContent(userOperationResponse, operationResponseList);
            }

            for (BulkResponseContent groupOperationResponse : groupResponseDataList) {
                encodeResponseContent(groupOperationResponse, operationResponseList);
            }

            for (BulkResponseContent roleOperationResponse : roleResponseDataList) {
                encodeResponseContent(roleOperationResponse, operationResponseList);
            }
            // Set operations.
            this.encodeArrayOfValues(SCIMConstants.OperationalConstants.OPERATIONS,
                    operationResponseList.toArray(), rootObject);

            encodedString = rootObject.toString();

        } catch (JSONException e) {
            throw new InternalErrorException("Error in encoding the response");
        }

        return encodedString;
    }


    private void encodeResponseContent(BulkResponseContent responseContent,
                                       ArrayList<JSONObject> operationResponseList)
            throws JSONException {

        int statusCode = responseContent.getScimResponse().getResponseStatus();

        JSONObject status = new JSONObject();
        status.put(SCIMConstants.OperationalConstants.CODE, statusCode);

        JSONObject operationObject = new JSONObject();
        operationObject.put(SCIMConstants.CommonSchemaConstants.LOCATION, responseContent.getLocation());
        operationObject.put(SCIMConstants.OperationalConstants.METHOD, responseContent.getMethod());
        operationObject.put(SCIMConstants.OperationalConstants.BULK_ID, responseContent.getBulkID());
        operationObject.put(SCIMConstants.OperationalConstants.STATUS, status);

        // When indicating a response with an HTTP status other than a 200-series response,
        // the response body MUST be included.
        //
        // Addition: 2023/11/15
        // We are now supporting bulk get as well. Hence, even with 200 responses, we can get a body. Hence, null
        // checking the scim response and if not null adding the response as well.
        // Keeping the old status code check for backward compatibility.
        if ((statusCode != 200 && statusCode != 201 && statusCode != 204)
                || responseContent.getScimResponse() != null) {
            operationObject.put(SCIMConstants.OperationalConstants.RESPONSE,
                    responseContent.getScimResponse().getResponseMessage());
        }
        operationResponseList.add(operationObject);
    }
}
