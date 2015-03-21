package Copra;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   COPRA.java

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.export.ExportFile;
import edu.czy.load.LoadGML;
import edu.uci.ics.jung.graph.SparseGraph;

public class COPRA
{

    public COPRA()
    {
    }

    public void run(String args[])
    {
        String s = "clusters-";
        String s1 = "clusters1-";
        String s2 = "clusters2-";
        Vector vector = null;
        Object obj = null;
        boolean flag = true;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean flag5 = false;
        int i = 0;
        float f = 1.0F;
        float f1 = 1.0F;
        int j = 0x7fffffff;
        int k = 1;
        int l = 0;
        int i1 = 1;
        int j1 = 1;
        int i2 = Array.getLength(args);
        if(i2 < 1)
            printUsageAndExit();
        String s4 = args[0];
        String s5 = (new File(s4)).getName();
        String s6 = (new StringBuilder()).append(s).append(s5).toString();
        String s7 = (new StringBuilder()).append(s).append(s5).toString();
        String s8 = null;
        String s9 = "";
        String s10 = "";
        String s11 = "";
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        HashMap hashmap = new HashMap();
        ArrayList arraylist2 = arraylist;
        ArrayList arraylist3 = arraylist1;
        HashMap hashmap1 = hashmap;
        ArrayList arraylist4 = null;
        while(j1 < i2) 
        {
            String s3 = args[j1++];
            if("-prop".equals(s3))
                j = Integer.parseInt(args[j1++]);
            else
            if("-v".equals(s3))
            {
                f = Float.parseFloat(args[j1++]);
                f1 = f;
            } else
            if("-vs".equals(s3))
            {
                f = Float.parseFloat(args[j1++]);
                f1 = Float.parseFloat(args[j1++]);
            } else
            if("-term".equals(s3))
                k = Integer.parseInt(args[j1++]);
            else
            if("-repeat".equals(s3))
            {
                String args1[] = args[j1++].split("-");
                if(Array.getLength(args1) == 1)
                {
                    l = 0;
                    i1 = Integer.parseInt(args1[0]);
                } else
                {
                    l = Integer.parseInt(args1[0]);
                    i1 = Integer.parseInt(args1[1]);
                }
            } else
            if("-nosplit".equals(s3))
                flag = false;
            else
            if("-extrasimplify".equals(s3))
                flag1 = true;
            else
            if("-bi".equals(s3))
                flag2 = true;
            else
            if("-w".equals(s3))
                flag3 = true;
            else
            if("-mo".equals(s3))
                flag4 = true;
            else
            if("-q".equals(s3))
                flag5 = true;
            else
            if("-stats".equals(s3))
                i = Integer.parseInt(args[j1++]);
            else
                printUsageAndExit();
        }
        if(j < 1 || f < 1.0F)
            printUsageAndExit();
        if(flag2)
        {
            String s12 = s4.substring(0, s4.lastIndexOf('.'));
            s10 = (new StringBuilder()).append(s12).append("-1.txt").toString();
            s11 = (new StringBuilder()).append(s12).append("-2.txt").toString();
            s7 = (new StringBuilder()).append(s1).append(s5).toString();
            s8 = (new StringBuilder()).append(s2).append(s5).toString();
            arraylist3 = new ArrayList();
            hashmap1 = new HashMap();
            arraylist2 = new ArrayList();
        }
        if(flag3)
            arraylist4 = new ArrayList();
        int j2 = readBiGraphEdges(s4, arraylist, arraylist2, arraylist1, arraylist3, hashmap, hashmap1, arraylist4);
        if(!flag5)
            showOptions(arraylist.size(), arraylist2.size(), j2, flag2, arraylist4, f, f1, j, i1, flag, flag1, flag4, s4, s10, s11);
        try
        {
            for(float f2 = f; f2 <= f1; f2++)
            {
                double d = 0.0D;
                double d1 = 0.0D;
                double d2 = 0.0D;
                double d3 = 0.0D;
                double d4 = 0.0D;
                double d5 = 0.0D;
                double d6 = (-1.0D / 0.0D);
                double d7 = (-1.0D / 0.0D);
                String s13 = (new StringBuilder()).append("best-").append(s6).toString();
                Vector vector2 = null;
                for(int k1 = 0; k1 < l; k1++)
                    clusterGraph1(j, s4, arraylist, arraylist2, arraylist1, arraylist3, arraylist4, s6, s7, s8, f2, k, flag, flag1, null, i);

                for(int l1 = 0; l1 < i1; l1++)
                {
                    VecPair vecpair = clusterGraph1(j, s4, arraylist, arraylist2, arraylist1, arraylist3, arraylist4, s6, s7, s8, f2, k, flag, flag1, null, i);
                    Vector vector1 = vecpair.value;
                    vector = vecpair.name;
                    if(flag4)
                        if(flag2)
                        {
                            d2 = ModOverlap.modOverlap(s10, s7);
                            d5 = ModOverlap.modOverlap(s11, s8);
                        } else
                        {
                            d2 = ModOverlap.modOverlap(s4, s6);
                        }
                    report(((Double)vector1.get(2)).doubleValue(), ((Double)vector1.get(3)).doubleValue(), ((Double)vector1.get(4)).doubleValue(), ((Double)vector1.get(5)).doubleValue(), d2, d5, ((Double)vector1.get(0)).doubleValue(), ((Double)vector1.get(1)).doubleValue(), flag2, flag4);
                    d += d2;
                    d1 += d2 * d2;
                    d3 += d5;
                    d4 += d5 * d5;
                    vector2 = sumRes(vector2, vector1, 0);
                    if(d2 > d6 && i1 > 1)
                    {
                        d6 = d2;
                        d7 = d5;
                        copyFile(s6, s13);
                    }
                }

                if(i1 > 1)
                {
                    System.out.println(separator);
                    System.out.println((new StringBuilder()).append("v=").append(f2).toString());
                    if(flag4)
                    {
                        double d8 = d / (double)i1;
                        double d10 = stdDev(d1, i1, d8);
                        String s14 = String.format("Modularity: best = %.3f", new Object[] {
                            Double.valueOf(d6)
                        });
                        String s15 = String.format(", average = %.3f+-%.3f", new Object[] {
                            Double.valueOf(d8), Double.valueOf(d10)
                        });
                        if(flag2)
                        {
                            double d9 = d3 / (double)i1;
                            double d11 = stdDev(d4, i1, d9);
                            s14 = (new StringBuilder()).append(s14).append(String.format("/%.3f", new Object[] {
                                Double.valueOf(d7)
                            })).toString();
                            s15 = (new StringBuilder()).append(s15).append(String.format("/%.3f+-%.3f", new Object[] {
                                Double.valueOf(d9), Double.valueOf(d11)
                            })).toString();
                        }
                        System.out.println((new StringBuilder()).append(s14).append(s15).toString());
                    }
                    reportRes(vector2, vector, i1);
                    System.out.println(separator);
                }
            }

        }
        catch(Exception exception)
        {
            System.err.println((new StringBuilder()).append("COPRA error: ").append(exception.toString()).toString());
            exception.printStackTrace();
        }
    }

    private static void showOptions(int i, int j, int k, boolean flag, List list, float f, float f1, int l, 
            int i1, boolean flag1, boolean flag2, boolean flag3, String s, String s1, String s2)
    {
        System.out.println(welcome1);
        System.out.println(welcome2);
        System.out.println(welcome1);
        System.out.println((new StringBuilder()).append("  Network file = ").append(s).toString());
        System.out.print("  Network is ");
        if(list != null)
            System.out.print("weighted, ");
        else
            System.out.print("unweighted, ");
        if(flag)
        {
            System.out.println("bipartite");
            System.out.println((new StringBuilder()).append("  ").append(i).append("/").append(j).append(" vertices, ").append(k).append(" edges").toString());
        } else
        {
            System.out.println("unipartite");
            System.out.println((new StringBuilder()).append("  ").append(i).append(" vertices, ").append(k).append(" edges").toString());
        }
        if(list != null && flag)
        {
            System.out.println("  Weighted bipartite not implemented");
            System.exit(1);
        }
        if(list != null)
        {
            float f2 = 3.402823E+038F;
            float f3 = 1.401298E-045F;
label0:
            for(int j1 = 0; j1 < list.size(); j1++)
            {
                Iterator iterator = ((HashMap)list.get(j1)).keySet().iterator();
                do
                {
                    if(!iterator.hasNext())
                        continue label0;
                    float f4 = ((Float)((HashMap)list.get(j1)).get(iterator.next())).floatValue();
                    if(f4 < f2)
                        f2 = f4;
                    if(f4 > f3)
                        f3 = f4;
                } while(true);
            }

            if(f2 < f3)
                System.out.println((new StringBuilder()).append("  Weights in ").append(s).append(" range from ").append(f2).append(" to ").append(f3).toString());
            else
                System.out.println((new StringBuilder()).append("  Warning: network in ").append(s).append(" is unweighted").toString());
        }
        System.out.print((new StringBuilder()).append("  v = ").append(f).toString());
        if(f < f1)
            System.out.println((new StringBuilder()).append(",...,").append(f1).toString());
        else
            System.out.println();
        if(l < 0x7fffffff)
            System.out.println((new StringBuilder()).append("  Number of iterations limited to ").append(l).toString());
        if(i1 > 1)
            System.out.println((new StringBuilder()).append("  Repeat ").append(i1).append(" times and show averages").toString());
        if(!flag1)
            System.out.println("  Do not split discontiguous communities");
        if(flag2)
            System.out.println("  Simplify communities again after splitting");
        if(flag3)
        {
            System.out.print("  Compute modularity wrt ");
            if(flag)
                System.out.println((new StringBuilder()).append("projections: ").append(s1).append("/").append(s2).toString());
            else
                System.out.println(s);
        }
        System.out.println(separator);
    }

    public static VecPair clusterGraph(int i, String s, String s1, String s2, String s3, float f, int j, boolean flag, 
            boolean flag1, String s4, int k, boolean flag2, boolean flag3)
    {
        if(flag3)
            System.exit(1);
        VecPair vecpair = clusterGraph(i, s, s1, s2, s3, f, j, flag, flag1, s4, k, flag2);
        return vecpair;
    }

    public static VecPair clusterGraph(int i, String s, String s1, String s2, String s3, float f, int j, boolean flag, 
            boolean flag1, String s4, int k, boolean flag2)
    {
        boolean flag3 = s3 != null;
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        HashMap hashmap = new HashMap();
        ArrayList arraylist2 = arraylist;
        ArrayList arraylist3 = arraylist1;
        HashMap hashmap1 = hashmap;
        ArrayList arraylist4 = null;
        if(flag3)
        {
            arraylist3 = new ArrayList();
            hashmap1 = new HashMap();
            arraylist2 = new ArrayList();
        }
        if(flag2)
            arraylist4 = new ArrayList();
        readBiGraphEdges(s, arraylist, arraylist2, arraylist1, arraylist3, hashmap, hashmap1, arraylist4);
        return clusterGraph1(i, s, arraylist, arraylist2, arraylist1, arraylist3, arraylist4, s1, s2, s3, f, j, flag, flag1, s4, k);
    }

    private static VecPair clusterGraph1(int i, String s, List list, List list1, List list2, List list3, List list4, String s1, 
            String s2, String s3, float f, int j, boolean flag, boolean flag1, String s4, 
            int k)
    {
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        long l5 = 0L;
        long l6 = 0L;
        boolean flag2 = s3 != null;
        Map map5 = null;
        Object obj = new HashMap();
        Object obj1 = new HashMap();
        List list5 = null;
        List list6 = null;
        boolean flag3 = k > 0 && !flag2;
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        Vector vector2 = new Vector();
        Vector vector3 = new Vector();
        boolean flag4 = true;
        boolean flag5 = false;
        Object obj2 = new HashMap();
        Object obj3 = new HashMap();
        long l1 = (new Date()).getTime();
        int j1;
        byte byte0;
        if(flag2)
        {
            if(list.size() >= list1.size())
            {
                j1 = list.size();
                byte0 = 1;
                list5 = simpleClustering(j1, f);
            } else
            {
                j1 = list1.size();
                byte0 = 2;
                list6 = simpleClustering(j1, f);
            }
        } else
        {
            j1 = list.size();
            byte0 = 0;
            list5 = simpleClustering(j1, f);
        }
        int l;
        for(l = 0; l < i; l++)
        {
            if(byte0 == 1)
            {
                list6 = propagateClusters(list5, list1, list4, j1, f);
                list5 = propagateClusters(list6, list, list4, j1, f);
            } else
            if(byte0 == 2)
            {
                list5 = propagateClusters(list6, list, list4, j1, f);
                list6 = propagateClusters(list5, list1, list4, j1, f);
            } else
            {
                list6 = propagateClusters(list5, list, list4, j1, f);
                list5 = list6;
            }
            long l2 = (new Date()).getTime();
            if(j == 1)
            {
                Map map = clusteringSummary1(list5);
                map5 = minSummary(((Map) (obj1)), map);
                if(flag2)
                {
                    Map map2 = clusteringSummary1(list6);
                    obj = minSummary(((Map) (obj3)), map2);
                }
            } else
            if(j == 2)
            {
                Map map1 = clusteringSummary2(list5);
                map5 = minSummary(((Map) (obj1)), map1);
                if(flag2)
                {
                    Map map3 = clusteringSummary2(list6);
                    obj = minSummary(((Map) (obj3)), map3);
                }
            } else
            if(flag3)
            {
                if(!flag4)
                {
                    Map map6 = clusteringSummary1(list5);
                    Map map7 = minSummary(((Map) (obj2)), map6);
                    if(map7.equals(obj2))
                    {
                        System.out.println((new StringBuilder()).append("Termination 1 at ").append(l).toString());
                        d = l;
                        outputClusters(list5, list6, s1, s2, s3, list, list1, list2, list3, flag, flag1);
                        d2 = ModOverlap.modOverlap(s, s2);
                        System.out.println((new StringBuilder()).append(l).append(": ").append(d2).toString());
                        flag4 = true;
                    }
                    obj2 = map7;
                }
                if(!flag5)
                {
                    Map map4 = clusteringSummary2(list5);
                    obj = minSummary(((Map) (obj3)), map4);
                    if(obj.equals(obj3))
                    {
                        System.out.println((new StringBuilder()).append("Termination 2 at ").append(l).toString());
                        d1 = l;
                        outputClusters(list5, list6, s1, s2, s3, list, list1, list2, list3, flag, flag1);
                        d3 = ModOverlap.modOverlap(s, s2);
                        System.out.println((new StringBuilder()).append(l).append(": ").append(d3).toString());
                        flag5 = true;
                    }
                    obj3 = obj;
                }
            }
            if(j >= 1 && map5.equals(obj1) && obj.equals(obj3))
            {
                long l3 = (new Date()).getTime();
                l2 = l3 - l2;
                l5 += l2;
                break;
            }
            long l4 = (new Date()).getTime();
            l2 = l4 - l2;
            l5 += l2;
            obj1 = map5;
            obj3 = obj;
            if(flag3 && l == k)
            {
                outputClusters(list5, list6, s1, s2, s3, list, list1, list2, list3, flag, flag1);
                double d5 = ModOverlap.modOverlap(s, s2);
                System.out.println((new StringBuilder()).append(l).append(": ").append(d5).toString());
                vector2.add(Double.valueOf(d5));
                vector3.add(Integer.valueOf(l));
                k += Math.max(l / 10, 1);
            }
        }

        l1 = (new Date()).getTime() - l1;
        Vector vector4 = outputClusters(list5, list6, s1, s2, s3, list, list1, list2, list3, flag, flag1);
        if(flag2)
        {
            vector.add(Double.valueOf(((Double)vector4.get(2)).doubleValue() / (double)list.size()));
            vector1.add("Overlap mode 1");
            vector.add(Double.valueOf(((Double)vector4.get(3)).doubleValue() / (double)list1.size()));
            vector1.add("Overlap mode 2");
        } else
        {
            vector.add(Double.valueOf(((Double)vector4.get(2)).doubleValue() / (double)list.size()));
            vector1.add("Overlap");
            vector.add(Double.valueOf(0.0D));
            vector1.add("Unused");
        }
        vector.add(vector4.get(0));
        vector1.add("Communities");
        vector.add(vector4.get(1));
        vector1.add("Non-singleton communities");
        vector.add(Double.valueOf(l));
        vector1.add("Iterations");
        vector.add(Double.valueOf((double)l1 + ((Double)vector4.get(4)).doubleValue()));
        vector1.add("Total time");
        vector.add(Double.valueOf(l5));
        vector1.add("Termination check time (included in total)");
        vector.add(vector4.get(4));
        vector1.add("Simplification time (included in total)");
        vector.add(Double.valueOf(list.size()));
        vector1.add("Vertices");
        vector.add(Double.valueOf(nEdges(list)));
        vector1.add("Edges");
        if(flag3)
        {
            vector.add(Double.valueOf(d));
            vector1.add("Termination 1");
            vector.add(Double.valueOf(d1));
            vector1.add("Termination 2");
            vector.add(Double.valueOf(d2));
            vector1.add("Termination 1 modOverlap");
            vector.add(Double.valueOf(d3));
            vector1.add("Termination 2 modOverlap");
            for(int i1 = 0; i1 < vector2.size(); i1++)
            {
                vector.add(vector2.get(i1));
                vector1.add((new StringBuilder()).append(vector3.get(i1)).append("").toString());
            }

            vector2.clear();
        }
        return new VecPair(vector1, vector);
    }

    private static int nEdges(List list)
    {
        int j = 0;
        for(int i = 0; i < list.size(); i++)
            j += ((HashSet)list.get(i)).size();

        return j / 2;
    }

    private static double stdDev(double d, int i, double d1)
    {
        double d2 = d / (double)i - d1 * d1;
        double d3 = 0.0D;
        if(d2 < 0.0D)
            d3 = 0.0D;
        else
            d3 = Math.sqrt(d2);
        return d3;
    }

    private static Map minSummary(Map map, Map map1)
    {
        if(map.keySet().equals(map1.keySet()))
        {
            Iterator iterator = map.keySet().iterator();
            HashMap hashmap = new HashMap();
            int i;
            for(; iterator.hasNext(); hashmap.put(Integer.valueOf(i), Float.valueOf(Math.min(((Float)map.get(Integer.valueOf(i))).floatValue(), ((Float)map1.get(Integer.valueOf(i))).floatValue()))))
                i = ((Integer)iterator.next()).intValue();

            return hashmap;
        } else
        {
            return map1;
        }
    }

    private static Map clusteringSummary1(List list)
    {
        HashMap hashmap = new HashMap();
        for(int i = 0; i < list.size(); i++)
        {
            for(Iterator iterator = ((ClusterLabel)list.get(i)).labelSet().iterator(); iterator.hasNext();)
            {
                int j = ((Integer)iterator.next()).intValue();
                if(hashmap.containsKey(Integer.valueOf(j)))
                    hashmap.put(Integer.valueOf(j), Float.valueOf(((Float)hashmap.get(Integer.valueOf(j))).floatValue() + 1.0F));
                else
                    hashmap.put(Integer.valueOf(j), Float.valueOf(1.0F));
            }

        }

        return hashmap;
    }

    private static Map clusteringSummary2(List list)
    {
        HashMap hashmap = new HashMap();
        for(int i = 0; i < list.size(); i++)
        {
            Map map = ((ClusterLabel)list.get(i)).labelMap();
            for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
            {
                int j = ((Integer)iterator.next()).intValue();
                float f = ((Float)map.get(Integer.valueOf(j))).floatValue();
                if(hashmap.containsKey(Integer.valueOf(j)))
                    hashmap.put(Integer.valueOf(j), Float.valueOf(((Float)hashmap.get(Integer.valueOf(j))).floatValue() + f));
                else
                    hashmap.put(Integer.valueOf(j), Float.valueOf(f));
            }

        }

        return hashmap;
    }

    public static void propagate(String s, int i, String s1, String s2, String s3, float f)
    {
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        ArrayList arraylist2 = new ArrayList();
        ArrayList arraylist3 = new ArrayList();
        HashMap hashmap = new HashMap();
        HashMap hashmap1 = new HashMap();
        readBiGraphEdges(s2, arraylist, arraylist1, arraylist2, arraylist3, hashmap, hashmap1, null);
        List list = readClusters(s1, i, f, hashmap);
        List list1 = propagateClusters(list, arraylist1, null, i, f);
        outputClusters(list1, list1, null, s3, s3, arraylist, arraylist, arraylist3, arraylist3, true, true);
    }

    private static List propagateClusters(List list, List list1, List list2, int i, float f)
    {
        float f1 = 1.0F;
        Object obj = null;
        ArrayList arraylist = new ArrayList();
        for(int j = 0; j < list1.size(); j++)
        {
            ClusterLabel clusterlabel = new ClusterLabel(i, f, j, false);
            int k;
            for(Iterator iterator = ((HashSet)list1.get(j)).iterator(); iterator.hasNext(); clusterlabel.neighbour((ClusterLabel)list.get(k), f1))
            {
                k = ((Integer)iterator.next()).intValue();
                if(list2 != null)
                    f1 = ((Float)((HashMap)list2.get(j)).get(Integer.valueOf(k))).floatValue();
            }

            clusterlabel.noMore();
            arraylist.add(clusterlabel);
        }

        return arraylist;
    }

    private static void reportRes(Vector vector, Vector vector1, int i)
    {
        if(vector != null)
        {
            for(int j = 0; j < vector.size(); j++)
                if(!"Unused".equals(vector1.get(j)))
                    System.out.println(String.format("%s: %.3f", new Object[] {
                        vector1.get(j), Double.valueOf(((Double)vector.get(j)).doubleValue() / (double)i)
                    }));

        }
    }

    private static Vector sumRes(Vector vector, Vector vector1, int i)
    {
        Vector vector2 = new Vector();
        if(vector == null)
        {
            for(int j = i; j < vector1.size(); j++)
                vector2.add(vector1.get(j));

        } else
        {
            for(int k = i; k < vector1.size(); k++)
                vector2.add(Double.valueOf(((Double)vector.get(k - i)).doubleValue() + ((Double)vector1.get(k)).doubleValue()));

        }
        return vector2;
    }

    private static void report(double d, double d1, double d2, double d3, 
            double d4, double d5, double d6, double d7, boolean flag, boolean flag1)
    {
        String s = String.format("%.0f (%.0f) communities, %.0f iterations, %.0fms", new Object[] {
            Double.valueOf(d), Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3)
        });
        if(flag)
        {
            s = (new StringBuilder()).append(s).append(String.format(", overlap=%.3f/%.3f", new Object[] {
                Double.valueOf(d6), Double.valueOf(d7)
            })).toString();
            if(flag1)
                s = (new StringBuilder()).append(s).append(String.format(", mod=%.3f/%.3f", new Object[] {
                    Double.valueOf(d4), Double.valueOf(d5)
                })).toString();
        } else
        {
            s = (new StringBuilder()).append(s).append(String.format(", overlap=%.3f", new Object[] {
                Double.valueOf(d6)
            })).toString();
            if(flag1)
                s = (new StringBuilder()).append(s).append(String.format(", mod=%.3f", new Object[] {
                    Double.valueOf(d4)
                })).toString();
        }
        System.out.println(s);
    }

    public static void readBiGraphEdges(String s, List list, List list1, List list2, List list3, List list4)
    {
        HashMap hashmap = new HashMap();
        HashMap hashmap1 = new HashMap();
        readBiGraphEdges(s, list, list1, list2, list3, ((Map) (hashmap)), ((Map) (hashmap1)), list4);
    }

    private static void copyFile(String s, String s1)
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
            PrintStream printstream = new PrintStream(new FileOutputStream(s1));
            String s2;
            while((s2 = bufferedreader.readLine()) != null) 
                printstream.println(s2);
            bufferedreader.close();
            printstream.close();
        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("copyFile error: ").append(s).append(" ").append(s1).toString());
            System.out.println(exception.toString());
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static int readBiGraphEdges(String s, List list, List list1, List list2, List list3, Map map, Map map1, List list4)
    {
        int k = 0;
        boolean flag = list2 != list3;
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
            do
            {
                String s1;
                if((s1 = bufferedreader.readLine()) == null)
                    break;
                if(!"".equals(s1) && !s1.startsWith("#"))
                {
                    String as[] = s1.split("[ \t]");
                    float f;
                    if(Array.getLength(as) >= 3)
                        f = Float.parseFloat(as[2]);
                    else
                        f = 1.0F;
                    if(flag || !as[0].equals(as[1]))
                    {
                        int i = nameInt(as[0], list2, map);
                        int j = nameInt(as[1], list3, map1);
                        do
                        {
                            if(list.size() >= i + 1)
                                break;
                            list.add(new HashSet());
                            if(list4 != null)
                                list4.add(new HashMap());
                        } while(true);
                        if(((HashSet)list.get(i)).add(Integer.valueOf(j)))
                            k++;
                        do
                        {
                            if(list1.size() >= j + 1)
                                break;
                            list1.add(new HashSet());
                            if(list4 != null)
                                list4.add(new HashMap());
                        } while(true);
                        ((HashSet)list1.get(j)).add(Integer.valueOf(i));
                        if(list4 != null)
                        {
                            ((HashMap)list4.get(i)).put(Integer.valueOf(j), Float.valueOf(f));
                            ((HashMap)list4.get(j)).put(Integer.valueOf(i), Float.valueOf(f));
                        }
                    } else
                    {
                        System.out.println((new StringBuilder()).append("Invalid edge (ignored): ").append(as[0]).append("/").append(as[1]).toString());
                    }
                }
            } while(true);
            bufferedreader.close();
        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("readBiGraphEdges error: ").append(s).toString());
            System.out.println(exception.toString());
            exception.printStackTrace();
            System.exit(1);
        }
        return k;
    }

    private static int nameInt(String s, List list, Map map)
    {
        int i = 0;
        if(list == null)
            i = Integer.parseInt(s);
        else
        if(map.containsKey(s))
        {
            i = ((Integer)map.get(s)).intValue();
        } else
        {
            i = list.size();
            list.add(s);
            map.put(s, Integer.valueOf(i));
        }
        return i;
    }

    private static Vector outputClusters(List list, List list1, String s, String s1, String s2, List list2, List list3, List list4, 
            List list5, boolean flag, boolean flag1)
    {
        long l = (new Date()).getTime();
        double d = 0.0D;
        int i = list.size();
        boolean flag2 = list4 != list5;
        ArrayList arraylist = new ArrayList();
        Object obj = null;
        Object obj1 = null;
        long l1 = (new Date()).getTime();
        List list6 = convertClusters(list, arraylist, 0);
        if(flag2)
        {
            List list8 = convertClusters(list1, arraylist, i);
            combineClusters(list6, list8);
            list2 = combineGraphs(list2, list3, i);
        }
        long l2 = (new Date()).getTime();
        HashSet hashset = flatten(list6);
        long l3 = (new Date()).getTime();
        simpleSimplifyClusters(list6, arraylist);
        long l4 = (new Date()).getTime();
        list6 = removeEmpty(list6);
        int j = list6.size();
        long l5 = (new Date()).getTime();
        List list7;
        if(flag)
        {
            list7 = contiguous(list6, list2);
            if(j != list7.size())
                list7 = removeEmpty(list7);
        } else
        {
            list7 = list6;
        }
        if(flag1)
            simplifyClusters(list7);
        double d1 = list7.size();
        long l6 = (new Date()).getTime();
        addSingletons(list7, hashset);
        long l7 = (new Date()).getTime();
        double d2 = list7.size();
        l = (new Date()).getTime() - l;
        if(flag2)
        {
            writeBiClusters(list7, s, list4, list5, i);
            List list9 = filterClusters(list7, i);
            writeClusters(list9, s2, list5);
            d = totSize(list9);
        }
        writeClusters(list7, s1, list4);
        double d3 = totSize(list7);
        Vector vector = new Vector();
        vector.add(Double.valueOf(d2));
        vector.add(Double.valueOf(d1));
        vector.add(Double.valueOf(d3));
        vector.add(Double.valueOf(d));
        vector.add(Double.valueOf(l));
        vector.add(Double.valueOf(l2 - l1));
        vector.add(Double.valueOf(l3 - l2));
        vector.add(Double.valueOf(l4 - l3));
        vector.add(Double.valueOf(l5 - l4));
        vector.add(Double.valueOf(l6 - l5));
        vector.add(Double.valueOf(l7 - l6));
        return vector;
    }

    private static int totSize(List list)
    {
        int i = 0;
        for(int j = 0; j < list.size(); j++)
            i += ((TreeSet)list.get(j)).size();

        return i;
    }

    private static List filterClusters(List list, int i)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            TreeSet treeset = (TreeSet)iterator.next();
            Iterator iterator1 = treeset.iterator();
            TreeSet treeset1 = new TreeSet();
            do
            {
                if(!iterator1.hasNext())
                    break;
                int j = ((Integer)iterator1.next()).intValue();
                if(j >= i)
                {
                    treeset1.add(Integer.valueOf(j - i));
                    iterator1.remove();
                }
            } while(true);
            if(treeset.isEmpty())
                iterator.remove();
            if(!treeset1.isEmpty())
                arraylist.add(treeset1);
        } while(true);
        return arraylist;
    }

    private static List combineGraphs(List list, List list1, int i)
    {
        ArrayList arraylist = new ArrayList();
        int j;
        for(j = 0; j < list.size(); j++)
        {
            arraylist.add(new HashSet());
            for(Iterator iterator = ((HashSet)list.get(j)).iterator(); iterator.hasNext(); ((HashSet)arraylist.get(j)).add(Integer.valueOf(((Integer)iterator.next()).intValue() + i)));
        }

        for(; j < i; j++)
            arraylist.add(new HashSet());

        for(int k = 0; k < list1.size(); k++)
            arraylist.add(list1.get(k));

        return arraylist;
    }

    private static void combineClusters(List list, List list1)
    {
        for(int i = 0; i < list.size(); i++)
            ((TreeSet)list.get(i)).addAll((Collection)list1.get(i));

    }

    private static void addSingletons(List list, HashSet hashset)
    {
        HashSet hashset1 = flatten(list);
        Iterator iterator = hashset.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            int i = ((Integer)iterator.next()).intValue();
            if(!hashset1.contains(Integer.valueOf(i)))
            {
                TreeSet treeset = new TreeSet();
                treeset.add(Integer.valueOf(i));
                list.add(treeset);
            }
        } while(true);
    }

    private static HashSet flatten(List list)
    {
        HashSet hashset = new HashSet();
        for(int i = 0; i < list.size(); i++)
            hashset.addAll((Collection)list.get(i));

        return hashset;
    }

    private static void writeClusters(List list, String s, List list1)
    {
        try
        {
            if("".equals(s))
            {
                writeClusters(list, System.out, list1);
            } else
            {
                PrintStream printstream = new PrintStream(new FileOutputStream(s));
                writeClusters(list, printstream, list1);
                printstream.close();
            }
        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("writeClusters error ").append(exception.toString()).toString());
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static List convertClusters(List list, List list1, int i)
    {
        ArrayList arraylist = new ArrayList();
        for(int j = 0; j < list.size(); j++)
        {
            TreeSet treeset = ((ClusterLabel)list.get(j)).labelSet();
            int k;
            for(Iterator iterator = treeset.iterator(); iterator.hasNext(); ((TreeSet)list1.get(k)).remove(Integer.valueOf(k)))
            {
                for(k = ((Integer)iterator.next()).intValue(); arraylist.size() < k + 1; list1.add(null))
                    arraylist.add(new TreeSet());

                ((TreeSet)arraylist.get(k)).add(Integer.valueOf(j + i));
                if(list1.get(k) == null)
                    list1.set(k, new TreeSet(treeset));
                else
                    ((TreeSet)list1.get(k)).retainAll(treeset);
            }

        }

        return arraylist;
    }

    private static List removeEmpty(List list)
    {
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < list.size(); i++)
        {
            TreeSet treeset = (TreeSet)list.get(i);
            if(treeset.size() > 1)
                arraylist.add(treeset);
        }

        return arraylist;
    }

    private static void simpleSimplifyClusters(List list, List list1)
    {
        for(int i = 0; i < list.size(); i++)
            if(list1.get(i) != null && !((TreeSet)list1.get(i)).isEmpty() && deletable(i, list1))
            {
                simpleDelClusters++;
                simpleDelVerts += ((TreeSet)list.get(i)).size();
                ((TreeSet)list.get(i)).clear();
            }

    }

    private static boolean deletable(int i, List list)
    {
        TreeSet treeset = (TreeSet)list.get(i);
        for(Iterator iterator = treeset.iterator(); iterator.hasNext();)
        {
            int j = ((Integer)iterator.next()).intValue();
            if(!((TreeSet)list.get(j)).contains(Integer.valueOf(i)) || j < i)
                return true;
        }

        return false;
    }

    private static void simplifyClusters(List list)
    {
        int l = list.size();
        if(l <= 1)
            return;
        SetPair asetpair[] = new SetPair[l];
        for(int i = 0; i < l; i++)
            asetpair[i] = new SetPair(i, (TreeSet)list.get(i));

        list.clear();
        Arrays.sort(asetpair);
        int j;
        for(j = 0; j < l - 1; j++)
        {
            boolean flag = true;
            int k = j + 1;
            do
            {
                if(k >= l)
                    break;
                if(asetpair[k].set.containsAll(asetpair[j].set))
                {
                    delClusters++;
                    delVerts += asetpair[j].set.size();
                    flag = false;
                    break;
                }
                k++;
            } while(true);
            if(flag)
                list.add(asetpair[j].set);
        }

        list.add(asetpair[j].set);
    }

    private static List contiguous(List list, List list1)
    {
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < list.size(); i++)
        {
            TreeSet treeset1;
            for(TreeSet treeset = (TreeSet)list.get(i); !treeset.isEmpty(); treeset.removeAll(treeset1))
            {
                treeset1 = contiguous(((Integer)treeset.iterator().next()).intValue(), treeset, list1);
                arraylist.add(treeset1);
            }

        }

        return arraylist;
    }

    private static TreeSet contiguous(int i, TreeSet treeset, List list)
    {
        LinkedList linkedlist = new LinkedList();
        TreeSet treeset1 = new TreeSet();
        linkedlist.add(Integer.valueOf(i));
        treeset1.add(Integer.valueOf(i));
        HashSet hashset;
        for(; !linkedlist.isEmpty(); treeset1.addAll(hashset))
        {
            i = ((Integer)linkedlist.removeFirst()).intValue();
            hashset = new HashSet((Collection)list.get(i));
            hashset.retainAll(treeset);
            hashset.removeAll(treeset1);
            linkedlist.addAll(hashset);
        }

        return treeset1;
    }

    private static void writeBiClusters(List list, String s, List list1, List list2, int i)
    {
        try
        {
            PrintStream printstream = new PrintStream(new FileOutputStream(s));
            for(int j = 0; j < list.size(); j++)
            {
                TreeSet treeset = (TreeSet)list.get(j);
                int k = treeset.size();
                if(k > 0)
                {
                    TreeSet treeset1 = new TreeSet();
                    TreeSet treeset2 = new TreeSet();
                    for(Iterator iterator = treeset.iterator(); iterator.hasNext();)
                    {
                        int l = ((Integer)iterator.next()).intValue();
                        if(l < i)
                            treeset1.add(Integer.valueOf(l));
                        else
                            treeset2.add(Integer.valueOf(l - i));
                    }

                    writeCluster(treeset1, printstream, list1);
                    printstream.print("\t");
                    writeCluster(treeset2, printstream, list2);
                    printstream.println();
                }
            }

        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("writeBiClusters error ").append(exception.toString()).toString());
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static void writeClusters(List list, PrintStream printstream, List list1)
    {
        for(int i = 0; i < list.size(); i++)
        {
            TreeSet treeset = (TreeSet)list.get(i);
            int j = treeset.size();
            if(j > 0)
            {
                writeCluster(treeset, printstream, list1);
                printstream.println();
            }
        }

    }

    private static void writeCluster(TreeSet treeset, PrintStream printstream, List list)
    {
        int l = treeset.size();
        Integer ainteger[] = new Integer[l];
        treeset.toArray(ainteger);
        String as[] = new String[l];
        for(int i = 0; i < l; i++)
        {
            int k = ainteger[i].intValue();
            if(list == null)
                as[i] = (new StringBuilder()).append(k).append("").toString();
            else
                as[i] = (String)list.get(k);
        }

        Arrays.sort(as);
        for(int j = 0; j < l; j++)
            printstream.print((new StringBuilder()).append(as[j]).append(" ").toString());

    }

    private static List simpleClustering(int i, float f)
    {
        ArrayList arraylist = new ArrayList();
        for(int j = 0; j < i; j++)
        {
            ClusterLabel clusterlabel = new ClusterLabel(i, f, j, true);
            arraylist.add(clusterlabel);
        }

        return arraylist;
    }

    private static void printUsageAndExit()
    {
        System.err.println("Usage: java COPRA <file> <options>");
        System.err.println("Options:");
        System.err.println("  -bi            <file> is a bipartite network. \"-w\" not allowed.");
        System.err.println("  -w             <file> is a weighted unipartite network. \"-bi\" not allowed.");
        System.err.println("  -v <v>         <v> is maximum number of communities/vertex. Default: 1.");
        System.err.println("  -vs <v1> <v2>  Repeats for -v <v> for all <v> between <v1>-<v2>.");
        System.err.println("  -prop <p>      <p> is maximum number of propagations. Default: no limit.");
        System.err.println("  -repeat <r>    Repeats <r> times, for each <v>, and computes average.");
        System.err.println("  -mo            Compute the overlap modularity of each solution.");
        System.err.println("  -nosplit       Don't split discontiguous communities.");
        System.err.println("  -extrasimplify Simplify communities again after splitting.");
        System.err.println("  -q             Don't show information when starting program.");
        System.exit(1);
    }

    private static List readClusters(String s, int i, float f, Map map)
    {
        int i1 = 0;
        Object obj = null;
        ArrayList arraylist = new ArrayList();
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
            String s1;
            while((s1 = bufferedreader.readLine()) != null) 
            {
                String as[] = s1.split(" ");
                boolean flag;
                if(as[0].endsWith(":"))
                    flag = true;
                else
                    flag = false;
                for(int j = ((flag) ? 1 : 0); j < Array.getLength(as); j++)
                {
                    int l;
                    for(l = ((Integer)map.get(as[j])).intValue(); arraylist.size() < l + 1; arraylist.add(new ClusterLabel(i, f)));
                    ((ClusterLabel)arraylist.get(l)).add(i1);
                }

                i1++;
            }
            for(int k = 0; k < arraylist.size(); k++)
                ((ClusterLabel)arraylist.get(k)).normalize();

        }
        catch(Exception exception)
        {
            System.err.println((new StringBuilder()).append("readClusters error: ").append(exception.toString()).toString());
            exception.printStackTrace();
            System.exit(1);
        }
        return arraylist;
    }

    static String separator = "------------------------------------------------------------------------";
    static String welcome1 = "***************************************";
    static String welcome2 = "* COPRA v1.25  (c) Steve Gregory 2011 *";
    static int delVerts = 0;
    static int delClusters = 0;
    static int simpleDelVerts = 0;
    static int simpleDelClusters = 0;
    
    
    public static void main(String[] args){
    	COPRA copra = new COPRA();
    	String tempfileName = "copra_temp.edge";
    	List<String> argsList = new ArrayList<String>();
    	for(int i=0;i<args.length;i++){
    		argsList.add(args[i]);
    	}
		String gmlfilename = "J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex, Edge> loadGML = new LoadGML<Vertex, Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex, Edge> graph = loadGML.loadGraph(gmlfilename);
		ExportFile.exportAsEdgeFile(graph, "copra_temp.edge");
		argsList.add("copra_temp.edge");
		argsList.add("-v");
		argsList.add("10");
		argsList.add("-mo");
//		argsList.add("-extrasimlify");
    	copra.run(argsList.toArray(new String[0]));
    }

}
