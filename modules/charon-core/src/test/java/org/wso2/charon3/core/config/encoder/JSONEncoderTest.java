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

package org.wso2.charon3.core.config.encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.wso2.charon3.core.schema.SCIMConstants.ENTERPRISE_USER_SCHEMA_URI;
import static org.wso2.charon3.core.schema.SCIMConstants.SYSTEM_USER_SCHEMA_URI;

public class JSONEncoderTest {

    private JSONEncoder jsonEncoder;
    private SCIMResourceSchemaManager resourceSchemaManager;
    private static MockedStatic<SCIMResourceSchemaManager> resourceSchemaManagerMock;

    @BeforeClass
    public void setUp() {

        jsonEncoder = new JSONEncoder();
        resourceSchemaManager = mock(SCIMResourceSchemaManager.class);
        resourceSchemaManagerMock = mockStatic(SCIMResourceSchemaManager.class);
        resourceSchemaManagerMock.when(SCIMResourceSchemaManager::getInstance).thenReturn(resourceSchemaManager);
    }

    @AfterClass
    public void tearDown() {

        resourceSchemaManagerMock.close();
    }

    @Test
    public void testBuildUserResourceTypeJsonBody() throws JSONException {

        when(resourceSchemaManager.isExtensionSet()).thenReturn(true);
        when(resourceSchemaManager.getExtensionURI()).thenReturn(ENTERPRISE_USER_SCHEMA_URI);
        when(resourceSchemaManager.getExtensionRequired()).thenReturn(true);
        when(resourceSchemaManager.getSystemSchemaExtensionURI()).thenReturn(SYSTEM_USER_SCHEMA_URI);
        when(resourceSchemaManager.getSystemSchemaExtensionRequired()).thenReturn(true);
        when(resourceSchemaManager.getCustomSchemaExtensionURI()).thenReturn("customSchemaURI");

        String jsonBody = jsonEncoder.buildUserResourceTypeJsonBody();

        JSONObject jsonObject = new JSONObject(jsonBody);

        assertEquals(jsonObject.getString(SCIMConstants.ResourceTypeSchemaConstants.ID), SCIMConstants.USER);
        assertEquals(jsonObject.getString(SCIMConstants.ResourceTypeSchemaConstants.NAME), SCIMConstants.USER);
        assertEquals(jsonObject.getString(SCIMConstants.ResourceTypeSchemaConstants.ENDPOINT),
                SCIMConstants.USER_ENDPOINT);
        assertEquals(jsonObject.getString(SCIMConstants.ResourceTypeSchemaConstants.DESCRIPTION),
                SCIMConstants.ResourceTypeSchemaConstants.USER_ACCOUNT);
        assertEquals(jsonObject.getString(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA),
                SCIMConstants.USER_CORE_SCHEMA_URI);

        JSONArray schemaExtensions = jsonObject.getJSONArray(
                SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS);
        assertEquals(schemaExtensions.length(), 3, "There should be three schema extensions.");

        JSONObject extensionSchema = schemaExtensions.getJSONObject(0);
        assertEquals(extensionSchema.getString(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA),
                ENTERPRISE_USER_SCHEMA_URI);
        assertTrue(extensionSchema.getBoolean(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED));

        JSONObject systemSchema = schemaExtensions.getJSONObject(1);
        assertEquals(systemSchema.getString(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA),
                SYSTEM_USER_SCHEMA_URI);
        assertTrue(systemSchema.getBoolean(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED));

        JSONObject customSchema = schemaExtensions.getJSONObject(2);
        assertEquals(customSchema.getString(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_SCHEMA),
                "customSchemaURI");
        assertFalse(customSchema.getBoolean(SCIMConstants.ResourceTypeSchemaConstants.SCHEMA_EXTENSIONS_REQUIRED));

        verify(resourceSchemaManager).isExtensionSet();
        verify(resourceSchemaManager).getExtensionURI();
        verify(resourceSchemaManager).getExtensionRequired();
        verify(resourceSchemaManager).getSystemSchemaExtensionURI();
        verify(resourceSchemaManager).getSystemSchemaExtensionRequired();
        verify(resourceSchemaManager, times(2)).getCustomSchemaExtensionURI();
    }
}
