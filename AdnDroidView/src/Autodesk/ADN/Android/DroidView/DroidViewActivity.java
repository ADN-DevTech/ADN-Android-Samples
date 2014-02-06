package Autodesk.ADN.Android.DroidView;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import Autodesk.ADN.Android.DroidView.ModelRenderer.ISelectionListener;
import Autodesk.ADN.Android.DroidView.TouchManager.ITouchListener;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

//////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////
public class DroidViewActivity 
	extends Activity
	implements WebServiceConnector.WebServiceResponseListener, ITouchListener, ISelectionListener
{
	private Timer _timer;
	
	private ModelRenderer _renderer;
	
	TouchManager _touchManager;
	
	MetaDataManager _metaDataManager;
	
	ValueAccumulator _zoomAccumulator;
	
	private WebServiceConnector _webSrvConnector;
	    
	private static final String METHOD_NAME = "GetMeshData/";
	
	private static final String URL = "http://23.23.212.64:80/AdnViewerSrv/rest/";
	//private static final String URL = "http://10.0.2.2:80/AdnViewerSrv/rest/";
    
    // Intent request codes
    private static final int REQUEST_SELECT_MODEL = 1;
    
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setTitle("ADN DroidView");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        _timer = new Timer();
        
        setContentView(R.layout.main);
        
        _renderer = new ModelRenderer(
    		this,
			getApplication());
        
        _renderer.AddEventListener(this);

        // set up the glsurfaceview
        GLSurfaceView glView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        glView.setRenderer(_renderer);
        
    	_touchManager = new TouchManager(glView);
    	
    	_metaDataManager = new MetaDataManager(this);
        
        _zoomAccumulator = new ValueAccumulator(100.0, 82.0, 180.0);
        
        _webSrvConnector = new WebServiceConnector(
        		URL, 
        		METHOD_NAME, 
        		this);
        
        //Setup full screen
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(
    		WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/ 
    }
	    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
    }

    @Override
    protected void onResume() 
    {   
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        
        inflater.inflate(R.menu.options, menu);
        
        return true;
    }

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        Intent intent = null;
        
        switch (item.getItemId()) 
        {
        case R.id.menu_select_model:
        	
        	intent = new Intent(this, ModelSelectActivity.class);
        	
            startActivityForResult(intent, REQUEST_SELECT_MODEL);
            
            return true;
            
        case R.id.menu_close_model:
        	
        	setTitle("ADN DroidView");
        	
        	_renderer.LoadIdleModel();
        	
        	_metaDataManager.ClearMetaData();
        	 
        	_touchManager.RemoveEventListener(this);
            
        	return true;
        }
        
        return false;
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        switch (requestCode) 
        {
        case REQUEST_SELECT_MODEL:
            
        	if(ModelSelectActivity.SelectedModel != null)
        	{
        		Toast.makeText(
    				this, 
    				"Selected Model: " + ModelSelectActivity.SelectedModel.ModelName, 
    				Toast.LENGTH_SHORT).show();
        		
        		setTitle("ADN DroidView [Connecting Web Service...]");
        		setProgressBarIndeterminateVisibility(true);
        		
        		_webSrvConnector.NewRequest();
            	
            	_webSrvConnector.AddRequestParam(
            		ModelSelectActivity.SelectedModel.ModelId);

        		_webSrvConnector.InvokeWebGet();
        		
        		_timer.Start();
        	}
        	        	
            break;
        }
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnWebServiceSuccess(String jsonMsg) 
	{
		final Activity activity = this;
		
		try 
		{
			String decompressed = _webSrvConnector.DecompressJson(jsonMsg);
			
			Type collectionType = new TypeToken<AdnMeshData[]>(){}.getType();

			final AdnMeshData[] data = (AdnMeshData[])_webSrvConnector.ParseJson(
					decompressed, collectionType);
			
			this.runOnUiThread(new Runnable() 
	        {
	            public void run() 
	            {	 
	            	if(data != null)
	            	{  
	            		data[0].Save(activity, "droidview.mesh");
	            		
	            		_metaDataManager.ClearMetaData();
	            		
	            		_renderer.LoadModel(data);            
	            		
	            		_touchManager.AddEventListener((ITouchListener) activity);
	            	}
	            	else
	            	{
	            		Toast.makeText(activity, "Failed to retrieve data..." , Toast.LENGTH_SHORT).show();
	            	}
	            	
	            	double elapsed = _timer.GetElapsedSeconds();
	            	 
	            	setTitle("ADN DroidView - " + 
            			ModelSelectActivity.SelectedModel.ModelName +
            			 String.format(" [Response Time: %.2f sec]", elapsed));
	            					
	            	activity.setProgressBarIndeterminateVisibility(false);
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
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnPick(MotionEvent event) 
	{
		float x = event.getX(0);
		float y = event.getY(0);
		
		_renderer.OnPick(x, y);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnDrag(MotionEvent event) 
	{
		float x = event.getX(0);
		float y = event.getY(0);
		
		_renderer.OnDrag(x, y);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnZoom(MotionEvent event) 
	{
		float zoom = _touchManager.CalcDistance(event);
		
		float accZoom = (float)_zoomAccumulator.accumulate(zoom * -0.1);
		
		_renderer.OnZoom(accZoom);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnPointerUp() 
	{
		_renderer.OnZoom((float)_zoomAccumulator.getAccunulatedValue());
		
		_zoomAccumulator.setCurrentValue(0.0);
		
		_renderer.OnPointerUp();
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnEntitySelected(GLBody body) 
	{
		if(body != null)
			_metaDataManager.DisplayMetaData(body.MetaDataId);
		else
			_metaDataManager.ClearMetaData();
	}
}












