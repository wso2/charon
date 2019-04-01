package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

/**
 * author Pascal Knueppel <br>
 * created at: 18.03.2019 - 16:15 <br>
 * <br>
 * this class serves as a wrapper class for user that helps reading the enterprise user attributes
 */
public class EnterpriseUser extends ScimAttributeAware {

    /**
     * a user object that might hold an enterprise extension
     */
    private User enterpriseUser;

    public EnterpriseUser(User enterpriseUser) {
        this.enterpriseUser = enterpriseUser;
    }

    /**
     * @return the enterprise employee number
     */
    public String getEmployeeNumber() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.EMPLOYEE_NUMBER);
    }

    /**
     * @return the enterprise cost center
     */
    public String getCostCenter() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.COST_CENTER);
    }

    /**
     * @return the enterprise organization
     */
    public String getOrganization() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.ORGANIZATION);
    }

    /**
     * @return the enterprise division
     */
    public String getDivision() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DIVISION);
    }

    /**
     * @return the enterprise employee number
     */
    public String getDepartment() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DEPARTMENT);
    }

    /**
     * @return the enterprise employee number
     */
    public MultiValuedComplexType getManager() {
        return getExtensionAttributeAsComplexType(
                SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.VALUE,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DISPLAY_NAME,
                null,
                null,
                SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.REF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSCIMObject getResource() {
        return enterpriseUser;
    }
}
