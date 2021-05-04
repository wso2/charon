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
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Mutability.READ_WRITE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Returned.DEFAULT;
import static org.wso2.charon3.core.schema.SCIMDefinitions.Uniqueness.NONE;

public class PatchOperationUtilTest {

    @DataProvider(name = "dataForPatchRemove")
    public Object[][] dataToPatchRemove() throws InstantiationException, IllegalAccessException, CharonException {

        PatchOperation patchOperation = PatchOperation.class.newInstance();
        PatchOperation patchOperation1 = PatchOperation.class.newInstance();
        PatchOperation patchOperation2 = PatchOperation.class.newInstance();
        PatchOperation patchOperation3 = PatchOperation.class.newInstance();
        PatchOperation patchOperation4 = PatchOperation.class.newInstance();

        patchOperation.setOperation("remove");

        patchOperation1.setOperation("remove");
        patchOperation1.setPath("permissions");

        patchOperation2.setOperation("remove");
        patchOperation2.setPath("nickName");

        patchOperation3.setOperation("remove");
        patchOperation3.setPath("emails[type eq home]");

        patchOperation4.setOperation("remove");
        patchOperation4.setPath("emails[type sw home]");

        User oldResource = User.class.newInstance();
        oldResource.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        oldResource.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttribute = new SimpleAttribute("nickName", "shaggy");
        simpleAttribute.setMutability(SCIMDefinitions.Mutability.READ_WRITE);
        simpleAttribute.setRequired(false);
        oldResource.setAttribute(simpleAttribute);

        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "229d3f0d-a07b-4052-bf4d-3071ecafed04");
        simpleAttribute1.setMutability(SCIMDefinitions.Mutability.READ_ONLY);
        simpleAttribute1.setRequired(false);
        oldResource.setAttribute(simpleAttribute1);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        oldResource.setAttribute(complexAttributeMeta);

        SimpleAttribute simpleAttributeEmailType = new SimpleAttribute("type", "home");
        SimpleAttribute simpleAttributeEmailValue = new SimpleAttribute("value", "rash@gmail.com");

        Map<String, Attribute> valuesMap = new HashMap<>();
        valuesMap.put("type", simpleAttributeEmailType);
        valuesMap.put("value", simpleAttributeEmailValue);

        ComplexAttribute complexAttributeEmail = new ComplexAttribute("emails_rash@gmail.com_home");
        complexAttributeEmail.setType(COMPLEX);
        complexAttributeEmail.setMutability(READ_WRITE);
        complexAttributeEmail.setRequired(false);
        complexAttributeEmail.setSubAttributesList(valuesMap);

        MultiValuedAttribute multiValuedAttributeEmail = new MultiValuedAttribute("emails");
        multiValuedAttributeEmail.setType(SCIMDefinitions.DataType.COMPLEX);
        multiValuedAttributeEmail.setMultiValued(true);
        multiValuedAttributeEmail.setMutability(READ_WRITE);
        multiValuedAttributeEmail.setAttributeValue(complexAttributeEmail);
        oldResource.setAttribute(multiValuedAttributeEmail);

        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);

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
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
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

        return new Object[][]{

                {patchOperation, oldResource, copyOfOldResource, scimResourceTypeSchema, "BAD_REQUEST"},
                {patchOperation1, oldResource, copyOfOldResource, scimResourceTypeSchema, "NOT_IMPLEMENTED"},
                {patchOperation2, oldResource, copyOfOldResource, scimResourceTypeSchema, "SUCCESS"},
                {patchOperation3, oldResource, copyOfOldResource, scimResourceTypeSchema, "SUCCESS"},
                {patchOperation4, oldResource, copyOfOldResource, scimResourceTypeSchema, "NOT_IMPLEMENTED"}
        };
    }

    @Test(dataProvider = "dataForPatchRemove")
    public void testDoPatchRemove(PatchOperation operation, AbstractSCIMObject oldResource,
                                  AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema, String expect)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        String result = "";

        try {
            PatchOperationUtil.doPatchRemove(operation, oldResource, copyOfOldResource, schema);
            result = "SUCCESS";
        } catch (BadRequestException e) {
            result = "BAD_REQUEST";
        } catch (NotImplementedException e) {
            result = "NOT_IMPLEMENTED";
        }

        Assert.assertEquals(expect, result);
    }

    @DataProvider(name = "dataForPatchAdd")
    public Object[][] dataToPatchAdd() throws InstantiationException, IllegalAccessException, CharonException {

        PatchOperation patchOperation = PatchOperation.class.newInstance();
        PatchOperation patchOperation1 = PatchOperation.class.newInstance();
        PatchOperation patchOperation2 = PatchOperation.class.newInstance();

        patchOperation.setOperation("add");

        patchOperation1.setOperation("add");
        patchOperation1.setPath("permissions");
        patchOperation1.setValues(READ_WRITE);

        patchOperation2.setOperation("add");
        patchOperation2.setPath("displayName");
        patchOperation2.setValues("Rash");

        JSONDecoder jsonDecoder = new JSONDecoder();

        User oldResource = User.class.newInstance();
        oldResource.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        oldResource.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "229d3f0d-a07b-4052-bf4d-3071ecafed04");
        simpleAttribute1.setMutability(SCIMDefinitions.Mutability.READ_ONLY);
        simpleAttribute1.setRequired(false);
        oldResource.setAttribute(simpleAttribute1);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        oldResource.setAttribute(complexAttributeMeta);

        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);

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
                        "addresses", COMPLEX, true, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null,
                        subSubAttributeSchemaList);
        AttributeSchema subAttributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:department",
                        "department", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        AttributeSchema attributeSchema1 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", COMPLEX,
                        false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList);
        AttributeSchema attributeSchema2 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:manager.displayName",
                        "displayName", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2);

        return new Object[][]{

                {patchOperation, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "BAD_REQUEST"},
                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "NOT_IMPLEMENTED"},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "SUCCESS"}
        };
    }

    @Test(dataProvider = "dataForPatchAdd")
    public void testDoPatchAdd(PatchOperation operation, JSONDecoder decoder, AbstractSCIMObject oldResource,
                               AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema, String expect)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        String result = "";

        try {
            PatchOperationUtil.doPatchAdd(operation, decoder, oldResource, copyOfOldResource, schema);
            result = "SUCCESS";
        } catch (BadRequestException e) {
            result = "BAD_REQUEST";
        } catch (NotImplementedException e) {
            result = "NOT_IMPLEMENTED";
        }

        Assert.assertEquals(expect, result);
    }

    @DataProvider(name = "dataForPatchReplace")
    public Object[][] dataToPatchReplace() throws InstantiationException, IllegalAccessException, CharonException {

        Map<String, String> operationValueName = new HashMap<>();
        operationValueName.put("givenName", "John");
        operationValueName.put("familyName", "Anderson");

        PatchOperation patchOperation = PatchOperation.class.newInstance();
        PatchOperation patchOperation1 = PatchOperation.class.newInstance();
        PatchOperation patchOperation2 = PatchOperation.class.newInstance();
        PatchOperation patchOperation3 = PatchOperation.class.newInstance();

        patchOperation.setOperation("replace");
        patchOperation.setValues("Rash");

        patchOperation1.setOperation("replace");
        patchOperation1.setPath("name");
        patchOperation1.setValues(operationValueName);

        patchOperation2.setOperation("replace");
        patchOperation2.setPath("manager.displayName");
        patchOperation2.setValues("Rash");

        patchOperation3.setOperation("replace");
        patchOperation3.setPath("emails[type eq home].value");
        patchOperation3.setValues("rash123@gmail.com");

        JSONDecoder jsonDecoder = new JSONDecoder();

        User oldResource = User.class.newInstance();
        oldResource.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        oldResource.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "229d3f0d-a07b-4052-bf4d-3071ecafed04");
        simpleAttribute1.setMutability(SCIMDefinitions.Mutability.READ_ONLY);
        simpleAttribute1.setRequired(false);
        oldResource.setAttribute(simpleAttribute1);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        oldResource.setAttribute(complexAttributeMeta);

        SimpleAttribute simpleAttributeEmailType = new SimpleAttribute("type", "home");
        SimpleAttribute simpleAttributeEmailValue = new SimpleAttribute("value", "rash@gmail.com");
        simpleAttributeEmailValue.setMutability(READ_WRITE);
        simpleAttributeEmailValue.setMultiValued(false);

        Map<String, Attribute> valuesMap = new HashMap<>();
        valuesMap.put("type", simpleAttributeEmailType);
        valuesMap.put("value", simpleAttributeEmailValue);

        ComplexAttribute complexAttributeEmail = new ComplexAttribute("emails_rash@gmail.com_home");
        complexAttributeEmail.setType(COMPLEX);
        complexAttributeEmail.setMutability(READ_WRITE);
        complexAttributeEmail.setRequired(false);
        complexAttributeEmail.setSubAttributesList(valuesMap);

        MultiValuedAttribute multiValuedAttributeEmail = new MultiValuedAttribute("emails");
        multiValuedAttributeEmail.setType(SCIMDefinitions.DataType.COMPLEX);
        multiValuedAttributeEmail.setMultiValued(true);
        multiValuedAttributeEmail.setMutability(READ_WRITE);
        multiValuedAttributeEmail.setAttributeValue(complexAttributeEmail);
        oldResource.setAttribute(multiValuedAttributeEmail);

        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);

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
                        "urn:ietf:params:scim:schemas:core:2.0:User:emails.value",
                        "value", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema subAttributeSchema4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:manager.displayName",
                        "displayName", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        ArrayList<AttributeSchema> subAttributeSchemaList = new ArrayList<>();
        subAttributeSchemaList.add(subAttributeSchema1);
        subAttributeSchemaList.add(subAttributeSchema2);
        ArrayList<AttributeSchema> subAttributeSchemaList1 = new ArrayList<>();
        subAttributeSchemaList1.add(subAttributeSchema3);
        ArrayList<AttributeSchema> subAttributeSchemaList2 = new ArrayList<>();
        subAttributeSchemaList2.add(subAttributeSchema4);
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
        AttributeSchema attributeSchema3 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:manager",
                        "manager", COMPLEX, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList2);
        SCIMResourceTypeSchema scimResourceTypeSchema = SCIMResourceTypeSchema.createSCIMResourceSchema
                (schemasList, attributeSchema1, attributeSchema2, attributeSchema3);

        return new Object[][]{

                {patchOperation, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "CHARON_EXCEPTION"},
                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "BAD_REQUEST"},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "SUCCESS"},
                {patchOperation3, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema,
                        "SUCCESS"}
        };
    }

    @Test(dataProvider = "dataForPatchReplace")
    public void testDoPatchReplace(PatchOperation operation, JSONDecoder decoder, AbstractSCIMObject oldResource,
                                   AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema, String expect)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        String result = "";

        try {
            PatchOperationUtil.doPatchReplace(operation, decoder, oldResource, copyOfOldResource, schema);
            result = "SUCCESS";
        } catch (CharonException e) {
            result = "CHARON_EXCEPTION";
        } catch (BadRequestException e) {
            result = "BAD_REQUEST";
        }

        Assert.assertEquals(expect, result);
    }
}
