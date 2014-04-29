/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.schema;

import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;

public class SCIMEnterpriseUserSchemaExtension {
	
	private static final String ENTERPRISE_EXTENSION_URI = "urn:scim:schemas:extension:enterprise:1.0";
	private static final String ENTERPRISE_EXTENSION_NAME = "wso2Extension";
	private static final String ENTERPRISE_EXTENSION_DESC = "SCIM Enterprise User Schema Extension";
	
	private static final String MANAGER_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.manager";
	private static final String MANAGER_NAME = "manager";
	private static final String MANAGER_DESC = "The User's manager";
	
	private static final String EMPLOYEE_NUMBER_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.employeeNumber";
	private static final String EMPLOYEE_NUMBER_NAME = "employeeNumber";
	private static final String EMPLOYEE_NUMBER_DESC = "Numeric or alphanumeric identifier assigned to a person, typically based on order of hire or association with an organization";

	private static final String COST_CENTER_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.costCenter";
	private static final String COST_CENTER_NAME = "costCenter";
	private static final String COST_CENTER_DESC = "Identifies the name of a cost center";
	
	private static final String ORGANIZATION_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.organization";
	private static final String ORGANIZATION_NAME = "organization";
	private static final String ORGANIZATION_DESC = "Identifies the name of an organization";
	
	private static final String DIVISION_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.division";
	private static final String DIVISION_NAME = "division";
	private static final String DIVISION_DESC = "Identifies the name of a division";
	
	private static final String DEPARTMENT_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.department";
	private static final String DEPARTMENT_NAME = "department";
	private static final String DEPARTMENT_DESC = "Identifies the name of a department";
	
	private static final String MANAGER_ID_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.manager.managerId";
	private static final String MANAGER_ID_NAME = "managerId";
	private static final String MANAGER_ID_DESC = "The id of the SCIM resource representing the User's manager";
	
	private static final String $REF_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.manager.$ref";
	private static final String $REF_NAME = "$ref";
	private static final String $REF_DESC = "The URI of the SCIM resource representing the User's manager";
	
	private static final String DISPLAY_NAME_URI = "urn:scim:schemas:extension:enterprise:1.0:wso2Extension.manager.displayName";
	private static final String DISPLAY_NAME_NAME = "displayName";
	private static final String DISPLAY_NAME_DESC = "The displayName of the User's manager";

    
	// ================================= Start of Attribute Definitions =============================
    private static final SCIMAttributeSchema EMPLOYEE_NUMBER =
            SCIMAttributeSchema.createSCIMAttributeSchema(EMPLOYEE_NUMBER_URI,
                                                          EMPLOYEE_NUMBER_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, EMPLOYEE_NUMBER_DESC,
                                                          ENTERPRISE_EXTENSION_URI, true, true, true, null); 
    
    private static final SCIMAttributeSchema COST_CENTER =
            SCIMAttributeSchema.createSCIMAttributeSchema(COST_CENTER_URI,
                                                          COST_CENTER_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, COST_CENTER_DESC,
                                                          ENTERPRISE_EXTENSION_URI, true, true, true, null); 
    
    private static final SCIMAttributeSchema ORGANIZATION =
            SCIMAttributeSchema.createSCIMAttributeSchema(ORGANIZATION_URI,
                                                          ORGANIZATION_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, ORGANIZATION_DESC,
                                                          ENTERPRISE_EXTENSION_URI, true, true, true, null); 
    
    private static final SCIMAttributeSchema DIVISION =
            SCIMAttributeSchema.createSCIMAttributeSchema(DIVISION_URI,
                                                          DIVISION_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, DIVISION_DESC,
                                                          ENTERPRISE_EXTENSION_URI, true, true, true, null); 
    
    private static final SCIMAttributeSchema DEPARTMENT =
            SCIMAttributeSchema.createSCIMAttributeSchema(DEPARTMENT_URI,
                                                          DEPARTMENT_NAME,
                                                          SCIMSchemaDefinitions.DataType.STRING, false,
                                                          null, DEPARTMENT_DESC,
                                                          ENTERPRISE_EXTENSION_URI, true, true, true, null);
    
    private static final SCIMSubAttributeSchema MANAGER_ID =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(MANAGER_ID_URI,
                                                                MANAGER_ID_NAME,
                                                                DataType.STRING, MANAGER_ID_DESC,
                                                                false, false, false, null);
    
    private static final SCIMSubAttributeSchema $REF =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema($REF_URI,
                                                                $REF_NAME,
                                                                DataType.STRING, $REF_DESC,
                                                                false, false, false, null);
    

    private static final SCIMSubAttributeSchema DISPLAY_NAME =
            SCIMSubAttributeSchema.createSCIMSubAttributeSchema(DISPLAY_NAME_URI,
                                                                DISPLAY_NAME_NAME,
                                                                DataType.STRING, DISPLAY_NAME_DESC,
                                                                false, false, false, null);
    
    // ===================================== End of Attribute Definitions ======================
    
    
    /**
     * =========================== Complex Attribute having sub attributes ========================================= 
     */
    private static final SCIMAttributeSchema MANAGER =
            SCIMAttributeSchema.createSCIMAttributeSchema(MANAGER_URI,
                                                          MANAGER_NAME,
                                                          null, false, 
                                                          null, MANAGER_DESC,
                                                          ENTERPRISE_EXTENSION_URI, false, false, false,
                                                          MANAGER_ID, $REF, DISPLAY_NAME);
    
    /**
     * =========================== End of Complex Attribute having attributes 
     */
    
    public static final SCIMAttributeSchema ENTERPRISE_EXTENSION = 
    		SCIMAttributeSchema.createSCIMAttributeSchema(ENTERPRISE_EXTENSION_URI, 
    		                                              ENTERPRISE_EXTENSION_NAME,
    		                                              null,
    		                                              ENTERPRISE_EXTENSION_DESC,
    		                                              ENTERPRISE_EXTENSION_URI,false, false, false,
    		                                              EMPLOYEE_NUMBER, COST_CENTER, ORGANIZATION, DIVISION, DEPARTMENT, MANAGER
    		                                              );
    
}
