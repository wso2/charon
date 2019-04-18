package org.wso2.charon3.core.setup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.wso2.charon3.core.config.BulkFeature;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.config.FilterFeature;
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

    @BeforeAll
    public static void initializeCharon() {
        Map<String, String> endpointMapUrl = new HashMap<>();
        endpointMapUrl.put(SCIMConstants.USER_ENDPOINT, BASE_URI + SCIMConstants.USER_ENDPOINT);
        endpointMapUrl.put(SCIMConstants.GROUP_ENDPOINT, BASE_URI + SCIMConstants.GROUP_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMapUrl);

        CharonConfiguration.getInstance().getFilter().setMaxResults(Integer.MAX_VALUE);
    }

    @BeforeEach
    public void initialize() {
        this.userResourceHandler = Mockito.spy(new UserResourceHandler());
        this.groupResourceHandler = Mockito.spy(new GroupResourceHandler());
        this.userManager = Mockito.spy(new UserManager(userResourceHandler));
        this.groupManager = Mockito.spy(new GroupManager(groupResourceHandler));
    }

    @AfterEach
    public void resetServiceProviderConfiguration() {
        CharonConfiguration.getInstance().setFilter(new FilterFeature(false, CharonConfiguration.DEFAULT_MAX_RESULTS));
        CharonConfiguration.getInstance().setBulk(new BulkFeature(false, CharonConfiguration.DEFAULT_MAX_OPERATIONS,
            CharonConfiguration.DEFAULT_MAX_OPERATIONS));
    }
}
