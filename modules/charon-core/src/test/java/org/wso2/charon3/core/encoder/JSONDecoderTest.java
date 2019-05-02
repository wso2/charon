package org.wso2.charon3.core.encoder;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.EnterpriseUser;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

import java.util.List;

/**
 * @author Pascal Knueppel
 */
public class JSONDecoderTest implements FileReferences {

    /**
     * this test will verify that the decoding of resources does work.
     */
    @Test
    public void testDecodeGroup() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_GROUP_BREMEN_FILE);
        Group group = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
        Assertions.assertEquals(1, group.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.GROUP_CORE_SCHEMA_URI, group.getSchemaList().get(0));
        Assertions.assertEquals("Bremen", group.getDisplayName());
        Assertions.assertEquals("123456", group.getExternalId());
        Assertions.assertNull(group.getId());
    }

    /**
     * this test will verify that the decoding of resources does work.
     */
    @ParameterizedTest
    @ValueSource(strings = {CREATE_USER_MAXILEIN_FILE, CREATE_ENTERPRISE_USER_MAXILEIN_FILE})
    public void testDecodeUser(String fileResource)
            throws InternalErrorException, BadRequestException, CharonException {
        // first remove extension schema from user schema in order for the other tests to work the schema must be
        // added again at the end of this test
        SCIMSchemaDefinitions.SCIM_USER_SCHEMA.getExtensions().clear();
        try {
            testUserDecoding(fileResource);
        } finally {
            SCIMSchemaDefinitions.SCIM_USER_SCHEMA.getExtensions()
                    .add(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA);
        }
    }

    /**
     * does the checks for {@link #testDecodeUser(String)}.
     */
    private void testUserDecoding(String fileResource)
            throws BadRequestException, CharonException, InternalErrorException {
        String groupJson = readResourceFile(fileResource);
        User user = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        Assertions.assertEquals(1, user.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.USER_CORE_SCHEMA_URI, user.getSchemaList().get(0));

        Assertions.assertEquals("1a7970d4-f1b3-4836-876e-7bda927def2d", user.getId());
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
        Assertions.assertNull(phoneNumber.getDisplay());
        Assertions.assertFalse(phoneNumber.isPrimary());

        Assertions.assertNotNull(user.getInstantMessagingAddresses());
        Assertions.assertEquals(1, user.getInstantMessagingAddresses().size());
        MultiValuedComplexType ims = user.getInstantMessagingAddresses().get(0);
        Assertions.assertEquals("some_ims_address", ims.getValue());
        Assertions.assertEquals("aim", ims.getType());
        Assertions.assertNull(ims.getDisplay());
        Assertions.assertFalse(ims.isPrimary());

        Assertions.assertNotNull(user.getGroups());
        Assertions.assertEquals(3, user.getGroups().size());
        List<MultiValuedComplexType> groups = user.getGroups();
        MultiValuedComplexType group1 = groups.get(0);
        Assertions.assertEquals("e9e30dba-f08f-4109-8486-d5c6a331660a", group1.getValue());
        Assertions.assertEquals("Tour Guides", group1.getDisplay());
        Assertions.assertEquals("https://example.com/v2/Groups/e9e30dba-f08f-4109-8486-d5c6a331660a",
                group1.getReference());
        Assertions.assertNull(group1.getType());
        Assertions.assertFalse(group1.isPrimary());


        MultiValuedComplexType group2 = groups.get(1);
        Assertions.assertEquals("fc348aa8-3835-40eb-a20b-c726e15c55b5", group2.getValue());
        Assertions.assertEquals("Employees", group2.getDisplay());
        Assertions.assertEquals("https://example.com/v2/Groups/fc348aa8-3835-40eb-a20b-c726e15c55b5",
                group2.getReference());
        Assertions.assertNull(group2.getType());
        Assertions.assertFalse(group2.isPrimary());

        MultiValuedComplexType group3 = groups.get(2);
        Assertions.assertEquals("71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7", group3.getValue());
        Assertions.assertEquals("US Employees", group3.getDisplay());
        Assertions.assertEquals("https://example.com/v2/Groups/71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7",
                group3.getReference());
        Assertions.assertNull(group3.getType());
        Assertions.assertFalse(group3.isPrimary());

        Assertions.assertNotNull(user.getX509Certificates());
        Assertions.assertEquals(2, user.getX509Certificates().size());
        user.getX509Certificates().forEach(multiValued -> Assertions.assertNotNull(multiValued.getValue()));
    }

    /**
     * this method will show that the enterprise user is resolved correctly if enterprise details are found within
     * the user resource representation.
     */
    @Test
    public void testResolveEnterpriseUser() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        Assertions.assertEquals(2, user.getSchemaList().size());
        Assertions.assertEquals(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI, user.getSchemaList().get(0));

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
}
