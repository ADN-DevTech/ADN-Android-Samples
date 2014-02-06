package Autodesk.ADN.Android.DroidView;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class ModelSelectActivity 
	extends Activity
	implements WebServiceConnector.WebServiceResponseListener
{
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	Gson _parser;
	
	public static AdnDbModel SelectedModel;
		
	private WebServiceConnector _webSrvConnector;
    
    private static final String METHOD_NAME = "GetDbModelData";

    private static final String URL = "http://23.23.212.64:80/AdnViewerSrv/rest/";
    //private static final String URL = "http://10.0.2.2:80/AdnViewerSrv/rest/";
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    private class AdnDbModelItemAdapter 
    	extends ArrayAdapter<AdnDbModel>
    { 	  
        int _resource; 
        Context _context; 
        AdnDbModel _data[] = null;

		//////////////////////////////////////////////////////////////////////////////////
		//
		//
		//////////////////////////////////////////////////////////////////////////////////
        public AdnDbModelItemAdapter(Context context, int resource,  AdnDbModel data[]) 
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
                
                holder.Image = (ImageView)row.findViewById(R.id.Image);
                holder.Text = (TextView)row.findViewById(R.id.Text);
                
                row.setTag(holder);
            }
            else
            {
                holder = (TagHolder)row.getTag();
            }
            
            AdnDbModel model = _data[position];
            
            holder.Text.setText(model.ModelName);
            holder.Image.setImageResource(model.IconRes);
            
            return row;
        }
        
        class TagHolder
        {
            ImageView Image;
            TextView Text;
        }
    }

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        _parser = new Gson();
        
        SelectedModel = null;
        
        setTitle("ADN DroidView");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setResult(Activity.RESULT_CANCELED);
        setContentView(R.layout.model_listview);
        
        _webSrvConnector = new WebServiceConnector(
        	URL, 
    		METHOD_NAME,
    		this);

        _webSrvConnector.InvokeWebGet();
        
        setTitle("ADN DroidView [Connecting Web Service...]");
		setProgressBarIndeterminateVisibility(true);
    }

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected void onDestroy() 
    {
        super.onDestroy();
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

            SelectedModel = (AdnDbModel)item;
            
            setResult(Activity.RESULT_OK);
            
            finish();
        }
    };

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnWebServiceSuccess(String JsonMsg) 
	{
		final Activity activity = this;
		
		try 
		{
			Type collectionType = new TypeToken<AdnDbModel[]>(){}.getType();
			
			final AdnDbModel[] models = (AdnDbModel[]) 
				_parser.fromJson(JsonMsg , collectionType);
			
			for(AdnDbModel model: models)
			{
				model.SetIcon();
			}
			
			this.runOnUiThread(new Runnable() 
	        {
	            public void run() 
	            {	    
	        	    AdnDbModelItemAdapter adapter = new AdnDbModelItemAdapter(
        	    		activity, 
        	    		R.layout.model_listview_item, 
        	    		models);
	                
	        	    View header = (View)getLayoutInflater().inflate(
        	    		R.layout.model_listview_header, null);
	        	    
	                ListView lv = (ListView) findViewById(R.id.modelsList);
	                
	                lv.addHeaderView(header, null, false);
	                lv.setAdapter(adapter);
	                lv.setOnItemClickListener(_itemClickListener);
	                
	                setTitle("ADN DroidView");
	           	 	activity.setProgressBarIndeterminateVisibility(false);
	           	 	
	           	 	//requestWindowFeature(Window.FEATURE_NO_TITLE);
	            }
	        });	
		}
		catch (Exception ex) 
		{
			OnWebServiceFailed(activity, ex);
			return;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnWebServiceFailed(Exception ex) 
	{
		OnWebServiceFailed(this, ex);
		
		finish();
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





