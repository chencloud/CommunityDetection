package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   COPRA.java


class Vert
    implements Comparable
{

    public Vert(int i, int j)
    {
        id = i;
        degree = j;
    }

    public int compareTo(Vert vert)
    {
        if(degree < vert.degree)
            return -1;
        if(degree > vert.degree)
            return 1;
        if(id < vert.id)
            return -1;
        return id <= vert.id ? 0 : 1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((Vert)obj);
    }

    public int id;
    public int degree;
}
