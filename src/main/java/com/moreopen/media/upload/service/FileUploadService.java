package com.moreopen.media.upload.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.moreopen.media.upload.exception.FileUploadException;
import com.moreopen.media.upload.response.BaseResponse;

public interface FileUploadService {
	
	public BaseResponse save(MultipartFile multipartFile) throws FileUploadException;

	public File getFile(String mid) throws Exception;

}
