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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.SCIMCustomAttribute;
import org.wso2.charon3.core.schema.AttributeSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
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
        void testGetURI() {

                String result = builder.getURI();
                assertNotNull(result, "The getURI method should not return null.");
                assertEquals(result, AGENT_SCHEMA_URI, "The getURI method should return the correct URI.");
        }

        @Test
        void testBuildAgentSchemaExtension() throws Exception {
                List<SCIMCustomAttribute> scimCustomAttributes = new java.util.ArrayList<>();
                SCIMCustomAttribute scimCustomAttribute = new SCIMCustomAttribute();
                Map<String, String> attributeCharacteristics = new HashMap<>();
                attributeCharacteristics.put(SCIMConfigConstants.SUB_ATTRIBUTES, "");
                attributeCharacteristics.put(SCIMConfigConstants.ATTRIBUTE_NAME, "agentUrl");
                attributeCharacteristics.put(SCIMConfigConstants.DESCRIPTION, "Agent Access URL");
                attributeCharacteristics.put(SCIMConfigConstants.ATTRIBUTE_URI, "urn:scim:wso2:agent:schema:agentUrl");
                scimCustomAttribute.setProperties(attributeCharacteristics);

                SCIMCustomAttribute scimCustomAttribute2 = new SCIMCustomAttribute();
                Map<String, String> attributeCharacteristics2 = new HashMap<>();
                attributeCharacteristics2.put(SCIMConfigConstants.SUB_ATTRIBUTES, "agentUrl");
                attributeCharacteristics2.put(SCIMConfigConstants.ATTRIBUTE_NAME, "urn:scim:wso2:agent:schema");
                attributeCharacteristics2.put(SCIMConfigConstants.DESCRIPTION, "Agent Schema");
                attributeCharacteristics2.put(SCIMConfigConstants.ATTRIBUTE_URI, "urn:scim:wso2:agent:schema");
                scimCustomAttribute2.setProperties(attributeCharacteristics2);

                scimCustomAttributes.add(scimCustomAttribute);
                scimCustomAttributes.add(scimCustomAttribute2);

                AttributeSchema result = builder.buildAgentSchemaExtension(scimCustomAttributes);

                assertNotNull(result, "The buildUserCustomSchemaExtension method should not return null.");
                assertEquals(result.getURI(), AGENT_SCHEMA_URI,
                                "The buildUserCustomSchemaExtension method should return the correct URI.");
        }

        @BeforeMethod
        void setUp() {
                builder = SCIMAgentSchemaExtensionBuilder.getInstance();
        }
}
