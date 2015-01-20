/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.protocol;

import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.bulk.BulkRequestContent;
import org.wso2.charon.core.objects.bulk.BulkRequestData;
import org.wso2.charon.core.objects.bulk.BulkResponseContent;
import org.wso2.charon.core.objects.bulk.BulkResponseData;
import org.wso2.charon.core.protocol.endpoints.GroupResourceEndpoint;
import org.wso2.charon.core.protocol.endpoints.UserResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BulkRequestProcessor {
    private UserResourceEndpoint userResourceEndpoint;
    private GroupResourceEndpoint groupResourceEndpoint;
    private String inputFormat;
    private String outputFormat;
    private int failOnError;
    private int errors;
    private UserManager userManager;

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public UserResourceEndpoint getUserResourceEndpoint() {
        return userResourceEndpoint;
    }

    public void setUserResourceEndpoint(UserResourceEndpoint userResourceEndpoint) {
        this.userResourceEndpoint = userResourceEndpoint;
    }

    public GroupResourceEndpoint getGroupResourceEndpoint() {
        return groupResourceEndpoint;
    }

    public void setGroupResourceEndpoint(GroupResourceEndpoint groupResourceEndpoint) {
        this.groupResourceEndpoint = groupResourceEndpoint;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public int getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(int failOnError) {
        this.failOnError = failOnError;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public BulkRequestProcessor() {
        userResourceEndpoint = new UserResourceEndpoint();
        groupResourceEndpoint = new GroupResourceEndpoint();
        inputFormat = SCIMConstants.APPLICATION_JSON;
        outputFormat = SCIMConstants.APPLICATION_JSON;
        failOnError = 0;
        errors = 0;
        userManager = null;
    }

    public BulkResponseData processBulkRequests(BulkRequestData bulkRequestData) {


        BulkResponseData bulkResponseData = new BulkResponseData();
        //collect the response from bulk user creating requests.
        List<BulkResponseContent> bulkUserResponse = this.processUserCreatingRequests(
                bulkRequestData.getUserCreatingRequests());
        List<BulkResponseContent> bulkGroupResponse = this.processGroupCreatingRequests(
                bulkRequestData.getGroupCreatingRequests());

        //TODO: Have to collect response of Other operations in User and Group endpoints

        bulkResponseData.setUserCreatingResponse(bulkUserResponse);
        bulkResponseData.setGroupCreatingResponse(bulkGroupResponse);
        bulkResponseData.setSchemas(bulkRequestData.getSchemas());

        return bulkResponseData;
    }

    /**
     * Before call this method you have to initialize inputFormat,outputFormat,failOnError, userManager
     *
     * @param userCreatingRequests
     * @return
     */
    private List<BulkResponseContent> processUserCreatingRequests(
            List<BulkRequestContent> userCreatingRequests) {
        List<BulkResponseContent> scimResponses = new ArrayList<BulkResponseContent>();
        for (int i = 0; i < userCreatingRequests.size(); i++) {
            BulkRequestContent userRequest = userCreatingRequests.get(i);
            BulkResponseContent bulkResponseContent = new BulkResponseContent();
            SCIMResponse response = null;
            //TODO: have to check the "failOnError" and "errors", if reached the failOnError limit we have to exit
            //since this is bulk user add bulk flag set to true, for the last element bulk user add flag set to false to
            //indicate last element of bulk user add so that provisioning component can reset
            // ThreadLocalProvisioningServiceProvider for the last element
            if (i < (userCreatingRequests.size() - 1)) {
                response = userResourceEndpoint
                        .create(userRequest.getData(), inputFormat, outputFormat, userManager, true);
            } else {
                response = userResourceEndpoint
                        .create(userRequest.getData(), inputFormat, outputFormat, userManager, false);
            }
            bulkResponseContent.setBulkID(userRequest.getBulkID());
            bulkResponseContent.setScimResponse(response);
            bulkResponseContent.setDescription(response.getResponseMessage());
            bulkResponseContent.setResponseCode(String.valueOf(response.getResponseCode()));
            bulkResponseContent.setMethod(userRequest.getMethod());

            scimResponses.add(bulkResponseContent);
        }
        return scimResponses;
    }

    private List<BulkResponseContent> processGroupCreatingRequests(
            List<BulkRequestContent> groupCreatingRequests) {
        List<BulkResponseContent> scimResponses = new ArrayList<BulkResponseContent>();
        for (BulkRequestContent groupRequest : groupCreatingRequests) {
            BulkResponseContent bulkResponseContent = new BulkResponseContent();
            //TODO: have to check the "failOnError" and "errors", if reached the failOnError limit we have to exit
            SCIMResponse response = groupResourceEndpoint.create(
                    groupRequest.getData(), inputFormat, outputFormat, userManager);
            bulkResponseContent.setBulkID(groupRequest.getBulkID());
            bulkResponseContent.setScimResponse(response);
            bulkResponseContent.setDescription(response.getResponseMessage());
            bulkResponseContent.setResponseCode(String.valueOf(response.getResponseCode()));
            bulkResponseContent.setMethod(groupRequest.getMethod());

            scimResponses.add(bulkResponseContent);
        }
        return scimResponses;
    }


}
