package org.wso2.charon3.core.protocol.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
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
        endpointMap.put(SCIMConstants.RESOURCE_TYPE_ENDPOINT, baseUri + SCIMConstants.RESOURCE_TYPE_ENDPOINT);
        endpointMap.put(ClientSchemaConstants.CLIENTS_ENDPOINT, baseUri + ClientSchemaConstants.CLIENTS_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMap);
    }

    @Test
    public void testGetResourcesFromResourceTypeEndpoint()
        throws BadRequestException, CharonException, NotFoundException {
        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        SCIMResponse scimResponse = resourceManager.get(null, null, null, null);
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
    public void addANewResourceType() throws BadRequestException, CharonException, NotFoundException {
        ResourceType clientType = new ResourceType(ClientSchemaConstants.CLIENT_RESOURCE_TYPE,
            ClientSchemaConstants.CLIENT_RESOURCE_TYPE, "OpenID Connect Clients",
            ClientSchemaConstants.CLIENTS_ENDPOINT, ClientSchemaDefinition.SCIM_CLIENT_SCHEMA);

        ResourceTypeRegistration.addResourceType(clientType);

        ResourceTypeResourceManager resourceManager = new ResourceTypeResourceManager();
        SCIMResponse scimResponse = resourceManager.get(null, null, null, null);
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