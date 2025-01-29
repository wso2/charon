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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.READ_WRITE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.DEFAULT;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.NEVER;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.REQUEST;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.NONE;

/**
 * Test class of ResourceManagerUtil.
 */
public class ResourceManagerUtilTest {

    private SCIMResourceTypeSchema getResourceSchema() {

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
        AttributeSchema subSubAttributeSchema =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subSubAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.subattribute1",
                        "subattribute1", STRING, false, "", false, false,
                        READ_WRITE, NEVER, NONE, null, null, null);
        AttributeSchema subSubAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.subattribute2",
                        "subattribute2", STRING, false, "", false, false,
                        READ_WRITE, REQUEST, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema);
        subSubAttributeSchemaList.add(subSubAttributeSchema1);
        subSubAttributeSchemaList.add(subSubAttributeSchema2);
        AttributeSchema subAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses",
                        "addresses", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null,
                        subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:attribute3",
                        "attribute3", STRING, true, "", false, false,
                        READ_WRITE, NEVER, NONE, null, null, null);
        AttributeSchema subAttributeSchema4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:attribute4",
                        "attribute4", STRING, true, "", false, false,
                        READ_WRITE, REQUEST, NONE, null, null, null);
        AttributeSchema subAttributeSchema5 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema6 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.subattribute",
                        "subattribute", STRING, false, "", false, false,
                        READ_WRITE, REQUEST, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        subAttributeSchemaList.add(subAttributeSchema3);
        subAttributeSchemaList.add(subAttributeSchema4);
        ArrayList<AttributeSchema> subAttributeSchemaList1 = new ArrayList<>();
        subAttributeSchemaList1.add(subAttributeSchema5);
        subAttributeSchemaList1.add(subAttributeSchema6);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "user", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails",
                        "emails", COMPLEX,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList1);
        AttributeSchema attributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute1",
                        "attribute1", STRING,
                        true, "", false, false,
                        READ_WRITE, NEVER, NONE, null, null, null);
        AttributeSchema attributeSchema4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute2",
                        "attribute2", STRING,
                        true, "", false, false,
                        READ_WRITE, REQUEST, NONE, null, null, null);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2,
                        attributeSchema3, attributeSchema4);

        return scimResourceTypeSchema;

    }

    @DataProvider(name = "dataForRequiredAttributesURIs")
    public Object[][] dataToRequiredAttributesURIs() {

        SCIMResourceTypeSchema scimResourceTypeSchema = getResourceSchema();

        Map<String, Boolean> uriList1 = new HashMap<>();

        Map<String, Boolean> uriList2 = new HashMap<>();
        uriList2.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city", true);
        uriList2.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department", false);
        uriList2.put("urn:ietf:params:scim:schemas:core:2.0:User:emails.value", true);

        Map<String, Boolean> uriList3 = new HashMap<>();
        uriList3.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city", true);
        uriList3.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department", false);

        Map<String, Boolean> uriList4 = new HashMap<>();
        uriList4.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city", true);

        Map<String, Boolean> uriList5 = new HashMap<>();
        uriList5.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department", false);
        uriList5.put("urn:ietf:params:scim:schemas:core:2.0:User:emails.value", true);

        Map<String, Boolean> uriList6 = new HashMap<>();
        uriList6.put("urn:ietf:params:scim:schemas:core:2.0:User:emails.value", true);

        Map<String, Boolean> uriList7 = new HashMap<>();
        uriList7.put("urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute2", true);

        return new Object[][]{
                {scimResourceTypeSchema, "emails.home", "emails.value", uriList1},
                {scimResourceTypeSchema, null, null, uriList2},
                {scimResourceTypeSchema, null, "emails", uriList3},
                {scimResourceTypeSchema, "user.addresses.city", null, uriList4},
                {scimResourceTypeSchema, null, "user.addresses.city", uriList5},
                {scimResourceTypeSchema, null, "emails.value", uriList3},
                {scimResourceTypeSchema, "emails.value", null, uriList6},
                {scimResourceTypeSchema, "city", null, uriList1},
                {scimResourceTypeSchema, "attribute2", null, uriList7}
        };
    }

    @Test(dataProvider = "dataForRequiredAttributesURIs")
    public void testGetOnlyRequiredAttributesURIs(SCIMResourceTypeSchema schema, String requestedAttributes,
             String requestedExcludingAttributes, Map<String, Boolean> expectedURIList) throws CharonException {

        Map<String, Boolean> uriList = ResourceManagerUtil.getOnlyRequiredAttributesURIs(schema, requestedAttributes,
                requestedExcludingAttributes);
        Assert.assertEquals(uriList, expectedURIList);
    }

    @DataProvider(name = "dataForGetAllAttributeURIs")
    public Object[][] dataToGetAllAttributeURIs() {

        SCIMResourceTypeSchema scimResourceTypeSchema = getResourceSchema();

        return new Object[][]{
                {scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForGetAllAttributeURIs")
    public void testGetAllAttributeURIs(SCIMResourceTypeSchema schema) throws CharonException {

        Map<String, Boolean> uriList = ResourceManagerUtil.getAllAttributeURIs(schema);
        Assert.assertTrue(true, "getAllAttributeURIs is successful");
    }

    @DataProvider(name = "dataForProcessCountString")
    public Object[][] dataToProcessCountString() {

        return new Object[][]{

                {"20", 20},
                {"-1", 0},
                {"20k", 0},
                {"abc", 0},
                {"", 0},
                {null, 0}
        };
    }

    @Test(dataProvider = "dataForProcessCountString")
    public void testProcessCount(String countStr, int expectedCount) throws BadRequestException {

        int count = ResourceManagerUtil.processCount(countStr);
        Assert.assertEquals(count, expectedCount);
    }

    @DataProvider(name = "dataForProcessCountInteger")
    public Object[][] dataToProcessCountInteger() {

        return new Object[][]{

                {20, 20},
                {-1, 0},
                {null, 0}
        };
    }

    @Test(dataProvider = "dataForProcessCountInteger")
    public void testProcessCount(Integer countInt, Integer expectedCountInt) {

        Integer count = ResourceManagerUtil.processCount(countInt);
        Assert.assertEquals(count, expectedCountInt);
    }

    @DataProvider(name = "dataForProcessStartIndexInteger")
    public Object[][] dataToProcessStartIndexInteger() {

        return new Object[][]{

                {0, 1},
                {2, 2},
                {null, 1}
        };
    }

    @Test(dataProvider = "dataForProcessStartIndexInteger")
    public void testProcessStartIndex(Integer startIndex, Integer expectedIndex) {

        Integer index = ResourceManagerUtil.processStartIndex(startIndex);
        Assert.assertEquals(index, expectedIndex);
    }

    @DataProvider(name = "dataForProcessStartIndexString")
    public Object[][] dataToProcessStartIndexString() {

        return new Object[][]{

                {"20", 20},
                {"0", 1},
                {"20k", 1},
                {"abc", 1},
                {"", 1},
                {null, 1}
        };
    }

    @Test(dataProvider = "dataForProcessStartIndexString")
    public void testProcessStartIndex(String startIndexStr, int expectedStartIndex) throws BadRequestException {

        int startIndex = ResourceManagerUtil.processStartIndex(startIndexStr);
        Assert.assertEquals(startIndex, expectedStartIndex);
    }

    @DataProvider(name = "dataForAllSimpleMultiValuedAttributes")
    public Object[][] dataToAllSimpleMultiValuedAttributes() {

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
        AttributeSchema subSubAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subSubAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.attribute3",
                        "attribute3", STRING, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema1);
        subSubAttributeSchemaList.add(subSubAttributeSchema2);
        AttributeSchema subAttributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses",
                        "addresses", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:attribute2",
                        "attribute2", STRING, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        subAttributeSchemaList.add(subAttributeSchema3);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute1",
                        "attribute1", STRING,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        List<String> multiValuedAttributes = new ArrayList<>();
        multiValuedAttributes.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.attribute3");
        multiValuedAttributes.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:attribute2");
        multiValuedAttributes.add("urn:ietf:params:scim:schemas:extension:2.0:CustomResource:attribute1");

        return new Object[][]{

                {scimResourceTypeSchema, multiValuedAttributes}
        };
    }

    @Test(dataProvider = "dataForAllSimpleMultiValuedAttributes")
    public void testGetAllSimpleMultiValuedAttributes(SCIMResourceTypeSchema schema,
                                                      List<String> expectedSimpleMultiValuedAttributes) {

        List<String> simpleMultiValuedAttributes = ResourceManagerUtil.getAllSimpleMultiValuedAttributes(schema);
        Assert.assertEquals(simpleMultiValuedAttributes, expectedSimpleMultiValuedAttributes);
    }

}
