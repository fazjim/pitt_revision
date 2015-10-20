package edu.pitt.cs.revision.purpose;

import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class CategoryFactory {
	public static int CONTENT_MODE = 1; //only the content changes
	public static int SURFACE_MODE = 2; //only the surface changes
	public static int CONTENTSURFACE_MODE = 3; //content changes plus one surface change
	public static int ALL_MODE = 4; //every type of change
	public static int BINARY_MODE = 5;//content or surface change
	
	public static ArrayList<String> buildCategories(int option) {
		ArrayList<String> categories = new ArrayList<String>();
		if(option == CONTENT_MODE) {
			for(int i = RevisionPurpose.CLAIMS_IDEAS;i<=RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT;i++) {
				categories.add(RevisionPurpose.getPurposeName(i));
			}
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.NOCHANGE));
		} else if(option == SURFACE_MODE) {
			for(int i = RevisionPurpose.WORDUSAGE_CLARITY_CASCADED;i<=RevisionPurpose.ORGANIZATION;i++) {
				categories.add(RevisionPurpose.getPurposeName(i));
			}
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.NOCHANGE));
		} else if(option == CONTENTSURFACE_MODE) {
			for(int i = RevisionPurpose.CLAIMS_IDEAS;i<=RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT;i++) {
				categories.add(RevisionPurpose.getPurposeName(i));
			}
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE));
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.NOCHANGE));
		} else if(option == ALL_MODE) {
			for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
				categories.add(RevisionPurpose.getPurposeName(i));
			}
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.NOCHANGE));
		} else if(option == BINARY_MODE) {
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE));
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.CONTENT));
			categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.NOCHANGE));
		}
		return categories;
	}
	
	
}
