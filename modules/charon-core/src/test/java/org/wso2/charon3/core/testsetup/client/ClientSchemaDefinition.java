package org.wso2.charon3.core.testsetup.client;

import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.Collections;

import static org.wso2.charon3.core.schema.SCIMAttributeSchema.createSCIMAttributeSchema;
import static org.wso2.charon3.core.schema.SCIMResourceTypeSchema.createSCIMResourceSchema;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.ACTIVE;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.ACTIVE_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.ACTIVE_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_CORE_SCHEMA_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_ID;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_ID_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_ID_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_SECRET;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_SECRET_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.CLIENT_SECRET_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWKS_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWKS_URI_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWKS_URI_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWT_X509;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWT_X509_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.JWT_X509_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.REDIRECT_URIS;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.REDIRECT_URIS_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.REDIRECT_URI_LIST_URI;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.SELF_AUTHORISATION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.SELF_AUTHORISATION_DESCRIPTION;
import static org.wso2.charon3.core.testsetup.client.ClientSchemaConstants.SELF_AUTHORISATION_URI;


/**
 * This class holds the schema definition for OpenID Connect clients.
 *
 * @author Pascal Knueppel
 */
public final class ClientSchemaDefinition {

    private ClientSchemaDefinition() {
    }

    /**
     * the SCIM definition for the client id.
     */
    public static final SCIMAttributeSchema CLIENT_ID_ATTRIBUTE = createSCIMAttributeSchema(CLIENT_ID_URI, CLIENT_ID,
        SCIMDefinitions.DataType.STRING, false, CLIENT_ID_DESCRIPTION, false, false,
        SCIMDefinitions.Mutability.IMMUTABLE, SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.SERVER, null,
        null, null);

    /**
     * the SCIM definition for the client secret.
     */
    public static final SCIMAttributeSchema CLIENT_SECRET_ATTRIBUTE = createSCIMAttributeSchema(CLIENT_SECRET_URI,
        CLIENT_SECRET, SCIMDefinitions.DataType.STRING, false, CLIENT_SECRET_DESCRIPTION, false, true,
        SCIMDefinitions.Mutability.READ_ONLY, SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.SERVER, null,
        null, null);

    /**
     * the SCIM definition for the oauth2 redirect uris.
     */
    public static final SCIMAttributeSchema REDIRECT_URIS_ATTRIBUTE = createSCIMAttributeSchema(REDIRECT_URI_LIST_URI,
        REDIRECT_URIS, SCIMDefinitions.DataType.STRING, true, REDIRECT_URIS_DESCRIPTION, true, false,
        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.NONE, null,
        null, null);

    /**
     * the SCIM definition for jwt x509 certificates.
     */
    public static final SCIMAttributeSchema JWT_X509_ATTRIBUTE = createSCIMAttributeSchema(JWT_X509_URI, JWT_X509,
        SCIMDefinitions.DataType.STRING, false, JWT_X509_DESCRIPTION, false, false,
        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.SERVER,
        null, null, null);

    /**
     * the SCIM definition for the client is active attribute.
     */
    public static final SCIMAttributeSchema ACTIVE_ATTRIBUTE = createSCIMAttributeSchema(ACTIVE_URI, ACTIVE,
        SCIMDefinitions.DataType.BOOLEAN, false, ACTIVE_DESCRIPTION, true, false, SCIMDefinitions.Mutability.READ_WRITE,
        SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.NONE, null, null, null);

    /**
     * the SCIM definition for the attribute that defines if client-credentials flow is enabled or not.
     */
    public static final SCIMAttributeSchema SELF_AUTHORISATION_ATTRIBUTE = createSCIMAttributeSchema(
        SELF_AUTHORISATION_URI, SELF_AUTHORISATION, SCIMDefinitions.DataType.BOOLEAN, false,
        SELF_AUTHORISATION_DESCRIPTION, true, false, SCIMDefinitions.Mutability.READ_WRITE,
        SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.NONE, null, null, null);

    /**
     * the SCIM definition the url of the certificate to validate JWTs.
     */
    public static final SCIMAttributeSchema JWKS_URI_ATTRIBUTE = createSCIMAttributeSchema(JWKS_URI_URI, JWKS_URI,
        SCIMDefinitions.DataType.STRING, false, JWKS_URI_DESCRIPTION, true, false,
        SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT, SCIMDefinitions.Uniqueness.NONE, null,
        null, null);

    /**
     * the SCIM schema definition for OpenID Connect clients.
     */
    public static final SCIMResourceTypeSchema SCIM_CLIENT_SCHEMA = createSCIMResourceSchema(
        new ArrayList<>(Collections.singletonList(CLIENT_CORE_SCHEMA_URI)),
        ClientSchemaConstants.CLIENT_EXTENSION_NAME,
        ClientSchemaConstants.CLIENT_EXTENSION_DESCRIPTION,
        SCIMSchemaDefinitions.ID,
        SCIMSchemaDefinitions.EXTERNAL_ID, SCIMSchemaDefinitions.META, CLIENT_ID_ATTRIBUTE, CLIENT_SECRET_ATTRIBUTE,
        REDIRECT_URIS_ATTRIBUTE, JWT_X509_ATTRIBUTE, ACTIVE_ATTRIBUTE, SELF_AUTHORISATION_ATTRIBUTE,
        JWKS_URI_ATTRIBUTE);
}
