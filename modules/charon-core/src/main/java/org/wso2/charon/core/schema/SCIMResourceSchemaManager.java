/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.schema;

import org.wso2.charon.core.config.SCIMUserSchemaExtensionBuilder;

public class SCIMResourceSchemaManager {
	
	private static SCIMResourceSchemaManager manager = new SCIMResourceSchemaManager();
	
	public static SCIMResourceSchemaManager getInstance() {
		return manager;
	}
	
	private SCIMResourceSchemaManager() {
		
	}

	/**
	 * Return the SCIM User Resource Schema
	 * @return
	 */
	public SCIMResourceSchema getUserResourceSchema() {

		SCIMAttributeSchema schemaExtension =
		                                      SCIMUserSchemaExtensionBuilder.getInstance()
		                                                                    .getSCIMUserSchemaExtension();
		// extensions exist. 
		if (schemaExtension != null) {
			return SCIMResourceSchema.createSCIMResourceSchema(SCIMConstants.USER,
			                                                   SCIMConstants.CORE_SCHEMA_URI,
			                                                   SCIMConstants.USER_DESC,
			                                                   SCIMConstants.USER_ENDPOINT,
			                                                   SCIMSchemaDefinitions.USER_NAME,
			                                                   SCIMSchemaDefinitions.NAME,
			                                                   SCIMSchemaDefinitions.DISPLAY_NAME,
			                                                   SCIMSchemaDefinitions.NICK_NAME,
			                                                   SCIMSchemaDefinitions.PROFILE_URL,
			                                                   SCIMSchemaDefinitions.TITLE,
			                                                   SCIMSchemaDefinitions.USER_TYPE,
			                                                   SCIMSchemaDefinitions.PREFERRED_LANGUAGE,
			                                                   SCIMSchemaDefinitions.LOCALE,
			                                                   SCIMSchemaDefinitions.TIMEZONE,
			                                                   SCIMSchemaDefinitions.ACTIVE,
			                                                   SCIMSchemaDefinitions.PASSWORD,
			                                                   SCIMSchemaDefinitions.EMAILS,
			                                                   SCIMSchemaDefinitions.PHONE_NUMBERS,
			                                                   SCIMSchemaDefinitions.IMS,
			                                                   SCIMSchemaDefinitions.PHOTOS,
			                                                   SCIMSchemaDefinitions.ADDRESSES,
			                                                   SCIMSchemaDefinitions.GROUPS,
			                                                   SCIMSchemaDefinitions.ENTITLEMENTS,
			                                                   SCIMSchemaDefinitions.ROLES,
			                                                   SCIMSchemaDefinitions.X509CERTIFICATES,
			                                                   schemaExtension);
		} else {
			// returning the core schema
			return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
		}
	}

}
