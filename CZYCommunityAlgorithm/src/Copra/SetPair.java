package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   COPRA.java

import java.util.TreeSet;

class SetPair
    implements Comparable
{

    public SetPair(int i, TreeSet treeset)
    {
        id = i;
        set = treeset;
    }

    public int compareTo(SetPair setpair)
    {
        if(set.size() < setpair.set.size())
            return -1;
        if(set.size() > setpair.set.size())
            return 1;
        if(id < setpair.id)
            return -1;
        return id <= setpair.id ? 0 : 1;
    }

    public int compareTo(Object obj)
    {
        return compareTo((SetPair)obj);
    }

    public int id;
    public TreeSet set;
}
