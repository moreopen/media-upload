package com.moreopen.media.upload.response;

public class FileUploadResponse extends BaseResponse {
	
	private String url;

	public FileUploadResponse(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
