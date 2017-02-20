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
package org.wso2.charon3.core.protocol;


import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.bulk.BulkRequestContent;
import org.wso2.charon3.core.objects.bulk.BulkRequestData;
import org.wso2.charon3.core.objects.bulk.BulkResponseContent;
import org.wso2.charon3.core.objects.bulk.BulkResponseData;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;
import org.wso2.charon3.core.protocol.endpoints.ResourceManager;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

/**
 *
 */
public class BulkRequestProcessor {
    private UserResourceManager userResourceManager;
    private GroupResourceManager groupResourceManager;
    private int failOnError;
    private int errors;
    private UserManager userManager;


    public UserResourceManager getUserResourceManager() {
        return userResourceManager;
    }

    public void setUserResourceManager(UserResourceManager userResourceManager) {
        this.userResourceManager = userResourceManager;
    }

    public GroupResourceManager getGroupResourceManager() {
        return groupResourceManager;
    }

    public void setGroupResourceManager(GroupResourceManager groupResourceManager) {
        this.groupResourceManager = groupResourceManager;
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
        userResourceManager = new UserResourceManager();
        groupResourceManager = new GroupResourceManager();
        failOnError = 0;
        errors = 0;
        userManager = null;
    }

    public BulkResponseData processBulkRequests(BulkRequestData bulkRequestData) throws BadRequestException {

        BulkResponseData bulkResponseData = new BulkResponseData();
        SCIMResponse response = null;

        for (BulkRequestContent bulkRequestContent : bulkRequestData.getUserOperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addUserOperation
                            (getBulkResponseContent(bulkRequestContent, userResourceManager));
            } else {
                if (errors < failOnError) {
                    bulkResponseData.addUserOperation
                            (getBulkResponseContent(bulkRequestContent, userResourceManager));
                }
            }

        }
        for (BulkRequestContent bulkRequestContent : bulkRequestData.getGroupOperationRequests()) {
            if (failOnError == 0) {
                bulkResponseData.addGroupOperation
                            (getBulkResponseContent(bulkRequestContent, groupResourceManager));
            } else  {
                if (errors < failOnError) {
                    bulkResponseData.addGroupOperation
                            (getBulkResponseContent(bulkRequestContent, groupResourceManager));
                }
            }

        }
        bulkResponseData.setSchema(SCIMConstants.BULK_RESPONSE_URI);
        return bulkResponseData;
    }


   private BulkResponseContent getBulkResponseContent
           (BulkRequestContent bulkRequestContent, ResourceManager resourceManager)
           throws BadRequestException {

       BulkResponseContent bulkResponseContent = null;
       SCIMResponse response;

       if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.POST)) {

           response = resourceManager.create
                   (bulkRequestContent.getData(), userManager, null, null);
           bulkResponseContent = createBulkResponseContent
                   (response, SCIMConstants.OperationalConstants.POST, bulkRequestContent);
           errorsCheck(response);

       } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PUT)) {

           String resourceId = extractIDFromPath(bulkRequestContent.getPath());
           response = resourceManager.updateWithPUT
                   (resourceId, bulkRequestContent.getData(), userManager, null, null);
           bulkResponseContent = createBulkResponseContent
                   (response, SCIMConstants.OperationalConstants.PUT, bulkRequestContent);
           errorsCheck(response);

       } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.PATCH)) {

           String resourceId = extractIDFromPath(bulkRequestContent.getPath());
           response = resourceManager.updateWithPATCH
                   (resourceId, bulkRequestContent.getData(), userManager, null, null);
           bulkResponseContent = createBulkResponseContent
                   (response, SCIMConstants.OperationalConstants.PATCH, bulkRequestContent);
           errorsCheck(response);

       } else if (bulkRequestContent.getMethod().equals(SCIMConstants.OperationalConstants.DELETE)) {
           String resourceId = extractIDFromPath(bulkRequestContent.getPath());
           response = resourceManager.delete(resourceId, userManager);
           bulkResponseContent = createBulkResponseContent
                   (response, SCIMConstants.OperationalConstants.DELETE, bulkRequestContent);
           errorsCheck(response);
       }
       return bulkResponseContent;
   }

    private String extractIDFromPath(String path) throws BadRequestException {
        String [] parts = path.split("[/]");
        if (parts[2] != null) {
            return parts[2];
        } else {
            throw new BadRequestException
                    ("No resource Id is provided in path", ResponseCodeConstants.INVALID_VALUE);
        }
    }

    private BulkResponseContent createBulkResponseContent(SCIMResponse response, String method,
                                                          BulkRequestContent requestContent) {
        BulkResponseContent bulkResponseContent = new BulkResponseContent();

        bulkResponseContent.setScimResponse(response);
        bulkResponseContent.setMethod(method);
        bulkResponseContent.setLocation(response.getHeaderParamMap().get(SCIMConstants.LOCATION_HEADER));
        bulkResponseContent.setBulkID(requestContent.getBulkID());
        bulkResponseContent.setVersion(requestContent.getVersion());

        return bulkResponseContent;

    }

    private void errorsCheck(SCIMResponse response) {
        if (response.getResponseStatus() != 200 && response.getResponseStatus() != 201 &&
                response.getResponseStatus() != 204) {
            errors++;
        }
    }

}
