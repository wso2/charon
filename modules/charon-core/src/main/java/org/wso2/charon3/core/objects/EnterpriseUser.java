package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.AbstractAttribute;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import static org.wso2.charon3.core.attributes.DefaultAttributeFactory.createAttribute;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;

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

    public void setEmployeeNumber(String employeeNumber) {
        setExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.EMPLOYEE_NUMBER, employeeNumber);
    }

    /**
     * @return the enterprise cost center
     */
    public String getCostCenter() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.COST_CENTER);
    }

    public void setCostCenter(String costCenter) {
        setExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.COST_CENTER, costCenter);
    }

    /**
     * @return the enterprise organization
     */
    public String getOrganization() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.ORGANIZATION);
    }

    public void setOrganization(String organization) {
        setExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.ORGANIZATION, organization);
    }

    /**
     * @return the enterprise division
     */
    public String getDivision() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DIVISION);
    }

    public void setDivision(String division) {
        setExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DIVISION, division);
    }

    /**
     * @return the enterprise employee number
     */
    public String getDepartment() {
        return getExtensionAttributeAsString(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DEPARTMENT);
    }

    public void setDepartment(String department) {
        setExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DEPARTMENT, department);
    }

    /**
     * @return the enterprise employee number
     */
    public MultiValuedComplexType getManager() {
        return getExtensionAttributeAsComplexType(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.VALUE,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DISPLAY_NAME, null, null,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.REF);
    }

    public void setManagerValue(String managerValue) {
        setManagerAttribute(SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.VALUE, managerValue);
    }

    public void setManagerDisplayname(String managerDisplayname) {
        setManagerAttribute(SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.DISPLAY_NAME, managerDisplayname);
    }

    public void setManagerReference(String managerReference) {
        setManagerAttribute(SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.REF, managerReference);
    }

    /**
     * sets an attribute within the manager complex type of the enterprise user
     * @param ref the attribute schema definition
     * @param value the value to set into the attribute
     */
    private void setManagerAttribute(SCIMAttributeSchema ref, String value) {
        ComplexAttribute extension = getOrCreateExtensionAttribute(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA);
        ComplexAttribute manager = getComplexAttributeFromExtension(SCIMSchemaDefinitions.SCIM_ENTERPRISE_USER_SCHEMA,
            SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER);
        if (manager == null) {
            manager = new ComplexAttribute(SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER.getName());
            rethrowConsumer(o -> createAttribute(SCIMSchemaDefinitions.SCIMEnterpriseUserSchemaDefinition.MANAGER,
                (AbstractAttribute) o)).accept(manager);
            rethrowConsumer(o -> extension.setSubAttribute((Attribute) o)).accept(manager);
        }
        getSetSubAttributeConsumer(manager).accept(ref, () -> value);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSCIMObject getResource() {
        return enterpriseUser;
    }
}
