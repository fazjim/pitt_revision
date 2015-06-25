package edu.pitt.lrdc.revision.discourse;

import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.discourse.DiscourseSegmenter;

public class DiscourseSegmenterTest {
	public static void main(String[] args) {
		DiscourseSegmenter ds = DiscourseSegmenter.getInstance();
		String text = "a, b, c, d, e, f and g and i and j and k and r or s or t and p and q, x, y and z (lalala) or (hahaha) or (ss how we should act and respond to different situations ";
		String text2 = "and can create a diffusion of responsibility";
		String[] breakers = { ",", ";", " and ", " or ", "(", ")",
				" for ", " nor ", " but ", " yet ", " so ",
				" because ", " before ", " even ", " even if ",
				" even though ", " after ", " although ", " as ",
				" as if ", " as long as ", " as much as ",
				" as soon as ", " as though ", " if ", " if only ",
				" if when ", " if then ", " inasmuch ",
				" in order that ", " just as ", " lest ", " now ",
				" now ", " since ", " now that ", " now when ",
				" once ", " provided ", " provided that ",
				" rather than ", " since ", " so that ", " supposing ",
				" than ", " that ", " though ", " til ", " unless ",
				" until ", " when ", " whenever ", " where ",
				" whereas ", " where if ", " wherever ", " whether ",
				" which ", " while ", " who ", " whoever ", " why " };
		ArrayList<String> segments = ds.breakSegmentsBetter(text, breakers);
		
		for(String segment: segments) {
			System.out.println(segment);
		}
	}
}
