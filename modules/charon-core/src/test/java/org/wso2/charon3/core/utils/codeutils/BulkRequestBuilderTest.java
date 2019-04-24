package org.wso2.charon3.core.utils.codeutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.BulkResourceManager;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.setup.CharonInitializer;
import org.wso2.charon3.core.testsetup.FileReferences;

import java.util.ArrayList;
import java.util.List;

/**
 * author Pascal Knueppel <br>
 * created at: 23.04.2019 - 11:40 <br>
 * <br>
 */
public class BulkRequestBuilderTest extends CharonInitializer implements FileReferences {

    private static final Logger log = LoggerFactory.getLogger(BulkRequestBuilderTest.class);

    @Test
    public void testBulkRequestBuilder() throws AbstractCharonException {
        String userString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(userString, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        String groupString = readResourceFile(CREATE_GROUP_BREMEN_FILE);
        Group group = JSON_DECODER.decodeResource(groupString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());

        String bulkRequest = BulkRequestBuilder.builder(null)
            .setMethod(BulkRequestBuilder.Method.POST)
            .setPath(SCIMConstants.USER_ENDPOINT)
            .setData(user)
            .next()
            .setMethod(BulkRequestBuilder.Method.POST)
            .setPath(SCIMConstants.GROUP_ENDPOINT)
            .setData(group)
            .build();

        BulkRequestData bulkRequestData = JSON_DECODER.decodeBulkData(bulkRequest);
        Assertions.assertEquals(1, bulkRequestData.getSchemas().size());
        Assertions.assertEquals(SCIMConstants.BULK_REQUEST_URI, bulkRequestData.getSchemas().get(0));
        Assertions.assertNull(bulkRequestData.getFailOnErrors());

        BulkResourceManager bulkResourceManager = new BulkResourceManager(createResourceManagers());
        SCIMResponse response = bulkResourceManager.processBulkData(bulkRequest);
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, response.getResponseStatus());
        Mockito.verify(userResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());
        Mockito.verify(groupResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());

        BulkResponseData bulkResponseData = JSON_DECODER.decodeBulkResponseData(response.getResponseMessage());
        Assertions.assertEquals(2, bulkResponseData.getOperationResponseList().size());
        for (BulkResponseContent bulkResponseContent : bulkResponseData.getOperationResponseList()) {
            SCIMResponse scimResponse = bulkResponseContent.getScimResponse();
            Assertions.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseStatus());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5, 8, 13})
    public void testFailOnErrorsValue(int failOnErrors)
        throws InternalErrorException, BadRequestException, CharonException {
        String userString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(userString, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());

        String bulkRequest = BulkRequestBuilder.builder(failOnErrors)
            .setMethod(BulkRequestBuilder.Method.POST)
            .setPath(SCIMConstants.USER_ENDPOINT)
            .setData(user)
            .build();

        BulkRequestData bulkRequestData = JSON_DECODER.decodeBulkData(bulkRequest);
        Assertions.assertEquals(1, bulkRequestData.getSchemas().size());
        Assertions.assertEquals(SCIMConstants.BULK_REQUEST_URI, bulkRequestData.getSchemas().get(0));
        Assertions.assertEquals(failOnErrors, bulkRequestData.getFailOnErrors());
    }

    protected List<ResourceManager> createResourceManagers() {
        List<ResourceManager> resourceManagerList = new ArrayList<>();
        resourceManagerList.add(userManager);
        resourceManagerList.add(groupManager);
        return resourceManagerList;
    }
}