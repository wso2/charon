/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.charon3.impl.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.impl.provider.resources.GroupResource;
import org.wso2.charon3.impl.provider.resources.UserResource;
import org.wso2.msf4j.MicroservicesRunner;

/**
 * This performs one-time initialization tasks at the application startup.
 */
public class ApplicationInitializer {

    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    public static void main(String[] args) {
        logger.info("SCIM micro service is starting up.....");

        new MicroservicesRunner().deploy(new UserResource(), new GroupResource()).start();

        logger.info("SCIM micro service is successfully started.");
    }

}
