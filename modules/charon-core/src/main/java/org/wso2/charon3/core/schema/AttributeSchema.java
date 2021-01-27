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
package org.wso2.charon3.core.schema;

import org.wso2.charon3.core.exceptions.CharonException;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface defines the common schema base for SCIM attributes and SCIM sub attributes.
 * In such defines as the 'attributes' attribute in Resource Schema Representation in SCIM 2.0
 */
public interface AttributeSchema {

    public void setURI(String uri);

    public String getURI();

    public String getName();

    public void setName(String name);

    public SCIMDefinitions.DataType getType();

    public void setType(SCIMDefinitions.DataType type);

    public boolean getMultiValued();

    public void setMultiValued(boolean isMultiValued);

    public String getDescription();

    public void setDescription(String description);

    public boolean getRequired();

    public void setRequired(boolean isRequired);

    public boolean getCaseExact();

    public void setCaseExact(boolean isCaseExact);

    public SCIMDefinitions.Mutability getMutability();

    public void setMutability(SCIMDefinitions.Mutability mutability);

    public SCIMDefinitions.Returned getReturned();

    public void setReturned(SCIMDefinitions.Returned returned);

    public SCIMDefinitions.Uniqueness getUniqueness();

    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness);

    public List<AttributeSchema> getSubAttributeSchemas();

    public AttributeSchema getSubAttributeSchema(String subAttribute);

    public void removeSubAttribute(String subAttributeName) throws CharonException;

    public List<String> getCanonicalValues();

    public void setCanonicalValues(ArrayList<String> canonicalValues);

}

