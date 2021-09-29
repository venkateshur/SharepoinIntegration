package com.test.integration;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

class GetAccessToken {
    public String getSharepointToken() throws IOException {
        String accessToken = "";
        try {

            // AccessToken url

            String wsURL = "https://accounts.accesscontrol.windows.net/92e84ceb-fbfd-47ab-be52-080c6b87953f/tokens/OAuth/2";

            URL url = new URL(wsURL);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;

            // Set header
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");


            String jsonParam = "grant_type=client_credentials"
                    + "&client_id=<shp_clientId>@<shp_tenandId>"
                    + "&client_secret=<shp_clientSecret>"
                    + "&resource=00000003-0000-0ff1-ce00-000000000000/<your organization here>.sharepoint.com@<shp_tenantId>";


            // Send Request
            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
            wr.writeBytes(jsonParam);
            wr.flush();
            wr.close();

            // Read the response.
            InputStreamReader isr = null;
            if (httpConn.getResponseCode() == 200) {
                isr = new InputStreamReader(httpConn.getInputStream());
            } else {
                isr = new InputStreamReader(httpConn.getErrorStream());
            }

            BufferedReader in = new BufferedReader(isr);
            String responseString = "";
            String outputString = "";

            // Write response to a String.
            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
            // Extracting accessToken from string, here response
            // (outputString)is a Json format string
            if (outputString.contains("access_token\":\"")) {
                int i1 = outputString.indexOf("access_token\":\"");
                String str1 = outputString.substring(i1 + 15);
                int i2 = str1.indexOf("\"}");
                String str2 = str1.substring(0, i2);
                accessToken = str2;
                // System.out.println("accessToken.........." + accessToken);
            }
        } catch (Exception e) {
            accessToken = "Error: " + e.getMessage();
        }
        return accessToken;
    }
}