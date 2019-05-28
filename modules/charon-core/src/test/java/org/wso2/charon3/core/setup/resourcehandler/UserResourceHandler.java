package org.wso2.charon3.core.setup.resourcehandler;

import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

/**
 *.
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public class UserResourceHandler extends InMemoryResourceHandler<User> {

    @Override
    protected String getResourceType() {
        return SCIMConstants.USER;
    }

    @Override
    public String getResourceEndpoint() {
        return SCIMConstants.USER_ENDPOINT;
    }

    @Override
    public SCIMResourceTypeSchema getResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
    }
}
