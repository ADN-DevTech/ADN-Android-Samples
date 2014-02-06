package Autodesk.ADN.Android.DroidView;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MetaDataManager 
	implements WebServiceConnector.WebServiceResponseListener
{
	private Activity _activity;
	
	private AdnMetaData _currentMetaData;
	
	private WebServiceConnector _webSrvConnector;
    
	private static final String METHOD_NAME = "GetMetaData/";
	
	private static final String URL = "http://23.23.212.64:80/AdnViewerSrv/rest/";

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private class MetaDataItemAdapter 
		extends ArrayAdapter<AdnMetaDataElement>
	{ 	  
		AdnMetaDataElement _data[] = null;
		
		Context _context; 
		
		int _resource; 
		
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
		public MetaDataItemAdapter(
			Context context, 
			int resource,  
			AdnMetaDataElement[] data) 
		{ 
			super(context, resource, data); 
			
			_resource = resource;
			
			_context = context;
			
			_data = data;
		} 
		
		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View row = convertView;
			
			TagHolder holder;
			
			if(row == null)
			{
				LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
				
				row = inflater.inflate(_resource, parent, false);
				
				holder = new TagHolder();
				
				holder.Text = (TextView)row.findViewById(R.id.metaDataText);
				
				row.setTag(holder);
			}
			else
			{
				holder = (TagHolder)row.getTag();
			}
			
			AdnMetaDataElement element = _data[position];
			
			holder.Text.setText(element.Name + " : " + element.Value.toString());
			
			return row;
		}
		
		private class TagHolder
		{
			TextView Text;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public MetaDataManager(Activity activity)
	{
		_activity = activity;
		
		Display display = _activity.getWindowManager().getDefaultDisplay();

		int width = display.getWidth();
		
		LinearLayout layout = 
			(LinearLayout) _activity.findViewById(R.id.dataLayout);
		
		layout.setVisibility(View.GONE);
		
		LayoutParams lp = layout.getLayoutParams();
	        
        lp.width = width / 4;
        
        layout.setLayoutParams(lp);
	
		View header = (View)activity.getLayoutInflater().inflate(
    		R.layout.metadata_listview_header, null);
	        	            		
        ListView lv = (ListView)activity.findViewById(R.id.listView);
        
        lv.addHeaderView(header, null, false);
        lv.setOnItemClickListener(_itemClickListener);
       
        //TextView tv = (TextView)activity.findViewById(R.id.metaDataText);
        
        //tv.setTextSize(20);
        
        final Button bCommit = 
    		(Button) _activity.findViewById(R.id.bCommit);
        
        bCommit.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                bCommit.setVisibility(View.GONE);
                
                WebServiceConnector webSrvConnector = new WebServiceConnector(
	    			URL, 
	    			"UpdateMetaData");
                
                //Perform Web Service POST operation
                Gson gson = new Gson();
                          
                //Create json parameters
                String json = gson.toJson(_currentMetaData);
                
                webSrvConnector.AddRequestParam(json);
                
                webSrvConnector.InvokeWebPost();
            }
        });
		
		_webSrvConnector = new WebServiceConnector(
    		URL, 
    		METHOD_NAME, 
    		this);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private OnItemClickListener _itemClickListener = new OnItemClickListener() 
	{
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		{
			Object item = parent.getItemAtPosition(position);
			
			final AdnMetaDataElement element = (AdnMetaDataElement)item;
			
			AlertDialog.Builder alert = new AlertDialog.Builder(_activity);

        	alert.setTitle("Edit metadata...");
        	alert.setMessage(element.Name);

        	// Set an EditText view to get user input 
        	final EditText input = new EditText(_activity);
        	
        	input.setText(element.Value.toString());
        	
        	alert.setView(input);

        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        	{
            	public void onClick(DialogInterface dialog, int whichButton) 
            	{
            	  Editable value = input.getText();
            	  
            	  element.Value = value.toString();
            	  
            	  Button bCommit = (Button) _activity.findViewById(R.id.bCommit);
  	        	  bCommit.setVisibility(View.VISIBLE);
            	}
        	});

        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        	{
        		public void onClick(DialogInterface dialog, int whichButton) 
			    {
			        // Canceled.
			    }
        	});

        	alert.show();
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void DisplayMetaData(String metaDataId)
	{
		_webSrvConnector.NewRequest();
    	
    	_webSrvConnector.AddRequestParam(metaDataId);

		_webSrvConnector.InvokeWebGet();
		
		_activity.setProgressBarIndeterminateVisibility(true);
		
		Button bCommit = (Button) _activity.findViewById(R.id.bCommit);
        bCommit.setVisibility(View.GONE);
	}
		
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void ClearMetaData()
	{
		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() 
        {
            public void run() 
            {	 
            	LinearLayout layout = 
        			(LinearLayout) activity.findViewById(R.id.dataLayout);
        		
        		layout.setVisibility(View.GONE);
            }
        });
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnWebServiceSuccess(String JsonMsg) 
	{
		final Activity activity = _activity;

		try 
		{
			String decompressed = _webSrvConnector.DecompressJson(JsonMsg);
			
			Type collectionType = new TypeToken<AdnMetaData>(){}.getType();
			
			_currentMetaData = (AdnMetaData)_webSrvConnector.ParseJson(
				decompressed, collectionType);
						
			activity.runOnUiThread(new Runnable() 
	        {
	            public void run() 
	            {	 
	            	if(_currentMetaData != null)
	            	{  
	            		AdnMetaDataElement values[] = 
            				new AdnMetaDataElement[_currentMetaData.Elements.size()];
	            		
	            		for(int i=0;i<_currentMetaData.Elements.size(); i++)
	            		{
	            			values[i] = _currentMetaData.Elements.get(i);
	            		}
	          		
	            		MetaDataItemAdapter adapter = new MetaDataItemAdapter(
	        	    		activity, 
	        	    		R.layout.metadata_listview_item, 
	        	    		values);
 		
		                ListView lv = (ListView)activity.findViewById(R.id.listView);
		                
		                lv.setAdapter(adapter);  
		                
	                	TextView tv = (TextView)activity.findViewById(R.id.metaDataTextHeader);
	            		
	            		tv.setText("MetaData");
		                
	            		LinearLayout layout = 
                			(LinearLayout) activity.findViewById(R.id.dataLayout);
	                		
                		layout.setVisibility(View.VISIBLE);
	            	}
	            	else
	            	{
	            		Toast.makeText(activity, "Failed to retrieve data..." , Toast.LENGTH_SHORT).show();
	            	}

	            	activity.setProgressBarIndeterminateVisibility(false);
	            }
	        });
		} 
		catch (Exception ex) 
		{
			OnWebServiceFailed(_activity, ex);
			return;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnWebServiceFailed(Exception ex) 
	{
		OnWebServiceFailed(_activity, ex);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private void OnWebServiceFailed(Activity activity, Exception ex)
    {
    	final Activity f_activity = activity;
		
    	f_activity.runOnUiThread(new Runnable() 
        {
            public void run() 
            {	 
            	Toast.makeText(f_activity, "Web service call failed..." , Toast.LENGTH_SHORT).show();
            	
            	f_activity.setTitle("ADN DroidView");
            	f_activity.setProgressBarIndeterminateVisibility(false);
            }
        });
    }
}























