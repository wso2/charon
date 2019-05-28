/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.charon3.core.objects.plainobjects;

/**
 * This class representation can be used to easily add a scim name to an.
 * {@link org.wso2.charon3.core.objects.AbstractSCIMObject} object
 */
public class ScimName {

    /**
     * The full name, including all middle names, titles, and suffixes as appropriate, formatted for display.
     * (e.g., "Ms. Barbara Jane Jensen, III").
     */
    private String formatted;

    /**
     * The family name of the User, or last name in most Western languages (e.g., "Jensen" given the full name.
     * "Ms. Barbara Jane Jensen, III").
     */
    private String familyName;

    /**
     * The given name of the User, or first name in most Western languages (e.g., "Barbara" given the full.
     * name "Ms. Barbara Jane Jensen, III").
     */
    private String givenName;

    /**
     * The middle name(s) of the User (e.g., "Jane" given the full name "Ms. Barbara Jane Jensen, III")..
     */
    private String middleName;

    /**
     * The honorific prefix(es) of the User, or title in most Western languages (e.g., "Ms." given the full.
     * name "Ms. Barbara Jane Jensen, III").
     */
    private String honorificPrefix;

    /**
     * The honorific suffix(es) of the User, or suffix in most Western languages (e.g., "III" given the full.
     * name "Ms. Barbara Jane Jensen, III").
     */
    private String honorificSuffix;

    public ScimName() {

    }

    public ScimName(String formatted,
                    String familyName,
                    String givenName,
                    String middleName,
                    String honorificPrefix,
                    String honorificSuffix) {

        this.formatted = formatted;
        this.familyName = familyName;
        this.givenName = givenName;
        this.middleName = middleName;
        this.honorificPrefix = honorificPrefix;
        this.honorificSuffix = honorificSuffix;
    }

    /**
     * @see #formatted.
     */
    public String getFormatted() {

        return formatted;
    }

    /**
     * @see #formatted.
     */
    public void setFormatted(String formatted) {

        this.formatted = formatted;
    }

    /**
     * @see #familyName.
     */
    public String getFamilyName() {

        return familyName;
    }

    /**
     * @see #familyName.
     */
    public void setFamilyName(String familyName) {

        this.familyName = familyName;
    }

    /**
     * @see #givenName.
     */
    public String getGivenName() {

        return givenName;
    }

    /**
     * @see #givenName.
     */
    public void setGivenName(String givenName) {

        this.givenName = givenName;
    }

    /**
     * @see #middleName.
     */
    public String getMiddleName() {

        return middleName;
    }

    /**
     * @see #middleName.
     */
    public void setMiddleName(String middleName) {

        this.middleName = middleName;
    }

    /**
     * @see #honorificPrefix.
     */
    public String getHonorificPrefix() {

        return honorificPrefix;
    }

    /**
     * @see #honorificPrefix.
     */
    public void setHonorificPrefix(String honorificPrefix) {

        this.honorificPrefix = honorificPrefix;
    }

    /**
     * @see #honorificSuffix.
     */
    public String getHonorificSuffix() {

        return honorificSuffix;
    }

    /**
     * @see #honorificSuffix.
     */
    public void setHonorificSuffix(String honorificSuffix) {

        this.honorificSuffix = honorificSuffix;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ScimName)) {
            return false;
        }

        ScimName scimName = (ScimName) o;

        if (formatted != null ? !formatted.equals(scimName.formatted) : scimName.formatted != null) {
            return false;
        }
        if (familyName != null ? !familyName.equals(scimName.familyName) : scimName.familyName != null) {
            return false;
        }
        if (givenName != null ? !givenName.equals(scimName.givenName) : scimName.givenName != null) {
            return false;
        }
        if (middleName != null ? !middleName.equals(scimName.middleName) : scimName.middleName != null) {
            return false;
        }
        if (honorificPrefix != null ? !honorificPrefix
                .equals(scimName.honorificPrefix) : scimName.honorificPrefix != null) {
            return false;
        }
        return honorificSuffix != null ? honorificSuffix
                .equals(scimName.honorificSuffix) : scimName.honorificSuffix == null;
    }

    @Override
    public int hashCode() {

        int result = formatted != null ? formatted.hashCode() : 0;
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (honorificPrefix != null ? honorificPrefix.hashCode() : 0);
        result = 31 * result + (honorificSuffix != null ? honorificSuffix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScimName{" +
                 "formatted='" + formatted + '\'' +
                 ", familyName='" + familyName + '\'' +
                 ", givenName='" + givenName + '\'' +
                 ", middleName='" + middleName + '\'' +
                 ", honorificPrefix='" + honorificPrefix + '\'' +
                 ", honorificSuffix='" + honorificSuffix + '\'' +
                 '}';
    }
}
