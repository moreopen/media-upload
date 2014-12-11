package com.moreopen.media.upload.exception;


public class FileUploadException extends Exception {

	private static final long serialVersionUID = -5818089070557696379L;
	
	public FileUploadException() {
		super();
	}
	
	public FileUploadException(String msg) {
		super(msg);
	}
	
	public FileUploadException(String msg, Throwable t) {
		super(msg, t);
	}

}
