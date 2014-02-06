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

public class Ray
{
	final float EPSILON = 0.000001f;
	
	public Point Origin;

	public Vector Direction;

	public Ray(Point origin, Vector direction)
	{
		Origin = origin;
		
		Direction = direction;
		
		Direction.Normalize();
	}
	
	public Point Intersect(
		Point v1,
		Point v2,
		Point v3)
	{
		Point p = null;
		
		float t = 0.0f;
		float u = 0.0f;
		float v = 0.0f;
		
		Vector vert0 = v1.AsVector();
		Vector vert1 = v2.AsVector();
		Vector vert2 = v3.AsVector();
		
		// Find vectors for two edges sharing vert0.
		Vector edge1 = vert1.Substract(vert0);
		Vector edge2 = vert2.Substract(vert0);
		
		// Begin calculating determinant - also used to calculate U parameter.
		Vector pvec = Direction.CrossProduct(edge2);
		
		// If determinant is near zero, ray lies in plane of triangle.
		float det = edge1.DotProduct(pvec);
		
		if (det > -EPSILON && det < EPSILON)
			return p;
		
		float inv_det = 1.0f / det;
		
		// Calculate distance from vert0 to ray origin.
		Vector tvec = Origin.AsVector().Substract(vert0);
		
		// Calculate U parameter and test bounds.
		u = tvec.DotProduct(pvec) * inv_det;
		
		if (u < 0.0f || u > 1.0f)
			return p;
		
		// Prepare to test V parameter.
		Vector qvec = tvec.CrossProduct(edge1);
		
		// Calculate V parameter and test bounds.
		v = Direction.DotProduct(qvec) * inv_det;
		
		if (v < 0.0f || u + v > 1.0f)
			return p;
		
		// Calculate t, ray intersects triangle.
		t = edge2.DotProduct(qvec) * inv_det;
		
		Vector pos = Origin.AsVector().Add(Direction.Scale(t));
		
		return new Point(pos.X, pos.Y, pos.Z);
	}
}
