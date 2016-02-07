package edu.pitt.cs.revision.reviewLinking.annotation;

import java.io.File;
import java.util.*;

import edu.pitt.cs.revision.reviewLinking.*;

public class AgreementCalculator {
	public static int EXACT_TARGET;
	public static int EXACT_SOLUTION;
	public static int EXACT_TYPE;

	public static int PARTIAL_TARGET;
	public static int PARTIAL_SOLUTION;
	public static int PARTIAL_TYPE;

	public static double getAgreement(List<CommentBoxReview> ann1,
			List<CommentBoxReview> ann2, int option) {
		double val = 0;
		if (option == EXACT_TARGET) {

		} else if (option == EXACT_SOLUTION) {

		} else if (option == EXACT_TYPE) {

		} else if (option == PARTIAL_TARGET) {

		} else if (option == PARTIAL_SOLUTION) {

		} else if (option == PARTIAL_TYPE) {

		}

		return val;
	}

	public static double getAgreementBatch(String ann1Folder,
			String ann2Folder, int option) {
		File ann1F = new File(ann1Folder);
		File ann2F = new File(ann2Folder);

		Hashtable<String, File> ann1Hash = new Hashtable<String, File>();
		File[] subs = ann1F.listFiles();
		for (File sub : subs) {
			ann1Hash.put(sub.getName(), sub);
		}

		File[] subs2 = ann2F.listFiles();
		int size = 0;
		double valAll = 0;
		for (File sub : subs2) {
			String name = sub.getName();
			if (ann1Hash.containsKey(name)) {
				valAll += getAgreement(ReviewAnnotationReader.readReviews(sub
						.getAbsolutePath()),
						ReviewAnnotationReader.readReviews(ann1Hash.get(name)
								.getAbsolutePath()), option);
				size++;
			}
		}
		return valAll/size;
	}
}
