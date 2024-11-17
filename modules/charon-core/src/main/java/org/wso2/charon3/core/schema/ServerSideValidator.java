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
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Role;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.utils.AttributeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Server Side Validator.
 */
public class ServerSideValidator extends AbstractValidator {

    /*
     * Validate created SCIMObject according to the spec
     *
     * @param scimObject
     * @param resourceSchema
     * @throw CharonException
     * @throw BadRequestException
     * @throw NotFoundException
     */
    public static void validateCreatedSCIMObject(AbstractSCIMObject scimObject, SCIMResourceTypeSchema resourceSchema)
            throws CharonException, BadRequestException, NotFoundException {

        if (scimObject instanceof User) {
            //set display names for complex multivalued attributes
            setDisplayNameInComplexMultiValuedAttributes(scimObject, resourceSchema);
        }
        //remove any read only attributes
        removeAnyReadOnlyAttributes(scimObject, resourceSchema);

        if (!(scimObject instanceof Role)) {
            String id = UUID.randomUUID().toString();
            scimObject.setId(id);
            Instant now = Instant.now();
            // Set the created date and time.
            scimObject.setCreatedInstant(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(now)));
            // Creates date and the last modified are the same if not updated.
            scimObject.setLastModifiedInstant(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(now)));
        }
        //set location and resourceType
        if (resourceSchema.isSchemaAvailable(SCIMConstants.USER_CORE_SCHEMA_URI)) {
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
            scimObject.setResourceType(SCIMConstants.USER);
        } else if (resourceSchema.isSchemaAvailable(SCIMConstants.GROUP_CORE_SCHEMA_URI)) {
            String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                    SCIMConstants.GROUP_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
            scimObject.setResourceType(SCIMConstants.GROUP);
        } else if (resourceSchema.isSchemaAvailable(SCIMConstants.ROLE_SCHEMA_URI)) {
            scimObject.setResourceType(SCIMConstants.ROLE);
        }
        //check for required attributes
        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
    }

    /*
     * create location header from location and resourceID
     *
     * @param location
     * @param resourceID
     * @return
     */
    private static String createLocationHeader(String location, String resourceID) {
        String locationString = location + "/" + resourceID;
        return locationString;
    }

    /*
     * validate Retrieved SCIM Object in List
     *
     * @param scimObject
     * @param resourceSchema
     * @param reuqestedAttributes
     * @param requestedExcludingAttributes
     * @throws BadRequestException
     * @throws CharonException
     */
    public static void validateRetrievedSCIMObjectInList(AbstractSCIMObject scimObject,
                                                         SCIMResourceTypeSchema resourceSchema, String
                                                                 reuqestedAttributes,
                                                         String requestedExcludingAttributes)
            throws BadRequestException, CharonException {

        validateReturnedAttributes(scimObject, reuqestedAttributes, requestedExcludingAttributes);
    }

    /*
     * validate Retrieved SCIM Object
     *
     * @param scimObject
     * @param resourceSchema
     * @param reuqestedAttributes
     * @param requestedExcludingAttributes
     * @throws BadRequestException
     * @throws CharonException
     */
    public static void validateRetrievedSCIMObject(AbstractSCIMObject scimObject,
                                                   SCIMResourceTypeSchema resourceSchema, String reuqestedAttributes,
                                                   String requestedExcludingAttributes)
            throws BadRequestException, CharonException {

        validateReturnedAttributes(scimObject, reuqestedAttributes, requestedExcludingAttributes);
        validateSchemaList(scimObject, resourceSchema);
    }

    /**
     * Validate Retrieved SCIM Role Object.
     *
     * @param scimObject                   Role object.
     * @param requestedExcludingAttributes RequestedExcludingAttributes.
     */
    public static void validateRetrievedSCIMRoleObject(Role scimObject, String requestedAttributes,
            String requestedExcludingAttributes) {

        List<String> requestedExcludingAttributesList = null;
        List<String> requestedAttributesList = null;
        if (requestedExcludingAttributes != null) {
            // Make a list from the comma separated requestedExcludingAttributes.
            requestedExcludingAttributesList = Arrays.asList(requestedExcludingAttributes.split(","));
        }
        if (requestedAttributes != null) {
            // Make a list from the comma separated requestedAttributes.
            requestedAttributesList = Arrays.asList(requestedAttributes.split(","));
        }
        if (requestedAttributesList != null && requestedAttributesList.
                stream().noneMatch(SCIMConstants.RoleSchemaConstants.PERMISSIONS::equalsIgnoreCase)) {
            scimObject.setPermissions(new ArrayList<>());
        } else if (requestedExcludingAttributesList != null && requestedExcludingAttributesList.
                stream().anyMatch(SCIMConstants.RoleSchemaConstants.PERMISSIONS::equalsIgnoreCase)) {
            scimObject.setPermissions(new ArrayList<>());
        }
    }

    /**
     * Validate Retrieved SCIM Role V2 Object.
     *
     * @param scimObject                   RoleV2 object.
     * @param requestedAttributes          RequestedAttributes.
     * @param requestedExcludingAttributes RequestedExcludingAttributes.
     */
    public static void validateRetrievedSCIMRoleV2Object(RoleV2 scimObject, String requestedAttributes,
                                                         String requestedExcludingAttributes) {

        List<String> requestedExcludingAttributesList = null;
        List<String> requestedAttributesList = null;
        if (requestedExcludingAttributes != null) {
            // Make a list from the comma separated requestedExcludingAttributes.
            requestedExcludingAttributesList = Arrays.asList(requestedExcludingAttributes.split(","));
        }
        if (requestedAttributes != null) {
            // Make a list from the comma separated requestedAttributes.
            requestedAttributesList = Arrays.asList(requestedAttributes.split(","));
        }
        if (requestedAttributesList != null && requestedAttributesList.
                stream().noneMatch(SCIMConstants.RoleSchemaConstants.PERMISSIONS::equalsIgnoreCase)) {
            scimObject.setPermissions(new ArrayList<>());
        } else if (requestedExcludingAttributesList != null && requestedExcludingAttributesList.
                stream().anyMatch(SCIMConstants.RoleSchemaConstants.PERMISSIONS::equalsIgnoreCase)) {
            scimObject.setPermissions(new ArrayList<>());
        }
    }

    /**
     * Perform validation on SCIM Object update on service provider side.
     *
     * @param oldObject      Old scim object.
     * @param newObject      New scim object.
     * @param resourceSchema Schema of the scim resource.
     * @return Validated object.
     * @throws CharonException     When error occurred while validating the scim object.
     * @throws BadRequestException When error in the user's input.
     */
    public static AbstractSCIMObject validateUpdatedSCIMObject(AbstractSCIMObject oldObject,
                                                               AbstractSCIMObject newObject,
                                                               SCIMResourceTypeSchema resourceSchema)
            throws CharonException, BadRequestException {

        AbstractSCIMObject validatedObject = validateUpdatedSCIMObject(oldObject, newObject, resourceSchema, false);
        return validatedObject;
    }

    /**
     * Perform validation on SCIM Object update on service provider side.
     *
     * @param oldObject                 Old scim object used for comparison.
     * @param newObject                 Updated scim object.
     * @param resourceSchema            Schema for the scim resource.
     * @param validatePerPatchOperation Whether this validation is done per patch operation.
     * @return Validated scim object.
     * @throws CharonException     When error occurred while validating the scim object.
     * @throws BadRequestException When error in the user's input.
     */
    public static AbstractSCIMObject validateUpdatedSCIMObject(AbstractSCIMObject oldObject,
                                                               AbstractSCIMObject newObject,
                                                               SCIMResourceTypeSchema resourceSchema,
                                                               boolean validatePerPatchOperation)
            throws CharonException, BadRequestException {

        if (newObject instanceof User) {
            // Set display names for complex multivalued attributes.
            setDisplayNameInComplexMultiValuedAttributes(newObject, resourceSchema);
        }
        // Check for read only and immutable attributes.
        AbstractSCIMObject validatedObject =
                checkIfReadOnlyAndImmutableAttributesModified(oldObject, newObject, resourceSchema);
        // Copy meta attribute from old to new.
        validatedObject.setAttribute(oldObject.getAttribute(SCIMConstants.CommonSchemaConstants.META));
        // Copy id attribute to new group object.
        validatedObject.setAttribute(oldObject.getAttribute(SCIMConstants.CommonSchemaConstants.ID));
        // Edit last modified date.
        validatedObject.setLastModifiedInstant(Instant.now());
        // If this check done per scim patch operation, Only validate the update cause for required attribute removal.
        if (validatePerPatchOperation) {
            // Check for required attributes.
            validatePatchOperationEffectForRequiredAttributes(oldObject, newObject, resourceSchema);
        } else {
            // Check for required attributes.
            validateSCIMObjectForRequiredAttributes(newObject, resourceSchema);
        }

        Map<String, Attribute> attributes = validatedObject.getAttributeList();

        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Attribute value = entry.getValue();

            if (value instanceof SimpleAttribute && StringUtils.equals(key, SCIMConstants.UserSchemaConstants.LOCALE)) {
                String localeAttributeValue = ((SimpleAttribute) value).getValue().toString();

                if (!isValidLocale(localeAttributeValue)) {
                    throw new BadRequestException
                            ("Provided locale value " + localeAttributeValue + " is invalid");
                }
            }
        }

        // Check for schema list.
        validateSchemaList(validatedObject, resourceSchema);
        return validatedObject;
    }

    private static boolean isValidLocale(String localeStr) {

        if (localeStr == null || StringUtils.isEmpty(localeStr)) {
            return false;
        }

        // Split the locale string into parts (language and country)
        String[] parts = localeStr.split("-");

        if (parts.length != 2) {
            return false; // Must have exactly two parts: language and country
        }

        String language = parts[0];
        String country = parts[1];

        // Check if the locale is available in the system
        for (Locale availableLocale : Locale.getAvailableLocales()) {
            if (availableLocale.getLanguage().equals(language) &&
                    availableLocale.getCountry().equals(country)) {
                return true;
            }
        }

        return false; // If no matching locale is found
    }

    /*
     * This method is to add meta data to the resource type resource
     *
     * @param scimObject
     * @return
     * @throws NotFoundException
     * @throws BadRequestException
     * @throws CharonException
     */
    public static AbstractSCIMObject validateResourceTypeSCIMObject(AbstractSCIMObject scimObject)
            throws NotFoundException, BadRequestException, CharonException {

        String endpoint = (String) (((SimpleAttribute) (scimObject.getAttribute
                (SCIMConstants.ResourceTypeSchemaConstants.NAME))).getValue());
        String location = createLocationHeader(AbstractResourceManager.getResourceEndpointURL(
                SCIMConstants.RESOURCE_TYPE_ENDPOINT), endpoint);

        scimObject.setLocation(location);
        scimObject.setResourceType(SCIMConstants.RESOURCE_TYPE);
        return scimObject;
    }
}


