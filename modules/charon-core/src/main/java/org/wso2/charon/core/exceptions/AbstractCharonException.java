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
package org.wso2.charon.core.exceptions;

/**
 * If an error occurs in SCIM operation,in addition to returning the HTTP response code,
 * an human readable explanation should also be returned in the body.
 * This class abstract out the Exceptions that should be thrown at a failure of SCIM operation and
 * implementers can use code property to decide which HTTP code needs to be set in header of the
 * response.
 */
public class AbstractCharonException extends Exception {

    //human readable explanation of the error.
    protected String description;

    //relevant HTTP code. 
    protected int code;

    public AbstractCharonException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public AbstractCharonException(String message) {
        this.code = -1;
        this.description = message;
    }

    public AbstractCharonException() {
        this.code = -1;
        this.description = null;
    }

    public int getCode() {
        return code;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public AbstractCharonException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
        this.description = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
