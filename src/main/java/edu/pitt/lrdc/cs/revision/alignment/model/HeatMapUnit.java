package edu.pitt.lrdc.cs.revision.alignment.model;

/**
 * Model for HeatMap visualization
 * 
 * Update: Also used in CRF sequential tagging
 * @author zhangfan
 *
 */
public class HeatMapUnit implements Comparable {
	public int getpD1() {
		return pD1;
	}

	public void setpD1(int pD1) {
		this.pD1 = pD1;
	}

	public int getpD2() {
		return pD2;
	}

	public void setpD2(int pD2) {
		this.pD2 = pD2;
	}

	public int getsD1() {
		return sD1;
	}

	public void setsD1(int sD1) {
		this.sD1 = sD1;
	}

	public int getsD2() {
		return sD2;
	}

	public void setsD2(int sD2) {
		this.sD2 = sD2;
	}

	public String getScD1() {
		return scD1;
	}

	public void setScD1(String scD1) {
		this.scD1 = scD1;
	}

	public String getScD2() {
		return scD2;
	}

	public void setScD2(String scD2) {
		this.scD2 = scD2;
	}

	public int getaR() {
		return aR;
	}

	public void setaR(int aR) {
		this.aR = aR;
	}

	public int getaC() {
		return aC;
	}

	public void setaC(int aC) {
		this.aC = aC;
	}

	public int getaVR() {
		return aVR;
	}

	public void setaVR(int aVR) {
		this.aVR = aVR;
	}

	public String getRType() {
		return rType;
	}

	public void setRType(String revType) {
		this.rType = revType;
	}

	public String getRPurpose() {
		return rPurpose;
	}

	public void setRPurpose(String revPurpose) {
		this.rPurpose = revPurpose;
	}

	/**
	 * Too lazy to change the domain from public to private!
	 */
	public int pD1;
	public int pD2;
	public int sD1;
	public int sD2;
	public String scD1;
	public String scD2;
	public int aR; // adjusted Row index
	public int aC; // adjusted Column index
	public int aVR; //adjusted Row index in vertical view
	public String rType;
	public String rPurpose="";
	public String rPurposeStr="";
	
	public String rPurposeOld="";
	public String rPurposeOldStr="";
	public String rPurposeNew="";
	public String rPurposeNewStr="";
	
	public int oldIndex;
	public int newIndex;
	
	public int realOldIndex = -1;
	public int realNewIndex = -1;
	public String realOldSC="";
	public String realNewSC="";
	

	/*
	public HeatMapUnit[] breakIntoTwo() {
		HeatMapUnit[] units = new HeatMapUnit[2];
		HeatMapUnit unit0 = new HeatMapUnit();
		HeatMapUnit unit1 = new HeatMapUnit();
		
		unit0.pD1= this.pD1;
		unit0.pD2 = -1;
		unit0.sD1 = this.sD1;
		unit0.sD2 = -1;
		unit0.scD1 = this.scD1;
		unit0.scD2 = "";
		unit0.rType = "Delete";
		unit0.rPurpose = this.rPurpose;
		unit0.oldIndex = this.oldIndex;
		unit0.newIndex = -1;
		unit0.realOldIndex = this.oldIndex;
		unit0.realNewIndex = this.newIndex;
		
		unit1.pD1= -1;
		unit1.pD2 = this.pD2;
		unit1.sD1 = -1;
		unit1.sD2 = this.sD2;
		unit1.scD1 = "";
		unit1.scD2 = this.scD2;
		unit1.rType = "Add";
		unit1.rPurpose = this.rPurpose;
		unit1.oldIndex = -1;
		unit1.newIndex = this.newIndex;
		unit1.realOldIndex = this.oldIndex;
		unit1.realNewIndex = this.newIndex;
		
		units[0] = unit0;
		units[1] = unit1;
		return units;
	}*/
	
	public String getRealOldSC() {
		return realOldSC;
	}

	public void setRealOldSC(String realOldSC) {
		this.realOldSC = realOldSC;
	}

	public String getRealNewSC() {
		return realNewSC;
	}

	public void setRealNewSC(String realNewSC) {
		this.realNewSC = realNewSC;
	}

	public String getrPurposeOld() {
		return rPurposeOld;
	}

	public void setrPurposeOld(String rPurposeOld) {
		this.rPurposeOld = rPurposeOld;
	}

	public String getrPurposeOldStr() {
		return rPurposeOldStr;
	}

	public void setrPurposeOldStr(String rPurposeOldStr) {
		this.rPurposeOldStr = rPurposeOldStr;
	}

	public String getrPurposeNew() {
		return rPurposeNew;
	}

	public void setrPurposeNew(String rPurposeNew) {
		this.rPurposeNew = rPurposeNew;
	}

	public String getrPurposeNewStr() {
		return rPurposeNewStr;
	}

	public void setrPurposeNewStr(String rPurposeNewStr) {
		this.rPurposeNewStr = rPurposeNewStr;
	}

	public String getrType() {
		return rType;
	}

	public void setrType(String rType) {
		this.rType = rType;
	}

	public String getrPurpose() {
		return rPurpose;
	}

	public void setrPurpose(String rPurpose) {
		this.rPurpose = rPurpose;
	}

	public String getrPurposeStr() {
		return rPurposeStr;
	}

	public void setrPurposeStr(String rPurposeStr) {
		this.rPurposeStr = rPurposeStr;
	}

	public int getOldIndex() {
		return oldIndex;
	}

	public void setOldIndex(int oldIndex) {
		this.oldIndex = oldIndex;
	}

	public int getNewIndex() {
		return newIndex;
	}

	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}

	public int getRealOldIndex() {
		return realOldIndex;
	}

	public void setRealOldIndex(int realOldIndex) {
		this.realOldIndex = realOldIndex;
	}

	public int getRealNewIndex() {
		return realNewIndex;
	}

	public void setRealNewIndex(int realNewIndex) {
		this.realNewIndex = realNewIndex;
	}

	public static int compare(HeatMapUnit unit1, HeatMapUnit unit2) {
		if(unit1.pD1 == -1) {
			if(unit2.pD2!=-1) {
				if(unit1.pD2 != unit2.pD2) {
					return unit1.pD2 - unit2.pD2;
				} else {
					return unit1.sD2 - unit2.sD2;
				}
			} else {
				return 0; //Can't determine the location
			}
		}
		if(unit1.pD2 == -1) {
			if(unit2.pD1!=-1) {
				if(unit1.pD1 != unit2.pD1) {
					return unit1.pD1 - unit2.pD1;
				} else {
					return unit1.sD1 - unit2.sD1;
				}
			} else {
				return 0;  //Can't determine the location
			}
		}
		
		if(unit2.pD1 == -1) {
			if(unit1.pD2!=-1) {
				if(unit1.pD2 != unit2.pD2) {
					return unit1.pD2 - unit2.pD2;
				} else {
					return unit1.sD2 - unit2.sD2;
				}
			} else {
				return 0;
			}
		}
		
		if(unit2.pD2 == -1) {
			if(unit1.pD1 != -1) {
				if(unit1.pD1 != unit2.pD1) {
					return unit1.pD1 - unit2.pD1;
				} else {
					return unit1.sD1 - unit2.sD1;
				}
			} else {
				return 0;
			}
		}
		
		if(unit1.pD1 != unit2.pD1) {
			return unit1.pD1 - unit2.pD1;
		} else {
			if(unit1.pD2 != unit2.pD2) {
				return unit1.pD2 - unit2.pD2;
			} else {
				if(unit1.sD1 != unit2.sD1) {
					return unit1.sD1 - unit2.sD1;
				} else {
					return unit1.sD2 - unit2.sD2;
				}
			}
		}
	}
	
	@Override
	public int compareTo(Object o) {
		HeatMapUnit compared = (HeatMapUnit) o;
		if(this.pD1 == -1) {
			if(compared.pD2!=-1) {
				if(this.pD2 != compared.pD2) {
					return this.pD2 - compared.pD2;
				} else {
					return this.sD2 - compared.sD2;
				}
			} else {
				return 0; //Can't determine the location
			}
		}
		if(this.pD2 == -1) {
			if(compared.pD1!=-1) {
				if(this.pD1 != compared.pD1) {
					return this.pD1 - compared.pD1;
				} else {
					return this.sD1 - compared.sD1;
				}
			} else {
				return 0;  //Can't determine the location
			}
		}
		
		if(compared.pD1 == -1) {
			if(this.pD2!=-1) {
				if(this.pD2 != compared.pD2) {
					return this.pD2 - compared.pD2;
				} else {
					return this.sD2 - compared.sD2;
				}
			} else {
				return 0;
			}
		}
		
		if(compared.pD2 == -1) {
			if(this.pD1 != -1) {
				if(this.pD1 != compared.pD1) {
					return this.pD1 - compared.pD1;
				} else {
					return this.sD1 - compared.sD1;
				}
			} else {
				return 0;
			}
		}
		
		if(this.pD1 != compared.pD1) {
			return this.pD1 - compared.pD1;
		} else {
			if(this.pD2 != compared.pD2) {
				return this.pD2 - compared.pD2;
			} else {
				if(this.sD1 != compared.sD1) {
					return this.sD1 - compared.sD1;
				} else {
					return this.sD2 - compared.sD2;
				}
			}
		}
	}

	public int compareTo2(Object o) {
		// TODO Auto-generated method stub
		HeatMapUnit compared = (HeatMapUnit) o;
		if (this.pD1 == -1 || compared.pD1 == -1) { //Put this in front so that the deleted are always ahead of the adds
			if (this.pD2 != compared.pD2) {
				return this.pD2 - compared.pD2;
			} else {
				return this.sD2 - compared.sD2;
			}
		}

		if (this.pD2 == -1 || compared.pD2 == -1) {
			if (this.pD1 != compared.pD1) {
				return this.pD1 - compared.pD1;
			} else {
				return this.sD1 - compared.sD1; // there must be one sD2 == -1
			}
		}

		// when there is not -1 existing, compare paragraph and then sentences
		if (this.pD1 != compared.pD1) {
			return this.pD1 - compared.pD1;
		} else {
			if (this.pD2 != compared.pD2) {
				return this.pD2 - compared.pD2;
			} else {
				if (this.sD1 != compared.sD1) {
					return this.sD1 - compared.sD1;
				} else {
					return this.sD2 - compared.sD2;
				}
			}
		}
	}
}
