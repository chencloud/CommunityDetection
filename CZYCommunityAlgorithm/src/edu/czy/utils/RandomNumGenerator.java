package edu.czy.utils;

import java.util.Random;


public class RandomNumGenerator {
	public static Random random = new Random();
	
	public static int getRandomInt(int max) {
		return random.nextInt(max);
	}
	
}