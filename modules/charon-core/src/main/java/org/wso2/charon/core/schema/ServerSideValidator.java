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
package org.wso2.charon.core.schema;

import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceEndpoint;
import org.wso2.charon.core.util.AttributeUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This is to perform SCIM service provider side validation and additions according to SCIM schema spec.
 */
public class ServerSideValidator extends AbstractValidator {

    private static Log logger = LogFactory.getLog(ServerSideValidator.class);

    /**
     * Add read-only attributes that only the service provider adds and validate the SCIM object
     * and attributes created by the client.
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateCreatedSCIMObject(AbstractSCIMObject scimObject,
                                                 SCIMResourceSchema resourceSchema)
            throws CharonException {
        //check if read-only attributes are set, if so put a debug level log and remove them.
        removeAnyReadOnlyAttributes(scimObject, resourceSchema);
        //add created and last modified dates
        String id = UUID.randomUUID().toString();
        scimObject.setId(id);
        Date date = new Date();
        scimObject.setCreatedDate(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //created n last modified are the same if not updated.
        scimObject.setLastModified(AttributeUtil.parseDateTime(AttributeUtil.formatDateTime(date)));
        //set location
        if (SCIMConstants.USER.equals(resourceSchema.getName())) {
            String location = createLocationHeader(AbstractResourceEndpoint.getResourceEndpointURL(
                    SCIMConstants.USER_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
        } else if (SCIMConstants.GROUP.equals(resourceSchema.getName())) {
            String location = createLocationHeader(AbstractResourceEndpoint.getResourceEndpointURL(
                    SCIMConstants.GROUP_ENDPOINT), scimObject.getId());
            scimObject.setLocation(location);
        }
        //TODO: add version, if user object - validate name

        //validate for required attributes.
        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
    }

    /**
     * Perform validation on SCIM Object update on service provider side.
     *
     * @param oldObject
     * @param newObject
     * @param resourceSchema
     * @return
     * @throws CharonException
     */
    public static AbstractSCIMObject validateUpdatedSCIMObject(AbstractSCIMObject oldObject,
                                                               AbstractSCIMObject newObject,
                                                               SCIMResourceSchema resourceSchema)
            throws CharonException {

        AbstractSCIMObject validatedObject = null;
        try {
            validatedObject = checkIfReadOnlyAttributesModified(oldObject, newObject, resourceSchema);
            //copy meta attribute from old to new
            validatedObject.setAttribute(oldObject.getAttribute(SCIMConstants.CommonSchemaConstants.META));
            //copy id attribute to new group object
            validatedObject.setAttribute(oldObject.getAttribute(SCIMConstants.CommonSchemaConstants.ID));
            //edit last modified date
            Date date = new Date();
            validatedObject.setLastModified(date);
            //check for required attributes.
            validateSCIMObjectForRequiredAttributes(validatedObject, resourceSchema);
        } catch (NotFoundException e) {
            logger.error("Meta attribute not found in the updating object.");
        }
        return validatedObject;
        //TODO: if user object, validate name
    }

    /**
     * Perform validation on the SCIM Object retrieved from service provider's UserManager before
     * returning it to client.
     *
     * @param scimObject
     * @param resourceSchema
     * @throws CharonException
     */
    public static void validateRetrievedSCIMObject(AbstractSCIMObject scimObject,
                                                   SCIMResourceSchema resourceSchema)
            throws CharonException {

        //if user object, remove password before returning.
        if (SCIMConstants.USER.equals(resourceSchema.getName())) {
            if (scimObject.getAttributeList().containsKey(SCIMConstants.UserSchemaConstants.PASSWORD)) {
                scimObject.deleteAttribute(SCIMConstants.UserSchemaConstants.PASSWORD);
            }
        }
        validateSCIMObjectForRequiredAttributes(scimObject, resourceSchema);
        validateSchemaList(scimObject, resourceSchema);
        //TODO:if user object, validate name - if validated in post and put, no need to validate in get.
    }

    /**
     * Log and ignore if any read only attributes are modified in the SCIM - update request.
     *
     * @param oldObject
     * @param newObject
     * @param resourceSchema
     * @return
     * @throws CharonException
     */
    public static AbstractSCIMObject checkIfReadOnlyAttributesModified(AbstractSCIMObject oldObject,
                                                                       AbstractSCIMObject newObject,
                                                                       SCIMResourceSchema resourceSchema)
            throws CharonException {
        //get attribute schema list
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from old object
        Map<String, Attribute> oldObjectAttributeList = oldObject.getAttributeList();
        Map<String, Attribute> newObjectAttributeList = newObject.getAttributeList();

        for (AttributeSchema attributeSchema : attributeSchemaList) {
            if (attributeSchema.getReadOnly()) {
                if ((!oldObjectAttributeList.containsKey(attributeSchema.getName())) &&
                    (newObjectAttributeList.containsKey(attributeSchema.getName()))) {
                    //log error
                    String error = "Read only attribute: " + attributeSchema.getName() + " is set. Removing it.";
                    newObjectAttributeList.remove(attributeSchema.getName());
                } else if ((oldObjectAttributeList.containsKey(attributeSchema.getName())) &&
                           (!newObjectAttributeList.containsKey(attributeSchema.getName()))) {
                    //log error
                    String error = "Existing read only attribute: " + attributeSchema.getName() + " is removed. Adding it.";
                    newObjectAttributeList.put(attributeSchema.getName(),
                                               oldObjectAttributeList.get(attributeSchema.getName()));
                } else if ((oldObjectAttributeList.containsKey(attributeSchema.getName())) &&
                           (newObjectAttributeList.containsKey(attributeSchema.getName()))) {
                    //log debug level
                    String error = "Replacing attribute: " + attributeSchema.getName() + " with the one from old attribute.";
                    newObjectAttributeList.remove(attributeSchema.getName());
                    newObjectAttributeList.put(attributeSchema.getName(),
                                               oldObjectAttributeList.get(attributeSchema.getName()));
                }
            } else {
                //check for sub attribute read only.
                //if only new has the attribute, remove its read only sub attr like in earlier case.
                if ((!oldObjectAttributeList.containsKey(attributeSchema.getName())) &&
                    (newObjectAttributeList.containsKey(attributeSchema.getName()))) {

                    AbstractAttribute attribute = (AbstractAttribute) newObjectAttributeList.get(attributeSchema.getName());

                    List<SCIMSubAttributeSchema> subAttributesSchemaList =
                            ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

                    if (subAttributesSchemaList != null && !subAttributesSchemaList.isEmpty()) {
                        for (SCIMSubAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                            if (subAttributeSchema.getReadOnly()) {
                                if (attribute instanceof ComplexAttribute) {
                                    if (attribute.getSubAttribute(subAttributeSchema.getName()) != null) {
                                        String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                       + " is set in the SCIM Attribute: " + attribute.getName() +
                                                       ". Removing it.";
                                        attribute.removeSubAttribute(subAttributeSchema.getName());
                                    }
                                } else if (attribute instanceof MultiValuedAttribute) {
                                    List<Attribute> values =
                                            ((MultiValuedAttribute) attribute).getValuesAsSubAttributes();
                                    for (Attribute value : values) {
                                        if (value instanceof ComplexAttribute) {
                                            if (value.getSubAttribute(subAttributeSchema.getName()) != null) {
                                                String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                               + " is set in the SCIM Attribute: " + attribute.getName() +
                                                               ". Removing it.";
                                                ((ComplexAttribute) value).removeSubAttribute(subAttributeSchema.getName());

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
                //if old has, new doesn't has, fine
                //if both has, check for modified.
            }
        }
        return newObject;
    }

    /**
     * In the process of validating SCIM objects being created in service provider side, we need to remove
     * any read-only attributes that are added by clients and remove them.
     *
     * @param scimObject
     * @param resourceSchema
     */
    private static void removeAnyReadOnlyAttributes(AbstractSCIMObject scimObject,
                                                    SCIMResourceSchema resourceSchema)
            throws CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for read-only attributes.
            if (attributeSchema.getReadOnly()) {
                if (attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                                   " is set from consumer in the SCIM Object. " + "Removing it.";
                    //TODO:put a debug level log and remove the attribute.
                    scimObject.deleteAttribute(attributeSchema.getName());
                }
            }
            //check for readonly sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMSubAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

                if (subAttributesSchemaList != null && !subAttributesSchemaList.isEmpty()) {
                    for (SCIMSubAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getReadOnly()) {
                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) != null) {
                                    String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                   + " is set in the SCIM Attribute: " + attribute.getName() +
                                                   ". Removing it.";
                                    attribute.removeSubAttribute(subAttributeSchema.getName());
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getValuesAsSubAttributes();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) != null) {
                                            String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                           + " is set in the SCIM Attribute: " + attribute.getName() +
                                                           ". Removing it.";
                                            ((ComplexAttribute) value).removeSubAttribute(subAttributeSchema.getName());

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    private static void validateName(User user, SCIMAttributeSchema nameAttributeSchema) {
        // Providers MAY return just the full name as a single string in the formatted sub-attribute,
        // or they MAY return just the individual component attributes using the other sub-attributes,
        // or they MAY return both. If both variants are returned, they SHOULD be describing the same name,
        // with the formatted name indicating how the component attributes should be combined.

    }

    private static String createLocationHeader(String location, String resourceID) {
        String locationString = location + "/" + resourceID;
        return locationString;
    }

    public static void removePasswordOnReturn(User scimUser) {
        if (scimUser.getAttributeList().containsKey(SCIMSchemaDefinitions.PASSWORD.getName())) {
            scimUser.deleteAttribute(SCIMSchemaDefinitions.PASSWORD.getName());
        }
    }

}
