package org.wso2.charon3.core.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

import java.util.List;
import java.util.UUID;

/**
 *
 * <br><br>
 * created at: 14.04.2019
 * @author Pascal Knüppel
 */
public class GroupTest implements FileReferences {

    @Test
    public void getMembersFromGroup() throws InternalErrorException, BadRequestException, CharonException {
        final String userId = UUID.randomUUID().toString();
        final String groupId = UUID.randomUUID().toString();
        String groupString = readResourceFile(CREATE_GROUP_BREMEN_WITH_MEMBERS_FILE,
            content -> content.replaceAll("\\$\\{user1}", userId)).replaceAll("\\$\\{group1}", groupId);
        Group groupBremen = JSON_DECODER.decodeResource(groupString, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
            new Group());

        Assertions.assertNotNull(groupBremen.getMembers());
        Assertions.assertEquals(2, groupBremen.getMembers().size());
        Assertions.assertEquals(1, groupBremen.getMemberIdsOfType(SCIMConstants.USER).size());
        Assertions.assertEquals(1, groupBremen.getMemberIdsOfType(SCIMConstants.GROUP).size());

        List<MultiValuedComplexType> members = groupBremen.getMembersAsComplexType();
        Assertions.assertEquals(2, members.size());
        Assertions.assertEquals(userId, members.get(0).getValue());
        Assertions.assertEquals(groupId, members.get(1).getValue());
    }
}
