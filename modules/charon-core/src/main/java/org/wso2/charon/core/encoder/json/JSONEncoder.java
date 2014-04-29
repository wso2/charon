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
package org.wso2.charon.core.encoder.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.objects.bulk.BulkData;
import org.wso2.charon.core.objects.bulk.BulkResponseContent;
import org.wso2.charon.core.objects.bulk.BulkResponseData;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.util.AttributeUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Encoder that encodes a given SCIM Object in JSON format.
 */
public class JSONEncoder implements Encoder {

    private String format;

    public JSONEncoder() {
        format = SCIMConstants.JSON;
    }

    /**
     * Encode the given SCIM object.
     *
     * @param scimObject
     * @return the resulting string after encoding.
     */
    public String encodeSCIMObject(SCIMObject scimObject) throws CharonException {
        //root json object containing the encoded SCIM Object.
        JSONObject rootObject = new JSONObject();
        rootObject = this.getSCIMObjectAsJSONObject(scimObject);
        return rootObject.toString();
    }

    /**
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

        } catch (JSONException e) {
            String errorMessage = "Error in encoding resource..";
            //TODO:log the error
            throw new CharonException(errorMessage);
        }
        return rootObject;
    }

    /**
     * Encode the Exception to be sent in the SCIM - response payload.
     *
     * @param exception
     * @return the resulting string after encoding
     */
    public String encodeSCIMException(AbstractCharonException exception) {
        //outer most json object
        JSONObject rootErrorObject = new JSONObject();
        //if multiple errors present, we send them in an array.
        JSONArray arrayOfErrors = new JSONArray();

        //JSON Object containing the error code and error message
        JSONObject errorObject = new JSONObject();
        try {
            //construct error object with details in the exception
            errorObject.put(ResponseCodeConstants.DESCRIPTION, exception.getDescription());
            errorObject.put(ResponseCodeConstants.CODE, String.valueOf(exception.getCode()));
            //TODO:for the moment it is expected that an exception only contains one error.
            arrayOfErrors.put(errorObject);
            //construct the full json obj.
            rootErrorObject.put(ResponseCodeConstants.ERRORS, arrayOfErrors);

        } catch (JSONException e) {
            //usually errors occur rarely when encoding exceptions. and no need to pass them to clients.
            //sufficient to log them in server side back end.
            //TODO:log the error
        }
        return rootErrorObject.toString();

    }

    /**
     * Obtain the format that the particular encoder supports. This can be initialized in the constructor.
     *
     * @return
     */
    @Override
    public String getFormat() {
        return format;
    }

    public void encodeArrayOfValues(String arrayName, Object[] arrayValues,
                                    JSONObject rootObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object arrayValue : arrayValues) {
            jsonArray.put(arrayValue);
        }
        rootObject.put(arrayName, jsonArray);
    }

    /**
     * Encode the simple attribute and include it in root json object to be returned.
     *
     * @param attribute
     * @param rootObject
     */
    public void encodeSimpleAttribute(SimpleAttribute attribute, JSONObject rootObject)
            throws JSONException {
        if (attribute.getValue() != null) {
            //if type is DateTime, convert before encoding.
            if (attribute.getDataType() != null &&
                attribute.getDataType() == SCIMSchemaDefinitions.DataType.DATE_TIME) {
                rootObject.put(attribute.getName(),
                               AttributeUtil.formatDateTime((Date) attribute.getValue()));
                return;
            }
            rootObject.put(attribute.getName(), attribute.getValue());
        }
    }

    /**
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
            if (attributeValue.getDataType() != null &&
                attributeValue.getDataType() == SCIMSchemaDefinitions.DataType.DATE_TIME) {
                attributeValueObject.put(attributeValue.getName(),
                                         AttributeUtil.formatDateTime((Date) attributeValue.getValue()));
                return;
            }
            attributeValueObject.put(attributeValue.getName(), attributeValue.getValue());
            jsonArray.put(attributeValueObject);
        }
    }


    /**
     * Encode the complex attribute and include it in root json object to be returned.
     *
     * @param complexAttribute
     * @param rootObject
     */
    public void encodeComplexAttribute(ComplexAttribute complexAttribute, JSONObject rootObject)
            throws JSONException {
        JSONObject subObject = new JSONObject();
		Map<String, Attribute> attributes = complexAttribute.getSubAttributes();
		if (attributes == null || attributes.size() < 1) {
			attributes = complexAttribute.getAttributes();
		}
        for (Attribute attributeValue : attributes.values()) {
            //using instanceof instead of polymorphic way, in order to make encoder pluggable.
            if (attributeValue instanceof SimpleAttribute) {
                //most of the time, this if condition is hit according to current SCIM spec.
                encodeSimpleAttribute((SimpleAttribute) attributeValue, subObject);

            } else if (attributeValue instanceof ComplexAttribute) {
                encodeComplexAttribute((ComplexAttribute) attributeValue, subObject);

            } else if (attributeValue instanceof MultiValuedAttribute) {
                encodeMultiValuedAttribute((MultiValuedAttribute) attributeValue, subObject);
            }
            rootObject.put(complexAttribute.getName(), subObject);
        }

    }

    /**
     * When an attribute value (of a multivalued attribute) becomes a complex attribute,
     * use this method to encode it.
     *
     * @param attributeValue
     * @param jsonArray
     */
    protected void encodeComplexAttributeValue(ComplexAttribute attributeValue,
                                               JSONArray jsonArray) throws JSONException {
        JSONObject subObject = new JSONObject();
        Map<String, Attribute> subAttributes = attributeValue.getSubAttributes();
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

    /**
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
        List<String> stringAttributeValues = multiValuedAttribute.getValuesAsStrings();
        List<Attribute> attributeValues = multiValuedAttribute.getValuesAsSubAttributes();
        //if values are strings,
        if (stringAttributeValues != null && !stringAttributeValues.isEmpty()) {
            for (String stringAttributeValue : stringAttributeValues) {
                jsonArray.put(stringAttributeValue);
            }
        }
        if (attributeValues != null && !attributeValues.isEmpty()) {
            for (Attribute attributeValue : attributeValues) {
                if (attributeValue instanceof SimpleAttribute) {
                    encodeSimpleAttributeValue((SimpleAttribute) attributeValue, jsonArray);

                } else if (attributeValue instanceof ComplexAttribute) {

                    encodeComplexAttributeValue((ComplexAttribute) attributeValue, jsonArray);
                }
            }
        }
        jsonObject.put(multiValuedAttribute.getName(), jsonArray);
    }

    /**
     * Encode given bulkResponseData object and return the encoded string
     *
     * @param bulkResponseData
     * @return
     */
    public String encodeBulkResponseData(BulkResponseData bulkResponseData) {
        String encodedString = "";
        List<BulkResponseContent> userResponseDataList = bulkResponseData.getUserCreatingResponse();
        List<BulkResponseContent> groupResponseDataList = bulkResponseData.getGroupCreatingResponse();
        JSONObject rootObject = new JSONObject();

        //encode schemas
        try {
            //set the [schemas]
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                                     bulkResponseData.getSchemas().toArray(), rootObject);

            //[Operations] - multi value attribute
            JSONObject[] operationResponseList = new JSONObject[userResponseDataList.size() + groupResponseDataList.size()];

            //POST: User creating response
            int i = 0;
            for (BulkResponseContent userCreatingResponse : userResponseDataList) {

                SCIMResponse scimResponse = userCreatingResponse.getScimResponse();
                JSONObject decodedObject = new JSONObject(new JSONTokener(scimResponse.getResponseMessage()));
                JSONObject operationObject = new JSONObject();

                //*****Split SCIM response******
                //get location from meta section
                JSONObject meta = decodedObject.optJSONObject(SCIMConstants.CommonSchemaConstants.META);
                String location = meta.optString(SCIMConstants.CommonSchemaConstants.LOCATION);
                //get method from response
                String method = userCreatingResponse.getMethod();
                //get bulkID from response
                String bulkId = userCreatingResponse.getBulkID();

                //create status object
                String code = userCreatingResponse.getResponseCode();
                JSONObject status = new JSONObject();
                status.put(SCIMConstants.CommonSchemaConstants.CODE, code);


                operationObject.put(SCIMConstants.CommonSchemaConstants.LOCATION, location);
                operationObject.put(SCIMConstants.CommonSchemaConstants.METHOD, "POST");
                operationObject.put(SCIMConstants.CommonSchemaConstants.BULK_ID, bulkId);
                operationObject.put(SCIMConstants.CommonSchemaConstants.STATUS, status);

                operationResponseList[i] = operationObject;

                i++;
            }
            //POST: Group creating response
            for (BulkResponseContent groupCreatingResponse : groupResponseDataList) {

                SCIMResponse scimResponse = groupCreatingResponse.getScimResponse();
                JSONObject decodedObject = new JSONObject(new JSONTokener(scimResponse.getResponseMessage()));
                JSONObject operationObject = new JSONObject();

                //*****Split SCIM response******
                //get location from meta section
                JSONObject meta = decodedObject.optJSONObject(SCIMConstants.CommonSchemaConstants.META);
                String location = meta.optString(SCIMConstants.CommonSchemaConstants.LOCATION);
                //get method from response
                String method = groupCreatingResponse.getMethod();
                //get bulkID from response
                String bulkId = groupCreatingResponse.getBulkID();

                //create status object
                String code = groupCreatingResponse.getResponseCode();
                JSONObject status = new JSONObject();
                status.put(SCIMConstants.CommonSchemaConstants.CODE, code);


                operationObject.put(SCIMConstants.CommonSchemaConstants.LOCATION, location);
                operationObject.put(SCIMConstants.CommonSchemaConstants.METHOD, "POST");
                operationObject.put(SCIMConstants.CommonSchemaConstants.BULK_ID, bulkId);
                operationObject.put(SCIMConstants.CommonSchemaConstants.STATUS, status);

                operationResponseList[i] = operationObject;

                i++;
            }

            //TODO: Have to encode the other types in User and Groups response data

            //set operations
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.OPERATIONS, operationResponseList, rootObject);

            encodedString = rootObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }


        return encodedString;
    }

    public String encodeBulkData(BulkData bulkData) {
        String encodedString = "";
        //root object
        JSONObject rootObject = new JSONObject();
        try {
            //encode [schema]
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.SCHEMAS,
                                     bulkData.getSchemas().toArray(), rootObject);
            //encode [failOnErrors]
            SimpleAttribute failOnErrorsAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.FAIL_ON_ERRORS,
                                                                        bulkData.getFailOnErrors());
            this.encodeSimpleAttribute(failOnErrorsAttribute, rootObject);

            //encode all User and Group requests
            JSONObject[] allRequestList = new JSONObject[bulkData.getUserList().size() + bulkData.getGroupList().size()];
            JSONObject encodedUserRequest;
            int i = 0;
            for (User user : bulkData.getUserList()) {
                JSONObject userOperation = new JSONObject();
                //set [bulkId]
                userOperation.put(SCIMConstants.CommonSchemaConstants.BULK_ID, user.getBulkID());
                //set [path]
                userOperation.put(SCIMConstants.CommonSchemaConstants.PATH, user.getPath());
                //set [method]
                userOperation.put(SCIMConstants.CommonSchemaConstants.METHOD, user.getMethod());
                // encode the user
                encodedUserRequest = this.getSCIMObjectAsJSONObject(user);
                //set [data]
                SimpleAttribute dataAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DATA,
                                                                    encodedUserRequest);

                this.encodeSimpleAttribute(dataAttribute, userOperation);

                allRequestList[i] = userOperation;

                i = i + 1;
            }


            JSONObject encodedGroupRequest;
            //encode the Group request and append to allRequestList.
            for (Group group : bulkData.getGroupList()) {
                JSONObject groupOperation = new JSONObject();
                //set [bulkId]
                groupOperation.put(SCIMConstants.CommonSchemaConstants.BULK_ID, group.getBulkID());
                //set [path]
                groupOperation.put(SCIMConstants.CommonSchemaConstants.PATH, group.getPath());
                //set [method]
                groupOperation.put(SCIMConstants.CommonSchemaConstants.METHOD, group.getMethod());
                // encode the user
                encodedGroupRequest = this.getSCIMObjectAsJSONObject(group);
                //set [data]
                SimpleAttribute dataAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DATA,
                                                                    encodedGroupRequest);

                this.encodeSimpleAttribute(dataAttribute, groupOperation);

                allRequestList[i] = groupOperation;

                i = i + 1;
            }

            //set to [Operations]
            this.encodeArrayOfValues(SCIMConstants.CommonSchemaConstants.OPERATIONS, allRequestList, rootObject);

            encodedString = rootObject.toString();


        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (CharonException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return encodedString;
    }
}
