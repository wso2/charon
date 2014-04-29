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
package org.wso2.charon.core.provisioning;

import java.util.Map;

/**
 * This is the interface for the provisioning handler or provisioning connector
 * which would perform provisioning of a particular operation asynchronously.
 * Therefore, this extends runnable.
 * Based on current design, this can be plugged in with a new UserOperationEventListener-to provision
 * user-mgt operations or can be plugged into SCIMUserManager-to provision operations when IS in dumb mode.
 * You can provide a list of provisioning handler implementations in provisioning-config.xml
 */
public interface ProvisioningHandler extends Runnable {

    /*Read provisioning related configuration*/

    public void initConfigManager();

    /*Perform provisioning*/

    public void provision();

    /*Set necessary data for provisioning, according to different impl of provisioning handlers*/

    public void setProperties(Map<String, Object> propertiesMap);

    /*Signal the provisioning handler about the object type to be provisioned*/

    public void setProvisioningObjectType(int objectType);

    /*Handover the object to be provisioned*/

    public void setProvisioningObject(Object object);

    /*Signal the provisioning handler about the provisioning method*/

    public void setProvisioningMethod(int provisioningMethod);

    /*Signal the provisioning handler about consumer name to pick the registered providers*/

    public void setProvisioningConsumer(String consumerName);

}