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

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
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
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
}
