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

public class BoundingBox
{
	private Point[] _vertices = new Point[8];
	
	public BoundingBox(Point[] meshVertices)
	{
		Point min = new Point(
			Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY);
		
		Point max =  new Point(
			Float.NEGATIVE_INFINITY,
			Float.NEGATIVE_INFINITY,
			Float.NEGATIVE_INFINITY);
		
		for(Point vertex : meshVertices)
		{
			if (vertex.X < min.X)
				min.X = vertex.X;
			
			if (vertex.Y < min.Y)
				min.Y = vertex.Y;
			
			if (vertex.Z < min.Z)
				min.Z = vertex.Z;
			
			if (vertex.X > max.X)
				max.X = vertex.X;
			
			if (vertex.Y > max.Y)
				max.Y = vertex.Y;
			
			if (vertex.Z > max.Z)
				max.Z = vertex.Z;
		}
		
		_vertices[0] = min;
		_vertices[1] = new Point(min.X, min.Y, max.Z);
		_vertices[2] = new Point(max.X, min.Y, max.Z);
		_vertices[3] = new Point(max.X, min.Y, min.Z);
		
		_vertices[4] = max;
		_vertices[5] = new Point(min.X, max.Y, max.Z);
		_vertices[6] = new Point(min.X, max.Y, min.Z);
		_vertices[7] = new Point(max.X, max.Y, min.Z);
	}
	
	public boolean Intersect(Ray ray)
	{ 
		if (ray.Intersect(_vertices[0], _vertices[1], _vertices[2]) != null)
		return true;
		
		if (ray.Intersect(_vertices[2], _vertices[3], _vertices[0]) != null)
			return true;
		
		if (ray.Intersect(_vertices[1], _vertices[5], _vertices[4]) != null)
			return true;
		
		if (ray.Intersect(_vertices[4], _vertices[2], _vertices[1]) != null)
			return true;
		
		if (ray.Intersect(_vertices[3], _vertices[2], _vertices[4]) != null)
			return true;
		
		if (ray.Intersect(_vertices[4], _vertices[7], _vertices[3]) != null)
			return true;
		
		if (ray.Intersect(_vertices[0], _vertices[7], _vertices[7]) != null)
			return true;
		
		if (ray.Intersect(_vertices[7], _vertices[0], _vertices[6]) != null)
			return true;
		
		if (ray.Intersect(_vertices[0], _vertices[1], _vertices[6]) != null)
			return true;
		
		if (ray.Intersect(_vertices[1], _vertices[5], _vertices[6]) != null)
			return true;
		
		if (ray.Intersect(_vertices[4], _vertices[7], _vertices[6]) != null)
			return true;
		
		if (ray.Intersect(_vertices[6], _vertices[5], _vertices[4]) != null)
			return true;
		
			return false;
	}
}

