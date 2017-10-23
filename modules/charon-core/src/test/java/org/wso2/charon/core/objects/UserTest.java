package org.wso2.charon.core.objects;

import org.testng.annotations.Test;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

/**
 * Tests the user bean.
 * Only tests the complex setters.
 */
public class UserTest {

    private static final String TEST_IM_ADDRESS_1 = "test@some.im.wso2.com";
    private static final String TEST_IM_TYPE = "test";

    @Test
    public void testSetIM() throws CharonException {
        User user = new User();

        assertNull(user.getIM(TEST_IM_TYPE));

        user.setIM(TEST_IM_ADDRESS_1, TEST_IM_TYPE, false);
        String im = user.getIM(TEST_IM_TYPE);
        assertNotNull(im);
        assertEquals(im, TEST_IM_ADDRESS_1);
    }

    @Test
    public void testGetPrimaryIM() throws CharonException {
        User user = new User();

        assertNull(user.getIM(TEST_IM_TYPE));
        user.setIM(TEST_IM_ADDRESS_1, TEST_IM_TYPE, true);
        String im = user.getPrimaryIM();
        assertNotNull(im);
        assertEquals(im, TEST_IM_ADDRESS_1);
    }

    @Test
    public void testRemoveFromGroup() throws CharonException {
        User user = new User();
        user.setGroup("test_type", "test_group", "Test Group type");

        List<String> groups = user.getGroups();
        assertNotNull(groups);
        assertEquals(groups.size(), 1);

        user.removeFromGroup("some_other_group");
        groups = user.getGroups();
        assertNotNull(groups);
        assertEquals(groups.size(), 1, "The user should not have been removed and still have one group");

        user.removeFromGroup("test_group");
        groups = user.getGroups();
        assertNotNull(groups);
        assertEquals(groups.size(), 0);
    }

    @Test
    public void testEmails() throws CharonException, NotFoundException {
        User user = new User();
        String[] emails = user.getEmails();
        assertNotNull(emails);
        assertEquals(emails.length, 0);

        user.setEmails(new String[] {"u1@test.wso2.com", "u2@test.wso2.com"});
        emails = user.getEmails();
        assertNotNull(emails);
        assertEquals(emails.length, 2);


        Map<String, Object> emailMap =  new HashMap<>();
        user.setEmail(emailMap);
        emails = user.getEmails();
        assertEquals(emails.length, 2);

        emailMap.put("work", "work@test.wso2.com");
        user.setEmail(emailMap);
        emails = user.getEmails();
    }

    @Test
    public void testPrimaryEmail() throws CharonException, NotFoundException {
        User user = new User();
        Map<String, Object> emailMap =  new HashMap<>();

        MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(SCIMConstants.UserSchemaConstants.EMAILS);

        user.setEmail(emailMap);

//        assertNotNull(user.getPrimaryEmail());
    }
}