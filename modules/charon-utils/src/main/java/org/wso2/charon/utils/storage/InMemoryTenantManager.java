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
package org.wso2.charon.utils.storage;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.extensions.TenantDTO;
import org.wso2.charon.core.extensions.TenantManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In memroy tenant manager implementation for demo purpose only.
 */
public class InMemoryTenantManager implements TenantManager {

    private static Map<Integer, TenantInfo> tenantsList = new ConcurrentHashMap<Integer, TenantInfo>();

    public void createTenant(TenantDTO tenantDTO) throws CharonException {
        if (tenantsList.size() != 0) {
            for (Map.Entry<Integer, TenantInfo> entry : tenantsList.entrySet()) {
                if (tenantDTO.getTenantDomain().equals(entry.getValue().getTenantDomain())) {
                    String error = "Tenant with the same domain name already exists.";
                    throw new CharonException(error);
                }
            }
            tenantsList.put(tenantsList.size(), (TenantInfo) tenantDTO);

        } else {
            tenantsList.put(0, (TenantInfo) tenantDTO);
        }
    }

    /**
     * Retrieve the corresponding tenant given the tenant admin's username.
     *
     * @param fullyQualifiedUserName
     * @return
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    @Override
    public int getTenantID(String fullyQualifiedUserName) throws CharonException {
        return this.getTenantIDFromDomain(getTenantDomain(fullyQualifiedUserName));
    }

    /**
     * Retrieve the tenant domain name given the tenant admin user name.
     *
     * @param fullyQualifiedUserName
     * @return
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    @Override
    public String getTenantDomain(String fullyQualifiedUserName) throws CharonException {
        return fullyQualifiedUserName.split("@")[1];
    }

    /**
     * Get the Tenant Info, given the tenantID.
     *
     * @param tenantID
     * @return
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    @Override
    public TenantDTO getTenantInfo(int tenantID) throws CharonException {
        if (tenantsList.containsKey(tenantID)) {
            return tenantsList.get(tenantID);
        } else {
            String error = "Requested tenant id does not exist.";
            throw new CharonException(error);
        }
    }

    /**
     * Identify the tenantID given the tenant domain name.
     *
     * @param tenantDomainName
     * @return
     */

    private int getTenantIDFromDomain(String tenantDomainName) throws CharonException {
        if (tenantsList.size() != 0) {
            for (Map.Entry<Integer, TenantInfo> entry : tenantsList.entrySet()) {
                if (tenantDomainName.equals(entry.getValue().getTenantDomain())) {
                    return entry.getKey();
                }
            }
        }
        String error = "No tenant registered with given domain name";
        throw new CharonException(error);
    }

}
