package Autodesk.ADN.Android.DroidView;

public class ValueAccumulator 
{
	double _currentValue;
	double _accumulator;
	
	double _minAccValue;
	double _maxAccValue;
	
	public ValueAccumulator(double initialAcc, double minAccValue, double maxAccValue)
	{
		_minAccValue = minAccValue;
		_maxAccValue = maxAccValue;
	
		_currentValue = 0.0;
		_accumulator = initialAcc;
	}   
	
	public double getAccunulatedValue()
	{
		return _accumulator;
	}
	
	public double accumulate(double value)
	{
		double delta = 0;
		
		if(_currentValue == 0.0)
			_currentValue = value;
		else
		{
			delta = value -_currentValue;
			_currentValue = value;
		}
		
		_accumulator += delta;
		
		if(_accumulator < _minAccValue)
			_accumulator = _minAccValue;
		else if(_accumulator > _maxAccValue)
			_accumulator = _maxAccValue;
		
		return _accumulator;
	}
	
	public double getDelta(double value)
	{
		double delta = 0;
		
		if(_currentValue == 0.0)
			_currentValue = value;
		else
		{
			delta = value -_currentValue;
			_currentValue = value;
		}
		
		return delta;
	}
	
	public double setAccumulator(double value)
	{
		_currentValue= 0.0;
		
		_accumulator = value;
			
		if(_accumulator < _minAccValue)
			_accumulator = _minAccValue;
		else if(_accumulator > _maxAccValue)
			_accumulator = _maxAccValue;
		
		return _accumulator;
	}
	
	public void setCurrentValue(double value)
	{
		_currentValue= value;
	}
}
