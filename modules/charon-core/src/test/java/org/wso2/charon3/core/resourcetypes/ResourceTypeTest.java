package org.wso2.charon3.core.resourcetypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * <br><br>
 * created at: 04.04.2019
 * @author Pascal Knüppel
 */
class ResourceTypeTest implements FileReferences {

    @BeforeEach
    public void registerEndpoints() {
        String baseUri = "https://localhost:8443/charon/scim/v2";
        Map<String, String> endpointMap = new HashMap<>();
        endpointMap.put(SCIMConstants.USER_ENDPOINT, baseUri + SCIMConstants.USER_ENDPOINT);
        endpointMap.put(SCIMConstants.RESOURCE_TYPE_ENDPOINT, baseUri + SCIMConstants.RESOURCE_TYPE_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMap);
    }

    @Test
    public void testResourceTypeDecoding() throws BadRequestException, CharonException {
        String userResourceTypeString = readResourceFile(USER_RESOURCE_TYPE_FILE);
        ListedResource listedResource = JSON_DECODER.decodeListedResource(userResourceTypeString,
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, ResourceType.class);

        ResourceType userResourceType = (ResourceType) listedResource.getResources().get(0);

        Assertions.assertEquals(SCIMConstants.USER_CORE_SCHEMA_URI, userResourceType.getSchema());
        Assertions.assertEquals(SCIMConstants.USER, userResourceType.getId());
        Assertions.assertEquals(SCIMConstants.USER, userResourceType.getName());
        Assertions.assertEquals(SCIMConstants.USER_ENDPOINT, userResourceType.getEndpoint());
        Assertions.assertEquals("User Account", userResourceType.getDescription());

        Assertions.assertEquals(1, userResourceType.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE_SCHEMA_URI, userResourceType.getSchemaList().get(0));

        Assertions.assertEquals(1, userResourceType.getSchemaExtensions().size());
        Assertions.assertEquals(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI,
            userResourceType.getSchemaExtensions().get(0).getSchema());

        /* test second entry */
        ResourceType groupResourceType = (ResourceType) listedResource.getResources().get(1);

        Assertions.assertEquals(SCIMConstants.GROUP_CORE_SCHEMA_URI, groupResourceType.getSchema());
        Assertions.assertEquals(SCIMConstants.GROUP, groupResourceType.getId());
        Assertions.assertEquals(SCIMConstants.GROUP, groupResourceType.getName());
        Assertions.assertEquals(SCIMConstants.GROUP_ENDPOINT, groupResourceType.getEndpoint());
        Assertions.assertEquals(SCIMConstants.GROUP, groupResourceType.getDescription());

        Assertions.assertEquals(1, groupResourceType.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE_SCHEMA_URI, groupResourceType.getSchemaList().get(0));

        Assertions.assertEquals(0, groupResourceType.getSchemaExtensions().size());
    }

    @Test
    public void testEncodingOfResourceType() throws CharonException, BadRequestException, InternalErrorException {
        ResourceType resourceType = new ResourceType();
        resourceType.replaceSchema(SCIMConstants.USER_CORE_SCHEMA_URI);
        resourceType.replaceId(SCIMConstants.USER);
        resourceType.replaceName(SCIMConstants.USER);
        resourceType.replaceDescription("User Account");
        resourceType.replaceEndpoint(SCIMConstants.USER_ENDPOINT);
        resourceType.addSchemaExtension(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI, true);

        String encodedResourceType = JSON_ENCODER.encodeSCIMObject(resourceType);
        ResourceType userResourceType = JSON_DECODER.decodeResource(encodedResourceType,
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, new ResourceType());

        Assertions.assertEquals(SCIMConstants.USER_CORE_SCHEMA_URI, userResourceType.getSchema());
        Assertions.assertEquals(SCIMConstants.USER, userResourceType.getId());
        Assertions.assertEquals(SCIMConstants.USER, userResourceType.getName());
        Assertions.assertEquals(SCIMConstants.USER_ENDPOINT, userResourceType.getEndpoint());
        Assertions.assertEquals("User Account", userResourceType.getDescription());

        Assertions.assertEquals(1, userResourceType.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE_SCHEMA_URI, userResourceType.getSchemaList().get(0));

        Assertions.assertEquals(1, userResourceType.getSchemaExtensions().size());
        Assertions.assertEquals(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI,
            userResourceType.getSchemaExtensions().get(0).getSchema());
    }

    @Test
    public void testEncodeListedResponseWithResourceType() throws CharonException, BadRequestException {
        ListedResource listedResource = new ListedResource();
        listedResource.setTotalResults(ResourceTypeRegistration.getResouceTypeCount());
        ResourceTypeRegistration.getResourceTypeList().forEach(listedResource::addResource);

        String encodedResourceTypeList = JSON_ENCODER.encodeSCIMObject(listedResource);

        ListedResource decodedList = JSON_DECODER.decodeListedResource(encodedResourceTypeList,
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, ResourceType.class);
        Assertions.assertEquals(2, decodedList.getTotalResults());
        Assertions.assertEquals(2, decodedList.getResources().size());
    }

    @Test
    public void testCreateResourceTypeSchema() {
        // unregister endpoints to provoke exception
        AbstractResourceManager.setEndpointURLMap(new HashMap<>());

        Assertions.assertThrows(NotFoundException.class,
            () -> new ResourceType("User", "User", "description", SCIMConstants.USER_ENDPOINT,
                SCIMSchemaDefinitions.SCIM_USER_SCHEMA));

        registerEndpoints();

        Assertions.assertThrows(NullPointerException.class,
            () -> new ResourceType("User", null, "description", SCIMConstants.USER_ENDPOINT,
                SCIMSchemaDefinitions.SCIM_USER_SCHEMA));
        Assertions.assertThrows(NullPointerException.class,
            () -> new ResourceType("User", "User", "description", SCIMConstants.USER_ENDPOINT, null));
        Assertions.assertThrows(NullPointerException.class,
            () -> new ResourceType("User", "User", "description", null, SCIMSchemaDefinitions.SCIM_USER_SCHEMA));

        ResourceType resourceType = new ResourceType("User", "User", "description", SCIMConstants.USER_ENDPOINT,
            SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
        Assertions.assertThrows(ConflictException.class, () -> ResourceTypeRegistration.addResourceType(resourceType));

        Assertions.assertEquals(2, ResourceTypeRegistration.getResourceTypeList().size());
        List<String> resourceTypeNames = ResourceTypeRegistration.getResourceTypeList().stream().map(
            ResourceType::getName).collect(Collectors.toList());
        Assertions.assertTrue(resourceTypeNames.contains(SCIMConstants.USER));
    }
}
