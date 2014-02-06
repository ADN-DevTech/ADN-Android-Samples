package Autodesk.ADN.Android.DroidView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import android.os.AsyncTask;
import android.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import com.google.gson.Gson;


//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class WebServiceConnector
{
	private Gson _parser;
	
	private WebServiceTask _webSrvTask;
	
	private enum WebAction
	{
		kGet, kPost
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private ArrayList<WebServiceResponseListener> _listeners = 
			new ArrayList<WebServiceResponseListener>();
	
	public interface WebServiceResponseListener 
	{
		public void OnWebServiceSuccess(String JsonMsg);
		
		public void OnWebServiceFailed(Exception ex);
	}
	
	public void addEventListener(WebServiceResponseListener listener) 
	{
		_listeners.add(listener);
	}

    public void removeEventListener(WebServiceResponseListener listener) 
    {
        _listeners.remove(listener);
    }
	    
    private void OnWebServiceSuccess(String JsonMsg) 
    {   	
        for (WebServiceResponseListener listener : _listeners) 
        {
            listener.OnWebServiceSuccess(JsonMsg);
        }
    }
    
    private void OnWebServiceFailed(Exception ex) 
    {   	
        for (WebServiceResponseListener listener : _listeners) 
        {
            listener.OnWebServiceFailed(ex);
        }
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public WebServiceConnector(
    		String url, 
    		String methodName)
    {
		_parser = new Gson();
    	
    	_webSrvTask = new WebServiceTask(
			url, 
    		methodName); 	
    }
    
    public WebServiceConnector(
    		String url, 
    		String methodName, 
			WebServiceResponseListener listener) 
    {
    	_parser = new Gson();
    	
    	_webSrvTask = new WebServiceTask(
			url, 
    		methodName);
    	
    	_listeners.add(listener);
    }
       
    public void NewRequest()
    {
    	_webSrvTask = new WebServiceTask(
    		_webSrvTask._url, 
			_webSrvTask._method);
    }
    
    void AddRequestParam(String param)
    {
    	_webSrvTask._param = param;
    }
    
    public void InvokeWebGet()
    {
    	_webSrvTask._WebAction = WebAction.kGet;
    	_webSrvTask.execute();
    }
    
    public void InvokeWebPost()
    {
    	_webSrvTask._WebAction = WebAction.kPost;
    	_webSrvTask.execute();
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public String DecompressJson(String zipText) throws IOException 
	{
		byte[] compressed = Base64.decode(zipText, Base64.DEFAULT);
		
		if (compressed.length <= 4)
			return null;
		
		GZIPInputStream gzipInputStream = new GZIPInputStream(
			new ByteArrayInputStream(
			compressed, 
			4,
			compressed.length - 4));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		for (int value = 0; value != -1;) 
		{
			value = gzipInputStream.read();
			
			if (value != -1) 
			{
				baos.write(value);
			}
		}
		
		gzipInputStream.close();
		baos.close();
		
		return new String(baos.toByteArray(), "UTF-8");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	Object ParseJson(String jsonMsg, Type collectionType)
	{
		return _parser.fromJson(jsonMsg, collectionType);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    private class WebServiceTask extends AsyncTask<Void, Void, Void> 
    {
    	String _url;
		String _method; 
		String _param;
		
		WebAction _WebAction;
		
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
    	public WebServiceTask(
        		String url,
        		String method)
        {
    		_url = url;
            _method = method;
            _param = null;
        }
        
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
    	private String ConvertStreamToString(InputStream is) 
		{ 
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
		    StringBuilder sb = new StringBuilder(); 
		
		    String line = null; 
		    try 
		    { 
		      while ((line = reader.readLine()) != null) 
		      { 
		        sb.append(line + "\n"); 
		      } 
		    } 
		    catch (IOException e) 
		    { 
		      e.printStackTrace(); 
		    } 
		    finally 
		    { 
		      try 
		      { 
		        is.close(); 
		      } 
		      catch (IOException e) 
		      { 
		        e.printStackTrace(); 
		      } 
		    } 
		
		    return sb.toString(); 
		} 
    	
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
    	private String GetJsonMessage(String address) 
    	{ 
		    HttpClient httpclient = new DefaultHttpClient(); 
		
			HttpGet httpget = new HttpGet(address); 
			  
			HttpResponse response; 
			
			try
			{
			    response = httpclient.execute(httpget); 
			    
			    HttpEntity entity = response.getEntity(); 
			    
			    if (entity != null) 
			    { 
			    	InputStream instream = entity.getContent(); 
			    	String result = ConvertStreamToString(instream);        
			    	instream.close(); 
			
			    	return result; 
			    } 
			    
			    return null; 
			}
			catch (Exception e) 
			{
				return null; 
			}	    
    	} 
    	
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
    	private String PostJsonMessage(String address, String jsonParam)
    	{
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(address);
			
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			
			try 
			{    	        
			    StringEntity strEntity = new StringEntity(jsonParam, HTTP.UTF_8);
			    
			    strEntity.setContentEncoding(
					new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    
			    httppost.setEntity(strEntity); 
			
			    HttpResponse response = httpclient.execute(httppost);
			    
			    HttpEntity entity = response.getEntity(); 
			    
			   	if (entity != null) 
			    { 
			    	InputStream instream = entity.getContent(); 
			    	String result = ConvertStreamToString(instream);        
			    	instream.close(); 
			
			    	return result; 
			    } 
			    
			    return null; 
     	    }
 	    	catch (Exception e) 
 	    	{
 	    		return null; 
	 	    }	
    	}
		        
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
    	@Override
    	protected Void doInBackground(Void... params) 
    	{
    		try 
            {			
    			String jsonMsg = null;
    			
    			String request = _url + _method;
            
    			switch(_WebAction)
    			{
    				case kGet:
	    			
		    			if(_param != null)
		    				request += _param;
		    			
		    			jsonMsg = GetJsonMessage(request); 
		    			
		    			break;
		    			
    				case kPost:
    					
    					jsonMsg = PostJsonMessage(request, _param); 
	    			
    					break;
    					
    				default:
    					break;
    			}
	    			
    			if(jsonMsg != null)
    			{
    				OnWebServiceSuccess(jsonMsg);
    			}
    			else
    			{
    				OnWebServiceFailed(null);
    			}
            } 
            catch (Exception ex) 
            {
            	//Web Service failed...
            	OnWebServiceFailed(ex);
            }
    		
			return null;
    	}
    }
}



