/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.aParser.Parser;
import org.wso2.charon3.core.aParser.ParserException;
import org.wso2.charon3.core.aParser.Rule;
import org.wso2.charon3.core.aParser.Rule_valuePath;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.ServerSideValidator;
import org.wso2.charon3.core.utils.codeutils.ExpressionNode;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This provides the methods on the PATCH operation of any resource type.
 */
public class PatchOperationUtil {

    public static final String PATH_RULE_NAME = "PATH";

    private static final Logger log = LoggerFactory.getLogger(PatchOperationUtil.class);

    /*
     * This method corresponds to the remove operation in patch requests.
     * @param operation
     * @param decoder
     * @param oldResource
     * @param copyOfOldResource
     * @return
     * @throws BadRequestException
     * @throws IOException
     * @throws NotImplementedException
     * @throws CharonException
     */
    public static AbstractSCIMObject doPatchRemove(PatchOperation operation, AbstractSCIMObject oldResource,
                                                   AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws BadRequestException, NotImplementedException, CharonException {

        if (operation.getPath() == null) {
            throw new BadRequestException
                    ("No path value specified for remove operation", ResponseCodeConstants.NO_TARGET);
        }

        String path = operation.getPath();
        //split the path to extract the filter if present.
        String[] parts = path.split("[\\[\\]]");

        if (parts.length != 1) {
            //currently we only support simple filters here.
            String[] filterParts = parts[1].split(" ");

            ExpressionNode expressionNode = new ExpressionNode();
            expressionNode.setAttributeValue(filterParts[0]);
            expressionNode.setOperation(filterParts[1]);
            // According to the specification filter attribute value specified with quotation mark, so we need to
            // remove it if exists.
            expressionNode.setValue(filterParts[2].replaceAll("^\"|\"$", ""));

            if (expressionNode.getOperation().equalsIgnoreCase((SCIMConstants.OperationalConstants.EQ).trim())) {

                doPatchRemoveWithFilters(parts, oldResource, expressionNode);
            } else {
                throw new NotImplementedException("Only Eq filter is supported");
            }
        } else {

            doPatchRemoveWithoutFilters(parts, oldResource);
        }
        //validate the updated object
        AbstractSCIMObject validatedResource =  ServerSideValidator.validateUpdatedSCIMObject
                (copyOfOldResource, oldResource, schema);

        return validatedResource;

    }

    /*
     * This is the patch remove operation when the path is specified with a filter in it.
     * @param parts
     * @param oldResource
     * @param expressionNode
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFilters(String[] parts,
                                                               AbstractSCIMObject oldResource,
                                                               ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        if (parts.length == 3) {
            parts[0] = parts[0] + parts[2];
        }
        String[] attributeParts = parts[0].split("[\\.]");

        if (attributeParts.length == 1) {

            doPatchRemoveWithFiltersForLevelOne(oldResource, attributeParts, expressionNode);

        } else if (attributeParts.length == 2) {

            doPatchRemoveWithFiltersForLevelTwo(oldResource, attributeParts, expressionNode);

        } else if (attributeParts.length == 3) {

            doPatchRemoveWithFiltersForLevelThree(oldResource, attributeParts, expressionNode);
        }
        return oldResource;
    }

    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelThree(AbstractSCIMObject oldResource,
                                                                            String[] attributeParts,
                                                                            ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        if (attribute != null) {

            Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);

            if (subAttribute != null) {
                if (subAttribute.getMultiValued()) {

                    List<Attribute> subValues = ((MultiValuedAttribute) subAttribute).getAttributeValues();
                    if (subValues != null) {
                        for (Attribute subValue: subValues) {
                            Map<String, Attribute> subSubAttributes =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            //this map is to avoid concurrent modification exception.
                            Map<String, Attribute> tempSubSubAttributes = (Map<String, Attribute>)
                                    CopyUtil.deepCopy(subSubAttributes);

                            for (Iterator<Attribute> iterator = tempSubSubAttributes.values().iterator();
                                 iterator.hasNext();) {
                                Attribute subSubAttribute = iterator.next();

                                if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {

                                    Attribute removingAttribute = subSubAttributes.get(attributeParts[2]);
                                    if (removingAttribute == null) {
                                        throw new BadRequestException("No such sub attribute with the name : " +
                                                attributeParts[2] + " " + "within the attribute " +
                                                attributeParts[1], ResponseCodeConstants.INVALID_PATH);
                                    }
                                    if (removingAttribute.getMutability().equals
                                            (SCIMDefinitions.Mutability.READ_ONLY) ||
                                            removingAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {

                                        ((ComplexAttribute) subValue).removeSubAttribute
                                                (removingAttribute.getName());
                                    }
                                }
                            }
                        }
                        if (subValues.size() == 0) {
                            //if the attribute has no values, make it unassigned
                            ((ComplexAttribute) attribute).removeSubAttribute(subAttribute.getName());
                        }
                    }
                } else {
                    throw new BadRequestException("Attribute : " + attributeParts[1] + " " +
                            "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                }

            } else {
                throw new BadRequestException("No such sub attribute with the name : " + attributeParts[1] + " " +
                        "within the attribute " + attributeParts[0], ResponseCodeConstants.INVALID_PATH);
            }

        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }

    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelTwo(AbstractSCIMObject oldResource,
                                                                          String[] attributeParts,
                                                                          ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        if (attribute != null) {

            if (attribute.getMultiValued()) {

                List<Attribute> subValues = ((MultiValuedAttribute) attribute).getAttributeValues();
                if (subValues != null) {
                    for (Attribute subValue: subValues) {
                        Map<String, Attribute> subAttributes = ((ComplexAttribute) subValue).getSubAttributesList();
                        //this map is to avoid concurrent modification exception.
                        Map<String, Attribute> tempSubAttributes = (Map<String, Attribute>)
                                CopyUtil.deepCopy(subAttributes);

                        for (Iterator<Attribute> iterator = tempSubAttributes.values().iterator();
                             iterator.hasNext();) {
                            Attribute subAttribute = iterator.next();

                            if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                if (((SimpleAttribute) subAttribute).getValue().equals(expressionNode.getValue())) {
                                    Attribute removingAttribute = subAttributes.get(attributeParts[1]);
                                    if (removingAttribute == null) {
                                        throw new BadRequestException
                                                ("No such sub attribute with the name : " + attributeParts[1] + " " +
                                                        "within the attribute " + attributeParts[0],
                                                        ResponseCodeConstants.INVALID_PATH);
                                    }
                                    if (removingAttribute.getMutability().
                                            equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            removingAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {

                                        ((ComplexAttribute) subValue).removeSubAttribute(removingAttribute.getName());
                                    }
                                }
                            }
                        }
                    }
                    if (subValues.size() == 0) {
                        //if the attribute has no values, make it unassigned
                        oldResource.deleteAttribute(attribute.getName());
                    }
                }
            } else if (attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is only valid for extension
                Attribute subAttribute  = attribute.getSubAttribute(attributeParts[1]);
                if (subAttribute == null) {
                    throw new BadRequestException("No such sub attribute with the name : "
                            + attributeParts[1] + " " + "within the attribute " + attributeParts[0],
                            ResponseCodeConstants.INVALID_PATH);
                }

                List<Attribute> subValues = ((MultiValuedAttribute) (subAttribute)).getAttributeValues();
                if (subValues != null) {
                    for (Iterator<Attribute> subValueIterator = subValues.iterator(); subValueIterator.hasNext();) {
                        Attribute subValue = subValueIterator.next();

                        Map<String, Attribute> subValuesSubAttribute =
                                ((ComplexAttribute) subValue).getSubAttributesList();
                        for (Iterator<Attribute> iterator =
                             subValuesSubAttribute.values().iterator(); iterator.hasNext();) {

                            Attribute subSubAttribute = iterator.next();
                            if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                if (((SimpleAttribute) (subSubAttribute)).getValue().equals
                                        (expressionNode.getValue())) {
                                    if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subValue.getRequired().equals(true)) {

                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        subValueIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    //if the attribute has no values, make it unassigned
                    if (((MultiValuedAttribute) (subAttribute)).getAttributeValues().size() == 0) {
                        ((ComplexAttribute) attribute).removeSubAttribute(subAttribute.getName());
                    }
                }
            } else {
                throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                        "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }


    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelOne(AbstractSCIMObject oldResource,
                                                                          String[] attributeParts,
                                                                          ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);

        if (attribute != null) {
            if (!attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (attribute.getMultiValued()) {

                        List<Object> valuesList  = ((MultiValuedAttribute)
                                (attribute)).getAttributePrimitiveValues();
                        for (Iterator<Object> iterator =
                             valuesList.iterator(); iterator.hasNext();) {
                            Object item = iterator.next();
                            //we only support "EQ" filter
                            if (item.equals(expressionNode.getValue())) {
                                if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        attribute.getRequired().equals(true)) {
                                    throw new BadRequestException
                                            ("Can not remove a required attribute or a read-only attribute",
                                                    ResponseCodeConstants.MUTABILITY);
                                } else {
                                    iterator.remove();
                                }
                            }
                        }
                        //if the attribute has no values, make it unassigned
                        if (((MultiValuedAttribute) (attribute)).getAttributePrimitiveValues().size() == 0) {
                            oldResource.deleteAttribute(attribute.getName());
                        }

                    } else {
                        throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                                "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                    }

            } else {
                if (attribute.getMultiValued()) {
                    //this is for paths value as 'emails[value EQ vindula@wso2.com]'
                    //this is multivalued complex case

                    List<Attribute> subValues = ((MultiValuedAttribute) (attribute)).getAttributeValues();
                    if (subValues != null) {
                        for (Iterator<Attribute> subValueIterator = subValues.iterator();
                             subValueIterator.hasNext();) {
                            Attribute subValue = subValueIterator.next();

                            Map<String, Attribute> subValuesSubAttribute =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            for (Iterator<Attribute> iterator =
                                 subValuesSubAttribute.values().iterator(); iterator.hasNext();) {

                                Attribute subAttribute = iterator.next();
                                if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                    if (((SimpleAttribute)
                                            (subAttribute)).getValue().equals(expressionNode.getValue())) {
                                        if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                                subValue.getRequired().equals(true)) {
                                            throw new BadRequestException
                                                    ("Can not remove a required attribute or a read-only attribute",
                                                            ResponseCodeConstants.MUTABILITY);
                                        } else {
                                            subValueIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        //if the attribute has no values, make it unassigned
                        if (((MultiValuedAttribute) (attribute)).getAttributeValues().size() == 0) {
                            oldResource.deleteAttribute(attribute.getName());
                        }
                    }
                } else {
                    //this is complex attribute which has multi valued primitive sub attribute.
                    Attribute subAttribute = attribute.getSubAttribute(expressionNode.getAttributeValue());
                    if (subAttribute != null) {

                        if (subAttribute.getMultiValued() && !subAttribute.getType().equals
                                (SCIMDefinitions.DataType.COMPLEX)) {
                            List<Object> valuesList  = ((MultiValuedAttribute)
                                    (subAttribute)).getAttributePrimitiveValues();
                            for (Iterator<Object> iterator =
                                 valuesList.iterator(); iterator.hasNext();) {
                                Object item = iterator.next();
                                //we only support "EQ" filter
                                if (item.equals(expressionNode.getValue())) {
                                    if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                            //if the subAttribute has no values, make it unassigned
                            if (((MultiValuedAttribute) (subAttribute)).getAttributePrimitiveValues().size() == 0) {
                                ((ComplexAttribute) (attribute)).removeSubAttribute(subAttribute.getName());
                            }

                        } else {
                            throw new BadRequestException("Sub attribute : " +
                                    expressionNode.getAttributeValue() + " " +
                                    "is not a primitive multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                        }

                    } else {
                        throw new BadRequestException("No sub attribute with the name : " +
                                expressionNode.getAttributeValue() + " " +
                                "in the attribute : " + attributeParts[0], ResponseCodeConstants.INVALID_PATH);
                    }
                }
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }

    /*
     * This is the patch remove operation when the path is specified without a filter in it.
     * @param parts
     * @param oldResource
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithoutFilters
    (String[] parts, AbstractSCIMObject oldResource) throws BadRequestException, CharonException {

        String[] attributeParts = parts[0].split("[\\.]");
        if (attributeParts.length == 1) {

            Attribute attribute = oldResource.getAttribute(parts[0]);

            if (attribute != null) {
                if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                        attribute.getRequired().equals(true)) {
                    throw new BadRequestException("Can not remove a required attribute or a read-only attribute",
                            ResponseCodeConstants.MUTABILITY);
                } else {
                    String attributeName = attribute.getName();
                    oldResource.deleteAttribute(attributeName);
                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                        "in the current resource", ResponseCodeConstants.INVALID_PATH);
            }

        } else {
            Attribute attribute = oldResource.getAttribute(attributeParts[0]);
            if (attribute != null) {
                if (attribute.getMultiValued()) {
                    //this is multivalued complex case
                    List<Attribute> subValuesList = ((MultiValuedAttribute) attribute).getAttributeValues();

                    if (subValuesList != null) {

                        for (Attribute subValue : subValuesList) {
                            Map<String, Attribute> subSubAttributeList =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            //need to remove attributes while iterating through the list.
                            for (Iterator<Attribute> iterator =
                                 subSubAttributeList.values().iterator(); iterator.hasNext();) {
                                Attribute subSubAttribute = iterator.next();

                                if (subSubAttribute.getName().equals(attributeParts[1])) {

                                    if (subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subSubAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }

                } else {
                    Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);
                    if (subAttribute != null) {
                        if (attributeParts.length == 3) {

                            if (subAttribute.getMultiValued()) {

                                List<Attribute> subSubValuesList = ((MultiValuedAttribute)
                                        subAttribute).getAttributeValues();

                                if (subSubValuesList != null) {
                                    for (Attribute subSubValue : subSubValuesList) {
                                        Map<String, Attribute> subSubAttributeList =
                                                ((ComplexAttribute) subSubValue).getSubAttributesList();
                                        //need to remove attributes while iterating through the list.
                                        for (Iterator<Attribute> iterator =
                                             subSubAttributeList.values().iterator(); iterator.hasNext();) {
                                            Attribute subSubAttribute = iterator.next();

                                            if (subSubAttribute.getName().equals(attributeParts[2])) {

                                                if (subSubAttribute.getMutability().equals
                                                        (SCIMDefinitions.Mutability.READ_ONLY) ||
                                                        subSubAttribute.getRequired().equals(true)) {
                                                    throw new BadRequestException
                                                            ("Can not remove a required attribute or a read-only " +
                                                                    "attribute", ResponseCodeConstants.MUTABILITY);
                                                } else {
                                                    iterator.remove();
                                                }
                                            }
                                        }
                                    }
                                }

                            } else {
                                Attribute subSubAttribute = subAttribute.getSubAttribute(attributeParts[2]);
                                if (subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        subSubAttribute.getRequired().equals(true)) {
                                    throw new BadRequestException
                                            ("Can not remove a required attribute or a read-only attribute",
                                                    ResponseCodeConstants.MUTABILITY);
                                } else {
                                    String subSubAttributeName = subSubAttribute.getName();
                                    ((ComplexAttribute) subAttribute).removeSubAttribute(subSubAttributeName);
                                }
                            }

                        } else {
                            //this is complex attribute's sub attribute check
                            if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                    subAttribute.getRequired().equals(true)) {
                                throw new BadRequestException
                                        ("Can not remove a required attribute or a read-only attribute",
                                                ResponseCodeConstants.MUTABILITY);
                            } else {
                                String subAttributeName = subAttribute.getName();
                                ((ComplexAttribute) attribute).removeSubAttribute(subAttributeName);
                            }
                        }
                    } else {
                        throw new BadRequestException("No such sub attribute with the name : " +
                                attributeParts[1] + " " + "in the attribute : " +
                                attributeParts[0], ResponseCodeConstants.INVALID_PATH);
                    }

                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                        "in the current resource", ResponseCodeConstants.INVALID_PATH);
            }
        }
        return oldResource;
    }

    /**
     * This method corresponds to the add operation in patch requests.
     *
     * @param operation         Operation to be performed.
     * @param decoder           JSON decoder.
     * @param oldResource       Original resource SCIM object.
     * @param copyOfOldResource Copy of an original resource.
     * @param schema            SCIM resource schema.
     * @return
     * @throws CharonException
     * @throws BadRequestException
     * @throws NotImplementedException
     * @throws InternalErrorException
     */
    public static AbstractSCIMObject doPatchAdd(PatchOperation operation, JSONDecoder decoder,
            AbstractSCIMObject oldResource, AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        if (operation.getValues() == null) {
            throw new BadRequestException("The value is not provided to perform patch add operation",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }

        if (log.isDebugEnabled()) {
            log.debug("Provided path: " + operation.getPath() + " and value(s): " + operation.getValues().toString());
        }

        if (operation.getPath() != null) {
            doPatchAddOnResourceWithPath(operation, decoder, oldResource, schema, operation.getPath());
        } else {
            doPatchAddOnResource(operation, decoder, oldResource, copyOfOldResource, schema);
        }
        // Validate the updated object.
        AbstractSCIMObject validatedResource = ServerSideValidator
                .validateUpdatedSCIMObject(copyOfOldResource, oldResource, schema);
        return validatedResource;
    }

    /**
     * Perform patch add on the resource according to the specified path.
     *
     * @param operation   Operation to be performed.
     * @param decoder     JSON decoder.
     * @param oldResource Original resource SCIM object.
     * @param schema      SCIM resource schema.
     * @throws CharonException
     * @throws BadRequestException
     * @throws NotImplementedException
     * @throws InternalErrorException
     */
    private static void doPatchAddOnResourceWithPath(PatchOperation operation, JSONDecoder decoder,
            AbstractSCIMObject oldResource, SCIMResourceTypeSchema schema, String path)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        try {
            Rule rule = Parser.parse(PATH_RULE_NAME, operation.getPath());

            if (isFilterConditionProvidedInPath(rule)) {
                // Filter condition has been provided in the path.
                try {
                    doPatchAddOnPathWithFilters(operation, decoder, oldResource, schema);
                } catch (JSONException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Input JSON object/array is invalid, " + operation.getValues().toString());
                    }
                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                }
            } else {
                // Provided path doesn't contain filter condition.
                doPatchAddOnPathWithoutFilters(oldResource, schema, decoder, operation, path);
            }
        } catch (ParserException e) {
            throw new BadRequestException(
                    ("Path value is not a valid syntax according to the SCIM PATCH PATH Rule. path: " + operation
                            .getPath()), ResponseCodeConstants.INVALID_SYNTAX);

        }
    }

    /**
     * Return true when the path contains filter condition(s), else return false. Normally filters surrounded by
     * square bracket.
     * According to SCIM spec Rule: valuePath = attributePath "[" valueFilter "]";
     * Example path with filters: addresses[type eq \"work\"]
     * Example path without filter: name.familyName
     *
     * @param rule
     * @return
     */
    private static boolean isFilterConditionProvidedInPath(Rule rule) {

        if (rule.rules.get(0).rules.size() == 4 && rule.rules.get(0) instanceof Rule_valuePath) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Perform patch add on the resource according to the specified filter condition provided in the path.
     *
     * @param operation             Operation to be performed.
     * @param decoder               JSON decoder.
     * @param oldResource           Original resource SCIM object.
     * @param schema                SCIM resource schema.
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static void doPatchAddOnPathWithFilters(PatchOperation operation, JSONDecoder decoder,
            AbstractSCIMObject oldResource, SCIMResourceTypeSchema schema)
            throws NotImplementedException, BadRequestException, CharonException, JSONException,
            InternalErrorException {

        String path = operation.getPath();
        // Split the path to extract the filter.
        String[] parts = path.split("[\\[\\]]");
        // Since the filter condition has been provided, we can consider this use-case behaviour as patch
        // replace with filters. So passing this to patch replace on path with filters method.
        doPatchReplaceOnPathWithFilters(oldResource, schema, decoder, operation, parts);
    }

    /**
     * Perform patch add on the resource according to the specified path without filters.
     *
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @throws BadRequestException
     * @throws CharonException
     * @throws InternalErrorException
     * @throws NotImplementedException
     */
    private static void doPatchAddOnPathWithoutFilters(AbstractSCIMObject oldResource, SCIMResourceTypeSchema schema,
            JSONDecoder decoder, PatchOperation operation, String path)
            throws BadRequestException, CharonException, InternalErrorException, NotImplementedException {

        if (operation.getPath().trim().length() > 0) {
            String[] attributeParts = operation.getPath().split("[\\.]");

            if (log.isDebugEnabled()) {
                log.debug("After splitting the Path attribute part(s): " + Arrays.toString(attributeParts));
            }

            /* Depends on the split attribute parts from path,
            if attribute parts length is one, we consider this as a level one case, where path is looks like
            path=AttributeX, as examples it can be path=members or path=nickname.
            if attribute parts length is two, we consider this as a level two case, where path is looks like
            path=AttributeX.subAttributeY, as examples it can be path=name.familyName.
            According to the SCIM specification max one sub attribute is allowed for a SCIM attribute. Hence if
            attribute parts length is greater than 2, throw BadRequestException.
             */
            if (attributeParts.length == 1) {
                doPatchAddOnPathWithoutFiltersForLevelOne(oldResource, schema, decoder, operation, attributeParts[0]);
            } else if (attributeParts.length == 2) {
                doPatchAddOnPathWithoutFiltersForLevelTwo(oldResource, schema, decoder, operation, attributeParts);
            } else {
                throw new BadRequestException(("According to SCIM specification max one sub attribute can be allowed "
                        + "for SCIM attribute. path: " + operation.getPath()), ResponseCodeConstants.NO_TARGET);
            }
        } else {
            throw new BadRequestException(("Path is empty. path: " + operation.getPath()),
                    ResponseCodeConstants.NO_TARGET);
        }
    }

    /**
     * Perform patch add operation for the simple path (level one). As example attributes members, nickname, name.
     *
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param attributePart
     * @throws BadRequestException
     * @throws CharonException
     * @throws InternalErrorException
     */
    private static void doPatchAddOnPathWithoutFiltersForLevelOne(AbstractSCIMObject oldResource,
            SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation, String attributePart)
            throws BadRequestException, CharonException, InternalErrorException {

        Attribute attribute = oldResource.getAttribute(attributePart);

        if (attribute != null) {
            if (log.isDebugEnabled()) {
                log.debug("Attribute: " + attribute.getName() + " extracted from old resource.");
            }
            checkMutability(attribute);
            if (SCIMDefinitions.DataType.COMPLEX.equals(attribute.getType()) && attribute.getMultiValued()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Attribute: %s is a multi-valued complex attribute", attribute.getName()));
                }
                if (attribute instanceof MultiValuedAttribute) {
                    // This is complex multi attribute case.
                    JSONArray jsonArray = getJsonArray(operation);
                    AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attribute.getName(), schema);
                    MultiValuedAttribute newMultiValuedAttribute = decoder
                            .buildComplexMultiValuedAttribute(attributeSchema, jsonArray);
                    for (Attribute newAttribute : newMultiValuedAttribute.getAttributeValues()) {
                        ((MultiValuedAttribute) attribute).setAttributeValue(newAttribute);
                    }
                } else {
                    throw new BadRequestException(
                            "Attribute: " + attribute.getName() + " is not a instance of MultiValuedAttribute.",
                            ResponseCodeConstants.INVALID_SYNTAX);
                }
            } else if (SCIMDefinitions.DataType.COMPLEX.equals(attribute.getType())) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Attribute: %s is a complex attribute but not multi-valued",
                            attribute.getName()));
                }
                // This is complex attribute case but not multi valued.
                JSONObject jsonObject = getJsonObject(operation);
                AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attribute.getName(), schema);
                ComplexAttribute newComplexAttribute = generateComplexAttribute(decoder, jsonObject, attributeSchema);
                addComplexAttributeToResourceExcludingMultiValued(attribute, newComplexAttribute, attributePart);
            } else if (attribute.getMultiValued()) {
                // This is multivalued primitive case.
                if (log.isDebugEnabled()) {
                    log.debug(
                            String.format("Attribute: %s is a multi-valued primitive attribute", attribute.getName()));
                }
                ((MultiValuedAttribute) attribute).deletePrimitiveValues();
                JSONArray jsonArray = getJsonArray(operation);
                addPrimitiveMultiValuedAttributeToResource(attribute, jsonArray);
            } else {
                // This is the simple attribute case, can replace the value.
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Attribute: %s is a simple attribute", attribute.getName()));
                }
                if (attribute instanceof SimpleAttribute) {
                    ((SimpleAttribute) attribute).setValue(operation.getValues());
                } else {
                    throw new BadRequestException(
                            "Attribute: " + attribute.getName() + " is not a instance of SimpleAttribute.",
                            ResponseCodeConstants.INVALID_SYNTAX);
                }
            }
        } else {
            // Create and add the attribute.
            if (log.isDebugEnabled()) {
                log.debug(String.format("Attribute: %s is not found in the resource so create newly and add it",
                        attributePart));
            }
            createAttributeOnResourceWithPathWithoutFiltersForLevelOne(oldResource, schema, decoder, operation,
                    new String[] { attributePart });
        }
    }

    /**
     * Perform patch add operation for the path which doesn't contain the filters but considered as level two.
     * As an example attribute name.familyName
     *
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param attributeParts
     */
    private static void doPatchAddOnPathWithoutFiltersForLevelTwo(AbstractSCIMObject oldResource,
            SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation, String[] attributeParts)
            throws BadRequestException, CharonException, InternalErrorException {

        if (attributeParts.length != 2) {
            throw new CharonException("attributeParts length should be three.");
        }
        Attribute attribute = oldResource.getAttribute(attributeParts[0]);

        if (attribute != null) {
            updateAttributeOnResourceWithPathWithoutFiltersForLevelTwo(schema, decoder, operation, attributeParts,
                    attribute);
        } else {
            // Targeted path location is not found, so create and add it.
            if (log.isDebugEnabled()) {
                log.debug(String.format("Attribute: %s is not found in the resource so create newly and add it",
                        attributeParts[0]));
            }
            createAttributeOnResourceWithPathWithoutFiltersForLevelTwo(oldResource, schema, decoder, operation,
                    attributeParts);
        }
    }

    private static void updateAttributeOnResourceWithPathWithoutFiltersForLevelTwo(SCIMResourceTypeSchema schema,
            JSONDecoder decoder, PatchOperation operation, String[] attributeParts, Attribute attribute)
            throws CharonException, BadRequestException, InternalErrorException {

        if (attributeParts.length != 2) {
            throw new CharonException("attributeParts length should be three.");
        }

        if (attribute.getMultiValued()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Attribute: %s is a complex multi-valued attribute ", attribute.getName()));
            }
            if (attribute instanceof MultiValuedAttribute) {
                List<Attribute> subValues = ((MultiValuedAttribute) attribute).getAttributeValues();
                for (Attribute subValue : subValues) {
                    Attribute subAttribute = subValue.getSubAttribute(attributeParts[1]);
                    if (subAttribute != null) {
                        checkMutability(subAttribute);
                        if (subAttribute.getMultiValued()) {
                            if (log.isDebugEnabled()) {
                                log.debug(String.format("Sub attribute: %s is a complex multi-valued attribute ",
                                        subAttribute.getName()));
                            }
                            JSONArray jsonArray = getJsonArray(operation);
                            addPrimitiveMultiValuedAttributeToResource(subAttribute, jsonArray);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(String.format("Sub attribute: %s is a simple attribute ",
                                        subAttribute.getName()));
                            }
                            if (subAttribute instanceof SimpleAttribute) {
                                ((SimpleAttribute) subAttribute).setValue(operation.getValues());
                            } else {
                                throw new BadRequestException(
                                        "Sub attribute: " + subAttribute.getName() + " is not a instance of "
                                                + "SimpleAttribute.", ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        }
                    } else {
                        // Sub attribute is null so add it.
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Sub attribute: %s is not found, so create and add it.",
                                    subValue.getName()));
                        }
                        createSubAttributeOnResourceWithPathWithoutFiltersForLevelTwo(schema, decoder, operation,
                                attributeParts, subValue);
                    }
                }
            } else {
                throw new BadRequestException(
                        "Attribute: " + attribute.getName() + " is not a instance of MultiValuedAttribute.",
                        ResponseCodeConstants.INVALID_SYNTAX);
            }
        } else {
            // attributeParts[0] is Complex but not multi valued.
            if (log.isDebugEnabled()) {
                log.debug(String.format("Attribute: %s is a complex but not multi-valued attribute ",
                        attribute.getName()));
            }
            Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);
            AttributeSchema subAttributeSchema = SchemaUtil
                    .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);
            if (subAttributeSchema != null) {
                updateSubAttributeOnResourceWithPathWithoutFiltersForLevelTwo(schema, decoder, operation,
                        attributeParts, attribute, subAttribute, subAttributeSchema);
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                        ResponseCodeConstants.NO_TARGET);
            }
        }
    }

    private static void updateSubAttributeOnResourceWithPathWithoutFiltersForLevelTwo(SCIMResourceTypeSchema schema,
            JSONDecoder decoder, PatchOperation operation, String[] attributeParts, Attribute attribute,
            Attribute subAttribute, AttributeSchema subAttributeSchema)
            throws BadRequestException, CharonException, InternalErrorException {

        if (attributeParts.length != 2) {
            throw new CharonException("attributeParts length should be three.");
        }

        if (attribute instanceof ComplexAttribute) {
            if (SCIMDefinitions.DataType.COMPLEX.equals(subAttributeSchema.getType())) {
                // Only extension schema reaches here.
                if (subAttribute != null) {
                    checkMutability(subAttribute);
                    if (subAttribute.getMultiValued()) {
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Sub attribute: %s is a multi-valued complex attribute ",
                                    subAttribute.getName()));
                        }
                        JSONArray jsonArray = getJsonArray(operation);
                        MultiValuedAttribute newMultiValuesAttribute = decoder
                                .buildComplexMultiValuedAttribute(subAttributeSchema, jsonArray);
                        ((ComplexAttribute) attribute).setSubAttribute(newMultiValuesAttribute);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Sub attribute: %s is a complex not multi-valued attribute ",
                                    subAttribute.getName()));
                        }
                        JSONObject jsonObject = getJsonObject(operation);
                        ComplexAttribute newComplexAttribute = generateComplexAttribute(decoder, jsonObject,
                                subAttributeSchema);
                        ((ComplexAttribute) attribute).setSubAttribute(newComplexAttribute);
                    }
                } else {
                    // Sub attribute is null, so add it.
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Sub attribute: %s is null so add it", attributeParts[1]));
                    }
                    if (subAttributeSchema.getMultiValued()) {
                        JSONArray jsonArray = getJsonArray(operation);
                        MultiValuedAttribute newMultiValuesAttribute = decoder
                                .buildComplexMultiValuedAttribute(subAttributeSchema, jsonArray);
                        ((ComplexAttribute) attribute).setSubAttribute(newMultiValuesAttribute);
                    } else {
                        JSONObject jsonObject = getJsonObject(operation);
                        ComplexAttribute newComplexAttribute = generateComplexAttribute(decoder, jsonObject,
                                subAttributeSchema);
                        ((ComplexAttribute) attribute).setSubAttribute(newComplexAttribute);
                    }
                }
            } else {
                // Sub attribute schema is not complex.
                if (subAttribute != null) {
                    checkMutability(subAttribute);
                    AttributeSchema attributeSchema = SchemaUtil
                            .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);
                    if (subAttribute.getMultiValued()) {
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Sub attribute: %s is a multi-valued attribute ",
                                    subAttribute.getName()));
                        }
                        JSONArray jsonArray = getJsonArray(operation);
                        MultiValuedAttribute newMultiValuedAttribute = decoder
                                .buildPrimitiveMultiValuedAttribute(attributeSchema, jsonArray);
                        ((ComplexAttribute) attribute).setSubAttribute(newMultiValuedAttribute);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                    String.format("Sub attribute: %s is a simple attribute ", subAttribute.getName()));
                        }
                        SimpleAttribute simpleAttribute = decoder
                                .buildSimpleAttribute(attributeSchema, operation.getValues());
                        ((ComplexAttribute) attribute).removeSubAttribute(attributeParts[1]);
                        ((ComplexAttribute) attribute).setSubAttribute(simpleAttribute);
                    }
                } else {
                    //Sub attribute is not there so add it.
                    createSubAttributeOnResourceWithPathWithoutFiltersForLevelTwo(schema, decoder, operation,
                            attributeParts, attribute);
                }
            }
        } else {
            throw new BadRequestException(
                    "Attribute: " + attribute.getName() + " is not a instance of ComplexAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void createSubAttributeOnResourceWithPathWithoutFiltersForLevelTwo(SCIMResourceTypeSchema schema,
            JSONDecoder decoder, PatchOperation operation, String[] attributeParts, Attribute attribute)
            throws BadRequestException, CharonException {

        if (attributeParts.length != 2) {
            throw new CharonException("attributeParts length should be three.");
        }

        if (attribute instanceof ComplexAttribute) {
            AttributeSchema attributeSchema = SchemaUtil
                    .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);
            if (attributeSchema.getMultiValued()) {
                JSONArray jsonArray = getJsonArray(operation);
                MultiValuedAttribute newMultiValuedAttribute = decoder
                        .buildPrimitiveMultiValuedAttribute(attributeSchema, jsonArray);
                ((ComplexAttribute) attribute).setSubAttribute(newMultiValuedAttribute);
            } else {
                SimpleAttribute simpleAttribute = decoder.buildSimpleAttribute(attributeSchema, operation.getValues());
                ((ComplexAttribute) attribute).setSubAttribute(simpleAttribute);
            }
        } else {
            throw new BadRequestException(
                    "Attribute: " + attribute.getName() + " is not a instance of ComplexAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void addComplexAttributeToResourceExcludingMultiValued(Attribute oldAttribute,
            ComplexAttribute newComplexAttribute, String attributePart) throws CharonException, BadRequestException {

        // This is the complex attribute case.
        if (newComplexAttribute != null && oldAttribute instanceof ComplexAttribute) {
            Map<String, Attribute> newSubAttributeList = newComplexAttribute.getSubAttributesList();

            for (Map.Entry<String, Attribute> newSubAttrib : newSubAttributeList.entrySet()) {
                Attribute oldSubAttribute = oldAttribute.getSubAttribute(newSubAttrib.getKey());

                if (oldSubAttribute != null) {
                    if (SCIMDefinitions.DataType.COMPLEX.equals(oldSubAttribute.getType())) {
                        if (oldSubAttribute.getMultiValued()) {
                            // Extension schema is the only one who reaches here.
                            handleSubAttributeComplexMultiValuedCaseInLevelOne(
                                    newComplexAttribute.getSubAttribute(attributePart), newSubAttrib.getKey(),
                                    oldSubAttribute);
                        } else {
                            // Extension schema is the only one who reaches here.
                            handleSubAttributeComplexExcludingMultiValuedCaseInLevelOne(
                                    newComplexAttribute.getSubAttribute(attributePart), newSubAttrib.getKey(),
                                    oldSubAttribute);
                        }
                    } else {
                        if (oldSubAttribute.getMultiValued()) {
                            handleSubAttributeMultiValuedCaseInLevelOne(newSubAttrib, oldSubAttribute);
                        } else {
                            handleSubAttributeSimpleCaseInLevelOne(newSubAttrib, oldSubAttribute);
                        }
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Sub attribute: %s is not found, so create and add it",
                                newSubAttrib.getKey()));
                    }
                    if (newSubAttrib.getValue() != null) {
                        ((ComplexAttribute) oldAttribute).setSubAttribute(newSubAttrib.getValue());
                    } else {
                        throw new BadRequestException("Not a valid attribute.", ResponseCodeConstants.INVALID_SYNTAX);
                    }
                }
            }
        } else {
            throw new BadRequestException(
                    "Attribute: " + oldAttribute.getName() + " is not a instance of ComplexAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void handleSubAttributeSimpleCaseInLevelOne(Map.Entry<String, Attribute> newSubAttrib,
            Attribute oldSubAttribute) throws BadRequestException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Sub attribute: %s is a simple attribute", oldSubAttribute.getName()));
        }
        if (oldSubAttribute instanceof SimpleAttribute && newSubAttrib.getValue() instanceof SimpleAttribute) {
            ((SimpleAttribute) oldSubAttribute).setValue(((SimpleAttribute) newSubAttrib.getValue()).getValue());
        } else {
            throw new BadRequestException(
                    "Sub attribute: " + oldSubAttribute.getName() + " is not a instance of SimpleAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void handleSubAttributeMultiValuedCaseInLevelOne(Map.Entry<String, Attribute> newSubAttrib,
            Attribute oldSubAttribute) throws BadRequestException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Sub attribute: %s is a primitive multi-valued attribute",
                    oldSubAttribute.getName()));
        }
        if (newSubAttrib.getValue() instanceof MultiValuedAttribute
                && oldSubAttribute instanceof MultiValuedAttribute) {
            List<Object> items = ((MultiValuedAttribute) (newSubAttrib.getValue())).
                    getAttributePrimitiveValues();
            if (items != null) {
                for (Object item : items) {
                    ((MultiValuedAttribute) oldSubAttribute).setAttributePrimitiveValue(item);
                }
            }
        } else {
            throw new BadRequestException(
                    "Sub attribute: " + oldSubAttribute.getName() + " is not a instance of MultiValuedAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void handleSubAttributeComplexMultiValuedCaseInLevelOne(Attribute subAttribute,
            String newSubAttribKey, Attribute oldSubAttribute) throws CharonException, BadRequestException {

        if (oldSubAttribute instanceof MultiValuedAttribute && subAttribute instanceof MultiValuedAttribute) {
            // Extension schema is the only one who reaches here.
            if (log.isDebugEnabled()) {
                log.debug(String.format("Sub attribute: %s is a multi-valued complex attribute",
                        oldSubAttribute.getName()));
            }
            MultiValuedAttribute attributeSubValue = (MultiValuedAttribute) subAttribute
                    .getSubAttribute(newSubAttribKey);
            for (Attribute attribute : attributeSubValue.getAttributeValues()) {
                ((MultiValuedAttribute) oldSubAttribute).setAttributeValue(attribute);
            }
        } else {
            throw new BadRequestException(
                    "Sub attribute: " + oldSubAttribute.getName() + " is not a instance of MultiValuedAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void handleSubAttributeComplexExcludingMultiValuedCaseInLevelOne(Attribute newSubAttribute,
            String newSubAttribKey, Attribute oldSubAttribute) throws CharonException, BadRequestException {

        if (oldSubAttribute instanceof ComplexAttribute && newSubAttribute instanceof ComplexAttribute) {
            // Extension schema is the only one who reaches here.
            if (log.isDebugEnabled()) {
                log.debug(String.format("Sub attribute: %s is a complex attribute but not multi-valued.",
                        oldSubAttribute.getName()));
            }
            Map<String, Attribute> subSubAttributeList = ((ComplexAttribute) newSubAttribute
                    .getSubAttribute(newSubAttribKey)).getSubAttributesList();

            if (subSubAttributeList != null) {
                for (Map.Entry<String, Attribute> subSubAttrib : subSubAttributeList.entrySet()) {
                    Attribute subSubAttribute = oldSubAttribute.getSubAttribute(subSubAttrib.getKey());

                    if (subSubAttribute != null) {
                        if (subSubAttribute.getMultiValued()) {
                            if (log.isDebugEnabled()) {
                                log.debug(String.format(
                                        "Sub sub attribute: %s is a primitive " + "multi-valued attribute",
                                        subSubAttribute.getName()));
                            }
                            if (subSubAttrib.getValue() instanceof MultiValuedAttribute
                                    && subSubAttribute instanceof MultiValuedAttribute) {
                                List<Object> items = ((MultiValuedAttribute) (subSubAttrib.getValue())).
                                        getAttributePrimitiveValues();
                                if (items != null) {
                                    for (Object item : items) {
                                        ((MultiValuedAttribute) subSubAttribute).setAttributePrimitiveValue(item);
                                    }
                                }
                            } else {
                                throw new BadRequestException(
                                        "Sub sub attribute: " + subSubAttribute.getName() + " is not a instance of "
                                                + "MultiValuedAttribute.", ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(String.format("Sub sub attribute: %s is a simple attribute",
                                        subSubAttribute.getName()));
                            }
                            if (subSubAttribute instanceof SimpleAttribute && subSubAttrib
                                    .getValue() instanceof SimpleAttribute) {
                                ((SimpleAttribute) subSubAttribute)
                                        .setValue(((SimpleAttribute) subSubAttrib.getValue()).getValue());
                            } else {
                                throw new BadRequestException(
                                        "Sub sub attribute: " + subSubAttribute.getName() + " is not a instance of "
                                                + "SimpleAttribute.", ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(String.format(
                                    "Sub sub attribute: %s is not found in the resource, so " + "create and add it",
                                    subSubAttrib.getKey()));
                        }
                        if (subSubAttrib.getValue() != null) {
                            ((ComplexAttribute) oldSubAttribute).setSubAttribute(subSubAttrib.getValue());
                        } else {
                            throw new BadRequestException("Not a valid attribute.",
                                    ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                }
            }
        } else {
            throw new BadRequestException(
                    "Sub attribute: " + oldSubAttribute.getName() + " is not a instance of ComplexAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static void addPrimitiveMultiValuedAttributeToResource(Attribute attribute, JSONArray jsonArray)
            throws BadRequestException {

        if (attribute instanceof MultiValuedAttribute) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    // JSON array may contains duplicates so retrieve every time and check.
                    List<Object> attributePrimitiveValues = ((MultiValuedAttribute) attribute)
                            .getAttributePrimitiveValues();
                    if (!attributePrimitiveValues.contains(jsonArray.get(i))) {
                        ((MultiValuedAttribute) attribute).setAttributePrimitiveValue(jsonArray.get(i));
                    }
                } catch (JSONException e) {
                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                }
            }
        } else {
            throw new BadRequestException(
                    "Attribute: " + attribute.getName() + " is not a instance of MultiValuedAttribute.",
                    ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    private static ComplexAttribute generateComplexAttribute(JSONDecoder decoder, JSONObject jsonObject,
            AttributeSchema attributeSchema) throws BadRequestException, CharonException, InternalErrorException {

        ComplexAttribute newComplexAttribute = null;
        try {
            newComplexAttribute = decoder.buildComplexAttribute(attributeSchema, jsonObject);
        } catch (JSONException e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid JSON string, " + jsonObject.toString());
            }
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
        return newComplexAttribute;
    }

    private static void checkMutability(Attribute attribute) throws BadRequestException {

        if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) || attribute.getMutability()
                .equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
            throw new BadRequestException("Can not update a immutable attribute or a read-only attribute",
                    ResponseCodeConstants.MUTABILITY);
        }
    }

    private static JSONObject getJsonObject(PatchOperation operation) throws BadRequestException {

        JSONObject jsonObject = null;
        try {
            if (operation.getValues() != null) {
                jsonObject = new JSONObject(new JSONTokener(operation.getValues().toString()));
            }
        } catch (JSONException e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid JSON string, " + operation.getValues().toString());
            }
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
        return jsonObject;
    }

    private static JSONArray getJsonArray(PatchOperation operation) throws BadRequestException {

        JSONArray jsonArray = null;
        try {
            if (operation.getValues() != null) {
                jsonArray = new JSONArray(new JSONTokener(operation.getValues().toString()));
            }
        } catch (JSONException e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid JSON string, " + operation.getValues().toString());
            }
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
        return jsonArray;
    }

    private static AbstractSCIMObject doPatchAddOnResource(PatchOperation operation, JSONDecoder decoder,
            AbstractSCIMObject oldResource, AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException {

        try {
            AbstractSCIMObject attributeHoldingSCIMObject = decoder.decode(operation.getValues().toString(), schema);
            if (oldResource != null) {
                for (String attributeName : attributeHoldingSCIMObject.getAttributeList().keySet()) {
                    Attribute oldAttribute = oldResource.getAttribute(attributeName);
                    if (oldAttribute != null) {
                        // if the attribute is there, append it.
                        if (oldAttribute.getMultiValued() &&
                                oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this is multivalued complex case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMObject.getAttribute(attributeName);

                            for (Attribute attribute : attributeValue.getAttributeValues()) {
                                ((MultiValuedAttribute) oldAttribute).setAttributeValue(attribute);
                            }

                        } else if (oldAttribute.getMultiValued()) {

                            //this is multivalued primitive case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMObject.getAttribute(attributeName);

                            for (Object obj : attributeValue.getAttributePrimitiveValues()) {
                                ((MultiValuedAttribute) oldAttribute).setAttributePrimitiveValue(obj);
                            }

                        } else if (oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this is the complex attribute case.
                            Map<String, Attribute> subAttributeList =
                                    ((ComplexAttribute) attributeHoldingSCIMObject.
                                            getAttribute(attributeName)).getSubAttributesList();

                            for (Map.Entry<String, Attribute> subAttrib : subAttributeList.entrySet()) {
                                Attribute subAttribute = oldAttribute.getSubAttribute(subAttrib.getKey());

                                if (subAttribute != null) {
                                    if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                        if (subAttribute.getMultiValued()) {
                                            //extension schema is the only one who reaches here.
                                            MultiValuedAttribute attributeSubValue = (MultiValuedAttribute)
                                                    ((ComplexAttribute) attributeHoldingSCIMObject.
                                                            getAttribute(attributeName)).
                                                            getSubAttribute(subAttrib.getKey());

                                            for (Attribute attribute : attributeSubValue.getAttributeValues()) {
                                                ((MultiValuedAttribute) subAttribute).setAttributeValue(attribute);
                                            }
                                        } else {
                                            //extension schema is the only one who reaches here.
                                            Map<String, Attribute> subSubAttributeList = ((ComplexAttribute)
                                                    (attributeHoldingSCIMObject.getAttribute(attributeName).
                                                            getSubAttribute(subAttrib.getKey()))).
                                                    getSubAttributesList();

                                            for (Map.Entry<String, Attribute> subSubAttrib :
                                                    subSubAttributeList.entrySet()) {
                                                Attribute subSubAttribute = oldAttribute.getSubAttribute
                                                        (subAttrib.getKey()).getSubAttribute(subSubAttrib.getKey());

                                                if (subSubAttribute != null) {
                                                    if (subSubAttribute.getMultiValued()) {
                                                        List<Object> items = ((MultiValuedAttribute)
                                                                (subSubAttrib.getValue())).
                                                                getAttributePrimitiveValues();
                                                        for (Object item : items) {
                                                            ((MultiValuedAttribute) subSubAttribute).
                                                                    setAttributePrimitiveValue(item);
                                                        }
                                                    } else {
                                                        ((SimpleAttribute) subSubAttribute).setValue(
                                                                ((SimpleAttribute) subSubAttrib.getValue()).getValue());
                                                    }
                                                } else {
                                                    if (subSubAttrib.getValue() != null) {
                                                        ((ComplexAttribute) (subAttribute)).setSubAttribute(
                                                                subSubAttrib.getValue());
                                                    } else {
                                                        throw new BadRequestException("Not a valid attribute.",
                                                                ResponseCodeConstants.INVALID_SYNTAX);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (subAttribute.getMultiValued()) {
                                            List<Object> items = ((MultiValuedAttribute)
                                                    (subAttrib.getValue())).
                                                    getAttributePrimitiveValues();
                                            for (Object item : items) {
                                                ((MultiValuedAttribute) subAttribute).setAttributePrimitiveValue(item);
                                            }
                                        } else {
                                            ((SimpleAttribute) subAttribute).setValue(((SimpleAttribute)
                                                    subAttrib.getValue()).getValue());
                                        }
                                    }
                                } else {
                                    if (subAttrib.getValue() != null) {
                                        ((ComplexAttribute) oldAttribute).setSubAttribute
                                                (subAttrib.getValue());
                                    } else {
                                        throw new BadRequestException("Not a valid attribute.",
                                                ResponseCodeConstants.INVALID_SYNTAX);
                                    }

                                }
                            }
                        } else {
                            // this is the simple attribute case.replace the value
                            ((SimpleAttribute) oldAttribute).setValue
                                    (((SimpleAttribute) attributeHoldingSCIMObject.getAttribute
                                            (oldAttribute.getName())).getValue());
                        }
                    } else {
                        //if the attribute is not already set, set it.
                        if (attributeHoldingSCIMObject.getAttribute(attributeName) != null) {
                            oldResource.setAttribute(attributeHoldingSCIMObject.getAttribute(attributeName));
                        } else {
                            throw new BadRequestException("Not a valid attribute.",
                                    ResponseCodeConstants.INVALID_SYNTAX);
                        }
                    }
                }
                AbstractSCIMObject validatedResource = ServerSideValidator.validateUpdatedSCIMObject
                        (copyOfOldResource, oldResource, schema);

                return validatedResource;
            } else  {
                throw new CharonException("Error in getting the old resource.");
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
        }
    }

    /*
     * This is the main patch replace method.
     * @param operation
     * @param decoder
     * @param oldResource
     * @param copyOfOldResource
     * @param schema
     * @return
     * @throws CharonException
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws JSONException
     * @throws InternalErrorException
     */
    public static AbstractSCIMObject doPatchReplace(PatchOperation operation, JSONDecoder decoder,
                                                    AbstractSCIMObject oldResource,
                                                    AbstractSCIMObject copyOfOldResource,
                                                    SCIMResourceTypeSchema schema)
            throws CharonException, NotImplementedException, BadRequestException, InternalErrorException {

        if (operation.getPath() != null) {
            String path = operation.getPath();
            //split the path to extract the filter if present.
            String[] parts = path.split("[\\[\\]]");

            if (operation.getPath().contains("[")) {
                try {
                    doPatchReplaceOnPathWithFilters(oldResource, schema, decoder, operation, parts);
                } catch (JSONException e) {
                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                }

            } else {
                    doPatchReplaceOnPathWithoutFilters(oldResource, schema, decoder, operation, parts);
            }

        } else {
            doPatchReplaceOnResource(oldResource, copyOfOldResource, schema, decoder, operation);
        }
        //validate the updated object
        AbstractSCIMObject validatedResource =  ServerSideValidator.validateUpdatedSCIMObject
                (copyOfOldResource, oldResource, schema);
        return validatedResource;
    }

    /*
     * This method is to do patch replace without a filter present but a path value present.
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param parts
     * @return
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static AbstractSCIMObject doPatchReplaceOnPathWithoutFilters(AbstractSCIMObject oldResource,
                                                                         SCIMResourceTypeSchema schema,
                                                                         JSONDecoder decoder, PatchOperation operation,
                                                                         String[] parts)
            throws BadRequestException, CharonException, InternalErrorException {

        String[] attributeParts = parts[0].split("[\\.]");

        if (attributeParts.length == 1) {

            doPatchReplaceOnPathWithoutFiltersForLevelOne(oldResource, schema, decoder, operation, attributeParts);

        } else if (attributeParts.length == 2) {

            doPatchReplaceOnPathWithoutFiltersForLevelTwo(oldResource, schema, decoder, operation, attributeParts);

        } else if (attributeParts.length == 3) {

            doPatchReplaceOnPathWithoutFiltersForLevelThree(oldResource, schema, decoder, operation, attributeParts);

        }
        return oldResource;
    }

    /*
     * This performs patch on resource based on the path value.No filter is specified here.
     * And this is for level one attributes.
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param attributeParts
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static void doPatchReplaceOnPathWithoutFiltersForLevelOne(AbstractSCIMObject oldResource,
                                                                      SCIMResourceTypeSchema schema,
                                                                      JSONDecoder decoder,
                                                                      PatchOperation operation,
                                                                      String[] attributeParts)
            throws BadRequestException, CharonException, InternalErrorException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);

        if (attribute != null) {
            if (!attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                if (!attribute.getMultiValued()) {
                    if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                            attribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                        throw new BadRequestException("Can not replace a immutable attribute or a read-only attribute",
                                ResponseCodeConstants.MUTABILITY);
                    } else {
                        ((SimpleAttribute) attribute).setValue(operation.getValues().toString());
                    }
                } else {
                    if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                            attribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                        throw new BadRequestException("Can not replace a immutable attribute or a read-only attribute",
                                ResponseCodeConstants.MUTABILITY);
                    } else {
                        ((MultiValuedAttribute) attribute).deletePrimitiveValues();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(operation.getValues());
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ((MultiValuedAttribute) attribute).setAttributePrimitiveValue(jsonArray.get(i));
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        }
                    }
                }

            } else {
                if (attribute.getMultiValued()) {
                    if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                            attribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                        throw new BadRequestException("Can not replace a immutable attribute or a read-only attribute",
                                ResponseCodeConstants.MUTABILITY);
                    } else {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray
                                    (new JSONTokener(operation.getValues().toString()));
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attribute.getName(), schema);
                        MultiValuedAttribute newMultiValuedAttribute = decoder.buildComplexMultiValuedAttribute
                                (attributeSchema, jsonArray);
                        oldResource.deleteAttribute(attribute.getName());
                        oldResource.setAttribute(newMultiValuedAttribute);
                    }


                } else {
                    if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                            attribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                        throw new BadRequestException("Can not replace a immutable attribute or a read-only attribute",
                                ResponseCodeConstants.MUTABILITY);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(new JSONTokener(operation.getValues().toString()));
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attribute.getName(), schema);
                        ComplexAttribute newComplexAttribute = null;
                        try {
                            newComplexAttribute = decoder.buildComplexAttribute(attributeSchema,
                                    jsonObject);
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        oldResource.deleteAttribute(attribute.getName());
                        oldResource.setAttribute(newComplexAttribute);
                    }
                }
            }

        } else {
            //create and add the attribute
            createAttributeOnResourceWithPathWithoutFiltersForLevelOne(oldResource, schema, decoder, operation,
                    attributeParts);
        }
    }

    private static void createAttributeOnResourceWithPathWithoutFiltersForLevelOne(AbstractSCIMObject oldResource,
            SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation, String[] attributeParts)
            throws BadRequestException, CharonException, InternalErrorException {

        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);
        if (attributeSchema != null) {
            if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                if (attributeSchema.getMultiValued()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(new JSONTokener(operation.getValues().toString()));
                    } catch (JSONException e) {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                    MultiValuedAttribute newMultiValuedAttribute = decoder
                            .buildComplexMultiValuedAttribute(attributeSchema, jsonArray);
                    oldResource.setAttribute(newMultiValuedAttribute);

                } else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(new JSONTokener(operation.getValues().toString()));
                    } catch (JSONException e) {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                    ComplexAttribute newComplexAttribute = null;
                    try {
                        newComplexAttribute = decoder.buildComplexAttribute(attributeSchema, jsonObject);
                    } catch (JSONException e) {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                    oldResource.setAttribute(newComplexAttribute);
                }

            } else {
                if (attributeSchema.getMultiValued()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(new JSONTokener(operation.getValues().toString()));
                    } catch (JSONException e) {
                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                    }
                    MultiValuedAttribute newMultiValuedAttribute = decoder
                            .buildPrimitiveMultiValuedAttribute(attributeSchema, jsonArray);
                    oldResource.setAttribute(newMultiValuedAttribute);

                } else {

                    SimpleAttribute simpleAttribute = decoder
                            .buildSimpleAttribute(attributeSchema, operation.getValues());
                    oldResource.setAttribute(simpleAttribute);
                }
            }
        } else {
            throw new BadRequestException("No attribute with the name : " + attributeParts[0]);
        }
    }

    /*
     * This performs patch on resource based on the path value.No filter is specified here.
     * And this is for level two attributes.
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param attributeParts
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static void doPatchReplaceOnPathWithoutFiltersForLevelTwo(AbstractSCIMObject oldResource,
                                                                      SCIMResourceTypeSchema schema,
                                                                      JSONDecoder decoder,
                                                                      PatchOperation operation,
                                                                      String[] attributeParts)
            throws BadRequestException, CharonException, InternalErrorException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);

        if (attribute != null) {

            if (attribute.getMultiValued()) {

                List<Attribute> subValues = ((MultiValuedAttribute) attribute).getAttributeValues();
                for (Attribute subValue : subValues) {
                    Attribute subAttribute = ((ComplexAttribute) subValue).getSubAttribute(attributeParts[1]);
                    if (subAttribute != null) {
                        if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                subAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                            throw new BadRequestException
                                    ("Can not replace a immutable attribute or a read-only attribute",
                                    ResponseCodeConstants.MUTABILITY);
                        } else {
                            if (subAttribute.getMultiValued()) {
                                ((MultiValuedAttribute) subAttribute).deletePrimitiveValues();
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(operation.getValues());
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        ((MultiValuedAttribute) subAttribute).
                                                setAttributePrimitiveValue(jsonArray.get(i));
                                    } catch (JSONException e) {
                                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                    }
                                }
                            } else {
                                ((SimpleAttribute) subAttribute).setValue(operation.getValues());
                            }
                        }
                    } else {
                        AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(
                                attributeParts[0] + "." + attributeParts[1], schema);
                        if (subAttributeSchema.getMultiValued()) {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray
                                        (new JSONTokener(operation.getValues().toString()));
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            MultiValuedAttribute newMultiValuedAttribute = decoder.buildPrimitiveMultiValuedAttribute(
                                    subAttributeSchema, jsonArray);
                            ((ComplexAttribute) (subValue)).setSubAttribute(newMultiValuedAttribute);
                        } else  {
                            SimpleAttribute simpleAttribute = decoder.buildSimpleAttribute(
                                    subAttributeSchema, operation.getValues());
                            ((ComplexAttribute) (subValue)).setSubAttribute(simpleAttribute);
                        }
                    }
                }
            } else {
                Attribute subAttribute = ((attribute)).getSubAttribute(attributeParts[1]);
                AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(
                        attributeParts[0] + "." + attributeParts[1], schema);
                if (subAttributeSchema != null) {
                    if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        //only extension schema reaches here.
                        if (subAttribute != null) {
                            if (!subAttribute.getMultiValued()) {
                                if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        subAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                    throw new BadRequestException
                                            ("Can not replace a immutable attribute or a read-only attribute",
                                            ResponseCodeConstants.MUTABILITY);
                                } else {
                                    ComplexAttribute newComplexAttribute =
                                            null;
                                    try {
                                        newComplexAttribute = decoder.buildComplexAttribute(subAttributeSchema,
                                        (JSONObject) operation.getValues());
                                    } catch (JSONException e) {
                                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                    }
                                    ((ComplexAttribute) (attribute)).removeSubAttribute(attributeParts[1]);
                                    ((ComplexAttribute) (attribute)).setSubAttribute(newComplexAttribute);
                                }
                            } else {
                                if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        subAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                    throw new BadRequestException
                                            ("Can not replace a immutable attribute or a read-only attribute",
                                            ResponseCodeConstants.MUTABILITY);
                                } else {
                                    JSONArray jsonArray = null;
                                    try {
                                        jsonArray = new JSONArray
                                                (new JSONTokener(operation.getValues().toString()));
                                    } catch (JSONException e) {
                                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                    }
                                    MultiValuedAttribute newMultiValuesAttribute =
                                            decoder.buildComplexMultiValuedAttribute(subAttributeSchema, jsonArray);
                                    ((ComplexAttribute) (attribute)).removeSubAttribute(attributeParts[1]);
                                    ((ComplexAttribute) (attribute)).setSubAttribute(newMultiValuesAttribute);
                                }
                            }

                        } else {

                            if (subAttributeSchema.getMultiValued()) {
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray
                                            (new JSONTokener(operation.getValues().toString()));
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                                MultiValuedAttribute newMultiValuesAttribute =
                                        decoder.buildComplexMultiValuedAttribute(subAttributeSchema, jsonArray);
                                ((ComplexAttribute) (attribute)).setSubAttribute(newMultiValuesAttribute);
                            } else {
                                ComplexAttribute newComplexAttribute = null;
                                try {
                                    newComplexAttribute = decoder.buildComplexAttribute(subAttributeSchema,
                                            (JSONObject) operation.getValues());
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                                ((ComplexAttribute) (attribute)).setSubAttribute(newComplexAttribute);
                            }
                        }

                    } else {
                        if (subAttribute != null) {
                            if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                    subAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                throw new BadRequestException
                                        ("Can not replace a immutable attribute or a read-only attribute",
                                        ResponseCodeConstants.MUTABILITY);
                            } else {
                                AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(
                                        attributeParts[0] + "." + attributeParts[1], schema);
                                if (subAttribute.getMultiValued()) {
                                    JSONArray jsonArray = null;
                                    try {
                                        jsonArray = new JSONArray
                                                (new JSONTokener(operation.getValues().toString()));
                                    } catch (JSONException e) {
                                        throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                    }
                                    MultiValuedAttribute newMultiValuedAttribute =
                                            decoder.buildPrimitiveMultiValuedAttribute(
                                            attributeSchema, jsonArray);
                                    ((ComplexAttribute) (attribute)).removeSubAttribute(attributeParts[1]);
                                    ((ComplexAttribute) (attribute)).setSubAttribute(newMultiValuedAttribute);
                                } else {
                                    SimpleAttribute simpleAttribute = decoder.buildSimpleAttribute(
                                            attributeSchema, operation.getValues());
                                    ((ComplexAttribute) (attribute)).removeSubAttribute(attributeParts[1]);
                                    ((ComplexAttribute) (attribute)).setSubAttribute(simpleAttribute);
                                }

                            }
                        } else {
                            //add the values
                            AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(
                                    attributeParts[0] + "." + attributeParts[1], schema);
                            if (attributeSchema.getMultiValued()) {
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray
                                            (new JSONTokener(operation.getValues().toString()));
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                                MultiValuedAttribute newMultiValuedAttribute =
                                        decoder.buildPrimitiveMultiValuedAttribute(
                                        attributeSchema, jsonArray);
                                ((ComplexAttribute) (attribute)).setSubAttribute(newMultiValuedAttribute);
                            } else {
                                SimpleAttribute simpleAttribute = decoder.buildSimpleAttribute(
                                        attributeSchema, operation.getValues());
                                ((ComplexAttribute) (attribute)).setSubAttribute(simpleAttribute);
                            }
                        }
                    }
                } else {
                    throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                            ResponseCodeConstants.NO_TARGET);
                }
            }

        } else {

            createAttributeOnResourceWithPathWithoutFiltersForLevelTwo(oldResource, schema, decoder, operation,
                    attributeParts);
        }

    }

    private static void createAttributeOnResourceWithPathWithoutFiltersForLevelTwo(AbstractSCIMObject oldResource,
            SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation, String[] attributeParts)
            throws CharonException, BadRequestException, InternalErrorException {

        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);
        if (attributeSchema != null) {

            if (attributeSchema.getMultiValued()) {
                MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                DefaultAttributeFactory.createAttribute(attributeSchema, multiValuedAttribute);

                AttributeSchema subAttributeSchema = SchemaUtil
                        .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);

                if (subAttributeSchema != null) {
                    SimpleAttribute simpleAttribute = new SimpleAttribute(subAttributeSchema.getName(),
                            operation.getValues());
                    DefaultAttributeFactory.createAttribute(subAttributeSchema, simpleAttribute);

                    String complexAttributeName =
                            attributeSchema.getName() + "_" + operation.getValues() + "_" + SCIMConstants.DEFAULT;
                    ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeName);
                    DefaultAttributeFactory.createAttribute(attributeSchema, complexAttribute);

                    complexAttribute.setSubAttribute(simpleAttribute);

                    multiValuedAttribute.setAttributeValue(complexAttribute);

                    oldResource.setAttribute(multiValuedAttribute);
                } else {
                    throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                            ResponseCodeConstants.NO_TARGET);
                }

            } else {

                ComplexAttribute complexAttribute = new ComplexAttribute(attributeSchema.getName());
                DefaultAttributeFactory.createAttribute(attributeSchema, complexAttribute);

                AttributeSchema subAttributeSchema = SchemaUtil
                        .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);
                if (subAttributeSchema != null) {
                    if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        if (subAttributeSchema.getMultiValued()) {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(new JSONTokener(operation.getValues().toString()));
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            MultiValuedAttribute multiValuedAttribute = decoder
                                    .buildComplexMultiValuedAttribute(subAttributeSchema, jsonArray);
                            complexAttribute.setSubAttribute(multiValuedAttribute);
                        } else {
                            ComplexAttribute subComplexAttribute = null;
                            try {
                                subComplexAttribute = decoder
                                        .buildComplexAttribute(subAttributeSchema, (JSONObject) operation.getValues());
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            complexAttribute.setSubAttribute(subComplexAttribute);
                        }

                    } else {
                        SimpleAttribute simpleAttribute = new SimpleAttribute(subAttributeSchema.getName(),
                                operation.getValues());
                        DefaultAttributeFactory.createAttribute(subAttributeSchema, simpleAttribute);
                        complexAttribute.setSubAttribute(simpleAttribute);

                    }
                    oldResource.setAttribute(complexAttribute);

                } else {
                    throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                            ResponseCodeConstants.NO_TARGET);
                }
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0],
                    ResponseCodeConstants.NO_TARGET);
        }
    }

    /*
     * This performs patch on resource based on the path value.No filter is specified here.
     * And this is for level three attributes.
     * @param oldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param attributeParts
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     */
    private static void doPatchReplaceOnPathWithoutFiltersForLevelThree(AbstractSCIMObject oldResource,
                                                                        SCIMResourceTypeSchema schema,
                                                                        JSONDecoder decoder,
                                                                        PatchOperation operation,
                                                                        String[] attributeParts)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        if (attribute != null) {
            Attribute subAttribute  = ((ComplexAttribute) attribute).getSubAttribute(attributeParts[1]);
            if (subAttribute != null) {

                if (subAttribute.getMultiValued()) {
                    List<Attribute> subValues = ((MultiValuedAttribute) subAttribute).getAttributeValues();
                    if (subValues != null) {
                        for (Attribute subValue : subValues) {
                            Attribute subSubAttribute  = subValue.getSubAttribute(attributeParts[2]);
                            if (subSubAttribute != null) {
                                AttributeSchema subSubAttributeSchema = SchemaUtil.getAttributeSchema(
                                        attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2], schema);

                                if (subSubAttributeSchema != null) {
                                    if (subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subSubAttribute.getMutability().equals
                                                    (SCIMDefinitions.Mutability.IMMUTABLE)) {
                                        throw new BadRequestException
                                                ("Can not replace a immutable attribute or a read-only attribute",
                                                ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        if (subSubAttribute.getMultiValued()) {
                                            JSONArray jsonArray = null;
                                            try {
                                                jsonArray = new JSONArray
                                                        (new JSONTokener(operation.getValues().toString()));
                                            } catch (JSONException e) {
                                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                            }
                                            MultiValuedAttribute multiValuedAttribute =
                                                    decoder.buildPrimitiveMultiValuedAttribute(subSubAttributeSchema,
                                                           jsonArray);
                                            ((ComplexAttribute) subValue).removeSubAttribute(attributeParts[2]);
                                            ((ComplexAttribute) subValue).setSubAttribute(multiValuedAttribute);
                                        } else {
                                            SimpleAttribute simpleAttribute =
                                                    decoder.buildSimpleAttribute(subSubAttributeSchema,
                                                    operation.getValues());
                                            ((ComplexAttribute) subValue).removeSubAttribute(attributeParts[2]);
                                            ((ComplexAttribute) subValue).setSubAttribute(simpleAttribute);
                                        }
                                    }
                                } else {
                                    throw new BadRequestException
                                            ("No such attribute with the name : " + attributeParts[2],
                                            ResponseCodeConstants.NO_TARGET);
                                }
                            } else {
                                createSubSubAttributeOnResourceWithPathWithoutFiltersForLevelThree(schema, decoder,
                                        operation, attributeParts, (ComplexAttribute) subValue);
                            }
                        }

                    }

                } else {
                    Attribute subSubAttribute  = ((ComplexAttribute) subAttribute).getSubAttribute(attributeParts[2]);

                    if (subSubAttribute != null) {
                        ((SimpleAttribute) subSubAttribute).setValue(operation.getValues());
                    } else {
                        createSubSubAttributeOnResourceWithPathWithoutFiltersForLevelThree(schema, decoder, operation,
                                attributeParts, (ComplexAttribute) subAttribute);
                    }
                }

            } else {
                createSubAttributeOnResourceWithPathWithoutFiltersForLevelThree(schema, operation, attributeParts,
                        (ComplexAttribute) attribute);
            }
        } else {

            createAttributeOnResourceWithPathWithoutFiltersForLevelThree(oldResource, schema, operation,
                    attributeParts);
        }
    }

    private static void createSubSubAttributeOnResourceWithPathWithoutFiltersForLevelThree(
            SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation, String[] attributeParts,
            ComplexAttribute subAttribute) throws BadRequestException, CharonException {

        AttributeSchema subSubAttributeSchema = SchemaUtil
                .getAttributeSchema(attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2], schema);

        if (subSubAttributeSchema != null) {
            if (subSubAttributeSchema.getMultiValued()) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(new JSONTokener(operation.getValues().toString()));
                } catch (JSONException e) {
                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                }
                MultiValuedAttribute multiValuedAttribute = decoder
                        .buildPrimitiveMultiValuedAttribute(subSubAttributeSchema, jsonArray);
                subAttribute.setSubAttribute(multiValuedAttribute);
            } else {
                SimpleAttribute simpleAttribute = decoder
                        .buildSimpleAttribute(subSubAttributeSchema, operation.getValues());
                subAttribute.setSubAttribute(simpleAttribute);
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[2],
                    ResponseCodeConstants.NO_TARGET);
        }
    }

    private static void createSubAttributeOnResourceWithPathWithoutFiltersForLevelThree(SCIMResourceTypeSchema schema,
            PatchOperation operation, String[] attributeParts, ComplexAttribute attribute)
            throws CharonException, BadRequestException {

        AttributeSchema subAttributeSchena = SchemaUtil
                .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);

        if (subAttributeSchena != null) {
            if (subAttributeSchena.getMultiValued()) {

                MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(subAttributeSchena.getName());
                DefaultAttributeFactory.createAttribute(subAttributeSchena, multiValuedAttribute);

                String complexAttributeName =
                        subAttributeSchena.getName() + "_" + operation.getValues() + "_" + SCIMConstants.DEFAULT;
                ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeName);
                DefaultAttributeFactory.createAttribute(subAttributeSchena, complexAttribute);

                AttributeSchema subSubAttributeSchema = SchemaUtil
                        .getAttributeSchema(attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2],
                                schema);
                if (subSubAttributeSchema != null) {
                    if (subSubAttributeSchema.getMultiValued()) {
                        MultiValuedAttribute multiValuedSubAttribute = new MultiValuedAttribute(
                                subSubAttributeSchema.getName());
                        DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedSubAttribute);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(operation.getValues());
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                multiValuedSubAttribute.setAttributePrimitiveValue(jsonArray.get(i));
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        }
                        complexAttribute.setSubAttribute(multiValuedSubAttribute);

                    } else {
                        SimpleAttribute simpleAttribute = new SimpleAttribute(subSubAttributeSchema.getName(),
                                operation.getValues());
                        DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                        complexAttribute.setSubAttribute(simpleAttribute);
                    }
                    multiValuedAttribute.setAttributeValue(complexAttribute);
                    attribute.setSubAttribute(multiValuedAttribute);
                } else {
                    throw new BadRequestException("No such attribute with the name : " + attributeParts[2],
                            ResponseCodeConstants.NO_TARGET);
                }

            } else {
                ComplexAttribute complexAttribute = new ComplexAttribute(subAttributeSchena.getName());
                DefaultAttributeFactory.createAttribute(subAttributeSchena, complexAttribute);

                AttributeSchema subSubAttributeSchema = SchemaUtil
                        .getAttributeSchema(attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2],
                                schema);

                if (subSubAttributeSchema != null) {

                    if (subSubAttributeSchema.getMultiValued()) {
                        MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(
                                subSubAttributeSchema.getName());
                        DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedAttribute);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(operation.getValues());
                        } catch (JSONException e) {
                            throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                multiValuedAttribute.setAttributePrimitiveValue(jsonArray.get(i));
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                        }
                        complexAttribute.setSubAttribute(multiValuedAttribute);

                    } else {
                        SimpleAttribute simpleAttribute = new SimpleAttribute(subSubAttributeSchema.getName(),
                                operation.getValues());
                        DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                        complexAttribute.setSubAttribute(simpleAttribute);
                    }
                    attribute.setSubAttribute(complexAttribute);

                } else {
                    throw new BadRequestException("No such attribute with the name : " + attributeParts[2],
                            ResponseCodeConstants.NO_TARGET);
                }
            }

        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                    ResponseCodeConstants.NO_TARGET);
        }
    }

    private static void createAttributeOnResourceWithPathWithoutFiltersForLevelThree(AbstractSCIMObject oldResource,
            SCIMResourceTypeSchema schema, PatchOperation operation, String[] attributeParts)
            throws CharonException, BadRequestException {

        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);

        if (attributeSchema != null) {

            ComplexAttribute parentAttribute = new ComplexAttribute(attributeSchema.getName());
            DefaultAttributeFactory.createAttribute(attributeSchema, parentAttribute);

            AttributeSchema subAttributeSchena = SchemaUtil
                    .getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);

            if (subAttributeSchena != null) {
                if (subAttributeSchena.getMultiValued()) {

                    MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(subAttributeSchena.getName());
                    DefaultAttributeFactory.createAttribute(subAttributeSchena, multiValuedAttribute);

                    String complexAttributeName =
                            subAttributeSchena.getName() + "_" + operation.getValues() + "_" + SCIMConstants.DEFAULT;
                    ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeName);
                    DefaultAttributeFactory.createAttribute(subAttributeSchena, complexAttribute);

                    AttributeSchema subSubAttributeSchema = SchemaUtil
                            .getAttributeSchema(attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2],
                                    schema);
                    if (subSubAttributeSchema != null) {
                        if (subSubAttributeSchema.getMultiValued()) {
                            MultiValuedAttribute multiValuedSubAttribute = new MultiValuedAttribute(
                                    subSubAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedSubAttribute);
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(operation.getValues());
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    multiValuedSubAttribute.setAttributePrimitiveValue(jsonArray.get(i));
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                            }
                            complexAttribute.setSubAttribute(multiValuedSubAttribute);

                        } else {
                            SimpleAttribute simpleAttribute = new SimpleAttribute(subSubAttributeSchema.getName(),
                                    operation.getValues());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                            complexAttribute.setSubAttribute(simpleAttribute);
                        }
                        multiValuedAttribute.setAttributeValue(complexAttribute);
                        parentAttribute.setSubAttribute(multiValuedAttribute);
                    } else {
                        throw new BadRequestException("No such attribute with the name : " + attributeParts[2],
                                ResponseCodeConstants.NO_TARGET);
                    }

                } else {
                    ComplexAttribute complexAttribute = new ComplexAttribute(subAttributeSchena.getName());
                    DefaultAttributeFactory.createAttribute(subAttributeSchena, complexAttribute);

                    AttributeSchema subSubAttributeSchema = SchemaUtil
                            .getAttributeSchema(attributeParts[0] + "." + attributeParts[1] + "." + attributeParts[2],
                                    schema);

                    if (subSubAttributeSchema != null) {

                        if (subSubAttributeSchema.getMultiValued()) {
                            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(
                                    subSubAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedAttribute);
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(operation.getValues());
                            } catch (JSONException e) {
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    multiValuedAttribute.setAttributePrimitiveValue(jsonArray.get(i));
                                } catch (JSONException e) {
                                    throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX);
                                }
                            }
                            complexAttribute.setSubAttribute(multiValuedAttribute);

                        } else {
                            SimpleAttribute simpleAttribute = new SimpleAttribute(subSubAttributeSchema.getName(),
                                    operation.getValues());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                            complexAttribute.setSubAttribute(simpleAttribute);
                        }
                        parentAttribute.setSubAttribute(complexAttribute);
                        oldResource.setAttribute(parentAttribute);

                    } else {
                        throw new BadRequestException("No such attribute with the name : " + attributeParts[2],
                                ResponseCodeConstants.NO_TARGET);
                    }
                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[1],
                        ResponseCodeConstants.NO_TARGET);
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0],
                    ResponseCodeConstants.NO_TARGET);
        }
    }

    /*
     * This method is to do patch replace for level three attributes with a filter and path value present.
     * @param oldResource
     * @param copyOfOldResource
     * @param schema
     * @param decoder
     * @param operation
     * @param parts
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static void doPatchReplaceOnPathWithFilters(AbstractSCIMObject oldResource,
                                                        SCIMResourceTypeSchema schema,
                                                        JSONDecoder decoder, PatchOperation operation,
                                                        String[] parts)
            throws NotImplementedException, BadRequestException,
            CharonException, JSONException, InternalErrorException {

        if (parts.length != 1) {
            //currently we only support simple filters here.
            String[] filterParts = parts[1].split(" ");

            ExpressionNode expressionNode = new ExpressionNode();
            expressionNode.setAttributeValue(filterParts[0]);
            expressionNode.setOperation(filterParts[1]);
            // According to the specification filter attribute value specified with quotation mark, so we need to
            // remove it if exists.
            expressionNode.setValue(filterParts[2].replaceAll("^\"|\"$", ""));

            if (expressionNode.getOperation().equalsIgnoreCase((SCIMConstants.OperationalConstants.EQ).trim())) {
                if (parts.length == 3) {
                    parts[0] = parts[0] + parts[2];
                }
                String[] attributeParts = parts[0].split("[\\.]");

                if (attributeParts.length == 1) {

                    doPatchReplaceWithFiltersForLevelOne(oldResource, attributeParts,
                            expressionNode, operation, schema, decoder);

                } else if (attributeParts.length == 2) {

                    doPatchReplaceWithFiltersForLevelTwo(oldResource, attributeParts,
                            expressionNode, operation, schema, decoder);

                } else if (attributeParts.length == 3) {

                    doPatchReplaceWithFiltersForLevelThree(oldResource, attributeParts,
                            expressionNode, operation, schema, decoder);
                }

            } else {
                throw new NotImplementedException("Only Eq filter is supported");
            }
        }
    }

    /*
     * This method is to do patch replace for level three attributes with a filter present.
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @param operation
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchReplaceWithFiltersForLevelThree(AbstractSCIMObject oldResource,
                                                                             String[] attributeParts,
                                                                             ExpressionNode expressionNode,
                                                                             PatchOperation operation,
                                                                             SCIMResourceTypeSchema schema,
                                                                             JSONDecoder decoder)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        boolean isValueFound = false;
        if (attribute != null) {

            Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);

            if (subAttribute != null) {
                if (subAttribute.getMultiValued()) {

                    List<Attribute> subValues = ((MultiValuedAttribute) subAttribute).getAttributeValues();
                    if (subValues != null) {
                        for (Attribute subValue: subValues) {
                            Map<String, Attribute> subSubAttributes =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            //this map is to avoid concurrent modification exception.
                            Map<String, Attribute> tempSubSubAttributes = (Map<String, Attribute>)
                                    CopyUtil.deepCopy(subSubAttributes);

                            for (Iterator<Attribute> iterator = tempSubSubAttributes.values().iterator();
                                 iterator.hasNext();) {
                                Attribute subSubAttribute = iterator.next();

                                if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {

                                    Attribute replacingAttribute = subSubAttributes.get(attributeParts[2]);
                                    if (replacingAttribute == null) {
                                       AttributeSchema replacingAttributeSchema = SchemaUtil.getAttributeSchema
                                               (attributeParts[0] + "." + attributeParts[1] + "." +
                                                       attributeParts[2], schema);

                                        if (replacingAttributeSchema != null) {

                                            if (replacingAttributeSchema.getMultiValued()) {
                                                ((ComplexAttribute) subValue).setSubAttribute(
                                                        decoder.buildPrimitiveMultiValuedAttribute
                                                        (replacingAttributeSchema, (JSONArray) operation.getValues()));
                                                break;

                                            } else {
                                                ((ComplexAttribute) subValue).setSubAttribute(
                                                        decoder.buildSimpleAttribute
                                                                (replacingAttributeSchema, operation.getValues()));
                                                break;

                                            }

                                        } else {
                                            throw new BadRequestException("No such attribute with the name : " +
                                                    attributeParts[0] + "." + attributeParts[1] + "." +
                                                    attributeParts[2]);
                                        }
                                    }
                                    if (replacingAttribute.getMutability().equals
                                            (SCIMDefinitions.Mutability.READ_ONLY) ||
                                            replacingAttribute.getMutability().equals
                                                    (SCIMDefinitions.Mutability.IMMUTABLE)) {
                                        throw new BadRequestException
                                                ("Can not remove a immutable attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {

                                        if (replacingAttribute.getMultiValued()) {
                                            ((MultiValuedAttribute) replacingAttribute).getAttributePrimitiveValues().
                                                    remove(expressionNode.getValue());
                                            ((MultiValuedAttribute) replacingAttribute).
                                                    setAttributePrimitiveValue(operation.getValues());
                                        } else  {
                                            ((SimpleAttribute) (replacingAttribute)).setValue(operation.getValues());
                                        }
                                        isValueFound = true;
                                    }
                                }
                            }
                        }
                        if (!isValueFound) {
                            throw new BadRequestException("No matching filter value found.",
                                    ResponseCodeConstants.NO_TARGET);
                        }
                    }
                } else {
                    throw new BadRequestException("Attribute : " + attributeParts[1] + " " +
                            "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                }

            } else {
                AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0]
                        + "." + attributeParts[1], schema);
                if (subAttributeSchema != null) {

                    MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(subAttributeSchema.getName());
                    DefaultAttributeFactory.createAttribute(subAttributeSchema, multiValuedAttribute);

                    String complexName = subAttributeSchema.getName() + "_" +
                            SCIMConstants.DEFAULT + "_" + SCIMConstants.DEFAULT;

                    ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
                    DefaultAttributeFactory.createAttribute(subAttributeSchema, complexAttribute);

                    AttributeSchema subSubAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0] + "."
                            + attributeParts[1] + "." + attributeParts[2], schema);

                    if (subSubAttributeSchema != null) {
                        if (subSubAttributeSchema.getMultiValued()) {

                            MultiValuedAttribute multiValuedSubAttribute =
                                    new MultiValuedAttribute(subSubAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedSubAttribute);
                            multiValuedSubAttribute.setAttributePrimitiveValue(operation.getValues());
                            complexAttribute.setSubAttribute(multiValuedSubAttribute);
                            multiValuedAttribute.setAttributeValue(complexAttribute);
                            ((ComplexAttribute) (attribute)).setSubAttribute(multiValuedAttribute);
                        } else {

                            SimpleAttribute simpleAttribute =
                                    new SimpleAttribute(subSubAttributeSchema.getName(), operation.getValues());
                            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                            complexAttribute.setSubAttribute(simpleAttribute);
                            multiValuedAttribute.setAttributeValue(complexAttribute);
                            ((ComplexAttribute) (attribute)).setSubAttribute(multiValuedAttribute);
                        }

                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]
                                + "." + attributeParts[2] + "does not exists.", ResponseCodeConstants.INVALID_PATH);
                    }
                } else {
                    throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]
                            + "does not exists.", ResponseCodeConstants.INVALID_PATH);
                }
            }

        } else {
            AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);

            if (attributeSchema != null) {

                if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {

                    ComplexAttribute extensionAttribute = new ComplexAttribute(attributeSchema.getName());
                    DefaultAttributeFactory.createAttribute(attributeSchema, extensionAttribute);

                    AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0]
                            + "." + attributeParts[1], schema);
                    if (subAttributeSchema != null) {

                        MultiValuedAttribute multiValuedAttribute =
                                new MultiValuedAttribute(subAttributeSchema.getName());
                        DefaultAttributeFactory.createAttribute(subAttributeSchema, multiValuedAttribute);

                        String complexName = subAttributeSchema.getName() + "_" +
                                SCIMConstants.DEFAULT + "_" + SCIMConstants.DEFAULT;

                        ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
                        DefaultAttributeFactory.createAttribute(subAttributeSchema, complexAttribute);

                        AttributeSchema subSubAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0] + "."
                                + attributeParts[1] + "." + attributeParts[2], schema);

                        if (subSubAttributeSchema != null) {
                            if (subSubAttributeSchema.getMultiValued()) {

                                MultiValuedAttribute multiValuedSubAttribute =
                                        new MultiValuedAttribute(subSubAttributeSchema.getName());
                                DefaultAttributeFactory.createAttribute(subSubAttributeSchema, multiValuedSubAttribute);
                                multiValuedAttribute.setAttributePrimitiveValue(operation.getValues());
                                complexAttribute.setSubAttribute(multiValuedSubAttribute);
                                (multiValuedAttribute).setAttributeValue(complexAttribute);
                                extensionAttribute.setSubAttribute(multiValuedAttribute);
                                oldResource.setAttribute(extensionAttribute);
                            } else {

                                SimpleAttribute simpleAttribute =
                                        new SimpleAttribute(subSubAttributeSchema.getName(), operation.getValues());
                                DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);
                                complexAttribute.setSubAttribute(simpleAttribute);
                                (multiValuedAttribute).setAttributeValue(simpleAttribute);
                                extensionAttribute.setSubAttribute(multiValuedAttribute);
                                oldResource.setAttribute(extensionAttribute);
                            }

                        } else {
                            throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]
                                    + "." + attributeParts[2] + "does not exists.", ResponseCodeConstants.INVALID_PATH);
                        }
                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]
                                + "does not exists.", ResponseCodeConstants.INVALID_PATH);
                    }

                }

            } else {
                throw new BadRequestException("Attribute : " + attributeParts[0]  + "does not exists.",
                        ResponseCodeConstants.INVALID_PATH);
            }
        }
        return oldResource;


    }

    /*
     * This method is to do patch replace for level two attributes with a filter present.
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @param operation
     * @param schema
     * @param decoder
     * @return
     * @throws CharonException
     * @throws BadRequestException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static AbstractSCIMObject doPatchReplaceWithFiltersForLevelTwo(AbstractSCIMObject oldResource,
                                                                           String[] attributeParts,
                                                                           ExpressionNode expressionNode,
                                                                           PatchOperation operation,
                                                                           SCIMResourceTypeSchema schema,
                                                                           JSONDecoder decoder)
            throws CharonException, BadRequestException, JSONException, InternalErrorException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        boolean isValueFound = false;
        if (attribute != null) {

            if (attribute.getMultiValued()) {

                List<Attribute> subValues = ((MultiValuedAttribute) attribute).getAttributeValues();
                if (subValues != null) {
                    for (Attribute subValue: subValues) {
                        Map<String, Attribute> subAttributes = ((ComplexAttribute) subValue).getSubAttributesList();
                        //this map is to avoid concurrent modification exception.
                        Map<String, Attribute> tempSubAttributes = (Map<String, Attribute>)
                                CopyUtil.deepCopy(subAttributes);

                        for (Iterator<Attribute> iterator = tempSubAttributes.values().iterator();
                             iterator.hasNext();) {
                            Attribute subAttribute = iterator.next();

                            if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {

                                if (((SimpleAttribute) subAttribute).getValue().equals(expressionNode.getValue())) {
                                    Attribute replacingAttribute = subAttributes.get(attributeParts[1]);
                                    if (replacingAttribute == null) {
                                        //add the attribute
                                        AttributeSchema replacingAttributeSchema =
                                                SchemaUtil.getAttributeSchema
                                                        (attributeParts[0] + "." + attributeParts[1], schema);
                                        if (replacingAttributeSchema.getMultiValued()) {
                                            MultiValuedAttribute multiValuedAttribute =
                                                    new MultiValuedAttribute(replacingAttributeSchema.getName());
                                            DefaultAttributeFactory.createAttribute
                                                    (replacingAttributeSchema, multiValuedAttribute);
                                            multiValuedAttribute.setAttributePrimitiveValue(operation.getValues());
                                            ((ComplexAttribute) subValue).setSubAttribute(multiValuedAttribute);
                                            break;
                                        } else {
                                            SimpleAttribute simpleAttribute =
                                                    new SimpleAttribute(replacingAttributeSchema.getName(),
                                                            operation.getValues());
                                            DefaultAttributeFactory.createAttribute
                                                    (replacingAttributeSchema, simpleAttribute);
                                            ((ComplexAttribute) subValue).setSubAttribute(simpleAttribute);
                                            break;
                                        }
                                    }
                                    if (replacingAttribute.getMutability().
                                            equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            replacingAttribute.getMutability().
                                                    equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                        throw new BadRequestException
                                                ("Can not remove a immutable attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        if (replacingAttribute.getMultiValued()) {
                                            ((MultiValuedAttribute) replacingAttribute).getAttributePrimitiveValues().
                                                    remove(expressionNode.getValue());
                                            ((MultiValuedAttribute) replacingAttribute).
                                                    setAttributePrimitiveValue(operation.getValues());
                                        } else  {
                                            ((SimpleAttribute) (replacingAttribute)).setValue(operation.getValues());
                                        }
                                        isValueFound = true;
                                    }
                                }
                            }
                        }
                    }
                    if (!isValueFound) {
                        throw new BadRequestException("No matching filter value found.",
                                ResponseCodeConstants.NO_TARGET);
                    }
                }
            } else if (attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is only valid for extension
                Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);
                if (subAttribute == null) {
                    //add the attribute
                    AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0] + "."
                            + attributeParts[1], schema);
                    if (subAttributeSchema != null) {

                        if (subAttributeSchema.getMultiValued()) {
                            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute
                                    (subAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subAttributeSchema, multiValuedAttribute);

                            multiValuedAttribute.setAttributeValue(
                                    decoder.buildComplexAttribute(subAttributeSchema,
                                            (JSONObject) operation.getValues()));
                            ((ComplexAttribute) (attribute)).setSubAttribute(multiValuedAttribute);

                        } else {
                            throw new BadRequestException("Attribute : " + attributeParts[1]  +
                                    "is not a multi valued attribute.", ResponseCodeConstants.INVALID_PATH);
                        }
                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]  +
                                "does not exists.", ResponseCodeConstants.INVALID_PATH);
                    }

                } else {
                    List<Attribute> subValues = ((MultiValuedAttribute) (subAttribute)).getAttributeValues();
                    if (subValues != null) {
                        for (Iterator<Attribute> subValueIterator = subValues.iterator();
                             subValueIterator.hasNext(); ) {
                            Attribute subValue = subValueIterator.next();

                            Map<String, Attribute> subValuesSubAttribute =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            for (Iterator<Attribute> iterator =
                                 subValuesSubAttribute.values().iterator(); iterator.hasNext(); ) {

                                Attribute subSubAttribute = iterator.next();
                                if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                    if (((SimpleAttribute) (subSubAttribute)).getValue().equals
                                            (expressionNode.getValue())) {
                                        if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                                subValue.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {

                                            throw new BadRequestException
                                                    ("Can not remove a immutable attribute or a read-only attribute",
                                                            ResponseCodeConstants.MUTABILITY);
                                        } else {
                                            subValueIterator.remove();
                                            isValueFound = true;
                                        }
                                    }
                                }
                            }
                        }
                        AttributeSchema attributeSchema =
                                SchemaUtil.getAttributeSchema(attributeParts[0] + "." + attributeParts[1], schema);
                        subValues.add(decoder.buildComplexAttribute(attributeSchema,
                                (JSONObject) operation.getValues()));
                        if (!isValueFound) {
                            throw new BadRequestException("No matching filter value found.",
                                    ResponseCodeConstants.NO_TARGET);
                        }
                    }
                }
            } else {
                throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                        "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
            }
        } else {
            //add the attribute
            AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);
            if (attributeSchema != null) {
                if (attributeSchema.getMultiValued()) {
                    MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                    DefaultAttributeFactory.createAttribute(attributeSchema, multiValuedAttribute);

                    String complexName = attributeSchema.getName() + "_" +
                            SCIMConstants.DEFAULT + "_" + SCIMConstants.DEFAULT;
                    ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
                    DefaultAttributeFactory.createAttribute(attributeSchema, complexAttribute);

                    AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0] + "."
                                                                    + attributeParts[1], schema);
                    if (subAttributeSchema != null) {
                        if (subAttributeSchema.getMultiValued()) {

                            MultiValuedAttribute multiValuedSubAttribute =
                                    new MultiValuedAttribute(subAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subAttributeSchema, multiValuedSubAttribute);
                            multiValuedAttribute.setAttributePrimitiveValue(operation.getValues());
                            complexAttribute.setSubAttribute(multiValuedSubAttribute);
                            multiValuedAttribute.setAttributeValue(complexAttribute);
                            oldResource.setAttribute(multiValuedAttribute);
                        } else {

                            SimpleAttribute simpleAttribute =
                                    new SimpleAttribute(subAttributeSchema.getName(), operation.getValues());
                            DefaultAttributeFactory.createAttribute(subAttributeSchema, simpleAttribute);
                            complexAttribute.setSubAttribute(simpleAttribute);
                            multiValuedAttribute.setAttributeValue(complexAttribute);
                            oldResource.setAttribute(multiValuedAttribute);
                        }

                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]  +
                                "does not exists.", ResponseCodeConstants.INVALID_PATH);
                    }

                } else {

                    ComplexAttribute extensionComplexAttribute = new ComplexAttribute(attributeSchema.getName());
                    DefaultAttributeFactory.createAttribute(attributeSchema, extensionComplexAttribute);


                    AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0] + "."
                            + attributeParts[1], schema);
                    if (subAttributeSchema != null) {

                        if (subAttributeSchema.getMultiValued()) {
                            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute
                                    (subAttributeSchema.getName());
                            DefaultAttributeFactory.createAttribute(subAttributeSchema, multiValuedAttribute);

                            multiValuedAttribute.setAttributeValue(
                                    decoder.buildComplexAttribute(subAttributeSchema,
                                            (JSONObject) operation.getValues()));
                            oldResource.setAttribute(multiValuedAttribute);

                        } else {
                            throw new BadRequestException("Attribute : " + attributeParts[1]  +
                                    "is not a multi valued attribute.", ResponseCodeConstants.INVALID_PATH);
                        }
                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + "." + attributeParts[1]  +
                                "does not exists.", ResponseCodeConstants.INVALID_PATH);
                    }

                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0],
                        ResponseCodeConstants.INVALID_PATH);
            }

        }
        return oldResource;


    }


    /*
     * This method is to do patch replace for level one attributes with a filter present.
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @param operation
     * @param schema
     * @param decoder
     * @return
     * @throws BadRequestException
     * @throws CharonException
     * @throws JSONException
     * @throws InternalErrorException
     */
    private static AbstractSCIMObject doPatchReplaceWithFiltersForLevelOne(AbstractSCIMObject oldResource,
                                                                           String[] attributeParts,
                                                                           ExpressionNode expressionNode,
                                                                           PatchOperation operation,
                                                                           SCIMResourceTypeSchema schema,
                                                                           JSONDecoder decoder)

            throws BadRequestException, CharonException, JSONException, InternalErrorException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        boolean isValueFound = false;
        if (attribute != null) {
            if (!attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is for paths value as 'attributeX[attributeX EQ yyy]'
                //this is multivalued primitive case
                if (attribute.getMultiValued()) {
                    List<Object> valuesList  = ((MultiValuedAttribute)
                            (attribute)).getAttributePrimitiveValues();
                    for (Iterator<Object> iterator =
                         valuesList.iterator(); iterator.hasNext();) {
                        Object item = iterator.next();
                        //we only support "EQ" filter
                        if (item.equals(expressionNode.getValue())) {

                            if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                    attribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                throw new BadRequestException
                                        ("Can not remove a immutable attribute or a read-only attribute",
                                                ResponseCodeConstants.MUTABILITY);
                            } else {
                                iterator.remove();
                                isValueFound = true;
                            }
                        }
                    }
                    if (!isValueFound) {
                        throw new BadRequestException("No matching filter value found.",
                                ResponseCodeConstants.NO_TARGET);
                    }
                    valuesList.add(operation.getValues());

                } else {
                    throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                            "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                }

            } else {
                if (attribute.getMultiValued()) {
                    //this is for paths value as 'emails[value EQ vindula@wso2.com]'
                    //this is multivalued complex case

                    List<Attribute> subValues = ((MultiValuedAttribute) (attribute)).getAttributeValues();
                    if (subValues != null) {
                        for (Iterator<Attribute> subValueIterator = subValues.iterator();
                             subValueIterator.hasNext(); ) {
                            Attribute subValue = subValueIterator.next();

                            Map<String, Attribute> subValuesSubAttribute =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            for (Iterator<Attribute> iterator =
                                 subValuesSubAttribute.values().iterator(); iterator.hasNext(); ) {

                                Attribute subAttribute = iterator.next();
                                if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                    if (((SimpleAttribute)
                                            (subAttribute)).getValue().equals(expressionNode.getValue())) {
                                        if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                                subValue.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                            throw new BadRequestException
                                                    ("Can not remove a immutable attribute or a read-only attribute",
                                                            ResponseCodeConstants.MUTABILITY);
                                        } else {
                                            subValueIterator.remove();
                                            isValueFound = true;
                                        }
                                    }
                                }
                            }
                        }
                        if (!isValueFound) {
                            throw new BadRequestException("No matching filter value found.",
                                    ResponseCodeConstants.NO_TARGET);
                        }
                        AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);
                        subValues.add(decoder.buildComplexAttribute(attributeSchema,
                                (JSONObject) operation.getValues()));

                    }
                } else {
                    //this is complex attribute which has multi valued primitive sub attribute.
                    Attribute subAttribute = attribute.getSubAttribute(expressionNode.getAttributeValue());
                    if (subAttribute != null) {

                        if (subAttribute.getMultiValued()) {
                            List<Object> valuesList = ((MultiValuedAttribute)
                                    (subAttribute)).getAttributePrimitiveValues();
                            for (Iterator<Object> iterator =
                                 valuesList.iterator(); iterator.hasNext(); ) {
                                Object item = iterator.next();
                                //we only support "EQ" filter
                                if (item.equals(expressionNode.getValue())) {
                                    if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                        throw new BadRequestException
                                                ("Can not remove a immutable attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                        isValueFound = true;
                                    }
                                }
                            }
                            if (!isValueFound) {
                                throw new BadRequestException("No matching filter value found.",
                                        ResponseCodeConstants.NO_TARGET);
                            }
                            valuesList.add(operation.getValues());

                        } else {
                            throw new BadRequestException("Sub attribute : " +
                                    expressionNode.getAttributeValue() + " " +
                                    "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                        }

                    } else {
                        AttributeSchema subAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0]
                                + "." + expressionNode.getAttributeValue(), schema);
                        if (subAttributeSchema.getMultiValued()) {
                            ((ComplexAttribute) (attribute)).setSubAttribute
                                    (decoder.buildPrimitiveMultiValuedAttribute(subAttributeSchema,
                                            (JSONArray) operation.getValues()));

                        } else {
                            if (subAttributeSchema.getMultiValued()) {
                                ((ComplexAttribute) (attribute)).setSubAttribute
                                        (decoder.buildSimpleAttribute(subAttributeSchema, operation.getValues()));
                            }
                        }
                    }
                }
            }
        } else {
            //add the attribute
           AttributeSchema attributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0], schema);
            if (attributeSchema != null) {
                if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (attributeSchema.getMultiValued()) {
                        MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                        DefaultAttributeFactory.createAttribute(attributeSchema, multiValuedAttribute);

                        String complexName  = attributeSchema.getName() + "_" + SCIMConstants.DEFAULT + "_" +
                                SCIMConstants.DEFAULT;
                        ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
                        DefaultAttributeFactory.createAttribute(attributeSchema, complexAttribute);

                        AttributeSchema subValuesSubAttributeSchema = SchemaUtil.getAttributeSchema(attributeParts[0]
                                + "." + expressionNode.getAttributeValue(), schema);
                        if (subValuesSubAttributeSchema != null) {
                            SimpleAttribute simpleAttribute =
                                    new SimpleAttribute(subValuesSubAttributeSchema.getName(), operation.getValues());
                            DefaultAttributeFactory.createAttribute(subValuesSubAttributeSchema, simpleAttribute);
                            complexAttribute.setSubAttribute(simpleAttribute);
                            multiValuedAttribute.setAttributeValue(complexAttribute);
                            oldResource.setAttribute(multiValuedAttribute);
                        } else {
                            throw new BadRequestException("No such attribute with name : " + attributeParts[0]
                                    + "." + expressionNode.getAttributeValue(), ResponseCodeConstants.INVALID_PATH);
                        }


                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + " " +
                                "is not a multivalued attribute.",
                                ResponseCodeConstants.INVALID_PATH);
                    }
                } else {
                    if (attributeSchema.getMultiValued()) {
                        //primitive case
                        MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
                        DefaultAttributeFactory.createAttribute(attributeSchema, multiValuedAttribute);
                        multiValuedAttribute.setAttributePrimitiveValue(operation.getValues());
                        oldResource.setAttribute(multiValuedAttribute);
                    } else {
                        throw new BadRequestException("Attribute : " + attributeParts[0] + " " +
                                "is not a multivalued attribute.",
                                ResponseCodeConstants.INVALID_PATH);
                    }
                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0],
                        ResponseCodeConstants.INVALID_PATH);
            }

        }
        return oldResource;
    }

    /*
     *
     * @param oldResource
     * @param copyOfOldResource
     * @param schema
     * @param decoder
     * @param operation
     * @return
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchReplaceOnResource(AbstractSCIMObject oldResource, AbstractSCIMObject
            copyOfOldResource, SCIMResourceTypeSchema schema, JSONDecoder decoder, PatchOperation operation)
            throws CharonException {

        try {
            AbstractSCIMObject attributeHoldingSCIMObject = decoder.decode(operation.getValues().toString(), schema);

            if (oldResource != null) {

                for (String attributeName : attributeHoldingSCIMObject.getAttributeList().keySet()) {
                    Attribute oldAttribute = oldResource.getAttribute(attributeName);
                    if (oldAttribute != null) {
                        // if the attribute is there, append it.
                        if (oldAttribute.getMultiValued()) {
                            //this is multivalued complex case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMObject.getAttribute(attributeName);
                            if (oldAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE) ||
                                    oldAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {

                                throw new BadRequestException("Immutable or Read-Only attributes can not be modified.",
                                        ResponseCodeConstants.MUTABILITY);
                            } else {
                                //delete the old attribute
                                oldResource.deleteAttribute(attributeName);
                                //replace with new attribute
                                oldResource.setAttribute(attributeValue);
                            }

                        } else if (oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this is the complex attribute case.
                            Map<String, Attribute> subAttributeList =
                                    ((ComplexAttribute) attributeHoldingSCIMObject.
                                            getAttribute(attributeName)).getSubAttributesList();

                            for (Map.Entry<String, Attribute> subAttrib : subAttributeList.entrySet()) {
                                Attribute subAttribute = oldAttribute.getSubAttribute(subAttrib.getKey());

                                if (subAttribute != null) {
                                    if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                        if (subAttribute.getMultiValued()) {
                                            //extension schema is the only one who reaches here.
                                            MultiValuedAttribute attributeSubValue = (MultiValuedAttribute)
                                                    ((ComplexAttribute) attributeHoldingSCIMObject.
                                                            getAttribute(attributeName)).
                                                            getSubAttribute(subAttrib.getKey());

                                            if (subAttribute.getMutability().equals
                                                    (SCIMDefinitions.Mutability.IMMUTABLE) ||
                                                    subAttribute.getMutability().equals
                                                            (SCIMDefinitions.Mutability.READ_ONLY)) {

                                                throw new BadRequestException
                                                        ("Immutable or Read-Only attributes can not be modified.",
                                                                ResponseCodeConstants.MUTABILITY);
                                            } else {
                                                //delete the old attribute
                                                ((ComplexAttribute) (oldAttribute)).removeSubAttribute
                                                        (subAttribute.getName());
                                                //replace with new attribute
                                                ((ComplexAttribute) (oldAttribute)).setSubAttribute(attributeSubValue);
                                            }

                                        } else {
                                            //extension schema is the only one who reaches here.
                                            Map<String, Attribute> subSubAttributeList = ((ComplexAttribute)
                                                    (attributeHoldingSCIMObject.getAttribute(attributeName).
                                                            getSubAttribute(subAttrib.getKey()))).
                                                    getSubAttributesList();

                                            for (Map.Entry<String, Attribute> subSubAttrb :
                                                    subSubAttributeList.entrySet()) {
                                                Attribute subSubAttribute = oldAttribute.getSubAttribute
                                                        (subAttrib.getKey()).getSubAttribute(subSubAttrb.getKey());
                                                Attribute subSubAttributeValue = attributeHoldingSCIMObject
                                                    .getAttribute(attributeName).getSubAttribute(subAttrib.getKey())
                                                    .getSubAttribute(subSubAttrb.getKey());

                                                if (subSubAttribute != null) {
                                                    if (subSubAttribute.getMultiValued()) {

                                                        if (subSubAttribute.getMutability().equals
                                                                (SCIMDefinitions.Mutability.IMMUTABLE) ||
                                                                subSubAttribute.getMutability().equals
                                                                        (SCIMDefinitions.Mutability.READ_ONLY)) {

                                                            throw new BadRequestException
                                                                    ("Immutable or Read-Only attributes " +
                                                                            "can not be modified.",
                                                                            ResponseCodeConstants.MUTABILITY);
                                                        } else {
                                                            //delete the old attribute
                                                            ((ComplexAttribute) (oldAttribute.getSubAttribute
                                                                    (subAttrib.getKey()))).removeSubAttribute
                                                                    (subSubAttribute.getName());
                                                            //replace with new attribute
                                                            ((ComplexAttribute) (oldAttribute.getSubAttribute
                                                                    (subAttrib.getKey()))).setSubAttribute
                                                                    (subSubAttributeValue);
                                                        }
                                                    } else {
                                                        ((SimpleAttribute) subSubAttribute).setValue(
                                                                ((SimpleAttribute) subSubAttrb.getValue()).getValue());
                                                    }
                                                } else {
                                                    ((ComplexAttribute) (subAttribute)).setSubAttribute(
                                                            subSubAttrb.getValue());
                                                }
                                            }
                                        }
                                    } else {
                                        if (subAttribute.getMutability().equals
                                                (SCIMDefinitions.Mutability.IMMUTABLE) ||
                                                subAttribute.getMutability().equals
                                                        (SCIMDefinitions.Mutability.READ_ONLY)) {

                                            throw new BadRequestException("Immutable or Read-Only " +
                                                    "attributes can not be modified.",
                                                    ResponseCodeConstants.MUTABILITY);
                                        } else {
                                            //delete the old attribute
                                            ((ComplexAttribute) (oldAttribute)).removeSubAttribute
                                                    (subAttribute.getName());
                                            //replace with new attribute
                                            ((ComplexAttribute) (oldAttribute)).setSubAttribute
                                                    (subAttributeList.get(subAttribute.getName()));
                                        }
                                    }
                                } else {
                                    //add the attribute
                                    ((ComplexAttribute) oldAttribute).setSubAttribute
                                            (subAttrib.getValue());
                                }
                            }
                        } else {
                            if (oldAttribute.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE) ||
                                    oldAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {

                                throw new BadRequestException("Immutable or Read-Only attributes can not be modified.",
                                        ResponseCodeConstants.MUTABILITY);
                            } else {
                                // this is the simple attribute case.replace the value
                                ((SimpleAttribute) oldAttribute).setValue
                                        (((SimpleAttribute) attributeHoldingSCIMObject.getAttribute
                                                (oldAttribute.getName())).getValue());
                            }
                        }
                    } else {
                        //add the attribute
                        oldResource.setAttribute(attributeHoldingSCIMObject.getAttributeList().get(attributeName));
                    }
                }
                AbstractSCIMObject validatedResource = ServerSideValidator.validateUpdatedSCIMObject
                        (copyOfOldResource, oldResource, schema);

                return validatedResource;
            } else  {
                throw new CharonException("Error in getting the old resource.");
            }
        } catch (BadRequestException | CharonException e) {
            throw new CharonException("Error in performing the add operation", e);
        }
    }
}
