package org.wso2.charon3.core.protocol.endpoints;

import org.hamcrest.Matchers;
import org.hamcrest.junit.MatcherAssert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.config.SchemaRegistration;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.SchemaDefinition;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.client.ClientSchemaConstants;
import org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * .<br><br>
 * created at: 19.04.2019
 *
 * @author Pascal Knüppel
 */
public class SchemasResourceManagerTest {

    private static final Logger log = LoggerFactory.getLogger(SchemasResourceManagerTest.class);

    /**
     * initializes the endpoints that need to be registered within the {@link AbstractResourceManager}.
     */
    @BeforeEach
    public void registerEndpoints () {
        String baseUri = "https://localhost:8443/charon/scim/v2";
        Map<String, String> endpointMap = new HashMap<>();
        endpointMap.put(SCIMConstants.USER_ENDPOINT, baseUri + SCIMConstants.USER_ENDPOINT);
        endpointMap.put(SCIMConstants.GROUP_ENDPOINT, baseUri + SCIMConstants.GROUP_ENDPOINT);
        endpointMap.put(SCIMConstants.USER_SCHEMA_ENDPOINT, baseUri + SCIMConstants.USER_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.ENTERPRISE_USER_SCHEMA_ENDPOINT,
            baseUri + SCIMConstants.ENTERPRISE_USER_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.GROUP_SCHEMA_ENDPOINT, baseUri + SCIMConstants.GROUP_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.RESOURCE_TYPE_ENDPOINT, baseUri + SCIMConstants.RESOURCE_TYPE_ENDPOINT);
        endpointMap.put(SCIMConstants.SCHEMAS_ENDPOINT, baseUri + SCIMConstants.SCHEMAS_ENDPOINT);
        endpointMap.put(ClientSchemaConstants.CLIENTS_ENDPOINT, baseUri + ClientSchemaConstants.CLIENTS_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMap);
    }

    @AfterEach
    public void removeAddedClientResourceType () {
        ResourceTypeRegistration.getResourceTypeList().removeIf(
            resourceType -> resourceType.getName().equals(ClientSchemaConstants.CLIENT_RESOURCE_TYPE));
        SchemaRegistration.getInstance().removeSchema(ClientSchemaConstants.CLIENT_CORE_SCHEMA_URI);
    }

    @ParameterizedTest
    @ValueSource(strings = {SCIMConstants.USER_CORE_SCHEMA_URI, SCIMConstants.GROUP_CORE_SCHEMA_URI,
        SCIMConstants.ENTERPRISE_USER_SCHEMA_URI, ClientSchemaConstants.CLIENT_CORE_SCHEMA_URI})
    public void testGetASingleSchema (String schemaUri)
        throws InternalErrorException, BadRequestException, CharonException {
        ResourceType clientType = new ResourceType(ClientSchemaConstants.CLIENT_RESOURCE_TYPE,
            ClientSchemaConstants.CLIENT_RESOURCE_TYPE, "OpenID Connect Clients",
            ClientSchemaConstants.CLIENTS_ENDPOINT, ClientSchemaDefinition.SCIM_CLIENT_SCHEMA);

        ResourceTypeRegistration.addResourceType(clientType);

        SchemasResourceManager schemasResourceManager = new SchemasResourceManager();
        SCIMResponse scimResponse = schemasResourceManager.get(schemaUri);
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());

        log.warn(scimResponse.getResponseMessage());

        SchemaDefinition schemaDefinition = new JSONDecoder().decodeResource(scimResponse.getResponseMessage(),
            SCIMSchemaDefinitions.SCIM_SCHEMA_DEFINITION_SCHEMA, new SchemaDefinition());
        Assertions.assertEquals(schemaUri, schemaDefinition.getId());

        Assertions.assertEquals(1, schemaDefinition.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.SCHEMA_URI, schemaDefinition.getSchemaList().get(0));
    }

    @Test
    public void testListAllSchemata () throws BadRequestException, CharonException {
        SchemasResourceManager schemasResourceManager = new SchemasResourceManager();
        SCIMResponse scimResponse = schemasResourceManager.listResources();
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());

        log.warn(scimResponse.getResponseMessage());
        JSONObject jsonObject = new JSONObject(new JSONTokener(scimResponse.getResponseMessage()));
        JSONArray resources =
            jsonObject.getJSONArray(SCIMSchemaDefinitions.ListedResourceSchemaDefinition.RESOURCES.getName());
        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource = resources.getJSONObject(i);
            MatcherAssert.assertThat(resource.optString(SCIMConstants.CommonSchemaConstants.SCHEMAS),
                Matchers.not(Matchers.emptyOrNullString()));
        }

        ListedResource listedResource = new JSONDecoder().decodeListedResource(scimResponse.getResponseMessage(),
            SCIMSchemaDefinitions.SCIM_SCHEMA_DEFINITION_SCHEMA, SchemaDefinition.class);
        Assertions.assertEquals(3, listedResource.getTotalResults());

        listedResource.getResources().forEach(scimObject -> {
            Assertions.assertEquals(1, scimObject.getSchemaList().size());
            Assertions.assertEquals(SCIMConstants.SCHEMA_URI, scimObject.getSchemaList().get(0));
        });

        validateAttributes(listedResource, SCIMConstants.USER_CORE_SCHEMA_URI, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
        validateAttributes(listedResource, SCIMConstants.GROUP_CORE_SCHEMA_URI,
            SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
        validateAttributes(listedResource, SCIMConstants.ENTERPRISE_USER_SCHEMA_URI,
            SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA);

        // validate that subattributes are also listed
        SchemaDefinition groupSchema = getSchemaDefinition(listedResource, SCIMConstants.GROUP_CORE_SCHEMA_URI);
        Optional<MultiValuedAttribute> attributesOptional = groupSchema.getMultiValuedAttribute(
            SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES);
        Assertions.assertTrue(attributesOptional.isPresent());
        MultiValuedAttribute attributes = attributesOptional.get();
        ComplexAttribute membersDefinitionAttribute =
            (ComplexAttribute) attributes.getAttributeValues().stream().filter(attribute -> {
                ComplexAttribute complexAttribute = (ComplexAttribute) attribute;
                SimpleAttribute name = (SimpleAttribute) rethrowSupplier(() -> complexAttribute.getSubAttribute(
                    SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_NAME.getName())).get();
                return rethrowSupplier(name::getStringValue).get().equals(
                    SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS.getName());
            }).findAny().orElseThrow(() -> new IllegalStateException("members definition was not found"));
        MultiValuedAttribute subAttributes = (MultiValuedAttribute) membersDefinitionAttribute.getSubAttribute(
            SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_SUB_ATTRIBUTES.getName());
        Assertions.assertNotNull(subAttributes);
        Assertions.assertEquals(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS.getSubAttributeSchemas().size(),
            subAttributes.getAttributeValues().size());
    }

    private void validateAttributes (ListedResource listedResource,
                                     String schemaUri,
                                     ResourceTypeSchema resourceTypeSchema) {
        SchemaDefinition schemaDefinition = getSchemaDefinition(listedResource, schemaUri);
        MultiValuedAttribute attributes = (MultiValuedAttribute) schemaDefinition.getAttribute(
            SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES.getName());
        Assertions.assertEquals(resourceTypeSchema.getAttributesList().size(), attributes.getAttributeValues().size());
    }

    private SchemaDefinition getSchemaDefinition (ListedResource listedResource, String schemaUri) {
        return listedResource.getResources().stream().map(scimObject -> (SchemaDefinition) scimObject).filter(
            schemaDefinition -> schemaDefinition.getId().equals(schemaUri)).findAny().orElseThrow(
            () -> new IllegalStateException("user schema must be present"));
    }
}
