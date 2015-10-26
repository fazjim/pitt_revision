package edu.pitt.lrdc.cs.revision.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

/**
 * The unit of a revision, a nested data structure
 * 
 * @author zhangfan
 * @version 1.1
 * 
 *          The current version adds many new stuff, but there should be some
 *          stuff from the old design that could be removed
 * 
 */
public class RevisionUnit {
	/*
	 * Only for sentence-level revision are the two attributes useful
	 */
	private ArrayList<Integer> oldSentenceIndex = new ArrayList<Integer>(); // index of the sentence in the old draft
	private ArrayList<Integer> newSentenceIndex = new ArrayList<Integer>(); // index of the sentence in the new draft

	private ArrayList<Integer> oldParagraphNo = new ArrayList<Integer>(); //index of the paragraph of the sentence in the old draft
	private ArrayList<Integer> newParagraphNo = new ArrayList<Integer>(); //index of the paragraph of the sentence in the new draft

	
	
	private String oldSentence; // content of the sentence in the old draft
	private String newSentence; // content of the sentence in the new draft;

	// Revision operation and revision purpose
	private int revision_op = -1; // revision operation
	private int revision_purpose = -1; // revision purpose

	private int revision_index;// Well, we need this attribute _(:3ã€�âˆ )
	private int revision_level;// Well, we need this too _(:3ã€�âˆ )
	private int parent_index = -1; // Well..., so that the paper root node with
									// index = -1 can find it
	private int parent_level = Integer.MAX_VALUE; // Well...

	private RevisionUnit root; // pointer to the ultra root node (Not the parent)

	private ArrayList<RevisionUnit> units = new ArrayList<RevisionUnit>();

	/**
	 * This attribute is needed for the GUI annotation tool
	 */
	private boolean isAbandoned = false;

	public void setAbandoned() {
		this.isAbandoned = true;
	}

	public boolean isAbandoned() {
		return this.isAbandoned;
	}

	/**
	 * Default constructor
	 */
	public RevisionUnit(RevisionUnit root) {
		this.root = root;
	}

	private boolean isRoot = false;

	public boolean isRoot() {
		return isRoot;
	}

	public RevisionUnit(boolean isRoot) throws Exception {
		if (isRoot) {
			this.isRoot = true;
		} else {
			throw new Exception(
					"Non-root units must be created with other constructors");
		}
	}

	/**
	 * Yet another constructor
	 * 
	 * @param oldSentenceIndex
	 * @param newSentenceIndex
	 * @param revisionOp
	 * @param revisionPurpose
	 */
	public RevisionUnit(ArrayList<Integer> oldSentenceIndex, ArrayList<Integer> newSentenceIndex,
			int revisionOp, int revisionPurpose, RevisionUnit root) {
		this.oldSentenceIndex = oldSentenceIndex;
		this.newSentenceIndex = newSentenceIndex;
		this.revision_op = revisionOp;
		this.revision_purpose = revisionPurpose;
	}

	/**
	 * Revision Index has no use to the user at all, ignore this function
	 * 
	 * @return
	 */
	public int getRevision_index() {
		return revision_index;
	}

	/**
	 * Just ignore this function, I just need this to construct the hierarchical
	 * revision unit without having my code looking like a mammoth
	 * 
	 * @param revision_index
	 */
	public void setRevision_index(int revision_index) {
		this.revision_index = revision_index;
	}

	/**
	 * Probably won't be useful to the user
	 * 
	 * @return
	 */
	public int getRevision_level() {
		return revision_level;
	}

	/**
	 * Ignore it...
	 * 
	 * @param revision_level
	 */
	public void setRevision_level(int revision_level) {
		this.revision_level = revision_level;
	}

	public int getParent_index() {
		return parent_index;
	}

	public void setParent_index(int parent_index) {
		this.parent_index = parent_index;
	}

	public int getParent_level() {
		return parent_level;
	}

	public void setParent_level(int parent_level) {
		this.parent_level = parent_level;
	}

	public ArrayList<RevisionUnit> getUnits() {
		return units;
	}

	public void setUnits(ArrayList<RevisionUnit> units) {
		this.units = units;
	}

	/**
	 * Release the unit from the current group, and allows it being connected to
	 * other groups Just for the use of the Revision Annotation Tool
	 * 
	 */
	public void release() {
		this.parent_index = -1;
		this.parent_level = Integer.MAX_VALUE;
		this.root.addUnit(this);
	}

	/**
	 * get the index of the old sentence
	 * 
	 * @return oldSentenceIndex
	 */
	public ArrayList<Integer> getOldSentenceIndex() {
		return oldSentenceIndex;
	}

	/**
	 * Setter
	 * 
	 * @param oldSentenceIndex
	 */
	public void setOldSentenceIndex(ArrayList<Integer> oldSentenceIndex) {
		this.oldSentenceIndex = oldSentenceIndex;
	}

	/**
	 * Getter
	 * 
	 * @return newSentenceIndex
	 */
	public ArrayList<Integer> getNewSentenceIndex() {
		return newSentenceIndex;
	}

	/**
	 * Setter
	 * 
	 * @param newSentenceIndex
	 */
	public void setNewSentenceIndex(ArrayList<Integer> newSentenceIndex) {
		this.newSentenceIndex = newSentenceIndex;
	}

	/**
	 * get the revision operation
	 * 
	 * @return
	 */
	public int getRevision_op() {
		return revision_op;
	}

	/**
	 * Setter
	 * 
	 * @param revision_op
	 *            Revision Operation
	 */
	public void setRevision_op(int revision_op) {
		this.revision_op = revision_op;
	}

	/**
	 * Getter
	 * 
	 * @return revision purpose
	 */
	public int getRevision_purpose() {
		return revision_purpose;
	}

	/**
	 * Setter
	 * 
	 * @param revision_purpose
	 */
	public void setRevision_purpose(int revision_purpose) {
		this.revision_purpose = revision_purpose;
	}

	/**
	 * Getter
	 * 
	 * @param index
	 * @return RevisionUnit, a lower level unit of the current revision unit
	 */
	public RevisionUnit getUnit(int index) {
		return units.get(index);
	}

	/**
	 * Add a unit to the child of the current unit
	 * 
	 * @param unit
	 */
	public void addUnit(RevisionUnit unit) {
		this.units.add(unit);
	}

	public String getOldSentence() {
		return oldSentence;
	}

	public void setOldSentence(String oldSentence) {
		this.oldSentence = oldSentence;
	}

	public String getNewSentence() {
		return newSentence;
	}

	public void setNewSentence(String newSentence) {
		this.newSentence = newSentence;
	}

	public String toString() {
		String msg = "Level " + this.revision_level + ":\n";
		String revPurpose = RevisionPurpose
				.getPurposeName(this.revision_purpose);
		if (this.revision_op == RevisionOp.ADD) {
			msg += "Adding (a/an) " + revPurpose;
		} else if (this.revision_op == RevisionOp.DELETE) {
			msg += "Deleting (a/an) " + revPurpose;
		} else if (this.revision_op == RevisionOp.MODIFY) {
			msg += "Change (a/an) " + revPurpose;
		} else if (this.revision_op == RevisionOp.NOCHANGE) {
			msg += "Change the elements of (a/an) " + revPurpose
					+ " while the whole " + revPurpose + " is not modified";
		}
		msg += ":\n";
		if (this.units.size() == 0) {
			// it is the sentence level
			msg += "Original Sentence: " + this.oldSentence + "\n";
			msg += "New Sentence: " + this.newSentence + "\n";
			if(this.oldSentenceIndex!=null && this.oldSentenceIndex.size()!=0)
				msg += "Original sentence index: "+this.oldSentenceIndex.toString() + "\n";
			if(this.newSentenceIndex!=null && this.newSentenceIndex.size()!=0)
				msg += "New sentence index: "+this.newSentenceIndex.toString() + "\n";

		} else {
			for (int i = 0; i < this.units.size(); i++) {
				msg += "\t" + units.get(i).toString();
			}
		}
		return msg;
	}

	/**
	 * Get the revision units at a specified level Use BFS, heavy weight
	 * function, might cause bad performance
	 * 
	 * @param level
	 * @return
	 */
	public ArrayList<RevisionUnit> getRevisionUnitAtLevel(int level) {
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		Stack<RevisionUnit> ruStack = new Stack<RevisionUnit>();
		ruStack.push(this);
		while (!ruStack.empty()) {
			RevisionUnit temp = ruStack.pop();
			if (temp.getRevision_level() == level && !temp.isAbandoned()) {
				rus.add(temp);
			} else {
				for (RevisionUnit ru : temp.getUnits()) {
					ruStack.push(ru);
				}
			}
		}
		return rus;
	}

	/**
	 * Get the sentence-level revision units, filtering the multiple purpose cases
	 * @return
	 */
	/*
	public ArrayList<RevisionUnit> getSentenceRevisionUnitsForML() {
		ArrayList<RevisionUnit> rus = this.getRevisionUnitAtLevel(0);
		ArrayList<RevisionUnit> filteredRus = new ArrayList<RevisionUnit>();
		for(RevisionUnit ru: rus) {
			if(ru.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY_CASCADED) {
			
			}
		}
		return rus;
	}*/
	
	/**
	 * Get the revision units at a specified level with the sentence index in
	 * the new draft heavy weight function, might cause bad performance
	 * 
	 * @param level
	 * @param sentenceIndex
	 * @return
	 */
	public ArrayList<RevisionUnit> getRevisionUnitNewAtLevel(int level,
			int sentenceIndex) {
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		ArrayList<RevisionUnit> candidates = getRevisionUnitAtLevel(level);
		for (RevisionUnit ru : candidates) {
			if (ru.getNewSentenceIndex()!=null && ru.getNewSentenceIndex().contains(sentenceIndex) && !ru.isAbandoned())
				rus.add(ru);
		}
		return rus;
	}

	/**
	 * Remove a child Unit
	 * 
	 * @param rmUnit
	 */
	public void removeUnit(RevisionUnit rmUnit) {
		this.units.remove(rmUnit);
	}

	/**
	 * Get the revision units at a specified level with the sentence index in
	 * the old draft heavy weight function, might cause bad performance
	 * 
	 * @param level
	 * @param sentenceIndex
	 * @return
	 */
	public ArrayList<RevisionUnit> getRevisionUnitOldAtLevel(int level,
			int sentenceIndex) {
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		ArrayList<RevisionUnit> candidates = getRevisionUnitAtLevel(level);
		for (RevisionUnit ru : candidates) {
			if (ru.getOldSentenceIndex()!=null && ru.getOldSentenceIndex().contains(sentenceIndex) && !ru.isAbandoned())
				rus.add(ru);
		}
		return rus;
	}

	/**
	 * Get the revision unit at a specified level with the revision index in the
	 * old draft heavy weight function, might cause bad performance
	 * 
	 * @param level
	 * @param sentenceIndex
	 * @return
	 */
	public RevisionUnit getRevisionUnitWithIndexAtLevel(int level,
			int revisionIndex) {
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		ArrayList<RevisionUnit> candidates = getRevisionUnitAtLevel(level);
		for (RevisionUnit ru : candidates) {
			if (ru.getRevision_index() == revisionIndex && !ru.isAbandoned())
				return ru;
		}
		return null;
	}

	/**
	 * Another function for the revision annotation tool
	 * 
	 * @param level
	 * @return
	 */
	public ArrayList<RevisionUnit> getCandidateUnitsAtLevel(int level) {
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		int rootLevel = 0;
		if(root == null) {
			rootLevel = this.getRevision_level();
		} else {
			rootLevel = root.getRevision_level();
		}
		
		HashSet<RevisionUnit> tmpSet = new HashSet<RevisionUnit>();
		for (int i = 0; i < level; i++) {
			ArrayList<RevisionUnit> temp = getRevisionUnitAtLevel(i);
			for (RevisionUnit t : temp) {		
				if (t.parent_level >= rootLevel && !t.isAbandoned()) {
					if(!tmpSet.contains(t)) {
						rus.add(t);
						tmpSet.add(t);
					}
				}
			}
		}
		return rus;
	}

	// Get a description of this revision unit
	public String getLabel() {
		String op = RevisionOp.getOpName(this.getRevision_op());
		String purpose = RevisionPurpose.getPurposeName(this
				.getRevision_purpose());
		int level = this.getRevision_level();
		int index = this.getRevision_index();
		return op + ": " + purpose + " Level: " + level + " Index:" + index;
	}

	/**
	 * Get the contents of revision document
	 * 
	 * @param doc
	 * @return
	 */
	public String getDetailContent(RevisionDocument doc) {
		ArrayList<RevisionUnit> basicUnits = this.getRevisionUnitAtLevel(0);
		String content = "";
		int size = basicUnits.size();
		for (int i = size-1;i>=0;i--) {
			RevisionUnit ru = basicUnits.get(i);
			if (ru.getNewSentenceIndex() != null && ru.getNewSentenceIndex().size()!=0) {
				for(Integer index: ru.getNewSentenceIndex())
				content += "NEW:"+ RevisionOp.getOpName(ru.getRevision_op()) + ":"
						+ doc.getNewSentence(index) + "\n";
			} 
			
			if (ru.getOldSentenceIndex() !=null && ru.getOldSentenceIndex().size() != 0) {
				for(Integer index: ru.getOldSentenceIndex())
				content += "OLD:"+ RevisionOp.getOpName(ru.getRevision_op()) + ":"
						+ doc.getOldSentence(index) + "\n";
			}
		}
		return content;
	}

	// Reorganize the units
	public void reorganize() {
		int level = this.getRevision_level();
		// clear
		int currentLevel = 0;
		while (currentLevel < level) {
			ArrayList<RevisionUnit> rus = this
					.getRevisionUnitAtLevel(currentLevel);
			for (RevisionUnit ru : rus) {
				boolean isAbandoned = true;
				if (ru.getUnits().size() == 0)
					isAbandoned = false;
				else {
					// Has Units, check if they are abandoned

					ArrayList<RevisionUnit> childs = ru.getUnits();
					for (RevisionUnit child : childs) {
						if (child.getParent_index() == ru.getRevision_index()
								&& child.getParent_level() == ru
										.getRevision_level()) {
							isAbandoned = false;
							break;
						}
					}
					if (isAbandoned) {
						RevisionUnit parent = this
								.getRevisionUnitWithIndexAtLevel(
										ru.getParent_level(),
										ru.getParent_index());
						parent.getUnits().remove(ru); // Remove the abandoned
														// ones
					}
				}

				if (!isAbandoned) {
					// Basic Unit
					RevisionUnit parent = this.getRevisionUnitWithIndexAtLevel(
							ru.getParent_level(), ru.getParent_index());
					ArrayList<RevisionUnit> subs = parent.getUnits();
					boolean isExist = false;
					for (RevisionUnit tmpUnit : subs) {
						if (tmpUnit.getRevision_index() == ru
								.getRevision_index()
								&& tmpUnit.getRevision_level() == ru
										.getRevision_level()) {
							isExist = true;
							break;
						}
					}
					if (!isExist) { // If has not been added yet
						parent.addUnit(ru); // Add the indexed ones
					}
				}
			}
			currentLevel++;
		}
	}

	/**
	 * Clear the non existing ones
	 */
	public void clear() {
		ArrayList<RevisionUnit> childs = this.getUnits();
		ArrayList<RevisionUnit> removes = new ArrayList<RevisionUnit>();
		for (RevisionUnit tmp : childs) {
			if (tmp.isAbandoned()) {
				removes.add(tmp);

				if (tmp.getUnits().size() != 0) {
					ArrayList<RevisionUnit> subChild = tmp.getUnits();
					for (RevisionUnit tt : subChild) {
						tt.setParent_level(Integer.MAX_VALUE);
						tt.setParent_index(-1);
						this.root.addUnit(tt);
					}
				}
			}
		}
		for (RevisionUnit rem : removes) {
			childs.remove(rem);
		}
		for (RevisionUnit rev : childs) {
			rev.clear();
		}
	}

	/**
	 * For the annotation tool
	 * 
	 * @param level
	 * @return
	 */
	public int getNextIndexAtLevel(int level) {
		ArrayList<RevisionUnit> rus = getRevisionUnitAtLevel(level);
		int nextIndex = 0;
		for (RevisionUnit ru : rus) {
			if (ru.getRevision_index() > nextIndex)
				nextIndex = ru.getRevision_index();
		}
		return nextIndex + 1;
	}

	public ArrayList<Integer> getOldParagraphNo() {
		return oldParagraphNo;
	}

	public void setOldParagraphNo(ArrayList<Integer> oldParagraphNo) {
		this.oldParagraphNo = oldParagraphNo;
	}

	public ArrayList<Integer> getNewParagraphNo() {
		return newParagraphNo;
	}

	public void setNewParagraphNo(ArrayList<Integer> newParagraphNo) {
		this.newParagraphNo = newParagraphNo;
	}

	
	public RevisionUnit copy(RevisionUnit anotherRoot) {
		RevisionUnit ru = new RevisionUnit(anotherRoot);
		ru.setNewSentenceIndex(this.getNewSentenceIndex());
		ru.setOldSentenceIndex(this.getOldSentenceIndex());
		ru.setRevision_op(this.getRevision_op());
		ru.setRevision_index(this.getRevision_index());
		return ru;
	}
	
	/**
	 * @deprecated
	 * Get the index label for the comparison of revisions
	 * @return
	 */
	public String getIndexLabel() {
		String label = "OLD:";
		for(Integer oldIndex: oldSentenceIndex) {
			label += oldIndex+"_";
		}
		label += "NEW:";
		for(Integer newIndex: newSentenceIndex) {
			label += newIndex + "_";
		}
		return label;
	}
	
	public String getUniqueID() {
		String ID = "";
		Collections.sort(oldSentenceIndex);
		Collections.sort(newSentenceIndex);
		ID+= "OLD-";
		for(Integer oldIndex: oldSentenceIndex) {
			if(oldIndex!=-1) ID += oldIndex+ "_";
		}
		ID += "NEW-";
		for(Integer newIndex: newSentenceIndex) {
			if(newIndex!=-1) ID += newIndex + "_";
		}
		return ID;
	}
}
