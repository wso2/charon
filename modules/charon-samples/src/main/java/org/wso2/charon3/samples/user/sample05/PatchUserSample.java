package org.wso2.charon3.samples.user.sample05;


import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * SCIM patch user sample.
 */
public class PatchUserSample {

    private static final Logger logger = LoggerFactory.getLogger(PatchUserSample.class);

    private static String patchRequestBody = "{\n" +
            "     \"schemas\":\n" +
            "       [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\n" +
            "     \"Operations\":[{\n" +
            "       \"op\":\"add\",\n" +
            "       \"value\":{\n" +
            "         \"emails\":[\n" +
            "           {\n" +
            "             \"value\":\"babs@jensen.org\",\n" +
            "             \"type\":\"home\"\n" +
            "           }\n" +
            "         ],\n" +
            "         \"nickname\":\"Babs\"\n" +
            "       }\n" +
            "     }," +
            "     {\n" +
            "        \"op\":\"replace\",\n" +
            "        \"path\":\"emails[value eq bjensen@example.com].value\",\n" +
            "        \"value\":\"vindula@wso2.com\",\n" +
            "     },\n" +
            "   ]\n" +
            "   }";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        //get the id of the user
        Scanner reader = new Scanner(System.in, "UTF-8");  // Reading from System.in
        System.out.print("Enter the user ID : ");
        String id = reader.next();
        try {
            String url = "http://localhost:8080/scim/v2/Users/"  + id;;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("PATCH");
            con.setRequestProperty("Content-Type", "application/scim+json");


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(patchRequestBody);
                wr.flush();
                wr.close();
            } finally {
                wr.close();
            }

            int responseCode = con.getResponseCode();

            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(
                        con.getErrorStream(), "UTF-8"));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //printing result from response
            logger.info("Response Code : " + responseCode);
            logger.info("Response Message : " + con.getResponseMessage());
            logger.info("Response Content : " + response.toString());


        } catch (ProtocolException e) {
            logger.error(e.getMessage());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
