package Autodesk.ADN.Android.DroidView;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class AdnDbModel
{
	public int IconRes;
	public int DocType;
	public String ModelId;
	public String ModelName;
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////    
    public void SetIcon()
    {
    	switch(DocType)
		{
		case 0:
			IconRes = R.drawable.part;
			break;
			
		case 1:
			IconRes = R.drawable.assembly;
			break;
			
		case 2:
			IconRes = R.drawable.acad;
			break;
			
		case 3:
			IconRes = R.drawable.revit;
			break;
			
		default:
			break;
		}
    }
}
