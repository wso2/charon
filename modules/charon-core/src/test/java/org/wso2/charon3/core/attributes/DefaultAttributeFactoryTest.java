/*
 * Copyright (c) 2025,  WSO2 LLC. (http://www.wso2.com).
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
package org.wso2.charon3.core.attributes;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.time.Instant;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BOOLEAN;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.COMPLEX;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DATE_TIME;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DECIMAL;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.INTEGER;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.REFERENCE;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.STRING;

public class DefaultAttributeFactoryTest {

    @Test(dataProvider = "isAttributeDataTypeValidDataProvider")
    public void testIsAttributeDataTypeValid(Object attribute, SCIMDefinitions.DataType dataType,
                                             boolean expectedResult) throws BadRequestException {

        boolean validationResult = DefaultAttributeFactory.isAttributeDataTypeValid(attribute, dataType);
        assertEquals(validationResult, expectedResult, "Validation failed for attribute: " + attribute +
                " and data type: " + dataType);
    }

    @DataProvider(name = "isAttributeDataTypeValidDataProvider")
    public Object[][] isAttributeDataTypeValidDataProvider() {

        return new Object[][]{
                // Valid cases.
                {"name", STRING, true},
                {true, BOOLEAN, true},
                {"", INTEGER, true},
                {123, INTEGER, true},
                {"", DECIMAL, true},
                {12.23, DECIMAL, true},
                {Instant.now(), DATE_TIME, true},
                {new Byte[]{1, 2, 3}, SCIMDefinitions.DataType.BINARY, true},
                {"http://example.com", SCIMDefinitions.DataType.REFERENCE, true},
                {"{\"name\":\"John\"}", SCIMDefinitions.DataType.COMPLEX, true},

                // Invalid cases.
                {123, STRING, false},
                {"", BOOLEAN, false},
                {true, INTEGER, false},
                {"2023-01-01", DATE_TIME, false},
                {10, DECIMAL, false},
                {Instant.now(), SCIMDefinitions.DataType.BINARY, false},
                {new Byte[]{1, 2, 3}, SCIMDefinitions.DataType.REFERENCE, false},
                {123, SCIMDefinitions.DataType.COMPLEX, false}
        };
    }

    /**
     * Builds a minimal-but-complete attribute schema so that {@code createAttribute} can stamp all the
     * schema-derived characteristics (mutability, returned, etc.) without NPEs of its own.
     */
    private static SCIMAttributeSchema buildSchema(String name, SCIMDefinitions.DataType type) {

        return SCIMAttributeSchema.createSCIMAttributeSchema(
                "urn:ietf:params:scim:schemas:core:2.0:Role:audience." + name,
                name, type, false, "test attribute", false, false,
                SCIMDefinitions.Mutability.READ_WRITE, SCIMDefinitions.Returned.DEFAULT,
                SCIMDefinitions.Uniqueness.NONE, null, null, null);
    }

    /**
     * Regression test for issue #28010 (internal patch #7428).
     * <p>
     * A Role audience sub-attribute (e.g. {@code display}) whose value cannot be resolved is built as a
     * {@link SimpleAttribute} with a {@code null} value. Before the fix, {@code createSimpleAttribute} returned
     * early on the null-value path without stamping the schema data type, leaving {@code getType()} null. That
     * later triggered an NPE in {@code AbstractValidator.validateReturnedAttributes} (and HTTP 500 from the SCIM2
     * Roles endpoint). The type is schema-derived, not value-derived, so it must always be set.
     */
    @Test
    public void testCreateAttributeSetsTypeWhenSimpleAttributeValueIsNull()
            throws CharonException, BadRequestException {

        SCIMAttributeSchema schema = buildSchema("display", STRING);
        SimpleAttribute nullValuedAttribute = new SimpleAttribute("display", null);

        // Sanity: the type is not set on the raw attribute before factory processing.
        assertNull(nullValuedAttribute.getType(), "Precondition: raw attribute type should be null.");

        Attribute created = DefaultAttributeFactory.createAttribute(schema, nullValuedAttribute);

        assertNotNull(created.getType(),
                "Type must be stamped from the schema even when the attribute value is null (issue #28010).");
        assertEquals(created.getType(), STRING,
                "Type must equal the schema-defined data type when the value is null.");
        assertNull(((SimpleAttribute) created).getValue(),
                "The null value must be preserved; only the type is derived from the schema.");
    }

    /**
     * Edge cases around the null-value path: the schema-defined type must be set regardless of which data type
     * the schema declares, and no {@link BadRequestException} should be raised for a null value.
     */
    @Test(dataProvider = "nullValueSchemaTypeProvider")
    public void testCreateSimpleAttributeSetsSchemaTypeForNullValue(SCIMDefinitions.DataType schemaType)
            throws CharonException, BadRequestException {

        SCIMAttributeSchema schema = buildSchema("audienceSub", schemaType);
        SimpleAttribute nullValuedAttribute = new SimpleAttribute("audienceSub", null);

        SimpleAttribute result = DefaultAttributeFactory.createSimpleAttribute(schema, nullValuedAttribute);

        assertEquals(result.getType(), schemaType,
                "Null-valued simple attribute must adopt the schema data type: " + schemaType);
    }

    @DataProvider(name = "nullValueSchemaTypeProvider")
    public Object[][] nullValueSchemaTypeProvider() {

        return new Object[][]{
                {STRING},
                {REFERENCE},
                {COMPLEX},
                {BOOLEAN},
                {INTEGER},
                {DATE_TIME}
        };
    }

    /**
     * Negative / regression guard: a non-null value whose runtime type does not match the schema data type must
     * still raise a {@link BadRequestException}. The fix only touches the null-value branch — the type-mismatch
     * validation on the value-present branch must be unchanged.
     */
    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateSimpleAttributeStillRejectsTypeMismatchedValue()
            throws CharonException, BadRequestException {

        SCIMAttributeSchema schema = buildSchema("count", INTEGER);
        // A String value against an INTEGER schema is a type mismatch.
        SimpleAttribute mismatched = new SimpleAttribute("count", "not-an-integer");

        DefaultAttributeFactory.createSimpleAttribute(schema, mismatched);
    }

    /**
     * Positive regression guard for the value-present branch: a non-null value that matches the schema data type
     * must continue to have its type stamped and its value preserved.
     */
    @Test
    public void testCreateSimpleAttributeSetsTypeForValidNonNullValue()
            throws CharonException, BadRequestException {

        SCIMAttributeSchema schema = buildSchema("value", STRING);
        SimpleAttribute attribute = new SimpleAttribute("value", "resolved-id");

        SimpleAttribute result = DefaultAttributeFactory.createSimpleAttribute(schema, attribute);

        assertEquals(result.getType(), STRING, "Valid non-null value must adopt the schema data type.");
        assertEquals(result.getValue(), "resolved-id", "Non-null value must be preserved.");
    }

}
