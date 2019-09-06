package org.wso2.charon3.core.testsetup.client;


import org.wso2.charon3.core.schema.SCIMConstants;


/**
 * Constant definitions for the OpenID Connect clients.
 *
 * @author Sebastian Loesch
 */
public final class ClientSchemaConstants {


    private ClientSchemaConstants () {
    }

    public static final String NAMESPACE = "urn:de:charon:scim:schemas:dvdv:2.0";

    /**
     * The clients endpoint name.
     */
    public static final String CLIENTS_ENDPOINT = "/Clients";

    /**
     * The clients resource type name.
     */
    public static final String CLIENT_RESOURCE_TYPE = "Client";

    /**
     * The client SCIM schema URI.
     */
    public static final String CLIENT_CORE_SCHEMA_URI = NAMESPACE + ":" + CLIENT_RESOURCE_TYPE;

    /**
     * the schema name.
     */
    public static final String CLIENT_EXTENSION_NAME = "OpenID Connect Client";

    /**
     * the schema description.
     */
    public static final String CLIENT_EXTENSION_DESCRIPTION =
        "An OpenID Connect client representation that can be used for OAuth2 authentication";

    /**
     * the string name of the client name identifier.
     */
    public static final String CLIENT_ID = "clientId";

    /**
     * the URI value for {@link #CLIENT_ID}.
     */
    public static final String CLIENT_ID_URI = CLIENT_CORE_SCHEMA_URI + ":" + CLIENT_ID;

    /**
     * the description of the client id field.
     */
    public static final String CLIENT_ID_DESCRIPTION =
        "A unique identifier for the OpenID Connect client that is used" +
            "as an identification string to let the provider know which" + "client did just request a token";

    /**
     * the string name of the client client secret identifier.
     */
    public static final String CLIENT_SECRET = "clientSecret";

    /**
     * the URI value for {@link #CLIENT_SECRET}.
     */
    public static final String CLIENT_SECRET_URI = CLIENT_CORE_SCHEMA_URI + ":" + CLIENT_SECRET;

    /**
     * the description of the client secret field.
     */
    public static final String CLIENT_SECRET_DESCRIPTION =
        "the secret password of the client that is needed to " + "authenticate at the OpenID Connect token endpoint";

    /**
     * the string name of the client redirect URI list identifier.
     */
    public static final String REDIRECT_URIS = "redirectUris";

    /**
     * the URI value for {@link #REDIRECT_URIS}.
     */
    public static final String REDIRECT_URI_LIST_URI = CLIENT_CORE_SCHEMA_URI + ":" + REDIRECT_URIS;

    /**
     * the description of the redirect URI field.
     */
    public static final String REDIRECT_URIS_DESCRIPTION =
        "a list of redirect uris that are needed to create OpenID " + "Connect clients on the server side";

    /**
     * the string name of the client certificate to authenticate with signed JWTs identifier.
     */
    public static final String JWT_X509 = "jwtX509";

    /**
     * the URI value for {@link #JWT_X509}.
     */
    public static final String JWT_X509_URI = CLIENT_CORE_SCHEMA_URI + ":" + JWT_X509;

    /**
     * the description of the JWT X.509 field.
     */
    public static final String JWT_X509_DESCRIPTION =
        "a x509 certificate that is used to verify signatures on signed " + "client assertions at the token endpoint";

    /**
     * the string name of the active identifier.
     */
    public static final String ACTIVE = SCIMConstants.UserSchemaConstants.ACTIVE;

    /**
     * the URI value for {@link #ACTIVE}.
     */
    public static final String ACTIVE_URI = CLIENT_CORE_SCHEMA_URI + ":" + ACTIVE;

    /**
     * the description of the active field.
     */
    public static final String ACTIVE_DESCRIPTION = "a boolean that describes if this client is still enabled or not.";

    /**
     * the string name of the self authorization identifier.
     */
    public static final String SELF_AUTHORISATION = "selfAuthorisation";


    /**
     * the URI value for {@link #SELF_AUTHORISATION}.
     */
    public static final String SELF_AUTHORISATION_URI = CLIENT_CORE_SCHEMA_URI + ":" + SELF_AUTHORISATION;

    /**
     * the description of the self authorization field.
     */
    public static final String SELF_AUTHORISATION_DESCRIPTION =
        "a boolean that describes if this client is allowed to " + "request an access token for himself";

    /**
     * the string name of the jwks uri.
     */
    public static final String JWKS_URI = "jwksUri";

    /**
     * the URI value for {@link #JWKS_URI}.
     */
    public static final String JWKS_URI_URI = CLIENT_CORE_SCHEMA_URI + ":" + JWKS_URI;

    /**
     * the description of the jwks uri.
     */
    public static final String JWKS_URI_DESCRIPTION =
        "The URI that is used to retrieve the public key necessary for JWT signature validation. " +
            "This is part of the OAuth2 JWT login in the client_credentials grant";
}
