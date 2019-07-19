package org.wso2.charon3.core.objects.plainobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.time.Instant;
import java.util.UUID;

/**
 * .
 * <br><br>
 * created at: 14.04.2019
 * @author Pascal Knüppel
 */
public class MetaTest {

    @Test
    public void testMeta() {
        User user = new User();

        String resourceType = SCIMConstants.USER;
        Instant created = Instant.now().minusSeconds(60);
        Instant lastModified = Instant.now();
        String location = "http://localhost/v2/Users/123456";
        String version = UUID.randomUUID().toString();
        Meta meta = new Meta(resourceType, created, lastModified, location, version);

        user.setMeta(meta);

        Assertions.assertNotNull(user.getMeta());
        Assertions.assertEquals(resourceType, user.getMeta().getResourceType());
        Assertions.assertEquals(created, user.getMeta().getCreated());
        Assertions.assertEquals(lastModified, user.getMeta().getLastModified());
        Assertions.assertEquals(location, user.getMeta().getLocation());
        Assertions.assertEquals(version, user.getMeta().getVersion());
    }
}
