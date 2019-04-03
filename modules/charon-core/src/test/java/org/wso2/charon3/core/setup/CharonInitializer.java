package org.wso2.charon3.core.setup;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.setup.resourcehandler.GroupResourceHandler;
import org.wso2.charon3.core.setup.resourcehandler.UserResourceHandler;
import org.wso2.charon3.core.setup.resourcemanagers.GroupManager;
import org.wso2.charon3.core.setup.resourcemanagers.UserManager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public abstract class CharonInitializer {

    public static final String BASE_URI = "https://localhost/charon/v2";

    @BeforeAll
    public static void initializeCharon() {
        Map<String, String> endpointMapUrl = new HashMap<>();
        endpointMapUrl.put(SCIMConstants.USER_ENDPOINT, BASE_URI + SCIMConstants.USER_ENDPOINT);
        endpointMapUrl.put(SCIMConstants.GROUP_ENDPOINT, BASE_URI + SCIMConstants.GROUP_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMapUrl);

        CharonConfiguration.getInstance().setCountValueForPagination(Integer.MAX_VALUE);
    }

    /**
     * the user resource manager that executes the SCIM charon implementation
     */
    protected UserManager userManager;

    /**
     * the group resource manager that executes the SCIM charon implementation
     */
    protected GroupManager groupManager;

    /**
     * the user resource handler that represents the user implementation
     */
    protected UserResourceHandler userResourceHandler;

    /**
     * the group resource handler that represents the group implementation
     */
    protected GroupResourceHandler groupResourceHandler;

    @BeforeEach
    public void initialize() {
        this.userResourceHandler = Mockito.spy(new UserResourceHandler());
        this.groupResourceHandler = Mockito.spy(new GroupResourceHandler());
        this.userManager = Mockito.spy(new UserManager(userResourceHandler));
        this.groupManager = Mockito.spy(new GroupManager(groupResourceHandler));
    }
}
