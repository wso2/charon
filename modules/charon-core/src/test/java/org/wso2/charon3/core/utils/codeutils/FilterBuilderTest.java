package org.wso2.charon3.core.utils.codeutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.wso2.charon3.core.schema.SCIMConstants.EnterpriseUserSchemaConstants.MANAGER_URI;
import static org.wso2.charon3.core.schema.SCIMDefinitions.FilterOperation.CO;
import static org.wso2.charon3.core.schema.SCIMDefinitions.FilterOperation.EQ;
import static org.wso2.charon3.core.schema.SCIMDefinitions.FilterOperation.SW;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_DISPLAY;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_TYPE;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAIL_VALUE;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.FAMILY_NAME;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GIVEN_NAME;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NICK_NAME;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS_DISPLAY;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME;

/**
 * author Pascal Knueppel <br>
 * created at: 22.03.2019 - 15:33 <br>
 * <br>
 */
public class FilterBuilderTest {

    /**
     * tests that the {@link FilterBuilder} works correctly
     */
    @Test
    public void testFilterBuilder() {
        final String randomValue = UUID.randomUUID().toString();
        String filter = FilterBuilder.builder().and(EMAIL_DISPLAY, EQ, randomValue).build();
        Assertions.assertEquals("emails.display eq \"" + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(EMAIL_TYPE, EQ, randomValue).build();
        Assertions.assertEquals("emails.type eq \"" + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(EMAIL_TYPE, CO, randomValue).build();
        Assertions.assertEquals("emails.type co \"" + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(PHONE_NUMBERS_DISPLAY, CO, randomValue).build();
        Assertions.assertEquals("phoneNumbers.display co \"" + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(MANAGER, CO, randomValue).build();
        Assertions.assertEquals(MANAGER_URI + " co \"" + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(MANAGER, CO, randomValue)
                .and(USERNAME, SW, randomValue).build();
        Assertions.assertEquals(MANAGER_URI + " co \"" + randomValue + "\" and userName sw \""
                + randomValue + "\"", filter);

        filter = FilterBuilder.builder().and(EMAIL_VALUE, EQ, randomValue)
                .and(USERNAME, SW, randomValue)
                .leftParenthesis()
                .or(NICK_NAME, EQ, randomValue)
                .or(GIVEN_NAME, EQ, randomValue)
                .rightParenthesis()
                .and(FAMILY_NAME, EQ, randomValue).build();
        Assertions.assertEquals(
                "emails.value eq \"" + randomValue + "\" and "
                        + "userName sw \"" + randomValue + "\" or "
                        + "(nickName eq \"" + randomValue + "\" or "
                        + "name.givenName eq \"" + randomValue + "\") and "
                        + "name.familyName eq \"" + randomValue + "\""
                , filter);
    }
}