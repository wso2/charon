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
package org.wso2.charon.core.protocol.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.FormatNotSupportedException;
import org.wso2.charon.core.extensions.Storage;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.bulk.BulkRequestData;
import org.wso2.charon.core.objects.bulk.BulkResponseData;
import org.wso2.charon.core.protocol.BulkRequestProcessor;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.schema.SCIMConstants;

/**
 * REST API exposed by Charon-Core to perform bulk operations on UserResource.
 * Any SCIM service provider can call this API perform bulk operations,
 * based on the HTTP requests received by SCIM Client.
 */
public class BulkResourceEndpoint extends AbstractResourceEndpoint implements ResourceEndpoint {
    private Log logger = LogFactory.getLog(BulkResourceEndpoint.class);
    private Encoder encoder;
    private Decoder decoder;
    private BulkRequestProcessor bulkRequestProcessor;

    public BulkResourceEndpoint() {
        bulkRequestProcessor = new BulkRequestProcessor();
    }


    public SCIMResponse processBulkData(String data, String inputFormat, String outputFormat,
                                        UserManager userManager) {
        //TODO: have to remove directly hard coded values
        SCIMResponse response = new SCIMResponse(200, "");
        BulkResponseData bulkResponseData;

        try {
            //Get encoder and decoder from AbstractResourceEndpoint
            encoder = getEncoder(SCIMConstants.identifyFormat(outputFormat));
            decoder = getDecoder(SCIMConstants.identifyFormat(inputFormat));

            BulkRequestData bulkRequestDataObject;
            //decode the request
            bulkRequestDataObject = decoder.decodeBulkData(data);

            bulkRequestProcessor.setFailOnError(bulkRequestDataObject.getFailOnErrors());
            bulkRequestProcessor.setInputFormat(inputFormat);
            bulkRequestProcessor.setOutputFormat(outputFormat);
            bulkRequestProcessor.setUserManager(userManager);

            //Get bulk response data
            bulkResponseData = bulkRequestProcessor.processBulkRequests(bulkRequestDataObject);
            //encode the BulkResponseData object
            String finalEncodedResponse = encoder.encodeBulkResponseData(bulkResponseData);

            //careate SCIM response message
            response.setResponseMessage(finalEncodedResponse);
        } catch (BadRequestException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (FormatNotSupportedException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //if requested format not supported, encode exception and set it in the response.
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        } catch (CharonException e) {
            e.printStackTrace();
            logger.error(e.getDescription());
            //we have charon exceptions also, instead of having only internal server error exceptions,
            //because inside API code throws CharonException.
            if (e.getCode() == -1) {
                e.setCode(ResponseCodeConstants.CODE_INTERNAL_SERVER_ERROR);
            }
            return AbstractResourceEndpoint.encodeSCIMException(encoder, e);
        }


        return response;
    }

    @Override
    public SCIMResponse get(String id, String format, UserManager userManager) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse create(String scimObjectString, String inputFormat, String outputFormat,
                               UserManager userManager) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse delete(String id, Storage storage, String outputFormat) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listByAttribute(String searchAttribute, UserManager userManager,
                                        String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listByFilter(String filterString, UserManager userManager, String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listBySort(String sortBy, String sortOrder, UserManager usermanager,
                                   String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse listWithPagination(int startIndex, int count, UserManager userManager,
                                           String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse list(UserManager userManager, String format) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse updateWithPUT(String existingId, String scimObjectString,
                                      String inputFormat, String outputFormat,
                                      UserManager userManager) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMResponse updateWithPATCH(String existingId, String scimObjectString,
                                        String inputFormat, String outputFormat, UserManager userManager) {
        return null;
    }
}
