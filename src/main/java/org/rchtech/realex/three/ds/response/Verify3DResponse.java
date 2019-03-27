package org.rchtech.realex.three.ds.response;

public class Verify3DResponse {

	private String pares;

	private String md;

	public Verify3DResponse(final String pares, final String md) {
		this.pares = pares;
		this.md = md;
	}

	public Verify3DResponse() {
	}

	public String getPares() {
		return pares;
	}

	public String getMd() {
		return md;
	}

	public void setPares(String pares) {
		this.pares = pares;
	}

	public void setMd(String md) {
		this.md = md;
	}

}
