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
package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents the Group object which is a collection of attributes defined by SCIM Group-schema.
 */
public class Group extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;
    /*
     * get the display name of the group
     * @return
     * @throws CharonException
     */
    public String getDisplayName() throws CharonException {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).getStringValue();
        } else {
            return null;
        }
    }

    /*
     * set the display name of the group
     * @param displayName
     * @throws CharonException
     * @throws BadRequestException
     */
    public void setDisplayName(String displayName) throws CharonException, BadRequestException {
        if (this.isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            ((SimpleAttribute) this.attributeList.get(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).
                    updateValue(displayName);
        } else {
            SimpleAttribute displayAttribute = new SimpleAttribute(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayName);
            displayAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute
                    (SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY_NAME, displayAttribute);
            this.attributeList.put(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME, displayAttribute);
        }
    }

    /*
     * get the members of the group
     * @return
     */
    public List<Object> getMembers() {
        List<Object> memberList = new ArrayList<>();
        if (this.isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) this.attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            List<Attribute> subValuesList = members.getAttributeValues();
            for (Attribute subValue : subValuesList) {
                ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                if(subAttributesList != null && subAttributesList.containsKey(
                        SCIMConstants.CommonSchemaConstants.VALUE)) {
                    memberList.add(((SimpleAttribute) (subAttributesList.get(
                            SCIMConstants.CommonSchemaConstants.VALUE))).getValue());
                }
            }
            return memberList;
        } else {
            return null;
        }
    }

    /*
     * get the members of the group with their display names
     * @return
     */
    public List<String> getMembersWithDisplayName() {
        ArrayList displayNames = new ArrayList();
        if (this.isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) this.attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            List<Attribute> values = members.getAttributeValues();
            if (values != null) {
                List<Attribute> subValuesList = members.getAttributeValues();
                for (Attribute subValue : subValuesList) {
                    ComplexAttribute complexAttribute = (ComplexAttribute) subValue;
                    Map<String, Attribute> subAttributesList = complexAttribute.getSubAttributesList();
                    if(subAttributesList != null && subAttributesList.containsKey(
                            SCIMConstants.CommonSchemaConstants.DISPLAY)) {
                        displayNames.add(((SimpleAttribute) (subAttributesList.get(
                                SCIMConstants.CommonSchemaConstants.DISPLAY))).getValue());
                    }

                }
                return displayNames;
            }
        }

        return displayNames;
    }

    /*
     * set a member to the group
     * @param userId
     * @param userName
     * @throws BadRequestException
     * @throws CharonException
     */
    public void setMember(String userId, String userName) throws BadRequestException, CharonException {
        if (this.isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) this.attributeList.get(
                    SCIMConstants.GroupSchemaConstants.MEMBERS);
            ComplexAttribute complexAttribute = setMemberCommon(userId, userName);
            members.setAttributeValue(complexAttribute);
        } else {
            MultiValuedAttribute members = new MultiValuedAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, members);
            ComplexAttribute complexAttribute = setMemberCommon(userId, userName);
            members.setAttributeValue(complexAttribute);
            this.setAttribute(members);
        }
    }

    /*
     * set member to the group
     * @param userId
     * @param userName
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private ComplexAttribute setMemberCommon(String userId, String userName)
            throws BadRequestException, CharonException {
        ComplexAttribute complexAttribute = new ComplexAttribute();
        complexAttribute.setName(SCIMConstants.GroupSchemaConstants.MEMBERS + "_" + userId + SCIMConstants.DEFAULT);
        SimpleAttribute valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, userId);
        DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.VALUE, valueSimpleAttribute);

        SimpleAttribute displaySimpleAttribute = new SimpleAttribute(
                SCIMConstants.GroupSchemaConstants.DISPLAY, userName);
        DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY, displaySimpleAttribute);

        complexAttribute.setSubAttribute(valueSimpleAttribute);
        complexAttribute.setSubAttribute(displaySimpleAttribute);
        DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, complexAttribute);
        return  complexAttribute;
    }

    /*
     * set the schemas for scim object -group
     */
    public void setSchemas() {
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }

    }

}
