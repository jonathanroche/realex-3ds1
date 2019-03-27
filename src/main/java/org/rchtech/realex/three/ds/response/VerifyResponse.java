package org.rchtech.realex.three.ds.response;

public class VerifyResponse {

	private final String pareq;
	private final String url;
	private final String orderId;

	public VerifyResponse(final String pareq, final String url, final String orderId) {
		this.pareq = pareq;
		this.url = url;
		this.orderId = orderId;
	}

	public String getPareq() {
		return pareq;
	}

	public String getUrl() {
		return url;
	}

	public String getOrderId() {
		return orderId;
	}

}
