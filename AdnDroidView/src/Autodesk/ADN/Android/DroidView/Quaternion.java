///////////////////////////////////////////////////////////////////////////////
//Copyright (c) Autodesk, Inc. All rights reserved 
//Written by Philippe Leefsma 2012 - ADN/Developer Technical Services
//
//Permission to use, copy, modify, and distribute this software in
//object code form for any purpose and without fee is hereby granted, 
//provided that the above copyright notice appears in all copies and 
//that both that copyright notice and the limited warranty and
//restricted rights notice below appear in all supporting 
//documentation.
//
//AUTODESK PROVIDES THIS PROGRAM "AS IS" AND WITH ALL FAULTS. 
//AUTODESK SPECIFICALLY DISCLAIMS ANY IMPLIED WARRANTY OF
//MERCHANTABILITY OR FITNESS FOR A PARTICULAR USE.  AUTODESK, INC. 
//DOES NOT WARRANT THAT THE OPERATION OF THE PROGRAM WILL BE
//UNINTERRUPTED OR ERROR FREE.
///////////////////////////////////////////////////////////////////////////////
package Autodesk.ADN.Android.DroidView;

public class Quaternion
{
	float _x;
	float _y;
	float _z;
	float _w;
	
	public Quaternion()
	{
		_x = _y = _z = 0.0f;
		_w = 1.0f;
	}
	
	public Quaternion(Vector axis, float degrees)
	{
		// First we want to convert the degrees to radians 
		// since the angle is assumed to be in radians
		float angle = (float)((degrees / 180.0) * Math.PI);
		
		// Here we calculate the sin( theta / 2) once for optimization
		float result = (float)Math.sin(angle * 0.5);
		
		// Calcualte the w value by cos( theta / 2 )
		_w = (float)Math.cos(angle * 0.5);
		
		// Calculate the x, y and z of the quaternion
		_x = axis.X * result;
		_y = axis.Y * result;
		_z = axis.Z * result;
	}
	
	public Quaternion Clone()
	{ 
		Quaternion q = new Quaternion();
		
		q._w = _w;
		q._x = _x;
		q._y = _y;
		q._z = _z;
		
		return q;
	}
	
	public float Angle()
	{
		return (float)(2 * Math.acos(_w));
	}
	
	public float[] Axis()
	{
		float sin = (float)Math.sin(Angle() * 0.5);
		
		return new float[]
		{
			_x / sin, 
			_y / sin, 
			_z / sin
		};
	}
	
	public void Normalise()
	{
		float norm = _w * _w + _x * _x + _y * _y + _z * _z;
		
		if (norm > 0.00001)
		{
			norm = (float)Math.sqrt(norm);
			
			_w /= norm;
			_x /= norm;
			_y /= norm;
			_z /= norm;
		}
		else
		{
			_w = 1.0f; _x = 0.0f; _y = 0.0f; _z = 0.0f;
		}
	}
	
	public Quaternion Multiply(Quaternion q)
	{
		Quaternion r = new Quaternion();
		
		r._w = _w * q._w - _x * q._x - _y * q._y - _z * q._z;
		r._x = _w * q._x + _x * q._w + _y * q._z - _z * q._y;
		r._y = _w * q._y + _y * q._w + _z * q._x - _x * q._z;
		r._z = _w * q._z + _z * q._w + _x * q._y - _y * q._x;
		
		return(r);
	}
	
	public Quaternion Invert()
	{
		Quaternion qinv = new Quaternion();
		
		qinv._w = _w;
		qinv._x = -_x;
		qinv._y = -_y;
		qinv._z = -_z;
		
		return qinv;
	}
	
	public float[] ToMatrix()
	{
		float[] m = new float[16];
		
		// First row
		m[0] = 1.0f - 2.0f * ( _y * _y + _z * _z ); 
		m[1] = 2.0f * (_x * _y + _z * _w);
		m[2] = 2.0f * (_x * _z - _y * _w);
		m[3] = 0.0f;  
		
		// Second row
		m[4] = 2.0f * ( _x * _y - _z * _w );  
		m[5] = 1.0f - 2.0f * ( _x * _x + _z * _z ); 
		m[6] = 2.0f * (_z * _y + _x * _w );  
		m[7] = 0.0f;  
		
		// Third row
		m[8] = 2.0f * ( _x * _z + _y * _w );
		m[9] = 2.0f * ( _y * _z - _x * _w );
		m[10] = 1.0f - 2.0f * ( _x * _x + _y * _y );  
		m[11] = 0.0f;  
		
		// Fourth row
		m[12] = 0;  
		m[13] = 0;  
		m[14] = 0;  
		m[15] = 1.0f;
		
		return m;
	}
	
	public Point Transform(Point point)
	{
		float[] m = ToMatrix();
		
		return new Point(
			m[0] * point.X + m[1] * point.Y + m[2] * point.Z,
			m[4] * point.X + m[5] * point.Y + m[6] * point.Z,
			m[8] * point.X + m[9] * point.Y + m[10] * point.Z);
	}
}


