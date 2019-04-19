package org.wso2.charon3.core.protocol.endpoints;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;
import org.wso2.charon3.core.testsetup.client.ClientSchemaConstants;
import org.wso2.charon3.core.testsetup.client.ClientSchemaDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <br><br>
 * created at: 08.04.2019
 * @author Pascal Knüppel
 */
class ResourceTypeResourceManagerTest implements FileReferences {

    @BeforeEach
    public void registerEndpoints() {
        String baseUri = "https://localhost:8443/charon/scim/v2";
        Map<String, String> endpointMap = new HashMap<>();
        endpointMap.put(SCIMConstants.USER_ENDPOINT, baseUri + SCIMConstants.USER_ENDPOINT);
        endpointMap.put(SCIMConstants.GROUP_ENDPOINT, baseUri + SCIMConstants.GROUP_ENDPOINT);
        endpointMap.put(SCIMConstants.USER_SCHEMA_ENDPOINT, baseUri + SCIMConstants.USER_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.ENTERPRISE_USER_SCHEMA_ENDPOINT,
            baseUri + SCIMConstants.ENTERPRISE_USER_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.GROUP_SCHEMA_ENDPOINT, baseUri + SCIMConstants.GROUP_SCHEMA_ENDPOINT);
        endpointMap.put(SCIMConstants.RESOURCE_TYPE_ENDPOINT, baseUri + SCIMConstants.RESOURCE_TYPE_ENDPOINT);
        endpointMap.put(ClientSchemaConstants.CLIENTS_ENDPOINT, baseUri + ClientSchemaConstants.CLIENTS_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMap);
    }

    @AfterEach
    public void removeAddedClientResourceType() {
        ResourceTypeRegistration.getResourceTypeList().removeIf(
            resourceType -> resourceType.getName().equals(ClientSchemaConstants.CLIENT_RESOURCE_TYPE));
    }

    @Test
    public void testGetResourcesFromResourceTypeEndpoint()
        throws BadRequestException, CharonException, NotFoundException {
        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        SCIMResponse scimResponse = resourceManager.listWithGET(null, 0, Integer.MAX_VALUE, null, null, null, null,
            null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());

        ListedResource listedResource = JSON_DECODER.decodeListedResource(scimResponse.getResponseMessage(),
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, ResourceType.class);

        Assertions.assertEquals(2, listedResource.getTotalResults());
        Assertions.assertEquals(2, listedResource.getResources().size());

        String resourceTypeEndpoint = AbstractResourceManager.getResourceEndpointURL(
            SCIMConstants.RESOURCE_TYPE_ENDPOINT);

        ResourceType userType = getResourceType(SCIMConstants.USER, listedResource);
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE, userType.getResourceType());
        Assertions.assertEquals(resourceTypeEndpoint + "/" + SCIMConstants.USER, userType.getLocation());

        ResourceType groupType = getResourceType(SCIMConstants.GROUP, listedResource);
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE, groupType.getResourceType());
        Assertions.assertEquals(resourceTypeEndpoint + "/" + SCIMConstants.GROUP, groupType.getLocation());
    }

    private ResourceType getResourceType(String type, ListedResource listedResource) {
        return listedResource.getResources().stream().map(rt -> (ResourceType) rt).filter(
            rt -> rt.getName().equals(type)).findAny().orElse(null);
    }

    @Test
    public void testGetSingleResource() throws InternalErrorException, BadRequestException, CharonException {
        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        final String resourceId = SCIMConstants.USER;
        SCIMResponse scimResponse = resourceManager.get(resourceId, null, null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());
        ResourceType resourceType = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, new ResourceType());
        Assertions.assertEquals(resourceId, resourceType.getId());
    }

    @Test
    public void testGetNotExistentResource() throws BadRequestException, CharonException {
        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        final String resourceId = "Unknown";
        SCIMResponse scimResponse = resourceManager.get(resourceId, null, null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND, scimResponse.getResponseStatus());
        NotFoundException notFoundException = JSON_DECODER.decodeCharonException(scimResponse.getResponseMessage(),
            NotFoundException.class);
        Assertions.assertNotNull(notFoundException);
    }

    @Test
    public void addANewResourceType() throws BadRequestException, CharonException, NotFoundException {
        ResourceType clientType = new ResourceType(ClientSchemaConstants.CLIENT_RESOURCE_TYPE,
            ClientSchemaConstants.CLIENT_RESOURCE_TYPE, "OpenID Connect Clients",
            ClientSchemaConstants.CLIENTS_ENDPOINT, ClientSchemaDefinition.SCIM_CLIENT_SCHEMA);

        ResourceTypeRegistration.addResourceType(clientType);

        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        SCIMResponse scimResponse = resourceManager.listWithGET(null, 0, Integer.MAX_VALUE, null, null, null, null,
            null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());

        ListedResource listedResource = JSON_DECODER.decodeListedResource(scimResponse.getResponseMessage(),
            SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA, ResourceType.class);

        Assertions.assertEquals(3, listedResource.getTotalResults());
        Assertions.assertEquals(3, listedResource.getResources().size());

        String resourceTypeEndpoint = AbstractResourceManager.getResourceEndpointURL(
            SCIMConstants.RESOURCE_TYPE_ENDPOINT);
        clientType = getResourceType(ClientSchemaConstants.CLIENT_RESOURCE_TYPE, listedResource);
        Assertions.assertEquals(SCIMConstants.RESOURCE_TYPE, clientType.getResourceType());
        Assertions.assertEquals(resourceTypeEndpoint + "/" + ClientSchemaConstants.CLIENT_RESOURCE_TYPE,
            clientType.getLocation());
    }

}
