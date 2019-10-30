package org.wso2.charon.core.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

import java.util.Calendar;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests attribute util.
 */
public class AttributeUtilTest {

    @Test(dataProvider = "getExpectedAttributeTranslation")
    public void testGetAttributeValueFromString(String attributeValue, SCIMSchemaDefinitions.DataType dataType,
            Object expectedResult) throws Exception {
        assertEquals(AttributeUtil.getAttributeValueFromString(attributeValue, dataType), expectedResult);
    }

    @Test
    public void testGetAttributeValueFromString_Null() throws Exception {
        try {
            AttributeUtil.getAttributeValueFromString("testVal", null);
            fail("Null datatype should throw an exception");
        } catch (Exception e) {
            //This is expected exception
            assertTrue(true);
        }
    }

    @Test(dataProvider = "getExpectedStringTranslation")
    public void testGetStringValueOfAttribute(Object value, SCIMSchemaDefinitions.DataType dataType,
            String expectedString) throws Exception {
        assertEquals(AttributeUtil.getStringValueOfAttribute(value, dataType), expectedString);
    }

    @Test
    public void testParseDateTime() throws Exception {
    }

    @Test
    public void testFormatDateTime() throws Exception {
    }

    @Test
    public void testGetAttributeURI() throws Exception {
    }

    @DataProvider
    private static final Object[][] getExpectedAttributeTranslation() {

        return new Object[][] { { "testVal", SCIMSchemaDefinitions.DataType.STRING, "testVal" },
                { "1", SCIMSchemaDefinitions.DataType.INTEGER, 1 }, { "0", SCIMSchemaDefinitions.DataType.INTEGER, 0 },
                { "-1", SCIMSchemaDefinitions.DataType.INTEGER, -1 },
                { "true", SCIMSchemaDefinitions.DataType.BOOLEAN, true },
                { "false", SCIMSchemaDefinitions.DataType.BOOLEAN, false },
                { "not-true", SCIMSchemaDefinitions.DataType.BOOLEAN, false },
                { "1", SCIMSchemaDefinitions.DataType.BINARY, new Byte((byte) 1) },
                { "-1.1", SCIMSchemaDefinitions.DataType.DECIMAL, -1.1 },
                { "2017-10-18T18:00:00", SCIMSchemaDefinitions.DataType.DATE_TIME, getTestTime() } };
    }

    @DataProvider
    private static final Object[][] getExpectedStringTranslation() {
        return new Object[][] { { "testVal", SCIMSchemaDefinitions.DataType.STRING, "testVal" },
                { 1, SCIMSchemaDefinitions.DataType.INTEGER, "1" }, { 0, SCIMSchemaDefinitions.DataType.INTEGER, "0" },
                { true, SCIMSchemaDefinitions.DataType.BOOLEAN, "true" },
                { false, SCIMSchemaDefinitions.DataType.BOOLEAN, "false" },
                { new Byte((byte) 1), SCIMSchemaDefinitions.DataType.BINARY, "1" },
                { -1.1, SCIMSchemaDefinitions.DataType.DECIMAL, "-1.1" },
                { getTestTime(), SCIMSchemaDefinitions.DataType.DATE_TIME, "2017-10-18T18:00:00Z" } };
    }

    private static Date getTestTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 9, 18, 18, 00);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}