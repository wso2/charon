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

package org.wso2.charon3.core.objects;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

/**
 * Unit tests for the Agent class.
 */
public class AgentTest {

    private Agent agent;
    private MockedStatic<SCIMResourceSchemaManager> scimResourceSchemaManagerMockedStatic;
    private SCIMResourceSchemaManager scimResourceSchemaManager;
    private SCIMResourceTypeSchema userResourceSchema;
    private UserManager userManager;

    @BeforeMethod
    public void setUp() throws Exception {
        agent = new Agent();

        // Mock the SCIMResourceSchemaManager
        scimResourceSchemaManagerMockedStatic = Mockito.mockStatic(SCIMResourceSchemaManager.class);
        scimResourceSchemaManager = mock(SCIMResourceSchemaManager.class);
        userResourceSchema = mock(SCIMResourceTypeSchema.class);
        userManager = mock(UserManager.class);

        scimResourceSchemaManagerMockedStatic.when(SCIMResourceSchemaManager::getInstance)
                .thenReturn(scimResourceSchemaManager);
        // mock getSchemasList
        when(userResourceSchema.getSchemasList()).thenReturn(new ArrayList<String>() {
            {
                add("USER_SCHEMA_URI");
            }
        });
        when(scimResourceSchemaManager.getUserResourceSchema()).thenReturn(userResourceSchema);
        when(scimResourceSchemaManager.getUserResourceSchema(userManager)).thenReturn(userResourceSchema);


    }

    @AfterMethod
    public void tearDown() {
        if (scimResourceSchemaManagerMockedStatic != null) {
            scimResourceSchemaManagerMockedStatic.close();
        }
    }

    /**
     * Test that Agent extends User class correctly.
     */
    @Test
    public void testAgentExtendsUser() {
        Assert.assertTrue(agent instanceof User, "Agent should extend User class");
        Assert.assertTrue(agent instanceof AbstractSCIMObject, "Agent should be a SCIM object");
    }

    /**
     * Test Agent default constructor.
     */
    @Test
    public void testAgentDefaultConstructor() {
        Agent newAgent = new Agent();
        Assert.assertNotNull(newAgent, "Agent should be instantiated successfully");
    }

    /**
     * Test setSchemas() method without UserManager.
     */
    @Test
    public void testSetSchemasWithoutUserManager() {
        // Create a spy to verify method calls
        Agent spyAgent = Mockito.spy(agent);

        // Call the method
        spyAgent.setSchemas();

        // Verify that the agent schema is set
        Assert.assertTrue(spyAgent.getSchemaList().contains(SCIMConstants.AGENT_SCHEMA_URI),
                "Agent schema URI should be set");
    }

    /**
     * Test setSchemas(UserManager) method successfully.
     */
    @Test
    public void testSetSchemasWithUserManagerSuccess()
            throws BadRequestException, NotImplementedException, CharonException {
        // Call the method
        agent.setSchemas(userManager);

        // Verify that the agent schema is set
        Assert.assertTrue(agent.getSchemaList().contains(SCIMConstants.AGENT_SCHEMA_URI),
                "Agent schema URI should be set");
    }

    /**
     * Test that Agent maintains User functionality.
     */
    @Test
    public void testAgentMaintainsUserFunctionality() throws CharonException {
        // Test that Agent can use User methods
        String testUsername = "test-agent";
        agent.replaceUsername(testUsername);

        Assert.assertEquals(agent.getUsername(), testUsername,
                "Agent should maintain User functionality like setting username");
    }

    /**
     * Test that Agent properly inherits from User and calls super methods.
     */
    @Test
    public void testAgentInheritanceAndSuperCalls()
            throws BadRequestException, NotImplementedException, CharonException {
        Agent spyAgent = Mockito.spy(agent);

        // Test setSchemas() without UserManager
        spyAgent.setSchemas();

        // Verify that setSchema is called for agent schema
        // Note: This test verifies the method completes without error
        Assert.assertTrue(spyAgent.getSchemaList().contains(SCIMConstants.AGENT_SCHEMA_URI),
                "Agent schema should be added after calling setSchemas()");
    }

    /**
     * Test Agent object identity and equality.
     */
    @Test
    public void testAgentObjectIdentity() {
        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        // Different instances should not be equal by reference
        Assert.assertNotSame(agent1, agent2, "Different Agent instances should not be the same");

        // But they should be valid Agent objects
        Assert.assertTrue(agent1 instanceof Agent, "agent1 should be instance of Agent");
        Assert.assertTrue(agent2 instanceof Agent, "agent2 should be instance of Agent");
    }
}
