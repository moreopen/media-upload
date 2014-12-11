package com.moreopen.media.upload.response;

public class BaseResponse extends Payload {
	
	public static final String OK = "200";
	
	public static final String UPLOAD_FAILED = "-1";
	
	public static final String DOWNLOAD_FAILED = "-2";
	
	public static final String INVALID_INPUT = "-3";
	
	public static final BaseResponse UNFORMATTED = new BaseResponse("401", "un-formatted input"); 
	
	public static final BaseResponse SERVER_ERROR = new BaseResponse("500", "server internal error");
	
	//返回码, 200 表示成功
	private String code = OK;
	
	//返回信息
	private String message;
	
	public BaseResponse() {
	}
	
	public BaseResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
