package org.wso2.charon.core.objects.bulk;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests the complex operations on the BulkRequestData.
 */
public class BulkRequestDataTest {

    @Test
    public void testToString() throws Exception {
        BulkRequestData bulkRequestData = new BulkRequestData();
        bulkRequestData.getUserCreatingRequests().add(new BulkRequestContent());
        bulkRequestData.getGroupCreatingRequests().add(new BulkRequestContent());
        assertNotNull(bulkRequestData.toString());
    }

}