package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   COPRA.java

import java.io.PrintStream;
import java.util.*;

class ClusterLabel
{

    public ClusterLabel(int i, float f)
    {
        initialize(i, f);
    }

    public ClusterLabel(int i, float f, int j, boolean flag)
    {
        initialize(i, f);
        if(flag)
        {
            label.put(Integer.valueOf(j), Float.valueOf(1.0F));
            weight = 1.0F;
        }
    }

    private void initialize(int i, float f)
    {
        label = new TreeMap();
        weight = 0.0F;
        if(f == 0.0F)
            overlap = i;
        else
            overlap = f;
        rand = new Random();
    }

    public Map getLabel()
    {
        return label;
    }

    public float getWeight()
    {
        return weight;
    }

    public TreeSet labelSet()
    {
        return new TreeSet(label.keySet());
    }

    public Map labelMap()
    {
        return label;
    }

    public void add(int i)
    {
        if(label.containsKey(Integer.valueOf(i)))
        {
            System.out.println((new StringBuilder()).append("Trying to add ").append(i).append(" more than once").toString());
        } else
        {
            label.put(Integer.valueOf(i), Float.valueOf(1.0F));
            weight++;
        }
    }

    public boolean sameAs(ClusterLabel clusterlabel)
    {
        Map map = clusterlabel.getLabel();
        for(Iterator iterator = label.keySet().iterator(); iterator.hasNext();)
        {
            int i = ((Integer)iterator.next()).intValue();
            if(!map.containsKey(Integer.valueOf(i)))
                return false;
            if((double)((Float)label.get(Integer.valueOf(i))).floatValue() < (double)((Float)map.get(Integer.valueOf(i))).floatValue() - 0.001D || (double)((Float)label.get(Integer.valueOf(i))).floatValue() > (double)((Float)map.get(Integer.valueOf(i))).floatValue() + 0.001D)
                return false;
        }

        for(Iterator iterator1 = map.keySet().iterator(); iterator1.hasNext();)
        {
            int j = ((Integer)iterator1.next()).intValue();
            if(!label.containsKey(Integer.valueOf(j)))
                return false;
        }

        return true;
    }

    public void neighbour(ClusterLabel clusterlabel, float f)
    {
        if(clusterlabel.getWeight() > 0.0F)
        {
            Map map = clusterlabel.getLabel();
            for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
            {
                int i = ((Integer)iterator.next()).intValue();
                if(label.containsKey(Integer.valueOf(i)))
                    label.put(Integer.valueOf(i), Float.valueOf(((Float)label.get(Integer.valueOf(i))).floatValue() + ((Float)map.get(Integer.valueOf(i))).floatValue() * f));
                else
                    label.put(Integer.valueOf(i), Float.valueOf(((Float)map.get(Integer.valueOf(i))).floatValue() * f));
            }

            weight += f;
        }
    }

    public void noMore()
    {
        reduce();
        normalize();
    }

    private void reduce()
    {
        HashSet hashset = new HashSet();
        float f = (-1.0F / 0.0F);
        float f1 = 0.0F;
        float f2 = 1.0F / overlap;
        for(Iterator iterator = label.keySet().iterator(); iterator.hasNext();)
        {
            int i = ((Integer)iterator.next()).intValue();
            if(((Float)label.get(Integer.valueOf(i))).floatValue() / weight < f2)
            {
                if(((Float)label.get(Integer.valueOf(i))).floatValue() > f)
                    f = ((Float)label.get(Integer.valueOf(i))).floatValue();
                hashset.add(Integer.valueOf(i));
            } else
            {
                f1 += ((Float)label.get(Integer.valueOf(i))).floatValue();
            }
        }

        if(f1 > 0.0F)
        {
            int j;
            for(Iterator iterator1 = hashset.iterator(); iterator1.hasNext(); label.remove(Integer.valueOf(j)))
                j = ((Integer)iterator1.next()).intValue();

            weight = f1;
        } else
        {
            ArrayList arraylist = new ArrayList();
            Iterator iterator2 = hashset.iterator();
            do
            {
                if(!iterator2.hasNext())
                    break;
                int k = ((Integer)iterator2.next()).intValue();
                if(((Float)label.get(Integer.valueOf(k))).floatValue() == f)
                    arraylist.add(Integer.valueOf(k));
            } while(true);
            label.clear();
            int i1 = arraylist.size();
            int l = ((Integer)arraylist.get(rand.nextInt(i1))).intValue();
            label.put(Integer.valueOf(l), Float.valueOf(1.0F));
            weight = 1.0F;
        }
    }

    public void normalize()
    {
        if(weight > 0.0F)
        {
            int i;
            for(Iterator iterator = label.keySet().iterator(); iterator.hasNext(); label.put(Integer.valueOf(i), Float.valueOf(((Float)label.get(Integer.valueOf(i))).floatValue() / weight)))
                i = ((Integer)iterator.next()).intValue();

            weight = 1.0F;
        }
    }

    public String toString()
    {
        String s = "";
        for(Iterator iterator = label.keySet().iterator(); iterator.hasNext();)
        {
            int i = ((Integer)iterator.next()).intValue();
            float f = ((Float)label.get(Integer.valueOf(i))).floatValue();
            if(!"".equals(s))
                s = (new StringBuilder()).append(s).append("/").toString();
            s = (new StringBuilder()).append(s).append(String.format("%d:%.3f", new Object[] {
                Integer.valueOf(i), Float.valueOf(f)
            })).toString();
        }

        return s;
    }

    private Map label;
    private float weight;
    private float overlap;
    private Random rand;
}
