package org.wso2.charon3.core.config;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.net.URL;
import java.util.List;
import java.util.function.Function;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 *
 * <br><br>
 * created at: 18.04.2019
 * @author Pascal Knüppel
 */
public class CharonConfigurationTest {

    private static final Logger log = LoggerFactory.getLogger(CharonConfigurationTest.class);

    /**
     * generates a URL object from a string
     */
    private static Function<String, URL> toUrl = url -> rethrowSupplier(() -> new URL(url)).get();

    @Test
    public void testCreateAndReadServiceProviderConfig() throws CharonException {
        CharonConfiguration configuration = CharonConfiguration.getInstance();
        Assertions.assertEquals(1, configuration.getSchemaList().size());
        String schema = configuration.getSchemaList().get(0);
        Assertions.assertEquals(SCIMConstants.SERVICE_PROVIDER_CONFIG_SCHEMA_URI, schema);

        configuration.setPatch(new ScimFeature(true));
        configuration.setChangePassword(new ScimFeature(true));
        configuration.seteTag(new ScimFeature(true));
        configuration.setSort(new ScimFeature(true));

        final int maxResults = 50;
        configuration.setFilter(new FilterFeature(true, maxResults));

        final int maxOperations = 10;
        final int maxPayload = 1024 * 50;
        configuration.setBulk(new BulkFeature(true, maxOperations, maxPayload));

        final URL documentationUri = toUrl.apply("https://localhost:8443/scim/v2/doc");
        configuration.setDocumentationUri(documentationUri);

        final String type = "oauthbearertoken";
        final String name = "OAuth2 Bearer Token";
        final String description = "Authentication scheme using the OAuth2 Bearer Token Standard";
        final URL specUri = toUrl.apply("https://tools.ietf.org/html/rfc6750");
        final URL docUri = toUrl.apply("https://localhost:8443/scim/v2/doc");
        configuration.addAuthenticationSchemes(new AuthenticationScheme(type, name, description, specUri, docUri));

        Assertions.assertTrue(configuration.getPatch().isSupported());
        Assertions.assertTrue(configuration.getChangePassword().isSupported());
        Assertions.assertTrue(configuration.geteTag().isSupported());
        Assertions.assertTrue(configuration.getSort().isSupported());

        Assertions.assertTrue(configuration.getFilter().isSupported());
        Assertions.assertEquals(maxResults, configuration.getFilter().getMaxResults());

        Assertions.assertTrue(configuration.getBulk().isSupported());
        Assertions.assertEquals(maxOperations, configuration.getBulk().getMaxOperations());
        Assertions.assertEquals(maxPayload, configuration.getBulk().getMaxPayLoadSize());

        List<AuthenticationScheme> authenticationSchemeList = configuration.getAuthenticationSchemes();
        Assertions.assertEquals(1, authenticationSchemeList.size());
        AuthenticationScheme authenticationScheme = authenticationSchemeList.get(0);
        Assertions.assertEquals(type, authenticationScheme.getType());
        Assertions.assertEquals(name, authenticationScheme.getName());
        Assertions.assertEquals(description, authenticationScheme.getDescription());
        Assertions.assertEquals(specUri, authenticationScheme.getSpecUri());
        Assertions.assertEquals(documentationUri, authenticationScheme.getDocumentationUri());

        log.warn(new JSONEncoder().encodeSCIMObject(CharonConfiguration.getInstance()));
    }

    @Test
    public void testUnconfiguredServiceProvider() throws CharonException {
        CharonConfiguration configuration = CharonConfiguration.getInstance();
        String encoded = new JSONEncoder().encodeSCIMObject(configuration);
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(encoded));

        Assertions.assertFalse(
            decodedJsonObj.isNull(SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.FILTER.getName()));
        Assertions.assertFalse(
            decodedJsonObj.isNull(SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.PATCH.getName()));
        Assertions.assertFalse(
            decodedJsonObj.isNull(SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.ETAG.getName()));
        Assertions.assertFalse(
            decodedJsonObj.isNull(SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.SORT.getName()));
        Assertions.assertFalse(
            decodedJsonObj.isNull(SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.BULK.getName()));
        Assertions.assertFalse(decodedJsonObj.isNull(
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.AUTHENTICATION_SCHEMES.getName()));
        Assertions.assertFalse(decodedJsonObj.isNull(
            SCIMSchemaDefinitions.SCIMServiceProviderConfigSchemaDefinition.CHANGE_PASSWORD.getName()));

        log.warn(encoded);
    }

}
