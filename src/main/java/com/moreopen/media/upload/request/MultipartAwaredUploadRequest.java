package com.moreopen.media.upload.request;

import org.springframework.web.multipart.MultipartFile;

public class MultipartAwaredUploadRequest {

	private String user;
	
	private MultipartFile multipartFile;

	public MultipartAwaredUploadRequest() {
		super();
	}

	public MultipartAwaredUploadRequest(String user, MultipartFile multipartFile) {
		this.user = user;
		this.multipartFile = multipartFile;
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}