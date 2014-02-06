package Autodesk.ADN.Android.DroidView;

import java.io.Serializable;
import java.io.InputStream;
import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class AdnMeshData 
	implements Serializable
{
	static final long serialVersionUID = 7859897975985915024L;
    
	public String Id;
	
	public int FacetCount;
	
	public int VertexCount;
	
	public int[] Color;
	
	public float[] Center;
	          
    public float[] Normals;
    
    public int[] NormalIndices;
   
    public float[] VertexCoords;
    
    public int[] VertexIndices;
    
          
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public boolean Save(Context context, String filename)
    {
    	try
    	{
    		///data/data/Autodesk.ADN.Android.DroidView/files
    		//File file = context.getFilesDir();
    		
		    FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		    ObjectOutputStream os = new ObjectOutputStream(fos);
		    os.writeObject(this);
		    os.close();
		    
		    return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public static AdnMeshData Load(Context context, String filename)
    {
    	try
    	{
    		FileInputStream fis = context.openFileInput(filename);
    		
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		
    		AdnMeshData data = (AdnMeshData) ois.readObject();
    		
    		ois.close();
    		
		    return data;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public static AdnMeshData Load(InputStream is)
    {
    	try
    	{
    		BufferedInputStream bis = new BufferedInputStream(is);
    		
    		ObjectInputStream ois = new ObjectInputStream(bis);
    		
    		AdnMeshData data = (AdnMeshData) ois.readObject();
    		
    		ois.close();
    		
		    return data;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
}