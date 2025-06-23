/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.schema.SCIMConstants;

/**
 * Represents the Agent object which extends the User object with agent-specific
 * functionality.
 * An Agent is essentially a specialized User that represents agentic AI or
 * automated entities
 * that can perform actions on behalf of the system or other users.
 */
public class Agent extends User {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for Agent.
     * Initializes an Agent object with the agent schema.
     */
    public Agent() {

        super();
    }

    /**
     * Sets the schemas of the agent.
     * This includes both user schema and agent-specific schema.
     */
    @Override
    public void setSchemas() {
        // First set the user schemas
        super.setSchemas();

        // Add agent-specific schema
        setSchema(SCIMConstants.AGENT_SCHEMA_URI);
    }

    /**
     * Sets the schemas of the agent using the provided UserManager.
     * This includes both user schema and agent-specific schema.
     *
     * @param userManager The UserManager instance to retrieve schema information
     * @throws BadRequestException     If the request is invalid
     * @throws NotImplementedException If the operation is not implemented
     * @throws CharonException         If there's an error in Charon processing
     */
    @Override
    public void setSchemas(UserManager userManager) throws BadRequestException,
            NotImplementedException, CharonException {
        // First set the user schemas
        super.setSchemas(userManager);

        // Add agent-specific schema
        setSchema(SCIMConstants.AGENT_SCHEMA_URI);
    }
}
