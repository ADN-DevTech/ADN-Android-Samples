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

public class Point
{
	public float X, Y, Z;

	public Point()
	{
		X = 0;
		Y = 0;
		Z = 0;
	}

	public Point(float x, float y, float z)
	{
		X = x;
		Y = y;
		Z = z;
	}
	
	public Point(float[] xyz)
    {
        X = xyz[0];
        Y = xyz[1];
        Z = xyz[2];
    }
	
	public void Add(Point point)
	{
		X += point.X;
		Y += point.Y;
		Z += point.Z;
	}
	
	public void Scale(float factor)
	{
		X *= factor;
		Y *= factor;
		Z *= factor;
	}

	public Vector AsVector()
	{
		return new Vector(X, Y, Z);
	}
	
	public float SquaredDistanceTo(Point p)
	{
		return
			(X - p.X) * (X - p.X) +
			(Y - p.Y) * (Y - p.Y) +
			(Z - p.Z) * (Z - p.Z);
	}
}


