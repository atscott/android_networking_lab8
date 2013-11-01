package com.notworking.application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: atscott
 * Date: 10/31/13
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BingImageResultJSONParser
{
  public List<String> getURLSForImagesFromJson(JSONObject BingJSONResponse)
  {
    List<String> urls = new ArrayList<String>();
    try
    {
      JSONObject d = BingJSONResponse.getJSONObject("d");
      JSONArray results = d.getJSONArray("results");
      for(int i=0;i<results.length();i++)
      {
        JSONObject result = results.getJSONObject(i);
        urls.add(result.getString("MediaUrl"));
      }
    } catch (JSONException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return urls;
  }
}
