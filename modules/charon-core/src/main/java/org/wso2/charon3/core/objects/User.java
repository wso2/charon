/*
 * Copyright (c) 2016-2023, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon3.core.objects;

import org.apache.commons.lang.StringUtils;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;
import org.wso2.charon3.core.objects.plainobjects.ScimName;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowBiConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;

/**
 * Represents the User object which is a collection of attributes defined by SCIM User-schema.
 */
public class User extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;

    /**
     * returns the username of this user
     */
    public String getUsername() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME;
        return getSimpleAttribute(attributeDefinition).map(rethrowFunction(SimpleAttribute::getStringValue))
                .orElse(null);
    }

    /**
     * deletes the current value of username and exchanges it with the given value
     */
    public void replaceUsername(String username) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME, username);
    }

    /**
     * gets the displayName of this user
     */
    public String getDisplayName() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.DISPLAY_NAME;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of displayName and exchanges it with the given value
     */
    public void replaceDisplayName(String displayName) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.DISPLAY_NAME, displayName);
    }

    /**
     * gets the nickname of this user
     */
    public String getNickName() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NICK_NAME;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of nickname and exchanges it with the given value
     */
    public void replaceNickName(String nickName) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NICK_NAME, nickName);
    }

    /**
     * gets the profile url of this user
     */
    public String getProfileUrl() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PROFILE_URL;
        return getSimpleAttribute(attributeDefinition)
                .map(simpleAttribute -> stripToNull((String) simpleAttribute.getValue())).orElse(null);
    }

    /**
     * deletes the current value of profile url and exchanges it with the given value
     */
    public void replaceProfileUrl(String profileUrl) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PROFILE_URL, profileUrl);
    }

    /**
     * gets the user type of this user
     */
    public String getUserType() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USER_TYPE;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of user type and exchanges it with the given value
     */
    public void replaceUserType(String userType) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USER_TYPE, userType);
    }

    /**
     * gets the title value of this user
     */
    public String getTitle() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TITLE;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of title and exchanges it with the given value
     */
    public void replaceTitle(String title) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TITLE, title);
    }

    /**
     * gets the preferred language value of this user
     */
    public String getPreferredLanguage() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PREFERRED_LANGUAGE;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of preferred language and exchanges it with the given value
     */
    public void setPreferredLanguage(String preferredLanguage) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PREFERRED_LANGUAGE, preferredLanguage);
    }

    /**
     * deletes the preferred language attribute and exchanges it with the given value
     *
     * @param preferredLanguage the new preferred language
     */
    public void replacePreferredLanguage(String preferredLanguage) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PREFERRED_LANGUAGE, preferredLanguage);
    }

    /**
     * gets the current locale value of this user
     */
    public String getLocale() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.LOCALE;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of preferred language and exchanges it with the given value
     */
    public void replaceLocale(String locale) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.LOCALE, locale);
    }

    /**
     * gets the current timezone value of this user
     */
    public String getTimezone() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TIME_ZONE;
        return getSimpleAttribute(attributeDefinition).map(
                simpleAttribute -> stripToNull(rethrowFunction(SimpleAttribute::getStringValue).apply(simpleAttribute)))
                .orElse(null);
    }

    /**
     * deletes the current value of timezone and exchanges it with the given value
     */
    public void setTimezone(String timezone) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TIME_ZONE, timezone);
    }

    /**
     * deletes the timezone attribute and exchanges it with the given value
     *
     * @param timezone the new timezone
     */
    public void replaceTimezone(String timezone) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TIME_ZONE, timezone);
    }

    /**
     * gets the current active value of this user
     */
    public boolean getActive() {

        SCIMAttributeSchema attributeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ACTIVE;
        return getSimpleAttribute(attributeDefinition)
                .map(simpleAttribute -> rethrowFunction(SimpleAttribute::getBooleanValue).apply(simpleAttribute))
                .orElse(false);
    }

    /**
     * deletes the current value of active and exchanges it with the given value
     */
    public void replaceActive(boolean active) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ACTIVE, active);
    }

    /**
     * extracts the complex "name" attribute from the Scim structure
     */
    public ScimName getName() {

        SCIMAttributeSchema nameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NAME;
        SCIMAttributeSchema formattedDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.FORMATTED;
        SCIMAttributeSchema givenNameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GIVEN_NAME;
        SCIMAttributeSchema familynameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.FAMILY_NAME;
        SCIMAttributeSchema middleNameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.MIDDLE_NAME;
        SCIMAttributeSchema honorificPrefixDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.HONORIFIC_PREFIX;
        SCIMAttributeSchema honorificSuffixDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.HONORIFIC_SUFFIX;

        return getComplexAttribute(nameDefinition).map(name -> {
            ScimName scimName = new ScimName();
            getSimpleAttributeValue(formattedDefinition, name).ifPresent(scimName::setFormatted);
            getSimpleAttributeValue(givenNameDefinition, name).ifPresent(scimName::setGivenName);
            getSimpleAttributeValue(familynameDefinition, name).ifPresent(scimName::setFamilyName);
            getSimpleAttributeValue(middleNameDefinition, name).ifPresent(scimName::setMiddleName);
            getSimpleAttributeValue(honorificPrefixDefinition, name).ifPresent(scimName::setHonorificPrefix);
            getSimpleAttributeValue(honorificSuffixDefinition, name).ifPresent(scimName::setHonorificSuffix);
            if (isBlank(scimName.getFormatted()) && isBlank(scimName.getFamilyName()) && isBlank(
                    scimName.getGivenName()) && isBlank(scimName.getMiddleName()) && isBlank(
                    scimName.getHonorificPrefix()) && isBlank(scimName.getHonorificSuffix())) {
                return null;
            }
            return scimName;
        }).orElse(null);
    }

    /**
     * deletes the current complex name attribute and exchanges it with the given value
     */
    public void replaceName(ScimName scimName) {

        SCIMAttributeSchema nameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NAME;
        SCIMAttributeSchema formattedDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.FORMATTED;
        SCIMAttributeSchema givenNameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GIVEN_NAME;
        SCIMAttributeSchema familynameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.FAMILY_NAME;
        SCIMAttributeSchema middleNameDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.MIDDLE_NAME;
        SCIMAttributeSchema honorificPrefixDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.HONORIFIC_PREFIX;
        SCIMAttributeSchema honorificSuffixDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.HONORIFIC_SUFFIX;

        ComplexAttribute name = new ComplexAttribute(nameDefinition.getName());
        rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(nameDefinition, name);

        BiConsumer<AttributeSchema, Supplier<String>> writeAttribute = (attributeSchema, stringSupplier) -> {
            if (isNotBlank(stringSupplier.get())) {
                SimpleAttribute attribute = new SimpleAttribute(attributeSchema.getName(), stringSupplier.get());
                rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(attributeSchema, attribute);
                rethrowConsumer(name::setSubAttribute).accept(attribute);
            }
        };

        writeAttribute.accept(formattedDefinition, scimName::getFormatted);
        writeAttribute.accept(familynameDefinition, scimName::getFamilyName);
        writeAttribute.accept(givenNameDefinition, scimName::getGivenName);
        writeAttribute.accept(middleNameDefinition, scimName::getMiddleName);
        writeAttribute.accept(honorificPrefixDefinition, scimName::getHonorificPrefix);
        writeAttribute.accept(honorificSuffixDefinition, scimName::getHonorificSuffix);

        if (name.getSubAttributesList().isEmpty()) {
            deleteAttribute(nameDefinition.getName());
        } else {
            deleteAttribute(nameDefinition.getName());
            setAttribute(name);
        }
    }

    /**
     * gets the emails from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getEmails() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAILS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_PRIMARY;

        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition,
                primaryDefinition, null).orElse(Collections.emptyList());
    }

    /**
     * deletes the current complex emails attributes and exchanges it with the given values
     */
    public void replaceEmails(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAILS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_PRIMARY;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, primaryDefinition, null);
    }

    /**
     * gets the phonenumbers from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getPhoneNumbers() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_PRIMARY;

        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition,
                primaryDefinition, null).orElse(Collections.emptyList());
    }

    /**
     * deletes the current complex phonenumber attributes and exchanges it with the given values
     */
    public void replacePhoneNumbers(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_PRIMARY;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, primaryDefinition, null);
    }

    /**
     * gets the instant messaging addresses from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getInstantMessagingAddresses() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_PRIMARY;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition,
                primaryDefinition, null).orElse(Collections.emptyList());
    }

    /**
     * deletes the current complex instant messaging addresses attributes and exchanges it with the given values
     */
    public void replaceInstantMessagingAddresses(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS_PRIMARY;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, primaryDefinition, null);
    }

    /**
     * gets the photo uris from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getPhotos() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_PRIMARY;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition,
                primaryDefinition, null).orElse(Collections.emptyList());
    }

    /**
     * deletes the current photo uri attributes and exchanges it with the given values
     */
    public void replacePhotos(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS_PRIMARY;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, primaryDefinition, null);
    }

    /**
     * gets the x509 certificates from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getX509Certificates() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_PRIMARY;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition,
                primaryDefinition, null).orElse(Collections.emptyList());
    }

    /**
     * deletes the current x509 certificate attributes and exchanges it with the given values
     */
    public void replaceX509Certificates(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES_PRIMARY;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, primaryDefinition, null);
    }

    /**
     * gets the groups from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getGroups() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE;
        SCIMAttributeSchema referenceDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_REF;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition, null,
                referenceDefinition).orElse(Collections.emptyList());
    }

    /**
     * deletes the group attributes and exchanges it with the given values
     */
    public void replaceGroups(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE;
        SCIMAttributeSchema referenceDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_REF;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, null, referenceDefinition);
    }

    /**
     * gets the roles from this user or an empty list if there are none present
     */
    public List<MultiValuedComplexType> getRoles() {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_TYPE;
        return getMultivaluedComplexType(complexDefinition, valueDefinition, displayDefinition, typeDefinition, null,
                null).orElse(Collections.emptyList());
    }

    /**
     * deletes the role attributes and exchanges it with the given values
     */
    public void replaceRoles(List<MultiValuedComplexType> multiValuedComplexTypeList) {

        SCIMAttributeSchema complexDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES;
        SCIMAttributeSchema valueDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_VALUE;
        SCIMAttributeSchema displayDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_DISPLAY;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_TYPE;

        addMultivaluedComplexAtribute(multiValuedComplexTypeList, complexDefinition, valueDefinition, displayDefinition,
                typeDefinition, null, null);
    }

    /**
     * gets the addresses from this user or an empty list if there are none present
     */
    public List<ScimAddress> getAddresses() {

        SCIMAttributeSchema multiValuedDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES;
        SCIMAttributeSchema formattedDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_FORMATTED;
        SCIMAttributeSchema streetAddressDefinition =
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_STREET_ADDRESS;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_PRIMARY;
        SCIMAttributeSchema localityDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_LOCALITY;
        SCIMAttributeSchema regionDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_REGION;
        SCIMAttributeSchema postalCodeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_POSTAL_CODE;
        SCIMAttributeSchema countryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_COUNTRY;

        return getMultiValuedAttribute(multiValuedDefinition).map(multiValuedAttribute -> {
            List<ScimAddress> multiValuedComplexTypes = new ArrayList<>();
            for (Attribute attributeValue : multiValuedAttribute.getAttributeValues()) {
                ComplexAttribute complexAttribute = (ComplexAttribute) attributeValue;
                ScimAddress address = new ScimAddress();
                getSimpleAttributeValue(formattedDefinition, complexAttribute).ifPresent(address::setFormatted);
                getSimpleAttributeValue(streetAddressDefinition, complexAttribute).ifPresent(address::setStreetAddress);
                getSimpleAttributeValue(typeDefinition, complexAttribute).ifPresent(address::setType);
                getSimpleAttribute(primaryDefinition, complexAttribute)
                        .map(rethrowFunction(SimpleAttribute::getBooleanValue)).ifPresent(address::setPrimary);
                getSimpleAttributeValue(localityDefinition, complexAttribute).ifPresent(address::setLocality);
                getSimpleAttributeValue(regionDefinition, complexAttribute).ifPresent(address::setRegion);
                getSimpleAttributeValue(postalCodeDefinition, complexAttribute).ifPresent(address::setPostalCode);
                getSimpleAttributeValue(countryDefinition, complexAttribute).ifPresent(address::setCountry);

                if (!(isBlank(address.getFormatted()) && isBlank(address.getStreetAddress()) && isBlank(
                        address.getLocality()) && isBlank(address.getRegion()) && isBlank(
                        address.getPostalCode()) && isBlank(address.getCountry()) && isBlank(address.getType()) &&
                        !address.isPrimary())) {
                    multiValuedComplexTypes.add(address);
                }
            }
            return multiValuedComplexTypes;
        }).orElse(Collections.emptyList());
    }

    /**
     * deletes the addresses attributes and exchanges it with the given values
     */
    public void replaceAddresses(List<ScimAddress> addressList) {

        SCIMAttributeSchema addressDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES;
        SCIMAttributeSchema formattedDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_FORMATTED;
        SCIMAttributeSchema streetAddressDefinition =
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_STREET_ADDRESS;
        SCIMAttributeSchema typeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_TYPE;
        SCIMAttributeSchema primaryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_PRIMARY;
        SCIMAttributeSchema localityDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_LOCALITY;
        SCIMAttributeSchema regionDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_REGION;
        SCIMAttributeSchema postalCodeDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_POSTAL_CODE;
        SCIMAttributeSchema countryDefinition = SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES_COUNTRY;

        deleteAttribute(addressDefinition.getName());

        MultiValuedAttribute multivaluedAddressAttribute = new MultiValuedAttribute(addressDefinition.getName());
        LambdaExceptionUtils.rethrowBiConsumer(DefaultAttributeFactory::createAttribute)
                .accept(addressDefinition, multivaluedAddressAttribute);

        addressList.forEach(address -> {
            ComplexAttribute complexAttribute = new ComplexAttribute(addressDefinition.getName());

            BiConsumer<SCIMAttributeSchema, Supplier<Object>> setSubAttribute = getSetSubAttributeConsumer(
                    complexAttribute);

            setSubAttribute.accept(formattedDefinition, address::getFormatted);
            setSubAttribute.accept(streetAddressDefinition, address::getStreetAddress);
            setSubAttribute.accept(localityDefinition, address::getLocality);
            setSubAttribute.accept(regionDefinition, address::getRegion);
            setSubAttribute.accept(postalCodeDefinition, address::getPostalCode);
            setSubAttribute.accept(countryDefinition, address::getCountry);
            setSubAttribute.accept(typeDefinition, address::getType);
            setSubAttribute.accept(primaryDefinition, () -> address.isPrimary() ? true : null);
            multivaluedAddressAttribute.setAttributeValue(complexAttribute);
        });
        setAttribute(multivaluedAddressAttribute);
    }

    /**
     * return userName of the user
     *
     * @return
     * @throws CharonException
     */
    public String getUserName() throws CharonException {

        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.USER_NAME);
    }

    /**
     * set the userName of the user
     *
     * @param userName
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setUserName(String userName) throws CharonException, BadRequestException {

        this.setSimpleAttribute(SCIMConstants.UserSchemaConstants.USER_NAME,
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME, userName);
    }

    /**
     * deletes the username attribute and exchanges it with the given value
     *
     * @param username the new username
     */
    public void replaceUserName(String username) {

        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME, username);
    }

    /**
     * return the password of the user
     *
     * @return
     * @throws CharonException
     */
    public String getPassword() throws CharonException {

        return this.getSimpleAttributeStringVal(SCIMConstants.UserSchemaConstants.PASSWORD);
    }

    /**
     * set the password of the user
     *
     * @param password
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setPassword(String password) throws CharonException, BadRequestException {

        setSimpleAttribute(SCIMConstants.UserSchemaConstants.PASSWORD, SCIMSchemaDefinitions.SCIMUserSchemaDefinition.
                PASSWORD, password);
    }

    /**
     * set simple attribute in the scim object
     *
     * @param attributeName
     * @param attributeSchema
     * @param value
     * @throws CharonException
     * @throws BadRequestException
     */
    private void setSimpleAttribute(String attributeName,
                                    AttributeSchema attributeSchema,
                                    Object value) throws CharonException, BadRequestException {

        if (this.isAttributeExist(attributeName)) {
            ((SimpleAttribute) this.attributeList.get(attributeName)).updateValue(value);
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(attributeName, value);
            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.
                    createAttribute(attributeSchema,
                            simpleAttribute);
            this.attributeList.put(attributeName, simpleAttribute);
        }

    }

    /**
     * return simple attribute's string value
     *
     * @param attributeName
     * @return
     * @throws CharonException
     */
    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {

        return this.isAttributeExist(attributeName) ? ((SimpleAttribute) this.attributeList.get(attributeName))
                .getStringValue() : null;
    }

    /**
     * This method used to add group details (attributes display and value) of a user.
     * According to the SCIM specification need to add ref attribute as well along with display and value. Hence
     * deprecated this method.
     *
     * @deprecated use {@link #setGroup(String type, Group group)} instead.
     */
    @Deprecated
    public void setGroup(String type,
                         String value,
                         String display) throws CharonException, BadRequestException {

        SimpleAttribute typeSimpleAttribute = null;
        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute = null;
        ComplexAttribute complexAttribute = new ComplexAttribute();
        if (type != null) {
            typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, type);
            typeSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE, typeSimpleAttribute);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
        }

        if (value != null) {
            valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value);
            valueSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (display != null) {
            displaySimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display);
            displaySimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY,
                            displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }
        if (complexAttribute.getSubAttributesList().size() != 0) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (typeSimpleAttribute != null && typeSimpleAttribute.getValue() != null) {
                typeVal = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.GROUPS + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, complexAttribute);
            setGroup(complexAttribute);
        }
    }

    private void setGroup(ComplexAttribute groupPropertiesAttribute) throws CharonException, BadRequestException {

        MultiValuedAttribute groupsAttribute;

        if (this.attributeList.containsKey(SCIMConstants.UserSchemaConstants.GROUPS)) {
            groupsAttribute = (MultiValuedAttribute) this.attributeList.get(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
        } else {
            groupsAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.GROUPS);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
            groupsAttribute = (MultiValuedAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, groupsAttribute);
            this.attributeList.put(SCIMConstants.UserSchemaConstants.GROUPS, groupsAttribute);
        }

    }

    /**
     * Set the associated groups of the user.
     * According to the SCIM specification need to add display, value and ref attributes.
     *
     * @param type  Type of resource.
     * @param group Group object.
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setGroup(String type, Group group) throws CharonException, BadRequestException {
        SimpleAttribute typeSimpleAttribute = null;
        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute = null;
        SimpleAttribute referenceSimpleAttribute = null;
        String reference = group.getLocation();
        String value = group.getId();
        String display = group.getDisplayName();
        ComplexAttribute complexAttribute = new ComplexAttribute();
        if (type != null) {
            typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, type);
            typeSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_TYPE, typeSimpleAttribute);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
        }

        if (value != null) {
            valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value);
            valueSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (reference != null) {
            referenceSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.REF, reference);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.REF, referenceSimpleAttribute);
            complexAttribute.setSubAttribute(referenceSimpleAttribute);
        }

        if (display != null) {
            displaySimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display);
            displaySimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUP_DISPLAY,
                            displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }

        if (complexAttribute.getSubAttributesList().size() != 0) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (typeSimpleAttribute != null && typeSimpleAttribute.getValue() != null) {
                typeVal = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.GROUPS + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS, complexAttribute);
            setGroup(complexAttribute);
        }
    }

    /**
     * Set the associated roles of the user.
     *
     * @param role Role object.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    public void setRole(Role role) throws CharonException, BadRequestException {

        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute;
        SimpleAttribute referenceSimpleAttribute;
        String reference = role.getLocation();
        String value = role.getId();
        String display = role.getDisplayName();
        ComplexAttribute complexAttribute = new ComplexAttribute();

        if (StringUtils.isNotBlank(value)) {
            valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value);
            valueSimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (StringUtils.isNotBlank(reference)) {
            referenceSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.REF, reference);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_REF,
                    referenceSimpleAttribute);
            complexAttribute.setSubAttribute(referenceSimpleAttribute);
        }

        if (StringUtils.isNotBlank(display)) {
            displaySimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display);
            displaySimpleAttribute = (SimpleAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_DISPLAY,
                            displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }

        if (!complexAttribute.getSubAttributesList().isEmpty()) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.ROLES + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_SCHEMA, complexAttribute);
            setRole(complexAttribute);
        }
    }

    private void setRole(ComplexAttribute groupPropertiesAttribute) throws CharonException, BadRequestException {

        MultiValuedAttribute groupsAttribute;
        if (this.attributeList.containsKey(SCIMConstants.UserSchemaConstants.ROLES)) {
            groupsAttribute = (MultiValuedAttribute) this.attributeList.get(SCIMConstants.UserSchemaConstants.ROLES);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
        } else {
            groupsAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.ROLES);
            groupsAttribute.setAttributeValue(groupPropertiesAttribute);
            groupsAttribute = (MultiValuedAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_SCHEMA, groupsAttribute);
            this.attributeList.put(SCIMConstants.UserSchemaConstants.ROLES, groupsAttribute);
        }
    }

    /**
     * Set the assigned V2 roles of the user.
     *
     * @param role RoleV2 object.
     * @throws CharonException     CharonException.
     * @throws BadRequestException BadRequestException.
     */
    public void setRoleV2(RoleV2 role) throws CharonException, BadRequestException {

        SimpleAttribute valueSimpleAttribute = null;
        SimpleAttribute displaySimpleAttribute;
        SimpleAttribute audienceValueSimpleAttribute;
        SimpleAttribute audienceDisplaySimpleAttribute;
        SimpleAttribute audienceTypeSimpleAttribute;
        String reference = role.getLocation();
        String value = role.getId();
        String display = role.getDisplayName();
        String audienceValue = role.getAudienceValue();
        String audienceDisplay = role.getAudienceDisplayName();
        String audienceType = role.getAudienceType();
        ComplexAttribute complexAttribute = new ComplexAttribute();

        if (StringUtils.isNotBlank(value)) {
            valueSimpleAttribute = getSimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, value,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_VALUE);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (StringUtils.isNotBlank(reference)) {
            complexAttribute.setSubAttribute(getSimpleAttribute(SCIMConstants.CommonSchemaConstants.REF, reference,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_REF));
        }

        if (StringUtils.isNotBlank(display)) {
            complexAttribute.setSubAttribute(getSimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, display,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_DISPLAY));
        }

        if (StringUtils.isNotBlank(audienceValue)) {
            complexAttribute.setSubAttribute(getSimpleAttribute(SCIMConstants.CommonSchemaConstants.AUDIENCE_VALUE,
                    audienceValue, SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_AUDIENCE_VALUE));
        }

        if (StringUtils.isNotBlank(audienceDisplay)) {
            complexAttribute.setSubAttribute(getSimpleAttribute(SCIMConstants.CommonSchemaConstants.AUDIENCE_DISPLAY,
                    audienceDisplay, SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_AUDIENCE_DISPLAY));
        }

        if (StringUtils.isNotBlank(audienceType)) {
            complexAttribute.setSubAttribute(getSimpleAttribute(SCIMConstants.CommonSchemaConstants.AUDIENCE_TYPE,
                    audienceType, SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_AUDIENCE_TYPE));
        }

        if (!complexAttribute.getSubAttributesList().isEmpty()) {
            Object typeVal = SCIMConstants.DEFAULT;
            Object valueVal = SCIMConstants.DEFAULT;
            if (valueSimpleAttribute != null && valueSimpleAttribute.getValue() != null) {
                valueVal = valueSimpleAttribute.getValue();
            }
            String complexAttributeName = SCIMConstants.UserSchemaConstants.ROLES + "_" + valueVal + "_" + typeVal;
            complexAttribute.setName(complexAttributeName);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_SCHEMA, complexAttribute);
            setRoleV2(complexAttribute);
        }
    }

    private SimpleAttribute getSimpleAttribute(String attributeName, String attributeValue,
                                                      SCIMAttributeSchema attributeSchema)
            throws CharonException, BadRequestException {

        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                new SimpleAttribute(attributeName, attributeValue));
    }

    private void setRoleV2(ComplexAttribute rolePropertiesAttribute) throws CharonException, BadRequestException {

        MultiValuedAttribute rolesAttribute;
        if (this.attributeList.containsKey(SCIMConstants.UserSchemaConstants.ROLES)) {
            rolesAttribute = (MultiValuedAttribute) this.attributeList.get(SCIMConstants.UserSchemaConstants.ROLES);
            rolesAttribute.setAttributeValue(rolePropertiesAttribute);
        } else {
            rolesAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.ROLES);
            rolesAttribute.setAttributeValue(rolePropertiesAttribute);
            rolesAttribute = (MultiValuedAttribute) DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES_SCHEMA, rolesAttribute);
            this.attributeList.put(SCIMConstants.UserSchemaConstants.ROLES, rolesAttribute);
        }
    }

    /**
     * set the schemas of the user
     */
    public void setSchemas() {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        java.util.List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }
    }

    /**
     * Set the schemas of the user
     */
    public void setSchemas(UserManager userManager) throws BadRequestException,
            NotImplementedException, CharonException {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema(userManager);
        java.util.List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }
    }
}
