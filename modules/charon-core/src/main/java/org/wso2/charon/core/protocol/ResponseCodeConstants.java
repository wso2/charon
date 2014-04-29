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

/**
 * SCIM Protocol uses the response status codes defined in HTTP to indicate
 * operation success or failure. This class includes those code and relevant description as constants.
 */
public class ResponseCodeConstants {

    //when errors returned in response, this goes as the heading of the body:
    public static final String ERRORS = "Errors";
    public static final String CODE = "code";
    public static final String DESCRIPTION = "description";

    public static final int CODE_FORMAT_NOT_SUPPORTED = 406;
    public static final String DESC_FORMAT_NOT_SUPPORTED = "Requested format is not supported.";

    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    public static final String DESC_INTERNAL_SERVER_ERROR =
            "The server encountered an unexpected condition which prevented it from fulfilling the request";

    public static final int CODE_RESOURCE_NOT_FOUND = 404;
    public static final String DESC_RESOURCE_NOT_FOUND = "Specified resource does not exist.";

    public static final int CODE_BAD_REQUEST = 400;
    public static final String DESC_BAD_REQUEST = "Request is unparseable, syntactically " +
                                                  "incorrect, or violates schema";
    public static final String DESC_BAD_REQUEST_GET = "GET request does not support the " +
                                                      "requested URL query parameter combination.";

    public static final int CODE_OK = 200;

    public static final int CODE_CREATED = 201;
    public static final String DESC_CREATED = "Created";

    public static final int CODE_NO_CONTENT = 204;
    public static final String DESC_NO_CONTENT = "No Content";

    public static final int CODE_UNAUTHORIZED = 401;
    public static final String DESC_UNAUTHORIZED = "Authorization failure";

    public static final int CODE_DUPLICATED = 409;
    public static final String DESC_DUPLICATED = "Duplicated Resource";
    

    //Other common error messages thrown by the API
    public static final String MISMATCH_IN_REQUESTED_DATATYPE = "Datatype doesn't match " +
                                                                "the datatype of the attribute value";
    public static final String ATTRIBUTE_ALREADY_EXIST = "Attribute with the same attribute name " +
                                                         "already exist.";
    public static final String ATTRIBUTE_READ_ONLY = "Attribute is read only. Hence can not be modified..";

    public static final String JSON_DECODE_ERROR = "Error in building resource from the JSON representation";


}
