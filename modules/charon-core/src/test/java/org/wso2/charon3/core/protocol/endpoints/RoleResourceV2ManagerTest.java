/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.protocol.endpoints;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Test class for RoleResourceV2Manager.
 */
public class RoleResourceV2ManagerTest {

    @Mock
    private RoleV2Manager roleV2Manager;

    @BeforeMethod
    public void setup() {

        openMocks(this);
    }

    @Test
    public void testCreateRoleWhileWorkflowEnabled() throws ConflictException, NotImplementedException,
            BadRequestException, CharonException {

        String postRequest = "{\n" +
                "  \"schemas\": [\n" +
                "    \"urn:ietf:params:scim:schemas:extension:role:2.0:Role\"\n" +
                "  ],\n" +
                "  \"displayName\": \"Admin\",\n" +
                "  \"users\": [\n" +
                "    { \"value\": \"user-123\", \"display\": \"John Doe\" }\n" +
                "  ],\n" +
                "  \"groups\": [\n" +
                "    { \"value\": \"group-456\", \"display\": \"Developers\" }\n" +
                "  ],\n" +
                "  \"permissions\": [\n" +
                "    { \"value\": \"perm-789\", \"display\": \"Read\" }\n" +
                "  ]\n" +
                "}";
        CharonException charonException = new CharonException();
        charonException.setStatus(ResponseCodeConstants.CODE_ACCEPTED);
        when(roleV2Manager.createRole(any())).thenThrow(charonException);
        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        SCIMResponse scimResponse = roleResourceV2Manager.createRole(postRequest, roleV2Manager);
        assert scimResponse.getResponseStatus() == ResponseCodeConstants.CODE_ACCEPTED;
    }

    @Test
    public void testValidateManagerWithNullCase() throws NoSuchMethodException, IllegalAccessException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method validateManagerMethod = RoleResourceV2Manager.class.getDeclaredMethod("validateManager",
                RoleV2Manager.class);
        validateManagerMethod.setAccessible(true);

        try {
            validateManagerMethod.invoke(roleResourceV2Manager, (RoleV2Manager) null);
            Assert.fail("Expected InternalErrorException was not thrown");
        } catch (InvocationTargetException e) {
            // Verify the cause is InternalErrorException with the expected message
            Assert.assertTrue(e.getCause() instanceof InternalErrorException);
            Assert.assertEquals(((InternalErrorException) e.getCause()).getDetail(),
                    "Provided role manager is null.");
        }
    }

    @Test
    public void testValidateManagerWithValidManager() throws NoSuchMethodException,
            IllegalAccessException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method validateManagerMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "validateManager", RoleV2Manager.class);
        validateManagerMethod.setAccessible(true);
        RoleV2Manager mockRoleManager = Mockito.mock(RoleV2Manager.class);

        try {
            validateManagerMethod.invoke(roleResourceV2Manager, mockRoleManager);
            // If we reach here, no exception was thrown, which is the expected behavior
        } catch (InvocationTargetException e) {
            Assert.fail("Unexpected exception was thrown: " + e.getCause().getMessage());
        }
    }

    @Test
    public void testDecodeRole() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, CharonException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();

        Method decodeRoleMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "decodeRole", String.class, SCIMResourceTypeSchema.class);
        decodeRoleMethod.setAccessible(true);
        SCIMResourceTypeSchema mockSchema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();

        String validRoleJson = "{\n" +
                "  \"schemas\": [\"urn:ietf:params:scim:schemas:extension:2.0:Role\"],\n" +
                "  \"displayName\": \"TestRole\",\n" +
                "  \"users\": [\n" +
                "    {\n" +
                "      \"value\": \"user123\",\n" +
                "      \"display\": \"Test User\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"permissions\": [\n" +
                "    {\n" +
                "      \"value\": \"permission123\",\n" +
                "      \"display\": \"Read Access\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            // Invoke the decodeRole method
            RoleV2 decodedRole = (RoleV2) decodeRoleMethod.invoke(
                    roleResourceV2Manager, validRoleJson, mockSchema);

            // Verify the decoded role attributes
            Assert.assertNotNull(decodedRole);
            Assert.assertEquals(decodedRole.getDisplayName(), "TestRole");

            // Verify users were decoded correctly
            MultiValuedAttribute users = (MultiValuedAttribute) decodedRole.getAttribute(
                    SCIMConstants.RoleSchemaConstants.USERS);
            Assert.assertNotNull(users);
            Assert.assertEquals(users.getAttributeValues().size(), 1);

            // Verify permissions were decoded correctly
            MultiValuedAttribute permissions = (MultiValuedAttribute) decodedRole.getAttribute(
                    SCIMConstants.RoleSchemaConstants.PERMISSIONS);
            Assert.assertNotNull(permissions);
            Assert.assertEquals(permissions.getAttributeValues().size(), 1);

        } catch (InvocationTargetException e) {
            Assert.fail("Unexpected exception: " + e.getCause().getMessage());
        }

        // Test with invalid JSON
        String invalidJson = "{invalid-json";
        try {
            decodeRoleMethod.invoke(roleResourceV2Manager, invalidJson, mockSchema);
            Assert.fail("Expected BadRequestException was not thrown for invalid JSON");
        } catch (InvocationTargetException e) {
            // Verify the exception type is BadRequestException
            Assert.assertTrue(e.getCause() instanceof BadRequestException);
        }
    }

    @Test
    public void testGetOldRole() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, BadRequestException, CharonException, NotImplementedException, NotFoundException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        RoleV2Manager mockRoleManager = Mockito.mock(RoleV2Manager.class);

        Method getOldRoleMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "getOldRole", String.class, RoleV2Manager.class, Map.class);
        getOldRoleMethod.setAccessible(true);

        String existingRoleId = "existing-role-123";
        String nonExistingRoleId = "non-existing-role-456";
        Map<String, Boolean> requestAttributes = Collections.singletonMap("displayName", true);

        RoleV2 mockRole = new RoleV2();
        mockRole.setId(existingRoleId);
        mockRole.setDisplayName("Test Role");

        when(mockRoleManager.getRole(eq(existingRoleId), Mockito.anyMap())).thenReturn(mockRole);
        when(mockRoleManager.getRole(eq(nonExistingRoleId), Mockito.anyMap())).thenReturn(null);

        try {
            RoleV2 retrievedRole = (RoleV2) getOldRoleMethod.invoke(
                    roleResourceV2Manager, existingRoleId, mockRoleManager, requestAttributes);

            // Verify the returned role matches what expected
            Assert.assertNotNull(retrievedRole);
            Assert.assertEquals(existingRoleId, retrievedRole.getId());
            Assert.assertEquals("Test Role", retrievedRole.getDisplayName());

            // Verify the mock was called with expected parameters
            Mockito.verify(mockRoleManager).getRole(existingRoleId, requestAttributes);
        } catch (InvocationTargetException e) {
            Assert.fail("Unexpected exception for existing role ID: " + e.getCause().getMessage());
        }
    }

    @Test
    public void testSetAttributesAndTimestamp() throws NoSuchMethodException, IllegalAccessException,
            BadRequestException, CharonException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method setAttributesAndTimestampMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "setAttributesAndTimestamp", RoleV2.class, RoleV2.class);
        setAttributesAndTimestampMethod.setAccessible(true);

        // Create old role with META and ID attributes properly set
        RoleV2 oldRole = new RoleV2();
        oldRole.setId("test-role-123");
        oldRole.setDisplayName("Old Role");
        oldRole.setCreatedInstant(java.time.Instant.now().minusSeconds(3600)); // 1 hour ago

        // Create new role without META and ID attributes
        RoleV2 newRole = new RoleV2();
        newRole.setDisplayName("New Role");

        // Record the time before the method call to verify timestamp is updated
        java.time.Instant beforeMethodCall = java.time.Instant.now();

        try {
            setAttributesAndTimestampMethod.invoke(roleResourceV2Manager, oldRole, newRole);

            // Verify that ID attribute was copied from old to new role
            Assert.assertEquals("test-role-123", newRole.getId());

            // Verify that META attribute was copied (check if meta attribute exists)
            Assert.assertTrue(newRole.isAttributeExist(SCIMConstants.CommonSchemaConstants.META));

            // Verify that lastModified timestamp was updated to current time
            java.time.Instant afterMethodCall = java.time.Instant.now();
            java.time.Instant lastModified = newRole.getLastModifiedInstant();

            Assert.assertNotNull(lastModified, "Last modified timestamp should not be null");
            Assert.assertTrue(lastModified.isAfter(beforeMethodCall) || lastModified.equals(beforeMethodCall),
                    "Last modified should be after method call start");
            Assert.assertTrue(lastModified.isBefore(afterMethodCall) || lastModified.equals(afterMethodCall),
                    "Last modified should be before method call end");
        } catch (InvocationTargetException e) {
            Assert.fail("Unexpected exception in setAttributesAndTimestamp: " + e.getCause().getMessage());
        }
    }

    @Test
    public void testGroupPatchOperationsByType() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, BadRequestException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();

        // Get the private method using reflection
        Method groupPatchOperationsByTypeMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "groupPatchOperationsByType", java.util.List.class);
        groupPatchOperationsByTypeMethod.setAccessible(true);

        // Create test patch operations for all operation types
        java.util.List<PatchOperation> opList = new java.util.ArrayList<>();

        // Create ADD operation
        PatchOperation addOp = new PatchOperation();
        addOp.setOperation(SCIMConstants.OperationalConstants.ADD);
        addOp.setPath("users");
        addOp.setValues("user123");
        opList.add(addOp);

        // Create another ADD operation
        PatchOperation addOp2 = new PatchOperation();
        addOp2.setOperation(SCIMConstants.OperationalConstants.ADD);
        addOp2.setPath("permissions");
        addOp2.setValues("permission456");
        opList.add(addOp2);

        // Create REMOVE operation
        PatchOperation removeOp = new PatchOperation();
        removeOp.setOperation(SCIMConstants.OperationalConstants.REMOVE);
        removeOp.setPath("users");
        removeOp.setValues("user789");
        opList.add(removeOp);

        // Create REPLACE operation
        PatchOperation replaceOp = new PatchOperation();
        replaceOp.setOperation(SCIMConstants.OperationalConstants.REPLACE);
        replaceOp.setPath("displayName");
        replaceOp.setValues("NewRoleName");
        opList.add(replaceOp);

        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, java.util.List<PatchOperation>> result =
                    (java.util.Map<String, java.util.List<PatchOperation>>)
                            groupPatchOperationsByTypeMethod.invoke(roleResourceV2Manager, opList);

            // Verify the result contains all three operation types
            Assert.assertNotNull(result);
            Assert.assertTrue(result.containsKey(SCIMConstants.OperationalConstants.ADD));
            Assert.assertTrue(result.containsKey(SCIMConstants.OperationalConstants.REMOVE));
            Assert.assertTrue(result.containsKey(SCIMConstants.OperationalConstants.REPLACE));

            // Verify ADD operations (should have 2)
            java.util.List<PatchOperation> addOps =
                    result.get(SCIMConstants.OperationalConstants.ADD);
            Assert.assertEquals(addOps.size(), 2);
            Assert.assertTrue(addOps.contains(addOp));
            Assert.assertTrue(addOps.contains(addOp2));

            // Verify REMOVE operations (should have 1)
            java.util.List<PatchOperation> removeOps =
                    result.get(SCIMConstants.OperationalConstants.REMOVE);
            Assert.assertEquals(removeOps.size(), 1);
            Assert.assertTrue(removeOps.contains(removeOp));

            // Verify REPLACE operations (should have 1)
            java.util.List<PatchOperation> replaceOps =
                    result.get(SCIMConstants.OperationalConstants.REPLACE);
            Assert.assertEquals(replaceOps.size(), 1);
            Assert.assertTrue(replaceOps.contains(replaceOp));

        } catch (InvocationTargetException e) {
            Assert.fail("Unexpected exception in groupPatchOperationsByType: " + e.getCause().getMessage());
        }
    }

    @Test
    public void testGroupPatchOperationsByTypeWithInvalidOperation() throws NoSuchMethodException,
            IllegalAccessException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method groupPatchOperationsByTypeMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "groupPatchOperationsByType", java.util.List.class);
        groupPatchOperationsByTypeMethod.setAccessible(true);

        // Create test patch operation with invalid operation type
        java.util.List<PatchOperation> opList = new java.util.ArrayList<>();

        PatchOperation invalidOp = new PatchOperation();
        invalidOp.setOperation("invalid_operation");
        invalidOp.setPath("test");
        invalidOp.setValues("value");
        opList.add(invalidOp);

        try {
            groupPatchOperationsByTypeMethod.invoke(roleResourceV2Manager, opList);
            Assert.fail("Expected BadRequestException was not thrown for invalid operation type");
        } catch (InvocationTargetException e) {
            // Verify the exception type is BadRequestException
            Assert.assertTrue(e.getCause() instanceof BadRequestException, "Exception is not a BadRequestException: " 
                    + e.getCause().getClass().getName());
        }
    }

    @Test
    public void testBuildSCIMResponse() throws NoSuchMethodException, IllegalAccessException,
            InternalErrorException, CharonException, NotFoundException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method buildSCIMResponseMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "buildSCIMResponse", RoleV2.class, String.class);
        buildSCIMResponseMethod.setAccessible(true);

        RoleV2 mockRole = new RoleV2();
        String roleId = "test-role-123";

        try {
            mockRole.setId(roleId);
            mockRole.setDisplayName("Test Role");
        } catch (BadRequestException e) {
            Assert.fail("Failed to set up mock role: " + e.getMessage());
        }

        String endpointUrl = SCIMConstants.ROLE_V2_ENDPOINT;
        String roleEndpointBaseUrl = "https://example.com/scim/v2/Roles";
        Map<String, String> endpointURLMap = new HashMap<>();
        endpointURLMap.put(SCIMConstants.ROLE_V2_ENDPOINT, roleEndpointBaseUrl);

        try {
            Method setEndpointURLMapMethod = AbstractResourceManager.class.getDeclaredMethod(
                    "setEndpointURLMap", Map.class);
            setEndpointURLMapMethod.invoke(null, endpointURLMap);
        } catch (Exception e) {
            Assert.fail("Failed to set endpoint URL map: " + e.getMessage());
        }

        try {
            SCIMResponse response = (SCIMResponse) buildSCIMResponseMethod.invoke(
                    roleResourceV2Manager, mockRole, endpointUrl);

            Assert.assertEquals(response.getResponseStatus(), ResponseCodeConstants.CODE_CREATED,
                    "Response status should be 201 CREATED");
            // Verify response body is not null (the encoded role)
            Assert.assertNotNull(response.getResponseMessage(),
                    "Response message (encoded role) should not be null");
            // Verify headers contain expected values
            Map<String, String> headers = response.getHeaderParamMap();
            Assert.assertNotNull(headers, "Headers map should not be null");
            // Verify Content-Type header
            Assert.assertEquals(headers.get(SCIMConstants.CONTENT_TYPE_HEADER),
                    SCIMConstants.APPLICATION_JSON,
                    "Content-Type header should be application/json");
            // Verify Location header
            String expectedLocation = "https://example.com/scim/v2/Roles/" + roleId;
            Assert.assertEquals(headers.get(SCIMConstants.LOCATION_HEADER), expectedLocation,
                    "Location header should match expected URL");
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                Assert.fail("Unexpected exception: " + e.getCause().getClass()
                    .getName() + " - " + e.getCause().getMessage());
            } else {
                Assert.fail("Unexpected exception with null cause in InvocationTargetException");
            }
        }
    }

    @Test
    public void testBuildSCIMResponseWithNullRole() throws NoSuchMethodException, IllegalAccessException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method buildSCIMResponseMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "buildSCIMResponse", RoleV2.class, String.class);
        buildSCIMResponseMethod.setAccessible(true);

        Map<String, String> endpointURLMap = new HashMap<>();
        endpointURLMap.put(SCIMConstants.ROLE_V2_ENDPOINT, "https://example.com/scim/v2/Roles");

        try {
            Method setEndpointURLMapMethod = AbstractResourceManager.class.getDeclaredMethod(
                    "setEndpointURLMap", Map.class);
            setEndpointURLMapMethod.invoke(null, endpointURLMap);
        } catch (Exception e) {
            // It's ok if this fails, since testing the null role case
            // which should throw an exception before URL building
        }

        try {
            // Invoke the method with a null role, which should throw an exception
            buildSCIMResponseMethod.invoke(roleResourceV2Manager, null, SCIMConstants.ROLE_V2_ENDPOINT);
            Assert.fail("Expected InternalErrorException was not thrown for null role");
        } catch (InvocationTargetException e) {
            // Verify the exception type is InternalErrorException
            Assert.assertTrue(e.getCause() instanceof InternalErrorException,
                    "Exception is not an InternalErrorException: " +
                    (e.getCause() != null ? e.getCause().getClass().getName() : "null"));

            // Verify the exception message
            InternalErrorException exception = (InternalErrorException) e.getCause();
            String actualMessage = exception.getDetail();
            if (actualMessage == null) {
                actualMessage = exception.getMessage();
            }
            Assert.assertEquals(actualMessage, "Newly created Role resource is null.",
                    "Exception message doesn't match expected message");
        }
    }

    @Test
    public void testUpdateGroupsWithPatchOperations() throws NoSuchMethodException,
            NotFoundException, BadRequestException, CharonException, NotImplementedException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method updateGroupsWithPatchOperationsMethod =
                RoleResourceV2Manager.class.getDeclaredMethod(
                    "updateGroupsWithPatchOperations",
                    String.class,
                    List.class,
                    RoleV2Manager.class,
                    SCIMResourceTypeSchema.class,
                    JSONEncoder.class);
        updateGroupsWithPatchOperationsMethod.setAccessible(true);

        // Create test data
        String roleId = "test-role-123";
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
        List<PatchOperation> patchOperations = new ArrayList<>();

        // Add a group to the role
        PatchOperation addOp = new PatchOperation();
        addOp.setOperation(SCIMConstants.OperationalConstants.ADD);
        addOp.setPath(SCIMConstants.RoleSchemaConstants.GROUPS);

        // Create a JSONArray instead of ArrayList for the values
        JSONArray groupsArray = new JSONArray();
        JSONObject groupValue = new JSONObject();
        groupValue.put(SCIMConstants.CommonSchemaConstants.VALUE, "group123");
        groupValue.put(SCIMConstants.CommonSchemaConstants.DISPLAY, "Test Group");
        groupsArray.put(groupValue);

        // Set the JSONArray as the values
        addOp.setValues(groupsArray);
        patchOperations.add(addOp);

        RoleV2Manager mockRoleManager = Mockito.mock(RoleV2Manager.class);
        RoleV2 mockUpdatedRole = new RoleV2();
        try {
            mockUpdatedRole.setId(roleId);
            mockUpdatedRole.setDisplayName("Test Role");
            when(mockRoleManager.patchGroupsOfRole(
                    eq(roleId), any())).thenReturn(mockUpdatedRole);

            Map<String, String> endpointURLMap = new HashMap<>();
            endpointURLMap.put(SCIMConstants.ROLE_V2_ENDPOINT, "https://example.com/scim/v2/Roles");
            Method setEndpointURLMapMethod = AbstractResourceManager.class.getDeclaredMethod(
                    "setEndpointURLMap", Map.class);
            setEndpointURLMapMethod.invoke(null, endpointURLMap);
            SCIMResponse response = (SCIMResponse) updateGroupsWithPatchOperationsMethod.invoke(
                    roleResourceV2Manager,
                    roleId,
                    patchOperations,
                    mockRoleManager,
                    schema,
                    new JSONEncoder());

            // Verify response
            Assert.assertNotNull(response, "Response should not be null");
            Assert.assertEquals(response.getResponseStatus(), ResponseCodeConstants.CODE_OK,
                    "Response status should be 200 OK");

            // Verify the response contains the updated role data
            Assert.assertNotNull(response.getResponseMessage(),
                    "Response message should not be null");

            // Verify headers
            Map<String, String> headers = response.getHeaderParamMap();
            Assert.assertNotNull(headers, "Headers map should not be null");
            Assert.assertEquals(headers.get(SCIMConstants.CONTENT_TYPE_HEADER),
                    SCIMConstants.APPLICATION_JSON,
                    "Content-Type header should be application/json");

            // Verify the location header contains the role ID
            Assert.assertTrue(headers.get(SCIMConstants.LOCATION_HEADER).contains(roleId),
                    "Location header should contain role ID");

            // Verify the role manager's patchGroupsOfRole method was called correctly
            Mockito.verify(mockRoleManager).patchGroupsOfRole(eq(roleId), any());
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Assert.fail("Unexpected exception: " +
                    (cause != null ? cause.getClass().getName() + " - " + cause.getMessage() : "null"));
        } catch (Exception e) {
            Assert.fail("Test setup failed: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateGroupsWithPatchOperationsWithNotFoundException() throws NoSuchMethodException {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();

        Method updateGroupsWithPatchOperationsMethod =
                RoleResourceV2Manager.class.getDeclaredMethod(
                    "updateGroupsWithPatchOperations",
                    String.class,
                    List.class,
                    RoleV2Manager.class,
                    SCIMResourceTypeSchema.class,
                    JSONEncoder.class);
        updateGroupsWithPatchOperationsMethod.setAccessible(true);
        String roleId = "non-existent-role";
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
        List<PatchOperation> patchOperations = new ArrayList<>();
        PatchOperation addOp = new PatchOperation();
        addOp.setOperation(SCIMConstants.OperationalConstants.ADD);
        addOp.setPath(SCIMConstants.RoleSchemaConstants.GROUPS);

        // Create a JSONArray instead of ArrayList
        JSONArray groupArray = new JSONArray();
        JSONObject groupValue = new JSONObject();
        groupValue.put(SCIMConstants.CommonSchemaConstants.VALUE, "group123");
        groupArray.put(groupValue);
        addOp.setValues(groupArray);

        patchOperations.add(addOp);
        RoleV2Manager mockRoleManager = Mockito.mock(RoleV2Manager.class);

        try {
            when(mockRoleManager.patchGroupsOfRole(eq(roleId), any()))
                .thenThrow(new NotFoundException("Role with ID " + roleId + " not found"));

            // Setup endpoint URL map to avoid NPE
            Map<String, String> endpointURLMap = new HashMap<>();
            endpointURLMap.put(SCIMConstants.ROLE_V2_ENDPOINT, "https://example.com/scim/v2/Roles");
            Method setEndpointURLMapMethod = AbstractResourceManager.class.getDeclaredMethod(
                    "setEndpointURLMap", Map.class);
            setEndpointURLMapMethod.invoke(null, endpointURLMap);

            SCIMResponse response = (SCIMResponse) updateGroupsWithPatchOperationsMethod.invoke(
                    roleResourceV2Manager,
                    roleId,
                    patchOperations,
                    mockRoleManager,
                    schema,
                    new JSONEncoder());

            // Verify it's a not found response
            Assert.assertNotNull(response, "Response should not be null");
            Assert.assertEquals(response.getResponseStatus(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND,
                    "Response status should be 404 Not Found");
            // Verify error message
            Assert.assertTrue(response.getResponseMessage().contains(roleId),
                    "Error message should contain the role ID");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Assert.fail("Unexpected exception: " +
                    (cause != null ? cause.getClass().getName() + " - " + cause.getMessage() : "null"));
        } catch (Exception e) {
            Assert.fail("Test failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersWithPatchOperations() throws Exception {

        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        Method updateUsersWithPatchOperationsMethod = RoleResourceV2Manager.class.getDeclaredMethod(
                "updateUsersWithPatchOperations",
                String.class, List.class, RoleV2Manager.class,
                SCIMResourceTypeSchema.class, JSONEncoder.class);
        updateUsersWithPatchOperationsMethod.setAccessible(true);

        String roleId = "test-role-123";
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getRoleResourceV2Schema();
        List<PatchOperation> patchOperations = new ArrayList<>();
        PatchOperation addOperation = new PatchOperation();
        addOperation.setOperation(SCIMConstants.OperationalConstants.ADD);
        addOperation.setPath(SCIMConstants.RoleSchemaConstants.USERS);

        JSONArray usersArray = new JSONArray();
        JSONObject userObject = new JSONObject();
        userObject.put(SCIMConstants.CommonSchemaConstants.VALUE, "user-123");
        userObject.put(SCIMConstants.CommonSchemaConstants.DISPLAY, "Test User");
        usersArray.put(userObject);
        addOperation.setValues(usersArray);
        patchOperations.add(addOperation);

        RoleV2Manager mockRoleManager = Mockito.mock(RoleV2Manager.class);
        RoleV2 mockUpdatedRole = new RoleV2();
        mockUpdatedRole.setId(roleId);

        Map<String, String> endpointURLMap = new HashMap<>();
        endpointURLMap.put(SCIMConstants.ROLE_V2_ENDPOINT, "https://example.com/scim/v2/Roles");
        Method setEndpointURLMapMethod = AbstractResourceManager.class.getDeclaredMethod(
                "setEndpointURLMap", Map.class);
        setEndpointURLMapMethod.setAccessible(true);
        setEndpointURLMapMethod.invoke(null, endpointURLMap);
        when(mockRoleManager.patchUsersOfRole(eq(roleId), any(Map.class))).thenReturn(mockUpdatedRole);

        SCIMResponse response = (SCIMResponse) updateUsersWithPatchOperationsMethod.invoke(
                roleResourceV2Manager, roleId, patchOperations,
                mockRoleManager, schema, new JSONEncoder());

        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCodeConstants.CODE_OK, response.getResponseStatus());
        verify(mockRoleManager).patchUsersOfRole(eq(roleId), any(Map.class));
    }
}
