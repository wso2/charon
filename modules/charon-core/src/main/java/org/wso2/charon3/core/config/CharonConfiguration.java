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
package org.wso2.charon3.core.config;

import org.wso2.charon3.core.attributes.*;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.net.URL;
import java.util.*;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.*;

/**
 * This class contains the charon related configurations.
 */
public final class CharonConfiguration extends AbstractSCIMObject {

    private static final long serialVersionUID = -324866822974408761L;

    /**
     * the singleton instance of the service provider configuration
     */
    private static final CharonConfiguration CHARON_CONFIGURATION = new CharonConfiguration();

    public static final int DEFAULT_MAX_RESULTS = 1;

    public static final int DEFAULT_MAX_PAYLOAD = 1024 * 50;

    public static final int DEFAULT_MAX_OPERATIONS = 1024 * 50;

    /**
     * private default constructor
     */
    private CharonConfiguration () {
        setSchema(SCIMConstants.SERVICE_PROVIDER_CONFIG_SCHEMA_URI);
        setPatch(null);
        setSort(null);
        seteTag(null);
        setFilter(null);
        setBulk(null);
        setChangePassword(null);
        setAuthenticationSchemes(null);
    }

    /**
     * return the instance of CharonConfiguration
     */
    public static CharonConfiguration getInstance () {
        return CHARON_CONFIGURATION;
    }

    /**
     * A complex type that specifies PATCH configuration options. REQUIRED.  See Section 3.5.2 of [RFC7644].
     */
    public ScimFeature getPatch () {
        SCIMAttributeSchema patchAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.PATCH;
        SCIMAttributeSchema patchSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.PATCH_SUPPORTED;
        ScimFeature patchFeature = new ScimFeature();
        getComplexAttribute(patchAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(patchSupported, complex).ifPresent(patchFeature::setSupported);
        });
        return patchFeature;
    }

    /**
     * A complex type that specifies PATCH configuration options. REQUIRED.  See Section 3.5.2 of [RFC7644].
     */
    public void setPatch (ScimFeature patch) {
        SCIMAttributeSchema patchAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.PATCH;
        SCIMAttributeSchema patchSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.PATCH_SUPPORTED;
        if (patch == null) {
            setPatch(new ScimFeature(false));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(patchAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(patchSupported, patch::isSupported);
    }

    /**
     * An HTTP-addressable URL pointing to the service provider's human-consumable help documentation.  OPTIONAL.
     */
    public URL getDocumentationUri () {
        SCIMAttributeSchema documentationUriAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DOCUMENTATION_URI;
        return getSimpleAttribute(documentationUriAttribute).map(rethrowFunction(simpleAttribute -> {
            return new URL((String) simpleAttribute.getValue());
        })).orElse(null);
    }

    /**
     * An HTTP-addressable URL pointing to the service provider's human-consumable help documentation.  OPTIONAL.
     */
    public void setDocumentationUri (URL documentationUri) {
        SCIMAttributeSchema documentationUriAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DOCUMENTATION_URI;
        replaceSimpleAttribute(documentationUriAttribute, documentationUri.toString());
    }

    /**
     * A complex type that specifies FILTER options.  REQUIRED.  See Section 3.4.2.2 of [RFC7644].
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     * <p>
     * maxResults  An integer value specifying the maximum number of resources returned in a response.  REQUIRED.
     */
    public FilterFeature getFilter () {
        SCIMAttributeSchema filterAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.FILTER;
        SCIMAttributeSchema filterSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.FILTER_SUPPORTED;
        SCIMAttributeSchema maxResults = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_RESULTS;

        FilterFeature filterFeature = new FilterFeature();
        getComplexAttribute(filterAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(filterSupported, complex).ifPresent(filterFeature::setSupported);
            getSimpleAttributeIntegerValue(maxResults, complex).ifPresent(filterFeature::setMaxResults);
        });
        return filterFeature;
    }

    /**
     * A complex type that specifies FILTER options.  REQUIRED.  See Section 3.4.2.2 of [RFC7644].
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     * <p>
     * maxResults  An integer value specifying the maximum number of resources returned in a response.  REQUIRED.
     */
    public void setFilter (FilterFeature filter) {
        SCIMAttributeSchema filterAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.FILTER;
        SCIMAttributeSchema filterSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.FILTER_SUPPORTED;
        SCIMAttributeSchema maxResults = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_RESULTS;
        if (filter == null) {
            setFilter(new FilterFeature(false, DEFAULT_MAX_RESULTS));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(filterAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(filterSupported, filter::isSupported);
        super.getSetSubAttributeConsumer(complexAttribute).accept(maxResults, filter::getMaxResults);
    }

    /**
     * A complex type that specifies bulk configuration options.  See Section 3.7 of [RFC7644].  REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     * <p>
     * maxOperations  An integer value specifying the maximum number of operations.  REQUIRED.
     * <p>
     * maxPayloadSize  An integer value specifying the maximum payload size in bytes.  REQUIRED.
     */
    public BulkFeature getBulk () {
        SCIMAttributeSchema bulkAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.BULK;
        SCIMAttributeSchema bulkSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.BULK_SUPPORTED;
        SCIMAttributeSchema maxOperations =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_OPERATIONS;
        SCIMAttributeSchema maxPayloadSize =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_PAYLOAD_SIZE;
        BulkFeature bulkFeature = new BulkFeature();
        getComplexAttribute(bulkAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(bulkSupported, complex).ifPresent(bulkFeature::setSupported);
            getSimpleAttributeIntegerValue(maxOperations, complex).ifPresent(bulkFeature::setMaxOperations);
            getSimpleAttributeIntegerValue(maxPayloadSize, complex).ifPresent(bulkFeature::setMaxPayLoadSize);
        });
        return bulkFeature;
    }

    /**
     * A complex type that specifies bulk configuration options.  See Section 3.7 of [RFC7644].  REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     * <p>
     * maxOperations  An integer value specifying the maximum number of operations.  REQUIRED.
     * <p>
     * maxPayloadSize  An integer value specifying the maximum payload size in bytes.  REQUIRED.
     */
    public void setBulk (BulkFeature bulk) {
        SCIMAttributeSchema bulkAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.BULK;
        SCIMAttributeSchema bulkSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.BULK_SUPPORTED;
        SCIMAttributeSchema maxOperations =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_OPERATIONS;
        SCIMAttributeSchema maxPayloadSize =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.MAX_PAYLOAD_SIZE;
        if (bulk == null) {
            setBulk(new BulkFeature(false, DEFAULT_MAX_OPERATIONS, DEFAULT_MAX_PAYLOAD));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(bulkAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(bulkSupported, bulk::isSupported);
        super.getSetSubAttributeConsumer(complexAttribute).accept(maxOperations, bulk::getMaxOperations);
        super.getSetSubAttributeConsumer(complexAttribute).accept(maxPayloadSize, bulk::getMaxPayLoadSize);
    }

    /**
     * A complex type that specifies Sort configuration options. REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not sorting is supported.  REQUIRED.
     */
    public ScimFeature getSort () {
        SCIMAttributeSchema sortAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SORT;
        SCIMAttributeSchema sortSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SORT_SUPPORTED;
        ScimFeature sortFeature = new ScimFeature();
        getComplexAttribute(sortAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(sortSupported, complex).ifPresent(sortFeature::setSupported);
        });
        return sortFeature;
    }

    /**
     * A complex type that specifies Sort configuration options. REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not sorting is supported.  REQUIRED.
     */
    public void setSort (ScimFeature sort) {
        SCIMAttributeSchema sortAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SORT;
        SCIMAttributeSchema sortSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SORT_SUPPORTED;
        if (sort == null) {
            setSort(new ScimFeature(false));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(sortAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(sortSupported, sort::isSupported);
    }

    /**
     * A complex type that specifies ETag configuration options. REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     */
    public ScimFeature geteTag () {
        SCIMAttributeSchema etagAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.ETAG;
        SCIMAttributeSchema etagSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.ETAG_SUPPORTED;
        ScimFeature etagFeature = new ScimFeature();
        getComplexAttribute(etagAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(etagSupported, complex).ifPresent(etagFeature::setSupported);
        });
        return etagFeature;
    }

    /**
     * A complex type that specifies ETag configuration options. REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     */
    public void seteTag (ScimFeature eTag) {
        SCIMAttributeSchema etagAttribute = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.ETAG;
        SCIMAttributeSchema etagSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.ETAG_SUPPORTED;
        if (eTag == null) {
            seteTag(new ScimFeature(false));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(etagAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(etagSupported, eTag::isSupported);
    }

    /**
     * A complex type that specifies configuration options related to changing a password.  REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     */
    public ScimFeature getChangePassword () {
        SCIMAttributeSchema changePasswordAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD;
        SCIMAttributeSchema changePasswordSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD_SUPPORTED;
        ScimFeature changePassword = new ScimFeature();
        getComplexAttribute(changePasswordAttribute).ifPresent(complex -> {
            getSimpleAttributeBooleanValue(changePasswordSupported, complex).ifPresent(changePassword::setSupported);
        });
        return changePassword;
    }

    /**
     * A complex type that specifies configuration options related to changing a password.  REQUIRED.
     * <p>
     * supported  A Boolean value specifying whether or not the operation is supported.  REQUIRED.
     */
    public void setChangePassword (ScimFeature changePassword) {
        SCIMAttributeSchema changePasswordAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD;
        SCIMAttributeSchema changePasswordSupported =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD_SUPPORTED;
        if (changePassword == null) {
            setChangePassword(new ScimFeature(false));
            return;
        }
        ComplexAttribute complexAttribute = getOrCrateComplexAttribute(changePasswordAttribute);
        super.getSetSubAttributeConsumer(complexAttribute).accept(changePasswordSupported, changePassword::isSupported);
    }

    /**
     * A multi-valued complex type that specifies supported authentication scheme properties.  To enable seamless
     * discovery of configurations, the service provider SHOULD, with the appropriate security considerations, make the
     * authenticationSchemes attribute publicly accessible without prior authentication.  REQUIRED.  The following
     * sub-attributes are defined:
     * <p>
     * type  The authentication scheme.  This specification defines the values "oauth", "oauth2", "oauthbearertoken",
     * "httpbasic", and "httpdigest".  REQUIRED.
     * <p>
     * name  The common authentication scheme name, e.g., HTTP Basic. REQUIRED.
     * <p>
     * description  A description of the authentication scheme. REQUIRED.
     * <p>
     * specUri  An HTTP-addressable URL pointing to the authentication scheme's specification.  OPTIONAL.
     * <p>
     * documentationUri  An HTTP-addressable URL pointing to the authentication scheme's usage documentation.
     * OPTIONAL.
     */
    public List<AuthenticationScheme> getAuthenticationSchemes () {
        SCIMAttributeSchema authSchemesAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.AUTHENTICATION_SCHEMES;
        SCIMAttributeSchema type = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.TYPE;
        SCIMAttributeSchema name = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.NAME;
        SCIMAttributeSchema description = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DESCRIPTION;
        SCIMAttributeSchema specUri = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SPEC_URI;
        SCIMAttributeSchema documentationUri =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DOCUMENTATION_URI;
        Optional<MultiValuedAttribute> authTypes = getMultiValuedAttribute(authSchemesAttribute);
        if (!authTypes.isPresent()) {
            return Collections.emptyList();
        }
        MultiValuedAttribute authenticationSchemes = authTypes.get();
        List<AuthenticationScheme> authenticationSchemeList = new ArrayList<>();

        for (Attribute attributeValue : authenticationSchemes.getAttributeValues()) {
            ComplexAttribute authScheme = (ComplexAttribute) attributeValue;
            AuthenticationScheme authenticationScheme = new AuthenticationScheme();
            getSimpleAttributeValue(type, authScheme).ifPresent(authenticationScheme::setType);
            getSimpleAttributeValue(name, authScheme).ifPresent(authenticationScheme::setName);
            getSimpleAttributeValue(description, authScheme).ifPresent(authenticationScheme::setDescription);
            getSimpleAttributeValue(specUri, authScheme).map(s -> rethrowSupplier(() -> new URL(s)).get()).ifPresent(
                authenticationScheme::setSpecUri);
            getSimpleAttributeValue(documentationUri, authScheme).map(s -> rethrowSupplier(() -> new URL(s)).get())
                .ifPresent(authenticationScheme::setDocumentationUri);
            authenticationSchemeList.add(authenticationScheme);
        }

        return authenticationSchemeList;
    }

    /**
     * A multi-valued complex type that specifies supported authentication scheme properties.  To enable seamless
     * discovery of configurations, the service provider SHOULD, with the appropriate security considerations, make the
     * authenticationSchemes attribute publicly accessible without prior authentication.  REQUIRED.  The following
     * sub-attributes are defined:
     * <p>
     * type  The authentication scheme.  This specification defines the values "oauth", "oauth2", "oauthbearertoken",
     * "httpbasic", and "httpdigest".  REQUIRED.
     * <p>
     * name  The common authentication scheme name, e.g., HTTP Basic. REQUIRED.
     * <p>
     * description  A description of the authentication scheme. REQUIRED.
     * <p>
     * specUri  An HTTP-addressable URL pointing to the authentication scheme's specification.  OPTIONAL.
     * <p>
     * documentationUri  An HTTP-addressable URL pointing to the authentication scheme's usage documentation.
     * OPTIONAL.
     */
    public void setAuthenticationSchemes (List<AuthenticationScheme> authenticationSchemes) {
        SCIMAttributeSchema authSchemesAttribute =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.AUTHENTICATION_SCHEMES;
        SCIMAttributeSchema type = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.TYPE;
        SCIMAttributeSchema name = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.NAME;
        SCIMAttributeSchema description = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DESCRIPTION;
        SCIMAttributeSchema specUri = SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SPEC_URI;
        SCIMAttributeSchema documentationUri =
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.DOCUMENTATION_URI;
        MultiValuedAttribute authSchemes = getOrCrateMultivaluedAttribute(authSchemesAttribute);
        if (authenticationSchemes == null || authenticationSchemes.isEmpty()) {
            return;
        }
        for (AuthenticationScheme authenticationScheme : authenticationSchemes) {
            ComplexAttribute auth = new ComplexAttribute();
            rethrowConsumer(o -> DefaultAttributeFactory.createAttribute(authSchemesAttribute, (AbstractAttribute) o))
                .accept(auth);
            authSchemes.setAttributeValue(auth);

            getSetSubAttributeConsumer(auth).accept(type, authenticationScheme::getType);
            getSetSubAttributeConsumer(auth).accept(name, authenticationScheme::getName);
            getSetSubAttributeConsumer(auth).accept(description, authenticationScheme::getDescription);
            Optional.ofNullable(authenticationScheme.getSpecUri()).ifPresent(url -> {
                getSetSubAttributeConsumer(auth).accept(specUri, url::toString);
            });
            Optional.ofNullable(authenticationScheme.getDocumentationUri()).ifPresent(url -> {
                getSetSubAttributeConsumer(auth).accept(documentationUri, url::toString);
            });
        }
    }

    /**
     * @see #getAuthenticationSchemes()
     * @see #setAuthenticationSchemes(List)
     */
    public void addAuthenticationSchemes (AuthenticationScheme authenticationScheme,
                                          AuthenticationScheme... authenticationSchemes) {
        List<AuthenticationScheme> authenticationSchemeList = new ArrayList<>(getAuthenticationSchemes());
        authenticationSchemeList.add(authenticationScheme);
        if (authenticationSchemes != null) {
            authenticationSchemeList.addAll(Arrays.asList(authenticationSchemes));
        }
        setAuthenticationSchemes(authenticationSchemeList);
    }


}
