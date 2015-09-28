package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Color;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class ColorConstants {
	public static  Color claimColor = new Color(255, 102, 102);
	public static Color warrantColor = new Color(153, 204, 255);
	public static Color evidenceColor = new Color(102, 255, 255);
	public static Color rebuttalColor = new Color(0, 64, 255);
	public static Color generalColor = new Color(204, 102, 255);
	public static Color conventionColor = new Color(255, 153, 51);
	public static Color wordColor = new Color(255, 255, 153);
	public static Color cascadedColor = new Color(255, 255, 204);
	public static Color organizationColor = new Color(153, 153, 255);
	public static Color unannotatedColor = Color.GRAY;//new Color(242,242,242);
	
	public static Color getColor(int revPurpose) {
		if(revPurpose == RevisionPurpose.CLAIMS_IDEAS) {
			return claimColor;
		} else if(revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
			return warrantColor;
		} else if(revPurpose == RevisionPurpose.EVIDENCE) {
			return evidenceColor;
		} else if(revPurpose == RevisionPurpose.CD_REBUTTAL_RESERVATION) {
			return rebuttalColor;
		} else if(revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
			return generalColor;
		} else if(revPurpose == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) {
			return conventionColor;
		} else if(revPurpose == RevisionPurpose.WORDUSAGE_CLARITY) {
			return wordColor;
		} else if(revPurpose == RevisionPurpose.WORDUSAGE_CLARITY_CASCADED) {
			return cascadedColor;
		} else if(revPurpose == RevisionPurpose.ORGANIZATION) {
			return organizationColor;
		}
		return null;
	}
}
