package Autodesk.ADN.Android.DroidView;

import android.content.Context;
import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class GLModel 
	extends GLModelBase
{
	private GLBody[] _bodies;
	
	private Point _center;
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public GLModel(Context context, AdnMeshData[] data) 
	{
		super(context);
		
		_center = new Point();
		
		_bodies = new GLBody[data.length];
		
		for (int i=0; i<data.length; ++i) 
        {
			AdnMeshData d = data[i];
			
			_bodies[i] = new GLBody(d);
			
			_center.X += d.Center[0];
			_center.Y += d.Center[1];
			_center.Z += d.Center[2];
        }
		
		_center.X /= data.length;
		_center.Y /= data.length;
		_center.Z /= data.length;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize(GL10 gl, Context context) 
	{
		super.Initialize(gl, context);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void draw(GL10 gl)
	{
		super.draw(gl);
		
		gl.glTranslatef(-_center.X, -_center.Y, -_center.Z);
		
		for (GLBody body : _bodies) 
        {
            body.draw(gl);
        }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void Select(GLBody selectedBody)
	{
		for (GLBody body : _bodies) 
		{
			body.IsSelected = false;
		}
		
		selectedBody.IsSelected = true;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void UnSelectAll()
	{
		for (GLBody body : _bodies) 
		{
			body.IsSelected = false;
		}
	}
		
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public GLBody Intersect(Ray ray)
    {
        double mindist = Double.POSITIVE_INFINITY;

        Point p = null;

        GLBody closestBody = null;

        for(GLBody body : _bodies)
        {
            if ((p = body.Intersect(ray)) != null)
            {
                double dist = ray.Origin.SquaredDistanceTo(p);

                if (dist < mindist)
                {
                    mindist = dist;
                    closestBody = body;
                }
            }
        }

        return closestBody;
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public GLBody CheckSelection(
		GL11 gl, 
		int[] viewport, 
		float[] modelview,
		float[] projection,
		float x, float y)
    {
        y = viewport[3] - y;

        Vector res1 = UnProject(gl, viewport, modelview, projection, x, y, -1);
        Vector res2 = UnProject(gl, viewport, modelview, projection, x, y, 1);

        res1 = res1.Add(_center.AsVector());
        res2 = res2.Add(_center.AsVector());
        
        Point origin = new Point(res1.ToArray());
        
        Vector direction = res2.Substract(res1);

        Ray ray = new Ray(origin, direction);

        return Intersect(ray);
    }	
    
    public Vector UnProject(
		GL11 gl, 
		int[] viewport, 
		float[] modelview,
		float[] projection,
		float winX, 
		float winY, 
		float winZ)
	{
        float[] fresult = new float[4];
        
        GLU.gluUnProject(
			winX, winY, winZ, 
			modelview, 0,
			projection,	0,
			viewport, 0,
			fresult, 0);
       
       Vector result = new Vector(fresult);
       
       return result.Scale(1.0f/fresult[3]);
	}
}







