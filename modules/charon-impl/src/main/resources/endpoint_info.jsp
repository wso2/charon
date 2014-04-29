<html>
<head>
    <title>Charon-SCIM Service Provider Reference Implementation</title>
    <!--<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="css/styles.css" type="text/css" rel="stylesheet" media="all"   />-->
</head>
<%
    if ("true".equals((String) request.getAttribute("registered"))) {

%>
<body>
<table border="0" width="100%">
    <tr>
        <td width="20%"><img src="images/scim.png" alt="WSO2 Charon" height="100" width="200"/>
        </td>
        <th width="60%">SCIM defined resource endpoints</th>
        <td width="20%"><img src="images/header-logo.gif" alt="WSO2" height="50" width="250"/>
        </td>
    </tr>
    <tr>
        <td width="20%">
            <table border="0" width="50%" align="top">
                <tr>
                    <td><a href="index.html">Home</a></td>
                </tr>
                <tr>
                    <td><a href="register.jsp">Register</a></td>
                </tr>
                <tr>
                    <td><a href="login.jsp">Login</a></td>
                </tr>
                <tr>
                    <td><a href="about2.html">About Charon</a></td>
                </tr>
            </table>
        </td>
        <td width="60%">
            <p> Following are the SCIM defined REST based resource endpoints which are made
                available
                publicly for the 1st SCIM interop event:</p>

            <p>User : http://localhost:8080/charonDemoApp/scim/Users/</p>

            <p>Group : http://localhost:8080/charonDemoApp/scim/Groups/</p>
            <%--<p>Bulk : </p>--%>
        <td width="20%"></td>
    </tr>
</table>
<%
    }
%>
</body>
</html>