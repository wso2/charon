package org.wso2.charon.core.objects.bulk;

import org.testng.annotations.Test;
import org.wso2.charon.core.objects.Group;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Tests complex methods on the bean.
 */
public class BulkDataTest {

    @Test
    public void testGetGroupList() throws Exception {
        BulkData bulkData = new BulkData();
        bulkData.addGroup(new Group());

        List<Group> groupList = bulkData.getGroupList();
        assertEquals(groupList.size(), 1);
    }
}