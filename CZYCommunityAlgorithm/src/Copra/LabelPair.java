package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   COPRA.java


class LabelPair
{

    public LabelPair(float f, int i)
    {
        value = f;
        pos = i;
    }

    public String toString()
    {
        return (new StringBuilder()).append(pos).append("/").append(value).toString();
    }

    public float value;
    public int pos;
}
