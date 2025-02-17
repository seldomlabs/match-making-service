
package com.matchmaker.service.unirest;

import com.matchmaker.service.util.HttpResponseOutput;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ake a generic way to make a get or a post call. Presently Unirest
 *         calls are spread throughout the code. Target going forward is to use
 *         this service for making get or post call for better code organization
 *
 */
@Service("unirestCallsService")
public class HttpCallsService {

    static Logger logger = LogManager.getLogger(HttpCallsService.class);

    public static boolean hasTimeOutBeenSet = false;

    // http call without unirest
    public static String makeHttpPostRequestWithTimeout(String url, String data, int timeOut) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        if (con != null) {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(timeOut);
            con.setConnectTimeout(timeOut);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            logger.info("post call with data " + data + " response code is " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            return response.toString();
        }

        return null;

    }

    // Http call without using unirest
    public static String makeGetRequestWithTimeout(String url, int timeinMillis) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setReadTimeout(timeinMillis);
        con.setConnectTimeout(timeinMillis);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static HttpResponseOutput makeGetRequestWithHeadersAndTimeout(String url, HashMap<String, String> paramMap, int timeOut) throws IOException
    {
        HttpResponseOutput httpResponse = new HttpResponseOutput();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        if (con != null)
        {
            con.setRequestMethod("GET");
            con.setReadTimeout(timeOut);

            // For https handling
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            if (paramMap != null && !paramMap.isEmpty()) {
                for (Map.Entry<String, String> entry: paramMap.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();
            httpResponse.setResponseString(response.toString());
            httpResponse.setStatusCode(responseCode);
            // print result
            return httpResponse;
        }
        return null;

    }

    public static HttpResponseOutput makePostRequestWithHeadersAndTimeout(String url, String data, HashMap<String, String> paramMap , int timeOut) throws IOException
    {
        HttpResponseOutput httpResponse = new HttpResponseOutput();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        if (con != null)
        {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            for (Map.Entry<String, String> entry: paramMap.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setDoOutput(true);
            con.setReadTimeout(timeOut);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            logger.info("post call with data " + data + " response code is " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            httpResponse.setStatusCode(responseCode);
            httpResponse.setResponseString(response.toString());
            return httpResponse;
        }
        return null;
    }

    public static String makeHttpPostRequestWithTimeoutAndEncoding(String url, String data, int timeOut) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        if (con != null) {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(timeOut);
            con.setConnectTimeout(timeOut);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(data.getBytes("UTF-8"));
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            logger.info("post call with data " + data + " response code is " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            return response.toString();
        }

        return null;

    }

    public static HttpResponseOutput postRequestWithHeadersAndTimeoutUtf8Data(String url, String data, HashMap<String, String> paramMap , int timeOut) throws IOException
    {
        HttpResponseOutput httpResponse = new HttpResponseOutput();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        if (con != null)
        {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setDoOutput(true);
            con.setReadTimeout(timeOut);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(data.getBytes("UTF-8"));
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            logger.info("post call with data " + data + " response code is " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            httpResponse.setStatusCode(responseCode);
            httpResponse.setResponseString(response.toString());
            return httpResponse;
        }
        return null;
    }
}
