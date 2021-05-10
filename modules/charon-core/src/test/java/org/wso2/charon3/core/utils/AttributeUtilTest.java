/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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

package org.wso2.charon3.core.utils;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BINARY;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BOOLEAN;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DATE_TIME;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DECIMAL;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.INTEGER;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.REFERENCE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.READ_WRITE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.DEFAULT;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.NONE;

/**
 * Test class of AttributeUtil.
 */
public class AttributeUtilTest {

    @DataProvider(name = "dataForGetAttributeValueFromString")
    public Object[][] dataToGetAttributeValueFromString() {

        return new Object[][]{

                {"username", STRING, String.class},
                {"", STRING, String.class},
                {true, BOOLEAN, Boolean.class},
                {2, INTEGER, Integer.class},
                {10.0, DECIMAL, Double.class},
                {"2021-04-20T09:06:19.839Z", DATE_TIME, Instant.class},
                {0b010110, BINARY, Byte.class},
                {"referenceString", REFERENCE, String.class},
                {"complexString", COMPLEX, String.class},
                {null, STRING, null},
                {JSONObject.NULL, STRING, String.class}
        };
    }

    @Test(dataProvider = "dataForGetAttributeValueFromString")
    public void testGetAttributeValueFromString(Object attributeValue, SCIMDefinitions.DataType
            dataType, Object expectedAttributeValueTypeFromString) throws CharonException, BadRequestException {

        Object attributeValueFromString = AttributeUtil.getAttributeValueFromString(attributeValue, dataType);

        if (attributeValue != null) {
            Assert.assertEquals(attributeValueFromString.getClass(), expectedAttributeValueTypeFromString);
        } else {
            Assert.assertNull(attributeValueFromString);
        }
    }

    @DataProvider(name = "dataForGetStringValueOfAttribute")
    public Object[][] dataToGetStringValueOfAttribute() throws InstantiationException, IllegalAccessException {

        Instant instant = Instant.parse("2021-04-20T09:06:19.839Z");

        return new Object[][]{

                {"username", STRING, "username"},
                {true, BOOLEAN, "true"},
                {2, INTEGER, "2"},
                {10.0, DECIMAL, "10.0"},
                {instant, DATE_TIME, "2021-04-20T09:06:19.839Z"},
                {0b010110, BINARY, "22"},
                {"referenceString", REFERENCE, "referenceString"},
                {"complexString", COMPLEX, "complexString"}
        };
    }

    @Test(dataProvider = "dataForGetStringValueOfAttribute")
    public void testGetStringValueOfAttribute(Object attributeValue, SCIMDefinitions.DataType
            dataType, Object expectedStringValueOfAttribute) throws CharonException {

        Object stringValueOfAttribute = AttributeUtil.getStringValueOfAttribute(attributeValue, dataType);
        Assert.assertEquals(stringValueOfAttribute, expectedStringValueOfAttribute);
    }

    @DataProvider(name = "dataForParseDateTimeSuccess")
    public Object[][] dataToParseDateTimeSuccess() {

        Instant instant = Instant.parse("2021-04-20T09:06:19.839Z");

        return new Object[][]{

                {"2021-04-20T09:06:19.839Z", instant},
                {"", null}
        };
    }

    @Test(dataProvider = "dataForParseDateTimeSuccess")
    public void testParseDateTimeSuccess(String dateTimeString, Instant expectedLocalDateTime)
            throws CharonException {

        Instant localDateTime = AttributeUtil.parseDateTime(dateTimeString);
        Assert.assertEquals(localDateTime, expectedLocalDateTime);
    }

    @DataProvider(name = "dataForParseDateTimeExceptions")
    public Object[][] dataToParseDateTimeExceptions() {

        return new Object[][]{

                {"2021-04-20T09"}
        };
    }

    @Test(dataProvider = "dataForParseDateTimeExceptions")
    public void testParseDateTimeExceptions(String dateTimeString) {

        Assert.assertThrows(CharonException.class, () -> AttributeUtil.parseDateTime(dateTimeString));
    }

    @DataProvider(name = "dataForParseBooleanSuccess")
    public Object[][] dataToParseBooleanSuccess() {

        return new Object[][]{

                {true, true},
                {false, false},
                {"true", true},
                {"false", false},
                {"abc", false},
                {"", false},
                {null, null}
        };
    }

    @Test(dataProvider = "dataForParseBooleanSuccess")
    public void testParseBooleanSuccess(Object booleanValueObject, Boolean expectedBooleanValue)
            throws BadRequestException {

        Boolean booleanValue = AttributeUtil.parseBoolean(booleanValueObject);
        Assert.assertEquals(booleanValue, expectedBooleanValue);
    }

    @DataProvider(name = "dataForParseBooleanExceptions")
    public Object[][] dataToParseBooleanExceptions() {

        return new Object[][]{

                {10},
                {0b010110}
        };
    }

    @Test(dataProvider = "dataForParseBooleanExceptions")
    public void testParseBooleanExceptions(Object booleanValueObject) {

        Assert.assertThrows(Exception.class, () -> AttributeUtil.parseBoolean(booleanValueObject));

    }

    private SCIMResourceTypeSchema getResourceSchema() {

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        AttributeSchema subSubAttributeSchema =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema);
        AttributeSchema subAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses",
                        "addresses", COMPLEX, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null,
                        subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        subAttributeSchemaList.add(subAttributeSchema3);
        ArrayList<AttributeSchema> subAttributeSchemaList1 = new ArrayList<>();
        subAttributeSchemaList1.add(subAttributeSchema3);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails",
                        "emails", COMPLEX,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList1);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        return scimResourceTypeSchema;
    }

    @DataProvider(name = "dataForQueryParamEncodingSuccess")
    public Object[][] dataToGetAttributeURISuccess() {

        SCIMResourceTypeSchema scimResourceTypeSchema = getResourceSchema();

        return new Object[][]{

                {"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.addresses.city",
                        scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city"},
                {"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city"},
                {"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department"},
                {"urn:ietf:params:scim:schemas:core:2.0:User:emails.home",
                        scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.home"},
                {"emails.home", scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.home"},
                {"emails", scimResourceTypeSchema,
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails"}
        };
    }

    @Test(dataProvider = "dataForQueryParamEncodingSuccess")
    public void testGetAttributeURISuccess(String attributeName, SCIMResourceTypeSchema schema,
                                    String expectedAttributeURI) throws BadRequestException {

        String attributeURI = AttributeUtil.getAttributeURI(attributeName, schema);
        Assert.assertEquals(attributeURI, expectedAttributeURI);
    }

    @DataProvider(name = "dataForQueryParamEncodingExceptions")
    public Object[][] dataToGetAttributeURIExceptions() {

        SCIMResourceTypeSchema scimResourceTypeSchema = getResourceSchema();

        return new Object[][]{

                {"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:accountState", scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForQueryParamEncodingExceptions")
    public void testGetAttributeURIExceptions(String attributeName, SCIMResourceTypeSchema schema) {

        Assert.assertThrows(BadRequestException.class, () -> AttributeUtil.getAttributeURI(attributeName, schema));
    }

}
