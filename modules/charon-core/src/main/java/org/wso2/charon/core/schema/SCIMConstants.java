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
 * Place to define the constants found in SCIM-Core schema.
 */
public class SCIMConstants {

    public static final String CORE_SCHEMA_URI = "urn:scim:schemas:core:1.0";

    /*Data formats*/
    public static final String JSON = "json";
    public static final String XML = "xml";

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";

    public static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Identify the format, given the Content-Type,Accept-Headers.
     *
     * @param format
     * @return
     */
    public static String identifyFormat(String format) {
        if (format.equals(APPLICATION_JSON)) {
            return SCIMConstants.JSON;
        } else if (format.equals(APPLICATION_XML)) {
            return SCIMConstants.XML;
        } else {
            return null;
        }
    }

    public static String identifyContentType(String contentType) {
        if (contentType.equals(JSON)) {
            return APPLICATION_JSON;
        } else if (contentType.equals(XML)) {
            return APPLICATION_XML;
        } else {
            return null;
        }
    }

    /*Constants found in core-common schema.*/

    public static class CommonSchemaConstants {
        public static final String ID = "id";
        public static final String EXTERNAL_ID = "externalId";
        public static final String META = "meta";
        public static final String CREATED = "created";
        public static final String LAST_MODIFIED = "lastModified";
        public static final String LOCATION = "location";
        public static final String VERSION = "version";
        public static final String ATTRIBUTES = "attributes";

        public static final String SCHEMAS = "schemas";

        //characteristics of multi valued attribute
        public static final String TYPE = "type";
        public static final String PRIMARY = "primary";

        public static final String DISPLAY = "display";
        public static final String OPERATION = "operation";
        public static final String VALUE = "value";

        //need bulk operations
        public static final String BULK_ID = "bulkId";
        public static final String METHOD = "method";
        public static final String DATA = "data";
        public static final String PATH = "path";
        public static final String USERS_PATH = "/Users";
        public static final String GROUPS_PATH = "/Groups";
        public static final String FAIL_ON_ERRORS = "failOnErrors";
        public static final String OPERATIONS = "Operations";
        public static final String CODE = "code";
        public static final String STATUS = "status";

    }

    /*Constants found in core-user schema.*/

    public static class UserSchemaConstants {
        public static final String USER_NAME = "userName";
        public static final String EMAILS = "emails";
        public static final String EMAIL = "email";

        //types for multi-valued attributes like emails
        public static final String HOME = "home";
        public static final String WORK = "work";

        public static final String DISPLAY_NAME = "displayName";

        public static final String FORMATTED_NAME = "formatted";
        public static final String FAMILY_NAME = "familyName";
        public static final String GIVEN_NAME = "givenName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String HONORIFIC_PREFIX = "honorificPrefix";
        public static final String HONORIFIC_SUFFIX = "honorificSuffix";

        public static final String NAME = "name";
        public static final String NICK_NAME = "nickName";
        public static final String PROFILE_URL = "profileUrl";
        public static final String TITLE = "title";
        public static final String USER_TYPE = "userType";
        public static final String PREFERRED_LANGUAGE = "preferredLanguage";
        public static final String LOCALE = "locale";
        public static final String TIME_ZONE = "timezone";
        public static final String ACTIVE = "active";
        public static final String PASSWORD = "password";

        public static final String PHONE_NUMBERS = "phoneNumbers";
        public static final String PHONE_NUMBER = "phoneNumber";

        public static final String IMS = "ims";
        public static final String IM = "im";

        public static final String PHOTOS = "photos";
        public static final String PHOTO = "photo";

        public static final String ADDRESSES = "addresses";
        public static final String ADDRESS = "address";

        public static final String GROUPS = "groups";
        public static final String GROUP = "group";

        public static final String ENTITLEMENTS = "entitlements";
        public static final String ENTITLEMENT = "entitlement";

        public static final String ROLES = "roles";
        public static final String ROLE = "role";

        public static final String X509CERTIFICATES = "x509Certificates";
        public static final String X509CERTIFICATE = "x509Certificate";

        public static final String FORMATTED_ADDRESS = "formatted";
        public static final String STREET_ADDRESS = "streetAddress";
        public static final String LOCALITY = "locality";
        public static final String REGION = "region";
        public static final String POSTAL_CODE = "postalCode";
        public static final String COUNTRY = "country";

        public static final String DIRECT_MEMBERSHIP = "direct";
        public static final String INDIRECT_MEMBERSHIP = "indirect";
    }

    public static class GroupSchemaConstants {
        public static final String DISPLAY_NAME = "displayName";
        public static final String MEMBERS = "members";
        public static final String MEMBER = "member";
    }

    public static class ListedResourcesConstants {
        public static final String TOTAL_RESULTS = "totalResults";
        public static final String RESOURCES = "Resources";
        public static final String ITEMS_PER_PAGE = "itemsPerPage";
        public static final String START_INDEX = "startIndex";
    }

    public static class EnterpriseUserSchema {
        // public static final String
    }

    public static class AttributeConstants {
        public static final String MULTI_VALUED_ATTRIBUTE_OPERATION = "delete";
    }

    /*Constants found in Service Provider Configuration Schema*/

    //constants related to supported authentication mechanism

    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTH_TYPE_OAUTH = "Bearer";
    public static final String AUTH_PROPERTY_PRIMARY = "primary";

    //authentication header names
    public static final String AUTH_HEADER_USERNAME = "userName";
    public static final String AUTH_HEADER_PASSWORD = "password";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    //if a custom authentication mechanism is used, you can specify it in following header
    public static final String AUTHENTICATION_TYPE_HEADER = "Auth_Type";


    /*Resource names as defined in SCIM Schema spec*/
    public static final String USER = "User";
    public static final String GROUP = "Group";
    public static final String COMMON = "Common";


    /*Resource and attribute descriptions according to SCIM spec.*/

    /**
     * ******Resource Descriptions********************
     */
    public static final String COMMON_DESC = "Each SCIM Resource (Users, Groups, etc.) includes the " +
                                             "common attributes of this schema. These attributes MUST be " +
                                             "included in all Resources, including any extended Resource types";

    public static final String USER_DESC = "SCIM provided schema for representing Users.";
    public static final String GROUP_DESC = "Schema for representing groups.";

    /**
     * ******Attributes Descriptions of the attributes found in Common Schema**************
     */
    public static final String ID_DESC = "Unique identifier for the SCIM Resource as defined by the Service Provider.";

    //*********
    // following descriptions used for bulk endpoint
    public static final String BULK_ID_DESC = "defined by the Service Consumer and must not be used in the unique identifier";
    public static final String PATH_DESC = "The Resource's relative path. If the method is POST the value must specify a Resource type endpoint; " +
                                           "e.g., /Users or /Groups whereas all other method values must specify the path to a specific Resource";
    public static final String METHOD_DESC = "The HTTP method of the current operation. Possible values are POST, PUT, PATCH or DELETE. REQUIRED.";
    //*********

    public static final String EXTERNAL_ID_DESC = "Unique identifier for the Resource as defined by the Service Consumer." +
                                                  "The Service Provider MUST always interpret the externalId as scoped to the Service Consumer's tenant.";
    public static final String CREATED_DESC = "The DateTime the Resource was added to the Service Provider.";
    public static final String LAST_MODIFIED_DESC = "The most recent DateTime the details of this Resource were updated at the Service Provider. " +
                                                    "If this Resource has never been modified since its initial creation, the value MUST be the same as the value of created.";
    public static final String LOCATION_DESC = "The URI of the Resource being returned. This value MUST be the same as the Location HTTP response header.";
    public static final String VERSION_DESC = "The version of the Resource being returned. This value must be the same as the ETag HTTP response header.";


    /**
     * ******Attributes descriptions of the attributes found in User Schema************
     */
    public static final String USER_NAME_DESC = "Unique identifier for the User, typically " +
                                                "used by the user to directly authenticate to the service provider.";

    public static final String EMAILS_DESC = "E-mail addresses for the User.The value SHOULD be canonicalized by the Service Provider,";

    public static final String TYPE_DESC = "A label indicating the attribute's function; e.g., \"work\" or \"home\".";

    public static final String PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred attribute value for this attribute, " +
                                              "e.g. the preferred mailing address or primary e-mail address. " +
                                              "The primary attribute value 'true' MUST appear no more than once";

    public static final String DISPLAY_DESC = "A human readable name, primarily used for display purposes. READ-ONLY.";

    public static final String OPERATION_DESC = "The operation to perform on the multi-valued attribute during a PATCH request. " +
                                                "The only valid value is \"delete\", which signifies that this instance should be removed from the Resource.";

    public static final String VALUE_DESC = "The attribute's significant value; e.g., the e-mail address, phone number, etc. " +
                                            "Attributes that define a \"value\" sub-attribute MAY be alternately represented as a collection of primitive types";
    public static final String ATTRIBUTES_DESC = "The names of the attributes to remove from the Resource during a PATCH operation.";
    public static final String META_DESC = "A complex attribute containing resource metadata.";

    public static final String USER_DISPLAY_NAME_DESC = "The name of the User, suitable for display to end-users.";

    public static final String FORMATTED_NAME_DESC = "The full name, including all middle names, titles, and suffixes as appropriate, formatted for display";
    public static final String FAMILY_NAME_DESC = "The family name of the User, or \"Last Name\" in most Western languages";
    public static final String GIVEN_NAME_DESC = "The given name of the User, or \"First Name\" in most Western languages";
    public static final String MIDDLE_NAME_DESC = "The middle name(s) of the User";
    public static final String HONORIFIC_PREFIX_DESC = "The honorific prefix(es) of the User, or \"Title\" in most Western languages";
    public static final String HONORIFIC_SUFFIX_DESC = "The honorific suffix(es) of the User, or \"Suffix\" in most Western languages";

    public static final String NAME_DESC = "The components of the User's real name.";

    public static final String NICK_NAME_DESC = "The casual way to address the user in real life. " +
                                                "This attribute SHOULD NOT be used to represent a User's username";
    public static final String PROFILE_URL_DESC = "A fully qualified URL to a page representing the User's online profile.";
    public static final String TITLE_DESC = "The user’s title, such as \"Vice President.\"";
    public static final String USER_TYPE_DESC = "Used to identify the organization to user relationship.";
    public static final String PREFERRED_LANGUAGE_DESC = "Indicates the User's preferred written or spoken language.";
    public static final String LOCALE_DESC = "Used to indicate the User's default location.";
    public static final String TIME_ZONE_DESC = "The User's time zone in the \"Olson\" timezone database format";
    public static final String ACTIVE_DESC = "A Boolean value indicating the User's administrative status.";
    public static final String PASSWORD_DESC = "The User's clear text password. This value MUST never be returned by a Service Provider in any form.";

    public static final String PHONE_NUMBERS_DESC = "Phone numbers for the User. Canonical Type values of work, home, mobile, fax, pager and other";
    public static final String IMS_DESC = "Instant messaging address for the User.Canonical Values to represent currently popular IM services: " +
                                          "aim, gtalk, icq, xmpp, msn, skype, qq, and yahoo";
    public static final String PHOTOS_DESC = "URL of a photo of the User. " +
                                             "Canonical Values to represent popular photo sizes: photo, thumbnail";

    public static final String ADDRESSES_DESC = "A physical mailing address for this User";

    public static final String FORMATTED_ADDRESS_DESC = "The full mailing address, " +
                                                        "formatted for display or use with a mailing label";

    public static final String STREET_ADDRESS_DESC = "The full street address component, " +
                                                     "which may include house number, street name, P.O. box, " +
                                                     "and multi-line extended street address information";
    public static final String LOCALITY_DESC = "The city or locality component";
    public static final String REGION_DESC = "The state or region component";
    public static final String POSTAL_CODE_DESC = "The zipcode or postal code component.";
    public static final String COUNTRY_DESC = "The country name component.";

    public static final String USER_GROUP_DESC = "A list of groups that the user belongs to, either thorough direct membership, " +
                                                 "nested groups, or dynamically calculated. The Canonical types " +
                                                 "\"direct\" and \"indirect\" are defined to describe how " +
                                                 "the group membership was derived. READ-ONLY";
    public static final String ENTITLEMENTS_DESC = "A list of entitlements for the User that represent a thing the User has.";
    public static final String ROLES_DESC = "A list of roles for the User that collectively represent who the User is";
    public static final String X509CERTIFICATES_DESC = "A list of certificates issued to the User. Values are Binary and DER encoded x509";


    /**
     * ****************** Attributes descriptions of the attributes found in Group Schema***********
     */
    public static final String DISPLAY_NAME_DESC = "A human readable name for the Group.";
    public static final String MEMBERS_DESC = "A list of members of the Group. The value must be the \"id\" of a SCIM resource, either a User, or a Group. " +
                                              "The intention of the Group type is to allow the Service Provider to support nested Groups.";


    /*Resource endpoints relative to the base SCIM URL*/
    public static final String USER_ENDPOINT = "/Users";
    public static final String GROUP_ENDPOINT = "/Groups";


    //HTTP Headers used in SCIM request/response other than auth headers.
    public static final String LOCATION_HEADER = "Location";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_HEADER = "Accept";

    //integer constants used for identification
    public static final int USER_INT = 1;
    public static final int GROUP_INT = 2;
    public static final int LISTED_RESOURCE_INT = 3;

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int DELETE = 3;
    public static final int PUT = 4;


    /*Attribute URIs for attributes defined in scim-core schema spec.
    * NOTE: No URI for parent attribute of complex attributes.*/

    //for SCIM-Common Schema
    public static final String ID_URI = "urn:scim:schemas:core:1.0:id";
    public static final String EXTERNAL_ID_URI = "urn:scim:schemas:core:1.0:externalId";

    public static final String META_URI = "urn:scim:schemas:core:1.0:meta";
    public static final String META_CREATED_URI = "urn:scim:schemas:core:1.0:meta.created";
    public static final String META_LAST_MODIFIED_URI = "urn:scim:schemas:core:1.0:meta.lastModified";
    public static final String META_LOCATION_URI = "urn:scim:schemas:core:1.0:meta.location";
    public static final String META_VERSION_URI = "urn:scim:schemas:core:1.0:meta.version";
    public static final String META_ATTRIBUTES_URI = "urn:scim:schemas:core:1.0:meta.attributes";

    public static final String BULK_ID = "urn:scim:schemas:core:1.0:bulkId";
    public static final String PATH = "urn:scim:schemas:core:1.0:path";
    public static final String METHOD = "urn:scim:schemas:core:1.0:method";

    //for SCIM-User Schema
    public static final String USER_NAME_URI = "urn:scim:schemas:core:1.0:userName";

    public static final String NAME_URI = "urn:scim:schemas:core:1.0:name";
    public static final String NAME_GIVEN_NAME_URI = "urn:scim:schemas:core:1.0:name.givenName";
    public static final String NAME_FAMILY_NAME_URI = "urn:scim:schemas:core:1.0:name.familyName";
    public static final String NAME_FORMATTED_NAME_URI = "urn:scim:schemas:core:1.0:name.formatted";
    public static final String NAME_MIDDLE_NAME_URI = "urn:scim:schemas:core:1.0:name.middleName";
    public static final String NAME_HONORIC_PREFIX_URI = "urn:scim:schemas:core:1.0:name.honorificPrefix";
    public static final String NAME_HONORIC_SUFFIX_URI = "urn:scim:schemas:core:1.0:name.honorificSuffix";

    public static final String DISPLAY_NAME_URI = "urn:scim:schemas:core:1.0:displayName";
    public static final String NICK_NAME_URI = "urn:scim:schemas:core:1.0:nickName";
    public static final String PROFILE_URL_URI = "urn:scim:schemas:core:1.0:profileUrl";
    public static final String TITLE_URI = "urn:scim:schemas:core:1.0:title";
    public static final String USER_TYPE_URI = "urn:scim:schemas:core:1.0:userType";
    public static final String PREFERRED_LANGUAGE_URI = "urn:scim:schemas:core:1.0:preferredLanguage";
    public static final String LOCAL_URI = "urn:scim:schemas:core:1.0:local";
    public static final String TIMEZONE_URI = "urn:scim:schemas:core:1.0:timeZone";
    public static final String ACTIVE_URI = "urn:scim:schemas:core:1.0:active";
    public static final String PASSWORD_URI = "urn:scim:schemas:core:1.0:password";

    public static final String EMAILS_URI = "urn:scim:schemas:core:1.0:emails";
    public static final String WORK_EMAIL_URI = "urn:scim:schemas:core:1.0:emails.work";
    public static final String HOME_EMAIL_URI = "urn:scim:schemas:core:1.0:emails.home";
    public static final String OTHER_EMAIL_URI = "urn:scim:schemas:core:1.0:emails.other";

    public static final String PHONE_NUMBERS_URI = "urn:scim:schemas:core:1.0:phoneNumbers";
    public static final String PHONE_NUMBERS_MOBILE_URI = "urn:scim:schemas:core:1.0:phoneNumbers.mobile";
    public static final String PHONE_NUMBERS_HOME_URI = "urn:scim:schemas:core:1.0:phoneNumbers.home";
    public static final String PHONE_NUMBERS_WORK_URI = "urn:scim:schemas:core:1.0:phoneNumbers.work";
    public static final String PHONE_NUMBERS_FAX_URI = "urn:scim:schemas:core:1.0:phoneNumbers.fax";
    public static final String PHONE_NUMBERS_PAGER_URI = "urn:scim:schemas:core:1.0:phoneNumbers.pager";
    public static final String PHONE_NUMBERS_OTHER_URI = "urn:scim:schemas:core:1.0:phoneNumbers.other";

    public static final String IMS_URI = "urn:scim:schemas:core:1.0:ims";
    public static final String GTALK_IM_URI = "urn:scim:schemas:core:1.0:ims.gtalk";
    public static final String SKYPE_IM_URI = "urn:scim:schemas:core:1.0:ims.skype";

    public static final String PHOTOS_URI = "urn:scim:schemas:core:1.0:photos";
    public static final String PHOTOS_PHOTO_URI = "urn:scim:schemas:core:1.0:photos.photo";
    public static final String PHOTOS_THUMBNAIL_URI = "urn:scim:schemas:core:1.0:photos.thumbnail";

    public static final String ADDRESSES_URI = "urn:scim:schemas:core:1.0:addresses";
    public static final String ADDRESSES_FORMATTED_URI = "urn:scim:schemas:core:1.0:addresses.formatted";
    public static final String ADDRESSES_STREET_ADDRESS_URI = "urn:scim:schemas:core:1.0:addresses.streetAddress";
    public static final String ADDRESSES_LOCALITY_URI = "urn:scim:schemas:core:1.0:addresses.locality";
    public static final String ADDRESSES_REGION_URI = "urn:scim:schemas:core:1.0:addresses.region";
    public static final String ADDRESSES_POSTALCODE_URI = "urn:scim:schemas:core:1.0:addresses.postalCode";
    public static final String ADDRESSES_COUNTRY_URI = "urn:scim:schemas:core:1.0:addresses.country";

    public static final String GROUPS_URI = "urn:scim:schemas:core:1.0:groups";

    public static final String ENTITLEMENTS_URI = "urn:scim:schemas:core:1.0:entitlements";
    public static final String ROLES_URI = "urn:scim:schemas:core:1.0:roles";
    public static final String X509CERTIFICATES_URI = "urn:scim:schemas:core:1.0:x509Certificates";


}
