package org.wso2.charon3.core.utils.codeutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.io.IOException;

import static org.wso2.charon3.core.schema.SCIMDefinitions.FilterOperation.EQ;
import static org.wso2.charon3.core.schema.SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.ORGANIZATION;

/**
 * .
 * <br><br>
 * created at: 13.04.2019
 * @author Pascal Knüppel
 */
class FilterTreeManagerTest {

    /**
     * checks that filter expressions are correctly resolved if extension attributes are present in the filter.
     */
    @Test
    public void testThatFilterWithExtensionAttributesAreResolved() throws IOException, BadRequestException {
        final String filterValue = "some organization";
        String filter = FilterBuilder.builder().and(ORGANIZATION, EQ, filterValue).build();
        Assertions.assertEquals(
            "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:organization eq \"some organization\"", filter);

        FilterTreeManager filterTreeManager = new FilterTreeManager(filter, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
        Node node = filterTreeManager.buildTree();
        Assertions.assertEquals(ExpressionNode.class, node.getClass());
        ExpressionNode expressionNode = (ExpressionNode) node;
        Assertions.assertEquals(ORGANIZATION.getURI(), expressionNode.getAttributeValue());
        Assertions.assertEquals(filterValue, expressionNode.getValue());
    }
}
