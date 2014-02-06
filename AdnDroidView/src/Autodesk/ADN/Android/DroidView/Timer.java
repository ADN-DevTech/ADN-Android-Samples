package Autodesk.ADN.Android.DroidView;

public class Timer 
{
	private long _startTime;
	
	public Timer()
	{
		
	}
	
	public void Start()
	{
		_startTime = System.currentTimeMillis();
	}
	
	public double GetElapsedSeconds()
	{
		long current = System.currentTimeMillis();
		
		long ms = current - _startTime;
		
		_startTime = current;
		
		return 0.001 * ms;
	}
}
