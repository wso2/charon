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
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.time.Instant;

import static org.testng.Assert.assertEquals;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.BOOLEAN;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DATE_TIME;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.DECIMAL;
import static org.wso2.charon3.core.schema.SCIMDefinitions.DataType.INTEGER;
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

}
