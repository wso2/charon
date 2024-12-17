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

package org.wso2.charon3.core.schema;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.config.SCIMCustomSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMSystemSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMUserSchemaExtensionBuilder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;

import java.util.List;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.SYSTEM_USER_SCHEMA_URI;

public class SCIMResourceSchemaManagerTest {

    private SCIMResourceSchemaManager manager;
    private static MockedStatic<SCIMUserSchemaExtensionBuilder> userSchemaBuilderMock;
    private static MockedStatic<SCIMSystemSchemaExtensionBuilder> systemSchemaBuilderMock;
    private static MockedStatic<SCIMCustomSchemaExtensionBuilder> customSchemaBuilderMock;
    private SCIMUserSchemaExtensionBuilder userSchemaExtensionBuilder;
    private SCIMSystemSchemaExtensionBuilder systemSchemaExtensionBuilder;
    private SCIMCustomSchemaExtensionBuilder customSchemaExtensionBuilder;
    private AttributeSchema userSchemaExtension;
    private AttributeSchema systemSchemaExtension;

    @BeforeMethod
    public void setUp() {

        manager = SCIMResourceSchemaManager.getInstance();
        clearInvocations(userSchemaExtensionBuilder, systemSchemaExtensionBuilder);
    }

    @Test
    public void testGetInstance() {

        SCIMResourceSchemaManager instance1 = SCIMResourceSchemaManager.getInstance();
        SCIMResourceSchemaManager instance2 = SCIMResourceSchemaManager.getInstance();

        assertNotNull(instance1, "The getInstance method should return a non-null instance.");
        assertSame(instance1, instance2, "The getInstance method should always return the same instance.");
    }

    @Test
    public void testGetUserResourceSchema() {

        userSchemaBuilderMock.when(SCIMUserSchemaExtensionBuilder::getInstance).thenReturn(userSchemaExtensionBuilder);
        systemSchemaBuilderMock.when(SCIMSystemSchemaExtensionBuilder::getInstance)
                .thenReturn(systemSchemaExtensionBuilder);

        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(systemSchemaExtension);
        when(userSchemaExtension.getURI()).thenReturn(ENTERPRISE_USER_SCHEMA_URI);
        when(systemSchemaExtension.getURI()).thenReturn(SYSTEM_USER_SCHEMA_URI);

        SCIMResourceTypeSchema schema = manager.getUserResourceSchema();

        assertNotNull(schema, "The getUserResourceSchema method should return a non-null schema.");

        List<String> schemaURIs = schema.getSchemasList();
        assertTrue(schemaURIs.contains(SCIMConstants.USER_CORE_SCHEMA_URI), "Core schema URI should be present.");
        assertTrue(schemaURIs.contains(ENTERPRISE_USER_SCHEMA_URI), "User extension URI should be present.");
        assertTrue(schemaURIs.contains(SYSTEM_USER_SCHEMA_URI), "System extension URI should be present.");

        verify(userSchemaExtensionBuilder, times(2)).getExtensionSchema();
        verify(systemSchemaExtensionBuilder).getExtensionSchema();
    }

    @Test
    public void testGetUserResourceSchemaWithUserManager() throws BadRequestException, NotImplementedException,
            CharonException {

        UserManager userManager = mock(UserManager.class);
        AttributeSchema customSchemaExtension = mock(AttributeSchema.class);

        when(userManager.getCustomUserSchemaExtension()).thenReturn(customSchemaExtension);
        when(customSchemaExtension.getURI()).thenReturn("customExtensionURI");

        SCIMResourceTypeSchema schema = manager.getUserResourceSchema(userManager);

        assertNotNull(schema, "The getUserResourceSchema(UserManager) method should return a non-null schema.");

        List<String> schemaURIs = schema.getSchemasList();
        assertTrue(schemaURIs.contains(SCIMConstants.USER_CORE_SCHEMA_URI), "Core schema URI should be present.");
        assertTrue(schemaURIs.contains(ENTERPRISE_USER_SCHEMA_URI), "User extension URI should be present.");
        assertTrue(schemaURIs.contains(SYSTEM_USER_SCHEMA_URI), "System extension URI should be present.");
        assertTrue(schemaURIs.contains("customExtensionURI"), "Custom extension URI should be present.");

        verify(userSchemaExtensionBuilder, times(2)).getExtensionSchema();
        verify(systemSchemaExtensionBuilder).getExtensionSchema();
        verify(userManager).getCustomUserSchemaExtension();
    }

    @Test
    public void testIsExtensionSet() {

        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        Boolean isSet = manager.isExtensionSet();
        assertTrue(isSet, "The isExtensionSet method should return true when the extension is set.");

        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        isSet = manager.isExtensionSet();
        assertFalse(isSet, "The isExtensionSet method should return false when the extension is not set.");

        verify(userSchemaExtensionBuilder, times(2)).getExtensionSchema();
    }

    @Test
    public void testGetExtensionName() {

        userSchemaBuilderMock.when(SCIMUserSchemaExtensionBuilder::getInstance).thenReturn(userSchemaExtensionBuilder);
        when(userSchemaExtension.getName()).thenReturn(ENTERPRISE_USER_SCHEMA_URI);
        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        String extensionName = manager.getExtensionName();
        assertEquals(extensionName, ENTERPRISE_USER_SCHEMA_URI,
                "The getExtensionName method should return the correct name.");

        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        extensionName = manager.getExtensionName();
        assertNull(extensionName, "The getExtensionName method should return null when no extension is set.");

        verify(userSchemaExtensionBuilder, times(2)).getExtensionSchema();
    }

    @Test
    public void testGetSystemSchemaExtensionName() {

        systemSchemaBuilderMock.when(SCIMSystemSchemaExtensionBuilder::getInstance)
                .thenReturn(systemSchemaExtensionBuilder);
        when(systemSchemaExtension.getName()).thenReturn(SYSTEM_USER_SCHEMA_URI);
        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(systemSchemaExtension);
        String extensionName = manager.getSystemSchemaExtensionName();
        assertEquals(extensionName, SYSTEM_USER_SCHEMA_URI,
                "The getSystemSchemaExtensionName method should return the correct name.");

        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        extensionName = manager.getSystemSchemaExtensionName();
        assertNull(extensionName,
                "The getSystemSchemaExtensionName method should return null when no system extension is set.");

        verify(systemSchemaExtensionBuilder, times(2)).getExtensionSchema();
    }

    @Test
    public void testGetCustomSchemaExtensionURI() {

        customSchemaBuilderMock = mockStatic(SCIMCustomSchemaExtensionBuilder.class);
        customSchemaExtensionBuilder = mock(SCIMCustomSchemaExtensionBuilder.class);

        customSchemaBuilderMock.when(SCIMCustomSchemaExtensionBuilder::getInstance)
                .thenReturn(customSchemaExtensionBuilder);
        when(customSchemaExtensionBuilder.getURI()).thenReturn("customSchemaURI");

        String customSchemaURI = manager.getCustomSchemaExtensionURI();

        assertEquals(customSchemaURI, "customSchemaURI",
                "The getCustomSchemaExtensionURI method should return the correct URI.");
        verify(customSchemaExtensionBuilder).getURI();
        customSchemaBuilderMock.close();
    }

    @Test
    public void testGetExtensionURI() {

        when(userSchemaExtension.getURI()).thenReturn(ENTERPRISE_USER_SCHEMA_URI);
        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        String extensionURI = manager.getExtensionURI();
        assertEquals(extensionURI, ENTERPRISE_USER_SCHEMA_URI,
                "The getExtensionURI method should return the correct URI.");

        // When extension schema is not available
        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        extensionURI = manager.getExtensionURI();
        assertNull(extensionURI, "The getExtensionURI method should return null when no extension is set.");

        verify(userSchemaExtensionBuilder, times(2)).getExtensionSchema();
    }

    @Test
    public void testGetSystemSchemaExtensionURI() {

        when(systemSchemaExtension.getURI()).thenReturn(SYSTEM_USER_SCHEMA_URI);
        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(systemSchemaExtension);
        String systemExtensionURI = manager.getSystemSchemaExtensionURI();
        assertEquals(systemExtensionURI, SYSTEM_USER_SCHEMA_URI,
                "The getSystemSchemaExtensionURI method should return the correct URI.");

        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        systemExtensionURI = manager.getSystemSchemaExtensionURI();
        assertNull(systemExtensionURI,
                "The getSystemSchemaExtensionURI method should return null when no system extension is set.");

        verify(systemSchemaExtensionBuilder, times(2)).getExtensionSchema();
    }

    @Test
    public void testGetSystemSchemaExtensionRequired() {

        when(systemSchemaExtension.getRequired()).thenReturn(true);
        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(systemSchemaExtension);
        boolean isRequired = manager.getSystemSchemaExtensionRequired();
        assertTrue(isRequired,
                "The getSystemSchemaExtensionRequired method should return true when the extension is required.");

        when(systemSchemaExtension.getRequired()).thenReturn(false);
        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(systemSchemaExtension);
        isRequired = manager.getSystemSchemaExtensionRequired();
        assertFalse(isRequired,
                "The getSystemSchemaExtensionRequired method should return false when the extension is not required.");

        when(systemSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        isRequired = manager.getSystemSchemaExtensionRequired();
        assertFalse(isRequired,
                "The getSystemSchemaExtensionRequired method should return false when no system extension is set.");

        verify(systemSchemaExtensionBuilder, times(3)).getExtensionSchema();
    }

    @Test
    public void testGetExtensionRequired() {

        when(userSchemaExtension.getRequired()).thenReturn(true);
        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        boolean isRequired = manager.getExtensionRequired();
        assertTrue(isRequired, "The getExtensionRequired method should return true when the extension is required.");

        when(userSchemaExtension.getRequired()).thenReturn(false);
        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(userSchemaExtension);
        isRequired = manager.getExtensionRequired();
        assertFalse(isRequired,
                "The getExtensionRequired method should return false when the extension is not required.");

        when(userSchemaExtensionBuilder.getExtensionSchema()).thenReturn(null);
        isRequired = manager.getExtensionRequired();
        assertFalse(isRequired, "The getExtensionRequired method should return false when no extension is set.");

        verify(userSchemaExtensionBuilder, times(3)).getExtensionSchema();
    }

    @BeforeClass
    public void initMocks() {

        userSchemaBuilderMock = mockStatic(SCIMUserSchemaExtensionBuilder.class);
        systemSchemaBuilderMock = mockStatic(SCIMSystemSchemaExtensionBuilder.class);

        userSchemaExtensionBuilder = mock(SCIMUserSchemaExtensionBuilder.class);
        systemSchemaExtensionBuilder = mock(SCIMSystemSchemaExtensionBuilder.class);
        userSchemaExtension = mock(AttributeSchema.class);
        systemSchemaExtension = mock(AttributeSchema.class);
    }

    @AfterClass
    static void closeMocks() {

        userSchemaBuilderMock.close();
        systemSchemaBuilderMock.close();
    }
}
