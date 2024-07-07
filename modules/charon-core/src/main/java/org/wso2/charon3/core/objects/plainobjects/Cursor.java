/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.objects.plainobjects;

/**
 * This class representation can be used to create a cursor type object which carries direction and cursor value
 * to be used in cursor-based pagination.
 */
public class Cursor {

    private String cursorValue;
    private String direction;

    public Cursor (String cursorVal, String direction) {
        this.cursorValue = cursorVal;
        this.direction = direction;
    }

    public String getCursorValue() {

        return cursorValue;
    }

    public void setCursorValue(String cursorValue) {

        this.cursorValue = cursorValue;
    }

    public String getDirection() {

        return direction;
    }

    public void setDirection(String direction) {

        this.direction = direction;
    }
}
