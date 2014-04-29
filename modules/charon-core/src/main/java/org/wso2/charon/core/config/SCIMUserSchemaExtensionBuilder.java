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
package org.wso2.charon.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;
import org.wso2.charon.core.schema.SCIMSubAttributeSchema;

public class SCIMUserSchemaExtensionBuilder {

	private static SCIMUserSchemaExtensionBuilder configReader = new SCIMUserSchemaExtensionBuilder();
	// configuration map
	private static Map<String, ExtensionAttributeSchemaConfig> extensionConfig =
	                                                                             new HashMap<String, ExtensionAttributeSchemaConfig>();
	// built schema map
	private static Map<String, SCIMAttributeSchema> attributeSchemas =
	                                                                   new HashMap<String, SCIMAttributeSchema>();
	// extension root attribute name
	String extensionRootAttributeName = null;
	// extension root attribute schema
	private static SCIMAttributeSchema extensionSchema = null;

	public static SCIMUserSchemaExtensionBuilder getInstance() {
		return configReader;
	}

	/**
	 * This method reads configuration file and stores in the memory as an
	 * configuration map
	 * 
	 * @param configFilePath
	 * @throws CharonException
	 */
	private void readConfiguration(String configFilePath) throws CharonException {
		File provisioningConfig = new File(configFilePath);
		try {
			InputStream inputStream = new FileInputStream(provisioningConfig);
			java.util.Scanner scaner = new java.util.Scanner(inputStream).useDelimiter("\\A");
			String jsonString = scaner.hasNext() ? scaner.next() : "";
			
			JSONArray attributeConfigArray = new JSONArray(jsonString);

			for (int index = 0; index < attributeConfigArray.length(); ++index) {
				JSONObject attributeConfig = attributeConfigArray.getJSONObject(index);
				ExtensionAttributeSchemaConfig attrubteConfig =
				                                                new ExtensionAttributeSchemaConfig(
				                                                                                   attributeConfig);
				extensionConfig.put(attrubteConfig.getAttributeName(), attrubteConfig);

				/**
				 * NOTE: Assume last config is the root config
				 */
				if (index == attributeConfigArray.length() - 1) {
					extensionRootAttributeName = attrubteConfig.getAttributeName();
				}
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			throw new CharonException(SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file not found!",
			                          e);
		} catch (JSONException e) {
			throw new CharonException("Error while parsing " +
			                          SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
		} catch (IOException e) {
			throw new CharonException("Error while closing " +
			                          SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!", e);
		}
	}

	/**
	 * Return the built schema
	 * 
	 * @return
	 */
	public SCIMAttributeSchema getSCIMUserSchemaExtension() {
		return extensionSchema;
	}

	/**
	 * Logic goes here
	 * @throws CharonException 
	 */
	public void buildUserSchemaExtension(String configFilePath) throws CharonException {
		
		readConfiguration(configFilePath);
		
		for (Map.Entry<String, ExtensionAttributeSchemaConfig> attributeSchemaConfig : extensionConfig.entrySet()) {
			// if there are no children its a simple attribute, build it
			if (!attributeSchemaConfig.getValue().hasChildren()) {
				buildSimpleAttributeSchema(attributeSchemaConfig.getValue());
			} else {
				// need to build child schemas first
				buildComplexAttributeSchema(attributeSchemaConfig.getValue());
			}
		}
		// now get the extension schema
		/**
		 * Assumption : Final config in the configuration file is the extension
		 * root attribute
		 */
		extensionSchema = attributeSchemas.get(extensionRootAttributeName);
	}

	/**
	 * Knows how to build a complex attribute
	 * 
	 * @param config
	 */
	private void buildComplexAttributeSchema(ExtensionAttributeSchemaConfig config) {
		if (!attributeSchemas.containsKey(config.getAttributeName())) {
			String[] subAttributes = config.getSubAttributes();
			for (String subAttribute : subAttributes) {
				ExtensionAttributeSchemaConfig subAttribConfig = extensionConfig.get(subAttribute);
				if (!subAttribConfig.hasChildren()) {
					buildSimpleAttributeSchema(subAttribConfig);
				} else {
					// need to build child schemas first
					buildComplexAttributeSchema(subAttribConfig);
				}
			}
			// now all sub attributes must be already built
			buildComplexSchema(config);
		}
	}

	/**
	 * Has the logic to iterate through child attributes
	 * 
	 * @param config
	 */
	private void buildComplexSchema(ExtensionAttributeSchemaConfig config) {
		String[] subAttributeNames = config.getSubAttributes();
		SCIMAttributeSchema[] subAttributes = new SCIMAttributeSchema[subAttributeNames.length];
		int i = 0;
		for (String subAttributeName : subAttributeNames) {
			subAttributes[i] = attributeSchemas.get(subAttributeName);
			i++;
		}
		SCIMAttributeSchema complexAttribute =
		                                       SCIMAttributeSchema.createSCIMAttributeSchema(config.getAttributeURI(),
		                                                                                     config.getAttributeName(),
		                                                                                     config.getDataType(),
		                                                                                     config.getDescription(),
		                                                                                     config.getSchemaURI(),
		                                                                                     config.isReadOnly(),
		                                                                                     config.isRequired(),
		                                                                                     config.isCaseExact(),
		                                                                                     subAttributes);
		attributeSchemas.put(config.getAttributeName(), complexAttribute);
	}

	/**
	 * Builds simple attribute schema
	 * 
	 * @param config
	 */
	private void buildSimpleAttributeSchema(ExtensionAttributeSchemaConfig config) {
		if (!attributeSchemas.containsKey(config.getAttributeName())) {
			SCIMSubAttributeSchema[] subAttribs = null;
			SCIMAttributeSchema attributeSchema =
			                                      SCIMAttributeSchema.createSCIMAttributeSchema(config.getAttributeURI(),
			                                                                                    config.getAttributeName(),
			                                                                                    config.getDataType(),
			                                                                                    config.isMultiValued(),
			                                                                                    config.getMultiValuedAttributeChildName(),
			                                                                                    config.getDescription(),
			                                                                                    config.getSchemaURI(),
			                                                                                    config.isReadOnly(),
			                                                                                    config.isRequired(),
			                                                                                    config.isCaseExact(),
			                                                                                    subAttribs);
			attributeSchemas.put(config.getAttributeName(), attributeSchema);
		}

	}

	/**
	 * This class holds an attribute configuration elements in the configuration
	 * file
	 * 
	 */
	class ExtensionAttributeSchemaConfig {

		private String attributeURI = null;
		private String attributeName = null;
		private String dataType = null;
		private boolean isMultiValued = false;
		private String multiValuedAttributeChildName = null;
		private String description = null;
		private String schemaURI = null;
		private boolean isReadOnly = false;
		private boolean isRequired = false;
		private boolean isCaseExact = false;
		private String[] subAttributes = null;

		public ExtensionAttributeSchemaConfig(JSONObject attributeConfigJSON) throws CharonException {
			try {
				attributeURI = attributeConfigJSON.getString("attributeURI");
				attributeName = attributeConfigJSON.getString("attributeName");
				dataType = attributeConfigJSON.getString("dataType");
				isMultiValued = attributeConfigJSON.getBoolean("multiValued");
				multiValuedAttributeChildName =
				                                attributeConfigJSON.getString("multiValuedAttributeChildName");
				description = attributeConfigJSON.getString("description");
				schemaURI = attributeConfigJSON.getString("schemaURI");
				isReadOnly = attributeConfigJSON.getBoolean("readOnly");
				isRequired = attributeConfigJSON.getBoolean("required");
				isCaseExact = attributeConfigJSON.getBoolean("caseExact");

				String subAttributesString = attributeConfigJSON.getString("subAttributes");
				if (!"null".equalsIgnoreCase(subAttributesString)) {
					subAttributes = subAttributesString.split(" ");
				}

			} catch (JSONException e) {
				throw new CharonException("Error while parsing extension configuration", e);
			}

		}

		public String getAttributeURI() {
			return attributeURI;
		}

		public DataType getDataType() {
			DataType type = null;
			if ("STRING".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.STRING;
			} else if ("INTEGER".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.INTEGER;
			} else if ("DECIMAL".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.DECIMAL;
			} else if ("BOOLEAN".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.BOOLEAN;
			} else if ("DATE_TIME".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.DATE_TIME;
			} else if ("BINARY".equalsIgnoreCase(dataType)) {
				type = SCIMSchemaDefinitions.DataType.BINARY;
			}
			return type;
		}

		public boolean isMultiValued() {
			return isMultiValued;
		}

		public String getMultiValuedAttributeChildName() {
			if ("null".equals(multiValuedAttributeChildName)) {
				return null;
			}
			return multiValuedAttributeChildName;
		}

		public String getDescription() {
			return description;
		}

		public String getSchemaURI() {
			return schemaURI;
		}

		public boolean isReadOnly() {
			return isReadOnly;
		}

		public boolean isRequired() {
			return isRequired;
		}

		public boolean isCaseExact() {
			return isCaseExact;
		}

		public String[] getSubAttributes() {
			return subAttributes;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public boolean hasChildren() {
			return subAttributes != null ? true : false;
		}
	}
}
