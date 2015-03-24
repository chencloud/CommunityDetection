package edu.czy.test;

import java.util.Map.Entry;
import java.util.TreeMap;

public class TestTest {
	public static void main(String[] args) {
//		TreeMap<Integer,Double> u = new TreeMap<Integer,Double>();
//		u.put(3, 1.0);
//		u.put(6, 0.0);
//		u.put(1, 3.0);
//		u.put(10, 4.0);
//		u.put(2, 5.0);
//		for(Entry<Integer,Double> entry: u.entrySet()) {
//			System.out.println(entry.getKey()+":"+entry.getValue());
//		}
		String test= "aa\tbb\tcc";
		for(String str : test.split("\\s"))
			System.out.print(str);
	}
}
