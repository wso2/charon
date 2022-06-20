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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class contains the schema definitions in
 * https://tools.ietf.org/html/rfc7643 as AttributeSchemas.
 * These are used when constructing SCIMObjects from the decoded payload
 */

public class SCIMSchemaDefinitions {

    /*********** SCIM defined common attribute schemas****************************/

    /* the default set of sub-attributes for a multi-valued attribute */

    /* sub-attribute schemas of the attributes defined in SCIM common schema. */

    // sub attributes of the meta attributes

    //The name of the resource type of the resource.
    public static final SCIMAttributeSchema RESOURCE_TYPE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE_URI,
                    SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE,
                    SCIMDefinitions.DataType.STRING, false, SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE_DESC,
                    false, true,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    //The "DateTime" that the resource was added to the service provider.
    public static final SCIMAttributeSchema CREATED =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.CREATED_URI,
                    SCIMConstants.CommonSchemaConstants.CREATED,
                    SCIMDefinitions.DataType.DATE_TIME, false, SCIMConstants.CommonSchemaConstants.CREATED_DESC,
                    false, false,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    //The most recent DateTime that the details of this resource were updated at the service provider.
    public static final SCIMAttributeSchema LAST_MODIFIED =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.LAST_MODIFIED_URI,
                    SCIMConstants.CommonSchemaConstants.LAST_MODIFIED,
                    SCIMDefinitions.DataType.DATE_TIME, false, SCIMConstants.CommonSchemaConstants
                            .LAST_MODIFIED_DESC, false, false,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    //The uri of the resource being returned
    public static final SCIMAttributeSchema LOCATION =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.LOCATION_URI,
                    SCIMConstants.CommonSchemaConstants.LOCATION,
                    SCIMDefinitions.DataType.STRING, false, SCIMConstants.CommonSchemaConstants.LOCATION_DESC, false,
                    false,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    // The uri of the resource being returned.
    public static final SCIMAttributeSchema SYSTEM_ROLE = SCIMAttributeSchema
            .createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.SYSTEM_ROLE_URI,
                    SCIMConstants.CommonSchemaConstants.SYSTEM_ROLE, SCIMDefinitions.DataType.BOOLEAN, false,
                    SCIMConstants.CommonSchemaConstants.SYSTEM_ROLE_DESC, false, false,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    //The version of the resource being returned.
    //This value must be the same as the entity-tag (ETag) HTTP response header.
    public static final SCIMAttributeSchema VERSION =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.VERSION_URI,
                    SCIMConstants.CommonSchemaConstants.VERSION,
                    SCIMDefinitions.DataType.STRING, false, SCIMConstants.CommonSchemaConstants.VERSION_DESC, false,
                    true,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

            /*---------------------------------------------------------------------------------------------*/

    /* attribute schemas of the attributes defined in common schema. */

    //A unique identifier for a SCIM resource as defined by the service provider
    public static final SCIMAttributeSchema ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.ID_URI,
                    SCIMConstants.CommonSchemaConstants.ID, SCIMDefinitions.DataType.STRING, false,
                    SCIMConstants.CommonSchemaConstants.ID_DESC, false, true,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.ALWAYS,
                    SCIMDefinitions.Uniqueness.SERVER, null, null, null);

    //A String that is an identifier for the resource as defined by the provisioning client.
    //The service provider MUST always interpret the externalId as scoped to the provisioning domain.
    public static final SCIMAttributeSchema EXTERNAL_ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID_URI,
                    SCIMConstants.CommonSchemaConstants.EXTERNAL_ID,
                    SCIMDefinitions.DataType.STRING, false, SCIMConstants.CommonSchemaConstants.EXTERNAL_ID_DESC,
                    false, true,
                    SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null, null);

    //A complex attribute containing resource metadata.
    public static final SCIMAttributeSchema META =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.META_URI,
                    SCIMConstants.CommonSchemaConstants.META,
                    SCIMDefinitions.DataType.COMPLEX, false, SCIMConstants.CommonSchemaConstants.META_DESC, false,
                    false,
                    SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE, null, null,
                    new ArrayList<AttributeSchema>(Arrays.asList(RESOURCE_TYPE, CREATED, LAST_MODIFIED, LOCATION,
                            VERSION)));


    /**
     * SCIM User Schema Definition.
     */
    public static class SCIMUserSchemaDefinition {

        /*********** SCIM defined user attribute schemas****************************/

    /* sub-attribute schemas of the attributes defined in SCIM user schema. */

        //sub attributes of email attribute

        //"Email addresses for the user.
        public static final SCIMAttributeSchema EMAIL_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.EMAILS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.EMAIL_VALUE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.  READ-ONLY.
        public static final SCIMAttributeSchema EMAIL_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.EMAILS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.EMAIL_DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, e.g., 'work' or 'home'.
        public static final SCIMAttributeSchema EMAIL_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.EMAILS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.EMAIL_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.WORK,
                                SCIMConstants.UserSchemaConstants.HOME, SCIMConstants.UserSchemaConstants.OTHER)),
                        null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute
        public static final SCIMAttributeSchema EMAIL_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.EMAILS_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .EMAIL_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //sub attributes of phoneNumbers attribute

        //Phone number of the User.
        public static final SCIMAttributeSchema PHONE_NUMBERS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHONE_NUMBERS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_VALUE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema PHONE_NUMBERS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, e.g., 'work', 'home', 'mobile'.
        public static final SCIMAttributeSchema PHONE_NUMBERS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHONE_NUMBERS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_TYPE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.WORK,
                                SCIMConstants.UserSchemaConstants.HOME, SCIMConstants.UserSchemaConstants.OTHER,
                                SCIMConstants.UserSchemaConstants.FAX, SCIMConstants.UserSchemaConstants.MOBILE,
                                SCIMConstants.UserSchemaConstants.PAGER)), null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute
        public static final SCIMAttributeSchema PHONE_NUMBERS_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .PHONE_NUMBERS_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //sub attributes of ims attribute

        //Instant messaging address for the User.
        public static final SCIMAttributeSchema IMS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.IMS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.IMS_VALUE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema IMS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.IMS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.IMS_DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, e.g., 'aim', 'gtalk', 'xmpp'
        public static final SCIMAttributeSchema IMS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.IMS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.IMS_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.SKYPE,
                                SCIMConstants.UserSchemaConstants.YAHOO, SCIMConstants.UserSchemaConstants.GTALK,
                                SCIMConstants.UserSchemaConstants.AIM, SCIMConstants.UserSchemaConstants.ICQ,
                                SCIMConstants.UserSchemaConstants.XMPP, SCIMConstants.UserSchemaConstants.MSN,
                                SCIMConstants.UserSchemaConstants.QQ)), null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute
        public static final SCIMAttributeSchema IMS_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.IMS_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants.IMS_PRIMARY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //sub attributes of photos attribute

        //URL of a photo of the User.
        public static final SCIMAttributeSchema PHOTOS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHOTOS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants
                                .PHOTOS_VALUE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType
                                .EXTERNAL)), null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema PHOTOS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHOTOS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .PHOTOS_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, i.e., 'photo' or 'thumbnail'.
        public static final SCIMAttributeSchema PHOTOS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHOTOS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.PHOTOS_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.PHOTO,
                                SCIMConstants.UserSchemaConstants.THUMBNAIL)), null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute
        public static final SCIMAttributeSchema PHOTOS_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHOTOS_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .PHOTOS_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);


        //sub attributes of addresses attribute

        //The full mailing address, formatted for display or use with a mailing label.
        public static final SCIMAttributeSchema ADDRESSES_FORMATTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.FORMATTED_ADDRESS_URI,
                        SCIMConstants.UserSchemaConstants.FORMATTED_ADDRESS,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_FORMATTED_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The full street address component
        public static final SCIMAttributeSchema ADDRESSES_STREET_ADDRESS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.STREET_ADDRESS_URI,
                        SCIMConstants.UserSchemaConstants.STREET_ADDRESS,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_STREET_ADDRESS_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The city or locality component.
        public static final SCIMAttributeSchema ADDRESSES_LOCALITY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.LOCALITY_URI,
                        SCIMConstants.UserSchemaConstants.LOCALITY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_LOCALITY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The state or region component.
        public static final SCIMAttributeSchema ADDRESSES_REGION =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.REGION_URI,
                        SCIMConstants.UserSchemaConstants.REGION,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_REGION_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The zip code or postal code component
        public static final SCIMAttributeSchema ADDRESSES_POSTAL_CODE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.POSTAL_CODE_URI,
                        SCIMConstants.UserSchemaConstants.POSTAL_CODE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_POSTAL_CODE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The country name component.
        public static final SCIMAttributeSchema ADDRESSES_COUNTRY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.COUNTRY_URI,
                        SCIMConstants.UserSchemaConstants.COUNTRY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_COUNTRY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, e.g., 'work' or 'home'.
        public static final SCIMAttributeSchema ADDRESSES_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ADDRESSES_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_TYPE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.WORK,
                                SCIMConstants.UserSchemaConstants.HOME, SCIMConstants.UserSchemaConstants.OTHER)),
                        null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute
        public static final SCIMAttributeSchema ADDRESSES_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ADDRESSES_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .ADDRESSES_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //sub attributes of ims attribute

        //The identifier of the User's group.
        public static final SCIMAttributeSchema GROUP_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GROUPS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.GROUP_VALUE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The uri of the corresponding 'Group' resource to which the user belongs.
        public static final SCIMAttributeSchema GROUP_REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GROUPS_REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants.GROUP_REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<SCIMDefinitions.ReferenceType>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.USER, SCIMDefinitions.ReferenceType
                                        .GROUP)), null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema GROUP_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GROUPS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.GROUP_DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function, e.g., 'direct' or 'indirect'.
        public static final SCIMAttributeSchema GROUP_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GROUPS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.GROUP_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, new ArrayList<String>
                                (Arrays.asList(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP,
                                        SCIMConstants.UserSchemaConstants.INDIRECT_MEMBERSHIP)), null, null);

        //sub attributes of entitlements attribute

        //The value of an entitlement.
        public static final SCIMAttributeSchema ENTITLEMENTS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ENTITLEMENTS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_VALUE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema ENTITLEMENTS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<SCIMDefinitions.ReferenceType>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.USER, SCIMDefinitions.ReferenceType
                                        .GROUP)), null);

        //A label indicating the attribute's function.
        public static final SCIMAttributeSchema ENTITLEMENTS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ENTITLEMENTS_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_TYPE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // Boolean value indicating the 'primary' or preferred attribute value for this attribute.he primary
        // attribute value 'true' MUST appear no more than once.
        public static final SCIMAttributeSchema ENTITLEMENTS_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .ENTITLEMENTS_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //sub attributes of entitlements attribute

        //The value of a role.
        public static final SCIMAttributeSchema ROLES_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.ROLES_VALUE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema ROLES_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants
                                .ROLES_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function..
        public static final SCIMAttributeSchema ROLES_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.ROLES_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute.
        public static final SCIMAttributeSchema ROLES_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants.ROLES_PRIMARY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // The uri of the corresponding 'Role' resource to which the user belongs.
        public static final SCIMAttributeSchema ROLES_REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants.ROLES_REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.USER, SCIMDefinitions.ReferenceType
                                        .ROLE)), null);

        //sub attributes of x509certificates attribute

        //The value of an X.509 certificate.
        public static final SCIMAttributeSchema X509CERTIFICATES_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_VALUE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema X509CERTIFICATES_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the attribute's function..
        public static final SCIMAttributeSchema X509CERTIFICATES_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_TYPE_URI,
                        SCIMConstants.CommonSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_TYPE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A Boolean value indicating the 'primary' or preferred attribute value for this attribute.
        public static final SCIMAttributeSchema X509CERTIFICATES_PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_PRIMARY_URI,
                        SCIMConstants.CommonSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_PRIMARY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);


        //sub attributes of name attribute

        //The full name, including all middle names, titles, and suffixes as appropriate, formatted for display
        public static final SCIMAttributeSchema FORMATTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.FORMATTED_NAME_URI,
                        SCIMConstants.UserSchemaConstants.FORMATTED_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .FORMATTED_NAME_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The family name of the User, or last name in most Western languages
        public static final SCIMAttributeSchema FAMILY_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.FAMILY_NAME_URI,
                        SCIMConstants.UserSchemaConstants.FAMILY_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.FAMILY_NAME_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The given name of the User, or first name in most Western languages.
        public static final SCIMAttributeSchema GIVEN_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GIVEN_NAME_URI,
                        SCIMConstants.UserSchemaConstants.GIVEN_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.GIVEN_NAME_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The middle name(s) of the User.
        public static final SCIMAttributeSchema MIDDLE_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.MIDDLE_NAME_URI,
                        SCIMConstants.UserSchemaConstants.MIDDLE_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.MIDDLE_NAME_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null); //honorificPrefix

        //The honorific prefix(es) of the User, or title in most Western languages.
        public static final SCIMAttributeSchema HONORIFIC_PREFIX =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.HONORIFIC_PREFIX_URI,
                        SCIMConstants.UserSchemaConstants.HONORIFIC_PREFIX,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .HONORIFIC_PREFIX_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The honorific suffix(es) of the User, or suffix in most Western languages.
        public static final SCIMAttributeSchema HONORIFIC_SUFFIX =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.HONORIFIC_SUFFIX_URI,
                        SCIMConstants.UserSchemaConstants.HONORIFIC_SUFFIX,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .HONORIFIC_SUFFIX_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

            /*-------------------------------------------------------------------------------------*/

            /* attribute schemas of the attributes defined in user schema. */

        //A service provider's unique identifier for the user, typically used by the user to directly
        //authenticate to the service provider.
        public static final SCIMAttributeSchema USERNAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.USER_NAME_URI,
                        SCIMConstants.UserSchemaConstants.USER_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.USERNAME_DESC,
                        true, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.SERVER, null, null, null);

        //The components of the user's real name.
        public static final SCIMAttributeSchema NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.NAME_URI,
                        SCIMConstants.UserSchemaConstants.NAME,
                        SCIMDefinitions.DataType.COMPLEX, false, SCIMConstants.UserSchemaConstants.NAME_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(FORMATTED, FAMILY_NAME, GIVEN_NAME,
                                MIDDLE_NAME,
                                HONORIFIC_PREFIX, HONORIFIC_SUFFIX)));

        //The name of the User, suitable for display to end-users
        public static final SCIMAttributeSchema DISPLAY_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.DISPLAY_NAME_URI,
                        SCIMConstants.UserSchemaConstants.DISPLAY_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.DISPLAY_NAME_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The casual way to address the user in real life
        public static final SCIMAttributeSchema NICK_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.NICK_NAME_URI,
                        SCIMConstants.UserSchemaConstants.NICK_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.NICK_NAME_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A fully qualified URL pointing to a page representing the User's online profile.
        public static final SCIMAttributeSchema PROFILE_URL =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PROFILE_URL_URI,
                        SCIMConstants.UserSchemaConstants.PROFILE_URL,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.UserSchemaConstants
                                .PROFILE_URL_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType
                                .EXTERNAL)), null);

        //The user's title, such as \"Vice President.\"
        public static final SCIMAttributeSchema TITLE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.TITLE_URI,
                        SCIMConstants.UserSchemaConstants.TITLE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.TITLE_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //Used to identify the relationship between the organization and the user.
        public static final SCIMAttributeSchema USER_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.USER_TYPE_URI,
                        SCIMConstants.UserSchemaConstants.USER_TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.USER_TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //Indicates the User's preferred written or spoken language.
        public static final SCIMAttributeSchema PREFERRED_LANGUAGE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PREFERRED_LANGUAGE_URI,
                        SCIMConstants.UserSchemaConstants.PREFERRED_LANGUAGE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants
                                .PREFERRED_LANGUAGE_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //Used to indicate the User's default location for purposes of localizing items such as currency,
        // date time format, or numerical representations.
        public static final SCIMAttributeSchema LOCALE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.LOCALE_URI,
                        SCIMConstants.UserSchemaConstants.LOCALE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.LOCALE_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The User's time zone in the 'Olson' time zone database format, e.g., 'America/Los_Angeles'.
        public static final SCIMAttributeSchema TIME_ZONE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.TIME_ZONE_URI,
                        SCIMConstants.UserSchemaConstants.TIME_ZONE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.TIME_ZONE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A Boolean value indicating the User's administrative status.
        public static final SCIMAttributeSchema ACTIVE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ACTIVE_URI,
                        SCIMConstants.UserSchemaConstants.ACTIVE,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.UserSchemaConstants.ACTIVE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The User's cleartext password.
        public static final SCIMAttributeSchema PASSWORD =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PASSWORD_URI,
                        SCIMConstants.UserSchemaConstants.PASSWORD,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.UserSchemaConstants.PASSWORD_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.WRITE_ONLY, SCIMDefinitions.Returned.NEVER,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //Email addresses for the user.
        public static final SCIMAttributeSchema EMAILS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.EMAILS_URI,
                        SCIMConstants.UserSchemaConstants.EMAILS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.EMAILS_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(EMAIL_VALUE, EMAIL_DISPLAY, EMAIL_TYPE,
                                EMAIL_PRIMARY)));

        //Phone numbers for the User.
        public static final SCIMAttributeSchema PHONE_NUMBERS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHONE_NUMBERS_URI,
                        SCIMConstants.UserSchemaConstants.PHONE_NUMBERS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.PHONE_NUMBERS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(PHONE_NUMBERS_VALUE, PHONE_NUMBERS_DISPLAY,
                                PHONE_NUMBERS_TYPE, PHONE_NUMBERS_PRIMARY)));

        //Instant messaging addresses for the User.
        public static final SCIMAttributeSchema IMS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.IMS_URI,
                        SCIMConstants.UserSchemaConstants.IMS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.IMS_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(IMS_VALUE, IMS_DISPLAY, IMS_TYPE,
                                IMS_PRIMARY)));

        //URLs of photos of the User.
        public static final SCIMAttributeSchema PHOTOS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.PHOTOS_URI,
                        SCIMConstants.UserSchemaConstants.PHOTOS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.PHOTOS_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(PHOTOS_VALUE, PHOTOS_DISPLAY, PHOTOS_TYPE,
                                PHOTOS_PRIMARY)));

        //A physical mailing address for this User.
        public static final SCIMAttributeSchema ADDRESSES =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ADDRESSES_URI,
                        SCIMConstants.UserSchemaConstants.ADDRESSES,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.ADDRESSES_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(ADDRESSES_FORMATTED,
                                ADDRESSES_STREET_ADDRESS, ADDRESSES_LOCALITY,
                                ADDRESSES_REGION, ADDRESSES_POSTAL_CODE, ADDRESSES_COUNTRY, ADDRESSES_TYPE,
                                ADDRESSES_PRIMARY)));

        //A list of groups to which the user belongs, either through direct membership, through nested groups, or
        // dynamically calculated.
        public static final SCIMAttributeSchema GROUPS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.GROUP_URI,
                        SCIMConstants.UserSchemaConstants.GROUPS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.GROUPS_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(GROUP_VALUE, GROUP_REF, GROUP_DISPLAY,
                                GROUP_TYPE)));

        public static final SCIMAttributeSchema ROLES_SCHEMA = SCIMAttributeSchema
                .createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_URI,
                        SCIMConstants.UserSchemaConstants.ROLES, SCIMDefinitions.DataType.COMPLEX, true,
                        SCIMConstants.UserSchemaConstants.ROLES_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<>(Arrays.asList(ROLES_VALUE, ROLES_REF, ROLES_DISPLAY)));

        //A list of entitlements for the User that represent a thing the User has.
        public static final SCIMAttributeSchema ENTITLEMENTS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ENTITLEMENTS_URI,
                        SCIMConstants.UserSchemaConstants.ENTITLEMENTS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.ENTITLEMENTS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(ENTITLEMENTS_VALUE, ENTITLEMENTS_DISPLAY,
                                ENTITLEMENTS_TYPE, ENTITLEMENTS_PRIMARY)));

        //A list of roles for the User that collectively represent who the User is, e.g., 'Student', 'Faculty'.
        public static final SCIMAttributeSchema ROLES =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.ROLES_URI,
                        SCIMConstants.UserSchemaConstants.ROLES,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants.ROLES_DESC, false,
                        false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(ROLES_VALUE, ROLES_DISPLAY,
                                ROLES_TYPE, ROLES_PRIMARY)));

        //A list of roles for the User that collectively represent who the User is, e.g., 'Student', 'Faculty'.
        public static final SCIMAttributeSchema X509CERTIFICATES =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.UserSchemaConstants.X509CERTIFICATES_URI,
                        SCIMConstants.UserSchemaConstants.X509CERTIFICATES,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.UserSchemaConstants
                                .X509CERTIFICATES_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(X509CERTIFICATES_VALUE,
                                X509CERTIFICATES_DISPLAY,
                                X509CERTIFICATES_TYPE, X509CERTIFICATES_PRIMARY)));

    }

    /**
     * SCIM defined group attribute schemas.
     */
    public static class SCIMGroupSchemaDefinition {

        /*********** SCIM defined group attribute schemas ****************************/

    /* sub-attribute schemas of the attributes defined in SCIM group schema. */

        //sub attributes of members attribute

        //Identifier of the member of this Group.
        public static final SCIMAttributeSchema VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.GroupSchemaConstants.VALUE_DESC, true,
                        false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //The uri corresponding to a SCIM resource that is a member of this Group.
        public static final SCIMAttributeSchema REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.GroupSchemaConstants.REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<SCIMDefinitions.ReferenceType>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.USER, SCIMDefinitions.ReferenceType
                                        .GROUP)), null);

        //A human-readable name for the Group. REQUIRED.
        public static final SCIMAttributeSchema DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.DISPLAY_URI,
                        SCIMConstants.GroupSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.GroupSchemaConstants.DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A label indicating the type of resource, e.g. 'User' or 'Group'.
        public static final SCIMAttributeSchema TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.TYPE_URI,
                        SCIMConstants.GroupSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.GroupSchemaConstants.TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE,
                        new ArrayList<String>(Arrays.asList(SCIMConstants.UserSchemaConstants.MEMBERS_TYPE_USER,
                                SCIMConstants.UserSchemaConstants.MEMBERS_TYPE_GROUP)), null, null);

    /*------------------------------------------------------------------------------------------------------*/

                /* attribute schemas of the attributes defined in group schema. */

        //A human-readable name for the Group. REQUIRED.
        public static final SCIMAttributeSchema DISPLAY_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME_URI,
                        SCIMConstants.GroupSchemaConstants.DISPLAY_NAME,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.GroupSchemaConstants.DISPLAY_NAME_DESC,
                        true, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        //A list of members of the Group.
        public static final SCIMAttributeSchema MEMBERS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.MEMBERS_URI,
                        SCIMConstants.GroupSchemaConstants.MEMBERS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.GroupSchemaConstants.MEMBERS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, new ArrayList<AttributeSchema>(Arrays.asList
                                (VALUE, REF, DISPLAY, TYPE)));

        // The value of a role.
        public static final SCIMAttributeSchema ROLES_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.ROLES_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.GroupSchemaConstants.ROLES_VALUE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // A human-readable name, primarily used for display purposes.
        public static final SCIMAttributeSchema ROLES_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.ROLES_DISPLAY_URI,
                        SCIMConstants.CommonSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.GroupSchemaConstants
                                .ROLES_DISPLAY_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // The uri of the corresponding 'Role' resource to which the user belongs.
        public static final SCIMAttributeSchema ROLES_REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.ROLES_REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.GroupSchemaConstants.ROLES_REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.GROUP, SCIMDefinitions.ReferenceType
                                        .ROLE)), null);

        // A list of roles of the Group.
        public static final SCIMAttributeSchema ROLES_SCHEMA = SCIMAttributeSchema
                .createSCIMAttributeSchema(SCIMConstants.GroupSchemaConstants.ROLES_URI,
                        SCIMConstants.GroupSchemaConstants.ROLES, SCIMDefinitions.DataType.COMPLEX, true,
                        SCIMConstants.GroupSchemaConstants.ROLES_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<>(Arrays.asList(ROLES_VALUE, ROLES_REF, ROLES_DISPLAY)));

    }

    /**
     * SCIM defined role attribute schemas.
     */
    public static class SCIMRoleSchemaDefinition {

        // Identifier of the user of this Role.
        public static final SCIMAttributeSchema USERS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.USERS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.USERS_VALUE_DESC,
                        false,
                        false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // The uri corresponding to a SCIM resource that is a user of this Role.
        public static final SCIMAttributeSchema USERS_REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.USERS_REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.RoleSchemaConstants.USERS_REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.ROLE, SCIMDefinitions.ReferenceType
                                        .USER)), null);

        // A human-readable name for the Role. REQUIRED.
        public static final SCIMAttributeSchema USERS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.USERS_DISPLAY_URI,
                        SCIMConstants.RoleSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.USERS_DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // A label indicating the type of resource, e.g. 'User' or 'Group'.
        public static final SCIMAttributeSchema USERS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_TYPE_URI,
                        SCIMConstants.RoleSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // Identifier of the group of this Role.
        public static final SCIMAttributeSchema GROUPS_VALUE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_VALUE_URI,
                        SCIMConstants.CommonSchemaConstants.VALUE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.GROUPS_VALUE_DESC,
                        false,
                        false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // The uri corresponding to a SCIM resource that is a group of this Role.
        public static final SCIMAttributeSchema GROUPS_REF =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_REF_URI,
                        SCIMConstants.CommonSchemaConstants.REF,
                        SCIMDefinitions.DataType.REFERENCE, false, SCIMConstants.RoleSchemaConstants.GROUPS_REF_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, new ArrayList<>
                                (Arrays.asList(SCIMDefinitions.ReferenceType.ROLE, SCIMDefinitions.ReferenceType
                                        .GROUP)), null);

        // A human-readable name for the ROle. REQUIRED.
        public static final SCIMAttributeSchema GROUPS_DISPLAY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_DISPLAY_URI,
                        SCIMConstants.RoleSchemaConstants.DISPLAY,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.GROUPS_DISPLAY_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // A label indicating the type of resource, e.g. 'User' or 'Group'.
        public static final SCIMAttributeSchema GROUPS_TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_TYPE_URI,
                        SCIMConstants.RoleSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.RoleSchemaConstants.TYPE_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // Attribute schemas of the attributes defined in role schema.

        // A human-readable name for the Role. REQUIRED.
        public static final SCIMAttributeSchema DISPLAY_NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.DISPLAY_NAME_URI,
                        SCIMConstants.RoleSchemaConstants.DISPLAY_NAME, SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.RoleSchemaConstants.DISPLAY_NAME_DESC,
                        true, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        // A list of users of the Role.
        public static final SCIMAttributeSchema USERS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.USERS_URI,
                        SCIMConstants.RoleSchemaConstants.USERS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.RoleSchemaConstants.USERS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, new ArrayList<>(Arrays.asList
                                (USERS_VALUE, USERS_REF, USERS_DISPLAY, USERS_TYPE)));

        // A list of groups of the Role.
        public static final SCIMAttributeSchema GROUPS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.GROUPS_URI,
                        SCIMConstants.RoleSchemaConstants.GROUPS,
                        SCIMDefinitions.DataType.COMPLEX, true, SCIMConstants.RoleSchemaConstants.GROUPS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, new ArrayList<>(Arrays.asList
                                (GROUPS_VALUE, GROUPS_REF, GROUPS_DISPLAY, GROUPS_TYPE)));

        // A list of permissions of the Role.
        public static final SCIMAttributeSchema PERMISSIONS =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.RoleSchemaConstants.PERMISSIONS_URI,
                        SCIMConstants.RoleSchemaConstants.PERMISSIONS, SCIMDefinitions.DataType.REFERENCE, true,
                        SCIMConstants.RoleSchemaConstants.PERMISSIONS_DESC,
                        false, false,
                        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);
    }

    /**
     * SCIM defined ServiceProviderConfig schemas.
     */
    public static class SCIMServiceProviderConfigSchemaDefinition {

        /*********** SCIM defined ServiceProviderConfig schemas ****************************/

    /* sub-attribute schemas of the attributes defined in SCIM ServiceProviderConfig schema. */

        public static final SCIMAttributeSchema PATCH_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PATCH_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema BULK_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.BULK_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema FILTER_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.FILTER_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema SORT_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SORT_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema ETAG_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.ETAG_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema CHANGE_PASSWORD_SUPPORTED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CHANGE_PASSWORD_SUPPORTED_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SUPPORTED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema MAX_OPERATIONS =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_OPERATIONS_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_OPERATIONS,
                        SCIMDefinitions.DataType.INTEGER, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_OPERATIONS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema MAX_PAYLOAD_SIZE =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_PAYLOAD_SIZE_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_PAYLOAD_SIZE,
                        SCIMDefinitions.DataType.INTEGER, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_PAYLOAD_SIZE_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema MAX_RESULTS =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_RESULTS_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_RESULTS,
                        SCIMDefinitions.DataType.INTEGER, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.MAX_RESULTS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);


        public static final SCIMAttributeSchema NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.NAME_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.NAME,
                        SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.NAME_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema DESCRIPTION =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DESCRIPTION_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DESCRIPTION,
                        SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DESCRIPTION_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema SPEC_URI =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SPEC_URI_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SPEC_URI,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SPEC_URI_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType
                                .EXTERNAL)),
                        null);

        public static final SCIMAttributeSchema AUTHENTICATION_SCHEMES_DOCUMENTATION_URI =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.AUTHENTICATION_SCHEMAS_DOCUMENTATION_URI_URI,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI_DESC, false, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType
                                .EXTERNAL)),
                        null);

        public static final SCIMAttributeSchema CURSOR_PAGINATION =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CURSOR_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CURSOR,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CURSOR_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema TYPE =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ServiceProviderConfigSchemaConstants
                                .TYPE_URL,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.TYPE,
                        SCIMDefinitions.DataType.STRING, false, SCIMConstants.ServiceProviderConfigSchemaConstants
                                .TYPE_DESC,
                        true, false, SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema PRIMARY =
                SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ServiceProviderConfigSchemaConstants
                                .PRIMARY_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PRIMARY,
                        SCIMDefinitions.DataType.BOOLEAN, false, SCIMConstants.ServiceProviderConfigSchemaConstants
                                .PRIMARY_DESC,
                        false, false, SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

    /*------------------------------------------------------------------------------------------------------*/

        /* attribute schemas of the attributes defined in ServiceProviderConfig schema. */

        public static final SCIMAttributeSchema DOCUMENTATION_URI =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.DOCUMENTATION_URI_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema PATCH =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PATCH_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PATCH,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PATCH_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(PATCH_SUPPORTED)));

        public static final SCIMAttributeSchema BULK =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.BULK_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.BULK,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.BULK_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(BULK_SUPPORTED, MAX_OPERATIONS,
                                MAX_PAYLOAD_SIZE)));

        public static final SCIMAttributeSchema FILTER =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.FILTER_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.FILTER,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.FILTERS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(FILTER_SUPPORTED, MAX_RESULTS)));

        public static final SCIMAttributeSchema CHANGE_PASSWORD =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CHANGE_PASSWORD_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CHANGE_PASSWORD,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.CHANGE_PASSWORD_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(CHANGE_PASSWORD_SUPPORTED)));

        public static final SCIMAttributeSchema SORT =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SORT_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SORT,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.SORT_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(SORT_SUPPORTED)));

        public static final SCIMAttributeSchema ETAG =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.ETAG_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.ETAG,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.ETAG_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(ETAG_SUPPORTED)));

        public static final SCIMAttributeSchema AUTHENTICATION_SCHEMES =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.AUTHENTICATION_SCHEMAS_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.AUTHENTICATION_SCHEMAS,
                        SCIMDefinitions.DataType.COMPLEX, true,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.AUTHENTICATION_SCHEMAS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(NAME, DESCRIPTION,
                                SPEC_URI, AUTHENTICATION_SCHEMES_DOCUMENTATION_URI, TYPE, PRIMARY)));

        public static final SCIMAttributeSchema PAGINATION =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PAGINATION_URI,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PAGINATION,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ServiceProviderConfigSchemaConstants.PAGINATION_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(CURSOR_PAGINATION)));
    }

    /**
     * SCIM defined resourceType  schemas.
     */
    public static class SCIMResourceTypeSchemaDefinition {

        /*********** SCIM defined resourceType  schemas. ****************************/

        public static final SCIMAttributeSchema SCHEMA_EXTENSION_SCHEMA =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType.URI)),
                        null);

        public static final SCIMAttributeSchema SCHEMA_EXTENSION_REQUIRED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED,
                        SCIMDefinitions.DataType.BOOLEAN, false,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSION_REQUIRED_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema ID =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.ID_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.ID,
                        SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.ResourceTypeSchemaConstants.ID_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema DESCRIPTION =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION,
                        SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema NAME =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.NAME_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.NAME,
                        SCIMDefinitions.DataType.STRING, false,
                        SCIMConstants.ResourceTypeSchemaConstants.NAME_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null, null);

        public static final SCIMAttributeSchema ENDPOINT =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType.URI)),
                        null);

        public static final SCIMAttributeSchema SCHEMA =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA,
                        SCIMDefinitions.DataType.REFERENCE, false,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null,
                        new ArrayList<SCIMDefinitions.ReferenceType>(Arrays.asList(SCIMDefinitions.ReferenceType.URI)),
                        null);

        public static final SCIMAttributeSchema SCHEMA_EXTENSIONS =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS,
                        SCIMDefinitions.DataType.COMPLEX, true,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(SCHEMA_EXTENSION_SCHEMA,
                                SCHEMA_EXTENSION_REQUIRED)));

        // Have the multivalued as false for SchemaExtensions schema inorder to have the backward compatibility.
        public static final SCIMAttributeSchema SCHEMA_EXTENSIONS_WITHOUT_MULTIVALUED =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_URI,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS,
                        SCIMDefinitions.DataType.COMPLEX, false,
                        SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_DESC, true, false,
                        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT,
                        SCIMDefinitions.Uniqueness.NONE, null, null,
                        new ArrayList<AttributeSchema>(Arrays.asList(SCHEMA_EXTENSION_SCHEMA,
                                SCHEMA_EXTENSION_REQUIRED)));


    }

    /*
     * **********SCIM defined User Resource Schema.****************************
     */

    public static final SCIMResourceTypeSchema SCIM_USER_SCHEMA =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.USER_CORE_SCHEMA_URI)),
                    ID, EXTERNAL_ID, META,
                    SCIMUserSchemaDefinition.USERNAME,
                    SCIMUserSchemaDefinition.NAME,
                    SCIMUserSchemaDefinition.DISPLAY_NAME,
                    SCIMUserSchemaDefinition.NICK_NAME,
                    SCIMUserSchemaDefinition.PROFILE_URL,
                    SCIMUserSchemaDefinition.TITLE,
                    SCIMUserSchemaDefinition.USER_TYPE,
                    SCIMUserSchemaDefinition.PREFERRED_LANGUAGE,
                    SCIMUserSchemaDefinition.LOCALE,
                    SCIMUserSchemaDefinition.TIME_ZONE,
                    SCIMUserSchemaDefinition.ACTIVE,
                    SCIMUserSchemaDefinition.PASSWORD,
                    SCIMUserSchemaDefinition.EMAILS,
                    SCIMUserSchemaDefinition.PHONE_NUMBERS,
                    SCIMUserSchemaDefinition.IMS,
                    SCIMUserSchemaDefinition.PHOTOS,
                    SCIMUserSchemaDefinition.ADDRESSES,
                    SCIMUserSchemaDefinition.GROUPS,
                    SCIMUserSchemaDefinition.ENTITLEMENTS,
                    SCIMUserSchemaDefinition.ROLES,
                    SCIMUserSchemaDefinition.X509CERTIFICATES);
    /*
     * **********SCIM defined Group Resource Schema.****************************
     */

    public static final SCIMResourceTypeSchema SCIM_GROUP_SCHEMA =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.GROUP_CORE_SCHEMA_URI)),
                    ID, EXTERNAL_ID, META,
                    SCIMGroupSchemaDefinition.DISPLAY_NAME,
                    SCIMGroupSchemaDefinition.MEMBERS);

    /**
     * SCIM defined Role Resource Schema.
     */
    public static final SCIMResourceTypeSchema SCIM_ROLE_SCHEMA =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<>(Collections.singletonList(SCIMConstants.ROLE_SCHEMA_URI)),
                    ID,
                    SCIMRoleSchemaDefinition.DISPLAY_NAME,
                    SCIMRoleSchemaDefinition.USERS,
                    SCIMRoleSchemaDefinition.GROUPS,
                    SCIMRoleSchemaDefinition.PERMISSIONS);

    /*
     * **********SCIM defined Service Provider Config Resource Schema.****************************
     */

    public static final SCIMResourceTypeSchema SCIM_SERVICE_PROVIDER_CONFIG_SCHEMA =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.SERVICE_PROVIDER_CONFIG_SCHEMA_URI)),
                    META,
                    SCIMServiceProviderConfigSchemaDefinition.DOCUMENTATION_URI,
                    SCIMServiceProviderConfigSchemaDefinition.PATCH,
                    SCIMServiceProviderConfigSchemaDefinition.BULK,
                    SCIMServiceProviderConfigSchemaDefinition.SORT,
                    SCIMServiceProviderConfigSchemaDefinition.FILTER,
                    SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD,
                    SCIMServiceProviderConfigSchemaDefinition.PAGINATION,
                    SCIMServiceProviderConfigSchemaDefinition.ETAG,
                    SCIMServiceProviderConfigSchemaDefinition.AUTHENTICATION_SCHEMES);

    /*
     * **********SCIM defined Resource Type Resource Schema.****************************
     */

    public static final SCIMResourceTypeSchema SCIM_RESOURCE_TYPE_SCHEMA =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI)), META,
                    SCIMResourceTypeSchemaDefinition.ID,
                    SCIMResourceTypeSchemaDefinition.NAME,
                    SCIMResourceTypeSchemaDefinition.ENDPOINT,
                    SCIMResourceTypeSchemaDefinition.DESCRIPTION,
                    SCIMResourceTypeSchemaDefinition.SCHEMA,
                    SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS);
    
    /**
     * In the spec it is mentioned to use JSONArray for SchemaExtensions. Inorder to keep the
     *  backward compatibility, this is added.
     */
    public static final SCIMResourceTypeSchema SCIM_RESOURCE_TYPE_SCHEMA_WITHOUT_MULTIVALUED_SCHEMA_EXTENSIONS =
            SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.LISTED_RESOURCE_CORE_SCHEMA_URI)), META,
                    SCIMResourceTypeSchemaDefinition.ID,
                    SCIMResourceTypeSchemaDefinition.NAME,
                    SCIMResourceTypeSchemaDefinition.ENDPOINT,
                    SCIMResourceTypeSchemaDefinition.DESCRIPTION,
                    SCIMResourceTypeSchemaDefinition.SCHEMA,
                    SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS_WITHOUT_MULTIVALUED);

}
