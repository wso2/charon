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

package org.wso2.charon3.core.config;

import java.util.ArrayList;

/**
 * This extension interface defines the defines the methods for configuring the charon.
 */
public interface Configuration {

    public void setDocumentationURL(String documentationURL);

    public void setPatchSupport(boolean supported);

    public void setBulkSupport(boolean supported, int maxOperations, int maxPayLoadSize);

    public void setFilterSupport(boolean supported, int maxResults);

    public void setChangePasswordSupport(boolean supported);

    public void setETagSupport(boolean supported);

    public void setSortSupport(boolean supported);

    public void setAuthenticationSchemes(ArrayList<Object[]> authenticationSchemes);

    public void setCountValueForPagination(int count);
}
