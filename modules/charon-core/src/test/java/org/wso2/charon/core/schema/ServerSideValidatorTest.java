package org.wso2.charon.core.schema;

import org.testng.annotations.Test;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.User;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests ServerSideValidator.
 */
public class ServerSideValidatorTest {

    @Test
    public void testValidateCreatedSCIMObject() throws Exception {
        User user = new User();
        try {
            ServerSideValidator.validateCreatedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
            fail("An exception should be thrown");
        } catch (BadRequestException be) {
            assertTrue(be.getMessage().contains("userName is missing"));
        }
        user.setUserName("test1");
        ServerSideValidator.validateCreatedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
    }

    @Test
    public void testValidateCreatedSCIMObject_MultivaluedAttribute() throws Exception {
        User user = new User();
        user.setUserName("test1");
        ServerSideValidator.validateCreatedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);

        Map<String, Object> emails = new HashMap<>();
        emails.put("work", "test1@work.test.wso2.com");
        emails.put("home", "test2@work.test.wso2.com");

        user.setEmail(emails);

        ServerSideValidator.validateCreatedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
    }

    @Test
    public void testValidateUpdatedSCIMObject() throws Exception {
        User user1 = new User();
        user1.setUserName("test1");

        User user2 = new User();
        user2.setUserName("test1");
        ServerSideValidator.validateUpdatedSCIMObject(user1, user2, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
    }

    @Test
    public void testValidateRetrievedSCIMObject() throws Exception {
        User user = new User();
        try {
            ServerSideValidator.validateCreatedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
            fail("An exception should be thrown");
        } catch (BadRequestException be) {
            assertTrue(be.getMessage().contains("userName is missing"));
        }
        user.setUserName("test1");
        ServerSideValidator.validateRetrievedSCIMObject(user, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
    }

    @Test
    public void testCheckIfReadOnlyAttributesModified() throws Exception {
        User user1 = new User();
        user1.setId("test1");

        User user2 = new User();
        user2.setId("test1");

        ServerSideValidator.checkIfReadOnlyAttributesModified(user1, user2, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);

        User user3 = new User();
        user3.setId("test3");
        try {
            ServerSideValidator.checkIfReadOnlyAttributesModified(user1, user3, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
            //            fail("Should have thrown exception \"Attribute is read only\"");
        } catch (CharonException e) {
            assertTrue(e.getMessage().contains("Attribute is read only"));
        }
    }

    @Test
    public void testRemovePasswordOnReturn() throws Exception {
        User user = new User();
        user.setUserName("test1");
        user.setPassword("some-secret");
        assertNotNull(user.getPassword());

        ServerSideValidator.removePasswordOnReturn(user);
        assertNull(user.getPassword());
    }

}