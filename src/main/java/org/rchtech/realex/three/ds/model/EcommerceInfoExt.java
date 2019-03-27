package org.rchtech.realex.three.ds.model;

import com.global.api.entities.EcommerceInfo;

public class EcommerceInfoExt extends EcommerceInfo {

	private String cavv;
	private String xid;
	private String eci;

	public String getCavv() {
		return cavv;
	}

	public void setCavv(String cavv) {
		this.cavv = cavv;
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getEci() {
		return eci;
	}

	public void setEci(String eci) {
		this.eci = eci;
	}

}
