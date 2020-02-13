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
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.schema.SCIMConstants.CommonSchemaConstants.VALUE;
import static org.wso2.charon3.core.schema.SCIMConstants.GroupSchemaConstants.MEMBERS;

/**
 * Represents the Group object which is a collection of attributes defined by SCIM Group-schema.
 */
public class Group extends AbstractSCIMObject {

    private static final long serialVersionUID = 6106269076155338045L;
    /**
     * get the display name of the group
     * @return
     * @throws CharonException
     */
    public String getDisplayName() {
        if (isAttributeExist(SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)) {
            return LambdaExceptionUtils.rethrowSupplier(() -> ((SimpleAttribute) attributeList.get(
                    SCIMConstants.GroupSchemaConstants.DISPLAY_NAME)).getStringValue()).get();
        } else {
            return null;
        }
    }

    /**
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

    /**
     * deletes the current value of displayname and exchanges it with the given value
     */
    public void replaceDisplayName(String displayname) {
        replaceSimpleAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY_NAME, displayname);
    }

    /**
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
                if (subAttributesList != null && subAttributesList.containsKey(
                        SCIMConstants.CommonSchemaConstants.VALUE)) {
                    memberList.add(((SimpleAttribute) (subAttributesList.get(
                            SCIMConstants.CommonSchemaConstants.VALUE))).getValue());
                }
            }
        }
        return memberList;
    }

    /**
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
                    if (subAttributesList != null && subAttributesList.containsKey(
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

    /**
     * This method used to add member (user) to a group. According to the SCIM specification need to add ref
     * attribute as well along with display and value. Hence deprecated this method.
     *
     * @deprecated use {@link #setMember(User user)} instead.
     */
    @Deprecated
    public void setMember(String value, String display) throws BadRequestException, CharonException {
        setMember(value, display, null, null);
    }

    /*
     * set a member to the group
     * @param value
     * @param display
     * @param ref
     * @param type
     * @throws BadRequestException
     * @throws CharonException
     */
    public void setMember(String value, String display, String ref, String type)
           throws BadRequestException, CharonException {
        if (!isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
          MultiValuedAttribute members = new MultiValuedAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS);
          DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, members);
          setAttribute(members);
        }
        MultiValuedAttribute members = (MultiValuedAttribute) getAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS);
        ComplexAttribute complexAttribute = setMemberCommon(value, display, ref, type);
        members.setAttributeValue(complexAttribute);
    }

    /**
     * set member to the group
     * @param userId
     * @param userName
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private ComplexAttribute setMemberCommon(String userId, String userName, String ref, String type)
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

        SimpleAttribute refSimpleAttribute = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.REF, ref);
        DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.REF, refSimpleAttribute);

        SimpleAttribute typeSimpleAttribute = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.TYPE, type);
                DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.TYPE, typeSimpleAttribute);

        complexAttribute.setSubAttribute(valueSimpleAttribute);
        complexAttribute.setSubAttribute(displaySimpleAttribute);
        complexAttribute.setSubAttribute(refSimpleAttribute);
        complexAttribute.setSubAttribute(typeSimpleAttribute);
        DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, complexAttribute);
        return  complexAttribute;
    }

    /**
     * Set a member to the group, where member will have three default attributes such as name, value and $ref.
     *
     * @param user
     * @throws BadRequestException
     * @throws CharonException
     */
    public void setMember(User user) throws BadRequestException, CharonException {

        if (this.isAttributeExist(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
            MultiValuedAttribute members = (MultiValuedAttribute) this.attributeList
                    .get(SCIMConstants.GroupSchemaConstants.MEMBERS);
            ComplexAttribute complexAttribute = setMemberCommon(user);
            members.setAttributeValue(complexAttribute);
        } else {
            MultiValuedAttribute members = new MultiValuedAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS);
            DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, members);
            ComplexAttribute complexAttribute = setMemberCommon(user);
            members.setAttributeValue(complexAttribute);
            this.setAttribute(members);
        }
    }

    /**
     * Create member attribute with three default attributes such as name, value and $ref.
     *
     * @param user User object.
     * @return Member object as a complex attribute.
     * @throws BadRequestException
     * @throws CharonException
     */
    private ComplexAttribute setMemberCommon(User user) throws BadRequestException, CharonException {

        String userId = user.getId();
        String userName = user.getUserName();
        String reference = user.getLocation();

        ComplexAttribute complexAttribute = new ComplexAttribute();
        complexAttribute.setName(SCIMConstants.GroupSchemaConstants.MEMBERS + "_" + userId + SCIMConstants.DEFAULT);

        if (userId != null) {
            SimpleAttribute valueSimpleAttribute =
                    new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, userId);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.VALUE, valueSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
        }

        if (userName != null) {
            SimpleAttribute displaySimpleAttribute = new SimpleAttribute(SCIMConstants.GroupSchemaConstants.DISPLAY,
                    userName);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.DISPLAY, displaySimpleAttribute);
            complexAttribute.setSubAttribute(displaySimpleAttribute);
        }

        if (reference != null) {
            SimpleAttribute referenceSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.REF,
                    reference);
            DefaultAttributeFactory
                    .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.REF, referenceSimpleAttribute);
            complexAttribute.setSubAttribute(referenceSimpleAttribute);
        }

        DefaultAttributeFactory
                .createAttribute(SCIMSchemaDefinitions.SCIMGroupSchemaDefinition.MEMBERS, complexAttribute);
        return complexAttribute;
    }

    /**
     * set the schemas for scim object -group
     */
    public void setSchemas() {
        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
        List<String> schemasList = schema.getSchemasList();
        for (String scheme : schemasList) {
            setSchema(scheme);
        }

    }

    /**
     * Returns the ID list of all members of type User
     */
    public List<String> getUserIds() {
        return getMemberIdsOfType(SCIMConstants.USER);
    }


    /**
     * Returns the ID list of all members of type Group
     */
    public List<String> getSubGroupIds() {
        return getMemberIdsOfType(SCIMConstants.GROUP);
    }


    /**
     * Returns the ID list of all members of specified type
     */
    public List<String> getMemberIdsOfType(String searchType) {
        List<String> memberIds = new ArrayList<>();
        if (!isAttributeExist(MEMBERS)) {
            return memberIds;
        }

        MultiValuedAttribute membersAttribute = (MultiValuedAttribute) getAttribute(MEMBERS);
        if (membersAttribute == null) {
            return Collections.emptyList();
        }
        List<Attribute> memberList = membersAttribute.getAttributeValues();
        for (Attribute memberListEntry : memberList) {
            ComplexAttribute memberComplexAttribute = (ComplexAttribute) memberListEntry;
            if (!memberComplexAttribute.isSubAttributeExist(VALUE)
                || !memberComplexAttribute.isSubAttributeExist(SCIMConstants.CommonSchemaConstants.TYPE)) {
                continue;
            }
            //@formatter:off
            String type = (String) (LambdaExceptionUtils.rethrowSupplier(() ->
                (SimpleAttribute) memberComplexAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.TYPE))
                .get()).getValue();
            //@formatter:on
            if (type == null || !type.equals(searchType)) {
                continue;
            }

            //@formatter:off
            String value = (LambdaExceptionUtils.rethrowSupplier(() ->
                ((SimpleAttribute) memberComplexAttribute.getSubAttribute(SCIMConstants.CommonSchemaConstants.VALUE))
                    .getStringValue())).get();
            //@formatter:on
            memberIds.add(value);
        }
        return memberIds;
    }

}
