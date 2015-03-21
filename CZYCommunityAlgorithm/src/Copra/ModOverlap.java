package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ModOverlap.java

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class ModOverlap
{

    public ModOverlap()
    {
    }

    public static void main(String args[])
    {
        String s = args[0];
        String s1 = args[1];
        double d = modOverlap(s, s1);
        System.out.println((new StringBuilder()).append(s).append(", ").append(s1).toString());
        System.out.println((new StringBuilder()).append("Modularity is ").append(d).toString());
    }

    public static double modOverlap(String s, String s1)
    {
        int i = 0;
        int j = 0;
        HashMap hashmap = readClusters(s1);
        HashMap hashmap1 = readGraphEdges(s);
        for(Iterator iterator = hashmap1.keySet().iterator(); iterator.hasNext();)
        {
            String s2 = (String)iterator.next();
            j++;
            i += ((HashSet)hashmap1.get(s2)).size();
        }

        i /= 2;
        return modOverlap(hashmap, hashmap1, j, i);
    }

    private static void showClusters(HashMap hashmap, int i, int j)
    {
        System.out.println((new StringBuilder()).append("n = ").append(i).append(", m = ").append(j).toString());
        System.out.println(hashmap);
        System.out.println(invertClusters(hashmap));
    }

    private static HashMap invertClusters(HashMap hashmap)
    {
        HashMap hashmap1 = new HashMap();
        for(Iterator iterator1 = hashmap.keySet().iterator(); iterator1.hasNext();)
        {
            int i = ((Integer)iterator1.next()).intValue();
            Iterator iterator = ((HashMap)hashmap.get(Integer.valueOf(i))).keySet().iterator();
            while(iterator.hasNext()) 
            {
                String s = (String)iterator.next();
                double d = ((Double)((HashMap)hashmap.get(Integer.valueOf(i))).get(s)).doubleValue();
                if(!hashmap1.containsKey(s))
                    hashmap1.put(s, new HashMap());
                ((HashMap)hashmap1.get(s)).put(Integer.valueOf(i), Double.valueOf(d));
            }
        }

        return hashmap1;
    }

    private static HashMap readClusters(String s)
    {
        Object obj = null;
        Object obj1 = null;
        HashMap hashmap1 = new HashMap();
        HashMap hashmap2 = new HashMap();
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
            String s1;
            for(int j = 0; (s1 = bufferedreader.readLine()) != null && !s1.equals(""); j++)
            {
                String as[] = s1.split(" ");
                int k = Array.getLength(as);
                boolean flag;
                if(as[0].endsWith(":"))
                    flag = true;
                else
                    flag = false;
                HashMap hashmap = new HashMap();
                for(int i = ((flag) ? 1 : 0); i < k; i++)
                {
                    String as1[] = as[i].split(":");
                    String s2 = as1[0];
                    double d;
                    if(Array.getLength(as1) == 1)
                        d = 1.0D;
                    else
                        d = Double.parseDouble(as1[1]);
                    hashmap.put(s2, Double.valueOf(d));
                    if(!hashmap1.containsKey(s2))
                        hashmap1.put(s2, Double.valueOf(0.0D));
                    hashmap1.put(s2, Double.valueOf(((Double)hashmap1.get(s2)).doubleValue() + d));
                }

                hashmap2.put(Integer.valueOf(j), hashmap);
            }

            normalize(hashmap2, hashmap1);
        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("Clusters/groups file error: ").append(exception.toString()).toString());
            System.exit(1);
        }
        return hashmap2;
    }

    public static HashMap readGraphEdges(String s)
    {
        HashMap hashmap = new HashMap();
        try
        {
            if((new File(s)).exists())
            {
                BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
                int i = 0;
                do
                {
                    String s1;
                    if((s1 = bufferedreader.readLine()) == null)
                        break;
                    i++;
                    if(!s1.equals("") && !s1.startsWith("#"))
                    {
                        String as[] = s1.split("[ \t]");
                        if(Array.getLength(as) < 2)
                        {
                            System.out.println((new StringBuilder()).append("Missing vertices on line ").append(i).append(" of ").append(s).toString());
                            System.exit(1);
                        }
                        String s2 = as[0];
                        String s3 = as[1];
                        if(s2.equals(s3))
                        {
                            System.out.println((new StringBuilder()).append("Ignoring self edge: ").append(s2).append("/").append(s3).toString());
                        } else
                        {
                            if(hashmap.get(s2) == null)
                                hashmap.put(s2, new HashSet());
                            if(((HashSet)hashmap.get(s2)).add(s3));
                            if(hashmap.get(s3) == null)
                                hashmap.put(s3, new HashSet());
                            ((HashSet)hashmap.get(s3)).add(s2);
                        }
                    }
                } while(true);
                bufferedreader.close();
            } else
            {
                System.out.println((new StringBuilder()).append("Graph file ").append(s).append(" not found").toString());
                System.exit(1);
            }
        }
        catch(Exception exception)
        {
            System.out.println("readGraphEdges error: ");
            System.out.println(exception.toString());
            System.exit(1);
        }
        return hashmap;
    }

    public static void normalize(HashMap hashmap, HashMap hashmap1)
    {
        for(Iterator iterator = hashmap.keySet().iterator(); iterator.hasNext();)
        {
            HashMap hashmap2 = (HashMap)hashmap.get(iterator.next());
            Iterator iterator1 = hashmap2.keySet().iterator();
            while(iterator1.hasNext()) 
            {
                String s = (String)iterator1.next();
                hashmap2.put(s, Double.valueOf(((Double)hashmap2.get(s)).doubleValue() / ((Double)hashmap1.get(s)).doubleValue()));
            }
        }

    }

    public static double modOverlap(HashMap hashmap, HashMap hashmap1, double d, double d1)
    {
        double d4 = 0.0D;
        for(Iterator iterator = hashmap.keySet().iterator(); iterator.hasNext();)
        {
            int i = ((Integer)iterator.next()).intValue();
            Iterator iterator2 = ((HashMap)hashmap.get(Integer.valueOf(i))).keySet().iterator();
            while(iterator2.hasNext()) 
            {
                String s = (String)iterator2.next();
                if(hashmap1.get(s) == null)
                    System.out.println((new StringBuilder()).append("vertex ").append(s).append(" not found in network").toString());
                Iterator iterator5 = ((HashSet)hashmap1.get(s)).iterator();
                while(iterator5.hasNext()) 
                {
                    String s3 = (String)iterator5.next();
                    d4 += F(s, s3, i, hashmap);
                }
            }
        }

        d4 /= d1 + d1;
        double d5 = 0.0D;
        for(Iterator iterator1 = hashmap.keySet().iterator(); iterator1.hasNext();)
        {
            int j = ((Integer)iterator1.next()).intValue();
            Set set = ((HashMap)hashmap.get(Integer.valueOf(j))).keySet();
            HashMap hashmap2 = new HashMap();
            HashMap hashmap3 = new HashMap();
            String s1;
            for(Iterator iterator3 = set.iterator(); iterator3.hasNext(); hashmap3.put(s1, Double.valueOf(beta_in(s1, j, d, hashmap1, hashmap))))
            {
                s1 = (String)iterator3.next();
                hashmap2.put(s1, Double.valueOf(beta_out(s1, j, d, hashmap1, hashmap)));
            }

            Iterator iterator4 = set.iterator();
            while(iterator4.hasNext()) 
            {
                String s2 = (String)iterator4.next();
                Iterator iterator6 = set.iterator();
                while(iterator6.hasNext()) 
                {
                    String s4 = (String)iterator6.next();
                    double d3 = ((Double)hashmap2.get(s2)).doubleValue();
                    double d2 = ((Double)hashmap3.get(s4)).doubleValue();
                    int k = ((HashSet)hashmap1.get(s2)).size();
                    int l = ((HashSet)hashmap1.get(s4)).size();
                    d5 += d3 * d2 * (double)k * (double)l;
                }
            }
        }

        d5 /= 4D * d1 * d1;
        return d4 - d5;
    }

    private static double beta_out(String s, int i, double d, HashMap hashmap, HashMap hashmap1)
    {
        double d2 = 0.0D;
        Set set = ((HashMap)hashmap1.get(Integer.valueOf(i))).keySet();
        if(!set.contains(s))
            return 0.0D;
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = (String)iterator.next();
            if(!s.equals(s1))
                d2 += F(s, s1, i, hashmap1);
        } while(true);
        double d1 = d2 / (d - 1.0D);
        return d1;
    }

    private static double beta_in(String s, int i, double d, HashMap hashmap, HashMap hashmap1)
    {
        double d2 = 0.0D;
        Set set = ((HashMap)hashmap1.get(Integer.valueOf(i))).keySet();
        if(!set.contains(s))
            return 0.0D;
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = (String)iterator.next();
            if(!s.equals(s1))
                d2 += F(s1, s, i, hashmap1);
        } while(true);
        double d1 = d2 / (d - 1.0D);
        return d1;
    }

    private static double F(String s, String s1, int i, HashMap hashmap)
    {
        double d1 = alpha(s, i, hashmap);
        double d2 = alpha(s1, i, hashmap);
        double d = 1.0D / ((1.0D + Math.exp(-f(d1))) * (1.0D + Math.exp(-f(d2))));
        return d;
    }

    private static double f(double d)
    {
        return 60D * d - 30D;
    }

    private static double alpha(String s, int i, HashMap hashmap)
    {
        if(((HashMap)hashmap.get(Integer.valueOf(i))).keySet().contains(s))
            return ((Double)((HashMap)hashmap.get(Integer.valueOf(i))).get(s)).doubleValue();
        else
            return 0.0D;
    }

    static int maxVertex = 0;

}
