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
package org.wso2.charon.impl.servlets;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.extensions.CharonManager;
import org.wso2.charon.utils.DefaultCharonManager;
import org.wso2.charon.utils.storage.TenantInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String domainName = (String) req.getAttribute("domain");
            String adminName = (String) req.getAttribute("adminUserName");
            String adminPassword = (String) req.getAttribute("adminPassword");

            CharonManager charonManger = DefaultCharonManager.getInstance();

            TenantInfo tenantInfo = new TenantInfo();
            //tenantInfo.setAuthenticationMechanism(authMechanism);
            tenantInfo.setTenantAdminUserName(adminName);
            tenantInfo.setTenantAdminPassword(adminPassword);
            tenantInfo.setTenantDomain(domainName);

            charonManger.registerTenant(tenantInfo);
            req.setAttribute("registered", "true");
            resp.sendRedirect("http://localhost:8080/charonDemoApp/jsp/endpoint_info.jsp");

        } catch (CharonException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            req.setAttribute("registered","false");
            resp.sendRedirect("http://localhost:8080/charonDemoApp/jsp/endpoint_info.jsp");
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
