package org.wso2.charon.core.protocol.endpoints;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.extensions.Storage;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.utils.InMemroyUserManager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests UserResourceEndpoint.
 */
public class UserResourceEndpointTest {

    private InMemroyUserManager userManager;
    private UserResourceEndpoint userResourceEndpoint;
    private String format = SCIMConstants.APPLICATION_JSON;

    @BeforeTest
    protected void setUp() throws CharonException {
        userResourceEndpoint = new UserResourceEndpoint();
        AbstractResourceEndpoint.registerEncoder(SCIMConstants.JSON, new JSONEncoder());
        AbstractResourceEndpoint.registerDecoder(SCIMConstants.JSON, new JSONDecoder());
    }

    @BeforeMethod
    protected void initEachTest() throws CharonException {
        userManager = new InMemroyUserManager(1, "wso2.org");
    }

    @Test
    public void testGet() throws Exception {
        //        String format = SCIMConstants.APPLICATION_JSON;
        //        SCIMResponse response = userResourceEndpoint.get("testUser", format, userManager);
        //        assertNotNull(response);
    }

    @Test
    public void testCreate() throws Exception {
        String fileName = this.getClass().getResource("user1.json").getFile();
        String userString = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString, format, format, userManager, false);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_CREATED);

        response = userResourceEndpoint.create(userString, format, format, null, false);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);

        response = userResourceEndpoint.create(userString, format, "text/plain", userManager, false);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);

        response = userResourceEndpoint.create(userString, "text/plain", format, userManager, false);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);
    }

    @Test
    public void testCreate1() throws Exception {
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);

        SCIMResponse response = userResourceEndpoint.create(userString1, "text/plain", format, userManager);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);
    }

    @Test
    public void testDelete() throws Exception {
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));
        String createdUserId = decodedJsonObj.getString("id");

        response = userResourceEndpoint.delete(createdUserId, userManager, format);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_OK);

        response = userResourceEndpoint.delete("not-a-user-id", userManager, format);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);

        response = userResourceEndpoint.delete(createdUserId, userManager, "text/plain");
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);

        Storage storage = new Storage() {

        };
        response = userResourceEndpoint.delete(createdUserId, storage, format);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testListByAttribute() throws Exception {
    }

    @Test
    public void testListByFilter() throws Exception {
    }

    @Test(dataProvider = "listByFilterAndAttribute_Success")
    public void testListByFilterAndAttribute(String filter, String searchAttribute, int expectedResults)
            throws Exception {
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);

        response = userResourceEndpoint.listByFilterAndAttribute(filter, searchAttribute, userManager, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_OK);
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(response.getResponseMessage()));
        Object totalResults = decodedJsonObj.get("totalResults");
        assertEquals(totalResults, expectedResults);
    }

    @Test(dataProvider = "listByFilterAndAttribute_Fail")
    public void testListByFilterAndAttribute_Fail(String filter, int code) throws Exception {
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);

        //Test the bad requests
        response = userResourceEndpoint.listByFilterAndAttribute(filter, null, userManager, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), code);
    }

    @Test
    public void testListByFilterAndAttribute_Exceptions() throws Exception {
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);

        response = userResourceEndpoint.listByFilterAndAttribute("userType eq Employee", null, null, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);

        response = userResourceEndpoint.listByFilterAndAttribute("userType eq Manager", null, userManager, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);

        response = userResourceEndpoint.listByFilterAndAttribute("userType eq Employee", null, userManager, "text/plain");
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);
    }

    @Test
    public void testListBySort() throws Exception {
    }

    @Test
    public void testListWithPagination() throws Exception {
    }

    @Test
    public void testList() throws Exception {

        //No data
        SCIMResponse response = userResourceEndpoint.list(userManager, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND);

        //Populate data
        String fileName1 = this.getClass().getResource("user3-with-attributes.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));

        response = userResourceEndpoint.list(null, format);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);

        response = userResourceEndpoint.list(userManager, "text/plain");
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);
    }

    @Test
    public void testUpdateWithPUT() throws Exception {
    }

    @Test
    public void testUpdateWithPATCH_Exceptions() throws Exception {
        String fileName1 = this.getClass().getResource("user1.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);

        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));
        String createdUserId = decodedJsonObj.getString("id");

        //Check for unsupported input/output formats
        response = userResourceEndpoint.updateWithPATCH(createdUserId, userString1, "text/plain", "text/plain", null);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);

        //Check with user-manager null
        response = userResourceEndpoint.updateWithPATCH(createdUserId, userString1, format, format, null);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
    }

    @Test(dataProvider = "createUpdateWithPATCH_Users")
    public void testUpdateWithPATCH(String fileName, int expectedCode) throws Exception {
        String fileName1 = this.getClass().getResource("user1.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        String fileName2 = this.getClass().getResource(fileName).getFile();
        String userString2 = new String(Files.readAllBytes(Paths.get(fileName2)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));
        String createdUserId = decodedJsonObj.getString("id");
        SCIMResponse response2 = userResourceEndpoint
                .updateWithPATCH(createdUserId, userString2, format, format, userManager);
        assertNotNull(response2);
        assertEquals(response2.getResponseCode(), expectedCode);
    }

    @Test
    public void testUpdateWithPUT_Exceptions() throws Exception {
        String fileName1 = this.getClass().getResource("user1.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);

        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));
        String createdUserId = decodedJsonObj.getString("id");

        //Check for unsupported input/output formats
        response = userResourceEndpoint.updateWithPUT(createdUserId, userString1, "text/plain", "text/plain", null);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_FORMAT_NOT_SUPPORTED);

        //Check with user-manager null
        response = userResourceEndpoint.updateWithPUT(createdUserId, userString1, format, format, null);
        assertNotNull(response);
        assertEquals(response.getResponseCode(), ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
    }

    @Test(dataProvider = "createUpdateWithPUT_Users")
    public void testUpdateWithPUT(String fileName, int expectedCode) throws Exception {
        String fileName1 = this.getClass().getResource("user1.json").getFile();
        String userString1 = new String(Files.readAllBytes(Paths.get(fileName1)), StandardCharsets.UTF_8);
        String fileName2 = this.getClass().getResource(fileName).getFile();
        String userString2 = new String(Files.readAllBytes(Paths.get(fileName2)), StandardCharsets.UTF_8);
        SCIMResponse response = userResourceEndpoint.create(userString1, format, format, userManager, false);
        String responseMessage = response.getResponseMessage();
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(responseMessage));
        String createdUserId = decodedJsonObj.getString("id");
        SCIMResponse response2 = userResourceEndpoint
                .updateWithPUT(createdUserId, userString2, format, format, userManager);
        assertNotNull(response2);
        assertEquals(response2.getResponseCode(), expectedCode);
    }

    @Test
    public void testCreateListedResource() throws Exception {
    }

    @DataProvider
    private static final Object[][] createUpdateWithPATCH_Users() {
        return new Object[][] { { "user2.json", ResponseCodeConstants.CODE_OK },
                { "user2-with-meta.json", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "user2-with-meta-attrib.json", ResponseCodeConstants.CODE_OK } };
    }

    @DataProvider
    private static final Object[][] createUpdateWithPUT_Users() {
        return new Object[][] { { "user2.json", ResponseCodeConstants.CODE_OK },
                { "user2-with-meta.json", ResponseCodeConstants.CODE_OK },
                { "user2-with-meta-attrib.json", ResponseCodeConstants.CODE_OK } };
    }

    @DataProvider
    private static final Object[][] listByFilterAndAttribute_Success() {
        return new Object[][] {
                { "userType eq Employee", null, 1 },
                { "userType eq \"Employee\"", null, 1 },
                { "userType eq Employee", "userType, firstName", 1 },
                { "userType eq Employee", "userType", 1 }
        };
    }

    @DataProvider
    private static final Object[][] listByFilterAndAttribute_Fail() {
        return new Object[][] { { "userType", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "userType lt Manager", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "badAttribute eq NoValue", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "userType Eq Manager Eq Employee", ResponseCodeConstants.CODE_BAD_REQUEST } };
    }

    @DataProvider
    private static final Object[][] listByFilterAndAttribute_Exceptions() {
        return new Object[][] { { "userType", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "userType lt Manager", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "badAttribute eq NoValue", ResponseCodeConstants.CODE_BAD_REQUEST },
                { "userType Eq Manager Eq Employee", ResponseCodeConstants.CODE_BAD_REQUEST } };
    }

}