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

package org.wso2.charon3.core.encoder;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.attributes.AbstractAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.schema.SCIMDefinitions;

/**
 * Test class for JSONEncoder.
 */
public class JsonEncoderTest {

    private JSONEncoder jsonEncoder;

    @BeforeMethod
    public void setUp() {

        jsonEncoder = new JSONEncoder();
    }

    @Test
    public void testEncodeBasicAttributeSchema() {

        AbstractAttribute attribute = new SimpleAttribute("test", null);
        attribute.setType(SCIMDefinitions.DataType.STRING);
        attribute.setMultiValued(false);
        attribute.setDescription("Test Description");
        attribute.setRequired(true);
        attribute.setCaseExact(true);
        attribute.setReturned(SCIMDefinitions.Returned.DEFAULT);
        attribute.setMutability(SCIMDefinitions.Mutability.READ_WRITE);
        attribute.setURI("testURI");
        attribute.setUniqueness(SCIMDefinitions.Uniqueness.NONE);
        attribute.addAttributeProperty("custom1", "value1");

        JSONObject testJsonObject = new JSONObject();
        testJsonObject.put("key1", "value1");
        attribute.addAttributeJsonProperty("json1", testJsonObject);
        attribute.addAttributeJsonProperty("json2", new JSONObject());
        attribute.removeAttributeJsonProperty("json2");

        JSONObject responseJson = jsonEncoder.encodeBasicAttributeSchema(attribute);

        // Assert.
        Assert.assertEquals(responseJson.get("name"), "test");
        Assert.assertEquals(responseJson.get("type"), SCIMDefinitions.DataType.STRING);
        Assert.assertEquals(responseJson.get("multiValued"), false);
        Assert.assertEquals(responseJson.get("description"), "Test Description");
        Assert.assertEquals(responseJson.get("required"), true);
        Assert.assertEquals(responseJson.get("caseExact"), true);
        Assert.assertEquals(responseJson.get("mutability"), SCIMDefinitions.Mutability.READ_WRITE);
        Assert.assertEquals(responseJson.get("returned"), SCIMDefinitions.Returned.DEFAULT);
        Assert.assertEquals(responseJson.get("uniqueness"), SCIMDefinitions.Uniqueness.NONE);

        Assert.assertEquals(responseJson.get("custom1"), "value1");

        JSONObject customJson = responseJson.getJSONObject("json1");
        Assert.assertEquals(customJson.get("key1"), "value1");

        Assert.assertTrue(attribute.getAttributeJSONProperties().containsKey("json1"));
        Assert.assertFalse(attribute.getAttributeJSONProperties().containsKey("json2"));
        Assert.assertEquals(attribute.getAttributeJsonProperty("json1"), testJsonObject);
    }
}
