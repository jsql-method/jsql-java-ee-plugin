package it.jsql.connector.service;

import com.google.gson.Gson;
import it.jsql.connector.controller.JSQLController;
import it.jsql.connector.dto.JSQLConfig;
import it.jsql.connector.dto.JSQLResponse;
import it.jsql.connector.exceptions.JSQLException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class JSQLConnector {

    public static JSQLResponse callSelect(String transactionId, String API_URL, Object data, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/select", data, jsqlConfig);
    }

    public static JSQLResponse callDelete(String transactionId, String API_URL, Object data, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/delete", data, jsqlConfig);
    }

    public static JSQLResponse callUpdate(String transactionId, String API_URL, Object data, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/update", data, jsqlConfig);
    }

    public static JSQLResponse callInsert(String transactionId, String API_URL, Object data, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/insert", data, jsqlConfig);
    }

    public static JSQLResponse callRollback(String API_URL, String transactionId, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/rollback", null, jsqlConfig);
    }

    public static JSQLResponse callCommit(String API_URL, String transactionId, JSQLConfig jsqlConfig) throws JSQLException {
        return call(transactionId, API_URL + "/commit", null, jsqlConfig);
    }

    public static JSQLResponse call(String transactionId, String fullUrl, Object request, JSQLConfig jsqlConfig) throws JSQLException {

        if(transactionId.trim().isEmpty()){
            transactionId = null;
        }

        HttpURLConnection conn = null;

        try {

            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Api-Key", jsqlConfig.getApiKey());
            conn.setRequestProperty("Dev-Key", jsqlConfig.getDevKey());
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 4.01; Windows NT)");

            if(transactionId != null){
                conn.setRequestProperty(JSQLController.TRANSACTION_ID, transactionId);
            }

            conn.setUseCaches(false);

            OutputStream os = conn.getOutputStream();

            if(request != null){
                os.write(new Gson().toJson(request).getBytes());
            }

            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                String response = readInputStreamToString(conn, true);
                conn.disconnect();

                if (response.length() > 0 && response.contains("<div>")) {
                    response = response.substring(response.lastIndexOf("</div><div>") + 11, response.lastIndexOf("</div></body></html>"));
                }

                throw new JSQLException("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : " + response);
            }

            String response = readInputStreamToString(conn, false);

            conn.disconnect();

            JSQLResponse jsqlResponse = new JSQLResponse();
            jsqlResponse.response = new Gson().fromJson(response, List.class);
            jsqlResponse.transactionId = conn.getHeaderField(JSQLController.TRANSACTION_ID);

            return jsqlResponse;


        } catch (Exception e) {
            e.printStackTrace();
            throw new JSQLException("IOException JSQLConnector.call: " + e.getMessage());
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }


    }

    private static String readInputStreamToString(HttpURLConnection connection, boolean error) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(error ? connection.getErrorStream() : connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}
