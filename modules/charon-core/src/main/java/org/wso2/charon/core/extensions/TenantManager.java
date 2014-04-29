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
package org.wso2.charon.core.extensions;

import org.wso2.charon.core.exceptions.CharonException;

public interface TenantManager {

    /**
     * Create the tenant, given the tenant info.
     *
     * @param tenantInfo
     * @throws CharonException
     */
    public void createTenant(TenantDTO tenantInfo) throws CharonException;

    /**
     * Identify the tenantID given the tenant domain name.
     *
     * @param tenantDomainName
     * @return
     */
    //public int getTenantID(String tenantDomainName) throws CharonException;

    /**
     * Retrieve the corresponding tenant given the tenant admin's username
     *
     * @param fullyQualifiedUserName
     * @return
     * @throws CharonException
     */
    public int getTenantID(String fullyQualifiedUserName) throws CharonException;

    /**
     * Retrieve the tenant domain name given the tenant admin user name.
     *
     * @param fullyQualifiedUserName
     * @return
     * @throws CharonException
     */
    public String getTenantDomain(String fullyQualifiedUserName) throws CharonException;

    /**
     * Get the Tenant Info, given the tenantID. 
     * @param tenantID
     * @return
     * @throws CharonException
     */
    public TenantDTO getTenantInfo(int tenantID) throws CharonException;
}

