package com.notworking.application;

import android.util.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class BingSearch
{
  public static JSONObject searchForImage(String query)
  {
    JSONObject json=null;
    String accountKey = "83yxv/tHAtgJffZrAQ/JtCK60XJnW3UcMi0nj5MM8PU";
    byte[] accountKeyBytes = Base64.encode((":" + accountKey).getBytes(), Base64.NO_WRAP);
    String accountKeyEnc = new String(accountKeyBytes);
    query = query.replaceAll(" ", "%20");

    HttpGet httpget = new HttpGet("https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27" + query + "%27&Adult=%27Off%27&$top=10&$format=Json");
    HttpClient httpclient = new DefaultHttpClient();
    try
    {
      httpget.setHeader("Authorization", "Basic " + accountKeyEnc);
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String responseBody = httpclient.execute(httpget, responseHandler);
      json = new JSONObject(responseBody);
    } catch (ClientProtocolException e1)
    {
      e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (IOException e1)
    {
      e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (JSONException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } finally
    {
      httpclient.getConnectionManager().shutdown();
    }

    return json;
  }

}

