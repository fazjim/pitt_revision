package edu.pitt.cs.revision.batch;

/**
 * All the parsed info for a sentence
 * @author zhangfan
 *
 */
public class SentenceInfo {
	private ErrorInfo errInfo;
	private POSTagInfo posInfo;
	
	public SentenceInfo() {
		errInfo = new ErrorInfo();
		posInfo = new POSTagInfo();
	}
	
	

	public ErrorInfo getErrInfo() {
		return errInfo;
	}



	public void setErrInfo(ErrorInfo errInfo) {
		this.errInfo = errInfo;
	}



	public POSTagInfo getPosInfo() {
		return posInfo;
	}



	public void setPosInfo(POSTagInfo posInfo) {
		this.posInfo = posInfo;
	}


	public double getTotal() {
		return this.posInfo.getTotalCount();
	}
	
	public double getJJRatio() {
		int jjNum = posInfo.getJJCount();
		int totalNum = posInfo.getTotalCount();
		return (jjNum * 1.0) / totalNum;
	}
	
	public double getNNRatio() {
		int nnNum = posInfo.getNNCount();
		int totalNum = posInfo.getTotalCount();
		return (nnNum * 1.0) / totalNum;
	}
	
	public double getRBRatio() {
		int RBNum = posInfo.getRBCount();
		int totalNum = posInfo.getTotalCount();
		return (RBNum * 1.0) / totalNum;
	}
	
	public double getVBRatio() {
		int vbNum = posInfo.getVBCount();
		int totalNum = posInfo.getTotalCount();
		return (vbNum * 1.0)/totalNum;
	}
	
	public int getNumofJJ() {
		return posInfo.getJJCount();
	}
	
	public int getNumofNN() {
		return posInfo.getNNCount();
	}
	
	public int getNumofRB() {
		return posInfo.getRBCount();
	}
	
	public int getNumofVB() {
		return posInfo.getVBCount();
	}
	
	public int getSpellError() {
		return errInfo.getNumofSpellError();
	}
	
	public int getGrammarError() {
		return errInfo.getNumOfGrammarError()-errInfo.getNumofSpellError();
	}
	
	public int getNumofContent() {
		return posInfo.getJJCount()+posInfo.getNNCount()+posInfo.getVBCount();
	}
	
	public int getNumofPERSON() {
		return posInfo.getPERSONCount();
	}
	
	public int getNumofLocation() {
		return posInfo.getLOCATIONCount();
	}
}
