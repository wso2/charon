package org.wso2.charon3.core.setup;

import org.junit.jupiter.api.BeforeAll;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

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

    @BeforeAll public static void initializeCharon() {
        Map<String, String> endpointMapUrl = new HashMap<>();
        endpointMapUrl.put(SCIMConstants.USER_ENDPOINT, BASE_URI + SCIMConstants.USER_ENDPOINT);
        endpointMapUrl.put(SCIMConstants.GROUP_ENDPOINT, BASE_URI + SCIMConstants.GROUP_ENDPOINT);
        AbstractResourceManager.setEndpointURLMap(endpointMapUrl);
    }

}
