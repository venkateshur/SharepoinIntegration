package com.test.integration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;

public class SharepointDownload {

    public static void main(String[] args) {

        try {
            GetAccessToken accessToken = new GetAccessToken();
            String accessTokenInt = null;
            accessTokenInt = accessToken.getSharepointToken();
            String accessTokenIntGlobal = accessTokenInt;
            System.out.println("Access Token Generated - " + accessTokenIntGlobal);
            //Below folderUrl is the directory from where we will download the file.
            //and SiteURL is organizations' site for e.g. gotobo.sharepoint.com/sites/mysite
            String folderUrl = "Shared%20Documents" + "/test";
            String siteURL = "https://gotobo.sharepoint.com/sites/mysite";
            // ======================================================================
            // Get Folder Names from share point site to match our required directory from where download operation will be performed
            // ======================================================================
            String fUrl0 = siteURL + "/_api/web/GetFolderByServerRelativeUrl('" + folderUrl + "/"
                    + "')/Folders?$orderby=ListItemAllFields/Modified%20desc&$top=1";
            URL fileLstUrl0 = null;
                fileLstUrl0 = new URL(fUrl0);

            URLConnection fConnection0 = fileLstUrl0.openConnection();
            HttpURLConnection httpFConn0 = (HttpURLConnection) fConnection0;
            httpFConn0.setRequestMethod("GET");
            httpFConn0.setRequestProperty("Authorization", "Bearer " + accessTokenIntGlobal);
            httpFConn0.setRequestProperty("accept", "application/json;odata=verbose");
            String httpFResponseStr0 = "";
            InputStreamReader fisr0 = null;
            if (httpFConn0.getResponseCode() == 200) {
                fisr0 = new InputStreamReader(httpFConn0.getInputStream());
            } else {
                fisr0 = new InputStreamReader(httpFConn0.getErrorStream());
            }
            BufferedReader fin0 = new BufferedReader(fisr0);
            String strfLine0 = "";
            while ((strfLine0 = fin0.readLine()) != null) {
                httpFResponseStr0 = httpFResponseStr0 + strfLine0;
                System.out.println("strfLine==" + strfLine0);
            }
            System.out.println(httpFResponseStr0); // Print
            String fileName0 = "";
            JsonParser fparser0 = new JsonParser();

            JsonObject rootfObj0 = fparser0.parse(httpFResponseStr0).getAsJsonObject();
            JsonArray nameFileArray0 = rootfObj0.get("d").getAsJsonObject().get("results")
                    .getAsJsonArray();
            for (JsonElement fpa : nameFileArray0) {
                JsonObject jsonFileNameObj = fpa.getAsJsonObject();
                fileName0 = jsonFileNameObj.get("Name").getAsString();
            }
            System.out.println(fileName0); // Print
            // ======================================================================
            // Get File Names to find our file to be downloaded
            // ======================================================================
            String fUrl = siteURL + "/_api/web/GetFolderByServerRelativeUrl('" + folderUrl + "/"
                    + "')/Files?$orderby=ListItemAllFields/Modified%20desc";
            URL fileLstUrl = new URL(fUrl);
            URLConnection fConnection = fileLstUrl.openConnection();
            HttpURLConnection httpFConn = (HttpURLConnection) fConnection;
            httpFConn.setRequestMethod("GET");
            httpFConn.setRequestProperty("Authorization", "Bearer " + accessTokenIntGlobal);
            httpFConn.setRequestProperty("accept", "application/json;odata=verbose");
            // Read the response
            String httpFResponseStr = "";
            InputStreamReader fisr = null;
            if (httpFConn.getResponseCode() == 200) {
                fisr = new InputStreamReader(httpFConn.getInputStream());
            } else {
                fisr = new InputStreamReader(httpFConn.getErrorStream());
            }
            BufferedReader fin = new BufferedReader(fisr);
            String strfLine = "";
            while ((strfLine = fin.readLine()) != null) {
                httpFResponseStr = httpFResponseStr + strfLine;
                System.out.println("strfLine==" + strfLine);
            }
            System.out.println(httpFResponseStr); // Print
            // response
            String fileName = "";
            JsonParser fparser = new JsonParser();
            JsonObject rootfObj = fparser.parse(httpFResponseStr).getAsJsonObject();
            JsonArray nameFileArray = rootfObj.get("d").getAsJsonObject().get("results")
                    .getAsJsonArray();
            for (JsonElement fpa : nameFileArray) {
                JsonObject jsonFileNameObj = fpa.getAsJsonObject();
                fileName = jsonFileNameObj.get("Name").getAsString();
                //System.out.println("fileName :" + fileName);
                // ======================================================================
                // Download files in the
                // respective
                // folders
                // ======================================================================
                if (fileName.toLowerCase().contains("gotobo.zip")) {
                    fileName = fileName.replaceAll("\\s", "%20");
                    String fileUrl = siteURL + "/_api/web/GetFolderByServerRelativeUrl('"
                            + folderUrl + "/"
                            + "')/Files('" + fileName + "')/$value";
                    URL urlf = new URL(fileUrl);
                    URLConnection fconnection = urlf.openConnection();
                    HttpURLConnection httpfConn = (HttpURLConnection) fconnection;
                    httpfConn.setRequestMethod("GET");
                    httpfConn.setRequestProperty("Authorization", "Bearer " + accessTokenIntGlobal);
                    InputStream inputStream = httpfConn.getInputStream();
                    String fileDir = "c:\\users\\gotobo\\";
                    //System.out.println("My path : " + fileDir);
                    File fileDirs = new File(fileDir);
                    if (!fileDirs.exists()) {
                        fileDirs.mkdirs();
                    }
                    fileName = fileName.replaceAll("%20", " ");
                    String saveFilePath = "c:\\users\\gotobo\\" + fileName;
                    //System.out.println("saveFilePath.." + saveFilePath);
                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                    int bytesRead = -1;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();
                    System.out.println(fileName + "  downloaded :");
                    if (fileName.length() == 0) {
                    } else {
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The file could not be downloaded/Token error/File IO : " + new Timestamp(System.currentTimeMillis()));
        }
    }
}
