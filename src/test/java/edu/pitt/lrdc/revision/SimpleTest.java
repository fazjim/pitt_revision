package edu.pitt.lrdc.revision;

import java.util.ArrayList;

public class SimpleTest {
	public static void main(String[] args) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		a.add(4);
		a.add(5);
		
		for(int i = 2;i<4;i++) {
			a.remove(2);
		}
		
		for(int i = 0;i<a.size();i++) {
			System.out.println(a.get(i));
		}
	}
}
