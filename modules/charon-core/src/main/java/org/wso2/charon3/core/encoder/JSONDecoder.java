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
package org.wso2.charon3.core.encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.bulk.BulkRequestContent;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.AttributeUtil;
import org.wso2.charon3.core.utils.codeutils.FilterTreeManager;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BINARY;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BOOLEAN;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DATE_TIME;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DECIMAL;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.INTEGER;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.REFERENCE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;

/**
 * This decodes the json encoded resource string and create a SCIM object model according to the specification
 * according to the info that the user has sent, and returns SCIMUser object.
 */

public class JSONDecoder {

    private static final Logger logger = LoggerFactory.getLogger(JSONDecoder.class);


    public JSONDecoder() {

    }

  /**
   * decodes a string that should match the {@link SCIMConstants#LISTED_RESOURCE_CORE_SCHEMA_URI} scheme to
   * {@link ListedResource} object that holds the parsed objects
   *
   * @param scimResourceString the listed resource string
   * @param resourceSchema the schema of the resource objects that should be present.
   * @param scimObjectType the type of the scim resources
   * @param <T> a {@link AbstractSCIMObject} type as {@link Group} or {@link User}
   * @return the listed resource object
   * @throws BadRequestException if the json could not be parsed
   * @throws CharonException if a value of the json contains data in an unexpected format or type
   */
    public <T extends AbstractSCIMObject> ListedResource decodeListedResource(String scimResourceString,
                                                                                   ResourceTypeSchema resourceSchema,
                                                                                   Class<T> scimObjectType)
        throws BadRequestException, CharonException {

        JSONObject decodedJsonObj;
        try {
            decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
        } catch (JSONException e) {
            logger.error("json error in decoding the resource", e);
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }

        int totalResults = getIntValueFromJson(decodedJsonObj,
                                               SCIMConstants.ListedResourceSchemaConstants.TOTAL_RESULTS);
        int startIndex = getIntValueFromJson(decodedJsonObj,
                                             SCIMConstants.ListedResourceSchemaConstants.START_INDEX);
        int itemsPerPage = getIntValueFromJson(decodedJsonObj,
                                               SCIMConstants.ListedResourceSchemaConstants.ITEMS_PER_PAGE);

        ListedResource listedResource = new ListedResource();
        listedResource.setSchema(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI);
        listedResource.setTotalResults(totalResults);
        listedResource.setStartIndex(startIndex);
        listedResource.setItemsPerPage(itemsPerPage);

        JSONArray resources = null;
        try {
            resources = decodedJsonObj.getJSONArray(SCIMConstants.ListedResourceSchemaConstants.RESOURCES);
        } catch (JSONException e) {
          logger.debug("could not get '{}' from json structure, result is empty",
                       SCIMConstants.ListedResourceSchemaConstants.RESOURCES);
          return listedResource;
        }

        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource;
            try {
                resource = resources.getJSONObject(i);
            } catch (JSONException e) {
                logger.error("could not get '{}' from json structure",
                             SCIMConstants.ListedResourceSchemaConstants.RESOURCES);
                throw new CharonException(ResponseCodeConstants.INVALID_SYNTAX, e);
            }
            try {
                T abstractSCIMObject = decodeResource(resource.toString(),
                                                      resourceSchema,
                                                      scimObjectType.newInstance());
                listedResource.addResource(abstractSCIMObject);
                listedResource.setResources(abstractSCIMObject.getAttributeList());
            } catch (InternalErrorException | InstantiationException | IllegalAccessException e) {
                throw new CharonException("could not create resource instance of type " + scimObjectType.getName(), e);
            }
        }
        return listedResource;
    }


    /**
     * retrieves an int value from the given {@link JSONObject}
     * @param jsonObject the jsonObject that might hold an int-value under the given key
     * @param name the name of the attribute in the json structure that should be retrieved as int
     * @return the int value of the key
     * @throws CharonException if the value under the given key is not an int value
     */
    private int getIntValueFromJson(JSONObject jsonObject, String name) throws CharonException {
        int totalResults;
        try {
            totalResults = jsonObject.getInt(name);
        } catch (JSONException e) {
            logger.error("could not get '{}' value from scim resource", name);
            throw new CharonException(ResponseCodeConstants.INVALID_SYNTAX, e);
        }
        return totalResults;
    }

    /**
     * Decode the resource string sent in the SCIM request payload.
     *
     * @param scimResourceString - json encoded string of user info
     * @param resourceSchema     - SCIM defined user schema
     * @param scimObject         - a container holding the attributes and schema list
     * @return SCIMObject
     */
    public <T extends AbstractSCIMObject> T decodeResource(String scimResourceString,
                                                           ResourceTypeSchema resourceSchema,
                                                           T scimObject)
            throws BadRequestException, CharonException, InternalErrorException {
        try {
            //decode the string into json representation
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            //get the attribute schemas list from the schema that defines the given resource
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();

            //set the schemas in scimobject
            for (int i = 0; i < resourceSchema.getSchemasList().size(); i++) {
                scimObject.setSchema(resourceSchema.getSchemasList().get(i));
            }
            //iterate through the schema and extract the attributes.
            for (AttributeSchema attributeSchema : attributeSchemas) {
                //obtain the user defined value for given key- attribute schema name
                Object attributeValObj = decodedJsonObj.opt(attributeSchema.getName());
                if (attributeValObj == null) {
                    //user may define the attribute by its fully qualified uri
                    attributeValObj = decodedJsonObj.opt(attributeSchema.getURI());
                }
                SCIMDefinitions.DataType attributeSchemaDataType = attributeSchema.getType();

                if (attributeSchemaDataType.equals(STRING) || attributeSchemaDataType.equals(BINARY) ||
                        attributeSchemaDataType.equals(BOOLEAN) || attributeSchemaDataType.equals(DATE_TIME) ||
                        attributeSchemaDataType.equals(DECIMAL) || attributeSchemaDataType.equals(INTEGER) ||
                        attributeSchemaDataType.equals(REFERENCE)) {

                    if (!attributeSchema.getMultiValued()) {
                        if (attributeValObj instanceof String || attributeValObj instanceof Boolean ||
                                attributeValObj instanceof Integer ||
                                JSONObject.NULL.equals(attributeValObj) || attributeValObj == null) {
                            //If an attribute is passed without a value, no need to save it.
                            if (attributeValObj == null) {
                                continue;
                            }
                            //if the corresponding schema data type is String/Boolean/Binary/Decimal/Integer/DataTime
                            // or Reference, it is a SimpleAttribute.
                            scimObject.setAttribute(buildSimpleAttribute
                                    (attributeSchema, attributeValObj), resourceSchema);

                        } else {
                            logger.error("Error decoding the simple attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    } else {
                        if (attributeValObj instanceof JSONArray || attributeValObj == null) {
                            //If an attribute is passed without a value, no need to save it.
                            if (attributeValObj == null) {
                                continue;
                            }

                            scimObject.setAttribute(buildPrimitiveMultiValuedAttribute(attributeSchema,
                                    (JSONArray) attributeValObj), resourceSchema);
                        } else {
                            logger.error("Error decoding the primitive multivalued attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                } else if (attributeSchemaDataType.equals(COMPLEX)) {
                    if (attributeSchema.getMultiValued() == true) {
                        if (attributeValObj instanceof JSONArray || attributeValObj == null) {
                            if (attributeValObj == null) {
                                continue;
                            }
                            //if the corresponding json value object is JSONArray, it is a MultiValuedAttribute.
                            scimObject.setAttribute(buildComplexMultiValuedAttribute(attributeSchema,
                                    (JSONArray) attributeValObj), resourceSchema);
                        } else {
                            logger.error("Error decoding the complex multivalued attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    } else if (attributeSchema.getMultiValued() == false) {
                        if (attributeValObj instanceof JSONObject || attributeValObj == null) {
                            if (attributeValObj == null) {
                                continue;
                            }
                            //if the corresponding json value object is JSONObject, it is a ComplexAttribute.
                            scimObject.setAttribute(buildComplexAttribute(attributeSchema,
                                    (JSONObject) attributeValObj), resourceSchema);
                        } else {
                            logger.error("Error decoding the complex attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                }
            }
            return scimObject;
        } catch (JSONException e) {
            logger.error("json error in decoding the resource");
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    /*
     * Return a simple attribute with the user defined value included and necessary attribute characteristics set
     *
     * @param attributeSchema - Attribute schema
     * @param attributeValue  - value for the attribute
     * @return SimpleAttribute
     */
    public SimpleAttribute buildSimpleAttribute(AttributeSchema attributeSchema,
                                                Object attributeValue) throws CharonException, BadRequestException {
        Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                attributeValue, attributeSchema.getType());
        SimpleAttribute simpleAttribute = new SimpleAttribute(attributeSchema.getName(), attributeValueObject);
        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                simpleAttribute);
    }

    /*
     * Return complex type multi valued attribute with the user defined
     * value included and necessary attribute characteristics set
     * @param attributeSchema - Attribute schema
     * @param attributeValues - values for the attribute
     * @return MultiValuedAttribute
     */
    public MultiValuedAttribute buildComplexMultiValuedAttribute
    (AttributeSchema attributeSchema, JSONArray attributeValues)
            throws CharonException, BadRequestException {
        try {
            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());

            List<Attribute> complexAttributeValues = new ArrayList<Attribute>();
            List<Object> simpleAttributeValues = new ArrayList<>();

            //iterate through JSONArray and create the list of string values.
            for (int i = 0; i < attributeValues.length(); i++) {
                Object attributeValue = attributeValues.get(i);
                if (attributeValue instanceof JSONObject) {
                    JSONObject complexAttributeValue = (JSONObject) attributeValue;
                    complexAttributeValues.add(buildComplexValue(attributeSchema, complexAttributeValue));
                } else if (attributeValue instanceof String || attributeValue instanceof Integer || attributeValue
                        instanceof Double || attributeValue instanceof Boolean || attributeValue == null) {
                    if (logger.isDebugEnabled()) {
                        if (attributeValue != null) {
                            logger.debug(
                                    "Primitive attribute type detected. Attribute type: " + attributeValue.getClass()
                                            .getName() + ", attribute value: " + attributeValue);
                        } else {
                            logger.debug("Attribute value is null.");
                        }
                    }
                    // If an attribute is passed without a value, no need to save it.
                    if (attributeValue == null) {
                        continue;
                    }
                    simpleAttributeValues.add(attributeValue);
                } else {
                    String error = "Unknown JSON representation for the MultiValued attribute " +
                            attributeSchema.getName() + " which has data type as " + attributeSchema.getType();
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_SYNTAX);
                }

            }
            multiValuedAttribute.setAttributeValues(complexAttributeValues);
            multiValuedAttribute.setAttributePrimitiveValues(simpleAttributeValues);

            return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                    multiValuedAttribute);
        } catch (JSONException e) {
            String error = "Error in accessing JSON value of multivalued attribute";
            throw new CharonException(error, e);
        }
    }

    /*
     * Return a primitive type multi valued attribute with the user defined value included and necessary
     * attribute characteristics set
     *
     * @param attributeSchema - Attribute schema
     * @param attributeValues - values for the attribute
     * @return MultiValuedAttribute
     */
    public MultiValuedAttribute buildPrimitiveMultiValuedAttribute(AttributeSchema attributeSchema,
                                                                    JSONArray attributeValues)
            throws CharonException, BadRequestException {
        try {
            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());

            List<Object> primitiveValues = new ArrayList<Object>();

            //iterate through JSONArray and create the list of string values.
            for (int i = 0; i < attributeValues.length(); i++) {
                Object attributeValue = attributeValues.get(i);
                if (attributeValue instanceof String || attributeValue instanceof Boolean ||
                        attributeValue instanceof Integer || attributeValue == null) {
                    //If an attribute is passed without a value, no need to save it.
                    if (attributeValue == null) {
                        continue;
                    }
                    primitiveValues.add(attributeValue);
                } else {
                    String error = "Unknown JSON representation for the MultiValued attribute " +
                            attributeSchema.getName() + " which has data type as " + attributeSchema.getType();
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_SYNTAX);
                }

            }
            multiValuedAttribute.setAttributePrimitiveValues(primitiveValues);

            return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                    multiValuedAttribute);
        } catch (JSONException e) {
            String error = "Error in accessing JSON value of multivalued attribute";
            throw new CharonException(error, e);
        }
    }

    /*
     * Return a complex attribute with the user defined sub values included and necessary attribute characteristics set
     *
     * @param complexAttributeSchema - complex attribute schema
     * @param jsonObject             - sub attributes values for the complex attribute
     * @return ComplexAttribute
     */
    public ComplexAttribute buildComplexAttribute(AttributeSchema complexAttributeSchema,
                                                  JSONObject jsonObject)
            throws BadRequestException, CharonException, InternalErrorException, JSONException {
        ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeSchema.getName());
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        //list of sub attributes of the complex attribute
        List<AttributeSchema> subAttributeSchemas =
                ((AttributeSchema) complexAttributeSchema).getSubAttributeSchemas();

        //iterate through the complex attribute schema and extract the sub attributes.
        for (AttributeSchema subAttributeSchema : subAttributeSchemas) {
            //obtain the user defined value for given key- attribute schema name
            Object attributeValObj = jsonObject.opt(subAttributeSchema.getName());
            SCIMDefinitions.DataType subAttributeSchemaType = subAttributeSchema.getType();
            if (subAttributeSchemaType.equals(STRING) || subAttributeSchemaType.equals(BINARY) ||
                    subAttributeSchemaType.equals(BOOLEAN) || subAttributeSchemaType.equals(DATE_TIME) ||
                    subAttributeSchemaType.equals(DECIMAL) || subAttributeSchemaType.equals(INTEGER) ||
                    subAttributeSchemaType.equals(REFERENCE)) {
                if (!subAttributeSchema.getMultiValued()) {
                    if (attributeValObj instanceof String || attributeValObj instanceof Boolean ||
                            attributeValObj instanceof Integer ||
                            JSONObject.NULL.equals(attributeValObj) || attributeValObj == null) {
                        //If an attribute is passed without a value, no need to save it.
                        if (attributeValObj == null) {
                            continue;
                        }
                        //if the corresponding schema data type is String/Boolean/Binary/Decimal/Integer/DataTime
                        // or Reference, it is a SimpleAttribute.
                        subAttributesMap.put(subAttributeSchema.getName(),
                                buildSimpleAttribute(subAttributeSchema, attributeValObj));
                    } else {
                        logger.error("Error decoding the sub attribute");
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                } else {
                    if (attributeValObj instanceof JSONArray || attributeValObj == null) {
                        //If an attribute is passed without a value, no need to save it.
                        if (attributeValObj == null) {
                            continue;
                        }
                        subAttributesMap.put(subAttributeSchema.getName(),
                                buildPrimitiveMultiValuedAttribute(subAttributeSchema, (JSONArray) attributeValObj));
                    } else {
                        logger.error("Error decoding the sub attribute");
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                }
                //this case is only valid for the extension schema
                //As according to the spec we have complex attribute inside complex attribute only for extension,
                //we need to treat it separately
            } else if (complexAttributeSchema.getName().equals(
                    SCIMResourceSchemaManager.getInstance().getExtensionName())) {
                if (subAttributeSchemaType.equals(COMPLEX)) {
                    //check for user defined extension's schema violation
                    List<AttributeSchema> subList = subAttributeSchema.getSubAttributeSchemas();
                    for (AttributeSchema attributeSchema : subList) {
                        if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            String error = "Complex attribute can not have complex sub attributes";
                            throw new InternalErrorException(error);
                        }
                    }
                    if (subAttributeSchema.getMultiValued() == true) {
                        if (attributeValObj instanceof JSONArray || attributeValObj == null) {
                            if (attributeValObj == null) {
                                continue;
                            }
                            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute
                                    (subAttributeSchema.getName());
                            JSONArray attributeValues = null;

                            List<Attribute> complexAttributeValues = new ArrayList<Attribute>();
                            try {
                                attributeValues = (JSONArray) attributeValObj;
                            } catch (Exception e) {
                                logger.error("Error decoding the extension");
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            //iterate through JSONArray and create the list of string values.
                            for (int i = 0; i < attributeValues.length(); i++) {
                                Object attributeValue = attributeValues.get(i);
                                if (attributeValue instanceof JSONObject) {
                                    JSONObject complexAttributeValue = (JSONObject) attributeValue;
                                    complexAttributeValues.add(buildComplexValue(subAttributeSchema,
                                            complexAttributeValue));
                                } else {
                                    String error = "Unknown JSON representation for the MultiValued attribute " +
                                            subAttributeSchema.getName() +
                                            " which has data type as " + subAttributeSchema.getType();
                                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_SYNTAX);
                                }
                                multiValuedAttribute.setAttributeValues(complexAttributeValues);

                                MultiValuedAttribute complexMultiValuedSubAttribute = (MultiValuedAttribute)
                                        DefaultAttributeFactory.createAttribute(subAttributeSchema,
                                                multiValuedAttribute);
                                subAttributesMap.put(complexMultiValuedSubAttribute.getName(),
                                        complexMultiValuedSubAttribute);

                            }
                        } else {
                            logger.error("Error decoding the extension sub attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    } else {
                        if (attributeValObj instanceof JSONObject || attributeValObj == null) {
                            if (attributeValObj == null) {
                                continue;
                            }
                            ComplexAttribute complexSubAttribute =
                                    buildComplexAttribute(subAttributeSchema, (JSONObject) attributeValObj);
                            subAttributesMap.put(complexSubAttribute.getName(), complexSubAttribute);
                        } else {
                            logger.error("Error decoding the extension sub attribute");
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                }
            } else {
                String error = "Complex attribute can not have complex sub attributes";
                throw new InternalErrorException(error);
            }

        }
        complexAttribute.setSubAttributesList(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(complexAttributeSchema, complexAttribute);
    }


    /*
     * To build a complex type value of a Multi Valued Attribute. (eg. Email with value,type,primary as sub attributes
     *
     * @param attributeSchema
     * @param jsonObject
     * @return ComplexAttribute
     */
    private ComplexAttribute buildComplexValue(AttributeSchema attributeSchema,
                                               JSONObject jsonObject) throws CharonException, BadRequestException {

        ComplexAttribute complexAttribute = new ComplexAttribute(attributeSchema.getName());
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        List<AttributeSchema> subAttributeSchemas =
                ((AttributeSchema) attributeSchema).getSubAttributeSchemas();

        for (AttributeSchema subAttributeSchema : subAttributeSchemas) {
            Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
            //setting up a name for the complex attribute for the reference purpose
            if (subAttributeSchema.getName().equals(SCIMConstants.CommonSchemaConstants.VALUE)) {
                //(value,type) pair is considered as a primary key for each entry
                if (subAttributeValue != null) {
                    Object subAttributeValueForType = jsonObject.opt(SCIMConstants.CommonSchemaConstants.TYPE);
                    if (subAttributeValueForType != null) {
                        complexAttribute.setName(attributeSchema.getName() + "_" +
                                subAttributeValue + "_" + subAttributeValueForType);
                    } else {
                        complexAttribute.setName(attributeSchema.getName() + "_" +
                                subAttributeValue + "_" + SCIMConstants.DEFAULT);
                    }
                } else {
                    Object subAttributeValueFortype = jsonObject.opt(SCIMConstants.CommonSchemaConstants.TYPE);
                    if (subAttributeValueFortype != null) {
                        complexAttribute.setName(attributeSchema.getName() + "_" +
                                SCIMConstants.DEFAULT + "_" + subAttributeValueFortype);
                    } else {
                        complexAttribute.setName(attributeSchema.getName() + "_" +
                                SCIMConstants.DEFAULT + "_" + SCIMConstants.DEFAULT);
                    }
                }
            }
            if (subAttributeValue != null) {
                if (subAttributeSchema.getMultiValued()) {
                    if (subAttributeValue instanceof JSONArray) {

                        MultiValuedAttribute multiValuedAttribute =
                                buildPrimitiveMultiValuedAttribute(subAttributeSchema, (JSONArray) subAttributeValue);
                        //let the attribute factory to set the sub attribute of a complex
                        // attribute to detect schema violations.
                        multiValuedAttribute = (MultiValuedAttribute)
                                DefaultAttributeFactory.createAttribute(subAttributeSchema,
                                        multiValuedAttribute);
                        subAttributesMap.put(subAttributeSchema.getName(), multiValuedAttribute);

                    } else {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                } else {
                    if (subAttributeValue instanceof String || subAttributeValue instanceof Boolean ||
                            JSONObject.NULL.equals(subAttributeValue) || subAttributeValue instanceof Integer) {

                        SimpleAttribute simpleAttribute =
                                buildSimpleAttribute(subAttributeSchema, subAttributeValue);
                        //let the attribute factory to set the sub attribute of a complex
                        // attribute to detect schema violations.
                        simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(subAttributeSchema,
                                simpleAttribute);
                        subAttributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                    } else {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                }


            }
        }
        complexAttribute.setSubAttributesList(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                complexAttribute);

    }

    /*
     * This method is to extract operations from the PATCH request body and create separate PatchOperation objects
     * for each operation
     * @param scimResourceString
     * @return
     */
    public ArrayList<PatchOperation> decodeRequest(String scimResourceString) throws BadRequestException {


        ArrayList<PatchOperation> operationList = new ArrayList<PatchOperation>();
        try {
            //decode the string into json representation
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            //obtain the Operations values
            JSONArray operationJsonList = (JSONArray) decodedJsonObj.opt(SCIMConstants.OperationalConstants.OPERATIONS);
            //for each operation, create a PatchOperation object and add the relevant values to it
            for (int count = 0; count < operationJsonList.length(); count++) {
                JSONObject operation = (JSONObject) operationJsonList.get(count);
                PatchOperation patchOperation = new PatchOperation();
                String op = (String) operation.opt(SCIMConstants.OperationalConstants.OP);
                if (op.equalsIgnoreCase(SCIMConstants.OperationalConstants.ADD)) {
                    patchOperation.setOperation(SCIMConstants.OperationalConstants.ADD);
                } else if (op.equalsIgnoreCase(SCIMConstants.OperationalConstants.REMOVE)) {
                    patchOperation.setOperation(SCIMConstants.OperationalConstants.REMOVE);
                } else if (op.equalsIgnoreCase(SCIMConstants.OperationalConstants.REPLACE)) {
                    patchOperation.setOperation(SCIMConstants.OperationalConstants.REPLACE);
                }
                patchOperation.setPath((String) operation.opt(SCIMConstants.OperationalConstants.PATH));
                patchOperation.setValues(operation.opt(SCIMConstants.OperationalConstants.VALUE));
                operationList.add(patchOperation);
            }
        } catch (JSONException e) {
            logger.error("json error in decoding the request");
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
        return  operationList;
    }

    public AbstractSCIMObject decode(String scimResourceString, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException {
        try {
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            AbstractSCIMObject scimObject = null;
            if (schema.getSchemasList().contains(SCIMConstants.GROUP_CORE_SCHEMA_URI)) {
                scimObject = (AbstractSCIMObject) decodeResource(decodedJsonObj.toString(), schema, new Group());
            } else  {
                scimObject = (AbstractSCIMObject) decodeResource(decodedJsonObj.toString(), schema, new User());
            }
            return scimObject;

        } catch (JSONException | InternalErrorException | CharonException e) {
            throw new CharonException("Error in decoding the request", e);
        } catch (BadRequestException e) {
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    /*
     * decode the raw string and create a search object
     * @param scimResourceString
     * @return
     * @throws BadRequestException
     */
    public SearchRequest decodeSearchRequestBody(String scimResourceString,
                                                 SCIMResourceTypeSchema schema) throws BadRequestException {
        FilterTreeManager filterTreeManager = null;
        Node rootNode = null;

        //decode the string and create search object
        try {
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            SearchRequest searchRequest = new SearchRequest();
            ArrayList<String> attributes = new ArrayList<>();
            ArrayList<String> excludedAttributes = new ArrayList<>();

            JSONArray attributesValues = (JSONArray)
                    decodedJsonObj.opt(SCIMConstants.OperationalConstants.ATTRIBUTES);
            JSONArray excludedAttributesValues = (JSONArray)
                    decodedJsonObj.opt(SCIMConstants.OperationalConstants.EXCLUDED_ATTRIBUTES);
            JSONArray schemas = (JSONArray)
                    decodedJsonObj.opt(SCIMConstants.CommonSchemaConstants.SCHEMAS);

            if (schemas.length() != 1) {
                throw new BadRequestException("Schema is invalid", ResponseCodeConstants.INVALID_VALUE);
            }
            if (attributesValues != null) {
                for (int i = 0; i < attributesValues.length(); i++) {
                    attributes.add((String) attributesValues.get(i));
                }
            }
            if (excludedAttributesValues != null) {
                for (int i = 0; i < excludedAttributesValues.length(); i++) {
                    excludedAttributes.add((String) excludedAttributesValues.get(i));
                }
            }

            if (decodedJsonObj.opt(SCIMConstants.OperationalConstants.FILTER) != null) {
                filterTreeManager = new FilterTreeManager(
                        (String) decodedJsonObj.opt(SCIMConstants.OperationalConstants.FILTER), schema);
                rootNode = filterTreeManager.buildTree();
            }
            searchRequest.setAttributes(attributes);
            searchRequest.setExcludedAttributes(excludedAttributes);
            searchRequest.setSchema((String) schemas.get(0));
            searchRequest.setCountStr(decodedJsonObj.optString(SCIMConstants.OperationalConstants.COUNT));
            searchRequest.setStartIndexStr(decodedJsonObj.optString(SCIMConstants.OperationalConstants.START_INDEX));
            searchRequest.setDomainName(decodedJsonObj.optString(SCIMConstants.OperationalConstants.DOMAIN));
            searchRequest.setFilter(rootNode);
            if (!decodedJsonObj.optString(SCIMConstants.OperationalConstants.SORT_BY).equals("")) {
                searchRequest.setSortBy(decodedJsonObj.optString(SCIMConstants.OperationalConstants.SORT_BY));
            }
            if (!decodedJsonObj.optString(SCIMConstants.OperationalConstants.SORT_ORDER).equals("")) {
                searchRequest.setSortOder(decodedJsonObj.optString(SCIMConstants.OperationalConstants.SORT_ORDER));
            }
            return searchRequest;

        } catch (JSONException | IOException e) {
            logger.error("Error while decoding the resource string");
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    /**
     * Decode BulkRequestData Json Sting.
     *
     * @param bulkResourceString
     * @return BulkRequestData Object
     */
    public BulkRequestData decodeBulkData(String bulkResourceString) throws BadRequestException {

        BulkRequestData bulkRequestDataObject = new BulkRequestData();
        List<BulkRequestContent> usersEndpointOperationList = new ArrayList<BulkRequestContent>();
        List<BulkRequestContent> groupsEndpointOperationList = new ArrayList<BulkRequestContent>();
        int failOnErrorsAttribute = 0;
        List<String> schemas = new ArrayList<String>();

        JSONObject decodedObject = null;
        try {
            decodedObject = new JSONObject(new JSONTokener(bulkResourceString));

            //prepare the schema list
            JSONArray membersAttributeSchemas = (JSONArray) decodedObject.opt(
                    SCIMConstants.CommonSchemaConstants.SCHEMAS);
            for (int i = 0; i < membersAttributeSchemas.length(); i++) {
                schemas.add(membersAttributeSchemas.get(i).toString());
            }
            bulkRequestDataObject.setSchemas(schemas);

            //get [operations] from the Json String and prepare the request List
            JSONArray membersAttributeOperations = (JSONArray) decodedObject.opt(
                    SCIMConstants.OperationalConstants.OPERATIONS);

            for (int i = 0; i < membersAttributeOperations.length(); i++) {
                JSONObject member = (JSONObject) membersAttributeOperations.get(i);
                //Request path - /Users or /Groups
                String requestType = member.optString(SCIMConstants.OperationalConstants.PATH);
                if (requestType == null) {
                    throw new BadRequestException("Missing required attribute : path",
                            ResponseCodeConstants.INVALID_SYNTAX);
                }
                //Request method  - POST,PUT..etc
                String requestMethod = member.optString(SCIMConstants.OperationalConstants.METHOD);
                if (requestMethod == null) {
                    throw new BadRequestException("Missing required attribute : method",
                            ResponseCodeConstants.INVALID_SYNTAX);
                }
                //Request version
                String requestVersion = member.optString(SCIMConstants.OperationalConstants.VERSION);

                if (requestMethod.equals(SCIMConstants.OperationalConstants.POST)) {

                    if (!member.optString(SCIMConstants.OperationalConstants.BULK_ID).equals("") &&
                            member.optString(SCIMConstants.OperationalConstants.BULK_ID) != null) {


                        setRequestData(requestType, requestMethod, requestVersion,
                                member, usersEndpointOperationList, groupsEndpointOperationList);
                    } else {
                        String error = "JSON string could not be decoded properly.Required " +
                                "attribute BULK_ID is missing in the request";
                        logger.error(error);
                        throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                    }
                } else  {
                    setRequestData(requestType, requestMethod, requestVersion,
                            member, usersEndpointOperationList, groupsEndpointOperationList);
                }
            }
            //extract [failOnErrors] attribute from Json string
            failOnErrorsAttribute = decodedObject.optInt(SCIMConstants.OperationalConstants.FAIL_ON_ERRORS);

            bulkRequestDataObject.setFailOnErrors(failOnErrorsAttribute);
            bulkRequestDataObject.setUserOperationRequests(usersEndpointOperationList);
            bulkRequestDataObject.setGroupOperationRequests(groupsEndpointOperationList);

        } catch (JSONException e1) {
            String error = "JSON string could not be decoded properly.";
            logger.error(error);
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
        return bulkRequestDataObject;
    }


    private void setRequestData(String requestType, String requestMethod,
                                String requestVersion, JSONObject member,
                                List<BulkRequestContent> usersEndpointOperationList,
                                List<BulkRequestContent> groupsEndpointOperationList) {
        //create user request list
        if (requestType.contains(SCIMConstants.USER_ENDPOINT)) {
            BulkRequestContent newRequestData =
                    getBulkRequestContent(member, requestMethod, requestType, requestVersion);

            usersEndpointOperationList.add(newRequestData);
        }

        //create group request list
        if (requestType.contains(SCIMConstants.GROUP_ENDPOINT)) {
            BulkRequestContent newRequestData =
                    getBulkRequestContent(member, requestMethod, requestType, requestVersion);

            groupsEndpointOperationList.add(newRequestData);

        }
    }

    private BulkRequestContent getBulkRequestContent(JSONObject member, String requestMethod,
                                                     String requestType, String requestVersion) {
        BulkRequestContent newRequestData = new BulkRequestContent();

        newRequestData.setData(member.optString(SCIMConstants.OperationalConstants.DATA));
        newRequestData.setBulkID(member.optString(SCIMConstants.OperationalConstants.BULK_ID));
        newRequestData.setMethod(requestMethod);
        newRequestData.setPath(requestType);
        newRequestData.setVersion(requestMethod);
        newRequestData.setVersion(requestVersion);

        return newRequestData;
    }

}


