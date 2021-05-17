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

/**
 * Test class of PatchOperationUtil.
 */
public class PatchOperationUtilTest {

    final String remove = "remove";
    final String add = "add";
    final String replace = "replace";

    @DataProvider(name = "dataForPatchRemoveSuccess")
    public Object[][] dataToPatchRemoveSuccess() throws InstantiationException, IllegalAccessException,
            CharonException {

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();

        patchOperation1.setOperation(remove);
        patchOperation1.setPath("nickName");

        patchOperation2.setOperation(remove);
        patchOperation2.setPath("emails[type eq home]");

        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, oldResource, copyOfOldResource, scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForPatchRemoveSuccess")
    public void testDoPatchRemoveSuccess(PatchOperation operation, AbstractSCIMObject oldResource,
                                         AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws BadRequestException, NotImplementedException, CharonException {

        AbstractSCIMObject validatedResource = PatchOperationUtil.doPatchRemove(operation, oldResource,
                copyOfOldResource, schema);

        Assert.assertNotNull(validatedResource);
    }

    @DataProvider(name = "dataForPatchRemoveExceptions")
    public Object[][] dataToPatchRemoveExceptions() throws InstantiationException, IllegalAccessException,
            CharonException {

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();
        PatchOperation patchOperation3 = new PatchOperation();

        patchOperation1.setOperation(remove);

        patchOperation2.setOperation(remove);
        patchOperation2.setPath("permissions");

        patchOperation3.setOperation(remove);
        patchOperation3.setPath("emails[type sw home]");

        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation3, oldResource, copyOfOldResource, scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForPatchRemoveExceptions",
            expectedExceptions = {BadRequestException.class, NotImplementedException.class})
    public void testDoPatchRemoveExceptions(PatchOperation operation, AbstractSCIMObject oldResource,
                                            AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws BadRequestException, NotImplementedException, CharonException {

        PatchOperationUtil.doPatchRemove(operation, oldResource, copyOfOldResource, schema);
    }

    @DataProvider(name = "dataForPatchAddSuccess")
    public Object[][] dataToPatchAddSuccess() throws InstantiationException, IllegalAccessException, CharonException {

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();

        patchOperation1.setOperation(add);
        patchOperation1.setPath("country");
        patchOperation1.setValues("UK");

        patchOperation2.setOperation(add);
        patchOperation2.setPath("manager.displayName");
        patchOperation2.setValues("abc");

        JSONDecoder jsonDecoder = new JSONDecoder();
        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForPatchAddSuccess")
    public void testDoPatchAddSuccess(PatchOperation operation, JSONDecoder decoder, AbstractSCIMObject oldResource,
                                      AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        AbstractSCIMObject validatedResource = PatchOperationUtil.doPatchAdd(operation, decoder, oldResource,
                copyOfOldResource, schema);

        Assert.assertNotNull(validatedResource);
    }

    @DataProvider(name = "dataForPatchAddExceptions")
    public Object[][] dataToPatchAddExceptions() throws InstantiationException, IllegalAccessException,
            CharonException {

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();

        patchOperation1.setOperation(add);

        patchOperation2.setOperation(add);
        patchOperation2.setPath("permissions");
        patchOperation2.setValues(READ_WRITE);

        JSONDecoder jsonDecoder = new JSONDecoder();
        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema},
        };
    }

    @Test(dataProvider = "dataForPatchAddExceptions",
            expectedExceptions = {NotImplementedException.class, BadRequestException.class})
    public void testDoPatchAddExceptions(PatchOperation operation, JSONDecoder decoder, AbstractSCIMObject oldResource,
                                         AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        PatchOperationUtil.doPatchAdd(operation, decoder, oldResource, copyOfOldResource, schema);
    }

    @DataProvider(name = "dataForPatchReplaceSuccess")
    public Object[][] dataToPatchReplaceSuccess() throws InstantiationException, IllegalAccessException,
            CharonException {

        Map<String, String> operationValueName = new HashMap<>();
        operationValueName.put("givenName", "John");
        operationValueName.put("familyName", "Anderson");

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();

        patchOperation1.setOperation(replace);
        patchOperation1.setPath("manager.displayName");
        patchOperation1.setValues("Rash");

        patchOperation2.setOperation(replace);
        patchOperation2.setPath("emails[type eq home].value");
        patchOperation2.setValues("rash123@gmail.com");

        JSONDecoder jsonDecoder = new JSONDecoder();
        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForPatchReplaceSuccess")
    public void testDoPatchReplaceSuccess(PatchOperation operation, JSONDecoder decoder, AbstractSCIMObject oldResource,
                                          AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, NotImplementedException, BadRequestException, InternalErrorException {

        AbstractSCIMObject validatedResource = PatchOperationUtil.doPatchReplace(operation, decoder, oldResource,
                copyOfOldResource, schema);

        Assert.assertNotNull(validatedResource);
    }

    @DataProvider(name = "dataForPatchReplaceExceptions")
    public Object[][] dataToPatchReplaceExceptions() throws InstantiationException, IllegalAccessException,
            CharonException {

        Map<String, String> operationValueName = new HashMap<>();
        operationValueName.put("givenName", "John");
        operationValueName.put("familyName", "Anderson");

        PatchOperation patchOperation1 = new PatchOperation();
        PatchOperation patchOperation2 = new PatchOperation();

        patchOperation1.setOperation(replace);
        patchOperation1.setValues("Rash");

        patchOperation2.setOperation(replace);
        patchOperation2.setPath("name");
        patchOperation2.setValues(operationValueName);

        JSONDecoder jsonDecoder = new JSONDecoder();
        User oldResource = getUser();
        User copyOfOldResource = (User) CopyUtil.deepCopy(oldResource);
        SCIMResourceTypeSchema scimResourceTypeSchema = getSchema();

        return new Object[][]{

                {patchOperation1, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema},
                {patchOperation2, jsonDecoder, oldResource, copyOfOldResource, scimResourceTypeSchema}
        };
    }

    @Test(dataProvider = "dataForPatchReplaceExceptions",
            expectedExceptions = {CharonException.class, BadRequestException.class})
    public void testDoPatchReplaceExceptions(PatchOperation operation, JSONDecoder decoder,
            AbstractSCIMObject oldResource, AbstractSCIMObject copyOfOldResource, SCIMResourceTypeSchema schema)
            throws CharonException, BadRequestException, NotImplementedException, InternalErrorException {

        PatchOperationUtil.doPatchReplace(operation, decoder, oldResource, copyOfOldResource, schema);
    }

    private SCIMResourceTypeSchema getSchema() {

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
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:country",
                        "country", STRING, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, null);
        AttributeSchema attributeSchema4 =
                SCIMAttributeSchema.createSCIMAttributeSchema(
                        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:manager",
                        "manager", COMPLEX, false, "", false, false,
                        READ_WRITE, DEFAULT, NONE, null, null, subAttributeSchemaList2);
        SCIMResourceTypeSchema scimResourceTypeSchema =
                SCIMResourceTypeSchema.createSCIMResourceSchema(schemasList, attributeSchema1, attributeSchema2,
                        attributeSchema3, attributeSchema4);

        return scimResourceTypeSchema;
    }

    private User getUser() throws InstantiationException, IllegalAccessException {

        User user = new User();
        user.setSchema("urn:ietf:params:scim:schemas:core:2.0:User");
        user.setSchema("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");

        SimpleAttribute simpleAttribute = new SimpleAttribute("nickName", "shaggy");
        simpleAttribute.setMutability(SCIMDefinitions.Mutability.READ_WRITE);
        simpleAttribute.setRequired(false);
        user.setAttribute(simpleAttribute);

        SimpleAttribute simpleAttribute1 = new SimpleAttribute("id", "229d3f0d-a07b-4052-bf4d-3071ecafed04");
        simpleAttribute1.setMutability(SCIMDefinitions.Mutability.READ_ONLY);
        simpleAttribute1.setRequired(false);
        user.setAttribute(simpleAttribute1);

        ComplexAttribute complexAttributeMeta = new ComplexAttribute("meta");
        user.setAttribute(complexAttributeMeta);

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
        user.setAttribute(multiValuedAttributeEmail);

        return user;
    }
}
