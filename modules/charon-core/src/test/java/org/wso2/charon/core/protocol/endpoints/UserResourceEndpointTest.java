package org.wso2.charon.core.protocol.endpoints;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.utils.InMemroyUserManager;

import static org.testng.Assert.*;

/**
 * Tests UserResourceEndpoint.
 */
public class UserResourceEndpointTest {

    private InMemroyUserManager userManager = new InMemroyUserManager(1, "wso2.org");
    private UserResourceEndpoint userResourceEndpoint;

    @BeforeTest
    protected void setUp() {
        userResourceEndpoint = new UserResourceEndpoint();
    }

    @Test
    public void testGet() throws Exception {
        SCIMResponse response = userResourceEndpoint.get("testUser", "test", userManager);
        assertNotNull(response);
    }

    @Test
    public void testCreate() throws Exception {
    }

    @Test
    public void testCreate1() throws Exception {
    }

    @Test
    public void testDelete() throws Exception {
    }

    @Test
    public void testListByAttribute() throws Exception {
    }

    @Test
    public void testListByFilter() throws Exception {
    }

    @Test
    public void testListByFilterAndAttribute() throws Exception {
    }

    @Test
    public void testListBySort() throws Exception {
    }

    @Test
    public void testListWithPagination() throws Exception {
    }

    @Test
    public void testList() throws Exception {
    }

    @Test
    public void testUpdateWithPUT() throws Exception {
    }

    @Test
    public void testUpdateWithPATCH() throws Exception {
    }

    @Test
    public void testCreateListedResource() throws Exception {
    }

}