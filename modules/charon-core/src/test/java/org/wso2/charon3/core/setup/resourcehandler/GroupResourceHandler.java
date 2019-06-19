package org.wso2.charon3.core.setup.resourcehandler;

import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

/**
 * .
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public class GroupResourceHandler extends InMemoryResourceHandler<Group> {

    @Override
    protected String getResourceType() {
        return SCIMConstants.GROUP;
    }

    @Override
    public String getResourceEndpoint() {
        return SCIMConstants.GROUP_ENDPOINT;
    }

    @Override
    public SCIMResourceTypeSchema getResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA;
    }
}
