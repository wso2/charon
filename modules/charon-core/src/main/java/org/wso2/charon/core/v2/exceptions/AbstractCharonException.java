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
package org.wso2.charon.core.v2.exceptions;


import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * If an error occurs in SCIM operation,in addition to returning the HTTP response code,
 * an human readable explanation should also be returned in the body.
 * This class abstract out the Exceptions that should be thrown at a failure of SCIM operation and
 * implementers can use code property to decide which HTTP code needs to be set in header of the
 * response.
 */
public class AbstractCharonException extends Exception {

    //Error responses are identified using the following "schema" uri
    protected String schemas;

    //A detailed human-readable message.
    protected String detail;

    //The HTTP status code
    protected int status;

    public AbstractCharonException(int status, String detail, String scimType) {
        this.schemas = ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI;
        this.status = status;
        this.detail = detail;
    }
    public AbstractCharonException() {
        this.schemas = ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI;
        this.status = ResponseCodeConstants.CODE_INTERNAL_ERROR;
        this.detail = null;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause. Note that the detail message associated with
     * causeis not automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A null value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public AbstractCharonException(String message, Throwable cause) {
        super(message, cause);
        this.status = ResponseCodeConstants.CODE_INTERNAL_ERROR;
        this.detail = message;
    }

    public AbstractCharonException(String message) {
        this.status = ResponseCodeConstants.CODE_INTERNAL_ERROR;
        this.detail = message;
    }

    public String getSchemas() {
        return schemas; }

    public void setSchemas(String schemas) {
        this.schemas = schemas; }

    public String getDetail() {
        return detail; }

    public void setDetail(String detail) {
        this.detail = detail; }

    public int getStatus() {
        return status; }

    public void setStatus(int status) {
        this.status = status; }
}


