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

package org.wso2.charon3.core.config;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.wso2.charon3.core.schema.SCIMConstants.AGENT_SCHEMA_URI;

/**
 * Unit test for SCIMAgentSchemaExtensionBuilder.
 */
public class SCIMAgentSchemaExtensionBuilderTest {

    private SCIMAgentSchemaExtensionBuilder builder;

    @Test
    void testGetInstance() {

        SCIMAgentSchemaExtensionBuilder instance1 = SCIMAgentSchemaExtensionBuilder.getInstance();
        SCIMAgentSchemaExtensionBuilder instance2 = SCIMAgentSchemaExtensionBuilder.getInstance();
        assertNotNull(instance1, "The getInstance method should return a non-null instance.");
        assertSame(instance1, instance2, "The getInstance method should return the same instance every time.");
    }

    @Test
    void testGetExtensionSchema() throws NoSuchFieldException, IllegalAccessException {

        AttributeSchema mockSchema = Mockito.mock(AttributeSchema.class);
        Field extensionSchemaField = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField("extensionSchema");
        extensionSchemaField.setAccessible(true);
        extensionSchemaField.set(builder, mockSchema);

        AttributeSchema result = builder.getExtensionSchema();
        assertNotNull(result, "The getExtensionSchema method should not return null.");
        assertSame(mockSchema, result,
                "The getExtensionSchema method should return the correct schema instance.");
    }

    @Test
    void testGetURI() {

        String result = builder.getURI();
        assertNotNull(result, "The getURI method should not return null.");
        assertEquals(result, AGENT_SCHEMA_URI, "The getURI method should return the correct URI.");
    }

    @Test
    void testIsRootConfig() throws NoSuchFieldException, IllegalAccessException {

        setInstanceField("extensionRootAttributeName", "testRootAttribute");

        ExtensionBuilder.ExtensionAttributeSchemaConfig mockConfig = 
                Mockito.mock(ExtensionBuilder.ExtensionAttributeSchemaConfig.class);
        Mockito.when(mockConfig.getName()).thenReturn("testRootAttribute");

        boolean result = builder.isRootConfig(mockConfig);
        assertTrue(result, "The isRootConfig method should return true for the root config.");

        Mockito.when(mockConfig.getName()).thenReturn("notRootAttribute");
        result = builder.isRootConfig(mockConfig);
        assertFalse(result, "The isRootConfig method should return false for non-root config.");
    }

    @Test
    void testReadConfiguration() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                "scim2-agent-schema-extension-test.config");
        assertNotNull(inputStream, "The test configuration file should exist.");

        builder.readConfiguration(inputStream);

        Field configField = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField("extensionConfig");
        configField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ExtensionBuilder.ExtensionAttributeSchemaConfig> extensionConfig =
                (Map<String, ExtensionBuilder.ExtensionAttributeSchemaConfig>) configField.get(builder);

        assertNotNull(extensionConfig, "The extensionConfig map should not be null.");
        assertEquals(extensionConfig.size(), 5, "The extensionConfig map should contain 5 entries.");
        assertTrue(extensionConfig.containsKey("urn:scim:wso2:agent:schema:agentUrl"),
                "The extensionConfig map should contain agentUrl.");
        assertTrue(extensionConfig.containsKey("urn:scim:wso2:agent:schema:agentDescription"),
                "The extensionConfig map should contain agentDescription.");
        assertTrue(extensionConfig.containsKey("urn:scim:wso2:agent:schema"),
                "The extensionConfig map should contain the root agent schema.");
    }

    @Test
    void testBuildAgentSchemaExtension() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                "scim2-agent-schema-extension-test.config");
        assertNotNull(inputStream, "The test configuration file should exist.");

        builder.buildAgentSchemaExtension(inputStream);

        Field schemaField = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField("extensionSchema");
        schemaField.setAccessible(true);
        AttributeSchema extensionSchema = (AttributeSchema) schemaField.get(builder);

        assertNotNull(extensionSchema, "The extensionSchema should not be null after building.");
        assertEquals(extensionSchema.getURI(), "urn:scim:wso2:agent:schema",
                "The root URI of the extension schema should match.");
    }

    @Test
    void testBuildAgentSchemaExtensionWithConfigFilePath() throws Exception {

        builder.buildAgentSchemaExtension("src/test/resources/scim2-agent-schema-extension-test.config");

        Field schemaField = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField("extensionSchema");
        schemaField.setAccessible(true);
        AttributeSchema extensionSchema = (AttributeSchema) schemaField.get(builder);

        assertNotNull(extensionSchema, "The extensionSchema should not be null after building from file.");
        assertEquals(extensionSchema.getURI(), "urn:scim:wso2:agent:schema",
                "The root URI of the extension schema should match.");
    }

    @BeforeMethod
    void setUp() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
            InstantiationException {

        builder = SCIMAgentSchemaExtensionBuilder.getInstance();

        Constructor<SCIMAgentSchemaExtensionBuilder> constructor =
                SCIMAgentSchemaExtensionBuilder.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        SCIMAgentSchemaExtensionBuilder newBuilderInstance = constructor.newInstance();

        resetSingletonField("EXTENSION_ROOT_ATTRIBUTE_URI", AGENT_SCHEMA_URI);
        resetSingletonField("instance", newBuilderInstance);
        resetSingletonField("extensionConfig", new HashMap<>());
        resetSingletonField("attributeSchemas", new HashMap<>());

        setInstanceField("extensionSchema", null);
        setInstanceField("extensionRootAttributeName", null);
    }

    private void resetSingletonField(String fieldName, Object newValue) throws NoSuchFieldException,
            IllegalAccessException {

        Field field = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        field.set(null, newValue);
    }

    private void setInstanceField(String fieldName, Object value) throws NoSuchFieldException,
            IllegalAccessException {

        Field field = SCIMAgentSchemaExtensionBuilder.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(builder, value);
    }
}
