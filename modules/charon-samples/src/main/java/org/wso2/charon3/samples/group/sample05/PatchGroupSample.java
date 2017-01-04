package org.wso2.charon3.samples.group.sample05;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class PatchGroupSample {

    private static String patchRequestBody = "{ \"schemas\":\n" +
            "      [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\n" +
            "     \"Operations\":[\n" +
            "       {\n" +
            "        \"op\":\"replace\",\n" +
            "        \"path\":\"members[value eq 902c246b-6245-4190-8e05-00816be7344a].display\",\n" +
            "        \"value\":\"Vindula\"\n" +
            "       },\n" +
            "     ]\n" +
            "   }";

    public static void main(String[] args) {

        //get the id of the user
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.print("Enter the group ID : ");
        String id = reader.next();
        try {
            String url = "http://localhost:8080/scim/v2/Groups/"  + id;;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("PATCH");
            con.setRequestProperty("Content-Type", "application/scim+json");


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(patchRequestBody);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(
                        con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //printing result from response
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Message : " + con.getResponseMessage());
            System.out.println("Response Content : " + response.toString());


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
