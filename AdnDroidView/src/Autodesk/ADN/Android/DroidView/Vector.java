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

public class Vector
{
	public static Vector XAxis = new Vector(1, 0, 0);
	public static Vector YAxis = new Vector(0, 1, 0);
	public static Vector ZAxis = new Vector(0, 0, 1);
	
	public Vector()
	{
		X = 0.0f;
		Y = 0.0f;
		Z = 0.0f;
	}
	
	public Vector(Vector v)
	{
		X = v.X;
		Y = v.Y;
		Z = v.Z;
	}
	
	public Vector(float x, float y, float z)
	{
		X = x;
		Y = y;
		Z = z;
	}
	
	public Vector(float[] xyz)
    {
        X = xyz[0];
        Y = xyz[1];
        Z = xyz[2];
    }
	
	public Vector Add(Vector v)
	{
		return new Vector(v.X + X, v.Y + Y, v.Z + Z);
	}
	
	public Vector Substract(Vector v)
	{
		return new Vector(X - v.X, Y - v.Y, Z - v.Z);
	}
	
	public Vector Scale(float factor)
	{
		return new Vector(X * factor, Y * factor, Z * factor);
	}
	
	public float DotProduct (Vector v)
	{
		return ((X * v.X) + (Y * v.Y) + (Z * v.Z));
	}
	
	public Vector CrossProduct (Vector v)
	{
		return new Vector(
			(Y * v.Z) - (Z * v.Y),
			(Z * v.X) - (X * v.Z),
			(X * v.Y) - (Y * v.X));
	}
	
	public void Normalize()
	{
		float norm = (float)Math.sqrt(X * X + Y * Y + Z * Z);
		
		if(norm > 0.0f)
		{
			X /= norm;
			Y /= norm;
			Z /= norm;
		}
	}
	
	public float[] ToArray()
	{
		return new float[] { X, Y, Z };
	}
	
	public float X, Y, Z;
}

