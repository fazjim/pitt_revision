package edu.pitt.lrdc.cs.revision.process;
import java.util.ArrayList;
import edu.pitt.lrdc.cs.revision.model.*;

/**
 * To take the place of the heavy InfoAdder class
 * @author zhangfan
 *
 */
public interface RevisionProcessingInterface {
	/**
	 * The alignment step should break the clauses, align the clauses, and adding the paragraph information
	 * @param trainDocs
	 * @param srcFolder
	 * @param dstFolder
	 */
	public ArrayList<RevisionDocument> align(ArrayList<RevisionDocument> trainDocs, String srcFolder, String dstFolder);
	
	/**
	 * The classify step classifies the revisions
	 * @param trainDocs
	 * @param srcFolder
	 * @param dstFolder
	 */
	public void classify(ArrayList<RevisionDocument> trainDocs, ArrayList<RevisionDocument> testDocs, String dstFolder);
	
}
