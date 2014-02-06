package Autodesk.ADN.Android.DroidView;

import java.util.ArrayList;

public class AdnMetaData 
{
	String Id;
	
	public ArrayList<AdnMetaDataElement> Elements;
}

class AdnMetaDataElement
{
    public String Name;
    public String Category;
    public String Value;
}
