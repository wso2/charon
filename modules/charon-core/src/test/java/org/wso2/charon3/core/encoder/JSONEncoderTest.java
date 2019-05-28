package org.wso2.charon3.core.encoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

/**
 * author Pascal Knueppel <br>.
 * created at: 18.03.2019 - 12:31 <br>
 * <br>
 * the tests witin this class require that the tests in {@link JSONDecoderTest} are working
 */
public class JSONEncoderTest implements FileReferences {

    /**
     * this test will show that the encoding of a group does work as expected.
     */
    @Test
    public void testEncodeGroup() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_GROUP_BREMEN_FILE);
        Group group = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(group);
        Group onceEncodedGroup = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                new Group());
        Assertions.assertEquals(group, onceEncodedGroup);
    }

    /**
     * this test will show that the encoding of a user does work as expected.
     */
    @Test
    public void testEncodeUser() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(user);
        User onceEncodedUser = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                new User());
        Assertions.assertEquals(user, onceEncodedUser);
    }

    /**
     * this test will show that the encoding of an enterprise user does work as expected.
     */
    @Test
    public void testEncodeEnterpriseUser() throws InternalErrorException, BadRequestException, CharonException,
            JSONException {
        String userJson = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(userJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(user);
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(userJson));
        Assertions.assertTrue(decodedJsonObj.has(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI));
        User onceEncodedUser = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                new User());
        Assertions.assertEquals(user, onceEncodedUser);
    }
}
