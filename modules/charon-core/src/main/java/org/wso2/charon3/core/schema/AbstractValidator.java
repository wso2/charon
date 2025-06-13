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
package org.wso2.charon3.core.schema;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.AbstractAttribute;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.utils.CopyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This defined the validation algorithms accroding to SCIM spec 2.0.
 */

public abstract class AbstractValidator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractValidator.class);

    /*
     * Validate SCIMObject for required attributes given the object and the corresponding schema.
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSCIMObjectForRequiredAttributes(AbstractSCIMObject scimObject,
                                                               ResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for required attributes.
            if (attributeSchema.getRequired()) {
                if (!attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Required attribute " + attributeSchema.getName() + " is missing in the SCIM " +
                            "Object.";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }

            //check for required sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            validateSCIMObjectForRequiredSubAttributes(attribute, attributeSchema, scimObject);
        }
    }

    /**
     * Validate whether scim object updates due to one patch operation, violate the required attributes conditions.
     *
     * @param oldObject      Scim object before update.
     * @param newObject      Scim object after update.
     * @param resourceSchema Schema for the scim resource.
     * @throws BadRequestException When error occurred due to client issue.
     * @throws CharonException     When error occurred due to validation failure.
     */
    public static void validatePatchOperationEffectForRequiredAttributes(AbstractSCIMObject oldObject,
                                                                         AbstractSCIMObject newObject,
                                                                         ResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {

        // Get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        // Get attribute list from old scim object.
        Map<String, Attribute> oldAttributeList = oldObject.getAttributeList();
        // Get attribute list from new scim object.
        Map<String, Attribute> newAttributeList = newObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            // Check for required attributes.
            if (attributeSchema.getRequired()) {
                /*
                If the attribute is not present in the updated object but included in the old object,
                it means the operation has removed the required attribute.
                 */
                if (!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    String error = "Required attribute " + attributeSchema.getName() + " is missing in the SCIM " +
                            "Object.";
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                }
            }

            // Check for required sub attributes.
            AbstractAttribute newAttribute = (AbstractAttribute) newAttributeList.get(attributeSchema.getName());
            AbstractAttribute oldAttribute = (AbstractAttribute) oldAttributeList.get(attributeSchema.getName());
            validatePatchOperationEffectForRequiredSubAttributes(oldAttribute, newAttribute, attributeSchema,
                    newObject);
        }
    }

    /*
     * Validate SCIMObject for required sub attributes given the object and the corresponding schema.
     *
     * @param attribute
     * @param attributeSchema
     * @param scimObject
     * @throws CharonException
     * @throws BadRequestException
     */
    private static void validateSCIMObjectForRequiredSubAttributes(AbstractAttribute attribute,
                                                                   AttributeSchema attributeSchema,
                                                                   AbstractSCIMObject scimObject) throws
            CharonException, BadRequestException {

        if (attribute != null) {
            List<AttributeSchema> subAttributesSchemaList =
                    ((AttributeSchema) attributeSchema).getSubAttributeSchemas();

            if (subAttributesSchemaList != null) {
                for (AttributeSchema subAttributeSchema : subAttributesSchemaList) {
                    if (subAttributeSchema.getRequired()) {

                        if (attribute instanceof ComplexAttribute) {
                            if (attribute.getSubAttribute(subAttributeSchema.getName()) == null) {
                                String error = "Required sub attribute: " + subAttributeSchema.getName()
                                        + " is missing in the SCIM Attribute: " + attribute.getName();
                                throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                            } else if (attribute.getSubAttribute(
                                    subAttributeSchema.getName()) instanceof SimpleAttribute) {
                                // If the attributes updated with "", that check is happening here.
                                if (StringUtils.isEmpty(((SimpleAttribute) attribute.getSubAttribute(
                                        subAttributeSchema.getName())).getValue().toString())) {
                                    String error = "Required sub attribute: " + subAttributeSchema.getName()
                                            + " is missing in the SCIM Attribute: " + attribute.getName();
                                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                                }
                            }
                        } else if (attribute instanceof MultiValuedAttribute) {
                            List<Attribute> values =
                                    ((MultiValuedAttribute) attribute).getAttributeValues();
                            for (Attribute value : values) {
                                if (value instanceof ComplexAttribute) {
                                    if (value.getSubAttribute(subAttributeSchema.getName()) == null) {
                                        String error = "Required sub attribute: " + subAttributeSchema.getName()
                                                + ", is missing in the SCIM Attribute: " + attribute.getName();
                                        throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                                    }
                                }
                            }
                        }
                    }

                    // Check for canonical attributes in groups.
                    validateCanonicalAttributesInScimObject(attribute, subAttributeSchema, scimObject);

                    //Following is only applicable for extension schema validation.
                    AbstractAttribute subAttribute = null;
                    if (attribute instanceof ComplexAttribute) {
                        subAttribute = (AbstractAttribute) ((ComplexAttribute) attribute).getSubAttribute
                                (subAttributeSchema.getName());
                    } else if (attribute instanceof MultiValuedAttribute) {
                        List<Attribute> subAttributeList = ((MultiValuedAttribute) attribute).getAttributeValues();
                        for (Attribute subAttrbte : subAttributeList) {
                            if (subAttrbte.getName().equals(subAttributeSchema.getName())) {
                                subAttribute = (AbstractAttribute) subAttrbte;
                            }
                        }
                    }
                    List<AttributeSchema> subSubAttributesSchemaList = subAttributeSchema.getSubAttributeSchemas();
                    if (subSubAttributesSchemaList != null) {
                        validateSCIMObjectForRequiredSubAttributes(subAttribute, subAttributeSchema, scimObject);
                    }
                }
            }
        }
    }

    /**
     * Validation the patch operation effect on sub attributes of a scim attribute.
     *
     * @param oldAttribute    Old scim attribute.
     * @param newAttribute    Updated scim attribute.
     * @param attributeSchema Attribute schema of the attribute.
     * @param scimObject      Scim object.
     * @throws CharonException     When error occurred during the validation.
     * @throws BadRequestException When error occurred due to the client issues.
     */
    private static void validatePatchOperationEffectForRequiredSubAttributes(AbstractAttribute oldAttribute,
                                                                             AbstractAttribute newAttribute,
                                                                             AttributeSchema attributeSchema,
                                                                             AbstractSCIMObject scimObject)
            throws CharonException, BadRequestException {

        if (newAttribute == null || attributeSchema == null || oldAttribute == null) {
            return;
        }
        List<AttributeSchema> subAttributesSchemaList = attributeSchema.getSubAttributeSchemas();
        if (subAttributesSchemaList == null) {
            return;
        }
        for (AttributeSchema subAttributeSchema : subAttributesSchemaList) {
            // Nothing to validate id the attribute schema has required=false.
            if (!subAttributeSchema.getRequired()) {
                continue;
            }
            if (newAttribute instanceof ComplexAttribute) {
                // If the sub attribute contained in the old attribute but not in the new attribute.
                if (newAttribute.getSubAttribute(subAttributeSchema.getName()) == null
                        && oldAttribute.getSubAttribute(subAttributeSchema.getName()) != null) {
                    String error = "Required sub attribute: " + subAttributeSchema.getName()
                            + " is missing in the SCIM Attribute: " + newAttribute.getName();
                    throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                } else if (newAttribute.getSubAttribute(
                        subAttributeSchema.getName()) instanceof SimpleAttribute) {
                    // If the attributes updated with "", that check is happening here.
                    if (StringUtils.isEmpty(((SimpleAttribute) newAttribute.getSubAttribute(
                            subAttributeSchema.getName())).getValue().toString())) {
                        String error = "Required sub attribute: " + subAttributeSchema.getName()
                                + " is missing in the SCIM Attribute: " + newAttribute.getName();
                        throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                    }
                }
            } else if (newAttribute instanceof MultiValuedAttribute) {
                List<Attribute> newValues =
                        ((MultiValuedAttribute) newAttribute).getAttributeValues();
                for (Attribute value : newValues) {
                    if (value instanceof ComplexAttribute) {
                        if (value.getSubAttribute(subAttributeSchema.getName()) == null) {
                            String error = "Required sub attribute: " + subAttributeSchema.getName()
                                    + ", is missing in the SCIM Attribute: " + newAttribute.getName();
                            throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                        }
                    }
                }
            }
            // Check for canonical attributes in groups.
            validateCanonicalAttributesInScimObject(newAttribute, subAttributeSchema, scimObject);
            /*
            Following is only applicable for extension schema validation.
            Extension schema also considered as complex Attribute.
            Therefore, The Complex Attributes inside the extension will validate here.
             */
            AbstractAttribute newSubAttribute = null;
            AbstractAttribute oldSubAttribute = null;
            if (newAttribute instanceof ComplexAttribute) {
                newSubAttribute = (AbstractAttribute) (newAttribute).getSubAttribute(subAttributeSchema.getName());
                oldSubAttribute = (AbstractAttribute) (oldAttribute).getSubAttribute(subAttributeSchema.getName());
            } else if (newAttribute instanceof MultiValuedAttribute) {
                List<Attribute> subAttributeList = ((MultiValuedAttribute) newAttribute).getAttributeValues();
                for (Attribute subAttrbte : subAttributeList) {
                    if (subAttrbte.getName().equals(subAttributeSchema.getName())) {
                        newSubAttribute = (AbstractAttribute) subAttrbte;
                    }
                }
            }
            List<AttributeSchema> subSubAttributesSchemaList = subAttributeSchema.getSubAttributeSchemas();
            if (subSubAttributesSchemaList != null) {
                validatePatchOperationEffectForRequiredSubAttributes(oldSubAttribute, newSubAttribute,
                        subAttributeSchema, scimObject);
            }
        }
    }

    private static void validateCanonicalAttributesInScimObject(AbstractAttribute attribute,
                                                                AttributeSchema subAttributeSchema,
                                                                AbstractSCIMObject scimObject) throws
            CharonException, BadRequestException {

        List<String> canonicalValues = subAttributeSchema.getCanonicalValues();
        if (!(scimObject instanceof Group) || canonicalValues == null) {
            return;
        }
        if (attribute instanceof MultiValuedAttribute) {
            List<Attribute> values =
                    ((MultiValuedAttribute) attribute).getAttributeValues();
            for (Attribute value : values) {
                if (value instanceof ComplexAttribute) {
                    SimpleAttribute subAttribute = (SimpleAttribute) value.getSubAttribute
                            (subAttributeSchema.getName());
                    if (subAttribute != null && !canonicalValues.contains(subAttribute.getValue())) {
                        String error = "Unsupported member type: " + subAttribute.getValue();
                        throw new BadRequestException(error, ResponseCodeConstants.INVALID_VALUE);
                    }
                }
            }
        }
    }

    /*
     * Validate SCIMObject for schema list
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSchemaList(AbstractSCIMObject scimObject,
                                          SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //get resource schema list
        List<String> resourceSchemaList = resourceSchema.getSchemasList();
        //get the scim object schema list
        List<String> objectSchemaList = scimObject.getSchemaList();

        for (String schema : resourceSchemaList) {
            //check for schema.
            if (!objectSchemaList.contains(schema)) {
                throw new CharonException("Not all schemas are set");
            }
        }
    }

    /*
     * Check for readonlyAttributes and remove them if they have been modified. - (create method)
     *
     * @param scimObject
     * @param resourceSchema
     * @throws CharonException
     */
    public static void removeAnyReadOnlyAttributes(AbstractSCIMObject scimObject,
                                                   SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //No need to check for immutable as immutable attributes can be defined at resource creation

        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for read-only attributes.
            if (attributeSchema.getMutability() == SCIMDefinitions.Mutability.READ_ONLY) {
                if (attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    scimObject.deleteAttribute(attributeSchema.getName());
                }
            }
            //check for readonly sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            removeAnyReadOnlySubAttributes(attribute, attributeSchema);
        }
    }

    /*
     * Check for readonlySubAttributes and remove them if they have been modified. - (create method)
     *
     * @param attribute
     * @param attributeSchema
     * @throws CharonException
     */
    private static void removeAnyReadOnlySubAttributes(Attribute attribute,
                                                       AttributeSchema attributeSchema) throws CharonException {
        if (attribute != null) {
            List<AttributeSchema> subAttributesSchemaList = attributeSchema.getSubAttributeSchemas();
            if (subAttributesSchemaList != null && !subAttributesSchemaList.isEmpty()) {
                for (AttributeSchema subAttributeSchema : subAttributesSchemaList) {
                    if (subAttributeSchema.getMutability() == SCIMDefinitions.Mutability.READ_ONLY) {
                        if (attribute instanceof ComplexAttribute) {
                            if ((attribute.getSubAttribute(subAttributeSchema.getName()) != null) &&
                                    StringUtils.equals(attribute.getSubAttribute(subAttributeSchema.getName()).getURI(),
                                            subAttributeSchema.getURI())) {
                                String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                        + " is set in the SCIM Attribute: " + attribute.getName() +
                                        ". Removing it.";
                                logger.debug(error);
                                ((ComplexAttribute) attribute).removeSubAttribute(subAttributeSchema.getName());
                            }
                        } else if (attribute instanceof MultiValuedAttribute) {
                            List<Attribute> values =
                                    ((MultiValuedAttribute) attribute).getAttributeValues();
                            for (Attribute value : values) {
                                if (value instanceof ComplexAttribute) {
                                    if (value.getSubAttribute(subAttributeSchema.getName()) != null) {
                                        String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                + " is set in the SCIM Attribute: " + attribute.getName() +
                                                ". Removing it.";
                                        logger.debug(error);
                                        ((ComplexAttribute) value).removeSubAttribute(subAttributeSchema.getName());

                                    }
                                }
                            }
                        }
                    }
                    //A this point only extension schema can have this situation.
                    //Otherwise no complex attribute can complex sub attributes.
                    if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        //check for readonly sub-sub attributes in extension.
                        //get attributes from schema.
                        Map<String, Attribute> subAttributeList = ((ComplexAttribute) attribute).getSubAttributesList();
                        for (Attribute subSubAttribute : subAttributeList.values()) {
                            removeAnyReadOnlySubAttributes(subSubAttribute, subAttributeSchema);
                        }
                    }

                }
            }
        }
    }

    /*
     * This method is to remove any defined and requested attributes and include
     * requested attributes if not they have been removed.
     *
     * @param scimObject
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     */
    public static void validateReturnedAttributes(AbstractSCIMObject scimObject, String requestedAttributes,
                                                  String requestedExcludingAttributes) throws CharonException {
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
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        ArrayList<Attribute> attributeTemporyList = new ArrayList<Attribute>();
        for (Attribute attribute : attributeList.values()) {
            attributeTemporyList.add(attribute);
        }
        for (Attribute attribute : attributeTemporyList) {
            //check for never/request attributes.
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                scimObject.deleteAttribute(attribute.getName());
            }
            //if the returned property is request, need to check whether is it specifically requested by the user.
            // If so return it.
            if (requestedAttributes == null && requestedExcludingAttributes == null) {
                if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteAttribute(attribute.getName());
                }
            } else {
                //A request should only contains either attributes or exclude attribute params. Not both
                if (requestedAttributes != null) {
                    //if attributes are set, delete all the request and default attributes
                    //and add only the requested attributes
                    if ((attribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                            || attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                            && (requestedAttributesList.stream().noneMatch(attribute.getName()::equalsIgnoreCase)
                            && !isSubAttributeExistsInList(requestedAttributesList, attribute))) {
                        scimObject.deleteAttribute(attribute.getName());
                    }
                } else if (requestedExcludingAttributes != null) {
                    //removing attributes which has returned as request. This is because no request is made
                    if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                        scimObject.deleteAttribute(attribute.getName());
                    }
                    //if exclude attribute is set, set of exclude attributes need to be
                    // removed from the default set of attributes
                    if ((attribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                            && requestedExcludingAttributesList.
                            stream().anyMatch(attribute.getName()::equalsIgnoreCase)) {
                        scimObject.deleteAttribute(attribute.getName());
                    }
                }
            }
            // If the Returned type ALWAYS : no need to check and it will be not affected by
            // requestedExcludingAttributes parameter

            //check the same for sub attributes
            if (attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                if (attribute.getMultiValued()) {
                    List<Attribute> valuesList = ((MultiValuedAttribute) attribute).getAttributeValues();

                    for (Attribute subAttribute : valuesList) {
                        Map<String, Attribute> valuesSubAttributeList = ((ComplexAttribute) subAttribute)
                                .getSubAttributesList();
                        ArrayList<Attribute> valuesSubAttributeTemporyList = new ArrayList<Attribute>();
                        //as we are deleting the attributes form the list, list size will change,
                        //hence need to traverse on a copy
                        for (Attribute subSimpleAttribute : valuesSubAttributeList.values()) {
                            valuesSubAttributeTemporyList.add(subSimpleAttribute);
                        }
                        for (Attribute subSimpleAttribute : valuesSubAttributeTemporyList) {
                            removeValuesSubAttributeOnReturn(subSimpleAttribute, subAttribute, attribute,
                                    requestedAttributes, requestedExcludingAttributes, requestedAttributesList,
                                    requestedExcludingAttributesList, scimObject);
                        }
                    }
                } else {
                    Map<String, Attribute> subAttributeList = ((ComplexAttribute) attribute).getSubAttributesList();
                    ArrayList<Attribute> subAttributeTemporyList = new ArrayList<Attribute>();
                    for (Attribute subAttribute : subAttributeList.values()) {
                        subAttributeTemporyList.add(subAttribute);
                    }
                    for (Attribute subAttribute : subAttributeTemporyList) {
                        if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this applicable for extension schema only
                            if (subAttribute.getMultiValued()) {

                                List<Attribute> valuesList = ((MultiValuedAttribute) subAttribute).getAttributeValues();

                                for (Attribute subSubValue : valuesList) {
                                    Map<String, Attribute> subValuesSubAttributeList = ((ComplexAttribute)
                                            subSubValue).getSubAttributesList();
                                    ArrayList<Attribute> valuesSubSubAttributeTemporyList = new ArrayList<Attribute>();
                                    //as we are deleting the attributes form the list, list size will change,
                                    //hence need to traverse on a copy
                                    for (Attribute subSubSimpleAttribute : subValuesSubAttributeList.values()) {
                                        valuesSubSubAttributeTemporyList.add(subSubSimpleAttribute);
                                    }
                                    for (Attribute subSubSimpleAttribute : valuesSubSubAttributeTemporyList) {
                                        removeValuesSubSubAttributeOnReturn(attribute, subAttribute, subSubValue,
                                                subSubSimpleAttribute,
                                                requestedAttributes, requestedExcludingAttributes,
                                                requestedAttributesList, requestedExcludingAttributesList, scimObject);
                                    }
                                }
                            } else {
                                ArrayList<Attribute> subSubAttributeTemporyList = new ArrayList<Attribute>();
                                Map<String, Attribute> subSubAttributeList = ((ComplexAttribute) subAttribute)
                                        .getSubAttributesList();
                                for (Attribute subSubAttribute : subSubAttributeList.values()) {
                                    subSubAttributeTemporyList.add(subSubAttribute);
                                }
                                for (Attribute subSubAttribute : subSubAttributeTemporyList) {
                                    removeSubSubAttributesOnReturn(attribute, subAttribute, subSubAttribute,
                                            requestedAttributes, requestedExcludingAttributes,
                                            requestedAttributesList, requestedExcludingAttributesList, scimObject);
                                }
                            }
                            removeSubAttributesOnReturn(subAttribute, attribute, requestedAttributes,
                                    requestedExcludingAttributes,
                                    requestedAttributesList, requestedExcludingAttributesList, scimObject);
                        } else {
                            removeSubAttributesOnReturn(subAttribute, attribute, requestedAttributes,
                                    requestedExcludingAttributes,
                                    requestedAttributesList, requestedExcludingAttributesList, scimObject);
                        }
                    }
                }
            }
        }
    }

    /*
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from complex attributes.
     *
     * @param subAttribute
     * @param attribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     */

    private static void removeSubAttributesOnReturn(Attribute subAttribute, Attribute attribute, String
            requestedAttributes,
                                                    String requestedExcludingAttributes, List<String>
                                                            requestedAttributesList,
                                                    List<String> requestedExcludingAttributesList, AbstractSCIMObject
                                                            scimObject) {
        //check for never/request attributes.
        if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
            scimObject.deleteSubAttribute(attribute.getName(), subAttribute.getName());
        }
        //if the returned property is request, need to check whether is it specifically requested by the user.
        // If so return it.
        if (requestedAttributes == null && requestedExcludingAttributes == null) {
            if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                scimObject.deleteSubAttribute(attribute.getName(), subAttribute.getName());
            }
        } else {
            //A request should only contains either attributes or exclude attribute params. Not the both
            if (requestedAttributes != null) {
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (requestedAttributesList.stream().noneMatch((attribute.getName() + "."
                        + subAttribute.getName())::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch(attribute.getName()::equalsIgnoreCase) &&
                        !isSubSubAttributeExistsInList(requestedAttributesList, attribute, subAttribute))) {
                    scimObject.deleteSubAttribute(attribute.getName(), subAttribute.getName());
                }
            } else if (requestedExcludingAttributes != null) {
                //removing attributes which has returned as request. This is because no request is made
                if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteSubAttribute(attribute.getName(), subAttribute.getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.stream().anyMatch((attribute.getName() + "."
                        + subAttribute.getName())::equalsIgnoreCase)) {
                    scimObject.deleteSubAttribute(attribute.getName(), subAttribute.getName());
                }
            }
        }
    }

    /*
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from complex attributes.
     * <p>
     * This is applicable for extension schema only
     *
     * @param attribute
     * @param subAttribute
     * @param subSubAttribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     * @throws CharonException
     */
    private static void removeSubSubAttributesOnReturn(Attribute attribute, Attribute subAttribute, Attribute
            subSubAttribute, String requestedAttributes,
                                                       String requestedExcludingAttributes, List<String>
                                                               requestedAttributesList,
                                                       List<String> requestedExcludingAttributesList,
                                                       AbstractSCIMObject scimObject) throws CharonException {
        //check for never/request attributes.
        if (subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
            scimObject.deleteSubSubAttribute(subSubAttribute.getName(), subAttribute.getName(), attribute.getName());
        }
        //if the returned property is request, need to check whether is it specifically requested by the user.
        // If so return it.
        if (requestedAttributes == null && requestedExcludingAttributes == null) {
            if (subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                scimObject.deleteSubSubAttribute(subSubAttribute.getName(), subAttribute.getName(), attribute.getName
                        ());
            }
        } else {
            //A request should only contains either attributes or exclude attribute params. Not the both
            if (requestedAttributes != null) {
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (requestedAttributesList.stream().noneMatch((attribute.getName() + "."
                        + subAttribute.getName() + "." + subSubAttribute.getName())::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch(attribute.getName()::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch((attribute.getName() + "." + subAttribute.getName())
                                ::equalsIgnoreCase) &&
                        !subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.ALWAYS))) {
                    scimObject.deleteSubSubAttribute(subSubAttribute.getName(), subAttribute.getName(), attribute
                            .getName());
                }
            } else if (requestedExcludingAttributes != null) {
                //removing attributes which has returned as request. This is because no request is made
                if (subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteSubSubAttribute(subSubAttribute.getName(), subAttribute.getName(), attribute
                            .getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subSubAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.stream().anyMatch((attribute.getName() + "."
                        + subAttribute.getName() + "." + subSubAttribute.getName())::equalsIgnoreCase)) {
                    scimObject.deleteSubSubAttribute(subSubAttribute.getName(), subAttribute.getName(), attribute
                            .getName());
                }
            }
        }
    }


    /*
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from multivalued attributes
     *
     * @param subSimpleAttribute
     * @param subAttribute
     * @param attribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     */
    private static void removeValuesSubAttributeOnReturn(Attribute subSimpleAttribute, Attribute subAttribute,
                                                         Attribute attribute, String requestedAttributes,
                                                         String requestedExcludingAttributes,
                                                         List<String> requestedAttributesList,
                                                         List<String> requestedExcludingAttributesList,
                                                         AbstractSCIMObject scimObject) {
        if (subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
            scimObject.deleteValuesSubAttribute(attribute.getName(),
                    subAttribute.getName(), subSimpleAttribute.getName());
        }
        if (requestedAttributes == null && requestedExcludingAttributes == null) {
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                scimObject.deleteValuesSubAttribute(attribute.getName(),
                        subAttribute.getName(), subSimpleAttribute.getName());
            }
        } else {
            //A request should only contains either attributes or exclude attribute params. Not the both
            if (requestedAttributes != null) {
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (requestedAttributesList.stream().noneMatch((attribute.getName() + "."
                        + subSimpleAttribute.getName())::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch(attribute.getName()::equalsIgnoreCase) &&
                        !isSubSubAttributeExistsInList(requestedAttributesList, attribute, subSimpleAttribute))) {
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subSimpleAttribute.getName());
                }
            } else if (requestedExcludingAttributes != null) {
                //removing attributes which has returned as request. This is because no request is made
                if (subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subSimpleAttribute.getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.stream().anyMatch((attribute.getName() + "."
                        + subSimpleAttribute.getName())::equalsIgnoreCase)) {
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subSimpleAttribute.getName());
                }
            }
        }


    }

    /*
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from multivalued attributes
     * <p>
     * This is only applicable for extension schema
     *
     * @param attribute
     * @param subAttribute
     * @param subValue
     * @param subSimpleAttribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     */
    private static void removeValuesSubSubAttributeOnReturn(Attribute attribute, Attribute subAttribute, Attribute
            subValue,
                                                            Attribute subSimpleAttribute,
                                                            String requestedAttributes, String
                                                                    requestedExcludingAttributes,
                                                            List<String> requestedAttributesList,
                                                            List<String> requestedExcludingAttributesList,
                                                            AbstractSCIMObject scimObject) {


        if (subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
            scimObject.deleteSubValuesSubAttribute(attribute.getName(),
                    subAttribute.getName(), subValue.getName(), subSimpleAttribute.getName());
        }
        if (requestedAttributes == null && requestedExcludingAttributes == null) {
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                scimObject.deleteSubValuesSubAttribute(attribute.getName(),
                        subAttribute.getName(), subValue.getName(), subSimpleAttribute.getName());
            }
        } else {
            //A request should only contains either attributes or exclude attribute params. Not the both
            if (requestedAttributes != null) {
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (requestedAttributesList.stream().noneMatch((attribute.getName() + "."
                        + subAttribute.getName() + "." + subSimpleAttribute.getName())::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch(attribute.getName()::equalsIgnoreCase) &&
                        requestedAttributesList.stream().noneMatch((attribute.getName() + "."
                                + subAttribute.getName())::equalsIgnoreCase))) {
                    scimObject.deleteSubValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subValue.getName(), subSimpleAttribute.getName());
                }
            } else if (requestedExcludingAttributes != null) {
                //removing attributes which has returned as request. This is because no request is made
                if (subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteSubValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subValue.getName(), subSimpleAttribute.getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.stream().anyMatch((attribute.getName() + "."
                        + subAttribute.getName() + "." + subSimpleAttribute.getName())::equalsIgnoreCase)) {
                    scimObject.deleteSubValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subValue.getName(), subSimpleAttribute.getName());
                }
            }
        }
    }

    /*
     * This checks whether, within the 'requestedAttributes', is there a sub attribute of the 'attribute'.
     * If so we should not delete the 'attribute'
     *
     * @param requestedAttributes
     * @param attribute
     * @return boolean
     */
    private static boolean isSubAttributeExistsInList(List<String> requestedAttributes, Attribute attribute) {
        List<Attribute> subAttributes = null;
        if (attribute instanceof MultiValuedAttribute) {
            subAttributes =
                    (List<Attribute>) ((MultiValuedAttribute) attribute).getAttributeValues();
            if (subAttributes != null) {
                for (Attribute subAttribute : subAttributes) {
                    ArrayList<Attribute> subSimpleAttributes = new ArrayList<Attribute>((
                            (ComplexAttribute) subAttribute).getSubAttributesList().values());
                    for (Attribute subSimpleAttribute : subSimpleAttributes) {
                        if (requestedAttributes.stream().anyMatch((attribute.getName() + "."
                                + subSimpleAttribute.getName())::equalsIgnoreCase)) {
                            return true;
                        }
                    }
                    //this case is only valid for extension schema
                    if (SCIMDefinitions.DataType.COMPLEX.equals(subAttribute.getType())) {
                        boolean isSubSubAttributeExists = isSubSubAttributeExistsInList(requestedAttributes,
                                attribute, subAttribute);
                        if (isSubSubAttributeExists) {
                            return true;
                        }
                    }
                }
            }
        } else if (attribute instanceof ComplexAttribute) {
            //complex attributes have sub attribute map, hence need conversion to arraylist
            subAttributes = new ArrayList<Attribute>
                    (((Map) (((ComplexAttribute) attribute).getSubAttributesList())).values());
                for (Attribute subAttribute : subAttributes) {
                    if (requestedAttributes.stream().anyMatch((attribute.getName() + "."
                            + subAttribute.getName())::equalsIgnoreCase)) {
                        return true;
                    }
                    //this case is only valid for extension schema
                    if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        boolean isSubSubAttributeExists = isSubSubAttributeExistsInList(requestedAttributes,
                                attribute, subAttribute);
                        if (isSubSubAttributeExists) {
                            return true;
                        }
                    }
                }
        }
        return false;
    }

    /*
     * This checks whether, within the 'requestedAttributes', is there a sub attribute of the 'subAttribute'.
     * If so we should not delete the 'attribute'
     * This case is only applicable for extension
     *
     * @param requestedAttributes
     * @param grandParentAttribute
     * @param parentAttribute
     * @return
     */
    private static boolean isSubSubAttributeExistsInList(List<String> requestedAttributes,
                                                         Attribute grandParentAttribute, Attribute parentAttribute) {
        List<Attribute> subAttributes = null;
        if (parentAttribute instanceof MultiValuedAttribute) {
            subAttributes = (List<Attribute>)
                    ((MultiValuedAttribute) parentAttribute).getAttributeValues();
            if (subAttributes != null) {
                for (Attribute subAttribute : subAttributes) {
                    ArrayList<Attribute> subSimpleAttributes = new ArrayList<Attribute>((
                            (ComplexAttribute) subAttribute).getSubAttributesList().values());
                    for (Attribute subSimpleAttribute : subSimpleAttributes) {
                        if (requestedAttributes.stream().anyMatch((grandParentAttribute.getName() + "."
                                + parentAttribute.getName() + "." + subSimpleAttribute.getName())::equalsIgnoreCase)) {
                            return true;
                        }
                    }
                }
            }
        } else if (parentAttribute instanceof ComplexAttribute) {
            //complex attributes have sub attribute map, hence need conversion to arraylist
            subAttributes = new ArrayList<Attribute>
                    (((Map) (((ComplexAttribute) parentAttribute).getSubAttributesList())).values());
            for (Attribute subAttribute : subAttributes) {
                if (requestedAttributes.stream().anyMatch((grandParentAttribute.getName() + "."
                        + parentAttribute.getName() + "." + subAttribute.getName())::equalsIgnoreCase)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * check for read only and immutable attributes which has been modified on update request
     *
     * @param oldObject
     * @param newObject
     * @param resourceSchema
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    protected static AbstractSCIMObject checkIfReadOnlyAndImmutableAttributesModified(
            AbstractSCIMObject oldObject, AbstractSCIMObject newObject, SCIMResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {

        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from old scim object.
        Map<String, Attribute> oldAttributeList = oldObject.getAttributeList();
        //get attribute list from new scim object.
        Map<String, Attribute> newAttributeList = newObject.getAttributeList();

        for (AttributeSchema attributeSchema : attributeSchemaList) {
            if (attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " +
                            "Removing it and updating from previous value.";
                    logger.debug(error);
                    newObject.deleteAttribute(attributeSchema.getName());
                    newObject.setAttribute((Attribute) (CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema
                            .getName()))));
                } else if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        !oldAttributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    newObject.deleteAttribute(attributeSchema.getName());
                } else if (!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    newObject.setAttribute((Attribute) (CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema
                            .getName()))));
                }
            } else if (attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    checkForSameValues(oldAttributeList, newAttributeList, attributeSchema);

                } else if (!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    newObject.setAttribute((Attribute) (CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema
                            .getName()))));
                }
            }
            checkIfReadOnlyAndImmutableSubAttributesModified(newAttributeList, oldAttributeList, attributeSchema);
        }
        return newObject;
    }

    /*
     * check for read only and immutable sub attributes which has been modified on update request
     *
     * @param newAttributeList
     * @param oldAttributeList
     * @param attributeSchema
     * @throws BadRequestException
     * @throws CharonException
     */
    private static void checkIfReadOnlyAndImmutableSubAttributesModified(Map<String, Attribute> newAttributeList,
                                                                         Map<String, Attribute> oldAttributeList,
                                                                         AttributeSchema attributeSchema)
            throws BadRequestException, CharonException {

        //check for sub attributes.
        AbstractAttribute newAttribute = (AbstractAttribute) newAttributeList.get(attributeSchema.getName());
        AbstractAttribute oldAttribute = (AbstractAttribute) oldAttributeList.get(attributeSchema.getName());
        List<AttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();

        if (subAttributeSchemaList != null) {
            String attributeName = attributeSchema.getName();
            SCIMResourceSchemaManager schemaManager = SCIMResourceSchemaManager.getInstance();

            if (attributeName != null) {
                String extensionName = schemaManager.getExtensionName();
                String systemSchemaExtensionName = schemaManager.getSystemSchemaExtensionName();
                String agentSchemaExtensionName = schemaManager.getAgentSchemaExtensionName();
                if (attributeName.equals(extensionName) || attributeName.equals(systemSchemaExtensionName) || 
                    attributeName.equals(agentSchemaExtensionName)) {
                    checkIfReadOnlyAndImmutableExtensionAttributesModified(subAttributeSchemaList, newAttribute,
                            oldAttribute);
                }
            }
            if (newAttribute != null && oldAttribute != null) {
                if (attributeSchema.getMultiValued()) {
                    //this is complex multivalued case
                    List<Attribute> newSubValuesList = ((MultiValuedAttribute) newAttribute).getAttributeValues();
                    List<Attribute> oldSubValuesList = ((MultiValuedAttribute) oldAttribute).getAttributeValues();
                    //if size aren't equal, they do not preserver immutable quality
                    if (newSubValuesList.size() != oldSubValuesList.size() &&
                            attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                        throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
                    }
                    //no need to check sub attributes of sub values separately for equality, stop at the sub value level
                    for (Attribute subValue : newSubValuesList) {
                        if (!isListContains((((ComplexAttribute) subValue).getName()), oldSubValuesList) &&
                                attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                            throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
                        }
                    }
                } else {
                    //A complex attribute itself can not be immutable if it's sub variables are not immutable
                    checkForReadOnlyAndImmutableInComplexAttributes(newAttribute, oldAttribute, subAttributeSchemaList);
                }
            } else if (newAttribute == null && oldAttribute != null) {
                if (attributeSchema.getMultiValued()) {
                    List<Attribute> oldSubValuesList = ((MultiValuedAttribute) oldAttribute).getAttributeValues();
                    Attribute clonedMultiValuedAttribute = (Attribute) CopyUtil.deepCopy(oldAttribute);
                    clonedMultiValuedAttribute.deleteSubAttributes();

                    for (Attribute subValue : oldSubValuesList) {
                        Attribute clonedSubValue = (Attribute) CopyUtil.deepCopy(subValue);
                        clonedSubValue.deleteSubAttributes();

                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)
                                    || subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability
                                    .IMMUTABLE)) {
                                if (((ComplexAttribute) subValue).isSubAttributeExist(subAttributeSchema.getName())) {
                                    Attribute clonedSubValuesAttribute = (Attribute) CopyUtil.deepCopy(
                                            ((ComplexAttribute) subValue).getSubAttribute(subAttributeSchema.getName
                                                    ()));
                                    ((ComplexAttribute) clonedSubValue).setSubAttribute(clonedSubValuesAttribute);
                                }
                            }
                        }
                        ((MultiValuedAttribute) (clonedMultiValuedAttribute)).setAttributeValue(clonedSubValue);
                    }
                } else {
                    Map<String, Attribute> oldSubAttributeList = ((ComplexAttribute) (oldAttribute))
                            .getSubAttributesList();
                    Attribute clonedAttribute = (Attribute) CopyUtil.deepCopy(oldAttribute);
                    clonedAttribute.deleteSubAttributes();
                    for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {

                        if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)
                                || subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                            if (oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                                ((ComplexAttribute) (clonedAttribute)).setSubAttribute(
                                        (Attribute) CopyUtil.deepCopy(oldSubAttributeList.get(subAttributeSchema
                                                .getName())));
                            }
                        }
                    }
                    newAttributeList.put(clonedAttribute.getName(), clonedAttribute);
                }
            } else if (newAttribute != null && oldAttribute == null) {
                if (attributeSchema.getMultiValued()) {
                    if (attributeSchema.getMultiValued()) {
                        List<Attribute> newSubValuesList = ((MultiValuedAttribute) newAttribute).getAttributeValues();

                        for (Attribute subValue : newSubValuesList) {
                            for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                                if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                                    ((ComplexAttribute) (subValue)).removeSubAttribute(subAttributeSchema.getName());
                                }
                            }
                        }
                    }
                } else {
                    //this is complex attribute case
                    Map<String, Attribute> newSubAttributeList = ((ComplexAttribute) (newAttribute))
                            .getSubAttributesList();

                    for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {

                        if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                            if (newSubAttributeList.containsKey(subAttributeSchema.getName())) {
                                String error = "Read only attribute: " + subAttributeSchema.getName() +
                                        " is set from consumer in the SCIM Object. Removing it.";
                                logger.debug(error);
                                ((ComplexAttribute) newAttribute).removeSubAttribute(subAttributeSchema.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * check for read only and immutable attributes in extension schema which has been modified on update request
     *
     * @param subAttributeSchemaList
     * @param newAttribute
     * @param oldAttribute
     * @throws CharonException
     * @throws BadRequestException
     */
    private static void checkIfReadOnlyAndImmutableExtensionAttributesModified(
            List<AttributeSchema> subAttributeSchemaList, AbstractAttribute newAttribute,
            AbstractAttribute oldAttribute) throws CharonException, BadRequestException {

        Map<String, Attribute> newAttributeList = new HashMap<String, Attribute>();
        Map<String, Attribute> oldAttributeList = new HashMap<String, Attribute>();

        if (newAttribute != null) {
            newAttributeList = ((ComplexAttribute) newAttribute).getSubAttributesList();
        }
        if (oldAttribute != null) {
            oldAttributeList = ((ComplexAttribute) oldAttribute).getSubAttributesList();
        }

        for (AttributeSchema attributeSchema : subAttributeSchemaList) {
            if (attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " +
                            "Removing it and updating from previous value.";
                    logger.debug(error);
                    ((ComplexAttribute) newAttribute).removeSubAttribute(attributeSchema.getName());
                    ((ComplexAttribute) newAttribute).setSubAttribute(
                            (Attribute) (CopyUtil.deepCopy((
                                    (ComplexAttribute) oldAttribute).getSubAttribute(attributeSchema.getName()))));
                } else if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        !oldAttributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    ((ComplexAttribute) newAttribute).removeSubAttribute(attributeSchema.getName());
                } else if (!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    if (newAttribute != null) {
                        ((ComplexAttribute) newAttribute).setSubAttribute(
                                (Attribute) (CopyUtil.deepCopy((
                                        (ComplexAttribute) oldAttribute).getSubAttribute(attributeSchema.getName()))));
                    }
                }
            } else if (attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                if (newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    checkForSameValues(oldAttributeList, newAttributeList, attributeSchema);

                } else if (!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())) {
                    if (newAttribute != null) {
                        ((ComplexAttribute) newAttribute).setSubAttribute(
                                (Attribute) (CopyUtil.deepCopy((
                                        (ComplexAttribute) oldAttribute).getSubAttribute(attributeSchema.getName()))));
                    }
                }
            }
            if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                checkIfReadOnlyAndImmutableSubAttributesModified(newAttributeList, oldAttributeList, attributeSchema);
            }
        }
    }

    /*
     * check whether the give attribute is in the given list
     *
     * @param attributeName
     * @param list
     * @return
     */
    private static boolean isListContains(String attributeName, List<Attribute> list) {
        for (Attribute attribute : list) {
            if (attribute.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }


    /*
     * check for same values in a simple singular attributes or multivalued primitive type attributes
     *
     * @param oldAttributeList
     * @param newAttributeList
     * @param attributeSchema
     * @throws BadRequestException
     */
    private static void checkForSameValues(Map<String, Attribute> oldAttributeList, Map<String, Attribute>
            newAttributeList,
                                           AttributeSchema attributeSchema) throws BadRequestException {

        Attribute newTemporyAttribute = newAttributeList.get(attributeSchema.getName());
        Attribute oldTemporyAttribute = oldAttributeList.get(attributeSchema.getName());

        if (newTemporyAttribute instanceof SimpleAttribute) {
            if (!((((SimpleAttribute) newTemporyAttribute).getValue()).equals(((SimpleAttribute) oldTemporyAttribute)
                    .getValue()))) {
                throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
            }
        } else if (newTemporyAttribute instanceof MultiValuedAttribute &&
                !attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
            if (!checkListEquality(((MultiValuedAttribute) newTemporyAttribute).getAttributePrimitiveValues(),
                    ((MultiValuedAttribute) oldTemporyAttribute).getAttributePrimitiveValues())) {
                throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
            }

        }
    }

    /*
     * check whether the given two lists are equal from the content irrespective of the order
     *
     * @param l1
     * @param l2
     * @return
     */
    private static boolean checkListEquality(List<Object> l1, List<Object> l2) {
        final Set<Object> s1 = new HashSet(l1);
        final Set<Object> s2 = new HashSet(l2);

        return s1.equals(s2);
    }

    /*
     * check for read only and immutable attributes that has been modified in a complex type attribute
     *
     * @param newAttribute
     * @param oldAttribute
     * @param subAttributeSchemaList
     * @throws CharonException
     * @throws BadRequestException
     */
    private static void checkForReadOnlyAndImmutableInComplexAttributes(Attribute newAttribute, Attribute oldAttribute,
                                                                        List<AttributeSchema> subAttributeSchemaList
    ) throws CharonException, BadRequestException {
        //this is complex attribute case
        Map<String, Attribute> newSubAttributeList = ((ComplexAttribute) (newAttribute)).getSubAttributesList();
        Map<String, Attribute> oldSubAttributeList = ((ComplexAttribute) (oldAttribute)).getSubAttributesList();

        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {

            if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                if (newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                    String error = "Read only attribute: " + subAttributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " +
                            "Removing it and updating from previous value.";
                    logger.debug(error);
                    ((ComplexAttribute) newAttribute).removeSubAttribute(subAttributeSchema.getName());
                    ((ComplexAttribute) newAttribute).setSubAttribute((Attribute) (CopyUtil.deepCopy(
                            (((ComplexAttribute) oldAttribute).getSubAttribute(subAttributeSchema.getName())))));
                } else if (newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        !oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                    String error = "Read only attribute: " + subAttributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    ((ComplexAttribute) newAttribute).removeSubAttribute(subAttributeSchema.getName());
                } else if (!newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                    ((ComplexAttribute) newAttribute).setSubAttribute((Attribute) (CopyUtil.deepCopy(
                            ((ComplexAttribute) oldAttribute).getSubAttribute(subAttributeSchema.getName()))));
                }
            } else if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {

                if (newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                    checkForSameValues(newSubAttributeList, oldSubAttributeList, subAttributeSchema);

                } else if (!newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                    ((ComplexAttribute) newAttribute).setSubAttribute((Attribute) (CopyUtil.deepCopy(
                            ((ComplexAttribute) oldAttribute).getSubAttribute(subAttributeSchema.getName()))));
                }
            }

        }
    }

    /*
     * This method is basically for adding display sub attribute to multivalued attributes
     * which has 'display' as a sub attribute in the respective attribute schema
     *
     * @param scimObject
     * @param resourceSchema
     * @throws CharonException
     * @throws BadRequestException
     */
    protected static void setDisplayNameInComplexMultiValuedAttributes(
            AbstractSCIMObject scimObject, SCIMResourceTypeSchema resourceSchema) throws CharonException,
            BadRequestException {

        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        ArrayList<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();

        for (AttributeSchema attributeSchema : attributeSchemaList) {

            if (attributeSchema.getMultiValued() && attributeSchema.getType().equals(SCIMDefinitions.DataType
                    .COMPLEX)) {
                if (attributeSchema.getSubAttributeSchema(SCIMConstants.CommonSchemaConstants.DISPLAY) != null) {

                    if (attributeList.containsKey(attributeSchema.getName())) {
                        Attribute multiValuedAttribute = attributeList.get(attributeSchema.getName());
                        setDisplayNameInComplexMultiValuedSubAttributes(multiValuedAttribute, attributeSchema);
                    }
                }
            } else if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is only valid for extension schema
                List<AttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                    if (subAttributeSchema.getMultiValued() &&
                            subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                        if (subAttributeSchema.getSubAttributeSchema(SCIMConstants.CommonSchemaConstants.DISPLAY) !=
                                null) {
                            Attribute extensionAttribute = attributeList.get(attributeSchema.getName());
                            if (extensionAttribute != null) {
                                if ((((ComplexAttribute) extensionAttribute).
                                        getSubAttribute(subAttributeSchema.getName())) != null) {
                                    Attribute multiValuedAttribute = (attributeList.get(attributeSchema.getName()))
                                            .getSubAttribute(subAttributeSchema.getName());
                                    setDisplayNameInComplexMultiValuedSubAttributes(multiValuedAttribute,
                                            subAttributeSchema);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * set the displayname sub attribute in complex type multi valued attribute
     * eg. display name of emails
     *
     * @param multiValuedAttribute
     * @param attributeSchema
     * @throws CharonException
     * @throws BadRequestException
     */
    private static void setDisplayNameInComplexMultiValuedSubAttributes(Attribute multiValuedAttribute,
                                                                        AttributeSchema attributeSchema) throws
            CharonException,
            BadRequestException {
        List<Attribute> subValuesList = ((MultiValuedAttribute) (multiValuedAttribute)).getAttributeValues();

        for (Attribute subValue : subValuesList) {

            if (!((ComplexAttribute) subValue).getSubAttributesList().isEmpty()) {
                for (AttributeSchema subAttributeSchema : attributeSchema.getSubAttributeSchemas()) {
                    if (subAttributeSchema.getName().equals(SCIMConstants.CommonSchemaConstants.VALUE)) {

                        if (!subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)
                                && !subAttributeSchema.getMultiValued()) {
                            //take the value from the value sub attribute and put is as display attribute
                            SimpleAttribute simpleAttribute = null;
                            Object simpleAttributeValue = null;
                            Attribute subAttribute = subValue.getSubAttribute(subAttributeSchema.getName());
                            if (subAttribute != null) {
                                simpleAttributeValue = ((SimpleAttribute) subAttribute).getValue();
                            }
                            simpleAttribute = new SimpleAttribute(
                                    SCIMConstants.CommonSchemaConstants.DISPLAY, simpleAttributeValue);

                            AttributeSchema subSchema = attributeSchema.getSubAttributeSchema(SCIMConstants
                                    .CommonSchemaConstants.DISPLAY);
                            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(subSchema,
                                    simpleAttribute);
                            ((ComplexAttribute) (subValue)).setSubAttribute(simpleAttribute);
                        } else if (!subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)
                                && subAttributeSchema.getMultiValued()) {

                            Attribute valueSubAttribute = (MultiValuedAttribute) (subValue.getSubAttribute
                                    (subAttributeSchema.getName()));
                            Object displayValue = null;
                            try {
                                displayValue =
                                        ((MultiValuedAttribute) (valueSubAttribute)).getAttributePrimitiveValues()
                                                .get(0);
                            } catch (Exception e) {
                                String error = "Can not set display attribute value without a value attribute value.";
                                throw new BadRequestException(ResponseCodeConstants.INVALID_SYNTAX, error);
                            }
                            //if multiple values are available, get the first value and put it as display name
                            SimpleAttribute simpleAttribute = new SimpleAttribute(
                                    SCIMConstants.CommonSchemaConstants.DISPLAY, displayValue);
                            AttributeSchema subSchema = attributeSchema.getSubAttributeSchema(SCIMConstants
                                    .CommonSchemaConstants.DISPLAY);
                            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(subSchema,
                                    simpleAttribute);
                            ((ComplexAttribute) (subValue)).setSubAttribute(simpleAttribute);

                        }
                    }
                }
            }
        }

    }
}
