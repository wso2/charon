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

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.objects.RoleV2;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test class for RoleResourceV2Manager.
 */
public class RoleResourceV2ManagerTest {

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
}
