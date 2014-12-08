package com.moreopen.media.upload.exception;

public class FileDownloadException extends Exception {
	
	private static final long serialVersionUID = 4485176892895193431L;

	public FileDownloadException() {
		super();
	}
	
	public FileDownloadException(String msg) {
		super(msg);
	}
	
	public FileDownloadException(String msg, Throwable t) {
		super(msg, t);
	}

}
