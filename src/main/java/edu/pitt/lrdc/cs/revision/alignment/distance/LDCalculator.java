package edu.pitt.lrdc.cs.revision.alignment.distance;

import edu.pitt.lrdc.cs.revision.alignment.model.Sentence;


public class LDCalculator extends SimCalculator{
	public static int ADDCOST = 1;
	public static int DELCOST = 1;
	public static int MODICOST = 1;

	public static double calcSen(Sentence src, Sentence dst) {
		String srcText = src.getContent();
		String dstText = dst.getContent();
		int length = srcText.split(" ").length;
		int val = calc(srcText,dstText);
		return val*1.0/length;
	}
	
	public static int calc(String src, String dst) {
		src = src.toLowerCase();
		dst = dst.toLowerCase();
		src = src.replaceAll("\\.", " ");
		src = src.replaceAll(",", " ");
		src = src.replaceAll("!", " ");

		dst = dst.replaceAll("\\.", " ");
		dst = dst.replaceAll(",", " ");
		dst = dst.replaceAll("!", " ");

		String[] srcTokens = src.split(" ");
		String[] dstTokens = dst.split(" ");

		int lenSrc = srcTokens.length;
		int lenDst = dstTokens.length;
		int[][] dp = new int[lenSrc][lenDst];
		if (lenSrc == 0)
			return lenDst * ADDCOST;
		if (lenDst == 0)
			return lenSrc * ADDCOST;
		for (int i = 0; i < lenSrc; i++) {
			for (int j = 0; j < lenDst; j++) {
				if (i == 0 && j == 0) {
					dp[0][0] = 0;
				} else {
					int smallest = Integer.MAX_VALUE;
					int add = Integer.MAX_VALUE;
					int delete = Integer.MAX_VALUE;
					int nomove = Integer.MAX_VALUE;

					if (j >= 1) {
						add = dp[i][j - 1] + ADDCOST;
						if (add < smallest)
							smallest = add;
					}
					if (i >= 1) {
						delete = dp[i - 1][j] + DELCOST;
						if (delete < smallest)
							smallest = delete;
					}

					if (i >= 1 && j >= 1) {
						if (srcTokens[i].equals(dstTokens[j])) {
							nomove = dp[i - 1][j - 1];
						} else {
							nomove = dp[i - 1][j - 1] + MODICOST;
						}
						if (nomove < smallest)
							smallest = nomove;
					}
					dp[i][j] = smallest;
				}
			}
		}
		return dp[lenSrc - 1][lenDst - 1];
	}

	public static void main(String[] args) {
		LDCalculator ld = new LDCalculator();
		String src = "The campaign 's data crunchers were able to extrapolate , using its extensive database , the individual household and then down to the individual person from there .";
		String dst = "With the help of an outside source the campaign sent its voter file and the television provider sent their billing file and boom , a list came back of people who had done certain things like , for example , watched the first presidential debate .";
		System.out.println("LDistance:" + ld.calc(src, dst));
	}
}
