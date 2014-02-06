package Autodesk.ADN.Android.DroidView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class ModelRenderer 
	implements GLSurfaceView.Renderer 
{
	GL10 _gl;
	
	Activity _activity;
	Context _context;
	
	float _xPos;
	float _yPos;
	
	float _xAngle;
	float _yAngle;
	
	float _zoom;
	
	int[] _viewport = new int[4];
    float[] _modelview = new float[16];
	float[] _projection = new float[16];
	
	GLModelBase _currentModel;
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private ArrayList<ISelectionListener> _listeners = new ArrayList<ISelectionListener>();
	
	public interface ISelectionListener extends EventListener 
	{
		public void OnEntitySelected(GLBody body);
	}
	
	boolean _onlyPick;
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void AddEventListener(ISelectionListener listener) 
	{
		_listeners.add(listener);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public void RemoveEventListener(ISelectionListener listener) 
    {
        _listeners.remove(listener);
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	void OnEntitySelected(GLBody body) 
	{   
		for (ISelectionListener listener : _listeners) 
		{
			listener.OnEntitySelected(body);
		}
	}
		
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public ModelRenderer(
		Activity activity,
		Context context)
	{
		_context = context;
		
		LoadIdleModel();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	private void ResetView()
	{
		_zoom = 100.0f;
		
		_xPos = -1.0f;
		_yPos = -1.0f;
		
		_xAngle = 180.0f;
		_yAngle = 0.0f;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void LoadModel(AdnMeshData[] data)
    {
		ResetView();
		
		_currentModel = new GLModel(_context, data);
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void LoadIdleModel()
    {
		try 
		{
			ResetView();
			
			AssetManager assetMng = _context.getAssets();
			
			InputStream is = assetMng.open("droidview.mesh");
			
			_currentModel = null;
			
			/*AdnMeshData data = AdnMeshData.Load(is);
			
			_currentModel = new GLIdleModel(
				_context, 
				new AdnMeshData[]{data});*/
			
			is.close();
		} 
		catch (Exception e) 
		{
			_currentModel = null;
		}
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnPick(float x, float y)
	{
		GLModel model = (GLModel) _currentModel;
		
		GLBody body = model.CheckSelection(
			(GL11)_gl, 
			_viewport, 
			_modelview,
			_projection,
			x, y);
		
		if(body != null)
		{
			if(body.IsSelected)
				return;
			
			model.Select(body);
			
			OnEntitySelected(body); 
		}
		else
		{
			model.UnSelectAll();
			
			OnEntitySelected(null); 
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnDrag(float x, float y)
    {
		if(_xPos < 0 && _xPos < 0)
		{
			_xPos = x;
			_yPos = y;
		}
		
		_xAngle += y - _yPos;
		_yAngle += x - _xPos;
		
		_xAngle = (float) (_xAngle % 360);
		_yAngle = (float) (_yAngle % 360);
				
		_xPos = x;
		_yPos = y;
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnZoom(float zoom)
    {
		_zoom = zoom;
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void OnPointerUp()
    {
		_xPos = -1.0f;
		_yPos = -1.0f;
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void onDrawFrame(GL10 gl) 
	{
		gl.glClear(
			GL10.GL_COLOR_BUFFER_BIT | 
			GL10.GL_DEPTH_BUFFER_BIT | 
			GL10.GL_STENCIL_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		Quaternion qx = new Quaternion(Vector.XAxis, _xAngle);
		Quaternion qy = new Quaternion(Vector.YAxis, _yAngle);
		
		Quaternion qres = qx.Multiply(qy);
		
		Point camPos = new Point(0, 0, 80 - _zoom);
		Point right = new Point(1, 0, 0 );
		
		Vector newPos = qres.Transform(camPos).AsVector();
		Vector newRight = qres.Transform(right).AsVector();
		
		Vector up = newPos.CrossProduct(newRight);
		
		GLU.gluLookAt(
			gl,
		    (float)newPos.X, 
		    (float)newPos.Y, 
		    (float)newPos.Z,
		    (float)0, 
		    (float)0, 
		    (float)0,
		    (float)up.X, 
		    (float)up.Y, 
		    (float)up.Z);
		
		UpdateMatrix((GL11) gl); 
		
		if(_currentModel != null)
			_currentModel.draw(gl);
		
		gl.glFlush();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void UpdateMatrix(GL11 gl11) 
	{
		gl11.glGetIntegerv(GL11.GL_VIEWPORT, _viewport, 0);		
		gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, _modelview, 0);
		gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, _projection, 0);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// Reset the projection matrix
		gl.glLoadIdentity();
		
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 1.0f, 200.0f);
		
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		// Reset the modelview matrix
		gl.glLoadIdentity();
		
		UpdateMatrix((GL11) gl); 
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{				
		_gl = gl;
		
		gl.glClearColor(34.0f/255.0f, 0.0f/255.0f, 119.0f/255.0f, 0.5f);  
		
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glEnable(GL10.GL_NORMALIZE);
		
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);


		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, 
				new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, 
				new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, 
				new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, 
				new float[] { 1, 1, 0, 1 }, 0);

         gl.glClearStencil(0);
         
         if(_currentModel != null)
        	 _currentModel.Initialize(gl, _context);
	}
}
