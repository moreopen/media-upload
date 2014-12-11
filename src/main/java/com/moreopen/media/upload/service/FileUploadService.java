package com.moreopen.media.upload.service;

import java.io.File;
import java.io.IOException;

import com.moreopen.media.upload.exception.FileUploadException;
import com.moreopen.media.upload.request.MultipartAwaredUploadRequest;
import com.moreopen.media.upload.response.BaseResponse;

public interface FileUploadService {
	
	public BaseResponse save(MultipartAwaredUploadRequest multipartAwaredUploadRequest) throws FileUploadException, IOException;

	public File getFile(String mid) throws Exception;

}
