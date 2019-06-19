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

/**
 * This defines the constants which can be found in SCIM 2.0 core schema can be found at. :
 * https://tools.ietf.org/html/rfc7643
 */
public class SCIMConstants {

    public static final String CORE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0";
    public static final String USER_CORE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0:User";
    public static final String ENTERPRISE_USER_SCHEMA_URI = "urn:ietf:params:scim:schemas:extension:enterprise:2" +
            ".0:User";
    public static final String GROUP_CORE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0:Group";
    public static final String LISTED_RESOURCE_CORE_SCHEMA_URI = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    public static final String MESSAGE_SCHEMA_URI = "urn:ietf:params:scim:api:messages:2.0";

    public static final String SERVICE_PROVIDER_CONFIG_SCHEMA_URI =
            "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";
    public static final String RESOURCE_TYPE_SCHEMA_URI = CORE_SCHEMA_URI + ":ResourceType";
    public static final String SEARCH_SCHEMA_URI = MESSAGE_SCHEMA_URI + ":SearchRequest";
    public static final String BULK_REQUEST_URI = MESSAGE_SCHEMA_URI + ":BulkRequest";
    public static final String BULK_RESPONSE_URI = MESSAGE_SCHEMA_URI + ":BulkResponse";
    public static final String SCHEMA_URI = CORE_SCHEMA_URI + ":Schema";


    /*Data formats*/
    public static final String JSON = "json";

    public static final String APPLICATION_JSON = "application/scim+json";
    @Deprecated
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    @Deprecated
    public static final String DATE_TIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss";

    /*Resource names as defined in SCIM Schema spec*/
    public static final String USER = "User";
    public static final String USER_DESC = "User Account";
    public static final String GROUP = "Group";
    public static final String RESOURCE_TYPE = "ResourceType";
    public static final String ENTERPRISE_USER = "EnterpriseUser";
    public static final String ENTERPRISE_USER_DESC = "Enterprise User";

    /*Resource endpoints relative to the base SCIM URL*/
    public static final String USER_ENDPOINT = "/Users";
    public static final String GROUP_ENDPOINT = "/Groups";
    public static final String ME_ENDPOINT = "/Me";
    public static final String SERVICE_PROVIDER_CONFIG_ENDPOINT = "/ServiceProviderConfig";
    public static final String RESOURCE_TYPE_ENDPOINT = "/ResourceTypes";
    public static final String SCHEMAS_ENDPOINT = "/Schemas";
    public static final String USER_SCHEMA_ENDPOINT = "/" + USER_CORE_SCHEMA_URI;
    public static final String ENTERPRISE_USER_SCHEMA_ENDPOINT = "/" + ENTERPRISE_USER_SCHEMA_URI;
    public static final String GROUP_SCHEMA_ENDPOINT = "/" + GROUP_CORE_SCHEMA_URI;


    //HTTP Headers used in SCIM request/response other than auth headers.
    public static final String LOCATION_HEADER = "Location";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String DEFAULT = "default";


    /**
     * Constants found in core-common schema.
     */
    public static class CommonSchemaConstants {

        public static final String SCHEMAS = "schemas";
        public static final String SCHEMAS_FIELD_URI = CORE_SCHEMA_URI + ":" + SCHEMAS;
        public static final String SCHEMAS_FIELD_DESC = "a list of the core schema and the schema extensions that " +
            "represent the current schema definiton";
        public static final String ID = "id";
        public static final String ID_URI = CORE_SCHEMA_URI + ":id";
        public static final String EXTERNAL_ID = "externalId";
        public static final String EXTERNAL_ID_URI = CORE_SCHEMA_URI + ":externalId";
        public static final String META = "meta";
        public static final String META_URI = CORE_SCHEMA_URI + ":meta";
        public static final String RESOURCE_TYPE = "resourceType";
        public static final String RESOURCE_TYPE_URI = CORE_SCHEMA_URI + ":meta.resourceType";
        public static final String CREATED = "created";
        public static final String CREATED_URI = CORE_SCHEMA_URI + ":meta.created";
        public static final String LAST_MODIFIED = "lastModified";
        public static final String LAST_MODIFIED_URI = CORE_SCHEMA_URI + ":meta.lastModified";
        public static final String LOCATION = "location";
        public static final String LOCATION_URI = CORE_SCHEMA_URI + ":meta.location";
        public static final String VERSION = "version";
        public static final String VERSION_URI = CORE_SCHEMA_URI + ":meta.version";
        public static final String TOTAL_RESULTS = "totalResults";
        //characteristics of multi valued attribute
        public static final String TYPE = "type";
        public static final String PRIMARY = "primary";
        public static final String DISPLAY = "display";
        public static final String REF = "$ref";
        public static final String VALUE = "value";

        /* ******Attributes descriptions of the attributes found in Common Schema.************** */

        public static final String ID_DESC = "Unique identifier for the SCIM Resource as defined by the Service " +
            "Provider.";
        public static final String EXTERNAL_ID_DESC = "A String that is an identifier for the resource as defined by " +
            "the provisioning client." +
            "The service provider MUST always interpret the externalId as scoped to the provisioning domain.";
        public static final String META_DESC = "A complex attribute containing resource metadata.";
        public static final String RESOURCE_TYPE_DESC = "The name of the resource type of the resource.";
        public static final String CREATED_DESC = "The \"DateTime\" that the resource was added to the service " +
            "provider.";
        public static final String LAST_MODIFIED_DESC = "The most recent DateTime that the details of this resource " +
            "were updated at the service provider.";
        public static final String LOCATION_DESC = "Location  The uri of the resource being returned";
        public static final String VERSION_DESC = "The version of the resource being returned.";

    }

    /**
     * Constants found in listed resource schema.
     */
    public static class ListedResourceSchemaConstants {

        public static final String TOTAL_RESULTS = "totalResults";
        public static final String TOTAL_RESULTS_URI = LISTED_RESOURCE_CORE_SCHEMA_URI + ":" + TOTAL_RESULTS;
        public static final String TOTAL_RESULTS_DESC = " The total number of results returned by the list or query " +
            "operation.";

        public static final String START_INDEX = "startIndex";
        public static final String START_INDEX_URI = LISTED_RESOURCE_CORE_SCHEMA_URI + ":" + START_INDEX;
        public static final String START_INDEX_DESC = " The 1-based index of the first result in the current set " +
            "of list results.";

        public static final String ITEMS_PER_PAGE = "itemsPerPage";
        public static final String ITEMS_PER_PAGE_URI = LISTED_RESOURCE_CORE_SCHEMA_URI + ":" + ITEMS_PER_PAGE;
        public static final String ITEMS_PER_PAGE_DESC = "The number of resources returned in a list response " +
            "page.";

        public static final String RESOURCES = "Resources";
        public static final String RESOURCES_URI = LISTED_RESOURCE_CORE_SCHEMA_URI + ":" + RESOURCES;
        public static final String RESOURCES_DESC = "A multi-valued list of complex objects containing the " +
            "requested resources.  ";
    }

    /**
     * Constants found in user schema.
     */
    public static class UserSchemaConstants {

        public static final String USER_NAME = "userName";
        public static final String USER_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":userName";
        public static final String NAME = "name";
        public static final String NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":name";
        public static final String FORMATTED_NAME = "formatted";
        public static final String FORMATTED_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":name.formatted";
        public static final String FAMILY_NAME = "familyName";
        public static final String FAMILY_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":name.familyName";
        public static final String GIVEN_NAME = "givenName";
        public static final String GIVEN_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":name.givenName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String MIDDLE_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":name.middleName";
        public static final String HONORIFIC_PREFIX = "honorificPrefix";
        public static final String HONORIFIC_PREFIX_URI = CORE_SCHEMA_URI + ":" + USER + ":name" +
            ".honorificPrefix";
        public static final String HONORIFIC_SUFFIX = "honorificSuffix";
        public static final String HONORIFIC_SUFFIX_URI = CORE_SCHEMA_URI + ":" + USER + ":name" +
            ".honorificSuffix";

        public static final String DISPLAY_NAME = "displayName";
        public static final String DISPLAY_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":displayName";
        public static final String NICK_NAME = "nickName";
        public static final String NICK_NAME_URI = CORE_SCHEMA_URI + ":" + USER + ":nickName";
        public static final String PROFILE_URL = "profileUrl";
        public static final String PROFILE_URL_URI = CORE_SCHEMA_URI + ":" + USER + ":profileUrl";
        public static final String TITLE = "title";
        public static final String TITLE_URI = CORE_SCHEMA_URI + ":" + USER + ":title";
        public static final String USER_TYPE = "userType";
        public static final String USER_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":userType";
        public static final String PREFERRED_LANGUAGE = "preferredLanguage";
        public static final String PREFERRED_LANGUAGE_URI =
            CORE_SCHEMA_URI + ":" + USER + ":preferredLanguage";
        public static final String LOCALE = "locale";
        public static final String LOCALE_URI = CORE_SCHEMA_URI + ":" + USER + ":locale";
        public static final String TIME_ZONE = "timezone";
        public static final String TIME_ZONE_URI = CORE_SCHEMA_URI + ":" + USER + ":timezone";
        public static final String ACTIVE = "active";
        public static final String ACTIVE_URI = CORE_SCHEMA_URI + ":" + USER + ":active";
        public static final String PASSWORD = "password";
        public static final String PASSWORD_URI = CORE_SCHEMA_URI + ":" + USER + ":password";

        public static final String FORMATTED_ADDRESS = "formatted";
        public static final String FORMATTED_ADDRESS_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses" +
            ".formatted";
        public static final String STREET_ADDRESS = "streetAddress";
        public static final String STREET_ADDRESS_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses" +
            ".streetAddress";
        public static final String LOCALITY = "locality";
        public static final String LOCALITY_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses.locality";
        public static final String REGION = "region";
        public static final String REGION_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses.region";
        public static final String POSTAL_CODE = "postalCode";
        public static final String POSTAL_CODE_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses.postalCode";
        public static final String COUNTRY = "country";
        public static final String COUNTRY_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses.country";

        //Multi-Valued Attributes
        public static final String EMAILS = "emails";
        public static final String EMAILS_URI = CORE_SCHEMA_URI + ":" + USER + ":emails";
        public static final String PHONE_NUMBERS = "phoneNumbers";
        public static final String PHONE_NUMBERS_URI = CORE_SCHEMA_URI + ":" + USER + ":phoneNumbers";
        public static final String IMS = "ims";
        public static final String IMS_URI = CORE_SCHEMA_URI + ":" + USER + ":ims";
        public static final String PHOTOS = "photos";
        public static final String PHOTOS_URI = CORE_SCHEMA_URI + ":" + USER + ":photos";
        public static final String ADDRESSES = "addresses";
        public static final String ADDRESSES_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses";
        public static final String GROUPS = "groups";
        public static final String GROUP_URI = CORE_SCHEMA_URI + ":" + USER + ":groups";
        public static final String ENTITLEMENTS = "entitlements";
        public static final String ENTITLEMENTS_URI = CORE_SCHEMA_URI + ":" + USER + ":entitlements";
        public static final String ROLES = "roles";
        public static final String ROLES_URI = CORE_SCHEMA_URI + ":" + USER + ":roles";
        public static final String X509CERTIFICATES = "x509Certificates";
        public static final String X509CERTIFICATES_URI = CORE_SCHEMA_URI + ":" + USER + ":x509Certificates";

        //possible canonical values
        public static final String HOME = "home";
        public static final String WORK = "work";
        public static final String OTHER = "other";
        public static final String MOBILE = "mobile";
        public static final String FAX = "fax";
        public static final String PAGER = "pager";

        public static final String SKYPE = "skpye";
        public static final String YAHOO = "yahoo";
        public static final String AIM = "aim";
        public static final String GTALK = "gtalk";
        public static final String ICQ = "icq";
        public static final String XMPP = "xmpp";
        public static final String MSN = "msn";
        public static final String QQ = "qq";

        public static final String PHOTO = "photo";
        public static final String THUMBNAIL = "thumbnail";

        public static final String DIRECT_MEMBERSHIP = "direct";
        public static final String INDIRECT_MEMBERSHIP = "indirect";

        /* ******URIs of sub and multivalued attributes.************* */

        public static final String EMAILS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":emails.value";
        public static final String EMAILS_DISPLAY_URI = CORE_SCHEMA_URI + ":" + USER + ":emails.display";
        public static final String EMAILS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":emails.type";
        public static final String EMAILS_PRIMARY_URI = CORE_SCHEMA_URI + ":" + USER + ":emails.primary";

        public static final String PHONE_NUMBERS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":phoneNumbers" +
            ".value";
        public static final String PHONE_NUMBERS_DISPLAY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":phoneNumbers.display";
        public static final String PHONE_NUMBERS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":phoneNumbers" +
            ".type";
        public static final String PHONE_NUMBERS_PRIMARY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":phoneNumbers.primary";

        public static final String IMS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":ims.value";
        public static final String IMS_DISPLAY_URI = CORE_SCHEMA_URI + ":" + USER + ":ims.display";
        public static final String IMS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":ims.type";
        public static final String IMS_PRIMARY_URI = CORE_SCHEMA_URI + ":" + USER + ":ims.primary";

        public static final String PHOTOS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":photos.value";
        public static final String PHOTOS_DISPLAY_URI = CORE_SCHEMA_URI + ":" + USER + ":photos.display";
        public static final String PHOTOS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":photos.type";
        public static final String PHOTOS_PRIMARY_URI = CORE_SCHEMA_URI + ":" + USER + ":photos.primary";

        public static final String ADDRESSES_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses.type";
        public static final String ADDRESSES_PRIMARY_URI = CORE_SCHEMA_URI + ":" + USER + ":addresses" +
            ".primary";

        public static final String GROUPS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":groups.value";
        public static final String GROUPS_DISPLAY_URI = CORE_SCHEMA_URI + ":" + USER + ":groups.display";
        public static final String GROUPS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":groups.type";
        public static final String GROUPS_REF_URI = CORE_SCHEMA_URI + ":" + USER + ":groups.$ref";

        public static final String ROLES_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":roles.value";
        public static final String ROLES_DISPLAY_URI = CORE_SCHEMA_URI + ":" + USER + ":roles.display";
        public static final String ROLES_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":roles.type";
        public static final String ROLES_PRIMARY_URI = CORE_SCHEMA_URI + ":" + USER + ":roles.primary";

        public static final String ENTITLEMENTS_VALUE_URI = CORE_SCHEMA_URI + ":" + USER + ":entitlements" +
            ".value";
        public static final String ENTITLEMENTS_DISPLAY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":entitlements.display";
        public static final String ENTITLEMENTS_TYPE_URI = CORE_SCHEMA_URI + ":" + USER + ":entitlements" +
            ".type";
        public static final String ENTITLEMENTS_PRIMARY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":entitlements.primary";

        public static final String X509CERTIFICATES_VALUE_URI =
            CORE_SCHEMA_URI + ":" + USER + ":x509certificates.value";
        public static final String X509CERTIFICATES_DISPLAY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":x509certificates.display";
        public static final String X509CERTIFICATES_TYPE_URI =
            CORE_SCHEMA_URI + ":" + USER + ":x509certificates.type";
        public static final String X509CERTIFICATES_PRIMARY_URI =
            CORE_SCHEMA_URI + ":" + USER + ":x509certificates.primary";

        /* ******Attributes descriptions of the attributes found in User Schema.************** */

        public static final String USERNAME_DESC = "A service provider's unique identifier for the user, typically\n" +
            "used by the user to directly authenticate to the service provider.Each User MUST include a non-empty" +
            " userName value.  This identifier\n" +
            "MUST be unique across the service provider's entire set of Users.";

        public static final String NAME_DESC = "The components of the user's real name.Providers MAY return just the " +
            "full name as a single string in the\n" +
            "formatted sub-attribute, or they MAY return just the individual component attributes using the other" +
            " sub-attributes, or they MAY\n" +
            "return both.  If both variants are returned, they SHOULD be describing the same name, with the " +
            "formatted name indicating how the\n" +
            "component attributes should be combined.";

        public static final String FORMATTED_NAME_DESC = "The full name, including all middle names, titles, and " +
            "suffixes as appropriate, formatted for display\n" +
            "(e.g., 'Ms. Barbara J Jensen, III').";
        public static final String FAMILY_NAME_DESC = "The family name of the User, or last name in most Western " +
            "languages (e.g., 'Jensen' given the full\n" +
            "name 'Ms. Barbara J Jensen, III').";
        public static final String GIVEN_NAME_DESC = "The given name of the User, or first name in most Western " +
            "languages (e.g., 'Barbara' given the\n" +
            "full name 'Ms. Barbara J Jensen, III').";
        public static final String MIDDLE_NAME_DESC = "The middle name(s) of the User (e.g., 'Jane' given the full " +
            "name 'Ms. Barbara J Jensen, III').";
        public static final String HONORIFIC_PREFIX_DESC = "The honorific prefix(es) of the User, or title in most " +
            "Western languages (e.g., 'Ms.' given the full name\n" +
            "'Ms. Barbara J Jensen, III').";
        public static final String HONORIFIC_SUFFIX_DESC = "The honorific suffix(es) of the User, or suffix in most " +
            "Western languages (e.g., 'III' given the full name\n" +
            "'Ms. Barbara J Jensen, III').";

        public static final String DISPLAY_NAME_DESC = "The name of the User, suitable for display\n" +
            "to end-users.  The name SHOULD be the full name of the User being described, if known.";
        public static final String NICK_NAME_DESC = "The casual way to address the user in real life, e.g., 'Bob' or " +
            "'Bobby' instead of 'Robert'.  This attribute\n" +
            "SHOULD NOT be used to represent a User's username (e.g., 'bjensen' or 'mpepperidge').";
        public static final String PROFILE_URL_DESC = "A fully qualified URL pointing to a page\n" +
            "representing the User's online profile.";
        public static final String TITLE_DESC = "The user's title, such as \\\"Vice President.\\\"";
        public static final String USER_TYPE_DESC = "Used to identify the relationship between the organization and " +
            "the user.  Typical values used might be\n" +
            "'Contractor', 'Employee', 'Intern', 'Temp', 'External', and 'Unknown', but any value may be used.";
        public static final String PREFERRED_LANGUAGE_DESC = "Indicates the User's preferred written or\n" +
            "spoken language.  Generally used for selecting a localized user interface; e.g., 'en_US' specifies " +
            "the language English and country";
        public static final String LOCALE_DESC = "Used to indicate the User's default location\n" +
            "for purposes of localizing items such as currency, date time format, or numerical representations.";
        public static final String TIME_ZONE_DESC = "The User's time zone in the 'Olson' time zone\n" +
            "database format, e.g., 'America/Los_Angeles'.";
        public static final String ACTIVE_DESC = "A Boolean value indicating the User's administrative status.";
        public static final String PASSWORD_DESC = "The User's cleartext password.  This attribute is intended to be " +
            "used as a means to specify an initial\n" +
            "password when creating a new User or to reset an existing User's password.";

        public static final String EMAILS_DESC = "Email addresses for the user.  The value SHOULD be canonicalized by" +
            " the service provider, e.g.,\n" +
            "'bjensen@example.com' instead of 'bjensen@EXAMPLE.COM'.Canonical type values of 'work', 'home', and " +
            "'other'.";
        public static final String EMAIL_VALUE_DESC = "Email addresses for the user.  The value SHOULD be " +
            "canonicalized by the service provider, e.g.,\n" +
            "'bjensen@example.com' instead of 'bjensen@EXAMPLE.COM'.Canonical type values of 'work', 'home', and " +
            "'other'.";
        public static final String EMAIL_DISPLAY_DESC = "A human-readable name, primarily used for display purposes. " +
            " READ-ONLY.";
        public static final String EMAIL_TYPE_DESC = "A label indicating the attribute's function, e.g., 'work' or " +
            "'home'.";
        public static final String EMAIL_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred " +
            "attribute value for this attribute, " +
            "e.g., the psreferred mailing address or primary email address.  The primary attribute value 'true' " +
            "MUST appear no more than once.";

        public static final String PHONE_NUMBERS_DESC = "Phone numbers for the User.  The value SHOULD be " +
            "canonicalized by the service provider according to the\n" +
            "format specified in RFC 3966, e.g., 'tel:+1-201-555-0123'.Canonical type values of 'work', 'home', " +
            "'mobile', 'fax', 'pager";
        public static final String PHONE_NUMBERS_VALUE_DESC = "Phone number of the User.";
        public static final String PHONE_NUMBERS_DISPLAY_DESC = "A human-readable name, primarily used for display " +
            "purposes.  READ-ONLY.";
        public static final String PHONE_NUMBERS_TYPE_DESC = "A label indicating the attribute's function, e.g., " +
            "'work', 'home', 'mobile'.";
        public static final String PHONE_NUMBERS_PRIMARY_DESC = "A Boolean value indicating the 'primary' or " +
            "preferred attribute value for this attribute, e.g., the preferred\n" +
            "phone number or primary phone number.  The primary attribute value 'true' MUST appear no more than " +
            "once.";

        public static final String IMS_DESC = "Instant messaging addresses for the User.";
        public static final String IMS_VALUE_DESC = "Instant messaging address for the User.";
        public static final String IMS_DISPLAY_DESC = "A human-readable name, primarily used for display purposes.  " +
            "READ-ONLY.";
        public static final String IMS_TYPE_DESC = "A label indicating the attribute's function, e.g., 'aim', " +
            "'gtalk', 'xmpp'.";
        public static final String IMS_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred " +
            "attribute value for this attribute, e.g., the preferred\n" +
            "messenger or primary messenger.  The primary attribute value 'true' MUST appear no more than once.";

        public static final String PHOTOS_DESC = "URLs of photos of the User.";
        public static final String PHOTOS_VALUE_DESC = "URLs of photos of the User.";
        public static final String PHOTOS_DISPLAY_DESC = "A human-readable name, primarily used for display purposes." +
            "  READ-ONLY.";
        public static final String PHOTOS_TYPE_DESC = "A label indicating the attribute's function, i.e., 'photo' or " +
            "'thumbnail'.";
        public static final String PHOTOS_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred " +
            "attribute value for this attribute, e.g., the preferred\n" +
            "phone number or primary phone number. The primary attribute value 'true' MUST appear no more than " +
            "once.";

        public static final String ADDRESSES_DESC = "A physical mailing address for this User.\n" +
            "Canonical type values of 'work', 'home', and 'other'.  This attribute is a complex type with the " +
            "following sub-attributes.";
        public static final String ADDRESSES_FORMATTED_DESC = "The full mailing address, formatted for display or use" +
            " with a mailing label.  This attribute MAY contain\n" +
            "newlines.";
        public static final String ADDRESSES_STREET_ADDRESS_DESC = "The full street address component, which may " +
            "include house number, street name, P.O. box, and multi-line\n" +
            "extended street address information.  This attribute MAY contain newlines.";
        public static final String ADDRESSES_LOCALITY_DESC = "The city or locality component.";
        public static final String ADDRESSES_REGION_DESC = "The state or region component.";
        public static final String ADDRESSES_POSTAL_CODE_DESC = "The zip code or postal code component.";
        public static final String ADDRESSES_COUNTRY_DESC = "The country name component.";
        public static final String ADDRESSES_TYPE_DESC = "A label indicating the attribute's function, e.g., 'work' " +
            "or 'home'.";
        public static final String ADDRESSES_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred " +
            "attribute value for this attribute.  The primary\n" +
            "attribute value 'true' MUST appear no more than once.";

        public static final String GROUPS_DESC = "A list of groups to which the user belongs,\n" +
            "either through direct membership, through nested groups, or dynamically calculated.";
        public static final String GROUP_VALUE_DESC = "The identifier of the User's group.";
        public static final String GROUP_DISPLAY_DESC = "A human-readable name, primarily used for display purposes. " +
            "READ-ONLY.";
        public static final String GROUP_REF_DESC = "The uri of the corresponding 'Group' resource to which the user" +
            " belongs.";
        public static final String GROUP_TYPE_DESC = "A label indicating the attribute's function, e.g., 'direct' or " +
            "'indirect'.";

        public static final String ENTITLEMENTS_DESC = "A list of entitlements for the User that represent a thing " +
            "the User has.";
        public static final String ENTITLEMENTS_VALUE_DESC = "The value of an entitlement.";
        public static final String ENTITLEMENTS_DISPLAY_DESC = "A human-readable name, primarily used for display " +
            "purposes.  READ-ONLY.";
        public static final String ENTITLEMENTS_TYPE_DESC = "A label indicating the attribute's function.";
        public static final String ENTITLEMENTS_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred" +
            " attribute value for this attribute.  The primary\n" +
            "attribute value 'true' MUST appear no more than once.";

        public static final String ROLES_DESC = "A list of roles for the User that collectively represent who the " +
            "User is, e.g., 'Student', 'Faculty'.";
        public static final String ROLES_VALUE_DESC = "The value of a role.";
        public static final String ROLES_DISPLAY_DESC = "A human-readable name, primarily used for display purposes. " +
            " READ-ONLY.";
        public static final String ROLES_TYPE_DESC = "A label indicating the attribute's function.";
        public static final String ROLES_PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred " +
            "attribute value for this attribute.  The primary attribute value 'true' MUST appear no more than " +
            "once.";

        public static final String X509CERTIFICATES_DESC = "A list of certificates issued to the User.";
        public static final String X509CERTIFICATES_VALUE_DESC = "The value of an X.509 certificate.";
        public static final String X509CERTIFICATES_DISPLAY_DESC = "A human-readable name, primarily used for display" +
            " purposes.  READ-ONLY.";
        public static final String X509CERTIFICATES_TYPE_DESC = "A label indicating the attribute's function.";
        public static final String X509CERTIFICATES_PRIMARY_DESC = "A Boolean value indicating the 'primary' or " +
            "preferred attribute value for this attribute." +
            "The primary attribute value 'true' MUST appear no more than once.";
    }

    /**
     * represents the constants for the enterprise user schema.
     */
    public static class EnterpriseUserSchemaConstants {

        public static final String EMPLOYEE_NUMBER = "employeeNumber";
        public static final String EMPLOYEE_NUMBER_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + EMPLOYEE_NUMBER;
        public static final String EMPLOYEE_NUMBER_DESC = "Numeric or alphanumeric identifier assigned to a " +
                "person, typically based on order of hire or association with an organization.";

        public static final String COST_CENTER = "costCenter";
        public static final String COST_CENTER_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + COST_CENTER;
        public static final String COST_CENTER_DESC = "Identifies the name of a cost center.";

        public static final String ORGANIZATION = "organization";
        public static final String ORGANIZATION_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + ORGANIZATION;
        public static final String ORGANIZATION_DESC = "Identifies the name of an organization.";

        public static final String DIVISION = "division";
        public static final String DIVISION_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + DIVISION;
        public static final String DIVISION_DESC = "Identifies the name of a division.";

        public static final String DEPARTMENT = "department";
        public static final String DEPARTMENT_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + DEPARTMENT;
        public static final String DEPARTMENT_DESC = "Identifies the name of a department.";

        public static final String VALUE = "value";
        public static final String VALUE_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + VALUE;
        public static final String VALUE_DESC = "The id of the SCIM resource representing the User's manager.  " +
                "REQUIRED.";

        public static final String REF = "$ref";
        public static final String REF_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + REF;
        public static final String REF_DESC = "The URI of the SCIM resource representing the User's manager.  " +
                "REQUIRED.";

        public static final String DISPLAY_NAME = "displayName";
        public static final String DISPLAY_NAME_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + DISPLAY_NAME;
        public static final String DISPLAY_NAME_DESC = "The displayName of the User's manager. " +
                "OPTIONAL and READ-ONLY.";

        public static final String MANAGER = "manager";
        public static final String MANAGER_URI = ENTERPRISE_USER_SCHEMA_URI + ":" + MANAGER;
        public static final String MANAGER_DESC = "The User's manager.  A complex type that optionally " +
                "allows service providers to represent organizational hierarchy by referencing the 'id' " +
                "attribute of another User.";


    }

    /**
     * Constants found in group schema.
     */
    public static class GroupSchemaConstants {

        public static final String DISPLAY_NAME = "displayName";
        public static final String DISPLAY_NAME_URI = CORE_SCHEMA_URI + ":" + GROUP + ":displayName";
        public static final String MEMBERS = "members";
        public static final String MEMBERS_URI = CORE_SCHEMA_URI + ":" + GROUP + ":members";
        public static final String DISPLAY = "display";
        public static final String TYPE = "type";

        /* ******Attributes descriptions of the attributes found in Group Schema.************** */

        public static final String DISPLAY_NAME_DESC = "A human-readable name for the Group. REQUIRED.";
        public static final String MEMBERS_DESC = "A list of members of the Group.";
        public static final String VALUE_DESC = "Identifier of the member of this Group.";
        public static final String REF_DESC = "The uri corresponding to a SCIM resource that is a member of this " +
            "Group.";
        public static final String DISPLAY_DESC = "A human-readable name for the Member";
        public static final String TYPE_DESC = "A label indicating the type of resource, e.g. 'User' or 'Group'";

        /* ******URIs of sub and multivalued attributes.************* */
        public static final String VALUE_URI = CORE_SCHEMA_URI + ":" + GROUP + ":members.value";
        public static final String REF_URI = CORE_SCHEMA_URI + ":" + GROUP + ":members.$ref";
        public static final String DISPLAY_URI = CORE_SCHEMA_URI + ":" + GROUP + ":members.display";
        public static final String TYPE_URI = CORE_SCHEMA_URI + ":" + GROUP + ":members.type";
    }

    /**
     * Constants found in Service Provider Config schema.
     */
    public static class ServiceProviderConfigSchemaConstants {

        public static final String SERVICE_PROVIDER_CONFIG = "ServiceProviderConfig";
        public static final String DOCUMENTATION_URI = "documentationUri";
        public static final String DOCUMENTATION_URI_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":documentationUri";
        public static final String PATCH = "patch";
        public static final String PATCH_URI = CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":patch";
        public static final String BULK = "bulk";
        public static final String BULK_URI = CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":bulk";
        public static final String FILTER = "filter";
        public static final String FILTER_URI = CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":filter";
        public static final String CHANGE_PASSWORD = "changePassword";
        public static final String CHANGE_PASSWORD_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":changePassword";
        public static final String SORT = "sort";
        public static final String SORT_URI = CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":sort";
        public static final String ETAG = "etag";
        public static final String ETAG_URI = CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":etag";
        public static final String AUTHENTICATION_SCHEMAS = "authenticationSchemes";
        public static final String AUTHENTICATION_SCHEMAS_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes";
        public static final String SUPPORTED = "supported";
        public static final String MAX_OPERATIONS = "maxOperations";
        public static final String MAX_OPERATIONS_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":bulk.maxOperations";
        public static final String MAX_PAYLOAD_SIZE = "maxPayloadSize";
        public static final String MAX_PAYLOAD_SIZE_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":bulk.maxPayloadSize";
        public static final String MAX_RESULTS = "maxResults";
        public static final String MAX_RESULTS_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":filter.maxResults";
        public static final String NAME = "name";
        public static final String NAME_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.name";
        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.description";
        public static final String SPEC_URI = "specUri";
        public static final String SPEC_URI_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.specUri";
        public static final String TYPE = "type";
        public static final String TYPE_URL =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.type";
        public static final String PRIMARY = "primary";
        public static final String PRIMARY_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.primary";

        public static final String PATCH_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":patcg.supported";
        public static final String BULK_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":bulk.supported";
        public static final String FILTER_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":filter.supported";
        public static final String CHANGE_PASSWORD_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":changePassword.supported";
        public static final String SORT_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":sort.supported";
        public static final String ETAG_SUPPORTED_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":etag.supported";
        public static final String AUTHENTICATION_SCHEMAS_DOCUMENTATION_URI_URI =
            CORE_SCHEMA_URI + ":" + SERVICE_PROVIDER_CONFIG + ":authenticationSchemes.documentationUri";


        /* ******Attributes descriptions of the attributes found in Service Provider Config Schema.************** */

        public static final String DOCUMENTATION_URI_DESC = "An HTTP-addressable URL pointing to the service " +
            "provider's human-consumable help documentation.";
        public static final String PATCH_DESC = "A complex type that specifies PATCH configuration options.";
        public static final String BULK_DESC = "A complex type that specifies bulk configuration options.";
        public static final String FILTERS_DESC = "A complex type that specifies FILTER options.";
        public static final String CHANGE_PASSWORD_DESC = "A complex type that specifies configuration options " +
            "related to changing a password.";
        public static final String SORT_DESC = "A complex type that specifies sort result options.";
        public static final String ETAG_DESC = "The version of the resources";
        public static final String AUTHENTICATION_SCHEMAS_DESC = "A complex type that specifies supported " +
            "authentication scheme properties.";
        public static final String SUPPORTED_DESC = "A Boolean value specifying whether or not the operation is " +
            "supported.";
        public static final String MAX_OPERATIONS_DESC = "An integer value specifying the maximum number of " +
            "operations.";
        public static final String MAX_PAYLOAD_SIZE_DESC = "An integer value specifying the maximum payload size in " +
            "bytes.";
        public static final String MAX_RESULTS_DESC = "An integer value specifying the maximum number of resources " +
            "returned in a response.";
        public static final String NAME_DESC = "The common authentication scheme name,e.g., HTTP Basic.";
        public static final String DESCRIPTION_DESC = "A description of the authentication scheme.";
        public static final String SPEC_URI_DESC = "An HTTP-addressable URL pointing to the authentication scheme's " +
            "specification.";
        public static final String TYPE_DESC = "A label indicating the attribute's function, e.g., 'work' or 'home'.";
        public static final String PRIMARY_DESC = "A Boolean value indicating the 'primary' or preferred attribute " +
            "value for this attribute.  " +
            "The primary attribute value 'true' MUST appear no more than once.";
    }

    /**
     * Constants found in Service Resource type schema.
     */
    public static class ResourceTypeSchemaConstants {
        public static final String RESOURCE_TYPE = "ResourceType";
        public static final String ID = "id";
        public static final String ID_URI = CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":id";
        public static final String NAME = "name";
        public static final String NAME_URI = CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":name";
        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION_URI = CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":description";
        public static final String ENDPOINT = "endpoint";
        public static final String ENDPOINT_URI = CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":endpoint";
        public static final String SCHEMA = "schema";
        public static final String SCHEMA_URI = CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":schema";
        public static final String SCHEMA_EXTENSIONS = "schemaExtensions";
        public static final String SCHEMA_EXTENSIONS_URI =
            CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":schemaExtensions";
        public static final String SCHEMA_EXTENSIONS_SCHEMA = "schema";
        public static final String SCHEMA_EXTENSIONS_SCHEMA_URI =
            CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":schemaExtension.schema";
        public static final String SCHEMA_EXTENSIONS_REQUIRED = "required";
        public static final String SCHEMA_EXTENSIONS_REQUIRED_URI =
            CORE_SCHEMA_URI + ":" + RESOURCE_TYPE + ":schemaExtension.required";

        public static final String NAME_DESC = "The resource type name.  When applicable, service providers MUST " +
            "specify the name, e.g., 'User'.\"";
        public static final String DESCRIPTION_DESC = "e resource type's human-readable description. When applicable," +
            " service providers MUST specify the description.";
        public static final String ENDPOINT_DESC = "The resource type's HTTP-addressable endpoint relative to the " +
            "Base URL, e.g., '/Users'.";
        public static final String SCHEMA_DESC = "The resource type's primary/base schema uri.";
        public static final String SCHEMA_EXTENSIONS_DESC = "A list of URIs of the resource type's schema extensions.";
        public static final String SCHEMA_EXTENSIONS_SCHEMA_DESC = "The uri of a schema extension.";
        public static final String SCHEMA_EXTENSION_REQUIRED_DESC = "A Boolean value that specifies whether or not " +
            "the schema extension is required for the resource type.";
        public static final String ID_DESC = "The resource type's server unique id";

        public static final String USER_ACCOUNT = "User Account";
        public static final String GROUP = "Group";
    }

    /**
     * this subclass holds the constant values for the schemas endpoint.
     */
    public static class SchemaSchemaConstants {

        public static final String NAME = "name";
        public static final String NAME_URI = SCHEMA_URI + ":" + NAME;
        public static final String NAME_DESC = "The schema's human-readable name.";

        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION_URI = SCHEMA_URI + ":" + DESCRIPTION;
        public static final String DESCRIPTION_DESC = "The schema's human-readable description.";

        public static final String ATTRIBUTES = "attributes";
        public static final String ATTRIBUTES_URI = SCHEMA_URI + ":" + ATTRIBUTES;
        public static final String ATTRIBUTES_DESC =
            " A complex type that defines service provider attributes and their qualities";

        public static final String ATTRIBUTES_NAME = "name";
        public static final String ATTRIBUTES_NAME_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_NAME;
        public static final String ATTRIBUTES_NAME_DESC = "The attribute's name.";

        public static final String ATTRIBUTES_TYPE = "type";
        public static final String ATTRIBUTES_TYPE_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_TYPE;
        public static final String ATTRIBUTES_TYPE_DESC =
            "The attribute's data type.  Valid values are \"string\", \"boolean\", \"decimal\", \"integer\", " +
                "\"dateTime\", \"reference\", and \"complex\".  When an attribute is of type " +
                "\"complex\", there SHOULD be a corresponding schema attribute \"subAttributes\" " +
                "defined, listing the sub-attributes of the attribute.";

        public static final String ATTRIBUTES_SUB_ATTRIBUTES = "subAttributes";
        public static final String ATTRIBUTES_SUB_ATTRIBUTES_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_SUB_ATTRIBUTES;
        public static final String ATTRIBUTES_SUB_ATTRIBUTES_DESC =
            "When an attribute is of type \"complex\", \"subAttributes\" defines a set of " +
                "sub-attributes. \"subAttributes\" has the same schema sub-attributes as \"attributes\".";

        public static final String ATTRIBUTES_MULTI_VALUED = "multiValued";
        public static final String ATTRIBUTES_MULTI_VALUED_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_MULTI_VALUED;
        public static final String ATTRIBUTES_MULTI_VALUED_DESC =
            "A Boolean value indicating the attribute's plurality.";

        public static final String ATTRIBUTES_DESCRIPTION = "description";
        public static final String ATTRIBUTES_DESCRIPTION_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_DESCRIPTION;
        public static final String ATTRIBUTES_DESCRIPTION_DESC =
            "The attribute's human-readable description.  When applicable, service providers MUST " +
                "specify the description.";

        public static final String ATTRIBUTES_REQUIRED = "required";
        public static final String ATTRIBUTES_REQUIRED_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_REQUIRED;
        public static final String ATTRIBUTES_REQUIRED_DESC =
            "A Boolean value that specifies whether or not the attribute is required.";

        public static final String ATTRIBUTES_CANONICAL_VALUES = "canonicalValues";
        public static final String ATTRIBUTES_CANONICAL_VALUES_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_CANONICAL_VALUES;
        public static final String ATTRIBUTES_CANONICAL_VALUES_DESC =
            "A collection of suggested canonical values that MAY be used (e.g., \"work\" and " +
                "\"home\"). In some cases, service providers MAY choose to ignore unsupported values. ";

        public static final String ATTRIBUTES_CASE_EXACT = "caseExact";
        public static final String ATTRIBUTES_CASE_EXACT_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_CASE_EXACT;
        public static final String ATTRIBUTES_CASE_EXACT_DESC =
            "A Boolean value that specifies whether or not a string attribute is case sensitive.";


        public static final String ATTRIBUTES_MUTABILITY = "mutability";
        public static final String ATTRIBUTES_MUTABILITY_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_MUTABILITY;
        public static final String ATTRIBUTES_MUTABILITY_DESC =
            "A single keyword indicating the circumstances under which the value of the attribute can be" +
                " (re)defined";

        public static final String ATTRIBUTES_RETURNED = "returned";
        public static final String ATTRIBUTES_RETURNED_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_RETURNED;
        public static final String ATTRIBUTES_RETURNED_DESC =
            "A single keyword that indicates when an attribute and associated values are returned in " +
                "response to a GET request or in response to a PUT, POST, or PATCH request";

        public static final String ATTRIBUTES_UNIQUENESS = "uniqueness";
        public static final String ATTRIBUTES_UNIQUENESS_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_UNIQUENESS;
        public static final String ATTRIBUTES_UNIQUENESS_DESC =
            "A single keyword value that specifies how the service provider enforces uniqueness of " +
                "attribute values.";

        public static final String ATTRIBUTES_REFERENCE_TYPES = "referenceTypes";
        public static final String ATTRIBUTES_REFERENCE_TYPES_URI = ATTRIBUTES_URI + "." + ATTRIBUTES_REFERENCE_TYPES;
        public static final String ATTRIBUTES_REFERENCE_TYPES_DESC =
            "A multi-valued array of JSON strings that indicate the SCIM resource types that may " +
                "be referenced.";

    }

    /**
     * Constants found in operations.
     */
    public static class OperationalConstants {

        /*SCIM filter types*/
        public static final String EQ = " eq ";
        public static final String NE = " ne ";
        public static final String CO = " co ";
        public static final String SW = " sw ";
        public static final String EW = " ew ";
        public static final String PR = " pr";   //this MUST NOT have a white space at the end
        public static final String GT = " gt ";
        public static final String GE = " ge ";
        public static final String LT = " lt ";
        public static final String LE = " le ";

        /*SCIM logical operators*/
        public static final String AND = "and";
        public static final String OR = "or";
        public static final String NOT = "not";
        public static final String LEFT = "(";
        public static final String RIGHT = ")";

        public static final String ASCENDING = "ascending";
        public static final String DESCENDING = "descending";

        public static final String ADD = "add";
        public static final String REMOVE = "remove";
        public static final String REPLACE = "replace";
        public static final String OPERATIONS = "Operations";
        public static final String OP = "op";
        public static final String PATH = "path";
        public static final String VALUE = "value";


        public static final String ATTRIBUTES = "attributes";
        public static final String EXCLUDED_ATTRIBUTES = "excludedAttributes";
        public static final String COUNT = "count";
        public static final String START_INDEX = "startIndex";
        public static final String SORT_BY = "sortBy";
        public static final String SORT_ORDER = "sortOrder";
        public static final String FILTER = "filter";
        public static final String DOMAIN = "domain";

        //bulk constants
        public static final String METHOD = "method";
        public static final String VERSION = "version";
        public static final String BULK_ID = "bulkId";
        public static final String FAIL_ON_ERRORS = "failOnErrors";
        public static final String DATA = "data";
        public static final String STATUS = "status";
        public static final String CODE = "code";
        public static final String RESPONSE = "response";

        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";


    }

}
