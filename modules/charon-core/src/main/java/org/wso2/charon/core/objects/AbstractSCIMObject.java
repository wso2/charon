/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.Most of the large enterprise systems consists of many software systems. These different systems provide various functionalities required by the whole system. Therefore in order to provide some features across the platform these smaller software modules has to communicate with each other. However different software modules use different technologies and provide various protocols to communicate with the external systems. Standard transports like HTTP, SMTP, JMS, FTP, FIX and various adapter types such as SAP are widely being used. These transports can use different message formats such as SOAP, POX messages and different text message formats. Hence integrating these systems efficient and manageable manner in a distributed system is a well known problem.

0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.attributes.MultiValuedAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents the object which is a collection of attributes defined by common-schema.
 * These attributes MUST be included in all other objects which become SCIM resources.
 */
public abstract class AbstractSCIMObject implements SCIMObject {

    /**
     * Constructor that sets mandatory fields in a SCIM object.. like Core Schema URI et.
     * (Doesn't need any moresince it is done in attribute factory.)
     */
    public AbstractSCIMObject() {

    }

    /*Collection of attributes which constitute this resource.*/
    protected Map<String, Attribute> attributeList = new HashMap<String, Attribute>();

    /*List of schemas where the attributes of this resource, are defined.*/
    protected List<String> schemaList = new ArrayList<String>();

    public Map<String, Attribute> getAttributeList() {
        return attributeList;
    }

    /*public void setAttributeList(Map<String, Attribute> attributeList) {
        this.attributeList = attributeList;
    }*/

    public List<String> getSchemaList() {
        return schemaList;
    }

    public void setSchemaList(List<String> schemaList) {
        this.schemaList = schemaList;
        //TODO:set these as attribute also
    }

    /**
     * Set the attribute in the SCIM Object.
     *
     * @param newAttribute
     */
    public void setAttribute(Attribute newAttribute) {
        //and update the schemas list if any new schema used in the attribute, and create schemas array.
        if (!isSchemaExists(newAttribute.getSchemaName())) {
            schemaList.add(newAttribute.getSchemaName());
        }
        //add the attribute to attribute map    //TODO:check if read only, if so only we do not cha
        if (!isAttributeExist(newAttribute.getName())) {
            attributeList.put(newAttribute.getName(), newAttribute);
        }
    }

    /**
     * Update the attribute value by attribute name. Needs to be overloaded by specific types of
     * attributes.
     *
     * @param attributeName
     */
    //public abstract void updateAttribute(String attributeName, Object attributeValue);
    public Attribute getAttribute(String attributeName) throws NotFoundException {
        if (attributeList.containsKey(attributeName)) {
            return attributeList.get(attributeName);
        } else {
            throw new NotFoundException();
        }
    }

    protected boolean isSchemaExists(String schemaName) {
        return schemaList.contains(schemaName);
    }

    public boolean isAttributeExist(String attributeName) {
        return attributeList.containsKey(attributeName);
    }

    /*******Methods to manipulate the common attributes defined in SCIM Core Spec**********/

    //1.id attribute

    /**
     * Set a value for the id attribute. If attribute not already created in the resource,
     * create attribute and set the value.
     * Unique identifier for the SCIM Resource as defined by the Service Provider
     * This is read-only. So can only set once.
     *
     * @param id Unique identifier for the SCIM Resource as defined by the Service Provider.
     */
    public void setId(String id) throws CharonException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.ID)) {
            throw new CharonException(ResponseCodeConstants.ATTRIBUTE_READ_ONLY);
        } else {
            /*Attribute idAttribute = new SimpleAttribute(
                    SCIMConstants.CommonSchemaConstants.ID,
                    SCIMConstants.CORE_SCHEMA_URI, id, DataType.STRING, true,
                    false);*/
            SimpleAttribute idAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.ID, id);
            idAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    SCIMSchemaDefinitions.ID, idAttribute);
            this.setAttribute(idAttribute);
        }

    }

    /**
     * Get the value of id attribute.
     * Unique identifier for the SCIM Resource as defined by the Service Provider.
     *
     * @return
     */
    public String getId() throws CharonException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.ID)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.ID)).getStringValue();
        } else {
            return null;
        }
    }

    //2. external id attribute

    /**
     * Set the value for externalId attribute.
     * Unique identifier for the Resource as defined by the Service Consumer.
     *
     * @param externalId
     */
    public void setExternalId(String externalId) throws CharonException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID)) {
            ((SimpleAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.EXTERNAL_ID)).updateValue(
                    externalId, DataType.STRING);
        } else {
            Attribute externalIdAttribute = new SimpleAttribute(
                    SCIMConstants.CommonSchemaConstants.EXTERNAL_ID,
                    SCIMConstants.CORE_SCHEMA_URI, externalId, DataType.STRING,
                    true, false);
            this.setAttribute(externalIdAttribute);
        }
    }

    /**
     * Get the value of externalId attribute.
     *
     * @return
     * @throws CharonException
     */
    public String getExternalId() throws CharonException {
        if (isAttributeExist(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID)) {
            return ((SimpleAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.EXTERNAL_ID)).getStringValue();
        } else {
            return null;
        }
    }

    //3. Meta attribute - complex attribute containing resource meta data.

    /**
     * Set created date sub attribute of Meta attribute.
     *
     * @param createdDate
     * @throws org.wso2.charon.core.exceptions.CharonException
     *
     */
    public void setCreatedDate(Date createdDate) throws CharonException {
        //create the created date attribute as defined in schema.
        SimpleAttribute createdDateAttribute = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.CREATED, null, createdDate,
                DataType.DATE_TIME, true, false);
        createdDateAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.CREATED, createdDateAttribute);
        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check created date attribute already exist
            if (metaAttribute.isSubAttributeExist(createdDateAttribute.getName())) {
                //log info level log that created date already set and can't set again.
                throw new CharonException(ResponseCodeConstants.ATTRIBUTE_READ_ONLY);
            } else {

                metaAttribute.setSubAttribute(createdDateAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(createdDateAttribute);

        }
    }

    /**
     * Get the created date attribute of resource meta data.
     *
     * @return
     */
    public Date getCreatedDate() throws CharonException {
        if (isMetaAttributeExist()) {
            SimpleAttribute createdDate = (SimpleAttribute) getMetaAttribute().getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.CREATED);
            if (createdDate != null) {
                return createdDate.getDateValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setLastModified(Date lastModifiedDate) throws CharonException {
        //create the lastModified date attribute as defined in schema.
        SimpleAttribute lastModifiedAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.LAST_MODIFIED,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.LAST_MODIFIED, lastModifiedDate));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check created date attribute already exist
            if (metaAttribute.isSubAttributeExist(lastModifiedAttribute.getName())) {
                metaAttribute.removeSubAttribute(lastModifiedAttribute.getName());
                metaAttribute.setSubAttribute(lastModifiedAttribute);
            } else {

                metaAttribute.setSubAttribute(lastModifiedAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(lastModifiedAttribute);

        }
    }

    public Date getLastModified() throws CharonException {
        if (isMetaAttributeExist()) {
            SimpleAttribute createdDate = (SimpleAttribute) getMetaAttribute().getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.LAST_MODIFIED);
            if (createdDate != null) {
                return createdDate.getDateValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setVersion(String version) throws CharonException {
        //create the version attribute as defined in schema.
        SimpleAttribute versionAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.VERSION,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VERSION, version));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check version attribute already exist
            if (metaAttribute.isSubAttributeExist(versionAttribute.getName())) {
                metaAttribute.removeSubAttribute(versionAttribute.getName());
                metaAttribute.setSubAttribute(versionAttribute);
            } else {
                metaAttribute.setSubAttribute(versionAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(versionAttribute);

        }
    }

    public String getVersion() throws CharonException {
        if (isMetaAttributeExist()) {
            SimpleAttribute version = (SimpleAttribute) getMetaAttribute().getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.VERSION);
            if (version != null) {
                return version.getStringValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setLocation(String location) throws CharonException {
        //create the version attribute as defined in schema.
        SimpleAttribute versionAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.LOCATION,
                new SimpleAttribute(SCIMConstants.CommonSchemaConstants.LOCATION, location));

        //check meta complex attribute already exist.
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //check version attribute already exist
            if (metaAttribute.isSubAttributeExist(versionAttribute.getName())) {
                //log info level log that version already set and can't set again.
                throw new CharonException(ResponseCodeConstants.ATTRIBUTE_READ_ONLY);
            } else {

                metaAttribute.setSubAttribute(versionAttribute);
            }

        } else {
            //create meta attribute and set the sub attribute.
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(versionAttribute);

        }
    }

    public String getLocation() throws CharonException {
        if (isMetaAttributeExist()) {
            SimpleAttribute version = (SimpleAttribute) getMetaAttribute().getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.LOCATION);
            if (version != null) {
                return version.getStringValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Set the attributes to be removed in a patch operation.
     *
     * @param attributeNames
     */
    public void setAttributesOfMeta(List<String> attributeNames) throws CharonException {
        MultiValuedAttribute attributes = new MultiValuedAttribute(
                SCIMConstants.CommonSchemaConstants.ATTRIBUTES);
        attributes.setValuesAsStrings(attributeNames);
        attributes = (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(
                SCIMSchemaDefinitions.ATTRIBUTES, attributes);
        if (getMetaAttribute() != null) {
            ComplexAttribute metaAttribute = getMetaAttribute();
            //since this is not read-only, we just replace the attribute with newly created one.
            metaAttribute.setSubAttribute(attributes);
        } else {
            createMetaAttribute();
            getMetaAttribute().setSubAttribute(attributes);
        }
    }

    /**
     * Get the attributes to be removed in a patch operation.
     *
     * @return
     */
    public List<String> getAttributesOfMeta() throws CharonException {
        if (isMetaAttributeExist()) {
            MultiValuedAttribute attributes = (MultiValuedAttribute) getMetaAttribute().getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.ATTRIBUTES);
            if (attributes != null) {
                return attributes.getValuesAsStrings();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected boolean isMetaAttributeExist() {
        return attributeList.containsKey(SCIMConstants.CommonSchemaConstants.META);
    }

    protected void createMetaAttribute() throws CharonException {
        /*Attribute metaAttribute = new ComplexAttribute(
                SCIMConstants.CommonSchemaConstants.META,
                SCIMConstants.CORE_SCHEMA_URI, false, new HashMap<String, Attribute>(), false);*/
        ComplexAttribute metaAttribute =
                (ComplexAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.META,
                        new ComplexAttribute(SCIMConstants.CommonSchemaConstants.META));
        if (isMetaAttributeExist()) {
            throw new CharonException(ResponseCodeConstants.ATTRIBUTE_ALREADY_EXIST);
        } else {
            attributeList.put(SCIMConstants.CommonSchemaConstants.META, metaAttribute);
        }
    }

    protected ComplexAttribute getMetaAttribute() {
        if (isMetaAttributeExist()) {
            return (ComplexAttribute) attributeList.get(
                    SCIMConstants.CommonSchemaConstants.META);
        } else {
            return null;
        }
    }

    /**
     * Deleting an attribute is the responsibility of an attribute holder.
     *
     * @param id
     */
    public void deleteAttribute(String id) {
        if (attributeList.containsKey(id)) {
            attributeList.remove(id);
        }
    }


}
