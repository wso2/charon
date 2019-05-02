package org.wso2.charon3.core.testsetup.client;


import org.wso2.charon3.core.attributes.AbstractAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.ACTIVE_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.CLIENT_ID_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.CLIENT_SECRET_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.JWKS_URI_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.JWT_X509_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.REDIRECT_URIS_ATTRIBUTE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.SCIM_CLIENT_SCHEMA;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition.SELF_AUTHORISATION_ATTRIBUTE;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;


/**
 * Represents an OpenID Connect client for Keycloak as SCIM representation.
 *
 * @author Pascal Knueppel
 * @author Sebastian Loesch
 */
@SuppressWarnings("serial")
public class Client extends AbstractSCIMObject {

    public Client() {
    }

    /**
     * Set the schemas for SCIM client.
     */
    public final void setSchemas() {
        SCIM_CLIENT_SCHEMA.getSchemasList().stream().forEach(this::setSchema);
    }

    /**
     * Gets the client id from this SCIM resource.
     */
    public String getClientId() {
        return getSimpleAttribute(CLIENT_ID_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(
            null);
    }

    /**
     * Puts the given client id into the object and overrides the value from before if it was present.
     *
     * @param clientId the new client id value for this object
     */
    public void setClientId(String clientId) {
        replaceSimpleAttribute(CLIENT_ID_ATTRIBUTE, clientId);
    }

    /**
     * Gets the client secret from this SCIM resource.
     */
    public String getClientSecret() {
        return getSimpleAttribute(CLIENT_SECRET_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(
            null);
    }

    /**
     * Puts the given client secret into the object and overrides the value from before if it was present.
     *
     * @param clientSecret the new client secret value for this object
     */
    public void setClientSecret(String clientSecret) {
        replaceSimpleAttribute(CLIENT_SECRET_ATTRIBUTE, clientSecret);
    }

    /**
     * Gets the active value from this SCIM resource.
     */
    public boolean isActive() {
        return getSimpleAttribute(ACTIVE_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getBooleanValue)).orElse(
            false);
    }

    /**
     * Puts the given active value into the object and overrides the value from before if it was present.
     *
     * @param active the new active value for this object
     */
    public void setActive(boolean active) {
        replaceSimpleAttribute(ACTIVE_ATTRIBUTE, active);
    }

    /**
     * Gets the JWT X.509 certificate from this SCIM resource as base64 encoded string.
     */
    public String getJwtX509() {
        return getSimpleAttribute(JWT_X509_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(
            null);
    }

    /**
     * Puts the given JWT X.509 certificate into the object and overrides the value from before if it was
     * present.
     *
     * @param jwtX509 the new JWT X.509 certificate as base64 encoded string
     */
    public void setJwtX509(String jwtX509) {
        replaceSimpleAttribute(JWT_X509_ATTRIBUTE, jwtX509);
    }

    /**
     * Gets the jwks uri value from this SCIM resource.
     */
    public String getJwksUri() {
        return getSimpleAttribute(JWKS_URI_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(
            null);
    }

    /**
     * Puts the given jwks uri value into the object and overrides the value from before if it was present.
     *
     * @param jwksUri the new jwks uri value for this object
     */
    public void setJwksUri(String jwksUri) {
        replaceSimpleAttribute(JWKS_URI_ATTRIBUTE, jwksUri);
    }

    /**
     * Gets the use jwks uri value from this SCIM resource. Only Getter since this attribute is dependent on
     * whether jwks uri is set or not.
     */
    public boolean isUseJwksUri() {
        return getJwksUri() != null;
    }

    /**
     * Gets the redirect URIs from this SCIM resource.
     */
    public List<String> getRedirectUris() {
        return getPrimitiveStringValuesOfMultiValuedAttribute(REDIRECT_URIS_ATTRIBUTE);
    }

    /**
     * Puts the given redirect uris into the object and overrides the value from before if it was present.
     *
     * @param redirectUris the new redirect uris list for this object
     */
    public void setRedirectUris(List<String> redirectUris) {
        setMultiValuedAttribute(REDIRECT_URIS_ATTRIBUTE, new ArrayList<>(redirectUris));
    }


    /**
     * Gets the self authorization value from this SCIM resource to tell if this client is allowed to request
     * access tokens for himself.
     */
    public boolean isSelfAuthorizationEnabled() {
        return getSimpleAttribute(SELF_AUTHORISATION_ATTRIBUTE).map(rethrowFunction(SimpleAttribute::getBooleanValue))
                                                               .orElse(false);
    }

    /**
     * Puts the given self authorization value into the object and overrides the value from before if it was
     * present.
     *
     * @param selfAuthorisation the new self authorization value for this object
     */
    public void setSelfAuthorizationEnabled(boolean selfAuthorisation) {
        replaceSimpleAttribute(SELF_AUTHORISATION_ATTRIBUTE, selfAuthorisation);
    }

    /**
     * Gets the primitive string values of the given attribute definition.
     *
     * @param attributeSchema the attribute definition
     * @return the values of the attribute or an empty list
     */
    private List<String> getPrimitiveStringValuesOfMultiValuedAttribute(SCIMAttributeSchema attributeSchema) {
        MultiValuedAttribute multiValuedAttribute = getMultiValuedAttribute(attributeSchema).orElse(null);
        if (multiValuedAttribute == null) {
            return Collections.emptyList();
        }
        return multiValuedAttribute.getAttributePrimitiveValues().stream().map(
            attributePrimitiveValue -> (String) attributePrimitiveValue).collect(Collectors.toList());
    }

    /**
     * Sets the values of a multi valued attribute with primitive values.
     *
     * @param attributeDefinition the attribute schema definition
     * @param values the values of the attributes
     */
    private void setMultiValuedAttribute(SCIMAttributeSchema attributeDefinition, List<Object> values) {
        MultiValuedAttribute multiValuedAttribute = getMultiValuedAttribute(attributeDefinition).orElse(null);
        if (multiValuedAttribute == null) {
            multiValuedAttribute = new MultiValuedAttribute(attributeDefinition.getName());
            rethrowConsumer(attribute -> DefaultAttributeFactory
                                             .createAttribute(attributeDefinition, (AbstractAttribute) attribute))
                .accept(multiValuedAttribute);
            getResource().setAttribute(multiValuedAttribute);
        }
        multiValuedAttribute.setAttributePrimitiveValues(new ArrayList<>(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSCIMObject getResource() {
        return this;
    }
}
