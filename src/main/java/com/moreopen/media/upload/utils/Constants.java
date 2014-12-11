package com.moreopen.media.upload.utils;

public class Constants {
	
	public static final String UPLOAD_TOP_DIR  = System.getProperty("user.home") + "/uploads/media/static";
	
	public static final String UPLOAD_TEMP_DIR = System.getProperty("user.home") + "/uploads/media/tmp";

	public static final String DOWNLOAD_CONTEXT = "/media/static";
	
	public static final String DOWNLOAD_URL_PREFIX = "http://127.0.0.1:8081" + DOWNLOAD_CONTEXT;
	
	//if get static resource by nginx, use below DOWNLOAD_URL_PREFIX
//	public static final String DOWNLOAD_URL_PREFIX = "http://127.0.0.1" + DOWNLOAD_CONTEXT;

	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

}
