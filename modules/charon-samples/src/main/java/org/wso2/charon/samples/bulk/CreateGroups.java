package org.wso2.charon.samples.bulk;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.bulk.BulkData;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

/**
 * Created with IntelliJ IDEA.
 * User: dinuka
 * Date: 3/19/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateGroups {

    private static String path = SCIMConstants.CommonSchemaConstants.GROUPS_PATH;
    //Group details
    private static String displayName1 = "engineer";
    private static final String externalID1 = "engineer";
    private static final String[] members1 = {"c34d6718-1fc5-4538-afb2-80538e802b6e",
                                              "740f15cd-432d-4cf9-b7da-5fb0d098e1cd"};
    private static String bulk_id1 = "bulkGroup1";
    private static String method1 = "POST";

    //Group details
    private static String displayName2 = "doctor";
    private static final String externalID2 = "doctor";
    private static final String[] members2 = {"c34d6718-1fc5-4538-afb2-80538e802b6e",
                                              "740f15cd-432d-4cf9-b7da-5fb0d098e1cd"};
    private static String bulk_id2 = "bulkGroup2";
    private static String method2 = "POST";


    public static void main(String[] args) {

        try {
            //set the keystore
            SampleConstants.setKeyStore();
            //create SCIM client
            SCIMClient scimClient = new SCIMClient();
            //create a user according to SCIM User Schema

            //encode the user in JSON format
            String encodedUser;
            //====================
            BulkData bulkData = scimClient.createBulkRequestData();
            bulkData.setFailOnErrors(2);
            bulkData.addSchemas("urn:scim:schemas:core:1.0");

            Group group1 = scimClient.createGroup();
            group1.setDisplayName(displayName1);
            group1.setExternalId(externalID1);
            group1.setBulkID(bulk_id1);
            group1.setMethod(method1);
            group1.setPath(path);
            for (String member : members1) {
                group1.setMember(member);
            }

            Group group2 = scimClient.createGroup();
            group2.setDisplayName(displayName2);
            group2.setExternalId(externalID2);
            group2.setBulkID(bulk_id2);
            group2.setMethod(method2);
            group2.setPath(path);
            for (String member : members2) {
                group2.setMember(member);
            }

            bulkData.addGroup(group1);
            bulkData.addGroup(group2);
            String encodedString = scimClient.encodeSCIMObject(bulkData, SCIMConstants.JSON);

            System.out.println(encodedString);

            //create a apache wink ClientHandler to intercept and identify response messages
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            //set the handler in wink client config
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            //create a wink rest client with the above config
            RestClient restClient = new RestClient(clientConfig);
            //create resource endpoint to access User resource
            Resource userResource = restClient.resource(SampleConstants.BULK_ENDPOINT);

            BasicAuthInfo basicAuthInfo = new BasicAuthInfo();
            basicAuthInfo.setUserName(SampleConstants.CRED_USER_NAME);
            basicAuthInfo.setPassword(SampleConstants.CRED_PASSWORD);

            BasicAuthHandler basicAuthHandler = new BasicAuthHandler();
            BasicAuthInfo encodedBasicAuthInfo = (BasicAuthInfo) basicAuthHandler.getAuthenticationToken(basicAuthInfo);

            //System.out.println(encodedUser);
            //TODO:enable, disable SSL. For the demo purpose, we make the calls over http
            //send previously registered SCIM consumer credentials in http headers.
            String response = userResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedString);

            //decode the response
            System.out.println(response);


        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        } catch (CharonException e) {
            e.printStackTrace();
        } catch (BadRequestException e) {
            e.printStackTrace();
        }

    }


}
