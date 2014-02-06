package Autodesk.ADN.Android.DroidView;

import android.view.View;
import java.util.ArrayList;
import java.util.EventListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

//////////////////////////////////////////////////////////////////////////////////////////
//
//
//////////////////////////////////////////////////////////////////////////////////////////
public class TouchManager 
	implements OnTouchListener
{
	private enum TouchState
	{
		kNone, kDrag, kZoom
	}
	
	private TouchState _touchState = TouchState.kNone;
    
    public TouchManager(View view)
    {
        view.setOnTouchListener(this); 
    }
    
    private ArrayList<ITouchListener> _listeners = new ArrayList<ITouchListener>();
	
	public interface ITouchListener extends EventListener 
	{
		public void OnPick(MotionEvent event);
		public void OnDrag(MotionEvent event);
	    public void OnZoom(MotionEvent event);
	    public void OnPointerUp();
	}
	
	Point _posInit = null;
	
	float _pickTolerance = 5;
	
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public void AddEventListener(ITouchListener listener) 
	{
		_listeners.add(listener);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    public void RemoveEventListener(ITouchListener listener) 
    {
        _listeners.remove(listener);
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	void OnPick(MotionEvent event) 
	{   
		for (ITouchListener listener : _listeners) 
		{
			listener.OnPick(event);
		}
	}
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    void OnDrag(MotionEvent event) 
    {   
        for (ITouchListener listener : _listeners) 
        {
            listener.OnDrag(event);
        }
    }

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    void OnZoom(MotionEvent event) 
    {   
        for (ITouchListener listener : _listeners) 
        {
            listener.OnZoom(event);
        }
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
    void OnPointerUp() 
    {   
        for (ITouchListener listener : _listeners) 
        {
            listener.OnPointerUp();
        }
    }
    
	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public boolean onTouch(View arg0, MotionEvent event) 
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
			case MotionEvent.ACTION_DOWN:
			{	
				_posInit = new Point(event.getX(0), event.getY(0), 0);
				
				_touchState = TouchState.kDrag;
				
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN:
			{				
				_touchState = TouchState.kZoom;
				
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				switch(_touchState) 
				{
					case kDrag:
					{			
						OnDrag(event); 
						break;
					}
					case kZoom:
					{	
						OnZoom(event); 
						break;
					}
				}
				
				break;
			}
				
			case MotionEvent.ACTION_UP:
				
				switch(_touchState) 
				{
					case kDrag:
					{			
						float x = _posInit.X - event.getX(0);
						float y = _posInit.Y - event.getY(0);
						
						float dist = (float) Math.sqrt(x * x + y * y);
						
						if(dist < _pickTolerance)
							OnPick(event); 
						
						break;
					}
				}

				 OnPointerUp();
				 
				_touchState = TouchState.kNone;
				
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
				
				OnPointerUp();
				
				_touchState = TouchState.kDrag;
				
				break;
		}
		
		return true;		
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////
	public float CalcDistance(MotionEvent event) 
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		
		return (float) Math.sqrt(x * x + y * y);
	}
}









