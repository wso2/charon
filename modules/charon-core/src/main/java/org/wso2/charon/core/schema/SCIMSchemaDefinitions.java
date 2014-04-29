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

/**
 * This class contains the schema definitions in
 * http://www.simplecloud.info/specs/draft-scim-core-schema-00.html as ResourceSchemas and AttributeSchemas.
 * These are used when constructing SCIMObjects from the decoded payload
 */
public class SCIMSchemaDefinitions {

    //data types that an attribute can take, according to the SCIM spec.

    public static enum DataType {
        STRING, BOOLEAN, DECIMAL, INTEGER, DATE_TIME, BINARY
    }

    /*sub attribute schemas for the sub attributes defined in SCIM Schema - including the common set
    * of sub attributes in Multi-Valued Attributes.*/


    /**
     * *******************Sub attributes found in common-schema*********************************
     */
    //TODO:add canonical values.
    public static final SCIMSubAttributeSchema TYPE =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(null,
                                                                SCIMConstants.CommonSchemaConstants.TYPE,
                                                                DataType.STRING, SCIMConstants.TYPE_DESC,
                                                                false, false, false, null);
    public static final SCIMSubAttributeSchema PRIMARY =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(null,
                                                                SCIMConstants.CommonSchemaConstants.PRIMARY,
                                                                DataType.BOOLEAN, SCIMConstants.PRIMARY_DESC,
                                                                false, false, false, null);
    /*TODO:Verify: removing read only requirement on DISPLAY to support creating groups with members whose
    name is sent in DISPLAY attribute*/
    public static final SCIMSubAttributeSchema DISPLAY =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(null,
                                                                SCIMConstants.CommonSchemaConstants.DISPLAY,
                                                                DataType.STRING, SCIMConstants.DISPLAY_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema OPERATION =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(null,
                                                                SCIMConstants.CommonSchemaConstants.OPERATION,
                                                                DataType.STRING, SCIMConstants.OPERATION_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema VALUE =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(null,
                                                                SCIMConstants.CommonSchemaConstants.VALUE,
                                                                DataType.STRING, SCIMConstants.VALUE_DESC,
                                                                false, false, false, null);

    // sub attributes of meta and then the meta attribute
    public static final SCIMSubAttributeSchema CREATED =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.META_CREATED_URI,
                                                                SCIMConstants.CommonSchemaConstants.CREATED,
                                                                SCIMSchemaDefinitions.DataType.DATE_TIME,
                                                                SCIMConstants.CREATED_DESC, true, false, false, null);

    public static final SCIMSubAttributeSchema LAST_MODIFIED =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.META_LAST_MODIFIED_URI,
                                                                SCIMConstants.CommonSchemaConstants.LAST_MODIFIED,
                                                                SCIMSchemaDefinitions.DataType.DATE_TIME,
                                                                SCIMConstants.LAST_MODIFIED_DESC, true, false, false, null);

    public static final SCIMSubAttributeSchema LOCATION =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.META_LOCATION_URI,
                                                                SCIMConstants.CommonSchemaConstants.LOCATION,
                                                                SCIMSchemaDefinitions.DataType.STRING,
                                                                SCIMConstants.LOCATION_DESC, true, false, false, null);
    public static final SCIMSubAttributeSchema VERSION =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.META_VERSION_URI,
                                                                SCIMConstants.CommonSchemaConstants.VERSION,
                                                                SCIMSchemaDefinitions.DataType.STRING,
                                                                SCIMConstants.VERSION_DESC, true, false, false, null);
    public static final SCIMSubAttributeSchema ATTRIBUTES =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.META_ATTRIBUTES_URI,
                                                                SCIMConstants.CommonSchemaConstants.ATTRIBUTES,
                                                                SCIMSchemaDefinitions.DataType.STRING,
                                                                SCIMConstants.ATTRIBUTES_DESC, false, false, false, null);


    /**
     * *************Sub attribute schemas for the sub attributes defined in user schema**********
     */
    //sub attributes of name
    public static final SCIMSubAttributeSchema FORMATTED =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_FORMATTED_NAME_URI,
                                                                SCIMConstants.UserSchemaConstants.FORMATTED_NAME,
                                                                DataType.STRING,
                                                                SCIMConstants.FORMATTED_NAME_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema FAMILY_NAME =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_FAMILY_NAME_URI,
                                                                SCIMConstants.UserSchemaConstants.FAMILY_NAME,
                                                                DataType.STRING, SCIMConstants.FAMILY_NAME_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema GIVEN_NAME =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_GIVEN_NAME_URI,
                                                                SCIMConstants.UserSchemaConstants.GIVEN_NAME,
                                                                DataType.STRING, SCIMConstants.GIVEN_NAME_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema MIDDLE_NAME =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_MIDDLE_NAME_URI,
                                                                SCIMConstants.UserSchemaConstants.MIDDLE_NAME,
                                                                DataType.STRING, SCIMConstants.MIDDLE_NAME_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema HONORIFIC_PREFIX =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_HONORIC_PREFIX_URI,
                                                                SCIMConstants.UserSchemaConstants.HONORIFIC_PREFIX,
                                                                DataType.STRING, SCIMConstants.HONORIFIC_PREFIX_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema HONORIFIC_SUFFIX =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.NAME_HONORIC_SUFFIX_URI,
                                                                SCIMConstants.UserSchemaConstants.HONORIFIC_SUFFIX,
                                                                DataType.STRING, SCIMConstants.HONORIFIC_SUFFIX_DESC,
                                                                false, false, false, null);

    //sub attributes of addresses
    public static final SCIMSubAttributeSchema FORMATTED_ADDRESS =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_FORMATTED_URI,
                                                                SCIMConstants.UserSchemaConstants.FORMATTED_ADDRESS,
                                                                DataType.STRING, SCIMConstants.FORMATTED_ADDRESS_DESC,
                                                                false, false, false, null);
    public static final SCIMSubAttributeSchema STREET_ADDRESS =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_STREET_ADDRESS_URI,
                                                                SCIMConstants.UserSchemaConstants.STREET_ADDRESS,
                                                                DataType.STRING, SCIMConstants.STREET_ADDRESS_DESC,
                                                                false, false, false, null);

    public static final SCIMSubAttributeSchema LOCALITY =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_LOCALITY_URI,
                                                                SCIMConstants.UserSchemaConstants.LOCALITY,
                                                                DataType.STRING, SCIMConstants.LOCALITY_DESC,
                                                                false, false, false, null);
    public static final SCIMSubAttributeSchema REGION =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_REGION_URI,
                                                                SCIMConstants.UserSchemaConstants.REGION,
                                                                DataType.STRING, SCIMConstants.REGION_DESC,
                                                                false, false, false, null);
    public static final SCIMSubAttributeSchema POSTAL_CODE =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_POSTALCODE_URI,
                                                                SCIMConstants.UserSchemaConstants.POSTAL_CODE,
                                                                DataType.STRING, SCIMConstants.POSTAL_CODE_DESC,
                                                                false, false, false, null);
    public static final SCIMSubAttributeSchema COUNTRY =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(SCIMConstants.ADDRESSES_COUNTRY_URI,
                                                                SCIMConstants.UserSchemaConstants.COUNTRY,
                                                                DataType.STRING, SCIMConstants.COUNTRY_DESC,
                                                                false, false, false, null);

    /**
     * *********SCIM defined attribute schemas***************************
     */


    //attribute schemas of the attributes defined in common schema.

    /*Unique identifier for the SCIM Resource as defined by the Service Provider*/
    public static final SCIMAttributeSchema ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ID_URI,
                                                          SCIMConstants.CommonSchemaConstants.ID,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, SCIMConstants.ID_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, true, true, true, null);

    /*Unique identifier for the Resource as defined by the Service Consumer.The Service Provider
    MUST always interpret the externalId as scoped to the Service Consumer's tenant*/
    public static final SCIMAttributeSchema EXTERNAL_ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.EXTERNAL_ID_URI,
                                                          SCIMConstants.CommonSchemaConstants.EXTERNAL_ID,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.EXTERNAL_ID_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    /*META - A complex attribute containing resource metadata. All sub-attributes are OPTIONAL*/

    /*Since all sub attributes of META are optional, META attribute is also optional.*/
    public static final SCIMAttributeSchema META =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.META_URI,
                                                          SCIMConstants.CommonSchemaConstants.META,
                                                          null, false, null, SCIMConstants.META_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false,
                                                          CREATED, LAST_MODIFIED, LOCATION, VERSION, ATTRIBUTES);
    

    //attribute schemas of the attributes defined in user schema.

    /*Unique identifier for the User, typically used by the user to directly authenticate to the service provider.*/
    public static final SCIMAttributeSchema USER_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.USER_NAME_URI,
                                                          SCIMConstants.UserSchemaConstants.USER_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.USER_NAME_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, true, false, null);

    /**
     * This is used to refer the bulk operation
     */
    public static final SCIMAttributeSchema BULK_ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.BULK_ID,
                                                          SCIMConstants.CommonSchemaConstants.BULK_ID,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.BULK_ID_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema PATH =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PATH,
                                                          SCIMConstants.CommonSchemaConstants.PATH,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.PATH_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema METHOD =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.METHOD,
                                                          SCIMConstants.CommonSchemaConstants.METHOD,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.METHOD_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    /*E-mail addresses for the User. The value SHOULD be canonicalized by the Service Provider*/
    //TODO:how 'work','home' and 'other' specified in emails
    //TODO:NOTE:MULTI-VALUED ATTRIBUTES HAVE SUB ATTRIBUTES - DEFINED IN SCHEMA
    public static final SCIMAttributeSchema EMAILS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.EMAILS_URI,
                                                          SCIMConstants.UserSchemaConstants.EMAILS,
                                                          SCIMSchemaDefinitions.DataType.STRING, true,
                                                          SCIMConstants.UserSchemaConstants.EMAIL,
                                                          SCIMConstants.EMAILS_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false, null);

    public static final SCIMAttributeSchema USER_DISPLAY_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.DISPLAY_NAME_URI,
                                                          SCIMConstants.UserSchemaConstants.DISPLAY_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.USER_DISPLAY_NAME_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false,
                                                          false, false, null);

    //no attribute URI for the parent attribute of complex attributes
    public static final SCIMAttributeSchema NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.NAME_URI,
                                                          SCIMConstants.UserSchemaConstants.NAME,
                                                          DataType.STRING, false, null,
                                                          SCIMConstants.NAME_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false,
                                                          FORMATTED, FAMILY_NAME, GIVEN_NAME, MIDDLE_NAME,
                                                          HONORIFIC_PREFIX, HONORIFIC_SUFFIX);
    public static final SCIMAttributeSchema NICK_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.NICK_NAME_URI,
                                                          SCIMConstants.UserSchemaConstants.NICK_NAME,
                                                          DataType.STRING, false, null, SCIMConstants.NICK_NAME_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);
    public static final SCIMAttributeSchema PROFILE_URL =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PROFILE_URL_URI,
                                                          SCIMConstants.UserSchemaConstants.PROFILE_URL,
                                                          DataType.STRING, false, null, SCIMConstants.PROFILE_URL_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema TITLE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.TITLE_URI,
                                                          SCIMConstants.UserSchemaConstants.TITLE,
                                                          DataType.STRING, false, null, SCIMConstants.TITLE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema USER_TYPE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.USER_TYPE_URI,
                                                          SCIMConstants.UserSchemaConstants.USER_TYPE,
                                                          DataType.STRING, false, null, SCIMConstants.USER_TYPE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema PREFERRED_LANGUAGE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PREFERRED_LANGUAGE_URI,
                                                          SCIMConstants.UserSchemaConstants.PREFERRED_LANGUAGE,
                                                          DataType.STRING, false, null,
                                                          SCIMConstants.PREFERRED_LANGUAGE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);
    public static final SCIMAttributeSchema LOCALE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.LOCAL_URI,
                                                          SCIMConstants.UserSchemaConstants.LOCALE,
                                                          DataType.STRING, false, null, SCIMConstants.LOCALE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);
    public static final SCIMAttributeSchema TIMEZONE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.TIMEZONE_URI,
                                                          SCIMConstants.UserSchemaConstants.TIME_ZONE,
                                                          DataType.STRING, false, null, SCIMConstants.TIME_ZONE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema ACTIVE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ACTIVE_URI,
                                                          SCIMConstants.UserSchemaConstants.ACTIVE,
                                                          DataType.BOOLEAN, false, null, SCIMConstants.ACTIVE_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema PASSWORD =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PASSWORD_URI,
                                                          SCIMConstants.UserSchemaConstants.PASSWORD,
                                                          DataType.STRING, false, null, SCIMConstants.PASSWORD_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);

    public static final SCIMAttributeSchema PHONE_NUMBERS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PHONE_NUMBERS_URI,
                                                          SCIMConstants.UserSchemaConstants.PHONE_NUMBERS,
                                                          DataType.STRING, true,
                                                          SCIMConstants.UserSchemaConstants.PHONE_NUMBER,
                                                          SCIMConstants.PHONE_NUMBERS_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, null);

    public static final SCIMAttributeSchema IMS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.IMS_URI,
                                                          SCIMConstants.UserSchemaConstants.IMS, DataType.STRING,
                                                          true, SCIMConstants.UserSchemaConstants.IM,
                                                          SCIMConstants.IMS_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false,
                                                          null);

    public static final SCIMAttributeSchema PHOTOS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.PHOTOS_URI,
                                                          SCIMConstants.UserSchemaConstants.PHOTOS,
                                                          DataType.STRING, true,
                                                          SCIMConstants.UserSchemaConstants.PHOTO,
                                                          SCIMConstants.PHOTOS_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, null);


    public static final SCIMAttributeSchema GROUPS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.GROUPS_URI,
                                                          SCIMConstants.UserSchemaConstants.GROUPS,
                                                          DataType.STRING, true,
                                                          SCIMConstants.UserSchemaConstants.GROUP,
                                                          SCIMConstants.USER_GROUP_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, true, false, null);

    public static final SCIMAttributeSchema ADDRESSES =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ADDRESSES_URI,
                                                          SCIMConstants.UserSchemaConstants.ADDRESSES,
                                                          DataType.STRING, true,
                                                          SCIMConstants.UserSchemaConstants.ADDRESS,
                                                          SCIMConstants.ADDRESSES_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false, FORMATTED_ADDRESS, STREET_ADDRESS,
                                                          LOCALITY, REGION, POSTAL_CODE, COUNTRY);
    public static final SCIMAttributeSchema ENTITLEMENTS =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ENTITLEMENTS_URI,
                                                          SCIMConstants.UserSchemaConstants.ENTITLEMENTS,
                                                          DataType.STRING, true, SCIMConstants.UserSchemaConstants.ENTITLEMENT,
                                                          SCIMConstants.ENTITLEMENTS_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false, null);
    public static final SCIMAttributeSchema ROLES =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.ROLES_URI,
                                                          SCIMConstants.UserSchemaConstants.ROLES, DataType.STRING,
                                                          true, SCIMConstants.UserSchemaConstants.ROLE, SCIMConstants.ROLES_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);
    public static final SCIMAttributeSchema X509CERTIFICATES =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.X509CERTIFICATES_URI,
                                                          SCIMConstants.UserSchemaConstants.X509CERTIFICATES,
                                                          DataType.BINARY, true,
                                                          SCIMConstants.UserSchemaConstants.X509CERTIFICATE,
                                                          SCIMConstants.X509CERTIFICATES_DESC,
                                                          SCIMConstants.CORE_SCHEMA_URI, false, false, false, null);


    //attribute schemas of the attributes defined in group schema.
    public static final SCIMAttributeSchema DISPLAY_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.DISPLAY_NAME_URI,
                                                          SCIMConstants.GroupSchemaConstants.DISPLAY_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false, null,
                                                          SCIMConstants.DISPLAY_NAME_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false, null);
    /*in SCIM 1.1 members is not required for groups.*/
    public static final SCIMAttributeSchema MEMBERS =
            SCIMAttributeSchema.createSCIMAttributeSchema(null,
                                                          SCIMConstants.GroupSchemaConstants.MEMBERS,
                                                          SCIMSchemaDefinitions.DataType.STRING, true,
                                                          SCIMConstants.GroupSchemaConstants.MEMBER,
                                                          SCIMConstants.MEMBERS_DESC, SCIMConstants.CORE_SCHEMA_URI,
                                                          false, false, false, null);

    /**
     * *************************Attributes defined in Enterprise User Schema***********************
     */
    //TODO


    //schemas of the resources as defined in SCIM Schema spec.


    /**
     * **********SCIM defined Resource Schemas****************************
     */
    public static final SCIMResourceSchema SCIM_COMMON_SCHEMA = SCIMResourceSchema.createSCIMResourceSchema(
            SCIMConstants.COMMON, SCIMConstants.CORE_SCHEMA_URI, SCIMConstants.COMMON_DESC, null,
            SCIMSchemaDefinitions.ID, SCIMSchemaDefinitions.EXTERNAL_ID, SCIMSchemaDefinitions.META);


    public static final SCIMResourceSchema SCIM_USER_SCHEMA =
            SCIMResourceSchema.createSCIMResourceSchema(SCIMConstants.USER, SCIMConstants.CORE_SCHEMA_URI,
                                                        SCIMConstants.USER_DESC, SCIMConstants.USER_ENDPOINT,
                                                        USER_NAME, NAME, DISPLAY_NAME, NICK_NAME, PROFILE_URL,
                                                        TITLE, USER_TYPE, PREFERRED_LANGUAGE, LOCALE,
                                                        TIMEZONE, ACTIVE, PASSWORD, EMAILS, PHONE_NUMBERS, IMS,
                                                        PHOTOS, ADDRESSES, GROUPS, ENTITLEMENTS, ROLES,
                                                        X509CERTIFICATES);


    public static final SCIMResourceSchema SCIM_GROUP_SCHEMA =
            SCIMResourceSchema.createSCIMResourceSchema(SCIMConstants.GROUP, SCIMConstants.CORE_SCHEMA_URI,
                                                        SCIMConstants.GROUP_DESC, SCIMConstants.GROUP_ENDPOINT,
                                                        DISPLAY_NAME, MEMBERS);


    /***********************Custom Defined Schemas for returning listed resources******************/
    //public static final 

    //TODO: think of a way to include canonical types included in SCIM spec for multi-valued attributes.
    //when constructing the resource schema
}
