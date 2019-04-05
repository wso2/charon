package org.wso2.charon3.core.resourcetypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

/**
 *
 * <br><br>
 * created at: 04.04.2019
 * @author Pascal Knüppel
 */
class ResourceTypeTest implements FileReferences {

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

}