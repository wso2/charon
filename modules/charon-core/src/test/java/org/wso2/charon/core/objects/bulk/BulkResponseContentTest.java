package org.wso2.charon.core.objects.bulk;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests complex methods of the bean.
 */
public class BulkResponseContentTest {

    @Test
    public void testGetDescription() throws Exception {
        BulkResponseContent bulkResponseContent = new BulkResponseContent();
        bulkResponseContent.setDescription("Some Description");
        assertEquals(bulkResponseContent.getDescription(), "Some Description");
    }

    @Test
    public void testToString() throws Exception {
        BulkResponseContent bulkResponseContent = new BulkResponseContent();

        assertNotNull(bulkResponseContent.toString());
    }

}