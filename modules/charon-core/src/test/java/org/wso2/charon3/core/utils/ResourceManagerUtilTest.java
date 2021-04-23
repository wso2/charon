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
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.NONE;

/**
 * Test class of ResourceManagerUtil.
 */

public class ResourceManagerUtilTest {

    @DataProvider(name = "dataForRequiredAttributesURIs")
    public Object[][] dataToRequiredAttributesURIs() {

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

        Map<String, Boolean> uriList1 = new HashMap<>();

        Map<String, Boolean> uriList2 = new HashMap<>();
        uriList2.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city", false);
        uriList2.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department", false);
        uriList2.put("urn:ietf:params:scim:schemas:core:2.0:User:emails.value", true);

        Map<String, Boolean> uriList3 = new HashMap<>();
        uriList3.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city", false);
        uriList3.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department", false);
        uriList3.put("urn:ietf:params:scim:schemas:core:2.0:User:emails.value", false);

        Map<String, Boolean> uriList4 = new HashMap<>();

        return new Object[][]{
                {scimResourceTypeSchema, "userName,name.familyName", "emails", uriList1},
                {scimResourceTypeSchema, null, null, uriList2},
                {scimResourceTypeSchema, null, "emails", uriList3},
                {scimResourceTypeSchema, "userName,name.familyName", null, uriList4}
        };
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

    @DataProvider(name = "dataForProcessCountInteger")
    public Object[][] dataToProcessCountInteger() {

        return new Object[][]{

                {20, 20},
                {-1, 0},
                {null, null}
        };
    }

    @DataProvider(name = "dataForProcessStartIndexInteger")
    public Object[][] dataToProcessStartIndexInteger() {

        return new Object[][]{

                {0, 1},
                {2, 2},
                {null, 1}
        };
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

    @DataProvider(name = "dataForAllSimpleMultiValuedAttributes")
    public Object[][] dataToAllSimpleMultiValuedAttributes() {

        List<String> schemasList = new ArrayList<>();
        schemasList.add("urn:ietf:params:scim:schemas:core:2.0:User");
        schemasList.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
        AttributeSchema subSubAttributeSchema =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city",
                        "city", STRING, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subSubAttributeSchemaList = new ArrayList<>();
        subSubAttributeSchemaList.add(subSubAttributeSchema);
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
                        READ_WRITE, DEFAULT, NONE, null, null,
                        null);
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
                        "emails", STRING,
                        true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList1);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        List<String> multiValuedAttributes = new ArrayList<>();
        multiValuedAttributes.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:addresses.city");
//        multiValuedAttributes.add("urn:ietf:params:scim:schemas:core:2.0:User:emails.value");
        multiValuedAttributes.add("urn:ietf:params:scim:schemas:core:2.0:User:emails");

        return new Object[][]{

                {scimResourceTypeSchema, multiValuedAttributes}
        };
    }

    @Test(dataProvider = "dataForRequiredAttributesURIs")
    public void testGetOnlyRequiredAttributesURIs(SCIMResourceTypeSchema schema, String requestedAttributes,
                                                  String requestedExcludingAttributes, Map<String, Boolean> expectedURIList) throws CharonException {

        Map<String, Boolean> uriList = ResourceManagerUtil.getOnlyRequiredAttributesURIs(schema, requestedAttributes,
                requestedExcludingAttributes);
        Assert.assertEquals(uriList, expectedURIList);
    }

    @Test(dataProvider = "dataForProcessCountString")
    public void testProcessCount(String countStr, int expectedCount) throws BadRequestException {

        int count = ResourceManagerUtil.processCount(countStr);
        Assert.assertEquals(count, expectedCount);
    }

    @Test(dataProvider = "dataForProcessCountInteger")
    public void testProcessCount(Integer countInt, Integer expectedCountInt) {

        Integer count = ResourceManagerUtil.processCount(countInt);
        Assert.assertEquals(count, expectedCountInt);
    }

    @Test(dataProvider = "dataForProcessStartIndexInteger")
    public void testProcessStartIndex(Integer startIndex, Integer expectedIndex) {

        Integer index = ResourceManagerUtil.processStartIndex(startIndex);
        Assert.assertEquals(index, expectedIndex);
    }

    @Test(dataProvider = "dataForProcessStartIndexString")
    public void testProcessStartIndex(String startIndexStr, int expectedStartIndex) throws BadRequestException {

        int startIndex = ResourceManagerUtil.processStartIndex(startIndexStr);
        Assert.assertEquals(startIndex, expectedStartIndex);
    }

    @Test(dataProvider = "dataForAllSimpleMultiValuedAttributes")
    public void testGetAllSimpleMultiValuedAttributes(SCIMResourceTypeSchema schema,
                                                      List<String> expectedSimpleMultiValuedAttributes){

        List<String> simpleMultiValuedAttributes = ResourceManagerUtil.getAllSimpleMultiValuedAttributes(schema);
        Assert.assertEquals(simpleMultiValuedAttributes, expectedSimpleMultiValuedAttributes);
    }

}