package com.moreopen.media.upload.response;

public class MediaIdAwaredResponse extends BaseResponse {
	
	private String mid;
	
	public MediaIdAwaredResponse(String mid) {
		this.mid = mid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

}
