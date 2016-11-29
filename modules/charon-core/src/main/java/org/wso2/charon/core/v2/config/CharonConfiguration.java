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
package org.wso2.charon.core.v2.config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains the charon related configurations.
 */
public class CharonConfiguration implements Configuration {

    private static CharonConfiguration charonConfiguration = new CharonConfiguration();

    private boolean patchSupport;
    private boolean filterSupport;
    private boolean bulkSupport;
    private boolean sortSupport;
    private boolean eTagSupport;
    private boolean changePasswordSupport;
    private String documentationURL;
    private int maxOperations;
    private int maxPayLoadSize;
    private int maxResults;
    private ArrayList<Object[]> authenticationSchemes = new ArrayList<Object[]>();

    //default count value for pagination
    private int count;

    /*
     * set documentationURL
     * @param documentationURL
     */
    public void setDocumentationURL(String documentationURL) {
        this.documentationURL = documentationURL;
    }

    /*
     * set Patch Support
     * @param supported
     */
    public void setPatchSupport(boolean supported) {
        this.patchSupport = supported;
    }

    /*
     * set Bulk Support
     * @param supported
     * @param maxOperations
     * @param maxPayLoadSize
     */
    public void setBulkSupport(boolean supported, int maxOperations, int maxPayLoadSize) {
        this.bulkSupport = supported;
        this.maxOperations = maxOperations;
        this.maxPayLoadSize = maxPayLoadSize;
    }

    /*
     * Set filter support
     * @param supported
     * @param maxResults
     */
    public void setFilterSupport(boolean supported, int maxResults) {
        this.filterSupport = supported;
        this.maxResults = maxResults;
    }

    /*
     * set Change Password Support
     * @param supported
     */
    public void setChangePasswordSupport(boolean supported) {
        this.changePasswordSupport = supported;
    }

    /*
     * set ETag Support
     * @param supported
     */
    public void setETagSupport(boolean supported) {
        this.eTagSupport = supported;
    }

    /*
     * set Sort Support
     * @param supported
     */
    public void setSortSupport(boolean supported) {
        this.sortSupport = supported;
    }

    /*
     * set Authentication Schemes
     * @param authenticationSchemes
     */
    public void setAuthenticationSchemes(ArrayList<Object[]> authenticationSchemes) {
        this.authenticationSchemes = authenticationSchemes;
    }

    /*
     * set Count Value For Pagination
     * @param count
     */
    @Override
    public void setCountValueForPagination(int count) {
        this.count = count;
    }

    /*
     * get Count Value For Pagination
     * @return
     */
    public int getCountValueForPagination() {
        return count;
    }

    /*
     * return the charon configuration map
     * @return
     */
    public HashMap<String, Object> getConfig() {
        HashMap<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(SCIMConfigConstants.DOCUMENTATION_URL, documentationURL);
        configMap.put(SCIMConfigConstants.BULK, bulkSupport);
        configMap.put(SCIMConfigConstants.SORT, sortSupport);
        configMap.put(SCIMConfigConstants.FILTER, filterSupport);
        configMap.put(SCIMConfigConstants.ETAG, eTagSupport);
        configMap.put(SCIMConfigConstants.CHNAGE_PASSWORD, changePasswordSupport);
        configMap.put(SCIMConfigConstants.MAX_OPERATIONS, maxOperations);
        configMap.put(SCIMConfigConstants.MAX_PAYLOAD_SIZE, maxPayLoadSize);
        configMap.put(SCIMConfigConstants.MAX_RESULTS, maxResults);
        configMap.put(SCIMConfigConstants.PATCH, patchSupport);
        configMap.put(SCIMConfigConstants.AUTHENTICATION_SCHEMES, authenticationSchemes);
        return  configMap;
    }

    /*
     * return the instance of CharonConfiguration
     * @return
     */
    public static CharonConfiguration getInstance() {
        return charonConfiguration;
    }
}
