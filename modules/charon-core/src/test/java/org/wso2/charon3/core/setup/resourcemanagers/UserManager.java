package org.wso2.charon3.core.setup.resourcemanagers;

import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;

/**
 *.
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public class UserManager extends ResourceManager<User> {

    public UserManager(ResourceHandler<User> resourceHandler) {
        super(resourceHandler);
    }
}
