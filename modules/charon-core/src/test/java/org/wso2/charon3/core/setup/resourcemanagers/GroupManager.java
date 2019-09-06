package org.wso2.charon3.core.setup.resourcemanagers;

import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;

/**
 * .
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public class GroupManager extends ResourceManager<Group> {

    public GroupManager(ResourceHandler<Group> resourceHandler) {
        super(resourceHandler);
    }
}
