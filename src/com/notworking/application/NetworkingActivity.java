package com.notworking.application;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkingActivity extends Activity
{
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    new DownloadImageTask().execute("http://jayurbain.com/images/mel-fetch-mke.jpg");
  }

  private InputStream openHttpConnection(String urlString) throws IOException
  {
    InputStream in = null;
    int response = -1;

    URL url = new URL(urlString);
    URLConnection conn = url.openConnection();

    if (!(conn instanceof HttpURLConnection))
    {
      throw new IOException("Not	an	HTTP	connection");
    }

    try
    {
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      httpConn.setAllowUserInteraction(false);
      httpConn.setInstanceFollowRedirects(true);
      httpConn.setRequestMethod("GET");
      httpConn.connect();
      response = httpConn.getResponseCode();
      if (response == HttpURLConnection.HTTP_OK)
      {
        in = httpConn.getInputStream();
      }
    } catch (Exception ex)
    {
      Log.d("Networking", ex.getLocalizedMessage());
      throw new IOException("Error	connecting");
    }
    return in;
  }

  private Bitmap downloadImage(String URL)
  {
    Bitmap bitmap = null;
    InputStream in = null;
    try
    {
      in = openHttpConnection(URL);
      bitmap = BitmapFactory.decodeStream(in);
      in.close();
    } catch (IOException e1)
    {
      Log.d("NetworkingActivity", e1.getLocalizedMessage());
    }
    return bitmap;
  }

  private class DownloadImageTask extends AsyncTask<String, Bitmap, Long>
  {
    //	---takes	in	a	list	of	image	URLs	in	String	type---
    protected Long doInBackground(String... urls)
    {
      long imagesCount = 0;
      for (int i = 0; i < urls.length; i++)
      {
        //	---download	the	image---
        Bitmap imageDownloaded = downloadImage(urls[i]);
        if (imageDownloaded != null)
        {
          //	---increment	the	image	count---
          imagesCount++;
          try
          {
            //	---insert	a	delay	of	3	seconds---
            Thread.sleep(3000);
          } catch (InterruptedException e)
          {
            e.printStackTrace();
          }
          //	---return	the	image	downloaded---
          publishProgress(imageDownloaded);
        }
      }
      //	---return	the	total	images	downloaded	count---
      return imagesCount;
    }

    //	---display	the	image	downloaded---
    protected void onProgressUpdate(Bitmap... bitmap)
    {
      ImageView img = (ImageView) findViewById(R.id.img);
      img.setImageBitmap(bitmap[0]);
    }

    //	---when	all	the	images	have	been	downloaded---
    protected void onPostExecute(Long imagesDownloaded)
    {
      Toast.makeText(getBaseContext(),
          "Total	" + imagesDownloaded + "	images	downloaded",
          Toast.LENGTH_LONG).show();
    }
  }


}