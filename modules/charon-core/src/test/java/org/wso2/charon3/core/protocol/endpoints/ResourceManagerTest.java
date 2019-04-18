package org.wso2.charon3.core.protocol.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.config.FilterFeature;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.objects.EnterpriseUser;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.setup.CharonInitializer;
import org.wso2.charon3.core.testsetup.FileReferences;

/**
 *
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
class ResourceManagerTest extends CharonInitializer implements FileReferences {


    private static final Logger log = LoggerFactory.getLogger(ResourceManagerTest.class);

    /**
     * will show that a user can successfully be created and that the methods of the
     * {@link org.wso2.charon3.core.extensions.ResourceHandler}s are called correctly
     */
    @Test
    public void testCreateUser() throws AbstractCharonException {
        String enterpriseUserString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        SCIMResponse scimResponse = userManager.create(enterpriseUserString, null, null);

        Mockito.verify(userResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());

        log.warn(scimResponse.getResponseMessage());
        Assertions.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseStatus());
        User user = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
                                                SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                new User());
        checkEnterpriseUservalues(user);
        Assertions.assertEquals(2, user.getSchemaList().size());

        Assertions.assertEquals("abcdefghijklmnopqrstuvwxyz", user.getExternalId());
        Assertions.assertEquals("maxilein", user.getUsername());
        Assertions.assertTrue(user.getActive());
        Assertions.assertNotNull(user.getName());
        Assertions.assertEquals("Dr. Hc. Max der Mittlere Mustermann, III", user.getName().getFormatted());
        Assertions.assertEquals("Max", user.getName().getGivenName());
        Assertions.assertEquals("Mustermann", user.getName().getFamilyName());
        Assertions.assertEquals("der Mittlere", user.getName().getMiddleName());
        Assertions.assertEquals("Dr. Hc.", user.getName().getHonorificPrefix());
        Assertions.assertEquals("III", user.getName().getHonorificSuffix());
        Assertions.assertEquals("myPwd", user.getPassword());
        Assertions.assertEquals("Max Muster", user.getDisplayName());
        Assertions.assertEquals("Maxi", user.getNickName());
        Assertions.assertEquals("Employee", user.getUserType());
        Assertions.assertEquals("en-US", user.getPreferredLanguage());
        Assertions.assertEquals("America/Los_Angeles", user.getTimezone());
        Assertions.assertEquals("Tour Guide", user.getTitle());
        Assertions.assertEquals("de-DE", user.getLocale());
        Assertions.assertEquals("https://localhost/profile/path", user.getProfileUrl());

        ScimAddress scimAddress = user.getAddresses().get(0);
        Assertions.assertNotNull(user.getAddresses());
        Assertions.assertEquals(1, user.getAddresses().size());
        Assertions.assertEquals("work", scimAddress.getType());
        Assertions.assertEquals("Am Hochschulring 4", scimAddress.getStreetAddress());
        Assertions.assertEquals("Bremen", scimAddress.getRegion());
        Assertions.assertEquals("Bremen", scimAddress.getLocality());
        Assertions.assertEquals("28359", scimAddress.getPostalCode());
        Assertions.assertEquals("DE", scimAddress.getCountry());
        Assertions.assertEquals("Am Fallturm 9\nBremen, Bremen 28239 DE", scimAddress.getFormatted());
        Assertions.assertTrue(scimAddress.isPrimary());

        Assertions.assertNotNull(user.getPhoneNumbers());
        Assertions.assertEquals(1, user.getPhoneNumbers().size());
        MultiValuedComplexType phoneNumber = user.getPhoneNumbers().get(0);
        Assertions.assertEquals("555-555-5555", phoneNumber.getValue());
        Assertions.assertEquals("work", phoneNumber.getType());
        Assertions.assertEquals("555-555-5555", phoneNumber.getDisplay());
        Assertions.assertFalse(phoneNumber.isPrimary());

        Assertions.assertNotNull(user.getInstantMessagingAddresses());
        Assertions.assertEquals(1, user.getInstantMessagingAddresses().size());
        MultiValuedComplexType ims = user.getInstantMessagingAddresses().get(0);
        Assertions.assertEquals("some_ims_address", ims.getValue());
        Assertions.assertEquals("aim", ims.getType());
        Assertions.assertEquals("some_ims_address", ims.getDisplay());
        Assertions.assertFalse(ims.isPrimary());

        Assertions.assertNotNull(user.getGroups());
        Assertions.assertEquals(0, user.getGroups().size());

        Assertions.assertNotNull(user.getX509Certificates());
        Assertions.assertEquals(2, user.getX509Certificates().size());
        user.getX509Certificates().forEach(multiValued -> Assertions.assertNotNull(multiValued.getValue()));
    }

    private void checkEnterpriseUservalues(User user) {
        EnterpriseUser enterpriseUser = new EnterpriseUser(user);
        Assertions.assertEquals("701984", enterpriseUser.getEmployeeNumber());
        Assertions.assertEquals("4130", enterpriseUser.getCostCenter());
        Assertions.assertEquals("Universal Studios", enterpriseUser.getOrganization());
        Assertions.assertEquals("Theme Park", enterpriseUser.getDivision());
        Assertions.assertEquals("Tour Operations", enterpriseUser.getDepartment());
        MultiValuedComplexType manager = enterpriseUser.getManager();
        Assertions.assertNotNull(manager);
        Assertions.assertEquals("26118915-6090-4610-87e4-49d8ca9f808d", manager.getValue());
        Assertions.assertEquals("John Smith", manager.getDisplay());
        Assertions.assertEquals("../Users/26118915-6090-4610-87e4-49d8ca9f808d", manager.getReference());
    }

    @Test
    public void testUpdateUser() throws AbstractCharonException {
        String enterpriseUserString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        SCIMResponse scimResponse = userManager.create(enterpriseUserString, null, null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseStatus());
        Mockito.verify(userResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());
        User user = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
                                                SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                new User());

        final String nickNameBefore = "Maxi";
        final String nickNameUpdate = "MaxMan";
        enterpriseUserString = enterpriseUserString.replace("\"" + nickNameBefore + "\"", "\"" + nickNameUpdate + "\"");
        scimResponse = userManager.updateWithPUT(user.getId(), enterpriseUserString, null, null);
        Mockito.verify(userResourceHandler, Mockito.times(1)).update(Mockito.any(), Mockito.any());
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());

        log.warn(scimResponse.getResponseMessage());

        user = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
                                           SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                           new User());
        Assertions.assertEquals(nickNameUpdate, user.getNickName());
    }

    @Test
    public void testDeleteUser() throws AbstractCharonException {
        String enterpriseUserString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        SCIMResponse scimResponse = userManager.create(enterpriseUserString, null, null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseStatus());
        Mockito.verify(userResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());
        User user = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
                                                SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                new User());

        userManager.delete(user.getId());
        Mockito.verify(userResourceHandler, Mockito.times(1)).delete(Mockito.eq(user.getId()));
    }

    @Test
    public void testGetSingleUser() throws AbstractCharonException {
        String enterpriseUserString = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        SCIMResponse scimResponse = userManager.create(enterpriseUserString, null, null);
        Assertions.assertEquals(ResponseCodeConstants.CODE_CREATED, scimResponse.getResponseStatus());
        Mockito.verify(userResourceHandler, Mockito.times(1)).create(Mockito.any(), Mockito.any());
        User user = JSON_DECODER.decodeResource(scimResponse.getResponseMessage(),
                                                SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                                                new User());

        scimResponse = userManager.get(user.getId(), null, null);
        Mockito.verify(userResourceHandler, Mockito.times(1)).get(Mockito.eq(user.getId()), Mockito.any());
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1 ,3 ,5, 50})
    public void testListUsersWithGet(int numberOfResults) throws AbstractCharonException {
        CharonConfiguration.getInstance().setFilter(new FilterFeature(true, 50));
        SCIMResponse scimResponse = userManager.listWithGET(null, null, numberOfResults, null, null, null, null, null);
        Mockito.verify(userResourceHandler, Mockito.times(1)).listResources(Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.eq(numberOfResults),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any());
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());
    }

    /**
     * this test will assure that the count value is reduced to the maximum number of results that was setup in the
     * service provider configuration if the client defined a value that is greater than the maximum number of results
     * @throws AbstractCharonException
     */
    @ParameterizedTest
    @ValueSource(ints = {5, 8, 13, 21, 50})
    public void testMaximumResults(int count) throws AbstractCharonException {
        final int maxNumberOfResults = 1;
        CharonConfiguration.getInstance().setFilter(new FilterFeature(true, maxNumberOfResults));
        SCIMResponse scimResponse = userManager.listWithGET(null, null, count, null, null, null, null,
            null);
        Mockito.verify(userResourceHandler, Mockito.times(1)).listResources(Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.eq(maxNumberOfResults),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any());
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());
    }

    @Test
    public void testListUsersWithPost() throws AbstractCharonException {
        final int startIndex = 1;
        final int count = CharonConfiguration.getInstance().getFilter().getMaxResults();
        String searchRequestString = readResourceFile(SEARCH_REQUEST_FILE);
        SCIMResponse scimResponse = userManager.listWithPOST(searchRequestString);
        Mockito.verify(userResourceHandler, Mockito.times(1)).listResources(Mockito.any(),
                                                                            Mockito.eq(startIndex),
                                                                            Mockito.eq(count),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any(),
                                                                            Mockito.any());
        Assertions.assertEquals(ResponseCodeConstants.CODE_OK, scimResponse.getResponseStatus());
    }
}
