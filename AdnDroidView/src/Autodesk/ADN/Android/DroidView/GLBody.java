package Autodesk.ADN.Android.DroidView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import Autodesk.ADN.Android.DroidView.AdnMeshData;
import Autodesk.ADN.Android.DroidView.BoundingBox;
import Autodesk.ADN.Android.DroidView.Point;
import Autodesk.ADN.Android.DroidView.Ray;

//////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////
public class GLBody
{		
	public String MetaDataId;
	
	private short[] _color;
	
	private Point[] _vertices;
		
	private BoundingBox _boundingBox;
	
	private FloatBuffer _vertexBuffer;
	private FloatBuffer _normalBuffer;
	
	public boolean IsSelected = false;
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////
	short[] ConvertClr(int clr)
    {		
        return new short[]
        {
            (short)((clr >> 24) & 0xFF),
            (short)((clr >> 16) & 0xFF),
            (short)((clr >> 8) & 0xFF),
            (short)(clr & 0xFF)
        };
    }
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////
	public GLBody(AdnMeshData data) 
	{
		MetaDataId = data.Id;
		
		_color = ConvertClr(data.Color[0]);
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(data.VertexIndices.length * 3 * Float.SIZE);
		vbb.order(ByteOrder.nativeOrder());
		_vertexBuffer = vbb.asFloatBuffer();
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(data.NormalIndices.length * 3 * Float.SIZE);
		nbb.order(ByteOrder.nativeOrder());
		_normalBuffer = nbb.asFloatBuffer();
		
		int i = 0;
		_vertices = new Point[data.VertexIndices.length];
		
		//uncompress vertices array
        for(int idx : data.VertexIndices)
        {
        	float x = data.VertexCoords[3 * idx];
        	float y = data.VertexCoords[3 * idx + 1];
        	float z = data.VertexCoords[3 * idx + 2];
        	
        	_vertexBuffer.put(x);
        	_vertexBuffer.put(y);
        	_vertexBuffer.put(z);
        	
        	_vertices[i++] = new Point(x, y, z);
        }

        //uncompress normals array
        for(int idx : data.NormalIndices)
        {
        	_normalBuffer.put(data.Normals[3 * idx]);
        	_normalBuffer.put(data.Normals[3 * idx + 1]);
        	_normalBuffer.put(data.Normals[3 * idx + 2]);
        }
						 
		_vertexBuffer.position(0);
		_normalBuffer.position(0);
		
		_boundingBox = new BoundingBox(_vertices);
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////
	public void draw(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CCW); 
		gl.glEnable(GL10.GL_CULL_FACE); 
		gl.glCullFace(GL10.GL_BACK);
	
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT,0, _normalBuffer);
		
		if(IsSelected)
		{
			 gl.glColor4f(
	             (float)0.0f / 255.0f,
	             (float)204.0f / 255.0f,
	             (float)255.0f / 255.0f,
	             (float)255.0f / 255.0f);
		}
		else
		{
			gl.glColor4f(
				(float)_color[0]/255.0f, 
				(float)_color[1]/255.0f, 
				(float)_color[2]/255.0f, 
				(float)_color[3]/255.0f);
		}

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _vertexBuffer.capacity()/3);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glDisable(GL10.GL_CULL_FACE); 
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////
	public Point Intersect(Ray ray)
    {
        Point p = null;

        // Perform intersection test on bounding box first for performances
        if (!_boundingBox.Intersect(ray))
            return p;

        double mindist = Double.POSITIVE_INFINITY;

        for (int idx = 0; idx < _vertices.length; idx += 3)
        {
            Point tmp = null;

            if ((tmp = ray.Intersect(
                _vertices[idx], 
                _vertices[idx + 1], 
                _vertices[idx + 2])) != null)
            {
                double dist = ray.Origin.SquaredDistanceTo(tmp);

                if (dist < mindist)
                {
                    mindist = dist;
                    p = tmp;
                }
            }
        }

        return p;
    }
}