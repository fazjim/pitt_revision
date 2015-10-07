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
	public String rPurpose;

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
