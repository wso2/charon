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

package org.wso2.charon3.core.utils;

import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class will act as a support class for endpoints..
 */
public class ResourceManagerUtil {

    /*
     * this method is to get the uri list of the attributes which need to retrieved from the databases.
     * Note that we should consider the 'attributes' and 'excludedAttributes' parameters for this process.
     *
     * @param schema
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @return
     * @throws CharonException
     */
    public static Map<String, Boolean> getOnlyRequiredAttributesURIs(SCIMResourceTypeSchema schema,
                                                                     String requestedAttributes,
                                                                     String requestedExcludingAttributes)
            throws CharonException {

        ArrayList<AttributeSchema> attributeSchemaArrayList = (ArrayList<AttributeSchema>)
                CopyUtil.deepCopy(schema.getAttributesList());

        List<String> requestedAttributesList = null;
        List<String> requestedExcludingAttributesList = null;

        if (requestedAttributes != null) {
            //make a list from the comma separated requestedAttributes
            requestedAttributesList = Arrays.asList(requestedAttributes.split(","));
        }
        if (requestedExcludingAttributes != null) {
            //make a list from the comma separated requestedExcludingAttributes
            requestedExcludingAttributesList = Arrays.asList(requestedExcludingAttributes.split(","));
        }

        ArrayList<AttributeSchema> attributeList = schema.getAttributesList();

        for (AttributeSchema attributeSchema : attributeList) {
            //check for never/request attributes.
            if (attributeSchema.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                removeAttributesFromList(attributeSchemaArrayList, attributeSchema.getName());
            }
            //if the returned property is request, need to check whether is it specifically requested by the user.
            // If so return it.
            if (requestedAttributes == null && requestedExcludingAttributes == null) {
                if (attributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    removeAttributesFromList(attributeSchemaArrayList, attributeSchema.getName());
                }
            } else {
                //A request should only contains either attributes or exclude attribute params. Not both
                if (requestedAttributes != null) {
                    //if attributes are set, delete all the request and default attributes
                    //and add only the requested attributes
                    if ((attributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                            || attributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                            && (!requestedAttributesList.contains(attributeSchema.getName())
                            && !isSubAttributeExistsInList(requestedAttributesList, attributeSchema))) {
                        removeAttributesFromList(attributeSchemaArrayList, attributeSchema.getName());
                    }
                } else if (requestedExcludingAttributes != null) {
                    //removing attributes which has returned as request. This is because no request is made
                    if (attributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                        removeAttributesFromList(attributeSchemaArrayList, attributeSchema.getName());
                    }
                    //if exclude attribute is set, set of exclude attributes need to be
                    // removed from the default set of attributes
                    if ((attributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                            && requestedExcludingAttributesList.contains(attributeSchema.getName())) {
                        removeAttributesFromList(attributeSchemaArrayList, attributeSchema.getName());
                    }
                }
            }
            getOnlyRequiredSubAttributesURIs(attributeSchema, attributeSchemaArrayList,
                    requestedAttributes, requestedExcludingAttributes,
                    requestedAttributesList, requestedExcludingAttributesList);
        }
        return convertSchemasToURIs(attributeSchemaArrayList);
    }

    /*
     * this method is to get the uri list of the sub attributes which need to retrieved from the databases.
     * Note that we should consider the 'attributes' and 'excludedAttributes' parameters for this process.
     *
     * @param attributeSchema
     * @param attributeSchemaArrayList
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @throws CharonException
     */
    private static void getOnlyRequiredSubAttributesURIs(AttributeSchema attributeSchema,
                                                         ArrayList<AttributeSchema> attributeSchemaArrayList,
                                                         String requestedAttributes,
                                                         String requestedExcludingAttributes,
                                                         List<String> requestedAttributesList,
                                                         List<String> requestedExcludingAttributesList)
            throws CharonException {
        if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {

            AttributeSchema realAttributeSchema = null;
            //need to get the right reference first as we are going to delete by reference.
            for (AttributeSchema schema : attributeSchemaArrayList) {
                if (schema.getName().equals(attributeSchema.getName())) {
                    realAttributeSchema = schema;
                    break;
                }
            }
            if (realAttributeSchema != null) {
                List<AttributeSchema> subAttributeList = attributeSchema.getSubAttributeSchemas();

                for (AttributeSchema subAttributeSchema : subAttributeList) {

                    //check for never/request attributes.
                    if (subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                        realAttributeSchema.removeSubAttribute(subAttributeSchema.getName());
                    }
                    //if the returned property is request, need to check whether is it specifically requested by the
                    // user.
                    // If so return it.
                    if (requestedAttributes == null && requestedExcludingAttributes == null) {
                        if (subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                            realAttributeSchema.removeSubAttribute(subAttributeSchema.getName());
                        }
                    } else {
                        //A request should only contains either attributes or exclude attribute params. Not both
                        if (requestedAttributes != null) {
                            //if attributes are set, delete all the request and default attributes
                            //and add only the requested attributes
                            if ((subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                                    || subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                                    && (!requestedAttributesList.contains(attributeSchema.getName() + "." +
                                    subAttributeSchema.getName())
                                    && !isSubSubAttributeExistsInList(requestedAttributesList,
                                    attributeSchema, subAttributeSchema))
                                    && (!requestedAttributesList.contains(attributeSchema.getName()))) {
                                realAttributeSchema.removeSubAttribute(subAttributeSchema.getName());
                            }
                        } else if (requestedExcludingAttributes != null) {
                            //removing attributes which has returned as request. This is because no request is made
                            if (subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                                realAttributeSchema.removeSubAttribute(subAttributeSchema.getName());
                            }
                            //if exclude attribute is set, set of exclude attributes need to be
                            // removed from the default set of attributes
                            if ((subAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                                    && requestedExcludingAttributesList.contains(attributeSchema.getName()
                                    + "." + subAttributeSchema.getName())) {
                                realAttributeSchema.removeSubAttribute(subAttributeSchema.getName());
                            }
                        }
                    }
                    getOnlyRequiredSubSubAttributesURIs(attributeSchema, subAttributeSchema,
                            attributeSchemaArrayList, requestedAttributes,
                            requestedExcludingAttributes, requestedAttributesList,
                            requestedExcludingAttributesList);
                }
            }
        }
    }

    /*
     * this method is to get the uri list of the sub sub attributes which need to retrieved from the databases.
     * Note that we should consider the 'attributes' and 'excludedAttributes' parameters for this process.
     *
     * @param attribute
     * @param subAttribute
     * @param attributeSchemaArrayList
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @throws CharonException
     */
    private static void getOnlyRequiredSubSubAttributesURIs(AttributeSchema attribute,
                                                            AttributeSchema subAttribute,
                                                            ArrayList<AttributeSchema> attributeSchemaArrayList,
                                                            String requestedAttributes,
                                                            String requestedExcludingAttributes,
                                                            List<String> requestedAttributesList,
                                                            List<String> requestedExcludingAttributesList)
            throws CharonException {

        if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {

            AttributeSchema realAttributeSchema = null;
            //need to get the right reference first as we are going to delete by reference
            for (AttributeSchema schema : attributeSchemaArrayList) {
                List<AttributeSchema> subSchemas = schema.getSubAttributeSchemas();
                if (subSchemas != null) {
                    for (AttributeSchema subSchema : subSchemas) {
                        if (subSchema.getURI().equals(subAttribute.getURI())) {
                            realAttributeSchema = subSchema;
                            break;
                        }
                    }
                }
            }
            if (realAttributeSchema != null) {
                List<AttributeSchema> subSubAttributeList = subAttribute.getSubAttributeSchemas();

                for (AttributeSchema subSubAttributeSchema : subSubAttributeList) {

                    //check for never/request attributes.
                    if (subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                        realAttributeSchema.removeSubAttribute(subSubAttributeSchema.getName());
                    }
                    //if the returned property is request, need to check whether is it specifically requested by the
                    // user.
                    // If so return it.
                    if (requestedAttributes == null && requestedExcludingAttributes == null) {
                        if (subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                            realAttributeSchema.removeSubAttribute(subSubAttributeSchema.getName());
                        }
                    } else {
                        //A request should only contains either attributes or exclude attribute params. Not both
                        if (requestedAttributes != null) {
                            //if attributes are set, delete all the request and default attributes
                            //and add only the requested attributes
                            if ((subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                                    || subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                                    && (!requestedAttributesList.contains(attribute.getName() + "." +
                                    subAttribute.getName() + "." + subSubAttributeSchema.getName()))
                                    && (!requestedAttributesList.contains(attribute.getName()))
                                    && (!requestedAttributesList.contains(attribute.getName() + "." + subAttribute
                                    .getName()))) {
                                realAttributeSchema.removeSubAttribute(subSubAttributeSchema.getName());
                            }
                        } else if (requestedExcludingAttributes != null) {
                            //removing attributes which has returned as request. This is because no request is made
                            if (subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                                realAttributeSchema.removeSubAttribute(subSubAttributeSchema.getName());
                            }
                            //if exclude attribute is set, set of exclude attributes need to be
                            // removed from the default set of attributes
                            if ((subSubAttributeSchema.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                                    && requestedExcludingAttributesList.contains(attribute.getName() +
                                    "." + subAttribute.getName() + "." + subSubAttributeSchema.getName())) {
                                realAttributeSchema.removeSubAttribute(subSubAttributeSchema.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * this checks whether the sub attribute or sub sub attribute is exist in the given list.
     *
     * @param requestedAttributes
     * @param attributeSchema
     * @return
     */
    private static boolean isSubAttributeExistsInList(List<String> requestedAttributes, AttributeSchema
            attributeSchema) {
        if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
            List<AttributeSchema> subAttributeSchemas = attributeSchema.getSubAttributeSchemas();

            for (AttributeSchema subAttributeSchema : subAttributeSchemas) {
                if (requestedAttributes.contains(attributeSchema.getName() + "." + subAttributeSchema.getName())) {
                    return true;
                }

                if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    List<AttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();

                    for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemas) {
                        if (requestedAttributes.contains(
                                attributeSchema.getName() + "." +
                                        subAttributeSchema.getName() + "." +
                                        subSubAttributeSchema.getName())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /*
     * this checks whether sub attribute is exist in the given list.
     *
     * @param requestedAttributes
     * @param attributeSchema
     * @param subAttributeSchema
     * @return
     */
    private static boolean isSubSubAttributeExistsInList(List<String> requestedAttributes,
                                                         AttributeSchema attributeSchema,
                                                         AttributeSchema subAttributeSchema) {

        if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
            List<AttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();

            for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemas) {
                if (requestedAttributes.contains(attributeSchema.getName() + "." +
                        subAttributeSchema.getName() + "." + subSubAttributeSchema.getName())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /*
     * this makes a list of URIs from the list of schemas.
     *
     * @param schemas
     * @return
     */
    private static Map<String, Boolean> convertSchemasToURIs(List<AttributeSchema> schemas) {

        Map<String, Boolean> uriList = new HashMap<>();
        for (AttributeSchema schema : schemas) {
            if (schema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                List<AttributeSchema> subAttributeSchemas = schema.getSubAttributeSchemas();
                for (AttributeSchema subAttributeSchema : subAttributeSchemas) {
                    if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        List<AttributeSchema> subSubAttributeSchemas = subAttributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemas) {
                            uriList.put(subSubAttributeSchema.getURI(), subAttributeSchema.getMultiValued());
                        }
                    } else {
                        uriList.put(subAttributeSchema.getURI(), schema.getMultiValued());
                    }
                }
            } else {
                uriList.put(schema.getURI(), schema.getMultiValued());
            }
        }
        return uriList;
    }

    /*
     * this is to remove given attribute from the given list.
     *
     * @param attributeSchemaList
     * @param attributeName
     * @throws CharonException
     */
    private static void removeAttributesFromList(List<AttributeSchema> attributeSchemaList, String attributeName)
            throws CharonException {
        List<AttributeSchema> tempList = (List<AttributeSchema>) CopyUtil.deepCopy(attributeSchemaList);
        int count = 0;
        for (AttributeSchema attributeSchema : tempList) {
            if (attributeSchema.getName().equals(attributeName)) {
                attributeSchemaList.remove(count);
            }
            count++;
        }
    }

    public static Map<String, Boolean> getAllAttributeURIs(SCIMResourceTypeSchema schema) throws CharonException {
        return getOnlyRequiredAttributesURIs(schema, null, null);
    }

    /**
     * Process count value according to SCIM 2.0 specification.
     *
     * @param countStr
     * @return
     * @throws BadRequestException
     */
    public static int processCount(String countStr) throws BadRequestException {

        int count;
        if (countStr == null || countStr.trim().isEmpty() || !countStr.matches("\\d+")) {
            count = CharonConfiguration.getInstance().getCountValueForPagination();
        } else {
            try {
                count = Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Value of parameter count is Invalid");
            }
        }

        if (count < 0) {
            count = 0;
        }

        return count;
    }

    /**
     * Process count value according to SCIM 2.0 specification..
     *
     * @param countInt The count value in the request
     * @return Integer according to the passed value. (NOTE: return NULL when the COUNT is not specified in the
     * request)
     */
    public static Integer processCount(Integer countInt) {

        if (countInt == null || countInt.toString().isEmpty()) {
            return null;
        } else {
            // All the negative values are interpreted as zero according to the specification.
            if (countInt <= 0) {
                return 0;
            } else {
                return countInt;
            }
        }
    }

    /**
     * Process startIndex value according to SCIM 2.0 specification.
     *
     * @param startIndex Starting index in the request.
     * @return Integer as the starting index.
     */
    public static Integer processStartIndex(Integer startIndex) {

        if (startIndex == null) {
            // According to SCIM2 spec, default value of startIndex should be one.
            return 1;
        } else if (startIndex >= 1) {
            return startIndex;
        } else {
            // Any value lesser than 1 is interpreted as 1.
            return 1;
        }
    }

    /**
     * Process startIndex value according to SCIM 2.0 specification.
     * @param startIndexStr
     * @return
     * @throws BadRequestException
     */
    public static int processStartIndex(String startIndexStr) throws BadRequestException {

        int startIndex = 1;
        if (startIndexStr == null || startIndexStr.trim().isEmpty() || !startIndexStr.matches("\\d+")) {
            return startIndex;
        }

        try {
            startIndex = Integer.parseInt(startIndexStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Value of parameter startIndex is Invalid");
        }

        if (startIndex < 1) {
            startIndex = 1;
        }

        return startIndex;
    }
}
