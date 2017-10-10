package org.wso2.charon3.core.utils;

import org.testng.annotations.Test;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.utils.codeutils.PatchOperation;

import java.util.Date;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Tests PatchOperationUtil.
 */
public class PatchOperationUtilTest {

    private JSONDecoder decoder = new JSONDecoder();
    private SCIMResourceSchemaManager scimResourceSchemaManager = SCIMResourceSchemaManager.getInstance();

    @Test
    public void testDoPatchAdd() throws Exception {
        PatchOperation patchOperation = new PatchOperation();
        patchOperation.setOperation(SCIMConstants.OperationalConstants.ADD);

        User user = createUser();

        User userCopy = CopyUtil.deepCopyScim(user);
        try {
            PatchOperationUtil
                    .doPatchAdd(null, decoder, user, userCopy, scimResourceSchemaManager.getUserResourceSchema());
            fail("Operation null, Thus an exception should be thrown");
        } catch (CharonException ce) {
            //Expected exception.
        }
        try {
            PatchOperationUtil.doPatchAdd(patchOperation, decoder, user, userCopy,
                    scimResourceSchemaManager.getUserResourceSchema());
            fail("Operation not defined, Thus an exception should be thrown");
        } catch (CharonException ce) {
            //Expected exception.
        }

        patchOperation.setValues("{userName : \"foo\"}");
        AbstractSCIMObject patchedObject = PatchOperationUtil
                .doPatchAdd(patchOperation, decoder, user, userCopy, scimResourceSchemaManager.getUserResourceSchema());
        assertNotNull(patchedObject);
    }

    @Test
    public void testDoPatchRemove() throws Exception {

    }

    @Test
    public void testDoPatchReplace() throws Exception {
        PatchOperation patchOperation = new PatchOperation();
        patchOperation.setOperation(SCIMConstants.OperationalConstants.EQ);

        User user = createUser();

        User userCopy = CopyUtil.deepCopyScim(user);

        patchOperation.setValues("{userName : \"foo\"}");
        AbstractSCIMObject patchedObject = PatchOperationUtil.doPatchReplace(patchOperation, decoder, user, userCopy,
                scimResourceSchemaManager.getUserResourceSchema());
        assertNotNull(patchedObject);

    }

    @Test
    public void testDoPatchReplace_WithPath() throws Exception {
        PatchOperation patchOperation = new PatchOperation();
        patchOperation.setOperation(SCIMConstants.OperationalConstants.EQ);

        User user = createUser();

        User userCopy = CopyUtil.deepCopyScim(user);

        patchOperation.setValues("{userName : \"foo\"}");

        patchOperation.setPath("");
        assertInvalidPathForPatchReplace(patchOperation, user, userCopy);

        patchOperation.setPath("[userName Eq \"testUser1\"\n]");
        assertInvalidPathForPatchReplace(patchOperation, user, userCopy);
        patchOperation.setPath("userName[userName Eq \"testUser1\"\n]");
        assertInvalidPathForPatchReplace(patchOperation, user, userCopy);

    }

    /**
     * Tests the patch operation with incorrect path throws correct exception.
     *
     * @param patchOperation
     * @param user
     * @param userCopy
     * @throws CharonException
     * @throws NotImplementedException
     * @throws BadRequestException
     * @throws InternalErrorException
     */
    private void assertInvalidPathForPatchReplace(PatchOperation patchOperation, User user, User userCopy)
            throws CharonException, NotImplementedException, InternalErrorException {
        try {
            PatchOperationUtil.doPatchReplace(patchOperation, decoder, user, userCopy,
                    scimResourceSchemaManager.getUserResourceSchema());
            fail("There should be BadRequestException thrown.");
        } catch (BadRequestException bre) {
            //This exception is required.
        }
    }

    private User createUser() throws CharonException, BadRequestException {
        User user = new User();
        user.setCreatedDate(new Date());
        user.setId("testUser1");
        user.setSchema(SCIMConstants.USER_CORE_SCHEMA_URI);
        return user;
    }

}
