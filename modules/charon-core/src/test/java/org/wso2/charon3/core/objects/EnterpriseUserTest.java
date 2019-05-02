package org.wso2.charon3.core.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;

/**
 * .
 * <br><br>
 * created at: 13.04.2019
 * @author Pascal Knüppel
 */
class EnterpriseUserTest implements FileReferences {

    /**
     * tests that the setter methods for the enterprise user are working if the user has no attributes.
     */
    @Test
    public void testSetValuesOfEnterpriseUser() {
        User user = new User();
        user.replaceUsername("donkey_kong");
        EnterpriseUser enterpriseUser = new EnterpriseUser(user);
        final String organization = "some organization";
        final String department = "some department";
        final String costCenter = "some cost center";
        final String division = "some division";
        final String employeeNumber = "123456";
        final String managerValue = "987564";
        final String managerDisplayname = "Donkey Kong";
        final String managerReference = "some reference";

        enterpriseUser.setOrganization(organization);
        enterpriseUser.setDepartment(department);
        enterpriseUser.setCostCenter(costCenter);
        enterpriseUser.setDivision(division);
        enterpriseUser.setEmployeeNumber(employeeNumber);
        enterpriseUser.setManagerValue(managerValue);
        enterpriseUser.setManagerDisplayname(managerDisplayname);
        enterpriseUser.setManagerReference(managerReference);

        Assertions.assertEquals(organization, enterpriseUser.getOrganization());
        Assertions.assertEquals(department, enterpriseUser.getDepartment());
        Assertions.assertEquals(costCenter, enterpriseUser.getCostCenter());
        Assertions.assertEquals(division, enterpriseUser.getDivision());
        Assertions.assertEquals(employeeNumber, enterpriseUser.getEmployeeNumber());
        Assertions.assertNotNull(enterpriseUser.getManager());
        Assertions.assertEquals(managerValue, enterpriseUser.getManager().getValue());
        Assertions.assertEquals(managerDisplayname, enterpriseUser.getManager().getDisplay());
        Assertions.assertEquals(managerReference, enterpriseUser.getManager().getReference());
    }

    /**
     * tests that the enterprise user setter methods are still working if the enterprise schema extension has already
     * all values set.
     */
    @Test
    public void testOverrideValuesOfEnterpriseUser()
        throws InternalErrorException, BadRequestException, CharonException {
        final String organization = "some organization";
        final String department = "some department";
        final String costCenter = "some cost center";
        final String division = "some division";
        final String employeeNumber = "123456";
        final String managerValue = "987564";
        final String managerDisplayname = "Donkey Kong";
        final String managerReference = "some reference";

        User user = JSON_DECODER.decodeResource(readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE),
            SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        EnterpriseUser enterpriseUser = new EnterpriseUser(user);

        Assertions.assertNotEquals(organization, enterpriseUser.getOrganization());
        Assertions.assertNotEquals(department, enterpriseUser.getDepartment());
        Assertions.assertNotEquals(costCenter, enterpriseUser.getCostCenter());
        Assertions.assertNotEquals(division, enterpriseUser.getDivision());
        Assertions.assertNotEquals(employeeNumber, enterpriseUser.getEmployeeNumber());
        Assertions.assertNotNull(enterpriseUser.getManager());
        Assertions.assertNotEquals(managerValue, enterpriseUser.getManager().getValue());
        Assertions.assertNotEquals(managerDisplayname, enterpriseUser.getManager().getDisplay());
        Assertions.assertNotEquals(managerReference, enterpriseUser.getManager().getReference());

        enterpriseUser.setOrganization(organization);
        enterpriseUser.setDepartment(department);
        enterpriseUser.setCostCenter(costCenter);
        enterpriseUser.setDivision(division);
        enterpriseUser.setEmployeeNumber(employeeNumber);
        enterpriseUser.setManagerValue(managerValue);
        enterpriseUser.setManagerDisplayname(managerDisplayname);
        enterpriseUser.setManagerReference(managerReference);

        Assertions.assertEquals(organization, enterpriseUser.getOrganization());
        Assertions.assertEquals(department, enterpriseUser.getDepartment());
        Assertions.assertEquals(costCenter, enterpriseUser.getCostCenter());
        Assertions.assertEquals(division, enterpriseUser.getDivision());
        Assertions.assertEquals(employeeNumber, enterpriseUser.getEmployeeNumber());
        Assertions.assertNotNull(enterpriseUser.getManager());
        Assertions.assertEquals(managerValue, enterpriseUser.getManager().getValue());
        Assertions.assertEquals(managerDisplayname, enterpriseUser.getManager().getDisplay());
        Assertions.assertEquals(managerReference, enterpriseUser.getManager().getReference());
    }

    /**
     * this test will make sure that the enterprise extension is not added to the user resource if the values are empty.
     */
    @Test
    public void testThatExtensionIsNotSerializedByEmptyValues() throws CharonException {
        User user = new User();
        user.replaceUsername("donkey_kong");

        EnterpriseUser enterpriseUser = new EnterpriseUser(user);
        enterpriseUser.setOrganization(null);
        enterpriseUser.setManagerValue(null);

        String encodedUser = JSON_ENCODER.encodeSCIMObject(user);
        Assertions.assertFalse(
            encodedUser.contains("\"" + SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA.getSchema() + "\""),
            encodedUser);
    }

}
