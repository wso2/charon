<html>
<head>
    <title>Charon-SCIM Service Provider Reference Implementation</title>
    <!--<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="css/styles.css" type="text/css" rel="stylesheet" media="all"   />-->
    <script type="text/javascript" src="js/jquery-min.js"></script>
    <script type="text/javascript">
        function hidePopupDiv(){
            //var popupDiv = document.getElementById('popupDiv');
            //popupDiv.style.display = "none";
            $('#popupDiv').hide('slow');
        }

    </script>
    <link type="text/css" href="css/foo.css" />
</head>

<body>
<table border="0" width="100%">
    <tr>
        <td width="20%"><img src="images/scim.png" alt="WSO2 Charon" height="100" width="200"/>
        </td>
        <th width="60%">Register as a Tenant.</th>
        <td width="20%"><img src="images/header-logo.gif" alt="WSO2" height="50" width="250"/>
        </td>
    </tr>
    <tr>
        <td width="20%">
            <table border="0" width="50%" align="top">
                <tr>
                    <td><a href="./index.html">Home</a></td>
                </tr>
                <tr>
                    <td><a href="./jsp/register.jsp">Register</a></td>
                </tr>
                <tr>
                    <td><a href="login.jsp">Login</a></td>
                </tr>
                <tr>
                    <td><a href="./about2.html">About Charon</a></td>
                </tr>
            </table>
        </td>
        <td width="60%">
            <div id="popupDiv">
                Successfull !!
                <input type="button" onclick="hidePopupDiv()" />
            </div>
            <table border="0" cellspacing="0" cellpadding="0" align="center">
                <form name="registerForm" method="post" action="RegisterServlet">
                    <tr>
                        <td>
                            Domain Name :
                        </td>
                        <td><input type="text" name="domain"></td>
                        <td><input type="Submit" name="checkAvailability" value="Check availability"
                                   class="button"></td>
                    </tr>
                    <tr>
                        <td/>
                        <td/>
                    </tr>
                    <tr>
                        <td>Admin user name :</td>
                        <td><input type="text" name="adminUserName"></td>
                    </tr>
                    <tr>
                        <td/>
                        <td/>
                    </tr>
                    <tr>
                        <td>Admin password :</td>
                        <td><input type="password" name="adminPassword"></td>
                    </tr>
                    <tr>
                        <td/>
                        <td/>
                    </tr>
                    <tr>
                        <td>Re-enter password :</td>
                        <td><input type="password" name="reEnteredPassword"></td>
                    </tr>
                    <tr>
                        <td/>
                        <td/>
                    </tr>
                    <tr>
                        <td/>
                        <td align="center"><input type="Submit" name="register" value="Register"
                                   class="button"></td>
                    </tr>
                </form>
            </table>

        <td width="20%"></td>
    </tr>
</table>
</body>
</html>