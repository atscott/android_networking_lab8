package com.notworking.application;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    new	DownloadTextTask().execute("apple");

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

  private	class	DownloadTextTask	extends	AsyncTask<String,	Void,	String>	{
    protected	String	doInBackground(String...	urls)	{
      return	wordDefinition(urls[0]);
    }

    @Override
    protected	void	onPostExecute(String	result)	{
      Toast.makeText(getBaseContext(),	result,
          Toast.LENGTH_LONG).show();
    }
  }

  private	String	wordDefinition(String	word)	{
    InputStream	in	=	null;
    String	strDefinition	=	"";
    try	{
      in	=	openHttpConnection(
          "http://services.aonaware.com/DictService/DictService.asmx/Define?word="+	word);
      Document doc	=	null;
      DocumentBuilderFactory	dbf	=
          DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      try	{
        db	=	dbf.newDocumentBuilder();
        doc	=	db.parse(in);
      }	catch	(ParserConfigurationException e)	{
        e.printStackTrace();
      }	catch	(Exception	e)	{
        e.printStackTrace();
      }
      doc.getDocumentElement().normalize();

      //	---retrieve	all	the	<Definition>	elements---
      NodeList	definitionElements	=	doc.getElementsByTagName("Definition");

      //	---iterate	through	each	<Definition>	elements---
      for	(int	i	=	0;	i	<	definitionElements.getLength();	i++)	{
        Node	itemNode	=	definitionElements.item(i);
        if	(itemNode.getNodeType()	==	Node.ELEMENT_NODE)	{
          //	---convert	the	Definition	node	into	an	Element---
          Element	definitionElement	=	(Element)	itemNode;

          //	---get	all	the	<WordDefinition>	elements	under
          //	the	<Definition>	element---
          NodeList	wordDefinitionElements	=	(definitionElement)
              .getElementsByTagName("WordDefinition");

          strDefinition	=	"";
          //	---iterate	through	each	<WordDefinition>	elements---
          for	(int	j	=	0;	j	<	wordDefinitionElements.getLength();
                j++)	{
            //	---convert	<WordDefinition>	node	into	an	Element---
            Element	wordDefinitionElement	=	(Element)
                wordDefinitionElements.item(j);

            //	---get	all	the	child	nodes	under	the
            //	<WordDefinition>	element---
            NodeList textNodes	=	((Node)
                wordDefinitionElement)
                .getChildNodes();

            strDefinition	+=	((Node)	textNodes.item(0))
                .getNodeValue()	+	".	\n";
          }

        }
      }
    }	catch	(IOException	e1)	{
      Log.d("NetworkingActivity",	e1.getLocalizedMessage());
    }
    //	---return	the	definitions	of	the	word---
    return	strDefinition;
  }

}